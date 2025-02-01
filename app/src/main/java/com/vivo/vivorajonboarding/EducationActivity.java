package com.vivo.vivorajonboarding;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.fragment.EducationCardFragment;
import com.vivo.vivorajonboarding.model.EducationCard;
import java.util.ArrayList;
import java.util.List;

public class EducationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private EducationPagerAdapter pagerAdapter;
    private List<EducationCard> educationCards;
    private static final String[] EDUCATION_LEVELS = {"12th", "Graduation", "Post Graduation", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        initializeViews();
        setupViewPager();
        updateProgress();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        educationCards = new ArrayList<>();
        // Add mandatory 10th education card
        educationCards.add(new EducationCard("10th", true));
    }

    private void setupViewPager() {
        pagerAdapter = new EducationPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress();
            }
        });
    }

    private void updateProgress() {
        if (educationCards.size() > 0) {
            int progress = ((viewPager.getCurrentItem() + 1) * 100) / educationCards.size();
            progressIndicator.setProgress(progress);
        }
    }

    public void addNewEducationCard() {
        int nextIndex = educationCards.size() - 1;
        if (nextIndex < EDUCATION_LEVELS.length) {
            EducationCard newCard = new EducationCard(EDUCATION_LEVELS[nextIndex], false);
            educationCards.add(newCard);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(educationCards.size() - 1, true);
            updateProgress();
        }
    }

    public void removeCard(int position) {
        if (position >= 0 && position < educationCards.size() && !educationCards.get(position).isMandatory()) {
            educationCards.remove(position);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(Math.max(0, position - 1), true);
            updateProgress();
        }
    }

    private class EducationPagerAdapter extends FragmentStateAdapter {
        public EducationPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return EducationCardFragment.newInstance(educationCards.get(position));
        }

        @Override
        public int getItemCount() {
            return educationCards.size();
        }
    }
}