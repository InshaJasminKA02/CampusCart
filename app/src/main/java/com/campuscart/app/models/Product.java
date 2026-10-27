package com.campuscart.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity representing an item listed for sale on CampusCart.
 * Serialized and deserialized to/from Cloud Firestore.
 */
public class Product implements Serializable {
    private String productId;
    private String title;
    private String description;
    private double price;
    private String category;
    private String condition;
    private List<String> imageUrls;
    private String sellerId;
    private String sellerName;
    private long createdAt;
    private long updatedAt;
    private boolean isSold;
    private boolean isFavourite;

    // Required empty constructor for Firestore deserialization
    public Product() {
        this.imageUrls = new ArrayList<>();
    }

    public Product(String productId, String title, String description, double price,
                   String category, String condition, List<String> imageUrls,
                   String sellerId, String sellerName, long createdAt, boolean isSold) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.isSold = isSold;
        this.isFavourite = false;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getPrimaryImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return null;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
