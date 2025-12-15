package com.medishelf.app.models;

import java.util.List;

public class Order {
    private String orderId;
    private String customerEmail;
    private String customerName;
    private String phoneNumber;
    private String deliveryAddress;
    private String city;
    private List<OrderItem> items;
    private double totalAmount;
    private String paymentStatus;
    private long timestamp;

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String orderId, String customerEmail, String customerName, String phoneNumber,
                 String deliveryAddress, String city, List<OrderItem> items, double totalAmount,
                 String paymentStatus, long timestamp) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.city = city;
        this.items = items;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}