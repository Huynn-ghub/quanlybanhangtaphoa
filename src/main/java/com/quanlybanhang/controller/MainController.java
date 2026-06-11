package com.quanlybanhang.controller;

import com.quanlybanhang.dao.*;
import com.quanlybanhang.model.User;
import com.quanlybanhang.view.LoginFrame;
import com.quanlybanhang.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller điều phối trung tâm của ứng dụng.
 * Quản lý thanh điều hướng và kích hoạt các sub-controllers tương ứng.
 */
public class MainController {
    private MainFrame view;
    private User currentUser;

    // Sub-controllers
    private DashboardController dashboardController;
    private SalesController salesController;
    private ProductController productController;
    private CustomerController customerController;
    private SupplierController supplierController;
    private ImportController importController;
    private UserController userController;

    public MainController(MainFrame view, User currentUser) {
        this.view = view;
        this.currentUser = currentUser;

        initSubControllers();
        initNavigation();
    }

    private void initSubControllers() {
        dashboardController = new DashboardController(view.getDashboardPanel(), new OrderDAO(), new CustomerDAO(), new ProductDAO());
        salesController = new SalesController(view.getSalesPanel(), new ProductDAO(), new CustomerDAO(), new OrderDAO(), currentUser);
        productController = new ProductController(view.getProductPanel(), new ProductDAO(), new CategoryDAO());
        customerController = new CustomerController(view.getCustomerPanel(), new CustomerDAO());
        supplierController = new SupplierController(view.getSupplierPanel(), new SupplierDAO());
        importController = new ImportController(view.getImportPanel(), new ProductDAO(), new SupplierDAO(), new ImportReceiptDAO(), currentUser);
        
        if (currentUser.getRole().equals("ADMIN")) {
            userController = new UserController(view.getUserPanel(), new UserDAO());
        }
    }

    private void initNavigation() {
        view.addDashboardListener(e -> showPanel("DASHBOARD"));
        view.addPOSListener(e -> showPanel("POS"));
        view.addProductListener(e -> showPanel("PRODUCT"));
        view.addCustomerListener(e -> showPanel("CUSTOMER"));
        view.addSupplierListener(e -> showPanel("SUPPLIER"));
        view.addImportListener(e -> showPanel("IMPORT"));
        
        if (currentUser.getRole().equals("ADMIN")) {
            view.addUserListener(e -> showPanel("USER"));
        }

        view.addLogoutListener(new LogoutButtonListener());

        showPanel("DASHBOARD");
    }

    public void showView() {
        view.setVisible(true);
    }

    private void showPanel(String cardName) {
        view.switchPanel(cardName);

        switch (cardName) {
            case "DASHBOARD" -> dashboardController.loadDashboardStats();
            case "POS" -> salesController.refreshData();
            case "PRODUCT" -> productController.loadData();
            case "CUSTOMER" -> customerController.loadData();
            case "SUPPLIER" -> supplierController.loadData();
            case "IMPORT" -> importController.refreshData();
            case "USER" -> {
                if (userController != null) userController.loadData();
            }
        }
    }

    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int confirm = JOptionPane.showConfirmDialog(view, 
                    "Bạn chắc chắn muốn đăng xuất?", 
                    "Xác nhận đăng xuất", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                view.dispose();

                SwingUtilities.invokeLater(() -> {
                    LoginFrame loginFrame = new LoginFrame();
                    UserDAO userDAO = new UserDAO();
                    LoginController loginController = new LoginController(loginFrame, userDAO);
                    loginController.showView();
                });
            }
        }
    }
}
