package com.quanlybanhang.view;

import com.quanlybanhang.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Giao diện cửa sổ chính của ứng dụng.
 * Tích hợp thanh điều hướng bên trái (Sidebar) và vùng hiển thị chính chứa các sub-panels.
 */
public class MainFrame extends JFrame {
    private User currentUser;
    
    // Sidebar Buttons
    private JButton btnDashboard;
    private JButton btnPOS;
    private JButton btnOrderHistory;
    private JButton btnProduct;
    private JButton btnCustomer;
    private JButton btnSupplier;
    private JButton btnImport;
    private JButton btnUser;
    private JButton btnLogout;

    // Content Panels (CardLayout)
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sub panels
    private DashboardPanel dashboardPanel;
    private SalesPanel salesPanel;
    private OrderHistoryPanel orderHistoryPanel;
    private ProductPanel productPanel;
    private CustomerPanel customerPanel;
    private SupplierPanel supplierPanel;
    private ImportPanel importPanel;
    private UserPanel userPanel;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        
        setTitle("HỆ THỐNG QUẢN LÝ BÁN HÀNG - CỬA HÀNG CÔNG NGHỆ");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Mở rộng tối đa màn hình
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. Sidebar Panel (Trái)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(33, 47, 60)); // Màu sẫm tối cao cấp
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());

        // Header Sidebar
        JPanel sbHeader = new JPanel();
        sbHeader.setOpaque(false);
        sbHeader.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sbHeader.setLayout(new BoxLayout(sbHeader, BoxLayout.Y_AXIS));
        
        JLabel lblLogo = new JLabel("QB-TECH MANAGER");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblRole = new JLabel(currentUser.getRole().equals("ADMIN") ? "Quyền: Quản Trị Viên" : "Quyền: Nhân Viên");
        lblRole.setForeground(new Color(235, 152, 78)); // Màu cam nhạt
        lblRole.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        sbHeader.add(lblLogo);
        sbHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        sbHeader.add(lblRole);
        sidebarPanel.add(sbHeader, BorderLayout.NORTH);

        // Sidebar Navigation Buttons
        JPanel sbMenu = new JPanel();
        sbMenu.setOpaque(false);
        sbMenu.setLayout(new GridLayout(9, 1, 0, 8));
        sbMenu.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btnDashboard  = createSidebarButton("Trang Chủ / Báo Cáo", "/icons/dashboard.png");
        btnPOS          = createSidebarButton("Bán Hàng (POS)", "/icons/pos.png");
        btnOrderHistory = createSidebarButton("Lịch Sử Hóa Đơn", "/icons/dashboard.png");
        btnProduct      = createSidebarButton("Quản Lý Sản Phẩm", "/icons/product.png");
        btnCustomer     = createSidebarButton("Quản Lý Khách Hàng", "/icons/customer.png");
        btnSupplier     = createSidebarButton("Quản Lý Nhà Cung Cấp", "/icons/supplier.png");
        btnImport       = createSidebarButton("Nhập Hàng Kho", "/icons/import.png");
        btnUser         = createSidebarButton("Quản Lý Nhân Viên", "/icons/user.png");
        
        sbMenu.add(btnDashboard);
        sbMenu.add(btnPOS);
        sbMenu.add(btnOrderHistory);
        sbMenu.add(btnProduct);
        sbMenu.add(btnCustomer);
        sbMenu.add(btnSupplier);
        sbMenu.add(btnImport);
        
        // Chỉ Admin mới được hiển thị chức năng Quản lý Nhân viên
        if (currentUser.getRole().equals("ADMIN")) {
            sbMenu.add(btnUser);
        } else {
            sbMenu.add(new JLabel(" ")); // Placeholder giữ đều layout
        }
        
        sidebarPanel.add(sbMenu, BorderLayout.CENTER);

        // Footer Sidebar (Đăng xuất)
        JPanel sbFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sbFooter.setOpaque(false);
        sbFooter.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setPreferredSize(new Dimension(180, 40));
        btnLogout.setBackground(new Color(192, 57, 43)); // Đỏ gạch
        btnLogout.setForeground(Color.WHITE);
        btnLogout.putClientProperty("JButton.buttonType", "roundRect");
        sbFooter.add(btnLogout);
        
        sidebarPanel.add(sbFooter, BorderLayout.SOUTH);
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Header Panel (Trên cùng bên phải)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 232, 232)));
        
        JLabel lblWelcome = new JLabel("Xin chào, " + currentUser.getFullName() + " | Hệ thống vận hành ổn định.");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(lblWelcome, BorderLayout.WEST);

        JLabel lblDateTime = new JLabel("Hôm nay: " + java.time.LocalDate.now().toString() + "   ");
        lblDateTime.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDateTime.setForeground(Color.GRAY);
        headerPanel.add(lblDateTime, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 3. Content Panel (Giữa)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Khởi tạo các Sub-Panel View
        dashboardPanel    = new DashboardPanel();
        salesPanel        = new SalesPanel(currentUser);
        orderHistoryPanel = new OrderHistoryPanel();
        productPanel      = new ProductPanel();
        customerPanel     = new CustomerPanel();
        supplierPanel     = new SupplierPanel();
        importPanel       = new ImportPanel(currentUser);
        userPanel         = new UserPanel();

        // Thêm vào card layout
        contentPanel.add(dashboardPanel,    "DASHBOARD");
        contentPanel.add(salesPanel,        "POS");
        contentPanel.add(orderHistoryPanel, "ORDER_HISTORY");
        contentPanel.add(productPanel,      "PRODUCT");
        contentPanel.add(customerPanel,     "CUSTOMER");
        contentPanel.add(supplierPanel,     "SUPPLIER");
        contentPanel.add(importPanel,       "IMPORT");
        if (currentUser.getRole().equals("ADMIN")) {
            contentPanel.add(userPanel, "USER");
        }

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(213, 219, 219));
        btn.setBackground(new Color(33, 47, 60));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Hover effect styling
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Chuyển tab giao diện
    public void switchPanel(String cardName) {
        cardLayout.show(contentPanel, cardName);
        
        // Reset active colors cho sidebar buttons
        resetButtonColors();
        switch (cardName) {
            case "DASHBOARD"     -> setButtonActive(btnDashboard);
            case "POS"           -> setButtonActive(btnPOS);
            case "ORDER_HISTORY" -> setButtonActive(btnOrderHistory);
            case "PRODUCT"       -> setButtonActive(btnProduct);
            case "CUSTOMER"      -> setButtonActive(btnCustomer);
            case "SUPPLIER"      -> setButtonActive(btnSupplier);
            case "IMPORT"        -> setButtonActive(btnImport);
            case "USER"          -> setButtonActive(btnUser);
        }
    }

    private void resetButtonColors() {
        Color darkBlue = new Color(33, 47, 60);
        Color lightText = new Color(213, 219, 219);
        
        btnDashboard.setBackground(darkBlue);    btnDashboard.setForeground(lightText);
        btnPOS.setBackground(darkBlue);          btnPOS.setForeground(lightText);
        btnOrderHistory.setBackground(darkBlue); btnOrderHistory.setForeground(lightText);
        btnProduct.setBackground(darkBlue);      btnProduct.setForeground(lightText);
        btnCustomer.setBackground(darkBlue);     btnCustomer.setForeground(lightText);
        btnSupplier.setBackground(darkBlue);     btnSupplier.setForeground(lightText);
        btnImport.setBackground(darkBlue);       btnImport.setForeground(lightText);
        if (btnUser != null) {
            btnUser.setBackground(darkBlue); btnUser.setForeground(lightText);
        }
    }

    private void setButtonActive(JButton btn) {
        if (btn != null) {
            btn.setBackground(new Color(52, 152, 219)); // Xanh dương tươi sáng
            btn.setForeground(Color.WHITE);
        }
    }

    public void addDashboardListener(ActionListener listener)    { btnDashboard.addActionListener(listener); }
    public void addPOSListener(ActionListener listener)          { btnPOS.addActionListener(listener); }
    public void addOrderHistoryListener(ActionListener listener) { btnOrderHistory.addActionListener(listener); }
    public void addProductListener(ActionListener listener)      { btnProduct.addActionListener(listener); }
    public void addCustomerListener(ActionListener listener)     { btnCustomer.addActionListener(listener); }
    public void addSupplierListener(ActionListener listener)     { btnSupplier.addActionListener(listener); }
    public void addImportListener(ActionListener listener)       { btnImport.addActionListener(listener); }
    public void addUserListener(ActionListener listener)         { if (btnUser != null) btnUser.addActionListener(listener); }
    public void addLogoutListener(ActionListener listener)       { btnLogout.addActionListener(listener); }

    // Getters cho các Sub Panel để Controller quản lý
    public DashboardPanel    getDashboardPanel()    { return dashboardPanel; }
    public SalesPanel        getSalesPanel()        { return salesPanel; }
    public OrderHistoryPanel getOrderHistoryPanel() { return orderHistoryPanel; }
    public ProductPanel      getProductPanel()      { return productPanel; }
    public CustomerPanel     getCustomerPanel()     { return customerPanel; }
    public SupplierPanel     getSupplierPanel()     { return supplierPanel; }
    public ImportPanel       getImportPanel()       { return importPanel; }
    public UserPanel         getUserPanel()         { return userPanel; }
}
