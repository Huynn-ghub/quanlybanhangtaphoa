package com.quanlybanhang.controller;

import com.quanlybanhang.dao.CustomerDAO;
import com.quanlybanhang.dao.OrderDAO;
import com.quanlybanhang.dao.ProductDAO;
import com.quanlybanhang.model.Customer;
import com.quanlybanhang.model.Order;
import com.quanlybanhang.model.OrderDetail;
import com.quanlybanhang.model.Product;
import com.quanlybanhang.model.User;
import com.quanlybanhang.view.SalesPanel;
import com.quanlybanhang.util.InvoicePrinter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller điều khiển màn hình bán hàng (POS).
 * Xử lý thêm sản phẩm vào giỏ, kiểm tra tồn kho, áp dụng giảm giá, lưu hóa đơn và xuất hóa đơn PDF.
 */
public class SalesController {
    private SalesPanel view;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private User currentUser;

    private Customer selectedCustomer;
    private double currentTotal = 0;

    public SalesController(SalesPanel view, ProductDAO productDAO, CustomerDAO customerDAO, OrderDAO orderDAO,
            User currentUser) {
        this.view = view;
        this.productDAO = productDAO;
        this.customerDAO = customerDAO;
        this.orderDAO = orderDAO;
        this.currentUser = currentUser;

        initListeners();
    }

    private void initListeners() {
        view.addSearchProductEnterListener(e -> handleProductSearch());
        view.addSearchProductKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String q = view.getProductSearchQuery();
                if (q.length() >= 2) {
                    view.setProductListData(productDAO.search(q));
                } else if (q.isEmpty()) {
                    view.setProductListData(productDAO.getActiveProducts());
                }
            }
        });

        view.addProductListMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Product selected = view.getSelectedProductListProduct();
                    if (selected != null) {
                        addProductToCart(selected);
                    }
                }
            }
        });

        view.addCartTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = view.getSelectedCartRow();
                if (row != -1) {
                    // TODO: có thể dùng row để chỉnh sửa số lượng trong giỏ hàng
                }
            }
        });

        view.addRemoveCartItemListener(e -> {
            view.removeSelectedCartItem();
            calculateTotals();
        });

        view.addClearCartListener(e -> {
            view.clearCart();
            calculateTotals();
        });

        view.addSearchCustomerListener(e -> handleCustomerSearch());
        view.addSearchCustomerEnterListener(e -> handleCustomerSearch());

        view.addAddQuickCustomerListener(e -> addQuickCustomer());

        view.addDiscountKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });

        view.addPayListener(e -> processPayment());

        view.addCancelInvoiceListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, "Bạn chắc chắn muốn hủy giỏ hàng và hóa đơn hiện tại?",
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.resetPOS();
                selectedCustomer = null;
            }
        });
    }

    public void refreshData() {
        view.setProductListData(productDAO.getActiveProducts());
        view.generateInvoiceCode();
    }

    private void handleProductSearch() {
        String q = view.getProductSearchQuery();
        if (q.isEmpty())
            return;

        Product p = productDAO.getByCode(q);
        if (p != null) {
            addProductToCart(p);
            view.clearProductSearchField();
        } else {
            List<Product> matches = productDAO.search(q);
            view.setProductListData(matches);
            if (matches.size() == 1) {
                addProductToCart(matches.get(0));
                view.clearProductSearchField();
            }
        }
    }

    private void addProductToCart(Product p) {
        if (p.getStockQuantity() <= 0) {
            JOptionPane.showMessageDialog(view, "Sản phẩm này đã hết hàng trong kho!");
            return;
        }

        int qtyInCart = 0;
        DefaultTableModel model = view.getCartTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(p.getCode())) {
                qtyInCart = (int) model.getValueAt(i, 4);
                break;
            }
        }

        if (qtyInCart + 1 > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(view,
                    "Số lượng bán vượt quá tồn kho hiện có (" + p.getStockQuantity() + " chiếc)!");
            return;
        }

        view.addProductToCartTable(p, 1);
        calculateTotals();
    }

    private void handleCustomerSearch() {
        String phone = view.getCustomerPhone();
        if (phone.isEmpty()) {
            selectedCustomer = null;
            view.setCustomerInfo(null);
            calculateTotals();
            return;
        }

        Customer c = customerDAO.getByPhone(phone);
        if (c != null) {
            selectedCustomer = c;
            view.setCustomerInfo(c);
            calculateTotals();
        } else {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Không tìm thấy số điện thoại: " + phone + ". Bạn có muốn thêm nhanh khách hàng này?",
                    "Thêm khách hàng", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                addQuickCustomer();
            }
        }
    }

    private void addQuickCustomer() {
        String phone = view.getCustomerPhone();
        String name = JOptionPane.showInputDialog(view, "Nhập họ và tên khách hàng:", "Đăng ký nhanh khách hàng",
                JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.trim().isEmpty())
            return;

        Customer c = new Customer();
        c.setCode("KH" + System.currentTimeMillis() / 100000);
        c.setName(name.trim());
        c.setPhone(phone.trim().isEmpty() ? "0" + System.currentTimeMillis() / 100000 : phone.trim());
        c.setLoyaltyPoints(0);

        if (customerDAO.insert(c)) {
            Customer created = customerDAO.getByPhone(c.getPhone());
            if (created != null) {
                selectedCustomer = created;
                view.setCustomerInfo(created);
                JOptionPane.showMessageDialog(view, "Đăng ký thành công!");
            }
        } else {
            JOptionPane.showMessageDialog(view, "Đăng ký nhanh thất bại.");
        }
    }

    private void calculateTotals() {
        currentTotal = 0;
        DefaultTableModel model = view.getCartTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            currentTotal += (double) model.getValueAt(i, 5);
        }

        double discount = view.getDiscountValue();
        if (discount > currentTotal) {
            discount = currentTotal;
            view.setDiscountValue(discount);
        }

        double finalAmount = currentTotal - discount;

        view.setTotalAmount(currentTotal);
        view.setFinalAmount(finalAmount);
    }

    private void processPayment() {
        if (view.getCartRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng hiện tại đang trống!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận thanh toán hóa đơn này?", "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        Order order = new Order();
        order.setCode(view.getInvoiceCode());
        order.setUserId(currentUser.getId());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(currentTotal);

        double discount = view.getDiscountValue();
        order.setDiscount(discount);
        order.setFinalAmount(currentTotal - discount);
        order.setPaymentMethod(view.getPaymentMethod());
        order.setNotes(view.getNotes());

        order.setCustomerId(selectedCustomer != null ? selectedCustomer.getId() : 1);

        List<OrderDetail> details = new ArrayList<>();
        DefaultTableModel model = view.getCartTableModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            OrderDetail od = new OrderDetail();
            od.setProductId((int) model.getValueAt(i, 6));
            od.setProductName((String) model.getValueAt(i, 2));
            od.setQuantity((int) model.getValueAt(i, 4));
            od.setPrice((double) model.getValueAt(i, 3));
            od.setTotalPrice((double) model.getValueAt(i, 5));
            details.add(od);
        }

        boolean success = orderDAO.createOrder(order, details);
        if (success) {
            JOptionPane.showMessageDialog(view, "Thanh toán thành công!");

            File file = new File("HoaDon_" + order.getCode() + ".pdf");
            String custName = selectedCustomer != null ? selectedCustomer.getName() : "Khách vãng lai";
            boolean printSuccess = InvoicePrinter.printInvoice(order, details, custName, currentUser.getFullName(),
                    file);
            if (printSuccess) {
                JOptionPane.showMessageDialog(view, "Đã xuất hóa đơn PDF thành công tại:\n" + file.getAbsolutePath());
            }

            view.resetPOS();
            selectedCustomer = null;
            refreshData();
        } else {
            JOptionPane.showMessageDialog(view,
                    "Thanh toán thất bại! Không đủ số lượng tồn kho sản phẩm hoặc lỗi cơ sở dữ liệu.");
        }
    }
}
