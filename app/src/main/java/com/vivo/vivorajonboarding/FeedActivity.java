package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.vivo.vivorajonboarding.adapter.FeedAdapter;
import com.vivo.vivorajonboarding.model.FeedPost;
import com.vivo.vivorajonboarding.model.ApiResponse;
import com.vivo.vivorajonboarding.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initializeViews();
        setupRecyclerView();
        setupBottomNavigation();
        setupFAB();
        setupSwipeRefresh();
        loadFeedData();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        feedRecyclerView = findViewById(R.id.feedRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.addItemDecoration(new FeedItemSpacing(this));
        // Initialize adapter with empty list
        feedAdapter = new FeedAdapter(new ArrayList<>());
        feedRecyclerView.setAdapter(feedAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadFeedData);
    }

    private void loadFeedData() {
        RetrofitClient.getInstance()
                .getApi()
                .getFeed()
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if ("success".equals(apiResponse.getStatus())) {
                                // Use the updatePosts method from adapter instead of directly manipulating the list
                                feedAdapter.updatePosts(apiResponse.getData());
                            } else {
                                showError("Error: " + apiResponse.getMessage());
                            }
                        } else {
                            showError("Error loading feed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupFAB() {
        fab.setOnClickListener(v -> navigateToUserDashboard());
    }

    private void navigateToUserDashboard() {
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivityWithAnimation(intent);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this);
        setupNavigationColors();
        bottomNavigationView.setSelectedItemId(R.id.nav_feed);
    }

    private void setupNavigationColors() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { -android.R.attr.state_checked }
        };

        int[] colors = new int[] {
                COLOR_BLUE,
                COLOR_CYAN
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigationView.setItemTextColor(colorStateList);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, LandingPageActivity.class);
        } else if (itemId == R.id.nav_feed) {
            return true; // Already in FeedActivity
        } else if (itemId == R.id.nav_notifications) {
            intent = new Intent(this, Notifications.class);
        } else if (itemId == R.id.nav_more) {
            intent = new Intent(this, MoreActivity.class);
        }

        if (intent != null) {
            startActivityWithAnimation(intent);
            return true;
        }

        return false;
    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}