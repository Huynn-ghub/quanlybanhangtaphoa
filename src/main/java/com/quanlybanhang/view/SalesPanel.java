package com.quanlybanhang.view;

import com.quanlybanhang.model.Customer;
import com.quanlybanhang.model.Product;
import com.quanlybanhang.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Bán hàng (Point of Sale - POS).
 * Giao diện chia làm hai phần: Bên trái chứa tìm kiếm sản phẩm và giỏ hàng hiện tại,
 * bên phải chứa thông tin khách hàng, số tiền thanh toán, giảm giá và phương thức giao dịch.
 */
public class SalesPanel extends JPanel {
    private User currentUser;

    // Left side: Product Search & Table
    private JTextField txtSearchProduct;
    private JTable tblProductList;
    private DefaultTableModel prodTableModel;

    // Cart table
    private JTable tblCart;
    private DefaultTableModel cartTableModel;
    private JButton btnRemoveItem;
    private JButton btnClearCart;
    private JSpinner spinQuantity;

    // Right side: Customer & Invoice calculations
    private JTextField txtCustPhone;
    private JButton btnSearchCust;
    private JLabel lblCustName;
    private JLabel lblCustPoints;
    private JButton btnAddQuickCust;

    // Calculations
    private JLabel lblTotalAmount;
    private JTextField txtDiscount;
    private JLabel lblFinalAmount;
    private JComboBox<String> cbPaymentMethod;
    private JTextField txtNotes;
    
    // POS Actions
    private JButton btnPay;
    private JButton btnCancelInvoice;
    private JLabel lblInvoiceCode;

    public SalesPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        // --- CHIA PANEL TRÁI VÀ PHẢI ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(750);
        splitPane.setResizeWeight(0.7);

        // ================= TRÁI: SẢN PHẨM & GIỎ HÀNG =================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        // 1. Tìm kiếm sản phẩm
        JPanel searchProdPanel = new JPanel(new BorderLayout(5, 5));
        searchProdPanel.setBorder(BorderFactory.createTitledBorder("TÌM KIẾM SẢN PHẨM SẴN CÓ"));
        txtSearchProduct = new JTextField();
        txtSearchProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearchProduct.putClientProperty("JTextField.placeholderText", "Gõ mã SP hoặc tên SP rồi ấn Enter để thêm nhanh...");
        searchProdPanel.add(txtSearchProduct, BorderLayout.CENTER);
        
        // Bảng danh sách sản phẩm tìm được để double click thêm vào giỏ
        String[] prodCols = {"Mã SP", "Tên sản phẩm", "Đơn giá bán", "Tồn kho", "Đơn vị"};
        prodTableModel = new DefaultTableModel(prodCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductList = new JTable(prodTableModel);
        tblProductList.setRowHeight(22);
        tblProductList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProds = new JScrollPane(tblProductList);
        scrollProds.setPreferredSize(new Dimension(leftPanel.getWidth(), 160));
        searchProdPanel.add(scrollProds, BorderLayout.SOUTH);

        leftPanel.add(searchProdPanel, BorderLayout.NORTH);

        // 2. Bảng giỏ hàng (Cart)
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder("GIỎ HÀNG CHI TIẾT"));

        String[] cartCols = {"STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền", "Sản phẩm ID"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCart = new JTable(cartTableModel);
        tblCart.setRowHeight(25);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Ẩn cột ID sản phẩm (cột thứ 6) để tăng tính thẩm mỹ
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCart = new JScrollPane(tblCart);
        cartPanel.add(scrollCart, BorderLayout.CENTER);

        // Nút điều khiển giỏ hàng ở dưới
        JPanel cartControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        cartControlPanel.add(new JLabel("Số lượng điều chỉnh:"));
        
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinQuantity.setPreferredSize(new Dimension(60, 28));
        cartControlPanel.add(spinQuantity);

        btnRemoveItem = new JButton("Xóa dòng chọn");
        btnClearCart = new JButton("Xóa tất cả");
        btnRemoveItem.setBackground(new Color(230, 126, 34)); btnRemoveItem.setForeground(Color.WHITE);
        btnClearCart.setBackground(new Color(192, 57, 43)); btnClearCart.setForeground(Color.WHITE);

        cartControlPanel.add(btnRemoveItem);
        cartControlPanel.add(btnClearCart);
        cartPanel.add(cartControlPanel, BorderLayout.SOUTH);

        leftPanel.add(cartPanel, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // ================= PHẢI: KHÁCH HÀNG & THANH TOÁN =================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN HÓA ĐƠN & THANH TOÁN"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        // Mã hóa đơn (Tự động sinh)
        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(new JLabel("Mã hóa đơn:"), gbc);
        lblInvoiceCode = new JLabel("HD" + System.currentTimeMillis() / 10000);
        lblInvoiceCode.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblInvoiceCode.setForeground(new Color(41, 128, 185));
        gbc.gridx = 1;
        rightPanel.add(lblInvoiceCode, gbc);

        // Thu ngân
        gbc.gridx = 0; gbc.gridy = 1;
        rightPanel.add(new JLabel("Thu ngân:"), gbc);
        JLabel lblCashier = new JLabel(currentUser.getFullName());
        lblCashier.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1;
        rightPanel.add(lblCashier, gbc);

        // Khách hàng (SĐT)
        gbc.gridx = 0; gbc.gridy = 2;
        rightPanel.add(new JLabel("SĐT Khách hàng:"), gbc);
        
        JPanel custSearchRow = new JPanel(new BorderLayout(5, 0));
        txtCustPhone = new JTextField();
        btnSearchCust = new JButton("Tìm");
        custSearchRow.add(txtCustPhone, BorderLayout.CENTER);
        custSearchRow.add(btnSearchCust, BorderLayout.EAST);
        gbc.gridx = 1;
        rightPanel.add(custSearchRow, gbc);

        // Tên khách hàng tìm thấy
        gbc.gridx = 0; gbc.gridy = 3;
        rightPanel.add(new JLabel("Tên khách hàng:"), gbc);
        lblCustName = new JLabel("Khách vãng lai");
        lblCustName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1;
        rightPanel.add(lblCustName, gbc);

        // Điểm tích lũy khách hàng
        gbc.gridx = 0; gbc.gridy = 4;
        rightPanel.add(new JLabel("Điểm tích lũy:"), gbc);
        lblCustPoints = new JLabel("0 điểm");
        gbc.gridx = 1;
        rightPanel.add(lblCustPoints, gbc);

        // Nút thêm nhanh khách hàng
        btnAddQuickCust = new JButton("Thêm nhanh KH mới");
        btnAddQuickCust.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 1; gbc.gridy = 5;
        rightPanel.add(btnAddQuickCust, gbc);

        // Tổng tiền hàng
        gbc.gridx = 0; gbc.gridy = 6;
        rightPanel.add(new JLabel("Tổng tiền hàng:"), gbc);
        lblTotalAmount = new JLabel("0 đ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 1;
        rightPanel.add(lblTotalAmount, gbc);

        // Giảm giá (VND)
        gbc.gridx = 0; gbc.gridy = 7;
        rightPanel.add(new JLabel("Giảm giá (đ):"), gbc);
        txtDiscount = new JTextField("0");
        txtDiscount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        rightPanel.add(txtDiscount, gbc);

        // Thành tiền phải thanh toán
        gbc.gridx = 0; gbc.gridy = 8;
        rightPanel.add(new JLabel("Khách phải trả:"), gbc);
        lblFinalAmount = new JLabel("0 đ");
        lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFinalAmount.setForeground(new Color(192, 57, 43));
        gbc.gridx = 1;
        rightPanel.add(lblFinalAmount, gbc);

        // Phương thức thanh toán
        gbc.gridx = 0; gbc.gridy = 9;
        rightPanel.add(new JLabel("Thanh toán:"), gbc);
        cbPaymentMethod = new JComboBox<>(new String[]{"CASH (Tiền mặt)", "TRANSFER (Chuyển khoản / QR)"});
        gbc.gridx = 1;
        rightPanel.add(cbPaymentMethod, gbc);

        // Ghi chú hóa đơn
        gbc.gridx = 0; gbc.gridy = 10;
        rightPanel.add(new JLabel("Ghi chú:"), gbc);
        txtNotes = new JTextField();
        gbc.gridx = 1;
        rightPanel.add(txtNotes, gbc);

        // Nút Thanh toán và In
        btnPay = new JButton("Thanh Toán & In Hóa Đơn");
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setPreferredSize(new Dimension(rightPanel.getWidth(), 45));
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.putClientProperty("JButton.buttonType", "roundRect");
        
        btnCancelInvoice = new JButton("Hủy hóa đơn hiện tại");
        btnCancelInvoice.setBackground(new Color(149, 165, 166));
        btnCancelInvoice.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        rightPanel.add(btnPay, gbc);

        gbc.gridy = 12;
        gbc.insets = new Insets(5, 10, 5, 10);
        rightPanel.add(btnCancelInvoice, gbc);
        
        // Đẩy lên trên
        gbc.gridy = 13; gbc.weighty = 1.0;
        rightPanel.add(new JLabel(" "), gbc);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    public void setProductListData(List<Product> products) {
        prodTableModel.setRowCount(0);
        for (Product p : products) {
            prodTableModel.addRow(new Object[]{
                    p.getCode(),
                    p.getName(),
                    p.getSalePrice(),
                    p.getStockQuantity(),
                    p.getUnit()
            });
        }
    }

    public void addProductToCartTable(Product p, int qty) {
        int existingRow = -1;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (cartTableModel.getValueAt(i, 1).equals(p.getCode())) {
                existingRow = i;
                break;
            }
        }

        if (existingRow != -1) {
            int currentQty = (int) cartTableModel.getValueAt(existingRow, 4);
            int newQty = currentQty + qty;
            cartTableModel.setValueAt(newQty, existingRow, 4);
            cartTableModel.setValueAt(newQty * p.getSalePrice(), existingRow, 5);
        } else {
            int stt = cartTableModel.getRowCount() + 1;
            cartTableModel.addRow(new Object[]{
                    stt,
                    p.getCode(),
                    p.getName(),
                    p.getSalePrice(),
                    qty,
                    p.getSalePrice() * qty,
                    p.getId()
            });
        }
    }

    public void removeSelectedCartItem() {
        int row = tblCart.getSelectedRow();
        if (row != -1) {
            cartTableModel.removeRow(row);
            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                cartTableModel.setValueAt(i + 1, i, 0);
            }
        }
    }

    public void clearCart() {
        cartTableModel.setRowCount(0);
    }

    public Product getSelectedProductListProduct() {
        int row = tblProductList.getSelectedRow();
        if (row == -1) return null;
        Product p = new Product();
        p.setCode((String) tblProductList.getValueAt(row, 0));
        p.setName((String) tblProductList.getValueAt(row, 1));
        p.setSalePrice((double) tblProductList.getValueAt(row, 2));
        p.setStockQuantity((int) tblProductList.getValueAt(row, 3));
        p.setUnit((String) tblProductList.getValueAt(row, 4));
        return p;
    }

    public int getSelectedCartRow() { return tblCart.getSelectedRow(); }
    public String getSelectedCartItemCode() {
        int row = tblCart.getSelectedRow();
        return row != -1 ? (String) tblCart.getValueAt(row, 1) : null;
    }

    public int getCartRowCount() { return cartTableModel.getRowCount(); }

    public void updateCartRowQuantity(int row, int newQty) {
        if (row >= 0 && row < cartTableModel.getRowCount()) {
            cartTableModel.setValueAt(newQty, row, 4);
            double price = (double) cartTableModel.getValueAt(row, 3);
            cartTableModel.setValueAt(price * newQty, row, 5);
        }
    }

    // Lấy thông tin giỏ hàng
    public DefaultTableModel getCartTableModel() { return cartTableModel; }

    public String getProductSearchQuery() { return txtSearchProduct.getText().trim(); }
    public void clearProductSearchField() { txtSearchProduct.setText(""); }

    // Customer
    public String getCustomerPhone() { return txtCustPhone.getText().trim(); }
    public void setCustomerInfo(Customer c) {
        if (c != null) {
            lblCustName.setText(c.getName());
            lblCustPoints.setText(c.getLoyaltyPoints() + " điểm");
        } else {
            lblCustName.setText("Khách vãng lai");
            lblCustPoints.setText("0 điểm");
        }
    }

    // Calculations
    public void setTotalAmount(double total) {
        lblTotalAmount.setText(String.format("%,.0f đ", total));
    }

    public void setFinalAmount(double finalAmount) {
        lblFinalAmount.setText(String.format("%,.0f đ", finalAmount));
    }

    public double getDiscountValue() {
        try {
            return Double.parseDouble(txtDiscount.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setDiscountValue(double discount) {
        txtDiscount.setText(String.valueOf((long) discount));
    }

    public String getPaymentMethod() {
        String selected = (String) cbPaymentMethod.getSelectedItem();
        return selected.split(" ")[0]; // CASH, TRANSFER
    }

    public String getNotes() { return txtNotes.getText().trim(); }

    public String getInvoiceCode() { return lblInvoiceCode.getText(); }
    public void generateInvoiceCode() {
        lblInvoiceCode.setText("HD" + System.currentTimeMillis() / 1000);
    }

    public int getSelectedQuantitySpinner() {
        return (int) spinQuantity.getValue();
    }

    public void resetPOS() {
        clearCart();
        setCustomerInfo(null);
        txtCustPhone.setText("");
        setTotalAmount(0);
        setDiscountValue(0);
        setFinalAmount(0);
        txtNotes.setText("");
        generateInvoiceCode();
        txtSearchProduct.requestFocus();
    }

    // Listener bindings
    public void addSearchProductEnterListener(ActionListener l) { txtSearchProduct.addActionListener(l); }
    public void addSearchProductKeyListener(KeyAdapter adapter) { txtSearchProduct.addKeyListener(adapter); }
    public void addProductListMouseListener(MouseAdapter adapter) { tblProductList.addMouseListener(adapter); }
    public void addRemoveCartItemListener(ActionListener l) { btnRemoveItem.addActionListener(l); }
    public void addClearCartListener(ActionListener l) { btnClearCart.addActionListener(l); }
    public void addSearchCustomerListener(ActionListener l) { btnSearchCust.addActionListener(l); }
    public void addSearchCustomerEnterListener(ActionListener l) { txtCustPhone.addActionListener(l); }
    public void addAddQuickCustomerListener(ActionListener l) { btnAddQuickCust.addActionListener(l); }
    public void addDiscountKeyListener(KeyAdapter adapter) { txtDiscount.addKeyListener(adapter); }
    public void addPayListener(ActionListener l) { btnPay.addActionListener(l); }
    public void addCancelInvoiceListener(ActionListener l) { btnCancelInvoice.addActionListener(l); }
    public void addCartTableMouseListener(MouseAdapter adapter) { tblCart.addMouseListener(adapter); }
}
