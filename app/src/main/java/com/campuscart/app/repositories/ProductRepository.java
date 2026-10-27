package com.campuscart.app.repositories;

import com.campuscart.app.models.Product;

import java.util.List;

/**
 * Modular repository interface for marketplace product operations.
 * Decouples UI activities from the underlying persistence layer, allowing
 * seamless transition from in-memory session data to Cloud Firestore.
 */
public interface ProductRepository {

    interface Callback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }

    void createProduct(Product product, Callback<Product> callback);

    void getAvailableProducts(Callback<List<Product>> callback);

    void getProductsBySeller(String sellerId, Callback<List<Product>> callback);

    void getProductById(String productId, Callback<Product> callback);

    void updateProduct(Product product, Callback<Void> callback);

    void deleteProduct(String productId, Callback<Void> callback);

    void markAsSold(String productId, Callback<Void> callback);

    void markAsAvailable(String productId, Callback<Void> callback);
}
