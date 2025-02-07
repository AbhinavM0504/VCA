package com.vivo.vivorajonboarding;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide; // Add this for image loading
import com.github.chrisbanes.photoview.PhotoView; // Import PhotoView
import com.vivo.vivorajonboarding.adapter.ImagesAdapter;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private RecyclerView imagesRecyclerView;
    private ImagesAdapter imagesAdapter;
    private View emptyStateView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout fragmentContainer;
    private PhotoView fullscreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        String albumName = getIntent().getStringExtra("albumName");
        List<String> images = getIntent().getStringArrayListExtra("images");
        setupToolbar(albumName);
        setupViews();
        setupRecyclerView(images);
        setupSwipeRefresh(images);
    }

    private void setupToolbar(String albumName) {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(albumName != null ? albumName : getString(R.string.default_album_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Set the back arrow color to white
            toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void setupViews() {
        emptyStateView = findViewById(R.id.emptyStateView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        fullscreenImage = findViewById(R.id.fullscreenImage);
    }

    private void setupRecyclerView(List<String> images) {
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Adding spacing between items in the grid
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        imagesRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacing, true));

        if (images == null || images.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            imagesAdapter = new ImagesAdapter(images, this::onImageClick);
            imagesRecyclerView.setAdapter(imagesAdapter);
        }
    }

    private void setupSwipeRefresh(List<String> images) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulate a refresh (e.g., fetching new data from a server)
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 1000); // Delay to simulate a network call
        });
    }

    private void onImageClick(String imageUrl, View sharedElement) {
        // Show the image overlay
        fragmentContainer.setVisibility(View.VISIBLE);
        fullscreenImage.setVisibility(View.VISIBLE);

        // Load the image into the fullscreen PhotoView using Glide
        Glide.with(this)
                .load(imageUrl)
                .into(fullscreenImage);

        // Set a click listener to close the overlay
        fragmentContainer.setOnClickListener(v -> closeImageOverlay());
    }

    private void closeImageOverlay() {
        // Hide the overlay and close it
        fragmentContainer.setVisibility(View.GONE);
        fullscreenImage.setVisibility(View.GONE);
    }
}
