package com.quanlybanhang.dao;

import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý dữ liệu Sản phẩm.
 * Hỗ trợ các thao tác CRUD và cập nhật số lượng tồn kho sản phẩm.
 */
public class ProductDAO {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "ORDER BY p.id ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return list;
    }

    public List<Product> getActiveProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.status = TRUE " +
                     "ORDER BY p.name ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active products: " + e.getMessage());
        }
        return list;
    }

    public Product getById(int id) {
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by id: " + e.getMessage());
        }
        return null;
    }

    public Product getByCode(String code) {
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.code = ? AND p.status = TRUE";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by code: " + e.getMessage());
        }
        return null;
    }

    public List<Product> search(String query) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.status = TRUE AND (LOWER(p.name) LIKE ? OR LOWER(p.code) LIKE ?) " +
                     "ORDER BY p.name ASC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query.toLowerCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return list;
    }

    public boolean isCodeExists(String code) {
        String sql = "SELECT 1 FROM products WHERE code = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking product code: " + e.getMessage());
            return false;
        }
    }

    public boolean insert(Product product) {
        String sql = "INSERT INTO products (code, name, category_id, purchase_price, sale_price, stock_quantity, unit, image_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            if (product.getCategoryId() > 0) {
                ps.setInt(3, product.getCategoryId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setDouble(4, product.getPurchasePrice());
            ps.setDouble(5, product.getSalePrice());
            ps.setInt(6, product.getStockQuantity());
            ps.setString(7, product.getUnit());
            ps.setString(8, product.getImagePath());
            ps.setBoolean(9, product.isStatus());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET code = ?, name = ?, category_id = ?, purchase_price = ?, sale_price = ?, " +
                     "stock_quantity = ?, unit = ?, image_path = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getCode());
            ps.setString(2, product.getName());
            if (product.getCategoryId() > 0) {
                ps.setInt(3, product.getCategoryId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setDouble(4, product.getPurchasePrice());
            ps.setDouble(5, product.getSalePrice());
            ps.setInt(6, product.getStockQuantity());
            ps.setString(7, product.getUnit());
            ps.setString(8, product.getImagePath());
            ps.setBoolean(9, product.isStatus());
            ps.setInt(10, product.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật số lượng tồn kho (ví dụ: trừ kho khi bán, cộng kho khi nhập).
     */
    public boolean updateStock(int productId, int quantityChange) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quantityChange);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "UPDATE products SET status = FALSE WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting/deactivating product: " + e.getMessage());
            return false;
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("code"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setPurchasePrice(rs.getDouble("purchase_price"));
        p.setSalePrice(rs.getDouble("sale_price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setUnit(rs.getString("unit"));
        p.setImagePath(rs.getString("image_path"));
        p.setStatus(rs.getBoolean("status"));
        return p;
    }
}
