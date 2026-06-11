package com.quanlybanhang.controller;

import com.quanlybanhang.dao.SupplierDAO;
import com.quanlybanhang.model.Supplier;
import com.quanlybanhang.view.SupplierPanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Controller điều khiển quản lý Nhà cung cấp.
 * Kết nối dữ liệu từ SupplierDAO sang SupplierPanel.
 */
public class SupplierController {
    private SupplierPanel view;
    private SupplierDAO supplierDAO;

    public SupplierController(SupplierPanel view, SupplierDAO supplierDAO) {
        this.view = view;
        this.supplierDAO = supplierDAO;

        initListeners();
    }

    private void initListeners() {
        view.addSearchListener(e -> searchSupplier());
        view.addAddListener(e -> addSupplier());
        view.addEditListener(e -> editSupplier());
        view.addDeleteListener(e -> deleteSupplier());
        view.addClearListener(e -> view.clearForm());

        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Supplier selected = view.getSelectedSupplier();
                if (selected != null) {
                    view.showSupplierOnForm(selected);
                }
            }
        });
    }

    public void loadData() {
        List<Supplier> list = supplierDAO.getAll();
        view.setSupplierTableData(list);
        view.clearForm();
    }

    private void searchSupplier() {
        String query = view.getSearchQuery();
        if (query.isEmpty()) {
            loadData();
            return;
        }

        List<Supplier> all = supplierDAO.getAll();
        List<Supplier> filtered = all.stream()
                .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase()) || 
                             (s.getPhone() != null && s.getPhone().contains(query)))
                .toList();
        view.setSupplierTableData(filtered);
    }

    private void addSupplier() {
        Supplier s = view.getSupplierFromForm();
        if (s.getName().isEmpty() || s.getCode().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Mã nhà cung cấp và Tên nhà cung cấp!");
            return;
        }

        if (s.getCode().isEmpty()) {
            s.setCode("NCC" + System.currentTimeMillis() / 100000);
        }

        if (supplierDAO.insert(s)) {
            JOptionPane.showMessageDialog(view, "Thêm nhà cung cấp thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm thất bại.");
        }
    }

    private void editSupplier() {
        Supplier selected = view.getSelectedSupplier();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn nhà cung cấp cần sửa đổi!");
            return;
        }

        Supplier s = view.getSupplierFromForm();
        s.setId(selected.getId());

        if (s.getName().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Tên nhà cung cấp không được rỗng!");
            return;
        }

        if (supplierDAO.update(s)) {
            JOptionPane.showMessageDialog(view, "Cập nhật thông tin nhà cung cấp thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật thất bại.");
        }
    }

    private void deleteSupplier() {
        Supplier selected = view.getSelectedSupplier();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Chọn nhà cung cấp cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn muốn xóa vĩnh viễn nhà cung cấp này?", 
                "Xác nhận xóa nhà cung cấp", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (supplierDAO.delete(selected.getId())) {
                JOptionPane.showMessageDialog(view, "Đã xóa nhà cung cấp!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Xóa thất bại (Nhà cung cấp đã có lịch sử nhập kho).");
            }
        }
    }
}
