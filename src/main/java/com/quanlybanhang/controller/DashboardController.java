package com.quanlybanhang.controller;

import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.dao.CustomerDAO;
import com.quanlybanhang.dao.OrderDAO;
import com.quanlybanhang.dao.ProductDAO;
import com.quanlybanhang.view.DashboardPanel;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Controller điều khiển trang tổng quan (Dashboard).
 * Tính toán doanh thu, số hóa đơn, số lượng khách hàng và cảnh báo hàng sắp hết kho.
 */
public class DashboardController {
    private DashboardPanel view;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0 đ");

    public DashboardController(DashboardPanel view, OrderDAO orderDAO, CustomerDAO customerDAO, ProductDAO productDAO) {
        this.view = view;
        this.orderDAO = orderDAO;
        this.customerDAO = customerDAO;
        this.productDAO = productDAO;
    }

    public void loadDashboardStats() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            double todayRevenue = 0;
            int todayOrders = 0;
            int totalCustomers = 0;
            int lowStockCount = 0;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            @Override
            protected Void doInBackground() throws Exception {
                fetchKPIMetrics();
                fetchChartData();
                return null;
            }

            private void fetchKPIMetrics() {
                String sqlOrderToday = "SELECT COUNT(id) AS ord_cnt, COALESCE(SUM(final_amount), 0) AS rev_sum " +
                                       "FROM orders WHERE order_date >= CURRENT_DATE";
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sqlOrderToday);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        todayOrders = rs.getInt("ord_cnt");
                        todayRevenue = rs.getDouble("rev_sum");
                    }
                } catch (SQLException e) {
                    System.err.println("Error fetching today stats: " + e.getMessage());
                }

                String sqlCustomer = "SELECT COUNT(id) AS cust_cnt FROM customers";
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sqlCustomer);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalCustomers = rs.getInt("cust_cnt");
                    }
                } catch (SQLException e) {
                    System.err.println("Error counting customers: " + e.getMessage());
                }

                String sqlStock = "SELECT COUNT(id) AS stock_low FROM products WHERE stock_quantity <= 5 AND status = TRUE";
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sqlStock);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lowStockCount = rs.getInt("stock_low");
                    }
                } catch (SQLException e) {
                    System.err.println("Error counting low stock items: " + e.getMessage());
                }
            }

            private void fetchChartData() {
                String sqlChart = "SELECT d.dt::date AS sales_date, COALESCE(SUM(o.final_amount), 0) AS total_sales " +
                                  "FROM generate_series(CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE, '1 day'::interval) d(dt) " +
                                  "LEFT JOIN orders o ON o.order_date::date = d.dt::date " +
                                  "GROUP BY d.dt " +
                                  "ORDER BY d.dt ASC";
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sqlChart);
                     ResultSet rs = ps.executeQuery()) {
                    
                    while (rs.next()) {
                        LocalDate date = rs.getDate("sales_date").toLocalDate();
                        double sales = rs.getDouble("total_sales");
                        String dateStr = date.getDayOfMonth() + "/" + date.getMonthValue();
                        dataset.addValue(sales, "Doanh thu", dateStr);
                    }
                } catch (SQLException e) {
                    System.err.println("Error generating sales chart dataset: " + e.getMessage());
                    for (int i = 6; i >= 0; i--) {
                        LocalDate d = LocalDate.now().minusDays(i);
                        dataset.addValue(0, "Doanh thu", d.getDayOfMonth() + "/" + d.getMonthValue());
                    }
                }
            }

            @Override
            protected void done() {
                view.setRevenueText(currencyFormatter.format(todayRevenue));
                view.setOrderCountText(String.valueOf(todayOrders));
                view.setCustomerCountText(String.valueOf(totalCustomers));
                view.setLowStockCountText(String.valueOf(lowStockCount));
                view.updateChart(dataset);
            }
        };
        worker.execute();
    }
}
