package com.campuscart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.campuscart.app.R;
import com.campuscart.app.models.Product;
import com.campuscart.app.utilities.Constants;
import com.campuscart.app.utilities.FavouritesSessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reusable adapter for displaying product cards in grids and lists.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavouriteClick(Product product, int position);
    }

    private final List<Product> productList;
    private final OnProductClickListener listener;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList != null ? productList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(product.getTitle());
        holder.tvCategory.setText(product.getCategory());

        // Price formatting in INR (₹)
        String priceFormatted;
        try {
            long priceLong = Math.round(product.getPrice());
            priceFormatted = "₹" + String.format(Locale.getDefault(), "%,d", priceLong);
        } catch (Exception e) {
            priceFormatted = "₹" + product.getPrice();
        }
        holder.tvPrice.setText(priceFormatted);

        // Seller
        if (product.getSellerName() != null && !product.getSellerName().isEmpty()) {
            holder.tvSeller.setText(product.getSellerName());
            holder.tvSeller.setVisibility(View.VISIBLE);
        } else {
            holder.tvSeller.setVisibility(View.GONE);
        }

        // Condition badge styling
        String condition = product.getCondition();
        if (condition != null) {
            holder.tvConditionBadge.setText(condition);
            holder.tvConditionBadge.setVisibility(View.VISIBLE);

            if (Constants.CONDITION_BRAND_NEW.equalsIgnoreCase(condition)) {
                holder.tvConditionBadge.setBackgroundResource(R.drawable.bg_badge_condition);
                holder.tvConditionBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_brand_new_text));
            } else if (Constants.CONDITION_LIKE_NEW.equalsIgnoreCase(condition)) {
                holder.tvConditionBadge.setBackgroundResource(R.drawable.bg_badge_condition);
                holder.tvConditionBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_like_new_text));
            } else if (Constants.CONDITION_GOOD.equalsIgnoreCase(condition)) {
                holder.tvConditionBadge.setBackgroundResource(R.drawable.bg_badge_condition);
                holder.tvConditionBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_good_text));
            } else {
                holder.tvConditionBadge.setBackgroundResource(R.drawable.bg_badge_condition);
                holder.tvConditionBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_fair_text));
            }
        } else {
            holder.tvConditionBadge.setVisibility(View.GONE);
        }

        // Favourite state from in-memory session manager
        boolean isFav = FavouritesSessionManager.isFavourite(product.getProductId());
        product.setFavourite(isFav);
        holder.btnFavourite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        // Product image loading via Glide
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_placeholder_image);
        }

        // Listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        holder.btnFavourite.setOnClickListener(v -> {
            boolean newState = FavouritesSessionManager.toggleFavourite(product.getProductId());
            product.setFavourite(newState);
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) {
                listener.onFavouriteClick(product, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        if (newProducts != null) {
            this.productList.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivProductImage;
        final TextView tvConditionBadge;
        final ImageButton btnFavourite;
        final TextView tvCategory;
        final TextView tvTitle;
        final TextView tvPrice;
        final TextView tvSeller;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivProductImage = itemView.findViewById(R.id.ivProductImage);
            this.tvConditionBadge = itemView.findViewById(R.id.tvConditionBadge);
            this.btnFavourite = itemView.findViewById(R.id.btnFavourite);
            this.tvCategory = itemView.findViewById(R.id.tvCategory);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvPrice = itemView.findViewById(R.id.tvPrice);
            this.tvSeller = itemView.findViewById(R.id.tvSeller);
        }
    }
}
