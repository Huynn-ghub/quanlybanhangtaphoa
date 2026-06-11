package com.quanlybanhang.controller;

import com.quanlybanhang.dao.ImportReceiptDAO;
import com.quanlybanhang.dao.ProductDAO;
import com.quanlybanhang.dao.SupplierDAO;
import com.quanlybanhang.model.ImportReceipt;
import com.quanlybanhang.model.ImportReceiptDetail;
import com.quanlybanhang.model.Product;
import com.quanlybanhang.model.Supplier;
import com.quanlybanhang.model.User;
import com.quanlybanhang.view.ImportPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller điều khiển nghiệp vụ Nhập kho sản phẩm.
 * Kết nối dữ liệu từ ProductDAO, SupplierDAO, ImportReceiptDAO sang ImportPanel.
 */
public class ImportController {
    private ImportPanel view;
    private ProductDAO productDAO;
    private SupplierDAO supplierDAO;
    private ImportReceiptDAO importReceiptDAO;
    private User currentUser;
    private double currentTotal = 0;

    public ImportController(ImportPanel view, ProductDAO productDAO, SupplierDAO supplierDAO, ImportReceiptDAO importReceiptDAO, User currentUser) {
        this.view = view;
        this.productDAO = productDAO;
        this.supplierDAO = supplierDAO;
        this.importReceiptDAO = importReceiptDAO;
        this.currentUser = currentUser;

        initListeners();
    }

    private void initListeners() {
        view.addProductListMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Product selected = view.getSelectedProduct();
                if (selected != null) {
                    view.setImportPriceInput(selected.getPurchasePrice());
                }
            }
        });

        view.addSearchProductEnterListener(e -> {
            String q = view.getSearchProductQuery();
            if (q.isEmpty()) {
                view.setProductListData(productDAO.getActiveProducts());
            } else {
                view.setProductListData(productDAO.search(q));
            }
        });

        view.addAddProductListener(e -> {
            Product selected = view.getSelectedProduct();
            if (selected == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần nhập kho từ bảng sản phẩm hệ thống!");
                return;
            }

            double price = view.getImportPriceInput();
            if (price <= 0) {
                JOptionPane.showMessageDialog(view, "Giá nhập hàng phải lớn hơn 0!");
                return;
            }

            int qty = view.getQuantityInput();
            if (qty <= 0) {
                JOptionPane.showMessageDialog(view, "Số lượng nhập hàng phải lớn hơn 0!");
                return;
            }

            view.addProductToImportCart(selected, price, qty);
            calculateTotals();
        });

        view.addRemoveProductListener(e -> {
            view.removeSelectedCartItem();
            calculateTotals();
        });

        view.addSaveImportListener(e -> saveReceipt());

        view.addCancelImportListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, "Hủy phiếu nhập kho hiện tại?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.resetImport();
                calculateTotals();
            }
        });
    }

    public void refreshData() {
        view.setSuppliers(supplierDAO.getAll());
        view.setProductListData(productDAO.getActiveProducts());
        view.generateReceiptCode();
        calculateTotals();
    }

    private void calculateTotals() {
        currentTotal = 0;
        DefaultTableModel model = view.getCartTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            currentTotal += (double) model.getValueAt(i, 4);
        }
        view.setTotalAmount(currentTotal);
    }

    private void saveReceipt() {
        Supplier supplier = view.getSelectedSupplier();
        if (supplier == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhà cung cấp!");
            return;
        }

        if (view.getCartTableModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Phiếu nhập đang trống! Vui lòng thêm sản phẩm.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận nhập kho danh sách hàng hóa này?", "Xác nhận nhập", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        ImportReceipt receipt = new ImportReceipt();
        receipt.setCode(view.getReceiptCode());
        receipt.setUserId(currentUser.getId());
        receipt.setSupplierId(supplier.getId());
        receipt.setImportDate(new Timestamp(System.currentTimeMillis()));
        receipt.setTotalAmount(currentTotal);

        List<ImportReceiptDetail> details = new ArrayList<>();
        DefaultTableModel model = view.getCartTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            ImportReceiptDetail ird = new ImportReceiptDetail();
            ird.setProductId((int) model.getValueAt(i, 5));
            ird.setPrice((double) model.getValueAt(i, 2));
            ird.setQuantity((int) model.getValueAt(i, 3));
            ird.setTotalPrice((double) model.getValueAt(i, 4));
            details.add(ird);
        }

        boolean success = importReceiptDAO.createReceipt(receipt, details);
        if (success) {
            JOptionPane.showMessageDialog(view, "Nhập kho hoàn tất thành công! Đã tự động cập nhật số lượng tồn kho sản phẩm.");
            view.resetImport();
            refreshData();
        } else {
            JOptionPane.showMessageDialog(view, "Lỗi xảy ra trong quá trình lưu phiếu nhập kho.");
        }
    }
}
