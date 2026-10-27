package com.campuscart.app.repositories;

import android.os.Handler;
import android.os.Looper;

import com.campuscart.app.models.Product;
import com.campuscart.app.utilities.AuthHelper;
import com.campuscart.app.utilities.Constants;
import com.campuscart.app.utilities.SampleDataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * In-memory repository implementation used for development and offline testing.
 * Provides full listing creation, editing, status toggling, and seller queries.
 */
public class InMemoryProductRepository implements ProductRepository {

    private static InMemoryProductRepository instance;
    private final List<Product> products = Collections.synchronizedList(new ArrayList<>());
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private InMemoryProductRepository() {
        products.addAll(SampleDataProvider.getSampleProducts());

        // Preload sample user listings associated with the development fallback UID
        long now = System.currentTimeMillis();
        long hour = 3600 * 1000L;

        products.add(0, new Product(
                "user_prod_1",
                "Data Structures and Algorithm Analysis in Java (Weiss)",
                "Standard computer science course textbook. Hardcover in excellent condition with no highlighting.",
                420.0,
                Constants.CATEGORY_BOOKS,
                Constants.CONDITION_LIKE_NEW,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1532012164546-f432f2e3edd4?w=600&auto=format&fit=crop&q=80")),
                AuthHelper.DEV_FALLBACK_UID,
                AuthHelper.DEV_FALLBACK_NAME,
                now - (3 * hour),
                false
        ));

        products.add(1, new Product(
                "user_prod_2",
                "USB-C Multiport Adapter Hub (4K HDMI, 3x USB 3.0)",
                "Anker compact aluminum hub. All ports tested and functioning. Great for MacBook and Dell laptops.",
                650.0,
                Constants.CATEGORY_ELECTRONICS,
                Constants.CONDITION_GOOD,
                new ArrayList<>(Collections.singletonList("https://images.unsplash.com/photo-1616440347437-b1c73416efc2?w=600&auto=format&fit=crop&q=80")),
                AuthHelper.DEV_FALLBACK_UID,
                AuthHelper.DEV_FALLBACK_NAME,
                now - (72 * hour),
                true // Sold item for testing sold tab
        ));
    }

    public static synchronized InMemoryProductRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryProductRepository();
        }
        return instance;
    }

    @Override
    public void createProduct(Product product, Callback<Product> callback) {
        if (product == null) {
            if (callback != null) callback.onError("Product cannot be null");
            return;
        }

        if (product.getProductId() == null || product.getProductId().trim().isEmpty()) {
            product.setProductId("prod_" + UUID.randomUUID().toString().substring(0, 8));
        }

        long now = System.currentTimeMillis();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // Prepend to top of listings
        products.add(0, product);

        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(product));
        }
    }

    @Override
    public void getAvailableProducts(Callback<List<Product>> callback) {
        List<Product> available = new ArrayList<>();
        synchronized (products) {
            for (Product p : products) {
                if (!p.isSold()) {
                    available.add(p);
                }
            }
        }
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(available));
        }
    }

    @Override
    public void getProductsBySeller(String sellerId, Callback<List<Product>> callback) {
        if (sellerId == null) {
            if (callback != null) callback.onError("Invalid seller ID");
            return;
        }
        List<Product> sellerProducts = new ArrayList<>();
        synchronized (products) {
            for (Product p : products) {
                if (sellerId.equalsIgnoreCase(p.getSellerId())) {
                    sellerProducts.add(p);
                }
            }
        }
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(sellerProducts));
        }
    }

    @Override
    public void getProductById(String productId, Callback<Product> callback) {
        if (productId == null) {
            if (callback != null) callback.onError("Invalid Product ID");
            return;
        }
        synchronized (products) {
            for (Product p : products) {
                if (productId.equalsIgnoreCase(p.getProductId())) {
                    if (callback != null) {
                        mainHandler.post(() -> callback.onSuccess(p));
                    }
                    return;
                }
            }
        }
        if (callback != null) {
            mainHandler.post(() -> callback.onError("Product not found"));
        }
    }

    @Override
    public void updateProduct(Product updated, Callback<Void> callback) {
        if (updated == null || updated.getProductId() == null) {
            if (callback != null) callback.onError("Invalid product for update");
            return;
        }
        synchronized (products) {
            for (int i = 0; i < products.size(); i++) {
                if (updated.getProductId().equalsIgnoreCase(products.get(i).getProductId())) {
                    updated.setUpdatedAt(System.currentTimeMillis());
                    products.set(i, updated);
                    if (callback != null) {
                        mainHandler.post(() -> callback.onSuccess(null));
                    }
                    return;
                }
            }
        }
        if (callback != null) {
            mainHandler.post(() -> callback.onError("Product not found to update"));
        }
    }

    @Override
    public void deleteProduct(String productId, Callback<Void> callback) {
        if (productId == null) {
            if (callback != null) callback.onError("Invalid Product ID");
            return;
        }
        boolean removed = false;
        synchronized (products) {
            for (int i = 0; i < products.size(); i++) {
                if (productId.equalsIgnoreCase(products.get(i).getProductId())) {
                    products.remove(i);
                    removed = true;
                    break;
                }
            }
        }
        final boolean success = removed;
        if (callback != null) {
            mainHandler.post(() -> {
                if (success) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Product not found to delete");
                }
            });
        }
    }

    @Override
    public void markAsSold(String productId, Callback<Void> callback) {
        setSoldStatus(productId, true, callback);
    }

    @Override
    public void markAsAvailable(String productId, Callback<Void> callback) {
        setSoldStatus(productId, false, callback);
    }

    private void setSoldStatus(String productId, boolean isSold, Callback<Void> callback) {
        if (productId == null) {
            if (callback != null) callback.onError("Invalid Product ID");
            return;
        }
        boolean updated = false;
        synchronized (products) {
            for (Product p : products) {
                if (productId.equalsIgnoreCase(p.getProductId())) {
                    p.setSold(isSold);
                    p.setUpdatedAt(System.currentTimeMillis());
                    updated = true;
                    break;
                }
            }
        }
        final boolean success = updated;
        if (callback != null) {
            mainHandler.post(() -> {
                if (success) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Product not found");
                }
            });
        }
    }

    public List<Product> getAllProductsSnapshot() {
        return new ArrayList<>(products);
    }
}
