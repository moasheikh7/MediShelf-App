package com.medishelf.app.models;

public class Product {
    private String id;
    private String name;
    private String author;
    private String description;
    // This Object type stops the Firebase crash
    private Object price;
    private String image;

    public Product() { }

    public Product(String id, String name, String author, String description, Object price, String image) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Object getPrice() { return price; }
    public void setPrice(Object price) { this.price = price; }

    // --- DECISIVE FIX: The Safety Helper ---
    public double getPriceAsDouble() {
        if (price == null) return 0.0;
        try {
            if (price instanceof Number) return ((Number) price).doubleValue();
            if (price instanceof String) return Double.parseDouble((String) price);
        } catch (Exception e) {
            return 0.0;
        }
        return 0.0;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}