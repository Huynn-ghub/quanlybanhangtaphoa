package com.quanlybanhang.view;

import com.quanlybanhang.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Quản lý Khách hàng.
 * Hiển thị danh sách khách hàng và biểu mẫu thêm, sửa, xóa thông tin khách hàng.
 */
public class CustomerPanel extends JPanel {
    private JTable tblCustomers;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtCode;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JTextField txtPoints;

    // Search
    private JTextField txtSearch;
    private JButton btnSearch;

    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;

    public CustomerPanel() {
        setLayout(new BorderLayout(15, 15));
        initComponents();
    }

    private void initComponents() {
        // --- 1. Top Panel (Tìm kiếm) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("BỘ LỌC TÌM KIẾM"));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập SĐT hoặc tên khách hàng...");
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.putClientProperty("JButton.buttonType", "roundRect");

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Bảng danh sách) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH KHÁCH HÀNG"));

        String[] columns = {"ID", "Mã khách hàng", "Họ và tên", "Số điện thoại", "Email", "Địa chỉ", "Điểm tích lũy"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCustomers = new JTable(tableModel);
        tblCustomers.setRowHeight(25);
        tblCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. East Panel (Form CRUD) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(340, getHeight()));
        formPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN KHÁCH HÀNG"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;

        // Mã KH
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã khách hàng:"), gbc);
        txtCode = new JTextField();
        gbc.gridy = 1;
        formPanel.add(txtCode, gbc);

        // Tên KH
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tên khách hàng:"), gbc);
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

        // Điểm tích lũy
        gbc.gridy = 10;
        formPanel.add(new JLabel("Điểm tích lũy:"), gbc);
        txtPoints = new JTextField("0");
        txtPoints.setEditable(false);
        gbc.gridy = 11;
        formPanel.add(txtPoints, gbc);

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

        gbc.gridy = 12;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(formButtons, gbc);

        add(formPanel, BorderLayout.EAST);
    }

    public void setCustomerTableData(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getCode(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getAddress(),
                    c.getLoyaltyPoints()
            });
        }
    }

    public Customer getSelectedCustomer() {
        int row = tblCustomers.getSelectedRow();
        if (row == -1) return null;
        
        Customer c = new Customer();
        c.setId((int) tblCustomers.getValueAt(row, 0));
        c.setCode((String) tblCustomers.getValueAt(row, 1));
        c.setName((String) tblCustomers.getValueAt(row, 2));
        c.setPhone((String) tblCustomers.getValueAt(row, 3));
        c.setEmail((String) tblCustomers.getValueAt(row, 4));
        c.setAddress((String) tblCustomers.getValueAt(row, 5));
        c.setLoyaltyPoints((int) tblCustomers.getValueAt(row, 6));
        return c;
    }

    public Customer getCustomerFromForm() {
        Customer c = new Customer();
        c.setCode(txtCode.getText().trim());
        c.setName(txtName.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        try {
            c.setLoyaltyPoints(Integer.parseInt(txtPoints.getText().trim()));
        } catch (NumberFormatException e) {
            c.setLoyaltyPoints(0);
        }
        return c;
    }

    public void showCustomerOnForm(Customer c) {
        txtCode.setText(c.getCode());
        txtCode.setEditable(false);
        txtName.setText(c.getName());
        txtPhone.setText(c.getPhone());
        txtEmail.setText(c.getEmail());
        txtAddress.setText(c.getAddress());
        txtPoints.setText(String.valueOf(c.getLoyaltyPoints()));
    }

    public void clearForm() {
        txtCode.setText("");
        txtCode.setEditable(true);
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtPoints.setText("0");
        tblCustomers.clearSelection();
    }

    public String getSearchQuery() { return txtSearch.getText().trim(); }

    public void addSearchListener(ActionListener l) { btnSearch.addActionListener(l); }
    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter adapter) {
        tblCustomers.addMouseListener(adapter);
    }
}
