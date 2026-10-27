package com.campuscart.app.services;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reusable service for uploading product images to Cloudinary via unsigned upload presets.
 * Excludes all API secrets from client source code.
 * Provides progress tracking, network error recovery, and seamless fallback when
 * credentials are placeholders.
 */
public class CloudinaryUploadService {

    private static final String TAG = "CloudinaryUploadService";
    private static CloudinaryUploadService instance;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface UploadCallback {
        void onProgress(int current, int total, int percentage);
        void onSuccess(List<String> uploadedHttpsUrls);
        void onError(String errorMessage);
    }

    public static synchronized CloudinaryUploadService getInstance() {
        if (instance == null) {
            instance = new CloudinaryUploadService();
        }
        return instance;
    }

    /**
     * Uploads a list of image Uris sequentially, reporting progress after each.
     */
    public void uploadImages(Context context, List<Uri> imageUris, UploadCallback callback) {
        if (imageUris == null || imageUris.isEmpty()) {
            callback.onError("No images selected for upload.");
            return;
        }

        final Context appContext = context.getApplicationContext();
        final int total = imageUris.size();

        executor.execute(() -> {
            List<String> resultUrls = new ArrayList<>();

            // 1. Placeholder Mode when credentials are not yet configured
            if (!CloudinaryConfig.isConfigured()) {
                Log.w(TAG, "Cloudinary is not configured. Running in documented placeholder mode. " +
                        "Images will use local content URIs to enable immediate UI testing.");

                for (int i = 0; i < total; i++) {
                    final int current = i + 1;
                    final int percent = (int) (((float) current / total) * 100);

                    try {
                        Thread.sleep(400); // Simulate upload latency
                    } catch (InterruptedException ignored) {}

                    resultUrls.add(imageUris.get(i).toString());

                    mainHandler.post(() -> callback.onProgress(current, total, percent));
                }

                mainHandler.post(() -> callback.onSuccess(resultUrls));
                return;
            }

            // 2. Real Upload Mode using Unsigned Preset
            for (int i = 0; i < total; i++) {
                final int current = i + 1;
                final int percent = (int) (((float) (i) / total) * 100);
                mainHandler.post(() -> callback.onProgress(current, total, percent));

                try {
                    String secureUrl = uploadSingleImage(appContext, imageUris.get(i));
                    resultUrls.add(secureUrl);
                } catch (Exception e) {
                    Log.e(TAG, "Error uploading image " + current + " of " + total, e);
                    mainHandler.post(() -> callback.onError("Failed to upload image " + current + ": " + e.getMessage()));
                    return;
                }
            }

            mainHandler.post(() -> {
                callback.onProgress(total, total, 100);
                callback.onSuccess(resultUrls);
            });
        });
    }

    private String uploadSingleImage(Context context, Uri uri) throws Exception {
        String boundary = "===" + System.currentTimeMillis() + "===";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        URL url = new URL(CloudinaryConfig.getUploadEndpoint());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
            // Form parameter: upload_preset
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"" + lineEnd + lineEnd);
            dos.writeBytes(CloudinaryConfig.UPLOAD_PRESET + lineEnd);

            // Form parameter: file
            String filename = "img_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + lineEnd);
            dos.writeBytes("Content-Type: image/jpeg" + lineEnd + lineEnd);

            try (InputStream is = context.getContentResolver().openInputStream(uri)) {
                if (is == null) throw new IllegalArgumentException("Cannot open stream for Uri: " + uri);
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            JSONObject json = new JSONObject(response.toString());
            return json.getString("secure_url");
        } else {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
            }
            throw new RuntimeException("Cloudinary upload failed HTTP " + responseCode + ": " + errorResponse);
        }
    }
}
