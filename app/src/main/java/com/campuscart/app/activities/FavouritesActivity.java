package com.campuscart.app.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscart.app.R;
import com.campuscart.app.adapters.ProductAdapter;
import com.campuscart.app.models.Product;
import com.campuscart.app.repositories.ProductRepository;
import com.campuscart.app.repositories.ProductRepositoryProvider;
import com.campuscart.app.utilities.FavouritesSessionManager;
import com.campuscart.app.utilities.FavouritesSessionManager.OnFavouritesChangeListener;
import com.campuscart.app.activities.ProductDetailsActivity;
import com.campuscart.app.utilities.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Phase 5 – Favourites screen.
 * Shows the list of products the user has marked as favourite during the current session.
 * Uses the in‑memory {@link FavouritesSessionManager} for storage; no Firebase required.
 */
public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;
    private View layoutEmpty;
    private TextView tvEmptyMessage;
    private ProductAdapter productAdapter;
    private final List<Product> favouriteProducts = new ArrayList<>();

    private final OnFavouritesChangeListener favouriteChangeListener = (productId, isFavourite) -> {
        // Re‑filter the list whenever a favourite state changes elsewhere.
        filterAndDisplayFavourites();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setSupportActionBar(findViewById(R.id.toolbar));
        // Enable back arrow in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_favourites);
        }
        initViews();
        setupRecycler();
        loadFavourites();
        // Listen for changes from other screens (Home, Details, etc.)
        FavouritesSessionManager.addListener(favouriteChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FavouritesSessionManager.removeListener(favouriteChangeListener);
    }

    private void initViews() {
        rvFavourites = findViewById(R.id.rvFavourites);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupRecycler() {
        productAdapter = new ProductAdapter(favouriteProducts, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Open product details.
                Intent intent = new Intent(FavouritesActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT, product);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
                startActivity(intent);
            }

            @Override
            public void onFavouriteClick(Product product, int position) {
                boolean isFav = FavouritesSessionManager.isFavourite(product.getProductId());
                String status = isFav ? "Saved to Favourites" : "Removed from Favourites";
                Toast.makeText(FavouritesActivity.this, status, Toast.LENGTH_SHORT).show();
            }
        });
        rvFavourites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavourites.setAdapter(productAdapter);
    }

    /** Load all products from the repository and keep only those marked as favourite. */
    private void loadFavourites() {
        ProductRepository repo = ProductRepositoryProvider.getRepository();
        repo.getAvailableProducts(new ProductRepository.Callback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> allProducts) {
                filterAndDisplayFavourites(allProducts);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(FavouritesActivity.this, "Failed to load products: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Filter the supplied list (or the cached list) by favourite IDs and update UI. */
    private void filterAndDisplayFavourites() {
        // Overload used when listener fires – we still have the master list cached in favouriteProducts.
        // To keep implementation simple, we re‑load from repository.
        loadFavourites();
    }

    private void filterAndDisplayFavourites(List<Product> allProducts) {
        Set<String> favIds = FavouritesSessionManager.getFavouriteProductIds();
        favouriteProducts.clear();
        if (favIds != null && !favIds.isEmpty()) {
            for (Product p : allProducts) {
                if (favIds.contains(p.getProductId())) {
                    favouriteProducts.add(p);
                }
            }
        }
        if (favouriteProducts.isEmpty()) {
            rvFavourites.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText(R.string.empty_favourites_message);
        } else {
            rvFavourites.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            productAdapter.notifyDataSetChanged();
        }
    }

    // Handle toolbar back press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
