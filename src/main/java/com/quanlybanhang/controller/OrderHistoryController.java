package com.quanlybanhang.controller;

import com.quanlybanhang.dao.OrderDAO;
import com.quanlybanhang.model.Order;
import com.quanlybanhang.model.OrderDetail;
import com.quanlybanhang.util.InvoicePrinter;
import com.quanlybanhang.view.OrderHistoryPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

/**
 * Controller điều khiển màn hình Lịch sử / Tra cứu Hóa đơn.
 * Mọi thao tác DB đều chạy trong SwingWorker để không block EDT.
 */
public class OrderHistoryController {
    private final OrderHistoryPanel view;
    private final OrderDAO orderDAO;

    /** Lưu danh sách HĐ hiện tại để tra cứu nhanh khi click */
    private List<Order> currentOrders;

    public OrderHistoryController(OrderHistoryPanel view, OrderDAO orderDAO) {
        this.view = view;
        this.orderDAO = orderDAO;
        initListeners();
    }

    private void initListeners() {
        view.addSearchListener(e -> handleSearch());
        view.addResetListener(e -> handleReset());
        view.addPrintPDFListener(e -> handlePrintPDF());

        view.addOrderTableListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getSelectedOrderRow();
                if (row >= 0 && currentOrders != null && row < currentOrders.size()) {
                    loadDetails(currentOrders.get(row));
                }
            }
        });
    }

    /** Tải toàn bộ hóa đơn (không filter) */
    public void loadData() {
        new SwingWorker<List<Order>, Void>() {
            @Override
            protected List<Order> doInBackground() {
                return orderDAO.getAll();
            }
            @Override
            protected void done() {
                try {
                    currentOrders = get();
                    view.setOrdersData(currentOrders);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu hóa đơn!");
                }
            }
        }.execute();
    }

    /** Tìm kiếm theo keyword + khoảng ngày */
    private void handleSearch() {
        String keyword = view.getSearchKeyword();
        Timestamp from = view.getFromTimestamp();
        Timestamp to   = view.getToTimestamp();

        new SwingWorker<List<Order>, Void>() {
            @Override
            protected List<Order> doInBackground() {
                return orderDAO.searchOrders(keyword, from, to);
            }
            @Override
            protected void done() {
                try {
                    currentOrders = get();
                    view.setOrdersData(currentOrders);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi tìm kiếm hóa đơn!");
                }
            }
        }.execute();
    }

    /** Xóa bộ lọc, về trạng thái ban đầu */
    private void handleReset() {
        view.resetFilters();
        loadData();
    }

    /** Tải chi tiết sản phẩm của hóa đơn được chọn */
    private void loadDetails(Order order) {
        new SwingWorker<List<OrderDetail>, Void>() {
            @Override
            protected List<OrderDetail> doInBackground() {
                return orderDAO.getDetails(order.getId());
            }
            @Override
            protected void done() {
                try {
                    view.setDetailsData(get(), order);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi tải chi tiết hóa đơn!");
                }
            }
        }.execute();
    }

    /** In lại hóa đơn PDF cho hóa đơn đang chọn */
    private void handlePrintPDF() {
        int row = view.getSelectedOrderRow();
        if (row < 0 || currentOrders == null || row >= currentOrders.size()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn hóa đơn cần in lại!");
            return;
        }

        Order order = currentOrders.get(row);

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("HoaDon_" + order.getCode() + ".pdf"));
        if (chooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        new SwingWorker<List<OrderDetail>, Void>() {
            @Override
            protected List<OrderDetail> doInBackground() {
                return orderDAO.getDetails(order.getId());
            }
            @Override
            protected void done() {
                try {
                    List<OrderDetail> details = get();
                    String custName = order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai";
                    String cashier  = order.getUserName() != null ? order.getUserName() : "";
                    boolean ok = InvoicePrinter.printInvoice(order, details, custName, cashier, file);
                    if (ok) {
                        JOptionPane.showMessageDialog(view, "Xuất PDF thành công:\n" + file.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(view, "Xuất PDF thất bại!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Lỗi in hóa đơn: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
