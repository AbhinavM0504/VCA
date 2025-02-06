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
import com.vivo.vivorajonboarding.fragment.InsuranceNomineeCardFragment;
import com.vivo.vivorajonboarding.fragment.NomineeCardFragment;
import com.vivo.vivorajonboarding.model.InsuranceNomineeCard;
import com.vivo.vivorajonboarding.model.NomineeCard;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class InsuranceNominationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private NomineePagerAdapter pagerAdapter;
    private List<InsuranceNomineeCard> nomineeCards;
    private Set<String> selectedRelations;
    private static final int MAX_NOMINEES = 4;  // Maximum number of nominees allowed
    private SlidingButton submitButton;
    private boolean hasChildren = false;  // Track if any children are added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_nomination);

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
        nomineeCards.add(new InsuranceNomineeCard());
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

    public void addNewNomineeCard() {
        if (nomineeCards.size() < MAX_NOMINEES) {
            InsuranceNomineeCard newCard = new InsuranceNomineeCard();
            nomineeCards.add(newCard);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(nomineeCards.size() - 1, true);
            updateProgress();
            checkAndAddSubmitButton(nomineeCards.size() - 1);
        } else {
            Toast.makeText(this, "Maximum number of nominees reached", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean canAddChildren() {
        return !hasChildren || (hasChildren && getChildrenCount() < 2);
    }

    private int getChildrenCount() {
        int count = 0;
        for (InsuranceNomineeCard card : nomineeCards) {
            String relation = card.getRelation();
            if (relation != null && (relation.equals("Son") || relation.equals("Daughter"))) {
                count++;
            }
        }
        return count;
    }

    public void setHasChildren(boolean value) {
        this.hasChildren = value;
    }

    public void removeCard(int position) {
        if (position >= 0 && position < nomineeCards.size()) {
            InsuranceNomineeCard removedCard = nomineeCards.get(position);
            if (removedCard.getRelation() != null && !removedCard.getRelation().isEmpty()) {
                String relation = removedCard.getRelation();
                selectedRelations.remove(relation);
                if (relation.equals("Son") || relation.equals("Daughter")) {
                    if (getChildrenCount() <= 1) {
                        hasChildren = false;
                    }
                }
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
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + position);

        if (currentFragment instanceof InsuranceNomineeCardFragment) {
            InsuranceNomineeCardFragment fragment = (InsuranceNomineeCardFragment) currentFragment;
            fragment.toggleSubmitButton(isLastCard);
        }
    }

    public Set<String> getSelectedRelations() {
        return selectedRelations;
    }

    public void addSelectedRelation(String relation) {
        if (relation.equals("Son") || relation.equals("Daughter")) {
            hasChildren = true;
        }
        selectedRelations.add(relation);
    }

    public void removeSelectedRelation(String relation) {
        selectedRelations.remove(relation);
        if ((relation.equals("Son") || relation.equals("Daughter")) && getChildrenCount() == 0) {
            hasChildren = false;
        }
    }

    private class NomineePagerAdapter extends FragmentStateAdapter {
        public NomineePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return InsuranceNomineeCardFragment.newInstance(nomineeCards.get(position), position);
        }

        @Override
        public int getItemCount() {
            return nomineeCards.size();
        }
    }

    public void onSubmitForm() {
        boolean isValid = true;
        for (InsuranceNomineeCard card : nomineeCards) {
            if (!isCardValid(card)) {
                isValid = false;
                break;
            }
        }

        if (isValid) {
            // Process the nomination data
            Toast.makeText(this, "Nomination submitted successfully!", Toast.LENGTH_SHORT).show();
            // Add your API call or data processing logic here
        } else {
            Toast.makeText(this, "Please ensure all details are filled correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCardValid(InsuranceNomineeCard card) {
        return card.getRelation() != null && !card.getRelation().isEmpty() &&
                card.getRelativeName() != null && !card.getRelativeName().isEmpty() &&
                card.getDateOfBirth() != null && !card.getDateOfBirth().isEmpty() &&
                card.getAadhaarFrontImage() != null &&
                card.getAadhaarBackImage() != null;
    }
}