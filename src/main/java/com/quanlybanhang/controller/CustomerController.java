package com.quanlybanhang.controller;

import com.quanlybanhang.dao.CustomerDAO;
import com.quanlybanhang.model.Customer;
import com.quanlybanhang.view.CustomerPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Controller điều khiển quản lý Khách hàng.
 * Kết nối dữ liệu từ CustomerDAO sang CustomerPanel.
 */
public class CustomerController {
    private CustomerPanel view;
    private CustomerDAO customerDAO;

    public CustomerController(CustomerPanel view, CustomerDAO customerDAO) {
        this.view = view;
        this.customerDAO = customerDAO;

        initListeners();
    }

    private void initListeners() {
        view.addSearchListener(e -> searchCustomer());
        view.addAddListener(e -> addCustomer());
        view.addEditListener(e -> editCustomer());
        view.addDeleteListener(e -> deleteCustomer());
        view.addClearListener(e -> view.clearForm());

        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Customer selected = view.getSelectedCustomer();
                if (selected != null) {
                    view.showCustomerOnForm(selected);
                }
            }
        });
    }

    public void loadData() {
        List<Customer> list = customerDAO.getAll();
        view.setCustomerTableData(list);
        view.clearForm();
    }

    private void searchCustomer() {
        String query = view.getSearchQuery();
        if (query.isEmpty()) {
            loadData();
            return;
        }

        Customer byPhone = customerDAO.getByPhone(query);
        if (byPhone != null) {
            view.setCustomerTableData(List.of(byPhone));
            view.showCustomerOnForm(byPhone);
        } else {
            List<Customer> all = customerDAO.getAll();
            List<Customer> filtered = all.stream()
                    .filter(c -> c.getName().toLowerCase().contains(query.toLowerCase()) || 
                                 (c.getPhone() != null && c.getPhone().contains(query)))
                    .toList();
            view.setCustomerTableData(filtered);
        }
    }

    private void addCustomer() {
        Customer c = view.getCustomerFromForm();
        if (c.getName().isEmpty() || c.getPhone().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng điền Tên và Số điện thoại khách hàng!");
            return;
        }

        if (customerDAO.isPhoneExists(c.getPhone())) {
            JOptionPane.showMessageDialog(view, "Số điện thoại này đã được đăng ký bởi khách hàng khác!");
            return;
        }

        if (c.getCode().isEmpty()) {
            c.setCode("KH" + System.currentTimeMillis() / 100000);
        }

        if (customerDAO.insert(c)) {
            JOptionPane.showMessageDialog(view, "Thêm khách hàng mới thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm khách hàng thất bại.");
        }
    }

    private void editCustomer() {
        Customer selected = view.getSelectedCustomer();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn khách hàng cần sửa từ danh sách!");
            return;
        }

        if (selected.getId() == 1) {
            JOptionPane.showMessageDialog(view, "Không được quyền chỉnh sửa tài khoản Khách vãng lai mặc định!");
            return;
        }

        Customer c = view.getCustomerFromForm();
        c.setId(selected.getId());

        if (c.getName().isEmpty() || c.getPhone().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Họ tên và Số điện thoại không được để trống!");
            return;
        }

        if (!c.getPhone().equals(selected.getPhone()) && customerDAO.isPhoneExists(c.getPhone())) {
            JOptionPane.showMessageDialog(view, "Số điện thoại mới đã tồn tại trên hệ thống!");
            return;
        }

        if (customerDAO.update(c)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thông tin khách hàng thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại.");
        }
    }

    private void deleteCustomer() {
        Customer selected = view.getSelectedCustomer();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn khách hàng cần xóa từ danh sách!");
            return;
        }

        if (selected.getId() == 1) {
            JOptionPane.showMessageDialog(view, "Không thể xóa tài khoản Khách vãng lai mặc định!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc muốn xóa vĩnh viễn khách hàng này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.delete(selected.getId())) {
                JOptionPane.showMessageDialog(view, "Đã xóa thông tin khách hàng!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại (Khách hàng đã có giao dịch lịch sử).");
            }
        }
    }
}
