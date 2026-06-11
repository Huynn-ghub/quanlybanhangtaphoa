package com.quanlybanhang;

import com.quanlybanhang.config.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Công cụ test kết nối Database và kiểm tra dữ liệu bảng users.
 */
public class TestDB {
    public static void main(String[] args) {
        System.out.println("Connecting to Database...");
        try (Connection conn = DatabaseHelper.getConnection()) {
            System.out.println("Connection successful!");
            
            // Kiểm tra bảng users
            String sql = "SELECT COUNT(*) FROM users";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Số lượng bản ghi trong bảng users: " + rs.getInt(1));
                }
            }
            
            // In danh sách users
            sql = "SELECT id, username, full_name, role, status FROM users";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("Danh sách nhân viên:");
                while (rs.next()) {
                    System.out.printf("- ID: %d | Username: %s | Tên: %s | Quyền: %s | Status: %b\n",
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getBoolean("status"));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
