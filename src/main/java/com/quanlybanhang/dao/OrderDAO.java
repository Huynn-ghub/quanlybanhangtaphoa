package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.model.Order;
import com.quanlybanhang.model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * DAO xử lý hóa đơn bán hàng.
 * createOrder() chạy trong một Transaction: lưu đơn → trừ tồn kho → cộng điểm khách hàng.
 * Nếu bất kỳ bước nào thất bại, toàn bộ sẽ rollback.
 */
public class OrderDAO {

    /**
     * Tạo hóa đơn mới, lưu chi tiết hóa đơn, cập nhật số lượng tồn kho và điểm tích lũy khách hàng.
     * Sử dụng Transaction để đảm bảo tính toàn vẹn dữ liệu.
     */
    public boolean createOrder(Order order, List<OrderDetail> details) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psProductStock = null;
        PreparedStatement psCustomerPoints = null;

        String insertOrderSql = "INSERT INTO orders (code, user_id, customer_id, total_amount, discount, final_amount, payment_method, notes) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertDetailSql = "INSERT INTO order_details (order_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        String updatePointsSql = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE id = ?";

        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, order.getCode());
            if (order.getUserId() > 0) {
                psOrder.setInt(2, order.getUserId());
            } else {
                psOrder.setNull(2, Types.INTEGER);
            }
            if (order.getCustomerId() > 0) {
                psOrder.setInt(3, order.getCustomerId());
            } else {
                psOrder.setNull(3, Types.INTEGER);
            }
            psOrder.setDouble(4, order.getTotalAmount());
            psOrder.setDouble(5, order.getDiscount());
            psOrder.setDouble(6, order.getFinalAmount());
            psOrder.setString(7, order.getPaymentMethod());
            psOrder.setString(8, order.getNotes());

            int affectedRows = psOrder.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            int orderId;
            try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            psDetail = conn.prepareStatement(insertDetailSql);
            psProductStock = conn.prepareStatement(updateStockSql);

            for (OrderDetail detail : details) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, detail.getProductId());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getPrice());
                psDetail.setDouble(5, detail.getQuantity() * detail.getPrice());
                psDetail.addBatch();

                psProductStock.setInt(1, detail.getQuantity());
                psProductStock.setInt(2, detail.getProductId());
                psProductStock.setInt(3, detail.getQuantity());
                int stockUpdated = psProductStock.executeUpdate();
                if (stockUpdated == 0) {
                    throw new SQLException("Sản phẩm ID " + detail.getProductId() + " không đủ hàng tồn kho để bán!");
                }
            }
            psDetail.executeBatch();

            // Mỗi 100.000đ = 1 điểm; chỉ cộng cho khách có ID > 1 (bỏ qua khách vãng lai)
            if (order.getCustomerId() > 0 && order.getCustomerId() != 1) {
                int pointsToAdd = (int) (order.getFinalAmount() / 100000);
                if (pointsToAdd > 0) {
                    psCustomerPoints = conn.prepareStatement(updatePointsSql);
                    psCustomerPoints.setInt(1, pointsToAdd);
                    psCustomerPoints.setInt(2, order.getCustomerId());
                    psCustomerPoints.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed, rolling back. Reason: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            DatabaseHelper.close(psOrder);
            DatabaseHelper.close(psDetail);
            DatabaseHelper.close(psProductStock);
            DatabaseHelper.close(psCustomerPoints);
            DatabaseHelper.close(conn);
        }
    }

    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS user_name, c.name AS customer_name FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return list;
    }

    public Order getById(int id) {
        String sql = "SELECT o.*, u.full_name AS user_name, c.name AS customer_name FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "WHERE o.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order by id: " + e.getMessage());
        }
        return null;
    }

    public List<OrderDetail> getDetails(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.*, p.name AS product_name, p.code AS product_code FROM order_details od " +
                     "LEFT JOIN products p ON od.product_id = p.id " +
                     "WHERE od.order_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail od = new OrderDetail();
                    od.setId(rs.getInt("id"));
                    od.setOrderId(rs.getInt("order_id"));
                    od.setProductId(rs.getInt("product_id"));
                    od.setProductName(rs.getString("product_name"));
                    od.setProductCode(rs.getString("product_code"));
                    od.setQuantity(rs.getInt("quantity"));
                    od.setPrice(rs.getDouble("price"));
                    od.setTotalPrice(rs.getDouble("total_price"));
                    list.add(od);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order details: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getByDateRange(Timestamp start, Timestamp end) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.full_name AS user_name, c.name AS customer_name FROM orders o " +
                     "LEFT JOIN users u ON o.user_id = u.id " +
                     "LEFT JOIN customers c ON o.customer_id = c.id " +
                     "WHERE o.order_date BETWEEN ? AND ? " +
                     "ORDER BY o.order_date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, start);
            ps.setTimestamp(2, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders by date range: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm kiếm hóa đơn linh hoạt theo từ khóa và khoảng thời gian.
     * keyword: khớp với mã HĐ, tên khách hàng, hoặc tên thu ngân (không phân biệt hoa/thường).
     * from / to: nếu null thì bỏ qua điều kiện tương ứng.
     */
    public List<Order> searchOrders(String keyword, Timestamp from, Timestamp to) {
        List<Order> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT o.*, u.full_name AS user_name, c.name AS customer_name FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "LEFT JOIN customers c ON o.customer_id = c.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (o.code ILIKE ? OR c.name ILIKE ? OR u.full_name ILIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        if (from != null) {
            sql.append("AND o.order_date >= ? ");
            params.add(from);
        }

        if (to != null) {
            sql.append("AND o.order_date <= ? ");
            params.add(to);
        }

        sql.append("ORDER BY o.order_date DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) ps.setString(i + 1, (String) p);
                else if (p instanceof Timestamp) ps.setTimestamp(i + 1, (Timestamp) p);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching orders: " + e.getMessage());
        }
        return list;
    }

    // user_id=0 xảy ra khi admin ngoài CSDL (cấu hình db.properties) tạo đơn
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCode(rs.getString("code"));
        o.setUserId(rs.getInt("user_id"));
        if (rs.wasNull()) {
            o.setUserId(0);
            o.setUserName("Admin (Ngoại vi)");
        } else {
            o.setUserName(rs.getString("user_name"));
        }
        o.setCustomerId(rs.getInt("customer_id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setDiscount(rs.getDouble("discount"));
        o.setFinalAmount(rs.getDouble("final_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setNotes(rs.getString("notes"));
        return o;
    }
}
