package com.quanlybanhang.controller;

import com.quanlybanhang.dao.CustomerDAO;
import com.quanlybanhang.dao.OrderDAO;
import com.quanlybanhang.dao.ProductDAO;
import com.quanlybanhang.view.DashboardPanel;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

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
                double[] todayStats = orderDAO.getTodayStats();
                todayOrders   = (int) todayStats[0];
                todayRevenue  = todayStats[1];

                totalCustomers = customerDAO.getCount();
                lowStockCount  = productDAO.getLowStockCount();
            }

            private void fetchChartData() {
                List<Object[]> rows = orderDAO.getWeeklySalesChart();
                if (!rows.isEmpty()) {
                    for (Object[] row : rows) {
                        LocalDate date  = (LocalDate) row[0];
                        double    sales = (double)    row[1];
                        String dateStr  = date.getDayOfMonth() + "/" + date.getMonthValue();
                        dataset.addValue(sales, "Doanh thu", dateStr);
                    }
                } else {
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

