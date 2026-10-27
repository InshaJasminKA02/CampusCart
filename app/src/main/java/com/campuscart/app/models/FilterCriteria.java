package com.campuscart.app.models;

import com.campuscart.app.utilities.Constants;

import java.io.Serializable;

/**
 * Filter and sorting criteria container for marketplace search.
 */
public class FilterCriteria implements Serializable {

    public static final String SORT_NEWEST = "NEWEST";
    public static final String SORT_PRICE_LOW_TO_HIGH = "PRICE_LOW_TO_HIGH";
    public static final String SORT_PRICE_HIGH_TO_LOW = "PRICE_HIGH_TO_LOW";

    private String query = "";
    private String category = Constants.CATEGORY_ALL;
    private String condition = "All";
    private Double minPrice = null;
    private Double maxPrice = null;
    private String sortBy = SORT_NEWEST;

    public FilterCriteria() {}

    public FilterCriteria(FilterCriteria other) {
        if (other != null) {
            this.query = other.query;
            this.category = other.category;
            this.condition = other.condition;
            this.minPrice = other.minPrice;
            this.maxPrice = other.maxPrice;
            this.sortBy = other.sortBy;
        }
    }

    public boolean hasActiveFilter() {
        return (!category.equalsIgnoreCase(Constants.CATEGORY_ALL))
                || (!condition.equalsIgnoreCase("All"))
                || (minPrice != null && minPrice > 0)
                || (maxPrice != null && maxPrice > 0)
                || (!query.trim().isEmpty())
                || (!sortBy.equals(SORT_NEWEST));
    }

    public void clear() {
        this.query = "";
        this.category = Constants.CATEGORY_ALL;
        this.condition = "All";
        this.minPrice = null;
        this.maxPrice = null;
        this.sortBy = SORT_NEWEST;
    }

    // Getters and Setters
    public String getQuery() {
        return query != null ? query : "";
    }

    public void setQuery(String query) {
        this.query = query != null ? query.trim() : "";
    }

    public String getCategory() {
        return category != null ? category : Constants.CATEGORY_ALL;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition != null ? condition : "All";
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSortBy() {
        return sortBy != null ? sortBy : SORT_NEWEST;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
