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
    private MaterialCardView personalDetailsCard;
    private MaterialCardView documentsCard;
    private MaterialCardView nomineeCard;
    private MaterialCardView policeCard;
    private MaterialCardView otherDocsCard;
    private MaterialCardView uploadCard;
    private CircularProgressIndicator progressIndicator;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initializeViews() {
        scrollView = findViewById(R.id.scrollView);
        personalDetailsCard = findViewById(R.id.personalDetailsCard);
        documentsCard = findViewById(R.id.documentsCard);
        nomineeCard = findViewById(R.id.nomineeCard);
        policeCard = findViewById(R.id.policeCard);
        otherDocsCard = findViewById(R.id.otherDocsCard);
        uploadCard = findViewById(R.id.uploadCard);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
    }

    private void setupClickListeners() {
        personalDetailsCard.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, PersonalActivity.class);
            startActivity(intent);
        });

        documentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, DocumentActivity.class);
            startActivity(intent);
        });

        nomineeCard.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, NomineeActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(v -> showAddOptionsDialog());
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

    private void showAddOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New");
        String[] options = {"Document", "Personal Detail", "Other"};
        builder.setItems(options, (dialog, which) -> {
            Intent intent;
            switch (which) {
                case 0:
                    intent = new Intent(this, DocumentActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(this, PersonalActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(this, QualificationActivity.class);
                    startActivity(intent);
                    break;
            }
        });
        builder.show();
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