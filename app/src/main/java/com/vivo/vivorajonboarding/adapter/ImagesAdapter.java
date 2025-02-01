package com.vivo.vivorajonboarding.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.vivo.vivorajonboarding.R;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private static final String TAG = "ImagesAdapter";
    private final List<String> images;
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl, View sharedElement);
    }

    public ImagesAdapter(List<String> images, OnImageClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = images.get(position);

        // Assign a unique transitionName for each ImageView
        holder.imageView.setTransitionName("image_" + position);

        holder.bind(imageUrl);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(imageUrl, holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final ProgressBar progressBar;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        void bind(String imageUrl) {
            // Check if imageUrl is valid
            if (imageUrl == null || imageUrl.isEmpty()) {
                Log.e(TAG, "Invalid image URL: " + imageUrl);
                progressBar.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.placeholder_image);
                return;
            }

            // Set progress bar visibility only when needed (show it before Glide starts loading)
            progressBar.setVisibility(View.VISIBLE);

            // Glide to load the image
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Use disk cache for better performance
                    .placeholder(R.drawable.placeholder_image) // Optional placeholder while loading
                    .error(R.drawable.placeholder_image) // Optional error image
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Image load failed, hide the progress bar and show error image
                            progressBar.setVisibility(View.GONE);
                            imageView.setImageResource(R.drawable.placeholder_image);
                            Log.e(TAG, "Image load failed: " + imageUrl, e);
                            return true; // Indicates that Glide handled the failure
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Image loaded successfully, hide the progress bar
                            progressBar.setVisibility(View.GONE);
                            return false; // Let Glide handle setting the image
                        }
                    })
                    .into(imageView);
        }
    }

}
