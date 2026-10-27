package com.campuscart.app.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.campuscart.app.R;

/**
 * Centralized navigation helper to manage bottom navigation destinations
 * across all marketplace screens cleanly without duplicate code or broken links.
 */
public final class NavigationHelper {

    public enum Screen {
        HOME,
        FAVOURITES,
        SELL,
        CHATS,
        PROFILE
    }

    private NavigationHelper() {}

    /**
     * Handles bottom navigation tab clicks.
     * Safely checks if target activity exists; otherwise shows a friendly phase notification.
     */
    public static void navigateTo(Context context, Screen target, Screen current) {
        if (context == null || target == current) {
            return;
        }

        switch (target) {
            case HOME:
                launchActivitySafely(context, "com.campuscart.app.activities.HomeActivity", "Home");
                break;
            case FAVOURITES:
                launchActivitySafely(context, "com.campuscart.app.activities.FavouritesActivity", "Favourites (Phase 5)");
                break;
            case SELL:
                launchActivitySafely(context, "com.campuscart.app.activities.AddProductActivity", "Sell a Product (Phase 3)");
                break;
            case CHATS:
                launchActivitySafely(context, "com.campuscart.app.activities.ChatListActivity", "Chats (Phase 6)");
                break;
            case PROFILE:
                // Phase 4: Profile tab opens My Listings until a full Profile screen is built in Phase 6.
                launchActivitySafely(context, "com.campuscart.app.activities.MyListingsActivity", "My Listings");
                break;
        }
    }

    private static void launchActivitySafely(Context context, String className, String screenLabel) {
        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(context, targetClass);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(context,
                    screenLabel + " will be fully available in its implementation phase.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
