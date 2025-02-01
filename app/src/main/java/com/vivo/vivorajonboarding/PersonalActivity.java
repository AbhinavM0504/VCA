package com.vivo.vivorajonboarding;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.adapter.StepperAdapter;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.fragment.StepperFragment;
import com.vivo.vivorajonboarding.model.PersonalDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PersonalActivity extends AppCompatActivity {
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private ViewPager2 stepperViewPager;
    private MaterialButton previousButton, nextButton;
    private LinearProgressIndicator stepperProgress;
    private Chip stepIndicator;
    private TextView stepTitle;
    private LoadingDialog loadingDialog;



    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    private StepperAdapter stepperAdapter;
    private final String[] stepTitles = {
            "Basic Information",
            "Bank Details",
            "Company Details",
            "Emergency Contact"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        initialization();
        setupToolbar();
        setupMotionLayout();
        setupStepperNavigation();
        setupAnimations();
        getPersonalData();
    }

    private void initialization() {
        // Basic stepper components
        stepperViewPager = findViewById(R.id.stepperViewPager);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        stepperProgress = findViewById(R.id.stepperProgress);
        stepIndicator = findViewById(R.id.stepIndicator);
        stepTitle = findViewById(R.id.stepTitle);
        loadingDialog = new LoadingDialog(this);

        // New UI components


        appBarLayout = findViewById(R.id.appBarLayout);

        toolbar = findViewById(R.id.toolbar);

        // Setup ViewPager
        stepperAdapter = new StepperAdapter(this);
        stepperViewPager.setAdapter(stepperAdapter);
        stepperViewPager.setUserInputEnabled(false); // Disable swipe

        // Setup progress indicator
        stepperProgress.setMax(100);
        updateStepperUI(0);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }




    }

    private void setupMotionLayout() {

    }

    private void setupAnimations() {

    }


    private void setupStepperNavigation() {
        previousButton.setOnClickListener(v -> navigateToPreviousStep());
        nextButton.setOnClickListener(v -> navigateToNextStep());

        stepperViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateStepperUI(position);
            }
        });
    }

    private void updateStepperUI(int position) {
        // Update progress with animation
        stepperProgress.setProgressCompat((position + 1) * 25, true);

        // Update step indicator and title with animation
        stepIndicator.setText(String.format("Step %d of 4", position + 1));
        stepTitle.setText(stepTitles[position]);

        // Update button states
        previousButton.setEnabled(position > 0);
        nextButton.setText(position == 3 ? "Submit" : "Next");

        // Update button icons
        previousButton.setIcon(getDrawable(R.drawable.animated_back_arrow));
        nextButton.setIcon(getDrawable(R.drawable.animated_forward_arrow));
    }

    private void navigateToPreviousStep() {
        int currentItem = stepperViewPager.getCurrentItem();
        if (currentItem > 0) {
            stepperViewPager.setCurrentItem(currentItem - 1);
        }
    }

    private void navigateToNextStep() {
        int currentItem = stepperViewPager.getCurrentItem();
        if (currentItem < 3) {
            // Validate current step before proceeding
            Fragment currentFragment = stepperAdapter.getFragment(currentItem);
            if (currentFragment instanceof StepperFragment && ((StepperFragment) currentFragment).isStepValid()) {
                stepperViewPager.setCurrentItem(currentItem + 1);
            }
        } else {
            // On last step, validate and submit
            Fragment lastFragment = stepperAdapter.getFragment(currentItem);
            if (lastFragment instanceof StepperFragment && ((StepperFragment) lastFragment).isStepValid()) {
                submitForm();
            }
        }
    }



    private void getPersonalData() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_PERSONAL_DATA_URL,
                response -> {
                    loadingDialog.hideDialog();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject object = jsonArray.getJSONObject(0);
                            PersonalDataModel data = parsePersonalData(object);
                            distributeDataToFragments(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showToast("Error parsing data");
                    }
                },
                error -> {
                    loadingDialog.hideDialog();
                    showToast(error.toString());
                }
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", Prevalent.currentOnlineUser.getUserid());
                return params;
            }
        };

        setupRequestRetryPolicy(stringRequest);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private PersonalDataModel parsePersonalData(JSONObject object) throws JSONException {

        PersonalDataModel personalDataModel = new PersonalDataModel();

        personalDataModel.setInitial(object.getString("initial"));
        personalDataModel.setName(object.getString("name"));
        personalDataModel.setEmail(object.getString("email"));
        personalDataModel.setNumber(object.getString("number"));
        personalDataModel.setFather_name(object.getString("father_name"));
        personalDataModel.setMother_name(object.getString("mother_name"));
        personalDataModel.setJoining_date(object.getString("joining_date"));
        personalDataModel.setDob(object.getString("dob"));
        personalDataModel.setGender(object.getString("gender"));
        personalDataModel.setBlood_group(object.getString("blood_group"));
        personalDataModel.setAadhar_no(object.getString("aadhar_no"));
        personalDataModel.setPan_no(object.getString("pan_no"));
        personalDataModel.setMarital_status(object.getString("marital_status"));
        personalDataModel.setPin_code(object.getString("pin_code"));
        personalDataModel.setPermanent_address(object.getString("permanent_address"));
        personalDataModel.setCurrent_address(object.getString("current_address"));

        personalDataModel.setBank_account_no(object.getString("bank_account_no"));
        personalDataModel.setBank_ifsc(object.getString("bank_ifsc"));

        personalDataModel.setUan_no(object.getString("uan_no"));
        personalDataModel.setEsic_no(object.getString("esic_no"));

        personalDataModel.setWork_with_company(object.getString("work_with_company"));
        personalDataModel.setWork_with_company_department(object.getString("work_with_company_department"));
        personalDataModel.setWork_with_company_designation(object.getString("work_with_company_designation"));
        personalDataModel.setWork_with_company_doj(object.getString("work_with_company_doj"));
        personalDataModel.setWork_with_company_dol(object.getString("work_with_company_dol"));

        personalDataModel.setCompany_relation(object.getString("company_relation"));
        personalDataModel.setCompany_relation_name(object.getString("company_relation_name"));
        personalDataModel.setCompany_relation_relation(object.getString("company_relation_relation"));
        personalDataModel.setCompany_relation_designation(object.getString("company_relation_designation"));

        personalDataModel.setPerson_name(object.getString("person_name"));
        personalDataModel.setPerson_number(object.getString("person_number"));
        personalDataModel.setPerson_relation(object.getString("person_relation"));

        personalDataModel.setRequest(object.getString("request"));
        personalDataModel.setHo_request(object.getString("ho_request"));

        //set values to fields

        return personalDataModel;
    }

    private void distributeDataToFragments(PersonalDataModel data) {
        for (int i = 0; i < stepperAdapter.getItemCount(); i++) {
            Fragment fragment = stepperAdapter.getFragment(i);
            if (fragment instanceof StepperFragment) {
                ((StepperFragment) fragment).setData(data);
            }
        }
    }

    private void submitForm() {
        // Collect data from all fragments
        Map<String, String> formData = new HashMap<>();
        boolean isValid = true;

        for (int i = 0; i < stepperAdapter.getItemCount(); i++) {
            Fragment fragment = stepperAdapter.getFragment(i);
            if (fragment instanceof StepperFragment) {
                if (((StepperFragment) fragment).isStepValid()) {
                    formData.putAll(((StepperFragment) fragment).getStepData());
                } else {
                    isValid = false;
                    stepperViewPager.setCurrentItem(i);
                    break;
                }
            }
        }

        if (isValid) {
            showConfirmationDialog(formData);
        }
    }

    private void showConfirmationDialog(Map<String, String> formData) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> savePersonalDetailData(formData))
                .setNegativeButton("No", null)
                .show();
    }

    private void savePersonalDetailData(Map<String, String> formData) {
        loadingDialog.showDialog("Saving Data...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SAVE_PERSONAL_DATA_URL,
                response -> {
                    loadingDialog.hideDialog();
                    Log.d("RESPONSE DATA", response);
                    showToast(response.trim());
                    finish();
                },
                error -> {
                    loadingDialog.hideDialog();
                    Log.e("RESPONSE ERROR", error.toString());
                    showToast(error.toString());
                }
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                // Add user data to form data
                formData.put("userid", Prevalent.currentOnlineUser.getUserid());
                formData.put("uDesignation", Prevalent.currentOnlineUser.getDesignation());
                formData.put("uDepartment", Prevalent.currentOnlineUser.getDepartment());
                formData.put("uGrade", Prevalent.currentOnlineUser.getGrade());
                formData.put("uBranch", Prevalent.currentOnlineUser.getBranch());
                formData.put("uZone", Prevalent.currentOnlineUser.getZone());
                formData.put("uEmployeeLevel", Prevalent.currentOnlineUser.getEmployee_level());
                formData.put("uCategory", Prevalent.currentOnlineUser.getCategory());
                formData.put("uUserStatus", Prevalent.currentOnlineUser.getUser_status());
                formData.put("uSalary", Prevalent.currentOnlineUser.getSalary());
                formData.put("uCandidateCategory", Prevalent.currentOnlineUser.getCandidate_category());
                return formData;
            }
        };

        setupRequestRetryPolicy(stringRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupRequestRetryPolicy(StringRequest request) {
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() { return 30000; }
            @Override
            public int getCurrentRetryCount() { return 30000; }
            @Override
            public void retry(VolleyError error) {
                showToast(error.toString());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}