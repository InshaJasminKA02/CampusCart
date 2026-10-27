package com.campuscart.app.utilities;

/**
 * Authentication and User Identity helper for CampusCart.
 *
 * Provides a clean development fallback UID ("dev_student_uid_2026") so the marketplace
 * listing creation, My Listings, ownership validation, and edit/delete permissions
 * can be tested without crashing before the friend's login and registration screens
 * are attached.
 *
 * When Firebase Authentication is connected:
 * Replace the fallback with:
 *   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
 *   return user != null ? user.getUid() : null;
 */
public final class AuthHelper {

    // Development fallback UID representing the logged-in student user for testing
    public static final String DEV_FALLBACK_UID = "dev_student_uid_2026";
    public static final String DEV_FALLBACK_NAME = "Campus Student (You)";

    private AuthHelper() {}

    /**
     * Gets the authenticated user's UID.
     */
    public static String getCurrentUserId() {
        // Fallback for offline/development preview prior to login integration
        return DEV_FALLBACK_UID;
    }

    /**
     * Gets the current user's display name.
     */
    public static String getCurrentUserName() {
        return DEV_FALLBACK_NAME;
    }

    /**
     * Checks if the currently authenticated user is the owner of a given product listing.
     */
    public static boolean isCurrentUserOwner(String sellerId) {
        if (sellerId == null) return false;
        return sellerId.equals(getCurrentUserId());
    }

    /**
     * Indicates whether a user session is active.
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}
