package com.quanlybanhang.controller;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.model.User;
import com.quanlybanhang.view.LoginFrame;
import com.quanlybanhang.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller điều khiển chức năng Đăng nhập.
 * Xác thực thông tin người dùng qua UserDAO và mở cửa sổ làm việc chính (MainFrame).
 */
public class LoginController {
    private LoginFrame view;
    private UserDAO model;

    public LoginController(LoginFrame view, UserDAO model) {
        this.view = view;
        this.model = model;

        this.view.addLoginListener(new LoginButtonListener());
        this.view.addCancelListener(new CancelButtonListener());
    }

    public void showView() {
        view.setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            String password = view.getPassword();

            if (username.isEmpty()) {
                view.showErrorMessage("Vui lòng nhập tên đăng nhập!");
                return;
            }
            if (password.isEmpty()) {
                view.showErrorMessage("Vui lòng nhập mật khẩu!");
                return;
            }

            view.showErrorMessage("Đang xác thực...");

            SwingWorker<User, Void> worker = new SwingWorker<>() {
                @Override
                protected User doInBackground() {
                    return model.login(username, password);
                }

                @Override
                protected void done() {
                    try {
                        User user = get();
                        if (user != null) {
                            view.showErrorMessage(" ");
                            view.dispose();

                            SwingUtilities.invokeLater(() -> {
                                MainFrame mainFrame = new MainFrame(user);
                                MainController mainController = new MainController(mainFrame, user);
                                mainController.showView();
                            });
                        } else {
                            view.showErrorMessage("Tên đăng nhập hoặc mật khẩu không chính xác!");
                        }
                    } catch (Exception ex) {
                        view.showErrorMessage("Lỗi kết nối cơ sở dữ liệu!");
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}
