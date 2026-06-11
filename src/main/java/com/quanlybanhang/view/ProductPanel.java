package com.quanlybanhang.view;

import com.quanlybanhang.model.Category;
import com.quanlybanhang.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Quản lý Sản phẩm.
 * Hiển thị bảng danh sách sản phẩm, bộ lọc theo danh mục/tìm kiếm và form thêm/sửa/ngưng bán sản phẩm.
 * Hỗ trợ xuất và nhập dữ liệu sản phẩm từ file Excel.
 */
public class ProductPanel extends JPanel {
    private JTable tblProducts;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtCode;
    private JTextField txtName;
    private JComboBox<Category> cbCategory;
    private JTextField txtPurchasePrice;
    private JTextField txtSalePrice;
    private JTextField txtStock;
    private JTextField txtUnit;
    private JTextField txtImagePath;
    private JCheckBox chkStatus;

    // Search
    private JTextField txtSearch;
    private JButton btnSearch;
    private JComboBox<Category> cbFilterCategory;

    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnManageCategory;
    private JButton btnExportExcel;
    private JButton btnImportExcel;

    public ProductPanel() {
        setLayout(new BorderLayout(15, 15));
        initComponents();
    }

    private void initComponents() {
        // --- 1. Top Panel (Tìm kiếm & Bộ lọc) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("BỘ LỌC TÌM KIẾM"));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc mã sản phẩm...");
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.putClientProperty("JButton.buttonType", "roundRect");

        cbFilterCategory = new JComboBox<>();
        cbFilterCategory.setPreferredSize(new Dimension(180, 30));

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(new JLabel("  Danh mục:"));
        topPanel.add(cbFilterCategory);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Bảng danh sách sản phẩm) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH SẢN PHẨM"));

        String[] columns = {"ID", "Mã sản phẩm", "Tên sản phẩm", "Danh mục", "Giá nhập", "Giá bán", "Tồn kho", "Đơn vị", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho edit trực tiếp trên bảng
            }
        };

        tblProducts = new JTable(tableModel);
        tblProducts.setRowHeight(25);
        tblProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblProducts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblProducts);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Nút Excel ở dưới bảng
        JPanel excelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnImportExcel = new JButton("Nhập Excel");
        btnExportExcel = new JButton("Xuất Excel");
        excelPanel.add(btnImportExcel);
        excelPanel.add(btnExportExcel);
        centerPanel.add(excelPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. East Panel (Form thông tin chi tiết) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(360, getHeight()));
        formPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN SẢN PHẨM"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.weightx = 1.0;

        // Mã SP
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã sản phẩm:"), gbc);
        txtCode = new JTextField();
        gbc.gridy = 1;
        formPanel.add(txtCode, gbc);

        // Tên SP
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        txtName = new JTextField();
        gbc.gridy = 3;
        formPanel.add(txtName, gbc);

        // Danh mục & Nút Quản lý nhóm
        gbc.gridy = 4;
        formPanel.add(new JLabel("Nhóm sản phẩm:"), gbc);
        
        JPanel catRow = new JPanel(new BorderLayout(5, 0));
        cbCategory = new JComboBox<>();
        btnManageCategory = new JButton("...");
        btnManageCategory.setToolTipText("Quản lý danh mục");
        catRow.add(cbCategory, BorderLayout.CENTER);
        catRow.add(btnManageCategory, BorderLayout.EAST);
        
        gbc.gridy = 5;
        formPanel.add(catRow, gbc);

        // Đơn vị tính
        gbc.gridy = 6;
        formPanel.add(new JLabel("Đơn vị tính:"), gbc);
        txtUnit = new JTextField();
        gbc.gridy = 7;
        formPanel.add(txtUnit, gbc);

        // Giá nhập & Giá bán (dòng đôi)
        JPanel pricesPanel = new JPanel(new GridLayout(2, 2, 10, 0));
        pricesPanel.add(new JLabel("Giá nhập:"));
        pricesPanel.add(new JLabel("Giá bán:"));
        txtPurchasePrice = new JTextField();
        txtSalePrice = new JTextField();
        pricesPanel.add(txtPurchasePrice);
        pricesPanel.add(txtSalePrice);
        gbc.gridy = 8;
        formPanel.add(pricesPanel, gbc);

        // Số lượng tồn
        gbc.gridy = 9;
        formPanel.add(new JLabel("Số lượng tồn kho:"), gbc);
        txtStock = new JTextField();
        gbc.gridy = 10;
        formPanel.add(txtStock, gbc);

        // Đường dẫn ảnh
        gbc.gridy = 11;
        formPanel.add(new JLabel("Đường dẫn ảnh:"), gbc);
        txtImagePath = new JTextField();
        gbc.gridy = 12;
        formPanel.add(txtImagePath, gbc);

        // Trạng thái hoạt động
        chkStatus = new JCheckBox("Còn hoạt động (Được bán)", true);
        gbc.gridy = 13;
        formPanel.add(chkStatus, gbc);

        // Nút chức năng Form (CRUD)
        JPanel formButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        formButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Cập nhật");
        btnDelete = new JButton("Ngưng bán");
        btnClear = new JButton("Làm mới");

        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(241, 196, 15)); btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(231, 76, 60)); btnDelete.setForeground(Color.WHITE);

        formButtons.add(btnAdd);
        formButtons.add(btnEdit);
        formButtons.add(btnDelete);
        formButtons.add(btnClear);

        gbc.gridy = 14;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(formButtons, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    // Setters / Getters cho các combobox nhóm
    public void setCategories(List<Category> categories) {
        cbCategory.removeAllItems();
        cbFilterCategory.removeAllItems();
        
        // Thêm mục "Tất cả" cho combobox filter
        Category allCategory = new Category(0, "Tất cả danh mục", "");
        cbFilterCategory.addItem(allCategory);

        for (Category c : categories) {
            cbCategory.addItem(c);
            cbFilterCategory.addItem(c);
        }
    }

    public void setProductTableData(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getCode(),
                    p.getName(),
                    p.getCategoryName() != null ? p.getCategoryName() : "Không",
                    String.format("%,.0f đ", p.getPurchasePrice()),
                    String.format("%,.0f đ", p.getSalePrice()),
                    p.getStockQuantity(),
                    p.getUnit(),
                    p.isStatus() ? "Đang bán" : "Ngưng bán"
            });
        }
    }

    public Product getSelectedProduct() {
        int row = tblProducts.getSelectedRow();
        if (row == -1) return null;
        
        Product p = new Product();
        p.setId((int) tblProducts.getValueAt(row, 0));
        p.setCode((String) tblProducts.getValueAt(row, 1));
        p.setName((String) tblProducts.getValueAt(row, 2));
        p.setCategoryName((String) tblProducts.getValueAt(row, 3));
        p.setStockQuantity((int) tblProducts.getValueAt(row, 6));
        p.setUnit((String) tblProducts.getValueAt(row, 7));
        p.setStatus(tblProducts.getValueAt(row, 8).equals("Đang bán"));
        return p;
    }

    // Lấy thông tin từ Form
    public Product getProductFromForm() {
        Product p = new Product();
        p.setCode(txtCode.getText().trim());
        p.setName(txtName.getText().trim());
        Category selectedCategory = (Category) cbCategory.getSelectedItem();
        if (selectedCategory != null) {
            p.setCategoryId(selectedCategory.getId());
        }
        p.setUnit(txtUnit.getText().trim());
        try {
            p.setPurchasePrice(Double.parseDouble(txtPurchasePrice.getText().trim()));
            p.setSalePrice(Double.parseDouble(txtSalePrice.getText().trim()));
            p.setStockQuantity(Integer.parseInt(txtStock.getText().trim()));
        } catch (NumberFormatException e) {
            // Sẽ kiểm tra lỗi ở Controller
            return null;
        }
        p.setImagePath(txtImagePath.getText().trim());
        p.setStatus(chkStatus.isSelected());
        return p;
    }

    // Hiển thị sản phẩm lên Form khi click vào bảng
    public void showProductOnForm(Product p) {
        txtCode.setText(p.getCode());
        txtCode.setEditable(false); // Không cho sửa mã sản phẩm trực tiếp
        txtName.setText(p.getName());
        txtUnit.setText(p.getUnit());
        txtPurchasePrice.setText(String.valueOf((long) p.getPurchasePrice()));
        txtSalePrice.setText(String.valueOf((long) p.getSalePrice()));
        txtStock.setText(String.valueOf(p.getStockQuantity()));
        txtImagePath.setText(p.getImagePath());
        chkStatus.setSelected(p.isStatus());
        
        // Chọn danh mục tương ứng
        for (int i = 0; i < cbCategory.getItemCount(); i++) {
            Category c = cbCategory.getItemAt(i);
            if (c.getId() == p.getCategoryId()) {
                cbCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    public void clearForm() {
        txtCode.setText("");
        txtCode.setEditable(true);
        txtName.setText("");
        txtUnit.setText("Chiếc");
        txtPurchasePrice.setText("");
        txtSalePrice.setText("");
        txtStock.setText("0");
        txtImagePath.setText("");
        chkStatus.setSelected(true);
        tblProducts.clearSelection();
    }

    public String getSearchQuery() { return txtSearch.getText().trim(); }
    public Category getSelectedFilterCategory() { return (Category) cbFilterCategory.getSelectedItem(); }

    // Action listener bindings
    public void addSearchListener(ActionListener l) { btnSearch.addActionListener(l); }
    public void addFilterCategoryListener(ActionListener l) { cbFilterCategory.addActionListener(l); }
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addManageCategoryListener(ActionListener l) { btnManageCategory.addActionListener(l); }
    public void addImportExcelListener(ActionListener l) { btnImportExcel.addActionListener(l); }
    public void addExportExcelListener(ActionListener l) { btnExportExcel.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter adapter) {
        tblProducts.addMouseListener(adapter);
    }
}
