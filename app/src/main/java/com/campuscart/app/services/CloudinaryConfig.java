package com.campuscart.app.services;

/**
 * Cloudinary Configuration for secure client-side product image uploads.
 *
 * SECURITY NOTICE:
 * Never store your Cloudinary API Secret in this Android application.
 * Image uploads must use an Unsigned Upload Preset configured in the Cloudinary Console:
 * 1. Log in to https://cloudinary.com/console
 * 2. Go to Settings (Gear Icon) -> Upload -> Upload presets
 * 3. Click "Add upload preset"
 * 4. Set "Signing Mode" to "Unsigned"
 * 5. Set folder to "campuscart_products" and save.
 * 6. Replace the placeholder values below with your Cloud Name and Unsigned Preset Name.
 */
public final class CloudinaryConfig {
    private CloudinaryConfig() {}

    // Replace these placeholders with your actual Cloudinary values
    public static final String CLOUD_NAME = "YOUR_CLOUDINARY_CLOUD_NAME";
    public static final String UPLOAD_PRESET = "YOUR_UNSIGNED_UPLOAD_PRESET";

    /**
     * Checks whether valid Cloudinary credentials have been configured.
     */
    public static boolean isConfigured() {
        return CLOUD_NAME != null
                && !CLOUD_NAME.trim().isEmpty()
                && !CLOUD_NAME.contains("YOUR_")
                && UPLOAD_PRESET != null
                && !UPLOAD_PRESET.trim().isEmpty()
                && !UPLOAD_PRESET.contains("YOUR_");
    }

    /**
     * Constructs the unsigned upload endpoint URL.
     */
    public static String getUploadEndpoint() {
        return "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";
    }
}
