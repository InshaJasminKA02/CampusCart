package com.campuscart.app.utilities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * In-memory session manager for product favourites.
 * Ensures favourite toggles remain 100% consistent across HomeActivity,
 * ProductDetailsActivity, and Search screens during the current application session
 * without requiring persistent Firestore credentials until Phase 5.
 */
public final class FavouritesSessionManager {

    public interface OnFavouritesChangeListener {
        void onFavouriteChanged(String productId, boolean isFavourite);
    }

    private static final Set<String> favouriteProductIds = Collections.synchronizedSet(new HashSet<>());
    private static final Set<OnFavouritesChangeListener> listeners = new CopyOnWriteArraySet<>();

    private FavouritesSessionManager() {}

    public static boolean isFavourite(String productId) {
        if (productId == null) return false;
        return favouriteProductIds.contains(productId);
    }

    public static void setFavourite(String productId, boolean isFavourite) {
        if (productId == null) return;
        boolean changed;
        if (isFavourite) {
            changed = favouriteProductIds.add(productId);
        } else {
            changed = favouriteProductIds.remove(productId);
        }
        if (changed) {
            notifyListeners(productId, isFavourite);
        }
    }

    public static boolean toggleFavourite(String productId) {
        if (productId == null) return false;
        boolean newState = !isFavourite(productId);
        setFavourite(productId, newState);
        return newState;
    }

    public static Set<String> getFavouriteProductIds() {
        return new HashSet<>(favouriteProductIds);
    }

    public static int getFavouriteCount() {
        return favouriteProductIds.size();
    }

    public static void addListener(OnFavouritesChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public static void removeListener(OnFavouritesChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private static void notifyListeners(String productId, boolean isFavourite) {
        for (OnFavouritesChangeListener listener : listeners) {
            try {
                listener.onFavouriteChanged(productId, isFavourite);
            } catch (Exception ignored) {}
        }
    }
}
