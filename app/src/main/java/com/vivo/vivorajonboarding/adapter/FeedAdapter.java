package com.vivo.vivorajonboarding.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.FeedPost;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private List<FeedPost> posts;

    public FeedAdapter(List<FeedPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedPost post = posts.get(position);

        // Set up carousel with the new images List<String>
        CarouselAdapter carouselAdapter = new CarouselAdapter(post.getImages());
        holder.imageCarousel.setAdapter(carouselAdapter);

        // Set up dot indicators
        new TabLayoutMediator(holder.dotsIndicator, holder.imageCarousel,
                (tab, position1) -> {
                    // You can customize the dot appearance here if needed
                }).attach();

        // Set the text fields using the new model's getters
        holder.postTitle.setText(post.getTitle());
        holder.postDescription.setText(post.getDescription());
        holder.postDate.setText(post.getRelativeTime());
    }


    public void updatePosts(List<FeedPost> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 imageCarousel;
        TabLayout dotsIndicator;
        TextView postTitle;
        TextView postDescription;
        TextView postDate;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCarousel = itemView.findViewById(R.id.imageCarousel);
            dotsIndicator = itemView.findViewById(R.id.dotsIndicator);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            postDate = itemView.findViewById(R.id.postDate);
        }
    }
}