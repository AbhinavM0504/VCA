package com.vivo.vivorajonboarding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.fragment.NomineeCardFragment;
import com.vivo.vivorajonboarding.model.NomineeCard;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NominationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private NomineePagerAdapter pagerAdapter;
    private List<NomineeCard> nomineeCards;
    private Set<String> selectedRelations;
    private static final int MAX_NOMINEES = 4;
    private SlidingButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomination);

        initializeViews();
        initializeToolbar();
        setupViewPager();
        updateProgress();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        nomineeCards = new ArrayList<>();
        selectedRelations = new HashSet<>();
        // Add first nominee card
        nomineeCards.add(new NomineeCard());
    }
    private void initializeToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(toolbar.getNavigationIcon()).setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewPager() {
        pagerAdapter = new NomineePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new CardPageTransformer());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress();
                checkAndAddSubmitButton(position);
            }
        });
    }

    private void updateProgress() {
        if (nomineeCards.size() > 0) {
            int progress = ((viewPager.getCurrentItem() + 1) * 100) / nomineeCards.size();
            progressIndicator.setProgress(progress);
        }
    }

    public float calculateTotalPercentage() {
        float total = 0;
        for (NomineeCard card : nomineeCards) {
            try {
                total += Float.parseFloat(card.getPercentage());
            } catch (NumberFormatException | NullPointerException e) {
                // Skip invalid percentages
            }
        }
        return total;
    }

    public void addNewNomineeCard() {
        float totalPercentage = calculateTotalPercentage();
        if (totalPercentage >= 100) {
            // Show error message that 100% has been allocated
            Toast.makeText(this, "Total nomination percentage cannot exceed 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nomineeCards.size() < MAX_NOMINEES) {
            NomineeCard newCard = new NomineeCard();
            nomineeCards.add(newCard);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(nomineeCards.size() - 1, true);
            updateProgress();
            checkAndAddSubmitButton(nomineeCards.size() - 1);
        }
    }

    public void removeCard(int position) {
        if (position >= 0 && position < nomineeCards.size()) {
            NomineeCard removedCard = nomineeCards.get(position);
            if (removedCard.getRelation() != null && !removedCard.getRelation().isEmpty()) {
                selectedRelations.remove(removedCard.getRelation());
            }
            nomineeCards.remove(position);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(Math.max(0, position - 1), true);
            updateProgress();
            checkAndAddSubmitButton(Math.max(0, position - 1));
        }
    }

    private void checkAndAddSubmitButton(int position) {
        boolean isLastCard = position == nomineeCards.size() - 1;
        float totalPercentage = calculateTotalPercentage();

        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + position);

        if (currentFragment instanceof NomineeCardFragment) {
            NomineeCardFragment fragment = (NomineeCardFragment) currentFragment;
            fragment.toggleSubmitButton(isLastCard && totalPercentage == 100);
        }
    }

    public Set<String> getSelectedRelations() {
        return selectedRelations;
    }

    public void addSelectedRelation(String relation) {
        selectedRelations.add(relation);
    }

    public void removeSelectedRelation(String relation) {
        selectedRelations.remove(relation);
    }

    private class NomineePagerAdapter extends FragmentStateAdapter {
        public NomineePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return NomineeCardFragment.newInstance(nomineeCards.get(position), position);
        }

        @Override
        public int getItemCount() {
            return nomineeCards.size();
        }
    }

    public void onSubmitForm() {
        // Handle form submission
        // Validate all data and proceed
        boolean isValid = true;
        for (NomineeCard card : nomineeCards) {
            if (!isCardValid(card)) {
                isValid = false;
                break;
            }
        }

        if (isValid && calculateTotalPercentage() == 100) {
            // Process the nomination data
            // You can add your submission logic here
            Toast.makeText(this, "Nomination submitted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please ensure all details are filled correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCardValid(NomineeCard card) {
        return card.getRelation() != null && !card.getRelation().isEmpty() &&
                card.getRelativeName() != null && !card.getRelativeName().isEmpty() &&
                card.getDateOfBirth() != null && !card.getDateOfBirth().isEmpty() &&
                card.getPercentage() != null && !card.getPercentage().isEmpty();
    }
}