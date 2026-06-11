package com.quanlybanhang.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Giao diện cửa sổ Đăng nhập.
 * Yêu cầu nhập tên đăng nhập và mật khẩu, hỗ trợ phím Enter để đăng nhập nhanh.
 */
public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JLabel lblError;

    public LoginFrame() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG - QUẢN LÝ BÁN HÀNG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        setResizable(false);
        
        initComponents();
    }

    private void initComponents() {
        // Layout chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Phần Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel("CỬA HÀNG ĐIỆN THOẠI & PHỤ KIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(0, 102, 204));
        
        JLabel lblSubtitle = new JLabel("Đăng nhập để tiếp tục làm việc");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setForeground(Color.GRAY);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblSubtitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Phần Form Nhập liệu
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Username Label & Textbox
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Đặt viền bo tròn hoặc padding cho đẹp hơn
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập mã nhân viên hoặc username");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        // Password Label & Textbox
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.putClientProperty("JPasswordField.placeholderText", "••••••••");
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true); // Feature FlatLaf ẩn/hiển thị MK
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        // Label hiển thị lỗi
        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(lblError, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Phần Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setPreferredSize(new Dimension(110, 35));
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        btnLogin.putClientProperty("JButton.borderColor", new Color(0, 102, 204));
        // FlatLaf styling class for colored button
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(80, 35));
        btnCancel.putClientProperty("JButton.buttonType", "roundRect");

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Đặt nút Enter làm nút kích hoạt đăng nhập mặc định
        getRootPane().setDefaultButton(btnLogin);

        add(mainPanel);
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        lblError.setText(message);
    }

    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
        txtUsername.requestFocus();
    }
}
