package com.quanlybanhang;

import com.formdev.flatlaf.FlatLightLaf;
import com.quanlybanhang.config.DatabaseHelper;
import com.quanlybanhang.controller.LoginController;
import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.view.LoginFrame;

import javax.swing.*;

/**
 * Lớp khởi chạy ứng dụng (Main Entry Point).
 * Thiết lập giao diện FlatLaf và kiểm tra kết nối cơ sở dữ liệu trước khi mở màn hình đăng nhập.
 */
public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf Look and Feel. Using default System Look and Feel.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        boolean dbConnected = DatabaseHelper.testConnection();
        if (!dbConnected) {
            int option = JOptionPane.showConfirmDialog(null, 
                    "Không thể kết nối đến cơ sở dữ liệu PostgreSQL!\n" +
                    "Vui lòng đảm bảo:\n" +
                    "1. PostgreSQL đang chạy.\n" +
                    "2. Database 'quanlybanhang' đã được tạo.\n" +
                    "3. Thông tin trong file 'db.properties' là chính xác.\n\n" +
                    "Bạn có muốn tiếp tục chạy ứng dụng không?", 
                    "CẢNH BÁO KẾT NỐI DATABASE", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
            
            if (option != JOptionPane.YES_OPTION) {
                System.exit(1);
            }
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            UserDAO userDAO = new UserDAO();
            LoginController loginController = new LoginController(loginFrame, userDAO);
            loginController.showView();
        });
    }
}
