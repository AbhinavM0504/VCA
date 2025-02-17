package com.vivo.vivorajonboarding;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.android.volley.VolleyLog.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.vivo.vivorajonboarding.api.ApiService;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.fragment.EducationCardFragment;
import com.vivo.vivorajonboarding.fragment.NomineeCardFragment;
import com.vivo.vivorajonboarding.model.ApiResponse2;
import com.vivo.vivorajonboarding.model.NomineeCard;
import com.vivo.vivorajonboarding.model.NomineeSubmissionRequest;
import com.vivo.vivorajonboarding.model.UserModel;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NominationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private NomineePagerAdapter pagerAdapter;
    private List<NomineeCard> nomineeCards;
    private Set<String> selectedRelations;
    private static final int MAX_NOMINEES = 4;
    private ImageView swipe_icon;
    private ApiService apiService;
    private String userId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nomination);

        apiService = RetrofitClient.getInstance().getApi();
        // Get userId from shared preferences or intent

        checkUserLoggedIn();

        initializeViews();
        initializeToolbar();
        updateSwipeIconVisibility(0);
        setupViewPager();
        updateProgress();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        swipe_icon=findViewById(R.id.ic_next_icon);
        nomineeCards = new ArrayList<>();
        selectedRelations = new HashSet<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving nomination details...");
        progressDialog.setCancelable(false);

        // Add first nominee card
        nomineeCards.add(new NomineeCard());
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

    private void setupViewPager() {
        pagerAdapter = new NomineePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new CardPageTransformer());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress();
                updateSwipeIconVisibility(position);
            }
        });
    }

    private void updateSwipeIconVisibility(int position) {
        if (swipe_icon != null) {
            // Show swipe icon only if there's a next page and current card is saved
            boolean hasNextPage = position < nomineeCards.size() - 1;
            NomineeCardFragment currentFragment = getCurrentFragment();
            boolean isCurrentCardSaved = currentFragment != null;

            swipe_icon.setVisibility(hasNextPage && isCurrentCardSaved ? View.VISIBLE : View.GONE);
        }
    }

    private NomineeCardFragment getCurrentFragment() {

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof NomineeCardFragment && fragment.isVisible()) {
                    return (NomineeCardFragment) fragment;
                }
            }
            return null;
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
            if (card != null && card.getPercentage() != null && !card.getPercentage().isEmpty()) {
                try {
                    float percentage = Float.parseFloat(card.getPercentage());
                    if (percentage >= 0 && percentage <= 100) {
                        total += percentage;
                    }
                } catch (NumberFormatException ignored) {
                    // Skip invalid percentages
                }
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
            updateSwipeIconVisibility(nomineeCards.size() - 1);
        }
    }

    public void removeCard(int position) {
        if (position >= 0 && position < nomineeCards.size()) {
            NomineeCard removedCard = nomineeCards.get(position);
            if (removedCard != null && removedCard.getRelation() != null) {
                selectedRelations.remove(removedCard.getRelation());
            }

            nomineeCards.remove(position);
            pagerAdapter.notifyDataSetChanged();

            // Update to previous position
            int newPosition = Math.max(0, position - 1);
            viewPager.setCurrentItem(newPosition, true);

            // Update progress and check submit button visibility
            updateProgress();
            checkAndAddSubmitButton(newPosition);
            updateSwipeIconVisibility(newPosition);

            // Notify all remaining fragments to update their positions
            for (int i = 0; i < nomineeCards.size(); i++) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
                if (fragment instanceof NomineeCardFragment) {
                    ((NomineeCardFragment) fragment).setPosition(i);
                }
            }
        }
    }

    public void checkAndAddSubmitButton(int position) {
        // First hide submit button on all fragments
        for (int i = 0; i < nomineeCards.size(); i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
            if (fragment instanceof NomineeCardFragment) {
                ((NomineeCardFragment) fragment).toggleSubmitButton(false);
            }
        }

        // Check if we're on the last card
        boolean isLastCard = position == nomineeCards.size() - 1;
        if (!isLastCard) {
            return;
        }

        // Calculate total percentage with proper error handling
        float totalPercentage = calculateTotalPercentage();
        boolean isExactly100 = Math.abs(totalPercentage - 100) < 0.01;

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + position);
        if (currentFragment instanceof NomineeCardFragment) {
            NomineeCardFragment fragment = (NomineeCardFragment) currentFragment;
            fragment.toggleSubmitButton(isExactly100);
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
        if (!validateNominations()) {
            Toast.makeText(this, "Please ensure all details are filled correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        NomineeSubmissionRequest request = new NomineeSubmissionRequest(userId, nomineeCards);
        Log.d("NominationSubmission", "Sending request: " + new Gson().toJson(request));

        Call<ApiResponse2> call = apiService.submitNominees(request);

        call.enqueue(new Callback<ApiResponse2>() {
            @Override
            public void onResponse(Call<ApiResponse2> call, Response<ApiResponse2> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse2 apiResponse = response.body();
                    Log.d("NominationSubmission", "Response received: " + new Gson().toJson(apiResponse));

                    if ("success".equals(apiResponse.getStatus())) {
                        Toast.makeText(NominationActivity.this,
                                "Nominations submitted successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showError(apiResponse.getMessage());
                    }
                } else {
                    Log.e("NominationSubmission", "Response failed: " + response.code() + " - " + response.message());
                    showError("Failed to submit nominations. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse2> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("NominationSubmission", "Network error: " + t.getMessage(), t);
                showError("Network error. Please check your connection and try again.");
            }
        });
    }

    private boolean validateNominations() {
        float totalPercentage = calculateTotalPercentage();
        if (Math.abs(totalPercentage - 100) >= 0.01) {
            return false;
        }

        for (NomineeCard card : nomineeCards) {
            if (!isCardValid(card)) {
                return false;
            }
        }

        return true;
    }

    private boolean isCardValid(NomineeCard card) {
        return card.getRelation() != null && !card.getRelation().isEmpty() &&
                card.getRelativeName() != null && !card.getRelativeName().isEmpty() &&
                card.getDateOfBirth() != null && !card.getDateOfBirth().isEmpty() &&
                card.getPercentage() != null && !card.getPercentage().isEmpty();
    }

    public List<NomineeCard> getNomineeCards() {
        return nomineeCards;
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}