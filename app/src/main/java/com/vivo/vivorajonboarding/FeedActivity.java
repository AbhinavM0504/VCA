package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.vivo.vivorajonboarding.adapter.FeedAdapter;
import com.vivo.vivorajonboarding.model.FeedPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;
    private List<FeedPost> feedPosts;
    private FloatingActionButton fab;

    // Constants for gradient colors
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
        initializePosts();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        feedRecyclerView = findViewById(R.id.feedRecyclerView);
    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUserDashboard();
            }
        });
    }

    private void navigateToUserDashboard() {
        Intent intent = new Intent(this, UserDashboardActivity.class);
        startActivityWithAnimation(intent);
    }

    private void setupRecyclerView() {
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.addItemDecoration(new FeedItemSpacing(this));
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this);
        setupNavigationColors();

        // Set the active item
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

    private void initializePosts() {
        feedPosts = new ArrayList<>();
        addSamplePosts();
        feedAdapter = new FeedAdapter(feedPosts);
        feedRecyclerView.setAdapter(feedAdapter);
    }

    private void addSamplePosts() {
        feedPosts.add(new FeedPost(
                Arrays.asList(R.drawable.image1, R.drawable.image, R.drawable.image2),
                "Welcome to the Future!",
                "We're thrilled to have you join our innovative team. Get ready to embark on an exciting journey where your ideas will shape tomorrow.",
                "Just now"
        ));

        feedPosts.add(new FeedPost(
                Arrays.asList(R.drawable.image, R.drawable.image2),
                "Our Culture & Values",
                "Innovation, collaboration, and excellence define us. Discover how our values drive everything we do and how you can be part of this vision.",
                "2 hours ago"
        ));

        feedPosts.add(new FeedPost(
                Arrays.asList(R.drawable.image, R.drawable.image2, R.drawable.image1, R.drawable.image2),
                "Your Journey Starts Here",
                "Explore our comprehensive benefits package, learning opportunities, and growth paths designed to help you thrive.",
                "4 hours ago"
        ));

        // Additional sample posts
        for (int i = 0; i < 3; i++) {
            feedPosts.add(new FeedPost(
                    Arrays.asList(R.drawable.image1, R.drawable.image, R.drawable.image2),
                    "Welcome to the Future!",
                    "We're thrilled to have you join our innovative team. Get ready to embark on an exciting journey where your ideas will shape tomorrow.",
                    "Just now"
            ));
        }
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
            intent = new Intent(this, FeedActivity.class);
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