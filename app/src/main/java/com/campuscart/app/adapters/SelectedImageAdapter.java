package com.campuscart.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.campuscart.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying thumbnails of images selected for a new product listing,
 * with remove capabilities and cover photo indication.
 */
public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder> {

    public interface OnImageRemoveListener {
        void onImageRemove(Uri uri, int position);
    }

    private final List<Uri> imageUris;
    private final OnImageRemoveListener removeListener;

    public SelectedImageAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
        this.imageUris = imageUris != null ? imageUris : new ArrayList<>();
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);
        Context context = holder.itemView.getContext();

        holder.tvPrimaryBadge.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_placeholder_image)
                .error(R.drawable.ic_placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.ivSelectedThumbnail);

        holder.btnRemoveImage.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && removeListener != null) {
                removeListener.onImageRemove(uri, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void addImages(List<Uri> newUris, int maxAllowed) {
        if (newUris == null) return;
        for (Uri uri : newUris) {
            if (imageUris.size() < maxAllowed && !imageUris.contains(uri)) {
                imageUris.add(uri);
            }
        }
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (position >= 0 && position < imageUris.size()) {
            imageUris.remove(position);
            notifyDataSetChanged();
        }
    }

    public List<Uri> getImageUris() {
        return imageUris;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivSelectedThumbnail;
        final ImageButton btnRemoveImage;
        final TextView tvPrimaryBadge;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivSelectedThumbnail = itemView.findViewById(R.id.ivSelectedThumbnail);
            this.btnRemoveImage = itemView.findViewById(R.id.btnRemoveImage);
            this.tvPrimaryBadge = itemView.findViewById(R.id.tvPrimaryBadge);
        }
    }
}
