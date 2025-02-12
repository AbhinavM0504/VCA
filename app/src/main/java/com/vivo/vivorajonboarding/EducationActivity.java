package com.vivo.vivorajonboarding;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.fragment.EducationCardFragment;
import com.vivo.vivorajonboarding.model.EducationCard;
import com.vivo.vivorajonboarding.model.UserModel;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EducationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private EducationPagerAdapter pagerAdapter;
    private List<EducationCard> educationCards;
    private static final String[] EDUCATION_LEVELS = {"10th", "12th", "Graduation", "Post Graduation", "Other"};
    private ImageView swipe_icon;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        checkUserLoggedIn();
        initializeViews();
        initializeToolbar();
        setupViewPager();
        updateProgress();
        updateSwipeIconVisibility(0); // Initialize swipe icon visibility
    }

    private void checkUserLoggedIn() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn() ) {
            UserModel user = sessionManager.getUserDetails();
            userId=(user.getUserid());
            Log.d(TAG, "\n======= Form Submission Data ======="+userId);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        educationCards = new ArrayList<>();
        swipe_icon = findViewById(R.id.ic_next_icon);

        // Only add 10th initially
        educationCards.add(new EducationCard("10th", true));
    }

    // New method to update swipe icon visibility
    private void updateSwipeIconVisibility(int position) {
        if (swipe_icon != null) {
            // Show swipe icon only if there's a next page and current card is saved
            boolean hasNextPage = position < educationCards.size() - 1;
            EducationCardFragment currentFragment = getCurrentFragment();
            boolean isCurrentCardSaved = currentFragment != null && currentFragment.isDataSaved();

            swipe_icon.setVisibility(hasNextPage && isCurrentCardSaved ? View.VISIBLE : View.GONE);
        }
    }

    // Helper method to get current fragment
    private EducationCardFragment getCurrentFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof EducationCardFragment && fragment.isVisible()) {
                return (EducationCardFragment) fragment;
            }
        }
        return null;
    }

    public void addNewEducationCard() {
        int nextIndex = educationCards.size();
        if (nextIndex < EDUCATION_LEVELS.length) {
            String nextLevel = EDUCATION_LEVELS[nextIndex];
            EducationCard newCard = new EducationCard(nextLevel, false);
            educationCards.add(newCard);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(educationCards.size() - 1, true);
            updateProgress();
            notifyFragmentsToUpdateButtons();
            updateSwipeIconVisibility(educationCards.size() - 1); // Update swipe icon when adding new card
        }
    }

    private void initializeToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hides the app name
            Objects.requireNonNull(toolbar.getNavigationIcon()).setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void setupViewPager() {
        pagerAdapter = new EducationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new CardPageTransformer());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress();
                notifyFragmentsToUpdateButtons();
                updateSwipeIconVisibility(position); // Update swipe icon visibility on page change
            }
        });
    }

    private void updateProgress() {
        if (!educationCards.isEmpty()) {
            int progress = ((viewPager.getCurrentItem() + 1) * 100) / educationCards.size();
            progressIndicator.setProgress(progress);
        }
    }

    public void enableAddButton() {
        // Called when current card is successfully saved
        if (educationCards.size() < EDUCATION_LEVELS.length) {
            findViewById(R.id.addButton).setVisibility(View.VISIBLE);
        }
        updateSwipeIconVisibility(viewPager.getCurrentItem()); // Update swipe icon when card is saved
    }

    public void removeCard(int position) {
        if (position >= 0 && position < educationCards.size() && !educationCards.get(position).isMandatory()) {
            educationCards.remove(position);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(Math.max(0, position - 1), true);
            updateProgress();
            notifyFragmentsToUpdateButtons();
            updateSwipeIconVisibility(Math.max(0, position - 1)); // Update swipe icon when removing card
        }
    }

    public boolean isLastCard(int position) {
        return position == educationCards.size() - 1;
    }

    private void notifyFragmentsToUpdateButtons() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof EducationCardFragment) {
                ((EducationCardFragment) fragment).updateButtonVisibility();
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    private class EducationPagerAdapter extends FragmentStateAdapter {
        public EducationPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return EducationCardFragment.newInstance(educationCards.get(position), position);
        }

        @Override
        public int getItemCount() {
            return educationCards.size();
        }
    }
}