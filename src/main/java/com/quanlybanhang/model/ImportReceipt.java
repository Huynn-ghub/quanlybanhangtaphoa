package com.quanlybanhang.model;

import java.sql.Timestamp;

/**
 * Model đại diện cho phiếu nhập kho.
 */
public class ImportReceipt {
    private int id;
    private String code;
    private int userId;
    private String userName; // Bổ trợ hiển thị
    private int supplierId;
    private String supplierName; // Bổ trợ hiển thị
    private Timestamp importDate;
    private double totalAmount;

    public ImportReceipt() {}

    public ImportReceipt(int id, String code, int userId, int supplierId, Timestamp importDate, double totalAmount) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.supplierId = supplierId;
        this.importDate = importDate;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Timestamp getImportDate() {
        return importDate;
    }

    public void setImportDate(Timestamp importDate) {
        this.importDate = importDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
