package com.vivo.vivorajonboarding;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.vivo.vivorajonboarding.adapter.AlbumsAdapter;
import com.vivo.vivorajonboarding.model.AlbumModel;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.model.ApiResponseAlbum;
import com.vivo.vivorajonboarding.model.ApiResponseImages;
import com.vivo.vivorajonboarding.model.ImageModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView albumsRecyclerView;
    private AlbumsAdapter albumsAdapter;
    private List<AlbumModel> albums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setupToolbar();
        setupRecyclerView();
        fetchAlbums();
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

    private void setupRecyclerView() {
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        albumsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        albumsAdapter = new AlbumsAdapter(albums, this::onAlbumClick);
        albumsRecyclerView.setAdapter(albumsAdapter);
    }

    private void fetchAlbums() {
        RetrofitClient.getInstance().getApi().getAlbums().enqueue(new Callback<ApiResponseAlbum>() {
            @Override
            public void onResponse(Call<ApiResponseAlbum> call, Response<ApiResponseAlbum> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseAlbum apiResponse = response.body();
                    if ("success".equals(apiResponse.getStatus())) {
                        albums.clear();
                        albums.addAll(apiResponse.getData());
                        albumsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(GalleryActivity.this,
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseAlbum> call, Throwable t) {
                Toast.makeText(GalleryActivity.this,
                        "Failed to fetch albums: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAlbumClick(AlbumModel album) {
        RetrofitClient.getInstance().getApi().getAlbumImages(album.getAlbumId())
                .enqueue(new Callback<ApiResponseImages>() {
                    @Override
                    public void onResponse(Call<ApiResponseImages> call, Response<ApiResponseImages> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponseImages apiResponse = response.body();
                            if ("success".equals(apiResponse.getStatus())) {
                                ArrayList<String> imageUrls = new ArrayList<>();
                                for (ImageModel image : apiResponse.getData()) {
                                    imageUrls.add(image.getImageUrl());
                                }

                                // Launch AlbumActivity with the fetched images
                                Intent intent = new Intent(GalleryActivity.this, AlbumActivity.class);
                                intent.putExtra("albumId", album.getAlbumId());
                                intent.putExtra("albumName", album.getAlbumName());
                                intent.putStringArrayListExtra("images", imageUrls);

                                ActivityOptions options = ActivityOptions
                                        .makeSceneTransitionAnimation(GalleryActivity.this);
                                startActivity(intent, options.toBundle());
                            } else {
                                Toast.makeText(GalleryActivity.this,
                                        apiResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseImages> call, Throwable t) {
                        Toast.makeText(GalleryActivity.this,
                                "Failed to fetch album images: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}