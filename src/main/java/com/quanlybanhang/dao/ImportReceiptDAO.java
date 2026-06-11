package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.model.ImportReceipt;
import com.quanlybanhang.model.ImportReceiptDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý dữ liệu Phiếu nhập kho.
 * Lưu thông tin phiếu nhập, chi tiết phiếu và cập nhật số lượng tồn kho sản phẩm.
 */
public class ImportReceiptDAO {

    /**
     * Tạo phiếu nhập kho mới, lưu chi tiết phiếu nhập, cộng thêm số lượng tồn kho sản phẩm.
     * Sử dụng Transaction để bảo vệ tính toàn vẹn dữ liệu.
     */
    public boolean createReceipt(ImportReceipt receipt, List<ImportReceiptDetail> details) {
        Connection conn = null;
        PreparedStatement psReceipt = null;
        PreparedStatement psDetail = null;
        PreparedStatement psProductStock = null;

        String insertReceiptSql = "INSERT INTO import_receipts (code, user_id, supplier_id, total_amount) VALUES (?, ?, ?, ?)";
        String insertDetailSql = "INSERT INTO import_receipt_details (receipt_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";

        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false);

            psReceipt = conn.prepareStatement(insertReceiptSql, Statement.RETURN_GENERATED_KEYS);
            psReceipt.setString(1, receipt.getCode());
            if (receipt.getUserId() > 0) {
                psReceipt.setInt(2, receipt.getUserId());
            } else {
                psReceipt.setNull(2, Types.INTEGER);
            }
            psReceipt.setInt(3, receipt.getSupplierId());
            psReceipt.setDouble(4, receipt.getTotalAmount());

            int affectedRows = psReceipt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating import receipt failed, no rows affected.");
            }

            int receiptId;
            try (ResultSet generatedKeys = psReceipt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    receiptId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating import receipt failed, no ID obtained.");
                }
            }

            psDetail = conn.prepareStatement(insertDetailSql);
            psProductStock = conn.prepareStatement(updateStockSql);

            for (ImportReceiptDetail detail : details) {
                psDetail.setInt(1, receiptId);
                psDetail.setInt(2, detail.getProductId());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getPrice());
                psDetail.setDouble(5, detail.getQuantity() * detail.getPrice());
                psDetail.addBatch();

                psProductStock.setInt(1, detail.getQuantity());
                psProductStock.setInt(2, detail.getProductId());
                psProductStock.executeUpdate();
            }
            psDetail.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Import receipt transaction failed, rolling back. Reason: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            DatabaseHelper.close(psReceipt);
            DatabaseHelper.close(psDetail);
            DatabaseHelper.close(psProductStock);
            DatabaseHelper.close(conn);
        }
    }

    public List<ImportReceipt> getAll() {
        List<ImportReceipt> list = new ArrayList<>();
        String sql = "SELECT ir.*, u.full_name AS user_name, s.name AS supplier_name FROM import_receipts ir " +
                     "LEFT JOIN users u ON ir.user_id = u.id " +
                     "LEFT JOIN suppliers s ON ir.supplier_id = s.id " +
                     "ORDER BY ir.import_date DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapReceipt(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching import receipts: " + e.getMessage());
        }
        return list;
    }

    public List<ImportReceiptDetail> getDetails(int receiptId) {
        List<ImportReceiptDetail> list = new ArrayList<>();
        String sql = "SELECT ird.*, p.name AS product_name, p.code AS product_code FROM import_receipt_details ird " +
                     "LEFT JOIN products p ON ird.product_id = p.id " +
                     "WHERE ird.receipt_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, receiptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImportReceiptDetail ird = new ImportReceiptDetail();
                    ird.setId(rs.getInt("id"));
                    ird.setReceiptId(rs.getInt("receipt_id"));
                    ird.setProductId(rs.getInt("product_id"));
                    ird.setProductName(rs.getString("product_name"));
                    ird.setProductCode(rs.getString("product_code"));
                    ird.setQuantity(rs.getInt("quantity"));
                    ird.setPrice(rs.getDouble("price"));
                    ird.setTotalPrice(rs.getDouble("total_price"));
                    list.add(ird);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching import receipt details: " + e.getMessage());
        }
        return list;
    }

    private ImportReceipt mapReceipt(ResultSet rs) throws SQLException {
        ImportReceipt ir = new ImportReceipt();
        ir.setId(rs.getInt("id"));
        ir.setCode(rs.getString("code"));
        ir.setUserId(rs.getInt("user_id"));
        if (rs.wasNull()) {
            ir.setUserId(0);
            ir.setUserName("Admin (Ngoại vi)");
        } else {
            ir.setUserName(rs.getString("user_name"));
        }
        ir.setSupplierId(rs.getInt("supplier_id"));
        ir.setSupplierName(rs.getString("supplier_name"));
        ir.setImportDate(rs.getTimestamp("import_date"));
        ir.setTotalAmount(rs.getDouble("total_amount"));
        return ir;
    }
}
