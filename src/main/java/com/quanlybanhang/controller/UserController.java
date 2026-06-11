package com.quanlybanhang.controller;

import com.quanlybanhang.dao.UserDAO;
import com.quanlybanhang.model.User;
import com.quanlybanhang.view.UserPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Controller điều khiển quản lý Nhân viên (chỉ dành cho tài khoản có vai trò ADMIN).
 * Kết nối dữ liệu từ UserDAO sang UserPanel.
 */
public class UserController {
    private UserPanel view;
    private UserDAO userDAO;

    public UserController(UserPanel view, UserDAO userDAO) {
        this.view = view;
        this.userDAO = userDAO;

        initListeners();
    }

    private void initListeners() {
        view.addAddListener(e -> addUser());
        view.addEditListener(e -> editUser());
        view.addDeleteListener(e -> deactivateUser());
        view.addClearListener(e -> view.clearForm());
        view.addResetPasswordListener(e -> resetPassword());

        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                User selected = view.getSelectedUser();
                if (selected != null) {
                    User detailed = userDAO.getById(selected.getId());
                    if (detailed != null) {
                        view.showUserOnForm(detailed);
                    }
                }
            }
        });
    }

    public void loadData() {
        List<User> list = userDAO.getAll();
        view.setUserTableData(list);
        view.clearForm();
    }

    private void addUser() {
        User u = view.getUserFromForm();
        if (u.getUsername().isEmpty() || u.getPassword().isEmpty() || u.getFullName().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Tên đăng nhập, Mật khẩu và Họ tên!");
            return;
        }

        if (userDAO.isUsernameExists(u.getUsername())) {
            JOptionPane.showMessageDialog(view, "Tên đăng nhập này đã tồn tại trên hệ thống!");
            return;
        }

        if (userDAO.insert(u)) {
            JOptionPane.showMessageDialog(view, "Thêm nhân viên mới thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm nhân viên thất bại.");
        }
    }

    private void editUser() {
        User selected = view.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn nhân viên cần sửa từ danh sách!");
            return;
        }

        User u = view.getUserFromForm();
        u.setId(selected.getId());

        if (u.getFullName().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Họ tên không được để trống!");
            return;
        }

        if (userDAO.update(u)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thông tin nhân viên thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thông tin thất bại.");
        }
    }

    private void deactivateUser() {
        User selected = view.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn nhân viên cần vô hiệu hóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
                "Vô hiệu hóa hoạt động của tài khoản này? Nhân viên này sẽ không thể đăng nhập.", 
                "Xác nhận vô hiệu hóa", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.delete(selected.getId())) {
                JOptionPane.showMessageDialog(view, "Đã vô hiệu hóa tài khoản nhân viên!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Vô hiệu hóa thất bại.");
            }
        }
    }

    private void resetPassword() {
        User selected = view.getSelectedUser();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhân viên cần đặt lại mật khẩu!");
            return;
        }

        String newPassword = JOptionPane.showInputDialog(view, 
                "Nhập mật khẩu mới cho nhân viên '" + selected.getUsername() + "':", 
                "Đặt lại mật khẩu", 
                JOptionPane.PLAIN_MESSAGE);
        
        if (newPassword == null) return;
        newPassword = newPassword.trim();
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Mật khẩu không được để trống!");
            return;
        }

        if (userDAO.updatePassword(selected.getId(), newPassword)) {
            JOptionPane.showMessageDialog(view, "Đặt lại mật khẩu nhân viên thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Lỗi cập nhật mật khẩu.");
        }
    }
}
