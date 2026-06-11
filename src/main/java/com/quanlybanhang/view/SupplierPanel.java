package com.quanlybanhang.view;

import com.quanlybanhang.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Quản lý Nhà cung cấp.
 * Hiển thị bảng danh sách các nhà cung cấp, form nhập liệu thêm, sửa, xóa thông tin nhà cung cấp.
 */
public class SupplierPanel extends JPanel {
    private JTable tblSuppliers;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;

    // Search
    private JTextField txtSearch;
    private JButton btnSearch;

    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;

    public SupplierPanel() {
        setLayout(new BorderLayout(15, 15));
        initComponents();
    }

    private void initComponents() {
        // --- 1. Top Panel (Tìm kiếm) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("BỘ LỌC TÌM KIẾM"));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên nhà cung cấp...");
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.putClientProperty("JButton.buttonType", "roundRect");

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Bảng danh sách) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH NHÀ CUNG CẤP"));

        String[] columns = {"ID", "Mã nhà cung cấp", "Tên nhà cung cấp", "Số điện thoại", "Email", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSuppliers = new JTable(tableModel);
        tblSuppliers.setRowHeight(25);
        tblSuppliers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblSuppliers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblSuppliers);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. East Panel (Form CRUD) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(340, getHeight()));
        formPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NHÀ CUNG CẤP"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        // Mã NCC
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã nhà cung cấp:"), gbc);
        txtCode = new JTextField();
        gbc.gridy = 1;
        formPanel.add(txtCode, gbc);

        // Tên NCC
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tên nhà cung cấp:"), gbc);
        txtName = new JTextField();
        gbc.gridy = 3;
        formPanel.add(txtName, gbc);

        // SĐT
        gbc.gridy = 4;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        txtPhone = new JTextField();
        gbc.gridy = 5;
        formPanel.add(txtPhone, gbc);

        // Email
        gbc.gridy = 6;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField();
        gbc.gridy = 7;
        formPanel.add(txtEmail, gbc);

        // Địa chỉ
        gbc.gridy = 8;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtAddress = new JTextField();
        gbc.gridy = 9;
        formPanel.add(txtAddress, gbc);

        // Nút chức năng
        JPanel formButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        formButtons.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa bỏ");
        btnClear = new JButton("Làm mới");

        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(241, 196, 15)); btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(231, 76, 60)); btnDelete.setForeground(Color.WHITE);

        formButtons.add(btnAdd);
        formButtons.add(btnEdit);
        formButtons.add(btnDelete);
        formButtons.add(btnClear);

        gbc.gridy = 10;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(formButtons, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    public void setSupplierTableData(List<Supplier> list) {
        tableModel.setRowCount(0);
        for (Supplier s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getCode(),
                    s.getName(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getAddress()
            });
        }
    }

    public Supplier getSelectedSupplier() {
        int row = tblSuppliers.getSelectedRow();
        if (row == -1) return null;
        
        Supplier s = new Supplier();
        s.setId((int) tblSuppliers.getValueAt(row, 0));
        s.setCode((String) tblSuppliers.getValueAt(row, 1));
        s.setName((String) tblSuppliers.getValueAt(row, 2));
        s.setPhone((String) tblSuppliers.getValueAt(row, 3));
        s.setEmail((String) tblSuppliers.getValueAt(row, 4));
        s.setAddress((String) tblSuppliers.getValueAt(row, 5));
        return s;
    }

    public Supplier getSupplierFromForm() {
        Supplier s = new Supplier();
        s.setCode(txtCode.getText().trim());
        s.setName(txtName.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setAddress(txtAddress.getText().trim());
        return s;
    }

    public void showSupplierOnForm(Supplier s) {
        txtCode.setText(s.getCode());
        txtCode.setEditable(false);
        txtName.setText(s.getName());
        txtPhone.setText(s.getPhone());
        txtEmail.setText(s.getEmail());
        txtAddress.setText(s.getAddress());
    }

    public void clearForm() {
        txtCode.setText("");
        txtCode.setEditable(true);
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tblSuppliers.clearSelection();
    }

    public String getSearchQuery() { return txtSearch.getText().trim(); }

    public void addSearchListener(ActionListener l) { btnSearch.addActionListener(l); }
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter adapter) {
        tblSuppliers.addMouseListener(adapter);
    }
}
