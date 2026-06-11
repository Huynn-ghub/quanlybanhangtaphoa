package com.quanlybanhang.model;

/**
 * Model đại diện cho chi tiết phiếu nhập kho (từng sản phẩm trong phiếu nhập).
 */
public class ImportReceiptDetail {
    private int id;
    private int receiptId;
    private int productId;
    private String productName; // Bổ trợ hiển thị
    private String productCode; // Bổ trợ hiển thị
    private int quantity;
    private double price;
    private double totalPrice;

    public ImportReceiptDetail() {}

    public ImportReceiptDetail(int id, int receiptId, int productId, int quantity, double price, double totalPrice) {
        this.id = id;
        this.receiptId = receiptId;
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

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
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
