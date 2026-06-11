package com.quanlybanhang.view;

import com.quanlybanhang.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

/**
 * Giao diện Quản lý Tài khoản Nhân viên (chỉ dành cho ADMIN).
 * Hiển thị bảng danh sách các nhân viên, form nhập liệu thêm mới, cập nhật thông tin,
 * vô hiệu hóa tài khoản và đặt lại mật khẩu nhân viên.
 */
public class UserPanel extends JPanel {
    private JTable tblUsers;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cbRole;
    private JCheckBox chkStatus;

    // Buttons
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnResetPassword;

    public UserPanel() {
        setLayout(new BorderLayout(15, 15));
        initComponents();
    }

    private void initComponents() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("DANH SÁCH NHÂN VIÊN HỆ THỐNG"));

        String[] columns = {"ID", "Tên đăng nhập", "Họ và tên", "Email", "Số điện thoại", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsers = new JTable(tableModel);
        tblUsers.setRowHeight(25);
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tblUsers);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Form panel (phải)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(340, getHeight()));
        formPanel.setBorder(BorderFactory.createTitledBorder("THÔNG TIN NHÂN VIÊN"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.weightx = 1.0;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        txtUsername = new JTextField();
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        // Password (chỉ dùng khi thêm mới)
        gbc.gridy = 2;
        formPanel.add(new JLabel("Mật khẩu (Khi thêm mới):"), gbc);
        txtPassword = new JPasswordField();
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        // FullName
        gbc.gridy = 4;
        formPanel.add(new JLabel("Họ và tên:"), gbc);
        txtFullName = new JTextField();
        gbc.gridy = 5;
        formPanel.add(txtFullName, gbc);

        // Phone
        gbc.gridy = 6;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        txtPhone = new JTextField();
        gbc.gridy = 7;
        formPanel.add(txtPhone, gbc);

        // Email
        gbc.gridy = 8;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField();
        gbc.gridy = 9;
        formPanel.add(txtEmail, gbc);

        // Role
        gbc.gridy = 10;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        cbRole = new JComboBox<>(new String[]{"EMPLOYEE", "ADMIN"});
        gbc.gridy = 11;
        formPanel.add(cbRole, gbc);

        // Status
        chkStatus = new JCheckBox("Còn hoạt động", true);
        gbc.gridy = 12;
        formPanel.add(chkStatus, gbc);

        // Buttons
        JPanel formButtons = new JPanel(new GridLayout(3, 2, 8, 8));
        formButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnAdd = new JButton("Thêm mới");
        btnEdit = new JButton("Cập nhật");
        btnDelete = new JButton("Vô hiệu hóa");
        btnClear = new JButton("Làm mới");
        btnResetPassword = new JButton("Đặt lại MK");

        btnAdd.setBackground(new Color(46, 204, 113)); btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(241, 196, 15)); btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(231, 76, 60)); btnDelete.setForeground(Color.WHITE);
        btnResetPassword.setBackground(new Color(52, 152, 219)); btnResetPassword.setForeground(Color.WHITE);

        formButtons.add(btnAdd);
        formButtons.add(btnEdit);
        formButtons.add(btnDelete);
        formButtons.add(btnClear);
        
        // Button Reset Password chiếm full hàng ngang cuối của cụm button
        gbc.gridy = 13;
        formPanel.add(formButtons, gbc);

        gbc.gridy = 14;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(btnResetPassword, gbc);

        // Đẩy lên trên cùng
        gbc.gridy = 15; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JLabel(" "), gbc);

        add(formPanel, BorderLayout.EAST);
    }

    public void setUserTableData(List<User> list) {
        tableModel.setRowCount(0);
        for (User u : list) {
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getEmail() != null ? u.getEmail() : "",
                    u.getPhone() != null ? u.getPhone() : "",
                    u.getRole(),
                    u.isStatus() ? "Hoạt động" : "Vô hiệu"
            });
        }
    }

    public User getSelectedUser() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) return null;
        
        User u = new User();
        u.setId((int) tblUsers.getValueAt(row, 0));
        u.setUsername((String) tblUsers.getValueAt(row, 1));
        u.setFullName((String) tblUsers.getValueAt(row, 2));
        u.setEmail((String) tblUsers.getValueAt(row, 3));
        u.setPhone((String) tblUsers.getValueAt(row, 4));
        u.setRole((String) tblUsers.getValueAt(row, 5));
        u.setStatus(tblUsers.getValueAt(row, 6).equals("Hoạt động"));
        return u;
    }

    public User getUserFromForm() {
        User u = new User();
        u.setUsername(txtUsername.getText().trim());
        u.setPassword(new String(txtPassword.getPassword()));
        u.setFullName(txtFullName.getText().trim());
        u.setPhone(txtPhone.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setRole((String) cbRole.getSelectedItem());
        u.setStatus(chkStatus.isSelected());
        return u;
    }

    public void showUserOnForm(User u) {
        txtUsername.setText(u.getUsername());
        txtUsername.setEditable(false);
        txtPassword.setText(""); // Không hiển thị mật khẩu hiện tại vì lý do bảo mật
        txtPassword.setEnabled(false); // Vô hiệu hóa trường mật khẩu khi sửa
        txtFullName.setText(u.getFullName());
        txtPhone.setText(u.getPhone());
        txtEmail.setText(u.getEmail());
        cbRole.setSelectedItem(u.getRole());
        chkStatus.setSelected(u.isStatus());
    }

    public void clearForm() {
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        txtPassword.setEnabled(true);
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cbRole.setSelectedIndex(0);
        chkStatus.setSelected(true);
        tblUsers.clearSelection();
    }

    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addEditListener(ActionListener l) { btnEdit.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addResetPasswordListener(ActionListener l) { btnResetPassword.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter adapter) {
        tblUsers.addMouseListener(adapter);
    }
}
