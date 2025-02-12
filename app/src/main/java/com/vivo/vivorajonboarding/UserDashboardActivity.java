package com.vivo.vivorajonboarding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;

public class UserDashboardActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    public static final String MyPREFERENCES = "prefs";

    // Constants for gradient colors
    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;

    // Views
    private NestedScrollView scrollView;
    private MaterialCardView personalDetailsCard,educationCard,nominationCard,experienceCard,insuranceCard,previewCard;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private View educationalInfoOverlay, nominationOverlay, experienceOverlay, insuranceOverlay, previewOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        initializeViews();
        setupOverlays();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        scrollView = findViewById(R.id.scrollView);
        personalDetailsCard = findViewById(R.id.basicInfo);
        educationCard=findViewById(R.id.educationalInfo);
        nominationCard=findViewById(R.id.nominationInfo);
        experienceCard=findViewById(R.id.experienceInfo);
        insuranceCard=findViewById(R.id.InsuranceNomination);
        previewCard=findViewById(R.id.PreviewDetails);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        educationalInfoOverlay = findViewById(R.id.educationalInfoOverlay);
        nominationOverlay = findViewById(R.id.nominationOverlay);
        experienceOverlay = findViewById(R.id.experienceOverlay);
        insuranceOverlay = findViewById(R.id.insuranceOverlay);
        previewOverlay = findViewById(R.id.previewOverlay);
    }
    private void setupOverlays() {
        // Only the basic info card will be clickable initially
        educationCard.setEnabled(false);
        nominationCard.setEnabled(false);
        experienceCard.setEnabled(false);
        insuranceCard.setEnabled(false);
        previewCard.setEnabled(false);

        // Make overlays visible
        educationalInfoOverlay.setVisibility(View.VISIBLE);
        nominationOverlay.setVisibility(View.VISIBLE);
        experienceOverlay.setVisibility(View.VISIBLE);
        insuranceOverlay.setVisibility(View.VISIBLE);
        previewOverlay.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        personalDetailsCard.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, BasicInfoActivity.class);
            startActivity(intent);
        });

        // Add click listeners for locked cards to show toast message
        View.OnClickListener lockedCardListener = v ->
                Toast.makeText(this, "Complete Basic Info first", Toast.LENGTH_SHORT).show();

        educationalInfoOverlay.setOnClickListener(lockedCardListener);
        nominationOverlay.setOnClickListener(lockedCardListener);
        experienceOverlay.setOnClickListener(lockedCardListener);
        insuranceOverlay.setOnClickListener(lockedCardListener);
        previewOverlay.setOnClickListener(lockedCardListener);
    }

    public void unlockEducationalCard() {
        educationCard.setEnabled(true);
        educationalInfoOverlay.setVisibility(View.GONE);
        educationCard.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, EducationActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.getMenu().setGroupCheckable(0, true, false); // Disable visual selection

        // Set custom colors for text and icons
        setupNavigationColors();

        // Handle navigation click events
        bottomNavigationView.setOnItemSelectedListener(this);
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
        // Clear all visual selection
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setChecked(false);
        }
        Intent intent = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, LandingPageActivity.class);
        } else if (itemId == R.id.nav_feed) {
            intent = new Intent(this, FeedActivity.class);
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



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}