package com.vivo.vivorajonboarding;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.fragment.ExperienceCardFragment;
import com.vivo.vivorajonboarding.model.ExperienceCard;
import com.vivo.vivorajonboarding.model.UserModel;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PastExperienceDetailsActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    public ExperiencePagerAdapter pagerAdapter;
    private List<ExperienceCard> experienceCards;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLoggedIn();
        setContentView(R.layout.activity_past_experience);
        initializeViews();
        initializeToolbar();

        setupViewPager();
        updateProgress();
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

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        experienceCards = new ArrayList<>();
        // Add first mandatory experience card
        experienceCards.add(new ExperienceCard(true));
    }

    private void setupViewPager() {
        pagerAdapter = new ExperiencePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new CardPageTransformer());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress();
            }
        });
    }

    private void updateProgress() {
        if (experienceCards.size() > 0) {
            int progress = ((viewPager.getCurrentItem() + 1) * 100) / experienceCards.size();
            progressIndicator.setProgress(progress);
        }
    }

    public void addNewExperienceCard() {
        ExperienceCard newCard = new ExperienceCard(false);
        experienceCards.add(newCard);
        pagerAdapter.updateData();
        viewPager.setCurrentItem(experienceCards.size() - 1, true);
        updateProgress();
    }

    public void removeCard(int position) {
        if (position >= 0 && position < experienceCards.size() &&
                !experienceCards.get(position).isMandatory() &&
                !experienceCards.get(position).isSubmitted()) {

            experienceCards.remove(position);
            pagerAdapter.updateData();
            viewPager.setCurrentItem(Math.max(0, position - 1), true);
            updateProgress();
        }
    }

    public List<ExperienceCard> getAllExperienceCards() {
        return new ArrayList<>(experienceCards);
    }

    public String getUserId() {
        return userId;
    }

    public class ExperiencePagerAdapter extends FragmentStateAdapter {
        private List<Fragment> fragments = new ArrayList<>();

        public ExperiencePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = ExperienceCardFragment.newInstance(
                    experienceCards.get(position),
                    position,
                    position == getItemCount() - 1
            );

            // Keep track of created fragments
            if (position >= fragments.size()) {
                fragments.add(fragment);
            } else {
                fragments.set(position, fragment);
            }

            return fragment;
        }

        @Override
        public int getItemCount() {
            return experienceCards.size();
        }

        public void refreshFragments() {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof ExperienceCardFragment && fragment.getView() != null) {
                    ((ExperienceCardFragment) fragment).setIsLast(i == getItemCount() - 1);
                    ((ExperienceCardFragment) fragment).updateButtonsVisibility(fragment.getView());
                }
            }
        }

        public void updateData() {
            notifyDataSetChanged();
            refreshFragments();
        }
    }

    public void refreshAllFragments() {
        if (pagerAdapter != null) {
            pagerAdapter.refreshFragments();
        }
    }


}
