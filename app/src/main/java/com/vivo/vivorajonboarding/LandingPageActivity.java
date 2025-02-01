package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class LandingPageActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private NestedScrollView scrollView;
    private FloatingActionButton scrollDownFab;
    private boolean isAtBottom = false;

    // Constants for gradient colors
    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        initializeViews();
        setupBottomNavigation();
        setupFAB();
        setupScrollBehavior();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        scrollView = findViewById(R.id.scrollView);
        scrollDownFab = findViewById(R.id.scrollDownFab);
    }

    private void setupScrollBehavior() {
        // Initially set the down arrow
        scrollDownFab.setRotation(0);

        scrollDownFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAtBottom) {
                    // Scroll to top when at bottom
                    scrollView.smoothScrollTo(0, 0);
                } else {
                    // Scroll to bottom when not at bottom
                    scrollView.smoothScrollTo(0, scrollView.getChildAt(0).getHeight());
                }
            }
        });

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {
                // Calculate if we're at the bottom
                int maxScroll = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                isAtBottom = scrollY >= maxScroll;

                // Animate the FAB rotation based on scroll position
                if (isAtBottom && scrollDownFab.getRotation() == 0) {
                    // Animate to up arrow when reaching bottom
                    scrollDownFab.animate().rotation(180).setDuration(200).start();
                } else if (!isAtBottom && scrollDownFab.getRotation() == 180) {
                    // Animate to down arrow when not at bottom
                    scrollDownFab.animate().rotation(0).setDuration(200).start();
                }

                // Show/hide FAB based on scroll direction
                if (scrollY > oldScrollY) {
                    // Scrolling down
                    scrollDownFab.show();
                } else if (scrollY < oldScrollY && scrollY < 100) {
                    // Near top while scrolling up
                    scrollDownFab.hide();
                }
            }
        });
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

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(this);
        setupNavigationColors();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
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
            return true;
        } else if (itemId == R.id.nav_feed) {
            intent = new Intent(this, FeedActivity.class);
        } else if (itemId == R.id.nav_notifications) {
            intent = new Intent(this, LandingPageActivity.class);
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