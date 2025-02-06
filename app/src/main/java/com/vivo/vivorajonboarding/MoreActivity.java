package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.vivo.vivorajonboarding.model.BannerModel;

import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private LinearLayout promotionalBannersContainer,officeTourLayout,galleryLayout,somLayout,srmLayout;

    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setupFAB();
        setupDummyBanners();
    }

    private void setupClickListeners() {
        officeTourLayout.setOnClickListener(v -> {
            openOfficeTour();
        });
        galleryLayout.setOnClickListener(v -> {
            openGallery();
        });
        somLayout.setOnClickListener(v -> {
            openSomStructure();
        });
        srmLayout.setOnClickListener(v -> {
            openSrmStructure();
        });
    }

    private void openSrmStructure() {
        startActivity(new Intent(this, BasicInfoActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openSomStructure() {
        startActivity(new Intent(this, SomActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openGallery() {
        startActivity(new Intent(this, GalleryActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openOfficeTour() {
        startActivity(new Intent(this, OfficeTourActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void initializeViews() {
        promotionalBannersContainer = findViewById(R.id.promotionalBannersContainer);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        officeTourLayout=findViewById(R.id.officeTourLayout);
        galleryLayout=findViewById(R.id.galleryLayout);
        somLayout=findViewById(R.id.somLayout);
        srmLayout=findViewById(R.id.srmLayout);
    }

    private void setupPromotionalBanners(List<BannerModel> banners) {
        promotionalBannersContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (BannerModel banner : banners) {
            View bannerView = inflater.inflate(R.layout.item_promotional_banner, promotionalBannersContainer, false);

            ImageView bannerImage = bannerView.findViewById(R.id.bannerImage);
            TextView bannerDetails = bannerView.findViewById(R.id.bannerDetails);
            TextView bannerTitle = bannerView.findViewById(R.id.bannerTitle);
            TextView bannerBadge = bannerView.findViewById(R.id.badgeText);
            MaterialButton bannerCta = bannerView.findViewById(R.id.bannerCta);
            bannerTitle.setText("Special Offer");
            bannerBadge.setVisibility(View.VISIBLE);
            bannerBadge.setText("NEW");
            bannerCta.setText("Shop Now");

            // Load banner image using Glide
            Glide.with(this)
                    .load(banner.getOfferImage())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(bannerImage);

            bannerDetails.setText(banner.getOfferDetails());

            // Add click listener
            bannerView.setOnClickListener(v -> {
                // Handle banner click
            });

            promotionalBannersContainer.addView(bannerView);
        }
    }

    private void setupDummyBanners() {
        List<BannerModel> banners = new ArrayList<>();

        // Creating dummy banners with correct properties
        BannerModel banner1 = new BannerModel();
        banner1.setOfferImage("https://picsum.photos/200/300");
        banner1.setOfferDetails("Enjoy 50% off on your first order!");

        BannerModel banner2 = new BannerModel();
        banner2.setOfferImage("https://picsum.photos/200/300");
        banner2.setOfferDetails("Limited time offer: Buy 1 Get 1 Free!");

        BannerModel banner3 = new BannerModel();
        banner3.setOfferImage("https://picsum.photos/200/300");
        banner3.setOfferDetails("Exclusive discounts on Vivo accessories.");

        // Adding banners to the list
        banners.add(banner1);
        banners.add(banner2);
        banners.add(banner3);

        // Passing the list to the setup method
        setupPromotionalBanners(banners);
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
        bottomNavigationView.setSelectedItemId(R.id.nav_more);
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
            intent = new Intent(this, FeedActivity.class);
        } else if (itemId == R.id.nav_notifications) {
            intent = new Intent(this, Notifications.class);
        } else if (itemId == R.id.nav_more) {
            return true;
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
