package com.campuscart.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscart.app.R;
import com.campuscart.app.adapters.SelectedImageAdapter;
import com.campuscart.app.models.Product;
import com.campuscart.app.repositories.ProductRepository;
import com.campuscart.app.repositories.ProductRepositoryProvider;
import com.campuscart.app.services.CloudinaryUploadService;
import com.campuscart.app.utilities.AuthHelper;
import com.campuscart.app.utilities.Constants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Add Product / Sell Listing Activity.
 * Enables students to pick multiple photos via Android Photo Picker, preview and remove images,
 * fill in details with validation, upload images via CloudinaryUploadService,
 * and publish the listing to the repository.
 */
public class AddProductActivity extends AppCompatActivity {

    private static final int MAX_PHOTOS = 5;

    private ImageButton btnBack;
    private MaterialCardView cardAddPhoto;
    private RecyclerView rvSelectedImages;
    private TextView tvPhotoCount;
    private TextView tvPhotoError;

    private TextInputLayout tilTitle;
    private TextInputEditText etTitle;
    private Spinner spinnerCategory;
    private ChipGroup chipGroupCondition;
    private TextInputLayout tilPrice;
    private TextInputEditText etPrice;
    private TextInputLayout tilDescription;
    private TextInputEditText etDescription;
    private TextInputEditText etSellerName;

    private View layoutPublishProgress;
    private TextView tvPublishStatus;
    private Button btnPublish;

    private SelectedImageAdapter imageAdapter;
    private final List<Uri> selectedUris = new ArrayList<>();
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initViews();
        setupPhotoPicker();
        setupCategorySpinner();
        setupInputWatchers();
        setupActions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardAddPhoto = findViewById(R.id.cardAddPhoto);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        tvPhotoCount = findViewById(R.id.tvPhotoCount);
        tvPhotoError = findViewById(R.id.tvPhotoError);

        tilTitle = findViewById(R.id.tilTitle);
        etTitle = findViewById(R.id.etTitle);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        chipGroupCondition = findViewById(R.id.chipGroupCondition);
        tilPrice = findViewById(R.id.tilPrice);
        etPrice = findViewById(R.id.etPrice);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
        etSellerName = findViewById(R.id.etSellerName);

        layoutPublishProgress = findViewById(R.id.layoutPublishProgress);
        tvPublishStatus = findViewById(R.id.tvPublishStatus);
        btnPublish = findViewById(R.id.btnPublish);
    }

    private void setupPhotoPicker() {
        // Android Visual Photo Picker launcher
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        int remaining = MAX_PHOTOS - selectedUris.size();
                        if (remaining > 0) {
                            List<Uri> toAdd = uris.size() > remaining ? uris.subList(0, remaining) : uris;
                            for (Uri u : toAdd) {
                                if (!selectedUris.contains(u)) {
                                    selectedUris.add(u);
                                }
                            }
                            imageAdapter.notifyDataSetChanged();
                            updatePhotoCount();
                            tvPhotoError.setVisibility(View.GONE);
                        }
                    }
                }
        );

        imageAdapter = new SelectedImageAdapter(selectedUris, (uri, position) -> {
            imageAdapter.removeImage(position);
            updatePhotoCount();
        });

        rvSelectedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSelectedImages.setAdapter(imageAdapter);
        updatePhotoCount();
    }

    private void updatePhotoCount() {
        tvPhotoCount.setText(selectedUris.size() + "/" + MAX_PHOTOS);
        cardAddPhoto.setVisibility(selectedUris.size() >= MAX_PHOTOS ? View.GONE : View.VISIBLE);
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add(Constants.CATEGORY_BOOKS);
        categories.add(Constants.CATEGORY_ELECTRONICS);
        categories.add(Constants.CATEGORY_ACCESSORIES);
        categories.add(Constants.CATEGORY_CLOTHING);
        categories.add(Constants.CATEGORY_STATIONERY);
        categories.add(Constants.CATEGORY_SPORTS);
        categories.add(Constants.CATEGORY_FURNITURE);
        categories.add(Constants.CATEGORY_OTHER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupInputWatchers() {
        etTitle.addTextChangedListener(new SimpleTextWatcher(() -> tilTitle.setError(null)));
        etPrice.addTextChangedListener(new SimpleTextWatcher(() -> tilPrice.setError(null)));
        etDescription.addTextChangedListener(new SimpleTextWatcher(() -> tilDescription.setError(null)));
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        cardAddPhoto.setOnClickListener(v -> {
            if (selectedUris.size() >= MAX_PHOTOS) {
                Toast.makeText(this, "Maximum 5 photos allowed", Toast.LENGTH_SHORT).show();
                return;
            }
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btnPublish.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        boolean isValid = true;

        // 1. Photos
        if (selectedUris.isEmpty()) {
            tvPhotoError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            tvPhotoError.setVisibility(View.GONE);
        }

        // 2. Title
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (title.length() < 3) {
            tilTitle.setError("Title must be at least 3 characters");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }

        // 3. Price
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        double price = 0;
        if (priceStr.isEmpty()) {
            tilPrice.setError("Price is required");
            isValid = false;
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    tilPrice.setError("Price must be greater than ₹0");
                    isValid = false;
                } else {
                    tilPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Please enter a valid numeric price");
                isValid = false;
            }
        }

        // 4. Description
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        if (description.length() < 8) {
            tilDescription.setError("Description must be at least 8 characters");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }

        if (!isValid) {
            Toast.makeText(this, "Please review and complete the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Category
        String category = spinnerCategory.getSelectedItem() != null ?
                spinnerCategory.getSelectedItem().toString() : Constants.CATEGORY_OTHER;

        // Get Condition
        String condition = Constants.CONDITION_LIKE_NEW;
        int checkedChipId = chipGroupCondition.getCheckedChipId();
        if (checkedChipId != -1) {
            Chip chip = findViewById(checkedChipId);
            if (chip != null) {
                condition = chip.getText().toString();
            }
        }

        // Get Seller Name
        String sellerName = etSellerName.getText() != null && !etSellerName.getText().toString().trim().isEmpty() ?
                etSellerName.getText().toString().trim() : "Campus Student Seller";

        startPublishFlow(title, description, price, category, condition, sellerName);
    }

    private void startPublishFlow(String title, String description, double price,
                                  String category, String condition, String sellerName) {
        setUiSubmitting(true);
        tvPublishStatus.setText("Uploading photos to Cloudinary…");

        // 1. Upload photos via CloudinaryUploadService
        CloudinaryUploadService.getInstance().uploadImages(this, selectedUris, new CloudinaryUploadService.UploadCallback() {
            @Override
            public void onProgress(int current, int total, int percentage) {
                tvPublishStatus.setText("Uploading photo " + current + " of " + total + " (" + percentage + "%)…");
            }

            @Override
            public void onSuccess(List<String> uploadedHttpsUrls) {
                tvPublishStatus.setText("Saving listing to marketplace…");

                // 2. Build product entity
                Product newProduct = new Product(
                        null,
                        title,
                        description,
                        price,
                        category,
                        condition,
                        uploadedHttpsUrls,
                        AuthHelper.getCurrentUserId(),   // seller UID from AuthHelper
                        sellerName,
                        System.currentTimeMillis(),
                        false
                );

                // 3. Save to repository via ProductRepositoryProvider (swappable backend)
                ProductRepositoryProvider.getRepository().createProduct(newProduct, new ProductRepository.Callback<Product>() {
                    @Override
                    public void onSuccess(Product created) {
                        setUiSubmitting(false);
                        Toast.makeText(AddProductActivity.this, "Listing published successfully!", Toast.LENGTH_LONG).show();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(Constants.EXTRA_PRODUCT, created);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        setUiSubmitting(false);
                        Toast.makeText(AddProductActivity.this, "Failed to save listing: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                setUiSubmitting(false);
                Toast.makeText(AddProductActivity.this, "Upload error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUiSubmitting(boolean isSubmitting) {
        btnPublish.setEnabled(!isSubmitting);
        cardAddPhoto.setEnabled(!isSubmitting);
        btnBack.setEnabled(!isSubmitting);
        etTitle.setEnabled(!isSubmitting);
        etPrice.setEnabled(!isSubmitting);
        etDescription.setEnabled(!isSubmitting);
        etSellerName.setEnabled(!isSubmitting);
        spinnerCategory.setEnabled(!isSubmitting);
        chipGroupCondition.setEnabled(!isSubmitting);

        layoutPublishProgress.setVisibility(isSubmitting ? View.VISIBLE : View.GONE);
        btnPublish.setText(isSubmitting ? "Publishing…" : "Publish Listing");
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable onTextChanged;

        SimpleTextWatcher(Runnable onTextChanged) {
            this.onTextChanged = onTextChanged;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (onTextChanged != null) onTextChanged.run();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
