package com.quanlybanhang.view;

import com.quanlybanhang.model.Product;
import com.quanlybanhang.model.Supplier;
import com.quanlybanhang.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Phiếu nhập kho hàng hóa.
 * Hỗ trợ chọn nhà cung cấp, tìm kiếm sản phẩm, quản lý giỏ hàng nhập và hoàn tất thủ tục nhập kho.
 */
public class ImportPanel extends JPanel {
    private User currentUser;

    // Supplier Select
    private JComboBox<Supplier> cbSuppliers;
    
    // Product select list
    private JTable tblProductList;
    private DefaultTableModel prodTableModel;
    private JTextField txtSearchProduct;

    // Import cart table
    private JTable tblImportCart;
    private DefaultTableModel cartTableModel;
    
    // Form Inputs
    private JSpinner spinQuantity;
    private JTextField txtImportPrice;
    private JButton btnAddProduct;
    private JButton btnRemoveProduct;
    
    // Totals & Actions
    private JLabel lblTotalAmount;
    private JButton btnSaveImport;
    private JButton btnCancelImport;
    private JLabel lblReceiptCode;

    public ImportPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.6);

        // ================= TRÁI: DỰN SẢN PHẨM & PHIẾU NHẬP =================
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        
        // 1. Chọn nhà cung cấp & Tìm kiếm SP
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NHÀ CUNG CẤP & SẢN PHẨM"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Chọn NCC
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        headerPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        cbSuppliers = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 0.7;
        headerPanel.add(cbSuppliers, gbc);

        // Tìm kiếm SP
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        headerPanel.add(new JLabel("Tìm sản phẩm:"), gbc);
        txtSearchProduct = new JTextField();
        txtSearchProduct.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc mã để lọc...");
        gbc.gridx = 1; gbc.weightx = 0.7;
        headerPanel.add(txtSearchProduct, gbc);

        leftPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Bảng sản phẩm hiện có
        JPanel prodListPanel = new JPanel(new BorderLayout(5, 5));
        prodListPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH SẢN PHẨM HỆ THỐNG"));
        
        String[] prodCols = {"Mã SP", "Tên sản phẩm", "Giá nhập hiện tại", "Tồn kho", "Đơn vị"};
        prodTableModel = new DefaultTableModel(prodCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductList = new JTable(prodTableModel);
        tblProductList.setRowHeight(22);
        tblProductList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProds = new JScrollPane(tblProductList);
        prodListPanel.add(scrollProds, BorderLayout.CENTER);

        leftPanel.add(prodListPanel, BorderLayout.CENTER);

        // 3. Form thêm sản phẩm vào phiếu nhập
        JPanel addControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        addControlPanel.add(new JLabel("Giá nhập thực tế:"));
        txtImportPrice = new JTextField(10);
        addControlPanel.add(txtImportPrice);

        addControlPanel.add(new JLabel("Số lượng nhập:"));
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinQuantity.setPreferredSize(new Dimension(70, 26));
        addControlPanel.add(spinQuantity);

        btnAddProduct = new JButton("Thêm vào phiếu");
        btnAddProduct.setBackground(new Color(52, 152, 219)); btnAddProduct.setForeground(Color.WHITE);
        addControlPanel.add(btnAddProduct);
        
        leftPanel.add(addControlPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // ================= PHẢI: CHI TIẾT PHIẾU NHẬP KHO =================
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("CHI TIẾT PHIẾU NHẬP"));

        // Thông tin phiếu nhập
        JPanel receiptInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        receiptInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        receiptInfo.add(new JLabel("Mã phiếu nhập:"));
        lblReceiptCode = new JLabel("PN" + System.currentTimeMillis() / 10000);
        lblReceiptCode.setFont(new Font("Segoe UI", Font.BOLD, 13));
        receiptInfo.add(lblReceiptCode);

        receiptInfo.add(new JLabel("Người thực hiện:"));
        JLabel lblUser = new JLabel(currentUser.getFullName());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        receiptInfo.add(lblUser);

        rightPanel.add(receiptInfo, BorderLayout.NORTH);

        // Bảng giỏ hàng phiếu nhập
        String[] cartCols = {"Mã SP", "Tên sản phẩm", "Giá nhập", "S.Lượng", "Thành tiền", "Sản phẩm ID"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblImportCart = new JTable(cartTableModel);
        tblImportCart.setRowHeight(25);
        tblImportCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCart = new JScrollPane(tblImportCart);
        rightPanel.add(scrollCart, BorderLayout.CENTER);

        // Tổng tiền & Nút hoàn tất
        JPanel footerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(5, 10, 5, 10);
        fgbc.weightx = 1.0;

        fgbc.gridx = 0; fgbc.gridy = 0;
        footerPanel.add(new JLabel("Tổng giá trị nhập:"), fgbc);
        lblTotalAmount = new JLabel("0 đ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalAmount.setForeground(new Color(192, 57, 43));
        fgbc.gridx = 1;
        footerPanel.add(lblTotalAmount, fgbc);

        btnRemoveProduct = new JButton("Xóa dòng chọn");
        btnRemoveProduct.setBackground(new Color(230, 126, 34)); btnRemoveProduct.setForeground(Color.WHITE);
        fgbc.gridx = 0; fgbc.gridy = 1; fgbc.gridwidth = 2;
        footerPanel.add(btnRemoveProduct, fgbc);

        btnSaveImport = new JButton("Hoàn Tất Nhập Kho");
        btnSaveImport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveImport.setBackground(new Color(46, 204, 113));
        btnSaveImport.setForeground(Color.WHITE);
        btnSaveImport.setPreferredSize(new Dimension(200, 40));
        fgbc.gridy = 2;
        footerPanel.add(btnSaveImport, fgbc);

        btnCancelImport = new JButton("Hủy phiếu");
        btnCancelImport.setBackground(new Color(149, 165, 166)); btnCancelImport.setForeground(Color.WHITE);
        fgbc.gridy = 3;
        footerPanel.add(btnCancelImport, fgbc);

        rightPanel.add(footerPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    public void setSuppliers(List<Supplier> list) {
        cbSuppliers.removeAllItems();
        for (Supplier s : list) {
            cbSuppliers.addItem(s);
        }
    }

    public void setProductListData(List<Product> products) {
        prodTableModel.setRowCount(0);
        for (Product p : products) {
            prodTableModel.addRow(new Object[]{
                    p.getCode(),
                    p.getName(),
                    p.getPurchasePrice(),
                    p.getStockQuantity(),
                    p.getUnit()
            });
        }
    }

    public Supplier getSelectedSupplier() {
        return (Supplier) cbSuppliers.getSelectedItem();
    }

    public Product getSelectedProduct() {
        int row = tblProductList.getSelectedRow();
        if (row == -1) return null;
        Product p = new Product();
        p.setCode((String) tblProductList.getValueAt(row, 0));
        p.setName((String) tblProductList.getValueAt(row, 1));
        p.setPurchasePrice((double) tblProductList.getValueAt(row, 2));
        p.setStockQuantity((int) tblProductList.getValueAt(row, 3));
        p.setUnit((String) tblProductList.getValueAt(row, 4));
        return p;
    }

    public double getImportPriceInput() {
        try {
            return Double.parseDouble(txtImportPrice.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setImportPriceInput(double val) {
        txtImportPrice.setText(String.valueOf((long) val));
    }

    public int getQuantityInput() {
        return (int) spinQuantity.getValue();
    }

    public void addProductToImportCart(Product p, double importPrice, int qty) {
        // Kiểm tra trùng lặp mã sản phẩm trong giỏ nhập kho
        int existingRow = -1;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            if (cartTableModel.getValueAt(i, 0).equals(p.getCode())) {
                existingRow = i;
                break;
            }
        }

        if (existingRow != -1) {
            int oldQty = (int) cartTableModel.getValueAt(existingRow, 3);
            int newQty = oldQty + qty;
            cartTableModel.setValueAt(newQty, existingRow, 3);
            cartTableModel.setValueAt(importPrice, existingRow, 2); // Cập nhật giá nhập mới nếu đổi
            cartTableModel.setValueAt(newQty * importPrice, existingRow, 4);
        } else {
            cartTableModel.addRow(new Object[]{
                    p.getCode(),
                    p.getName(),
                    importPrice,
                    qty,
                    importPrice * qty,
                    p.getId() // ID ẩn
            });
        }
    }

    public void removeSelectedCartItem() {
        int row = tblImportCart.getSelectedRow();
        if (row != -1) {
            cartTableModel.removeRow(row);
        }
    }

    public void clearCart() {
        cartTableModel.setRowCount(0);
        lblTotalAmount.setText("0 đ");
    }

    public DefaultTableModel getCartTableModel() { return cartTableModel; }

    public void setTotalAmount(double total) {
        lblTotalAmount.setText(String.format("%,.0f đ", total));
    }

    public String getSearchProductQuery() {
        return txtSearchProduct.getText().trim();
    }

    public String getReceiptCode() {
        return lblReceiptCode.getText();
    }

    public void generateReceiptCode() {
        lblReceiptCode.setText("PN" + System.currentTimeMillis() / 1000);
    }

    public void resetImport() {
        clearCart();
        generateReceiptCode();
        txtImportPrice.setText("");
        spinQuantity.setValue(1);
    }

    // Action Listener bindings
    public void addSearchProductEnterListener(ActionListener l) { txtSearchProduct.addActionListener(l); }
    public void addProductListMouseListener(MouseAdapter adapter) { tblProductList.addMouseListener(adapter); }
    public void addAddProductListener(ActionListener l) { btnAddProduct.addActionListener(l); }
    public void addRemoveProductListener(ActionListener l) { btnRemoveProduct.addActionListener(l); }
    public void addSaveImportListener(ActionListener l) { btnSaveImport.addActionListener(l); }
    public void addCancelImportListener(ActionListener l) { btnCancelImport.addActionListener(l); }
}
