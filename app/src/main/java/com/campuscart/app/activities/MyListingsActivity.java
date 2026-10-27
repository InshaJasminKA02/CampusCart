package com.campuscart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscart.app.R;
import com.campuscart.app.adapters.MyListingAdapter;
import com.campuscart.app.models.Product;
import com.campuscart.app.repositories.ProductRepository;
import com.campuscart.app.repositories.ProductRepositoryProvider;
import com.campuscart.app.utilities.AuthHelper;
import com.campuscart.app.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * My Listings screen — shows the current seller's own listings split into
 * Active and Sold tabs. Sellers can:
 *
 *   • View any listing's details (tapping the card body)
 *   • Toggle a listing between "Available" and "Sold"
 *   • Edit a listing (navigates to AddProductActivity with pre-filled data)
 *   • Delete a listing with a confirmation dialog
 *
 * The activity uses {@link ProductRepositoryProvider} to obtain the active
 * repository implementation. During development (no Firebase), this is
 * {@link com.campuscart.app.repositories.InMemoryProductRepository}.
 * Once Firebase is configured, swap in FirestoreProductRepository via
 * ProductRepositoryProvider.setRepository(...) in your Application class.
 *
 * Navigation:
 *   Reached from HomeActivity's Profile bottom nav tab (and later from a
 *   dedicated Profile screen). Navigated back via the toolbar back button.
 */
public class MyListingsActivity extends AppCompatActivity {

    // ── Views ──────────────────────────────────────────────────────────────────
    private ImageButton btnBack;
    private TextView tvListingCount;
    private TextView tabActive;
    private TextView tabSold;
    private View tabIndicator;
    private View layoutLoading;
    private View layoutEmptyActive;
    private View layoutEmptySold;
    private RecyclerView rvActiveListings;
    private RecyclerView rvSoldListings;

    // ── Data ───────────────────────────────────────────────────────────────────
    private final List<Product> activeListings = new ArrayList<>();
    private final List<Product> soldListings = new ArrayList<>();
    private MyListingAdapter activeAdapter;
    private MyListingAdapter soldAdapter;
    private ProductRepository repository;

    // ── Tab state ──────────────────────────────────────────────────────────────
    private boolean showingActiveTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        repository = ProductRepositoryProvider.getRepository();

        initViews();
        setupWindowInsets();
        setupTabs();
        setupAdapters();
        setupActions();

        loadMyListings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh in case listings changed from another screen (e.g. AddProductActivity)
        loadMyListings();
    }

    // ── Initialisation ─────────────────────────────────────────────────────────

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvListingCount = findViewById(R.id.tvListingCount);
        tabActive = findViewById(R.id.tabActive);
        tabSold = findViewById(R.id.tabSold);
        tabIndicator = findViewById(R.id.tabIndicator);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutEmptyActive = findViewById(R.id.layoutEmptyActive);
        layoutEmptySold = findViewById(R.id.layoutEmptySold);
        rvActiveListings = findViewById(R.id.rvActiveListings);
        rvSoldListings = findViewById(R.id.rvSoldListings);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootMyListings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
    }

    private void setupTabs() {
        tabActive.setOnClickListener(v -> switchTab(true));
        tabSold.setOnClickListener(v -> switchTab(false));
        // Set initial indicator width after layout
        tabActive.post(this::updateTabIndicator);
    }

    private void setupAdapters() {
        activeAdapter = new MyListingAdapter(activeListings, buildActionListener(true));
        rvActiveListings.setLayoutManager(new LinearLayoutManager(this));
        rvActiveListings.setAdapter(activeAdapter);
        rvActiveListings.setNestedScrollingEnabled(false);

        soldAdapter = new MyListingAdapter(soldListings, buildActionListener(false));
        rvSoldListings.setLayoutManager(new LinearLayoutManager(this));
        rvSoldListings.setAdapter(soldAdapter);
        rvSoldListings.setNestedScrollingEnabled(false);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());

        // "Sell Now" button inside the empty-active state
        View btnSellNow = findViewById(R.id.btnSellNow);
        if (btnSellNow != null) {
            btnSellNow.setOnClickListener(v -> {
                startActivity(new Intent(this, AddProductActivity.class));
            });
        }
    }

    // ── Data Loading ───────────────────────────────────────────────────────────

    private void loadMyListings() {
        String sellerId = AuthHelper.getCurrentUserId();
        if (sellerId == null) {
            showError("You must be signed in to view your listings.");
            return;
        }

        showLoadingState();

        repository.getProductsBySeller(sellerId, new ProductRepository.Callback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> allMyListings) {
                partitionListings(allMyListings);
                refreshAllViews();
            }

            @Override
            public void onError(String errorMessage) {
                layoutLoading.setVisibility(View.GONE);
                showError("Failed to load listings: " + errorMessage);
            }
        });
    }

    /**
     * Splits the flat list from the repository into active and sold buckets.
     * Newest items are first (repository guarantees createdAt-desc order).
     */
    private void partitionListings(List<Product> all) {
        activeListings.clear();
        soldListings.clear();
        if (all != null) {
            for (Product p : all) {
                if (p.isSold()) {
                    soldListings.add(p);
                } else {
                    activeListings.add(p);
                }
            }
        }
    }

    // ── UI State helpers ───────────────────────────────────────────────────────

    private void showLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutEmptyActive.setVisibility(View.GONE);
        layoutEmptySold.setVisibility(View.GONE);
        rvActiveListings.setVisibility(View.GONE);
        rvSoldListings.setVisibility(View.GONE);
        tvListingCount.setVisibility(View.GONE);
    }

    private void refreshAllViews() {
        layoutLoading.setVisibility(View.GONE);
        activeAdapter.notifyDataSetChanged();
        soldAdapter.notifyDataSetChanged();
        updateListingCount();
        applyTabVisibility();
    }

    private void updateListingCount() {
        int total = activeListings.size() + soldListings.size();
        if (total > 0) {
            tvListingCount.setText(total + (total == 1 ? " listing" : " listings"));
            tvListingCount.setVisibility(View.VISIBLE);
        } else {
            tvListingCount.setVisibility(View.GONE);
        }
    }

    private void applyTabVisibility() {
        if (showingActiveTab) {
            rvActiveListings.setVisibility(activeListings.isEmpty() ? View.GONE : View.VISIBLE);
            layoutEmptyActive.setVisibility(activeListings.isEmpty() ? View.VISIBLE : View.GONE);
            rvSoldListings.setVisibility(View.GONE);
            layoutEmptySold.setVisibility(View.GONE);
        } else {
            rvSoldListings.setVisibility(soldListings.isEmpty() ? View.GONE : View.VISIBLE);
            layoutEmptySold.setVisibility(soldListings.isEmpty() ? View.VISIBLE : View.GONE);
            rvActiveListings.setVisibility(View.GONE);
            layoutEmptyActive.setVisibility(View.GONE);
        }
    }

    private void switchTab(boolean showActive) {
        showingActiveTab = showActive;

        tabActive.setTextColor(getColor(showActive ? R.color.primary_purple : R.color.text_secondary));
        tabActive.setTypeface(showActive
                ? android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.BOLD)
                : android.graphics.Typeface.DEFAULT);

        tabSold.setTextColor(getColor(!showActive ? R.color.primary_purple : R.color.text_secondary));
        tabSold.setTypeface(!showActive
                ? android.graphics.Typeface.defaultFromStyle(android.graphics.Typeface.BOLD)
                : android.graphics.Typeface.DEFAULT);

        updateTabIndicator();
        applyTabVisibility();
    }

    /**
     * Moves the tab-indicator underline under the selected tab by adjusting its
     * left margin and width to match the tab TextView bounds.
     */
    private void updateTabIndicator() {
        View targetTab = showingActiveTab ? tabActive : tabSold;
        android.view.ViewGroup.MarginLayoutParams lp =
                (android.view.ViewGroup.MarginLayoutParams) tabIndicator.getLayoutParams();
        lp.leftMargin = targetTab.getLeft();
        lp.width = targetTab.getWidth();
        tabIndicator.setLayoutParams(lp);
    }

    // ── Listing Action Listener factory ───────────────────────────────────────

    private MyListingAdapter.OnListingActionListener buildActionListener(boolean isActiveTab) {
        return new MyListingAdapter.OnListingActionListener() {

            @Override
            public void onCardClick(Product product) {
                // Open Product Details screen (read-only view for own listing)
                Intent intent = new Intent(MyListingsActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT, product);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
                startActivity(intent);
            }

            @Override
            public void onToggleSoldStatus(Product product, int position) {
                boolean markAsSold = !product.isSold();
                String actionLabel = markAsSold ? "Mark as Sold" : "Mark as Available";
                String confirmMsg = markAsSold
                        ? "Mark \"" + product.getTitle() + "\" as sold? It will no longer appear in search results."
                        : "Re-list \"" + product.getTitle() + "\" as available?";

                new AlertDialog.Builder(MyListingsActivity.this)
                        .setTitle(actionLabel)
                        .setMessage(confirmMsg)
                        .setPositiveButton("Yes", (dialog, which) -> performToggleSold(product, markAsSold))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onEditClick(Product product, int position) {
                // Phase 4: Edit navigates to AddProductActivity pre-filled
                // Full edit pre-fill is a Phase 5 enhancement; for now notify user
                Toast.makeText(MyListingsActivity.this,
                        "Edit listing: \"" + product.getTitle() + "\" — full edit form coming in Phase 5.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeleteClick(Product product, int position) {
                new AlertDialog.Builder(MyListingsActivity.this)
                        .setTitle("Delete Listing")
                        .setMessage("Permanently delete \"" + product.getTitle() + "\"? This cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> performDelete(product))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        };
    }

    // ── Repository write operations ────────────────────────────────────────────

    private void performToggleSold(Product product, boolean markAsSold) {
        String productId = product.getProductId();

        ProductRepository.Callback<Void> callback = new ProductRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                product.setSold(markAsSold);

                // Move product between active/sold buckets
                if (markAsSold) {
                    activeListings.remove(product);
                    soldListings.add(0, product);
                } else {
                    soldListings.remove(product);
                    activeListings.add(0, product);
                }

                refreshAllViews();

                String msg = markAsSold
                        ? "\"" + product.getTitle() + "\" marked as sold."
                        : "\"" + product.getTitle() + "\" is now available again.";
                Toast.makeText(MyListingsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MyListingsActivity.this,
                        "Could not update listing: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        };

        if (markAsSold) {
            repository.markAsSold(productId, callback);
        } else {
            repository.markAsAvailable(productId, callback);
        }
    }

    private void performDelete(Product product) {
        repository.deleteProduct(product.getProductId(), new ProductRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                activeListings.remove(product);
                soldListings.remove(product);
                refreshAllViews();
                Toast.makeText(MyListingsActivity.this,
                        "\"" + product.getTitle() + "\" deleted.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MyListingsActivity.this,
                        "Could not delete listing: " + errorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
