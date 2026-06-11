package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý dữ liệu Nhân viên (Người dùng hệ thống).
 * Hỗ trợ các chức năng đăng nhập, đổi mật khẩu và quản lý thông tin.
 */
public class UserDAO {

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     * @param username Tên đăng nhập
     * @param password Mật khẩu thô (plaintext)
     * @return Đối tượng User nếu đăng nhập thành công, ngược lại null
     */
    public User login(String username, String password) {
        if (username.equals(DatabaseHelper.getAdminUsername()) && password.equals(DatabaseHelper.getAdminPassword())) {
            return new User(0, DatabaseHelper.getAdminUsername(), "", "Nguyễn Quản Trị", "admin@qmbh.com", "0987654321", "ADMIN", true);
        }

        String sql = "SELECT * FROM users WHERE username = ? AND status = TRUE";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return mapUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login check: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả nhân viên.
     */
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy thông tin nhân viên theo ID.
     */
    public User getById(int id) {
        if (id == 0) {
            return new User(0, DatabaseHelper.getAdminUsername(), "", "Nguyễn Quản Trị", "admin@qmbh.com", "0987654321", "ADMIN", true);
        }

        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Kiểm tra username đã tồn tại chưa.
     */
    public boolean isUsernameExists(String username) {
        if (username.equals(DatabaseHelper.getAdminUsername())) {
            return true;
        }

        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thêm nhân viên mới. Mật khẩu thô truyền vào sẽ được mã hóa BCrypt tự động.
     */
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isStatus());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin nhân viên (không cập nhật mật khẩu ở đây).
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, role = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isStatus());
            ps.setInt(6, user.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đổi mật khẩu nhân viên.
     */
    public boolean updatePassword(int userId, String rawPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa nhân viên (hoặc chuyển status thành FALSE để giữ lịch sử hóa đơn).
     */
    public boolean delete(int id) {
        String sql = "UPDATE users SET status = FALSE WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting/deactivating user: " + e.getMessage());
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getBoolean("status"));
        return user;
    }
}
