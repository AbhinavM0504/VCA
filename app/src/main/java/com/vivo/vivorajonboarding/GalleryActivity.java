package com.vivo.vivorajonboarding;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vivo.vivorajonboarding.adapter.AlbumsAdapter;
import com.vivo.vivorajonboarding.model.AlbumModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter albumsAdapter;
    private List<AlbumModel> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setupToolbar();
        initializeData();
        setupRecyclerView();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(toolbar.getNavigationIcon()).setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeData() {
        albums = new ArrayList<>();
        // Dummy data
        List<String> workImages = Arrays.asList(
                "https://picsum.photos/200/300",
                "https://picsum.photos/300/400",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/300/400",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300",
                "https://picsum.photos/200/300"

        );
        List<String> eventsImages = Arrays.asList(
                "https://picsum.photos/400/500",
                "https://picsum.photos/500/600"
        );

        albums.add(new AlbumModel("Work Photos", workImages.get(0), workImages));
        albums.add(new AlbumModel("Events", eventsImages.get(0), eventsImages));
    }

    private void setupRecyclerView() {
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        albumsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        albumsAdapter = new AlbumsAdapter(albums, this::onAlbumClick);
        albumsRecyclerView.setAdapter(albumsAdapter);
    }

    private void onAlbumClick(AlbumModel album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("albumName", album.getAlbumName());
        intent.putStringArrayListExtra("images", new ArrayList<>(album.getImageUrls()));

        // Shared element transition
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }
}