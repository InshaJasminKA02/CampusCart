package com.campuscart.app.repositories;

/**
 * Cloud Firestore Database Configuration & Schema Reference for CampusCart.
 *
 * COLLECTIONS:
 * - "products": Contains all marketplace product listings.
 * - "users": Contains user profile and registration records.
 * - "favourites": Contains per-user saved product references.
 * - "conversations": Contains buyer-seller chat threads.
 *
 * PRODUCT DOCUMENT SCHEMA:
 * {
 *   "productId": String,          // Unique document ID
 *   "title": String,              // Listing title (4-80 chars)
 *   "description": String,        // Item description
 *   "price": Double,              // Price in INR (₹)
 *   "category": String,           // One of 8 valid campus categories
 *   "condition": String,          // Brand New, Like New, Good, Fair
 *   "imageUrls": List<String>,    // HTTPS URLs stored on Cloudinary
 *   "sellerId": String,           // Firebase Authentication UID
 *   "sellerName": String,         // Display name of student seller
 *   "createdAt": Long / Timestamp,// Server creation timestamp
 *   "updatedAt": Long / Timestamp,// Server modification timestamp
 *   "isSold": Boolean             // true if purchased/sold, false if active
 * }
 */
public final class FirestoreConfig {
    private FirestoreConfig() {}

    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_FAVOURITES = "favourites";
    public static final String COLLECTION_CONVERSATIONS = "conversations";
    public static final String COLLECTION_MESSAGES = "messages";

    // Fields
    public static final String FIELD_PRODUCT_ID = "productId";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_CONDITION = "condition";
    public static final String FIELD_IMAGE_URLS = "imageUrls";
    public static final String FIELD_SELLER_ID = "sellerId";
    public static final String FIELD_SELLER_NAME = "sellerName";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_UPDATED_AT = "updatedAt";
    public static final String FIELD_IS_SOLD = "isSold";
}
