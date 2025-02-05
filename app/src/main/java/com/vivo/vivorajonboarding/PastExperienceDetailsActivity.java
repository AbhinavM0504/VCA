package com.vivo.vivorajonboarding;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.fragment.ExperienceCardFragment;
import com.vivo.vivorajonboarding.model.ExperienceCard;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PastExperienceDetailsActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private ExperiencePagerAdapter pagerAdapter;
    private List<ExperienceCard> experienceCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_experience);

        initializeViews();
        initializeToolbar();
        setupViewPager();
        updateProgress();
    }

    private void initializeToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(toolbar.getNavigationIcon())
                    .setTint(getResources().getColor(android.R.color.white));
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
        pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(experienceCards.size() - 1, true);
        updateProgress();
    }

    public void removeCard(int position) {
        if (position >= 0 && position < experienceCards.size() && !experienceCards.get(position).isMandatory()) {
            experienceCards.remove(position);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(Math.max(0, position - 1), true);
            updateProgress();
        }
    }

    private class ExperiencePagerAdapter extends FragmentStateAdapter {
        public ExperiencePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return ExperienceCardFragment.newInstance(experienceCards.get(position), position, position == getItemCount() - 1);
        }

        @Override
        public int getItemCount() {
            return experienceCards.size();
        }
    }
}
