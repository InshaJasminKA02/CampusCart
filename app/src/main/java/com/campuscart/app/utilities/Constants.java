package com.campuscart.app.utilities;

import com.campuscart.app.R;
import com.campuscart.app.models.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Global application constants, category definitions, and intent extras.
 */
public final class Constants {
    private Constants() {}

    // Categories
    public static final String CATEGORY_ALL = "All";
    public static final String CATEGORY_BOOKS = "Books";
    public static final String CATEGORY_ELECTRONICS = "Electronics";
    public static final String CATEGORY_ACCESSORIES = "Accessories";
    public static final String CATEGORY_CLOTHING = "Clothing";
    public static final String CATEGORY_STATIONERY = "Stationery";
    public static final String CATEGORY_SPORTS = "Sports";
    public static final String CATEGORY_FURNITURE = "Furniture";
    public static final String CATEGORY_OTHER = "Other";

    // Conditions
    public static final String CONDITION_BRAND_NEW = "Brand New";
    public static final String CONDITION_LIKE_NEW = "Like New";
    public static final String CONDITION_GOOD = "Good";
    public static final String CONDITION_FAIR = "Fair";

    // Intent Extras
    public static final String EXTRA_PRODUCT = "extra_product";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    public static final String EXTRA_SELLER_ID = "extra_seller_id";
    public static final String EXTRA_CONVERSATION_ID = "extra_conversation_id";

    // Firestore Collections
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_FAVOURITES = "favourites";
    public static final String COLLECTION_CONVERSATIONS = "conversations";
    public static final String COLLECTION_MESSAGES = "messages";
    public static final String COLLECTION_USERS = "users";

    /**
     * Get default category list with corresponding icons.
     */
    public static List<Category> getMarketplaceCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("all", CATEGORY_ALL, R.drawable.ic_category_all, true));
        list.add(new Category("books", CATEGORY_BOOKS, R.drawable.ic_book));
        list.add(new Category("electronics", CATEGORY_ELECTRONICS, R.drawable.ic_laptop));
        list.add(new Category("accessories", CATEGORY_ACCESSORIES, R.drawable.ic_watch));
        list.add(new Category("clothing", CATEGORY_CLOTHING, R.drawable.ic_shirt));
        list.add(new Category("stationery", CATEGORY_STATIONERY, R.drawable.ic_pencil));
        list.add(new Category("sports", CATEGORY_SPORTS, R.drawable.ic_sports));
        list.add(new Category("furniture", CATEGORY_FURNITURE, R.drawable.ic_chair));
        list.add(new Category("other", CATEGORY_OTHER, R.drawable.ic_category_all));
        return list;
    }
}
