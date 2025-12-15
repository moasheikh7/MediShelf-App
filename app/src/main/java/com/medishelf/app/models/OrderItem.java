package com.medishelf.app.models;

public class OrderItem {
    private String productId;
    private String productName;
    private Object productPrice; // Safety Fix
    private int quantity;

    public OrderItem() { }

    public OrderItem(String productId, String productName, Object productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Object getProductPrice() { return productPrice; }
    public void setProductPrice(Object productPrice) { this.productPrice = productPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAsDouble() {
        if (productPrice == null) return 0.0;
        try {
            if (productPrice instanceof Number) return ((Number) productPrice).doubleValue();
            if (productPrice instanceof String) return Double.parseDouble((String) productPrice);
        } catch (Exception e) { return 0.0; }
        return 0.0;
    }

    public double getTotalPrice() {
        return getPriceAsDouble() * quantity;
    }
}