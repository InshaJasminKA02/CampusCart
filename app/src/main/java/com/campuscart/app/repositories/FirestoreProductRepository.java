package com.campuscart.app.repositories;

import com.campuscart.app.models.Product;

import java.util.List;

/**
 * FirestoreProductRepository — Cloud Firestore Backend for CampusCart
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * STATUS: COMPILE-SAFE PLACEHOLDER — NOT YET OPERATIONAL
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This class is structured in two parts:
 *
 *   PART A (active, below): A compile-safe stub that implements the
 *   ProductRepository interface and reports a clear error message on every
 *   call. This stub allows the project to build and run without Firebase
 *   being on the classpath.
 *
 *   PART B (in FirestoreProductRepository_REAL.java.txt, same package folder):
 *   The real Firestore implementation with full SDK calls — create, read,
 *   update, delete, markAsSold, markAsAvailable. Preserved for when Firebase
 *   is configured.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * FIREBASE SETUP CHECKLIST (complete before activating PART B)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 *  Step 1. Create a Firebase project at https://console.firebase.google.com
 *
 *  Step 2. Click "Add app" → Android → enter package name: com.campuscart.app
 *          Download google-services.json → place it at:
 *            CampusCart/app/google-services.json
 *
 *  Step 3. In CampusCart/build.gradle.kts (root) add the Google Services
 *          plugin (under [plugins] if using version catalog, or literally):
 *
 *            plugins {
 *                ...
 *                id("com.google.gms.google-services") version "4.4.2" apply false
 *            }
 *
 *  Step 4. In CampusCart/app/build.gradle.kts apply the plugin and add deps:
 *
 *            plugins {
 *                ...
 *                id("com.google.gms.google-services")   // ← apply here
 *            }
 *
 *            dependencies {
 *                ...
 *                implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
 *                implementation("com.google.firebase:firebase-firestore")
 *                implementation("com.google.firebase:firebase-auth")
 *            }
 *
 *  Step 5. In gradle/libs.versions.toml add the plugin alias:
 *
 *            [plugins]
 *            google-services = { id = "com.google.gms.google-services", version = "4.4.2" }
 *
 *  Step 6. Ensure network access to dl.google.com is available so Gradle can
 *          resolve Firebase Maven artifacts.
 *
 *  Step 7. Replace this file with FirestoreProductRepository_REAL.java.txt
 *          (rename to .java, correct package/imports, remove the _REAL suffix).
 *
 *  Step 8. In your Application class (or HomeActivity.onCreate) activate:
 *
 *            ProductRepositoryProvider.setRepository(
 *                FirestoreProductRepository.getInstance()
 *            );
 *
 *  Step 9. Update AuthHelper.getCurrentUserId() to use FirebaseAuth:
 *
 *            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
 *            return user != null ? user.getUid() : null;
 *
 * Step 10. Deploy firestore.rules from CampusCart/firestore.rules using the
 *          Firebase CLI:   firebase deploy --only firestore:rules
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * FIRESTORE RULES STATUS
 * ═══════════════════════════════════════════════════════════════════════════
 * firestore.rules exists at CampusCart/firestore.rules and enforces:
 *   - products: public read; create only when sellerId == auth.uid;
 *               update/delete only when auth.uid == existing sellerId
 *   - favourites: owner-only read/write per UID
 *   - conversations: participant-only access
 *   - users: authenticated read, owner-only write
 *
 * ⚠ The rules file has NOT been deployed. They are NOT live or enforced.
 *   They are local documentation for the intended security model only.
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class FirestoreProductRepository implements ProductRepository {

    private static final String NOT_CONFIGURED_MSG =
            "Firestore is not yet configured. " +
            "Complete the Firebase setup checklist in FirestoreProductRepository.java " +
            "to enable live persistence.";

    private static volatile FirestoreProductRepository instance;

    private FirestoreProductRepository() {}

    /**
     * Returns the singleton stub instance.
     * After Firebase is fully configured, this will return a live Firestore client.
     */
    public static synchronized FirestoreProductRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreProductRepository();
        }
        return instance;
    }

    // ── Stub implementations — each reports NOT_CONFIGURED_MSG ────────────────

    @Override
    public void createProduct(Product product, Callback<Product> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void getAvailableProducts(Callback<List<Product>> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void getProductsBySeller(String sellerId, Callback<List<Product>> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void getProductById(String productId, Callback<Product> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void updateProduct(Product product, Callback<Void> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void deleteProduct(String productId, Callback<Void> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void markAsSold(String productId, Callback<Void> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }

    @Override
    public void markAsAvailable(String productId, Callback<Void> callback) {
        if (callback != null) callback.onError(NOT_CONFIGURED_MSG);
    }
}
