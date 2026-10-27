package com.campuscart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.campuscart.app.R;
import com.campuscart.app.models.Product;
import com.campuscart.app.utilities.Constants;
import com.campuscart.app.utilities.FavouritesSessionManager;
import com.campuscart.app.utilities.SampleDataProvider;

import java.util.Locale;

/**
 * Product Details screen displaying comprehensive listing information,
 * seller profile overview, session-consistent favourite toggling,
 * and safe Contact Seller action placeholder.
 */
public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvStatusBadge;
    private TextView tvConditionBadge;
    private TextView tvCategoryBadge;
    private TextView tvPrice;
    private TextView tvBottomPrice;
    private TextView tvTitle;
    private TextView tvListingDate;
    private TextView tvDescription;
    private TextView tvSellerName;
    private ImageButton btnBack;
    private ImageButton btnShare;
    private ImageButton btnDetailsFavourite;
    private Button btnContactSeller;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        initViews();
        resolveProduct();

        if (product == null) {
            Toast.makeText(this, "Product details could not be loaded", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindProductData();
        setupActions();
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvConditionBadge = findViewById(R.id.tvConditionBadge);
        tvCategoryBadge = findViewById(R.id.tvCategoryBadge);
        tvPrice = findViewById(R.id.tvPrice);
        tvBottomPrice = findViewById(R.id.tvBottomPrice);
        tvTitle = findViewById(R.id.tvTitle);
        tvListingDate = findViewById(R.id.tvListingDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvSellerName = findViewById(R.id.tvSellerName);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnDetailsFavourite = findViewById(R.id.btnDetailsFavourite);
        btnContactSeller = findViewById(R.id.btnContactSeller);
    }

    private void resolveProduct() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constants.EXTRA_PRODUCT)) {
                product = (Product) intent.getSerializableExtra(Constants.EXTRA_PRODUCT);
            } else if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
                String productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID);
                for (Product p : SampleDataProvider.getSampleProducts()) {
                    if (p.getProductId().equalsIgnoreCase(productId)) {
                        product = p;
                        break;
                    }
                }
            }
        }
    }

    private void bindProductData() {
        // Title & Description
        tvTitle.setText(product.getTitle());
        tvDescription.setText(product.getDescription());

        // Price formatting in INR (₹)
        String priceFormatted;
        try {
            long priceLong = Math.round(product.getPrice());
            priceFormatted = "₹" + String.format(Locale.getDefault(), "%,d", priceLong);
        } catch (Exception e) {
            priceFormatted = "₹" + product.getPrice();
        }
        tvPrice.setText(priceFormatted);
        tvBottomPrice.setText(priceFormatted);

        // Category & Condition
        tvCategoryBadge.setText(product.getCategory());
        String condition = product.getCondition();
        if (condition != null && !condition.isEmpty()) {
            tvConditionBadge.setText(condition);
            tvConditionBadge.setVisibility(View.VISIBLE);
        } else {
            tvConditionBadge.setVisibility(View.GONE);
        }

        // Sold / Available Status
        if (product.isSold()) {
            tvStatusBadge.setText("Sold");
            tvStatusBadge.setBackgroundColor(ContextCompat.getColor(this, R.color.badge_sold_bg));
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_sold_text));
            btnContactSeller.setEnabled(false);
            btnContactSeller.setText("Item Sold");
            btnContactSeller.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.text_muted));
        } else {
            tvStatusBadge.setText("Available");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_condition);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_available_text));
        }

        // Listing timestamp
        long createdAt = product.getCreatedAt();
        if (createdAt > 0) {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                    createdAt,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            );
            tvListingDate.setText("Listed " + relativeTime + " • Campus Handoff");
        } else {
            tvListingDate.setText("Campus In-Person Handoff");
        }

        // Seller
        String sellerName = product.getSellerName();
        tvSellerName.setText(sellerName != null ? sellerName : "Student Seller");

        // Favourites state from session manager
        updateFavouriteUi();

        // Product image via Glide
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(ivProductImage);
        } else {
            ivProductImage.setImageResource(R.drawable.ic_placeholder_image);
        }
    }

    private void updateFavouriteUi() {
        boolean isFav = FavouritesSessionManager.isFavourite(product.getProductId());
        btnDetailsFavourite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, product.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check out \"" + product.getTitle() + "\" for " + tvPrice.getText() + " on CampusCart!");
            startActivity(Intent.createChooser(shareIntent, "Share Listing"));
        });

        btnDetailsFavourite.setOnClickListener(v -> {
            boolean newState = FavouritesSessionManager.toggleFavourite(product.getProductId());
            product.setFavourite(newState);
            updateFavouriteUi();
            String msg = newState ? "Saved to Favourites" : "Removed from Favourites";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        });

        btnContactSeller.setOnClickListener(v -> {
            if (product.isSold()) {
                Toast.makeText(this, "This item has already been marked as sold.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Messaging " + product.getSellerName() + " will connect via Firestore in Phase 6.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
