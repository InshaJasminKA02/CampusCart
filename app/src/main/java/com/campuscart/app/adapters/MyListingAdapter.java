package com.campuscart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.campuscart.app.R;
import com.campuscart.app.models.Product;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for the seller's own My Listings screen.
 * Displays each listing with its thumbnail, title, price, category, status badge,
 * and three seller actions: toggle sold/available, edit, and delete.
 *
 * Uses the existing item_my_listing.xml layout (created in Phase 4 prep).
 */
public class MyListingAdapter extends RecyclerView.Adapter<MyListingAdapter.ViewHolder> {

    /**
     * Listener interface for the three seller actions exposed on each card.
     */
    public interface OnListingActionListener {
        /** Called when the card body (not an action button) is tapped. */
        void onCardClick(Product product);

        /** Called when the "Mark as Sold" / "Mark as Available" toggle is pressed. */
        void onToggleSoldStatus(Product product, int position);

        /** Called when the "Edit" button is pressed. */
        void onEditClick(Product product, int position);

        /** Called when the delete icon is pressed. */
        void onDeleteClick(Product product, int position);
    }

    private final List<Product> listings;
    private final OnListingActionListener listener;

    public MyListingAdapter(List<Product> listings, OnListingActionListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = listings.get(position);
        Context context = holder.itemView.getContext();

        // ── Title ──────────────────────────────────────────────────────────────
        holder.tvTitle.setText(product.getTitle());

        // ── Price in INR ───────────────────────────────────────────────────────
        try {
            long priceLong = Math.round(product.getPrice());
            holder.tvPrice.setText(String.format(Locale.getDefault(), "₹%,d", priceLong));
        } catch (Exception e) {
            holder.tvPrice.setText("₹" + product.getPrice());
        }

        // ── Category ───────────────────────────────────────────────────────────
        String category = product.getCategory();
        holder.tvCategory.setText(category != null ? "• " + category : "");

        // ── Condition badge ────────────────────────────────────────────────────
        String condition = product.getCondition();
        if (condition != null && !condition.isEmpty()) {
            holder.tvConditionBadge.setText(condition);
            holder.tvConditionBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvConditionBadge.setVisibility(View.GONE);
        }

        // ── Sold / Available status badge ──────────────────────────────────────
        if (product.isSold()) {
            holder.tvStatusBadge.setText("Sold");
            holder.tvStatusBadge.setTextColor(context.getColor(R.color.badge_sold_text));
            holder.tvStatusBadge.setBackgroundColor(context.getColor(R.color.badge_sold_bg));
            holder.btnToggleSold.setText("Mark as Available");
        } else {
            holder.tvStatusBadge.setText("Available");
            holder.tvStatusBadge.setTextColor(context.getColor(R.color.badge_available_text));
            holder.tvStatusBadge.setBackgroundColor(context.getColor(R.color.badge_available_bg));
            holder.btnToggleSold.setText("Mark as Sold");
        }

        // ── Thumbnail image ────────────────────────────────────────────────────
        String imageUrl = product.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder_image)
                    .error(R.drawable.ic_placeholder_image)
                    .centerCrop()
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.ic_placeholder_image);
        }

        // ── Click listeners ────────────────────────────────────────────────────
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCardClick(product);
        });

        holder.btnToggleSold.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_ID) {
                    listener.onToggleSoldStatus(product, adapterPos);
                }
            }
        });

        holder.btnEditListing.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_ID) {
                    listener.onEditClick(product, adapterPos);
                }
            }
        });

        holder.btnDeleteListing.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_ID) {
                    listener.onDeleteClick(product, adapterPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings != null ? listings.size() : 0;
    }

    // ── ViewHolder ─────────────────────────────────────────────────────────────

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivThumbnail;
        final TextView tvStatusBadge;
        final TextView tvConditionBadge;
        final TextView tvTitle;
        final TextView tvPrice;
        final TextView tvCategory;
        final Button btnToggleSold;
        final Button btnEditListing;
        final ImageButton btnDeleteListing;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvConditionBadge = itemView.findViewById(R.id.tvConditionBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnToggleSold = itemView.findViewById(R.id.btnToggleSold);
            btnEditListing = itemView.findViewById(R.id.btnEditListing);
            btnDeleteListing = itemView.findViewById(R.id.btnDeleteListing);
        }
    }
}
