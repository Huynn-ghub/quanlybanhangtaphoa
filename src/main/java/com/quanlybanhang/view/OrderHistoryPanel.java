package com.quanlybanhang.view;

import com.quanlybanhang.model.Order;
import com.quanlybanhang.model.OrderDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Giao diện Lịch sử / Tra cứu Hóa đơn.
 * Gồm bộ lọc (mã HĐ / tên KH / thu ngân, khoảng ngày), bảng danh sách HĐ,
 * bảng chi tiết sản phẩm của HĐ đang chọn và nút in lại PDF.
 */
public class OrderHistoryPanel extends JPanel {

    // --- Bộ lọc ---
    private JTextField txtSearch;
    private JSpinner spinFrom;
    private JSpinner spinTo;
    private JButton btnSearch;
    private JButton btnReset;

    // --- Bảng danh sách HĐ ---
    private JTable tblOrders;
    private DefaultTableModel ordersModel;

    // --- Bảng chi tiết HĐ ---
    private JTable tblDetails;
    private DefaultTableModel detailsModel;

    // --- Footer ---
    private JLabel lblSelectedInfo;
    private JButton btnPrintPDF;

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public OrderHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildMainSplitPane(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    // ===== NORTH: Bộ lọc =====
    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("BỘ LỌC TRA CỨU HÓA ĐƠN"));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Mã HĐ / Tên khách / Thu ngân...");

        // SpinnerDateModel: mặc định from = đầu tháng, to = hôm nay
        Date today = new Date();
        Date firstOfMonth = getFirstOfMonth();

        spinFrom = new JSpinner(new SpinnerDateModel(firstOfMonth, null, null, java.util.Calendar.DAY_OF_MONTH));
        spinTo   = new JSpinner(new SpinnerDateModel(today, null, null, java.util.Calendar.DAY_OF_MONTH));

        JSpinner.DateEditor edFrom = new JSpinner.DateEditor(spinFrom, "dd/MM/yyyy");
        JSpinner.DateEditor edTo   = new JSpinner.DateEditor(spinTo,   "dd/MM/yyyy");
        spinFrom.setEditor(edFrom);
        spinTo.setEditor(edTo);
        spinFrom.setPreferredSize(new Dimension(110, 28));
        spinTo.setPreferredSize(new Dimension(110, 28));

        btnSearch = new JButton("Tìm kiếm");
        btnReset  = new JButton("Xóa bộ lọc");
        btnSearch.putClientProperty("JButton.buttonType", "roundRect");
        btnReset.putClientProperty("JButton.buttonType",  "roundRect");

        panel.add(new JLabel("Từ khóa:"));
        panel.add(txtSearch);
        panel.add(new JLabel("  Từ ngày:"));
        panel.add(spinFrom);
        panel.add(new JLabel("→"));
        panel.add(spinTo);
        panel.add(btnSearch);
        panel.add(btnReset);

        return panel;
    }

    // ===== CENTER: JSplitPane dọc =====
    private JSplitPane buildMainSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.6);
        split.setDividerLocation(350);

        // --- Bảng danh sách HĐ (trên) ---
        String[] orderCols = {
            "Mã HĐ", "Ngày lập", "Khách hàng", "Thu ngân",
            "Tổng tiền", "Giảm giá", "Thực thu", "Phương thức TT"
        };
        ordersModel = new DefaultTableModel(orderCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOrders = new JTable(ordersModel);
        tblOrders.setRowHeight(24);
        tblOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblOrders.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Ẩn cột ID ẩn nếu cần — dữ liệu Order ID lưu bằng putClientProperty bên Controller

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH HÓA ĐƠN"));
        topPanel.add(new JScrollPane(tblOrders), BorderLayout.CENTER);

        // --- Bảng chi tiết SP (dưới) ---
        String[] detailCols = {"STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        detailsModel = new DefaultTableModel(detailCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDetails = new JTable(detailsModel);
        tblDetails.setRowHeight(22);
        tblDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblDetails.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("CHI TIẾT SẢN PHẨM TRONG HÓA ĐƠN"));
        bottomPanel.add(new JScrollPane(tblDetails), BorderLayout.CENTER);

        split.setTopComponent(topPanel);
        split.setBottomComponent(bottomPanel);
        return split;
    }

    // ===== SOUTH: Footer =====
    private JPanel buildFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        lblSelectedInfo = new JLabel("← Chọn hóa đơn để xem chi tiết");
        lblSelectedInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSelectedInfo.setForeground(Color.GRAY);

        btnPrintPDF = new JButton("In lại hóa đơn PDF");
        btnPrintPDF.setBackground(new Color(52, 152, 219));
        btnPrintPDF.setForeground(Color.WHITE);
        btnPrintPDF.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPrintPDF.setEnabled(false); // Chỉ bật khi có HĐ được chọn

        panel.add(lblSelectedInfo, BorderLayout.WEST);
        panel.add(btnPrintPDF, BorderLayout.EAST);
        return panel;
    }

    // ===== Public API cho Controller =====

    public void setOrdersData(List<Order> orders) {
        ordersModel.setRowCount(0);
        for (Order o : orders) {
            ordersModel.addRow(new Object[]{
                o.getCode(),
                o.getOrderDate() != null ? DATE_FMT.format(o.getOrderDate()) : "",
                o.getCustomerName() != null ? o.getCustomerName() : "Khách vãng lai",
                o.getUserName() != null ? o.getUserName() : "",
                String.format("%,.0f đ", o.getTotalAmount()),
                String.format("%,.0f đ", o.getDiscount()),
                String.format("%,.0f đ", o.getFinalAmount()),
                o.getPaymentMethod()
            });
        }
        clearDetails();
        lblSelectedInfo.setText("Tổng: " + orders.size() + " hóa đơn — chọn để xem chi tiết");
        btnPrintPDF.setEnabled(false);
    }

    public void setDetailsData(List<OrderDetail> details, Order order) {
        detailsModel.setRowCount(0);
        int stt = 1;
        for (OrderDetail d : details) {
            detailsModel.addRow(new Object[]{
                stt++,
                d.getProductCode(),
                d.getProductName(),
                String.format("%,.0f đ", d.getPrice()),
                d.getQuantity(),
                String.format("%,.0f đ", d.getTotalPrice())
            });
        }
        if (order != null) {
            lblSelectedInfo.setText(
                "HĐ: " + order.getCode() +
                "  |  Thực thu: " + String.format("%,.0f đ", order.getFinalAmount()) +
                "  |  " + (order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai")
            );
            lblSelectedInfo.setForeground(new Color(41, 128, 185));
        }
        btnPrintPDF.setEnabled(true);
    }

    public void clearDetails() {
        detailsModel.setRowCount(0);
        lblSelectedInfo.setText("← Chọn hóa đơn để xem chi tiết");
        lblSelectedInfo.setForeground(Color.GRAY);
        btnPrintPDF.setEnabled(false);
    }

    // Getters
    public String getSearchKeyword()    { return txtSearch.getText().trim(); }
    public Timestamp getFromTimestamp() { return new Timestamp(((Date) spinFrom.getValue()).getTime()); }
    public Timestamp getToTimestamp()   {
        // to: cuối ngày được chọn (23:59:59)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime((Date) spinTo.getValue());
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        return new Timestamp(cal.getTimeInMillis());
    }

    public int getSelectedOrderRow()    { return tblOrders.getSelectedRow(); }

    public void resetFilters() {
        txtSearch.setText("");
        spinFrom.setValue(getFirstOfMonth());
        spinTo.setValue(new Date());
    }

    // Listener bindings
    public void addSearchListener(ActionListener l)    { btnSearch.addActionListener(l); }
    public void addResetListener(ActionListener l)     { btnReset.addActionListener(l); }
    public void addPrintPDFListener(ActionListener l)  { btnPrintPDF.addActionListener(l); }
    public void addOrderTableListener(javax.swing.event.ListSelectionListener l) {
        tblOrders.getSelectionModel().addListSelectionListener(l);
    }

    // Tiện ích
    private Date getFirstOfMonth() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        return cal.getTime();
    }
}
