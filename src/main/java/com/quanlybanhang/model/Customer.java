package com.quanlybanhang.model;

/**
 * Model đại diện cho khách hàng trong hệ thống.
 * Chứa thông tin liên hệ và điểm tích lũy thành viên.
 */
public class Customer {
    private int id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;

    public Customer() {}

    public Customer(int id, String code, String name, String phone, String email, String address, int loyaltyPoints) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
