package com.vivo.vivorajonboarding;

import static com.android.volley.VolleyLog.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.vivo.vivorajonboarding.api.ApiService;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.fragment.InsuranceNomineeCardFragment;
import com.vivo.vivorajonboarding.model.ApiResponse1;
import com.vivo.vivorajonboarding.model.InsuranceNomineeCard;
import com.vivo.vivorajonboarding.model.NomineeData;
import com.vivo.vivorajonboarding.model.NomineeRequest;
import com.vivo.vivorajonboarding.model.UserModel;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsuranceNominationActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private NomineePagerAdapter pagerAdapter;
    private List<InsuranceNomineeCard> nomineeCards;
    private Set<String> selectedRelations;
    private static final int MAX_NOMINEES = 4;  // Maximum number of nominees allowed
    private boolean hasChildren = false;
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_nomination);
        initializeViews();
        apiService = RetrofitClient.getInstance().getApi();
        initializeToolbar();
        checkUserLoggedIn();
        setupViewPager();
        updateProgress();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        progressIndicator = findViewById(R.id.stepperProgress);
        nomineeCards = new ArrayList<>();
        selectedRelations = new HashSet<>();
        nomineeCards.add(new InsuranceNomineeCard());
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
                updateSubmitButtonVisibility();
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
            // Hide submit button on current last card
            updateSubmitButtonForPosition(nomineeCards.size() - 1, false);

            InsuranceNomineeCard newCard = new InsuranceNomineeCard();
            nomineeCards.add(newCard);
            pagerAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(nomineeCards.size() - 1, true);
            updateProgress();

            // Show submit button on new last card
            updateSubmitButtonForPosition(nomineeCards.size() - 1, true);
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

            // Calculate new position after removal
            int newPosition = Math.max(0, position - 1);
            viewPager.setCurrentItem(newPosition, true);
            updateProgress();

            // Update submit button visibility for all cards
            updateSubmitButtonVisibility();
        }
    }

    private void updateSubmitButtonVisibility() {
        // Hide submit button on all cards except the last one
        for (int i = 0; i < nomineeCards.size(); i++) {
            updateSubmitButtonForPosition(i, i == nomineeCards.size() - 1);
        }
    }

    private void updateSubmitButtonForPosition(int position, boolean showSubmitButton) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
        if (fragment instanceof InsuranceNomineeCardFragment) {
            InsuranceNomineeCardFragment nomineeFragment = (InsuranceNomineeCardFragment) fragment;
            nomineeFragment.toggleSubmitButton(showSubmitButton);
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
            uploadNomineeData();
        } else {
            Toast.makeText(this, "Please ensure all details are filled correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNomineeData() {

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting nomination data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Prepare nominee data
        List<NomineeData> nomineeDataList = new ArrayList<>();

        for (InsuranceNomineeCard card : nomineeCards) {
            try {
                String relation = card.getRelation();
                String name = card.getRelativeName();
                String dob = card.getDateOfBirth();
                String aadhaarFront = getBase64Image(card.getAadhaarFrontImage());
                String aadhaarBack = getBase64Image(card.getAadhaarBackImage());

                Log.d("UPLOAD_DATA", "Relation: " + relation);
                Log.d("UPLOAD_DATA", "Name: " + name);
                Log.d("UPLOAD_DATA", "DOB: " + dob);
                Log.d("UPLOAD_DATA", "Aadhaar Front (Base64): " + aadhaarFront.substring(0, 50) + "..."); // Partial log
                Log.d("UPLOAD_DATA", "Aadhaar Back (Base64): " + aadhaarBack.substring(0, 50) + "...");

                nomineeDataList.add(new NomineeData(relation, name, dob, aadhaarFront, aadhaarBack));
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(this, "Error processing images", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create request object
        String userId = getUserId(); // Get from your session management
        NomineeRequest request = new NomineeRequest(userId, nomineeDataList);

        Log.d("UPLOAD_DATA", "User ID: " + userId);
        Log.d("UPLOAD_DATA", "Nominee Data List Size: " + nomineeDataList.size());

        // Make API call
        apiService.submitNominees(request).enqueue(new Callback<ApiResponse1>() {
            @Override
            public void onResponse(Call<ApiResponse1> call, Response<ApiResponse1> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse1 nomineeResponse = response.body();
                    Log.d("API_RESPONSE", "Success: " + nomineeResponse.isSuccess());
                    Log.d("API_RESPONSE", "Message: " + nomineeResponse.getMessage());

                    if (nomineeResponse.isSuccess()) {
                        Toast.makeText(InsuranceNominationActivity.this,
                                "Nomination submitted successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(InsuranceNominationActivity.this,
                                "Failed: " + nomineeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API_RESPONSE", "Server error: Response unsuccessful");
                    Toast.makeText(InsuranceNominationActivity.this,
                            "Server error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse1> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API_FAILURE", "Network error: " + t.getMessage());
                Toast.makeText(InsuranceNominationActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getBase64Image(String imageUri) throws IOException {
        Uri uri = Uri.parse(imageUri);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private String getUserId() {
        // Return user ID from SharedPreferences or your session management
       return userId;
    }

    private boolean isCardValid(InsuranceNomineeCard card) {
        return card.getRelation() != null && !card.getRelation().isEmpty() &&
                card.getRelativeName() != null && !card.getRelativeName().isEmpty() &&
                card.getDateOfBirth() != null && !card.getDateOfBirth().isEmpty() &&
                card.getAadhaarFrontImage() != null &&
                card.getAadhaarBackImage() != null;
    }
}