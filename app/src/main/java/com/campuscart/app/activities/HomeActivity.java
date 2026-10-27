package com.campuscart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscart.app.R;
import com.campuscart.app.adapters.CategoryAdapter;
import com.campuscart.app.adapters.ProductAdapter;
import com.campuscart.app.models.Category;
import com.campuscart.app.models.FilterCriteria;
import com.campuscart.app.models.Product;
import com.campuscart.app.repositories.ProductRepository;
import com.campuscart.app.repositories.ProductRepositoryProvider;
import com.campuscart.app.utilities.Constants;
import com.campuscart.app.utilities.FavouritesSessionManager;
import com.campuscart.app.utilities.NavigationHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Main Home Activity for CampusCart marketplace.
 * Features live search, category browsing, advanced filtering,
 * session-consistent favourites, and direct integration with AddProductActivity.
 */
public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnClearSearch;
    private ImageButton btnOpenFilter;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private View layoutActiveFilter;
    private TextView tvActiveFilterLabel;
    private TextView tvClearFilter;
    private TextView tvSectionTitle;
    private TextView tvProductCount;
    private View layoutLoading;
    private View layoutEmpty;
    private Button btnResetFilter;
    private ExtendedFloatingActionButton fabSell;

    // Adapters & Data
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private final List<Product> masterProductList = new ArrayList<>();
    private final List<Product> displayedProductList = new ArrayList<>();
    private FilterCriteria filterCriteria = new FilterCriteria();

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> productDetailsLauncher;
    private ActivityResultLauncher<Intent> searchFilterLauncher;
    private ActivityResultLauncher<Intent> addProductLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupWindowInsets();
        setupResultLaunchers();
        setupCategoryRecycler();
        setupProductRecycler();
        setupSearchAndFilter();
        setupBottomNavigation();

        loadDataFromRepository();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favourite indicators and update counts in case changed elsewhere
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnOpenFilter = findViewById(R.id.btnOpenFilter);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        layoutActiveFilter = findViewById(R.id.layoutActiveFilter);
        tvActiveFilterLabel = findViewById(R.id.tvActiveFilterLabel);
        tvClearFilter = findViewById(R.id.tvClearFilter);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        tvProductCount = findViewById(R.id.tvProductCount);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnResetFilter = findViewById(R.id.btnResetFilter);
        fabSell = findViewById(R.id.fabSell);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootHome), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
    }

    private void setupResultLaunchers() {
        // Details launcher
        productDetailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (productAdapter != null) {
                        productAdapter.notifyDataSetChanged();
                    }
                }
        );

        // Search Filter launcher
        searchFilterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.hasExtra(SearchFilterActivity.EXTRA_FILTER_CRITERIA)) {
                            filterCriteria = (FilterCriteria) data.getSerializableExtra(SearchFilterActivity.EXTRA_FILTER_CRITERIA);
                            if (filterCriteria == null) {
                                filterCriteria = new FilterCriteria();
                            }
                            syncUiWithFilterCriteria();
                            filterProducts();
                        }
                    }
                }
        );

        // Add Product launcher
        addProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Product newProduct = (Product) result.getData().getSerializableExtra(Constants.EXTRA_PRODUCT);
                        loadDataFromRepository();
                        rvProducts.smoothScrollToPosition(0);
                        String title = newProduct != null ? newProduct.getTitle() : "Listing";
                        Toast.makeText(this, "Your listing \"" + title + "\" is now live on CampusCart!", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setupCategoryRecycler() {
        List<Category> categories = Constants.getMarketplaceCategories();
        categoryAdapter = new CategoryAdapter(categories, (category, position) -> {
            filterCriteria.setCategory(category.getName());
            filterProducts();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProductRecycler() {
        productAdapter = new ProductAdapter(displayedProductList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT, product);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProductId());
                productDetailsLauncher.launch(intent);
            }

            @Override
            public void onFavouriteClick(Product product, int position) {
                boolean isFav = FavouritesSessionManager.isFavourite(product.getProductId());
                String status = isFav ? "Saved to Favourites" : "Removed from Favourites";
                Toast.makeText(HomeActivity.this, status, Toast.LENGTH_SHORT).show();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setAdapter(productAdapter);
    }

    private void setupSearchAndFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                filterCriteria.setQuery(q);
                btnClearSearch.setVisibility(q.isEmpty() ? View.GONE : View.VISIBLE);
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            filterCriteria.setQuery("");
            filterProducts();
        });

        btnOpenFilter.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchFilterActivity.class);
            intent.putExtra(SearchFilterActivity.EXTRA_FILTER_CRITERIA, filterCriteria);
            searchFilterLauncher.launch(intent);
        });

        tvClearFilter.setOnClickListener(v -> resetFilters());
        btnResetFilter.setOnClickListener(v -> resetFilters());

        fabSell.setOnClickListener(v -> launchSellScreen());
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            rvProducts.smoothScrollToPosition(0);
        });

        findViewById(R.id.navFavourites).setOnClickListener(v ->
                NavigationHelper.navigateTo(this, NavigationHelper.Screen.FAVOURITES, NavigationHelper.Screen.HOME)
        );

        findViewById(R.id.navSell).setOnClickListener(v -> launchSellScreen());

        findViewById(R.id.navChats).setOnClickListener(v ->
                NavigationHelper.navigateTo(this, NavigationHelper.Screen.CHATS, NavigationHelper.Screen.HOME)
        );

        findViewById(R.id.navProfile).setOnClickListener(v ->
                NavigationHelper.navigateTo(this, NavigationHelper.Screen.PROFILE, NavigationHelper.Screen.HOME)
        );
    }

    private void launchSellScreen() {
        Intent intent = new Intent(this, AddProductActivity.class);
        addProductLauncher.launch(intent);
    }

    private void loadDataFromRepository() {
        layoutLoading.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        ProductRepositoryProvider.getRepository().getAvailableProducts(new ProductRepository.Callback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                layoutLoading.setVisibility(View.GONE);
                masterProductList.clear();
                if (products != null) {
                    masterProductList.addAll(products);
                }
                filterProducts();
            }

            @Override
            public void onError(String errorMessage) {
                layoutLoading.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Error loading products: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts() {
        displayedProductList.clear();

        String query = filterCriteria.getQuery().toLowerCase(Locale.ROOT);
        String category = filterCriteria.getCategory();
        String condition = filterCriteria.getCondition();
        Double minPrice = filterCriteria.getMinPrice();
        Double maxPrice = filterCriteria.getMaxPrice();

        for (Product product : masterProductList) {
            // 1. Category Filter
            boolean matchesCategory = category.equalsIgnoreCase(Constants.CATEGORY_ALL) ||
                    (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category));

            // 2. Query Filter (searches both title and description)
            boolean matchesQuery = query.isEmpty() ||
                    (product.getTitle() != null && product.getTitle().toLowerCase(Locale.ROOT).contains(query)) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase(Locale.ROOT).contains(query));

            // 3. Condition Filter
            boolean matchesCondition = condition.equalsIgnoreCase("All") ||
                    (product.getCondition() != null && product.getCondition().equalsIgnoreCase(condition));

            // 4. Price Range Filter
            boolean matchesMinPrice = (minPrice == null) || (product.getPrice() >= minPrice);
            boolean matchesMaxPrice = (maxPrice == null) || (product.getPrice() <= maxPrice);

            if (matchesCategory && matchesQuery && matchesCondition && matchesMinPrice && matchesMaxPrice) {
                displayedProductList.add(product);
            }
        }

        // 5. Sorting
        String sort = filterCriteria.getSortBy();
        if (FilterCriteria.SORT_PRICE_LOW_TO_HIGH.equals(sort)) {
            Collections.sort(displayedProductList, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else if (FilterCriteria.SORT_PRICE_HIGH_TO_LOW.equals(sort)) {
            Collections.sort(displayedProductList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        } else {
            // Newest first by createdAt
            Collections.sort(displayedProductList, (p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
        }

        productAdapter.notifyDataSetChanged();
        updateUIState();
    }

    private void updateUIState() {
        int count = displayedProductList.size();

        // Active filter summary banner
        if (filterCriteria.hasActiveFilter()) {
            layoutActiveFilter.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder("Filtered: ");
            List<String> activeFilters = new ArrayList<>();

            if (!filterCriteria.getCategory().equalsIgnoreCase(Constants.CATEGORY_ALL)) {
                activeFilters.add(filterCriteria.getCategory());
            }
            if (!filterCriteria.getQuery().isEmpty()) {
                activeFilters.add("\"" + filterCriteria.getQuery() + "\"");
            }
            if (!filterCriteria.getCondition().equalsIgnoreCase("All")) {
                activeFilters.add(filterCriteria.getCondition());
            }
            if (filterCriteria.getMinPrice() != null || filterCriteria.getMaxPrice() != null) {
                String min = filterCriteria.getMinPrice() != null ? "₹" + filterCriteria.getMinPrice().intValue() : "₹0";
                String max = filterCriteria.getMaxPrice() != null ? "₹" + filterCriteria.getMaxPrice().intValue() : "any";
                activeFilters.add(min + "–" + max);
            }
            if (FilterCriteria.SORT_PRICE_LOW_TO_HIGH.equals(filterCriteria.getSortBy())) {
                activeFilters.add("Price ↑");
            } else if (FilterCriteria.SORT_PRICE_HIGH_TO_LOW.equals(filterCriteria.getSortBy())) {
                activeFilters.add("Price ↓");
            }

            for (int i = 0; i < activeFilters.size(); i++) {
                sb.append(activeFilters.get(i));
                if (i < activeFilters.size() - 1) {
                    sb.append(" • ");
                }
            }
            tvActiveFilterLabel.setText(sb.toString());
        } else {
            layoutActiveFilter.setVisibility(View.GONE);
        }

        // Section header and count
        String categoryTitle = filterCriteria.getCategory();
        tvSectionTitle.setText(categoryTitle.equalsIgnoreCase(Constants.CATEGORY_ALL) ?
                getString(R.string.recently_listed_title) : categoryTitle);
        tvProductCount.setText(count + (count == 1 ? " item available" : " items available"));

        // Empty state
        if (count == 0) {
            rvProducts.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvProducts.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void syncUiWithFilterCriteria() {
        if (!etSearch.getText().toString().equals(filterCriteria.getQuery())) {
            etSearch.setText(filterCriteria.getQuery());
            etSearch.setSelection(filterCriteria.getQuery().length());
        }
        categoryAdapter.setSelectedCategoryByName(filterCriteria.getCategory());
    }

    private void resetFilters() {
        filterCriteria.clear();
        etSearch.setText("");
        categoryAdapter.setSelectedCategoryByName(Constants.CATEGORY_ALL);
        filterProducts();
    }
}
