package com.campuscart.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.campuscart.app.R;
import com.campuscart.app.models.FilterCriteria;
import com.campuscart.app.utilities.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * Advanced Search and Filter activity for narrowing campus listings
 * by keyword, category, condition, price range, and sorting orders.
 */
public class SearchFilterActivity extends AppCompatActivity {

    public static final String EXTRA_FILTER_CRITERIA = "extra_filter_criteria";

    private ImageButton btnBack;
    private TextView tvResetAll;
    private EditText etFilterQuery;
    private ChipGroup chipGroupCategory;
    private ChipGroup chipGroupCondition;
    private EditText etMinPrice;
    private EditText etMaxPrice;
    private ChipGroup chipGroupSort;
    private Button btnApplyFilters;

    private FilterCriteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        initViews();
        resolveCriteria();
        populateUi();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvResetAll = findViewById(R.id.tvResetAll);
        etFilterQuery = findViewById(R.id.etFilterQuery);
        chipGroupCategory = findViewById(R.id.chipGroupCategory);
        chipGroupCondition = findViewById(R.id.chipGroupCondition);
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        chipGroupSort = findViewById(R.id.chipGroupSort);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
    }

    private void resolveCriteria() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_FILTER_CRITERIA)) {
            criteria = (FilterCriteria) intent.getSerializableExtra(EXTRA_FILTER_CRITERIA);
        }
        if (criteria == null) {
            criteria = new FilterCriteria();
        }
    }

    private void populateUi() {
        // Query
        etFilterQuery.setText(criteria.getQuery());

        // Category
        selectChipByText(chipGroupCategory, criteria.getCategory(), R.id.chipCatAll);

        // Condition
        selectChipByText(chipGroupCondition, criteria.getCondition(), R.id.chipCondAll);

        // Price
        if (criteria.getMinPrice() != null && criteria.getMinPrice() > 0) {
            etMinPrice.setText(String.valueOf(criteria.getMinPrice().intValue()));
        } else {
            etMinPrice.setText("");
        }

        if (criteria.getMaxPrice() != null && criteria.getMaxPrice() > 0) {
            etMaxPrice.setText(String.valueOf(criteria.getMaxPrice().intValue()));
        } else {
            etMaxPrice.setText("");
        }

        // Sort By
        if (FilterCriteria.SORT_PRICE_LOW_TO_HIGH.equals(criteria.getSortBy())) {
            chipGroupSort.check(R.id.chipSortPriceAsc);
        } else if (FilterCriteria.SORT_PRICE_HIGH_TO_LOW.equals(criteria.getSortBy())) {
            chipGroupSort.check(R.id.chipSortPriceDesc);
        } else {
            chipGroupSort.check(R.id.chipSortNewest);
        }
    }

    private void selectChipByText(ChipGroup group, String text, int defaultId) {
        if (text == null || text.isEmpty()) {
            group.check(defaultId);
            return;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof Chip) {
                Chip chip = (Chip) group.getChildAt(i);
                if (text.equalsIgnoreCase(chip.getText().toString())) {
                    chip.setChecked(true);
                    return;
                }
            }
        }
        group.check(defaultId);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvResetAll.setOnClickListener(v -> {
            criteria.clear();
            populateUi();
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
        });

        btnApplyFilters.setOnClickListener(v -> applyAndReturn());
    }

    private void applyAndReturn() {
        // 1. Query
        criteria.setQuery(etFilterQuery.getText().toString().trim());

        // 2. Category
        int selectedCatId = chipGroupCategory.getCheckedChipId();
        if (selectedCatId != -1) {
            Chip catChip = findViewById(selectedCatId);
            if (catChip != null) {
                criteria.setCategory(catChip.getText().toString());
            }
        } else {
            criteria.setCategory(Constants.CATEGORY_ALL);
        }

        // 3. Condition
        int selectedCondId = chipGroupCondition.getCheckedChipId();
        if (selectedCondId != -1) {
            Chip condChip = findViewById(selectedCondId);
            if (condChip != null) {
                criteria.setCondition(condChip.getText().toString());
            }
        } else {
            criteria.setCondition("All");
        }

        // 4. Prices
        String minStr = etMinPrice.getText().toString().trim();
        String maxStr = etMaxPrice.getText().toString().trim();

        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (!minStr.isEmpty()) {
                minPrice = Double.parseDouble(minStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (!maxStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxStr);
            }
        } catch (NumberFormatException ignored) {}

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            Toast.makeText(this, "Minimum price cannot be greater than Maximum price", Toast.LENGTH_SHORT).show();
            return;
        }

        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);

        // 5. Sort By
        int selectedSortId = chipGroupSort.getCheckedChipId();
        if (selectedSortId == R.id.chipSortPriceAsc) {
            criteria.setSortBy(FilterCriteria.SORT_PRICE_LOW_TO_HIGH);
        } else if (selectedSortId == R.id.chipSortPriceDesc) {
            criteria.setSortBy(FilterCriteria.SORT_PRICE_HIGH_TO_LOW);
        } else {
            criteria.setSortBy(FilterCriteria.SORT_NEWEST);
        }

        // Return result
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_FILTER_CRITERIA, criteria);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
