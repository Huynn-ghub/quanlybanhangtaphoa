package com.quanlybanhang.model;

/**
 * Model đại diện cho chi tiết hóa đơn bán hàng (từng sản phẩm trong hóa đơn).
 */
public class OrderDetail {
    private int id;
    private int orderId;
    private int productId;
    private String productName; // Bổ trợ hiển thị
    private String productCode; // Bổ trợ hiển thị
    private int quantity;
    private double price;
    private double totalPrice;

    public OrderDetail() {}

    public OrderDetail(int id, int orderId, int productId, int quantity, double price, double totalPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
