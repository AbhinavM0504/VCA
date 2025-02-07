package com.vivo.vivorajonboarding;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.vivo.vivorajonboarding.model.Banner;

import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final int COLOR_CYAN = 0xFF2B2B2B;
    private static final int COLOR_BLUE = 0xFF2B2B2B;
    private static final long SLIDE_DELAY = 3000;
    private static final int ANIM_SLIDE_IN_RIGHT = R.anim.slide_in_right;
    private static final int ANIM_SLIDE_OUT_LEFT = R.anim.slide_out_left;

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private LinearLayout officeTourLayout, galleryLayout, somLayout, srmLayout;
    private TextView userName;
    private ViewPager2 bannerViewPager;
    private List<Banner> bannerList;
    private BannerAdapter bannerAdapter;
    private LinearLayout dotsIndicator;
    private ImageView[] dots;
    private Handler slideHandler;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerList != null && !bannerList.isEmpty() && bannerViewPager != null) {
                if (bannerViewPager.getCurrentItem() == bannerList.size() - 1) {
                    bannerViewPager.setCurrentItem(0, true);
                } else {
                    bannerViewPager.setCurrentItem(bannerViewPager.getCurrentItem() + 1, true);
                }
            }
            if (slideHandler != null) {
                slideHandler.postDelayed(this, SLIDE_DELAY);
            }
        }
    };

    private static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private final List<Banner> banners;

        BannerAdapter(List<Banner> banners) {
            this.banners = banners;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            Banner banner = banners.get(position);
            if (holder.itemView.getContext() != null && banner.getOfferImage() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(banner.getOfferImage())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(holder.bannerImage);
            }
        }

        @Override
        public int getItemCount() {
            return banners != null ? banners.size() : 0;
        }

        static class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView bannerImage;

            BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                bannerImage = itemView.findViewById(R.id.bannerImage);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        slideHandler = new Handler(Looper.getMainLooper());
        initializeViews();
        checkUserLoggedIn();
        setupClickListeners();
        setupBottomNavigation();
        setupFAB();
        setupDummyBanners();
    }

    private void initializeViews() {
        bannerViewPager = findViewById(R.id.bannerViewPager);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        officeTourLayout = findViewById(R.id.officeTourLayout);
        galleryLayout = findViewById(R.id.galleryLayout);
        somLayout = findViewById(R.id.somLayout);
        srmLayout = findViewById(R.id.srmLayout);
        userName = findViewById(R.id.userNameText);
    }

    private void checkUserLoggedIn() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn() && userName != null) {
            userName.setText(sessionManager.getUserName());
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupClickListeners() {
        if (officeTourLayout != null) {
            officeTourLayout.setOnClickListener(v -> openOfficeTour());
        }
        if (galleryLayout != null) {
            galleryLayout.setOnClickListener(v -> openGallery());
        }
        if (somLayout != null) {
            somLayout.setOnClickListener(v -> openSomStructure());
        }
        if (srmLayout != null) {
            srmLayout.setOnClickListener(v -> openSrmStructure());
        }
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

    private void setupDummyBanners() {
        bannerList = new ArrayList<>();

        Banner banner1 = new Banner();
        banner1.setId(1);
        banner1.setOfferImage("https://picsum.photos/800/600?random=1");

        Banner banner2 = new Banner();
        banner2.setId(2);
        banner2.setOfferImage("https://picsum.photos/800/600?random=2");

        Banner banner3 = new Banner();
        banner3.setId(3);
        banner3.setOfferImage("https://picsum.photos/800/600?random=3");

        bannerList.add(banner1);
        bannerList.add(banner2);
        bannerList.add(banner3);

        bannerAdapter = new BannerAdapter(bannerList);
        if (bannerViewPager != null) {
            bannerViewPager.setAdapter(bannerAdapter);
            setupDotsIndicator(bannerList.size());
            setCurrentDot(0);

            pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    setCurrentDot(position);
                }
            };
            bannerViewPager.registerOnPageChangeCallback(pageChangeCallback);
        }
    }

    private void setupDotsIndicator(int count) {
        if (dotsIndicator != null) {
            dotsIndicator.removeAllViews();
            dots = new ImageView[count];

            for (int i = 0; i < count; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot_inactive));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 0, 8, 0);
                dotsIndicator.addView(dots[i], params);
            }
        }
    }

    private void setCurrentDot(int position) {
        if (dots != null) {
            for (int i = 0; i < dots.length; i++) {
                if (dots[i] != null) {
                    dots[i].setImageDrawable(getResources().getDrawable(
                            i == position ? R.drawable.dot_active : R.drawable.dot_inactive
                    ));
                }
            }
        }
    }

    private void setupFAB() {
        if (fab != null) {
            fab.setOnClickListener(v -> navigateToUserDashboard());
        }
    }

    private void navigateToUserDashboard() {
        startActivityWithAnimation(new Intent(this, UserDashboardActivity.class));
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this);
            setupNavigationColors();
            bottomNavigationView.setSelectedItemId(R.id.nav_more);
        }
    }

    private void setupNavigationColors() {
        if (bottomNavigationView != null) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked}
            };

            int[] colors = new int[]{
                    COLOR_BLUE,
                    COLOR_CYAN
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);
            bottomNavigationView.setItemTextColor(colorStateList);
        }
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
        overridePendingTransition(ANIM_SLIDE_IN_RIGHT, ANIM_SLIDE_OUT_LEFT);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (slideHandler != null) {
            slideHandler.removeCallbacks(slideRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slideHandler != null) {
            slideHandler.postDelayed(slideRunnable, SLIDE_DELAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerViewPager != null && pageChangeCallback != null) {
            bannerViewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        }
        if (slideHandler != null) {
            slideHandler.removeCallbacksAndMessages(null);
            slideHandler = null;
        }
        bannerList = null;
        bannerAdapter = null;
        dots = null;
        pageChangeCallback = null;
    }
}