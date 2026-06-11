package com.quanlybanhang.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện Trang tổng quan (Dashboard).
 */
public class DashboardPanel extends JPanel {
    private JLabel lblRevenue;
    private JLabel lblOrderCount;
    private JLabel lblCustomerCount;
    private JLabel lblLowStock;
    private JPanel chartContainerPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        initComponents();
    }

    private void initComponents() {
        // 1. Grid of KPI Cards (North)
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setOpaque(false);

        lblRevenue = new JLabel("0 đ", SwingConstants.CENTER);
        lblOrderCount = new JLabel("0", SwingConstants.CENTER);
        lblCustomerCount = new JLabel("0", SwingConstants.CENTER);
        lblLowStock = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(createKPICard("DOANH THU HÔM NAY", lblRevenue, new Color(46, 204, 113))); // Xanh lá
        kpiPanel.add(createKPICard("HÓA ĐƠN HÔM NAY", lblOrderCount, new Color(52, 152, 219))); // Xanh dương
        kpiPanel.add(createKPICard("TỔNG SỐ KHÁCH HÀNG", lblCustomerCount, new Color(155, 89, 182))); // Tím
        kpiPanel.add(createKPICard("HÀNG SẮP HẾT KHO", lblLowStock, new Color(231, 76, 60))); // Đỏ

        add(kpiPanel, BorderLayout.NORTH);

        // 2. Chart Section (Center)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("BIỂU ĐỒ DOANH THU 7 NGÀY GẦN NHẤT"));

        chartContainerPanel = new JPanel(new BorderLayout());
        centerPanel.add(chartContainerPanel, BorderLayout.CENTER);

        // Load default mock chart initially
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(0, "Doanh thu", "N/A");
        updateChart(dataset);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createKPICard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Thiết lập viền bo tròn qua FlatLaf client property
        card.putClientProperty("JPanel.style", "arc: 12");

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(242, 243, 244));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void setRevenueText(String text) {
        lblRevenue.setText(text);
    }

    public void setOrderCountText(String text) {
        lblOrderCount.setText(text);
    }

    public void setCustomerCountText(String text) {
        lblCustomerCount.setText(text);
    }

    public void setLowStockCountText(String text) {
        lblLowStock.setText(text);
    }

    /**
     * Cập nhật biểu đồ thống kê với bộ dữ liệu mới.
     */
    public void updateChart(CategoryDataset dataset) {
        JFreeChart barChart = ChartFactory.createBarChart(
                "",
                "Ngày",
                "Doanh thu (VND)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Styling chart
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setBackgroundPaint(new Color(248, 249, 249));
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.LIGHT_GRAY);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 450));

        chartContainerPanel.removeAll();
        chartContainerPanel.add(chartPanel, BorderLayout.CENTER);
        chartContainerPanel.validate();
        chartContainerPanel.repaint();
    }
}
