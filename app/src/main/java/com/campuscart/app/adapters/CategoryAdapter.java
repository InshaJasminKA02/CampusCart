package com.campuscart.app.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.campuscart.app.R;
import com.campuscart.app.models.Category;

import java.util.List;

/**
 * Adapter for horizontal category chip shortcuts.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        Context context = holder.itemView.getContext();
        boolean isSelected = (position == selectedPosition);

        holder.tvCategoryName.setText(category.getName());
        holder.ivCategoryIcon.setImageResource(category.getIconResId());

        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_category_chip_selected);
            holder.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.white)));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_category_chip);
            holder.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.primary_purple));
            holder.ivCategoryIcon.setImageTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.primary_purple)));
        }

        holder.itemView.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(category, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void setSelectedCategoryByName(String categoryName) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equalsIgnoreCase(categoryName)) {
                int prev = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View itemView;
        final ImageView ivCategoryIcon;
        final TextView tvCategoryName;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            this.tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
