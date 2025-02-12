package com.vivo.vivorajonboarding;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.adapter.FormCardAdapter;
import com.vivo.vivorajonboarding.adapter.GenderSpinnerAdapter;
import com.vivo.vivorajonboarding.model.FormCard;
import com.vivo.vivorajonboarding.model.FormField;
import com.vivo.vivorajonboarding.model.GenderItem;
import com.vivo.vivorajonboarding.model.UserModel;
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class BasicInfoActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView stepTitle;
    private Chip stepIndicator;
    private LinearProgressIndicator progressIndicator;
    private SlidingButton submitButton;
    private List<FormCard> cards;
    private ImageView swipe_icon;
    private ProgressDialog progressDialog;
    private static final String TAG = "FormSubmission";
    private  String userId;// More specific tag for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        initializeViews();
        initializeToolbar();
        checkUserLoggedIn();
        setupCards();
        setupViewPager();

    }

    private void initializeViews() {
        viewPager = findViewById(R.id.stepperViewPager);
        stepTitle = findViewById(R.id.stepTitle);
        stepIndicator = findViewById(R.id.stepIndicator);
        progressIndicator = findViewById(R.id.stepperProgress);
        swipe_icon=findViewById(R.id.ic_next_icon);

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


    private void setupCards() {
        cards = new ArrayList<>();

        // Card 1: Personal Information
        List<FormField> personalInfoFields = createPersonalInfoFields();
        cards.add(new FormCard(getString(R.string.personal_information), personalInfoFields));

        // Card 2: Contact Information
        List<FormField> contactInfoFields = createContactInfoFields();
        cards.add(new FormCard(getString(R.string.contact_information), contactInfoFields));

        // Card 3: Additional Information
        List<FormField> additionalInfoFields = createAdditionalInfoFields();
        cards.add(new FormCard(getString(R.string.additional_information), additionalInfoFields));

        List<FormField> pastInfoFields = createPastDetails();
        cards.add(new FormCard(getString(R.string.company_details_one), pastInfoFields));

        List<FormField> bankInfoFields = createBankDetails();
        cards.add(new FormCard(getString(R.string.banking_details), bankInfoFields));


    }

    private List<FormField> createPersonalInfoFields() {
        List<FormField> fields = new ArrayList<>();

        // Full Name with Title
        fields.add(new FormField(
                getString(R.string.name),
                getString(R.string.enter_name),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    // Title Spinner
                    Spinner titleSpinner = new Spinner(ctx);
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 90),
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    spinnerParams.setMarginEnd(dpToPx(ctx, 0));
                    titleSpinner.setLayoutParams(spinnerParams);
                    titleSpinner.setTag("title_spinner");

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            ctx,
                            R.array.initials_array,
                            android.R.layout.simple_spinner_item
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(adapter);

                    // Name Input with validation
                    TextInputLayout nameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.name),
                            getString(R.string.enter_name),
                            R.drawable.ic_username_one_one,
                            true
                    );

                    TextInputEditText nameEditText = (TextInputEditText) nameInput.getEditText();
                    if (nameEditText != null) {
                        nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        nameEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String name = nameEditText.getText().toString().trim();
                                if (name.isEmpty()) {
                                    nameInput.setError("Name is required");
                                } else if (!name.matches("^[a-zA-Z\\s]+$")) {
                                    nameInput.setError("Name should only contain letters and spaces");
                                } else if (name.length() < 2) {
                                    nameInput.setError("Name should be at least 2 characters long");
                                } else {
                                    nameInput.setError(null);
                                    nameInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(titleSpinner);
                    layout.addView(nameInput);
                    return layout;
                }
        ));

        // Father's Name
        fields.add(new FormField(
                getString(R.string.father_name),
                getString(R.string.enter_father_name),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout fatherNameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.father_name),
                            getString(R.string.enter_father_name),
                            R.drawable.ic_father_name,
                            true
                    );

                    TextInputEditText fatherNameEditText = (TextInputEditText) fatherNameInput.getEditText();
                    if (fatherNameEditText != null) {
                        fatherNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        fatherNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String fatherName = fatherNameEditText.getText().toString().trim();
                                if (fatherName.isEmpty()) {
                                    fatherNameInput.setError("Father's name is required");
                                } else if (!fatherName.matches("^[a-zA-Z\\s]+$")) {
                                    fatherNameInput.setError("Father's name should only contain letters and spaces");
                                } else if (fatherName.length() < 2) {
                                    fatherNameInput.setError("Father's name should be at least 2 characters long");
                                } else {
                                    fatherNameInput.setError(null);
                                    fatherNameInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(fatherNameInput);
                    return layout;
                }
        ));

        // Mother's Name
        fields.add(new FormField(
                getString(R.string.mother_name),
                getString(R.string.enter_mother_name),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout motherNameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.mother_name),
                            getString(R.string.enter_mother_name),
                            R.drawable.ic_mother_name,
                            true
                    );

                    TextInputEditText motherNameEditText = (TextInputEditText) motherNameInput.getEditText();
                    if (motherNameEditText != null) {
                        motherNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                        motherNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String motherName = motherNameEditText.getText().toString().trim();
                                if (motherName.isEmpty()) {
                                    motherNameInput.setError("Mother's name is required");
                                } else if (!motherName.matches("^[a-zA-Z\\s]+$")) {
                                    motherNameInput.setError("Mother's name should only contain letters and spaces");
                                } else if (motherName.length() < 2) {
                                    motherNameInput.setError("Mother's name should be at least 2 characters long");
                                } else {
                                    motherNameInput.setError(null);
                                    motherNameInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(motherNameInput);
                    return layout;
                }
        ));

        // Date of Birth
        fields.add(new FormField(
                getString(R.string.date_of_birth),
                getString(R.string.select_date_of_birth),
                InputType.TYPE_CLASS_DATETIME,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));
                    TextInputLayout dobLayout = createTextInputLayout(
                            ctx,
                            getString(R.string.date_of_birth),
                            getString(R.string.select_date_of_birth),
                            R.drawable.ic_calender,
                            false
                    );

                    TextInputEditText dobInput = (TextInputEditText) dobLayout.getEditText();
                    if (dobInput != null) {
                        dobInput.setFocusable(false);
                        dobInput.setClickable(true);

                        dobInput.setOnClickListener(v -> {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ctx,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        Calendar birthDate = Calendar.getInstance();
                                        birthDate.set(selectedYear, selectedMonth, selectedDay);
                                        Calendar today = Calendar.getInstance();
                                        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                                        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                                            age--;
                                        }

                                        if (age < 18) {
                                            dobLayout.setError("You must be at least 18 years old");
                                        } else if (age > 100) {
                                            dobLayout.setError("Please enter a valid date of birth");
                                        } else {
                                            String selectedDate = String.format(Locale.getDefault(),
                                                    "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                                            dobInput.setText(selectedDate);
                                            dobLayout.setError(null);
                                            dobLayout.setErrorEnabled(false);
                                        }
                                    },
                                    year, month, day
                            );

                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            Calendar minDate = Calendar.getInstance();
                            minDate.add(Calendar.YEAR, -100);
                            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                            datePickerDialog.show();
                        });
                    }

                    layout.addView(dobLayout);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.combined_fields),
                getString(R.string.select_fields),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    // Main container - Vertical orientation
                    LinearLayout mainLayout = new LinearLayout(ctx);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    mainLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    // First row container for gender and blood group
                    LinearLayout firstRowLayout = new LinearLayout(ctx);
                    firstRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    firstRowLayout.setWeightSum(2);
                    LinearLayout.LayoutParams firstRowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    firstRowParams.setMargins(0, 0, 0, dpToPx(ctx, 8));
                    firstRowLayout.setLayoutParams(firstRowParams);

                    // Gender Spinner Section
                    CardView genderCard = new CardView(ctx);
                    genderCard.setCardElevation(dpToPx(ctx, 2));
                    genderCard.setRadius(dpToPx(ctx, 12));
                    genderCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    genderCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams genderCardParams = new LinearLayout.LayoutParams(
                            0,
                            dpToPx(ctx, 80),
                            1.0f
                    );
                    genderCardParams.setMarginEnd(dpToPx(ctx, 4));
                    genderCard.setLayoutParams(genderCardParams);

                    LinearLayout genderSpinnerLayout = new LinearLayout(ctx);
                    genderSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    genderSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    genderSpinnerLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8));

                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );

                    Spinner genderSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    genderSpinner.setLayoutParams(spinnerParams);

                    List<GenderItem> genderItems = new ArrayList<>();
                    genderItems.add(new GenderItem("Male", ContextCompat.getDrawable(ctx, R.drawable.ic_male)));
                    genderItems.add(new GenderItem("Female", ContextCompat.getDrawable(ctx, R.drawable.ic_female)));

                    GenderSpinnerAdapter genderAdapter = new GenderSpinnerAdapter(ctx, genderItems);
                    genderSpinner.setAdapter(genderAdapter);
                    genderAdapter.setDropDownViewResource(R.layout.custom_dropdown_item);
                    genderSpinner.setBackgroundResource(R.drawable.spinner_ripple_bg);

                    ImageView genderDropdownArrow = new ImageView(ctx);
                    LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    arrowParams.setMarginStart(dpToPx(ctx, 8));
                    genderDropdownArrow.setLayoutParams(arrowParams);
                    genderDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    genderDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Blood Group Spinner Section
                    CardView bloodGroupCard = new CardView(ctx);
                    bloodGroupCard.setCardElevation(dpToPx(ctx, 2));
                    bloodGroupCard.setRadius(dpToPx(ctx, 12));
                    bloodGroupCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    bloodGroupCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams bloodGroupCardParams = new LinearLayout.LayoutParams(
                            0,
                            dpToPx(ctx, 80),
                            1.0f
                    );
                    bloodGroupCardParams.setMarginStart(dpToPx(ctx, 4));
                    bloodGroupCard.setLayoutParams(bloodGroupCardParams);

                    LinearLayout bloodGroupSpinnerLayout = new LinearLayout(ctx);
                    bloodGroupSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    bloodGroupSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    bloodGroupSpinnerLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8));

                    ImageView bloodGroupIcon = new ImageView(ctx);
                    bloodGroupIcon.setLayoutParams(iconParams);
                    bloodGroupIcon.setImageResource(R.drawable.ic_blood);
                    bloodGroupIcon.setColorFilter(null);

                    Spinner bloodGroupSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    bloodGroupSpinner.setLayoutParams(spinnerParams);

                    String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
                    ArrayAdapter<String> bloodGroupAdapter = new ArrayAdapter<String>(ctx, R.layout.custom_spinner_item_1, bloodGroups) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getView(position, convertView, parent);
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_medium));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                            view.setPadding(dpToPx(ctx, 16), dpToPx(ctx, 12), dpToPx(ctx, 16), dpToPx(ctx, 12));
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                            if (position == bloodGroupSpinner.getSelectedItemPosition()) {
                                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.selected_item_bg));
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.red));
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                            }
                            return view;
                        }
                    };

                    bloodGroupAdapter.setDropDownViewResource(R.layout.custom_dropdown_item);
                    bloodGroupSpinner.setAdapter(bloodGroupAdapter);
                    bloodGroupSpinner.setBackgroundResource(R.drawable.spinner_ripple_bg);

                    ImageView bloodGroupDropdownArrow = new ImageView(ctx);
                    bloodGroupDropdownArrow.setLayoutParams(arrowParams);
                    bloodGroupDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    bloodGroupDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Second row container for marital status
                    LinearLayout secondRowLayout = new LinearLayout(ctx);
                    secondRowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams secondRowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    secondRowLayout.setLayoutParams(secondRowParams);

                    // Marital Status Spinner Section
                    CardView maritalStatusCard = new CardView(ctx);
                    maritalStatusCard.setCardElevation(dpToPx(ctx, 2));
                    maritalStatusCard.setRadius(dpToPx(ctx, 12));
                    maritalStatusCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    maritalStatusCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams maritalStatusCardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 80)
                    );
                    maritalStatusCard.setLayoutParams(maritalStatusCardParams);

                    LinearLayout maritalStatusSpinnerLayout = new LinearLayout(ctx);
                    maritalStatusSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    maritalStatusSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    maritalStatusSpinnerLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8));

                    ImageView maritalStatusIcon = new ImageView(ctx);
                    maritalStatusIcon.setLayoutParams(iconParams);
                    maritalStatusIcon.setImageResource(R.drawable.ic_marital_status);

                    Spinner maritalStatusSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    maritalStatusSpinner.setLayoutParams(spinnerParams);

                    String[] maritalStatus = {"Single", "Married"};
                    ArrayAdapter<String> maritalStatusAdapter = new ArrayAdapter<String>(ctx, R.layout.custom_spinner_item_1, maritalStatus) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getView(position, convertView, parent);
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_medium));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                            view.setPadding(dpToPx(ctx, 16), dpToPx(ctx, 12), dpToPx(ctx, 16), dpToPx(ctx, 12));
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                            if (position == maritalStatusSpinner.getSelectedItemPosition()) {
                                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.selected_item_bg));
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.red));
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                            }
                            return view;
                        }
                    };

                    maritalStatusAdapter.setDropDownViewResource(R.layout.custom_dropdown_item);
                    maritalStatusSpinner.setAdapter(maritalStatusAdapter);
                    maritalStatusSpinner.setBackgroundResource(R.drawable.spinner_ripple_bg);

                    ImageView maritalStatusDropdownArrow = new ImageView(ctx);
                    maritalStatusDropdownArrow.setLayoutParams(arrowParams);
                    maritalStatusDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    maritalStatusDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Set up animations for all spinners
                    setupSpinnerAnimation(genderSpinner, genderDropdownArrow);
                    setupSpinnerAnimation(maritalStatusSpinner, maritalStatusDropdownArrow);
                    setupSpinnerAnimation(bloodGroupSpinner, bloodGroupDropdownArrow);

                    // Add components to their layouts
                    genderSpinnerLayout.addView(genderSpinner);
                    genderSpinnerLayout.addView(genderDropdownArrow);
                    genderCard.addView(genderSpinnerLayout);

                    bloodGroupSpinnerLayout.addView(bloodGroupIcon);
                    bloodGroupSpinnerLayout.addView(bloodGroupSpinner);
                    bloodGroupSpinnerLayout.addView(bloodGroupDropdownArrow);
                    bloodGroupCard.addView(bloodGroupSpinnerLayout);

                    maritalStatusSpinnerLayout.addView(maritalStatusIcon);
                    maritalStatusSpinnerLayout.addView(maritalStatusSpinner);
                    maritalStatusSpinnerLayout.addView(maritalStatusDropdownArrow);
                    maritalStatusCard.addView(maritalStatusSpinnerLayout);

                    // Add cards to their respective row layouts
                    firstRowLayout.addView(genderCard);
                    firstRowLayout.addView(bloodGroupCard);
                    secondRowLayout.addView(maritalStatusCard);

                    // Add rows to main layout
                    mainLayout.addView(firstRowLayout);
                    mainLayout.addView(secondRowLayout);

                    // Add tag to the container LinearLayout
                    mainLayout.setTag("combined_fields_container");

                    return mainLayout;
                }
        ));



        return fields;
    }

    private void setupSpinnerAnimation(Spinner spinner, ImageView dropdownArrow) {
        spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dropdownArrow.animate()
                        .rotation(180f)
                        .setDuration(200)
                        .start();
                v.performClick();
            }
            return false;
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dropdownArrow.animate()
                        .rotation(0f)
                        .setDuration(200)
                        .start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dropdownArrow.animate()
                        .rotation(0f)
                        .setDuration(200)
                        .start();
            }
        });
    }


    private List<FormField> createContactInfoFields() {
        List<FormField> fields = new ArrayList<>();

        // Phone Number
        fields.add(new FormField(
                getString(R.string.phone),
                getString(R.string.enter_phone),
                InputType.TYPE_CLASS_PHONE,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout phoneInput = createTextInputLayout(
                            ctx,
                            getString(R.string.phone),
                            getString(R.string.enter_phone),
                            R.drawable.ic_phone_two,
                            true
                    );

                    TextInputEditText phoneEditText = (TextInputEditText) phoneInput.getEditText();
                    if (phoneEditText != null) {
                        phoneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

                        // Add phone validation
                        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String phone = phoneEditText.getText().toString().trim();
                                if (phone.isEmpty()) {
                                    phoneInput.setError("Phone number is required");
                                } else if (phone.length() != 10) {
                                    phoneInput.setError("Phone number must be 10 digits");
                                } else if (!phone.matches("^[6-9]\\d{9}$")) {
                                    phoneInput.setError("Please enter a valid Indian mobile number");
                                } else {
                                    phoneInput.setError(null);
                                    phoneInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(phoneInput);
                    return layout;
                }
        ));

        // Email Address
        fields.add(new FormField(
                getString(R.string.email),
                getString(R.string.enter_email),
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout emailInput = createTextInputLayout(
                            ctx,
                            getString(R.string.email),
                            getString(R.string.enter_email),
                            R.drawable.ic_email_one,
                            true
                    );

                    TextInputEditText emailEditText = (TextInputEditText) emailInput.getEditText();
                    if (emailEditText != null) {
                        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                        // Add email validation
                        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String email = emailEditText.getText().toString().trim();
                                if (email.isEmpty()) {
                                    emailInput.setError("Email address is required");
                                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    emailInput.setError("Please enter a valid email address");
                                } else {
                                    emailInput.setError(null);
                                    emailInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(emailInput);
                    return layout;
                }
        ));

        // PIN Code
        fields.add(new FormField(
                getString(R.string.pin_code),
                getString(R.string.enter_pin_code),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout pinInput = createTextInputLayout(
                            ctx,
                            getString(R.string.pin_code),
                            getString(R.string.enter_pin_code),
                            R.drawable.ic_pin_code_one,
                            true
                    );

                    TextInputEditText pinEditText = (TextInputEditText) pinInput.getEditText();
                    if (pinEditText != null) {
                        pinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                        pinEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add PIN code validation
                        pinEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String pin = pinEditText.getText().toString().trim();
                                if (pin.isEmpty()) {
                                    pinInput.setError("PIN code is required");
                                } else if (pin.length() != 6) {
                                    pinInput.setError("PIN code must be 6 digits");
                                } else if (!pin.matches("^[1-9][0-9]{5}$")) {
                                    pinInput.setError("Please enter a valid PIN code");
                                } else {
                                    pinInput.setError(null);
                                    pinInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(pinInput);
                    return layout;
                }
        ));

        // Current Address
        fields.add(new FormField(
                getString(R.string.current_address),
                getString(R.string.enter_current_address),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout addressInput = createTextInputLayout(
                            ctx,
                            getString(R.string.current_address),
                            getString(R.string.enter_current_address),
                            R.drawable.ic_address_one,
                            true
                    );
                    addressInput.setTag("current_address_input");

                    TextInputEditText addressEditText = (TextInputEditText) addressInput.getEditText();
                    if (addressEditText != null) {
                        addressEditText.setMinLines(1);
                        addressEditText.setGravity(Gravity.START);

                        // Add address validation
                        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String address = addressEditText.getText().toString().trim();
                                if (address.isEmpty()) {
                                    addressInput.setError("Current address is required");
                                } else if (address.length() < 10) {
                                    addressInput.setError("Please enter a complete address");
                                } else if (!address.matches("^[\\w\\s,.-]+$")) {
                                    addressInput.setError("Address contains invalid characters");
                                } else {
                                    addressInput.setError(null);
                                    addressInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(addressInput);
                    return layout;
                }
        ));

        // Combined Fields (Permanent Address & Checkbox)
        fields.add(new FormField(
                getString(R.string.permanent_address),
                getString(R.string.enter_permanent_address),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE,
                ctx -> {
                    LinearLayout mainLayout = new LinearLayout(ctx);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    mainLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));
                    mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    MaterialCheckBox sameAsCurrentCheckbox = new MaterialCheckBox(ctx);
                    sameAsCurrentCheckbox.setText(getString(R.string.same_as_permanent_address));
                    sameAsCurrentCheckbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    sameAsCurrentCheckbox.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
                    sameAsCurrentCheckbox.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                    LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    checkboxParams.setMargins(dpToPx(ctx, 4), 0, 0, dpToPx(ctx, 8));
                    sameAsCurrentCheckbox.setLayoutParams(checkboxParams);

                    LinearLayout addressContainer = new LinearLayout(ctx);
                    addressContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    TextInputLayout addressInput = createTextInputLayout(
                            ctx,
                            getString(R.string.permanent_address),
                            getString(R.string.enter_permanent_address),
                            R.drawable.ic_address_one,
                            true
                    );
                    addressInput.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    TextInputEditText addressEditText = (TextInputEditText) addressInput.getEditText();
                    if (addressEditText != null) {
                        addressEditText.setMinLines(1);
                        addressEditText.setGravity(Gravity.TOP | Gravity.START);
                        addressEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                        // Add permanent address validation
                        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus && !sameAsCurrentCheckbox.isChecked()) {
                                String address = addressEditText.getText().toString().trim();
                                if (address.isEmpty()) {
                                    addressInput.setError("Permanent address is required");
                                } else if (address.length() < 10) {
                                    addressInput.setError("Please enter a complete address");
                                } else if (!address.matches("^[\\w\\s,.-]+$")) {
                                    addressInput.setError("Address contains invalid characters");
                                } else {
                                    addressInput.setError(null);
                                    addressInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    // Enhanced checkbox listener with validation
                    sameAsCurrentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        View rootView = mainLayout.getRootView();
                        TextInputLayout currentAddressLayout = rootView.findViewWithTag("current_address_input");

                        if (isChecked) {
                            addressInput.setEnabled(false);
                            if (addressEditText != null && currentAddressLayout != null &&
                                    currentAddressLayout.getEditText() != null) {
                                String currentAddress = currentAddressLayout.getEditText().getText().toString();
                                if (!currentAddress.trim().isEmpty()) {
                                    addressEditText.setText(currentAddress);
                                    addressInput.setError(null);
                                    addressInput.setErrorEnabled(false);
                                } else {
                                    sameAsCurrentCheckbox.setChecked(false);
                                    addressInput.setEnabled(true);
                                    Toast.makeText(ctx, "Please fill current address first",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            addressInput.setEnabled(true);
                            if (addressEditText != null) {
                                addressEditText.setEnabled(true);
                                addressEditText.setText("");
                            }
                        }
                    });

                    addressContainer.addView(addressInput);
                    mainLayout.addView(sameAsCurrentCheckbox);
                    mainLayout.addView(addressContainer);
                    return mainLayout;
                }
        ));
        return fields;
    }

    private List<FormField> createAdditionalInfoFields() {
        List<FormField> fields = new ArrayList<>();

        fields.add(new FormField(
                getString(R.string.aadhar_number),
                getString(R.string.enter_aadhar),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout aadharInput = createTextInputLayout(
                            ctx,
                            getString(R.string.aadhar_number),
                            getString(R.string.enter_aadhar),
                            R.drawable.ic_aadhar_card_number,
                            true
                    );

                    TextInputEditText aadharEditText = (TextInputEditText) aadharInput.getEditText();
                    if (aadharEditText != null) {
                        aadharEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        aadharEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add focus change listener for validation
                        aadharEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String aadhar = aadharEditText.getText().toString();
                                if (aadhar.isEmpty()) {
                                    aadharInput.setError("Aadhar number is required");
                                } else if (aadhar.length() != 12) {
                                    aadharInput.setError("Aadhar number must be 12 digits");
                                } else if (!aadhar.matches("^[0-9]{12}$")) {
                                    aadharInput.setError("Invalid Aadhar number format");
                                } else {
                                    aadharInput.setError(null);
                                }
                            }
                        });

                        // Add text change listener for real-time validation
                        aadharEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() > 0 && !s.toString().matches("^[0-9]*$")) {
                                    aadharInput.setError("Only numbers are allowed");
                                } else {
                                    aadharInput.setError(null);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                    }

                    layout.addView(aadharInput);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.pan_number),
                getString(R.string.enter_pan),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout panInput = createTextInputLayout(
                            ctx,
                            getString(R.string.pan_number),
                            getString(R.string.enter_pan),
                            R.drawable.ic_pan_number,
                            true
                    );

                    TextInputEditText panEditText = (TextInputEditText) panInput.getEditText();
                    if (panEditText != null) {
                        panEditText.setFilters(new InputFilter[]{
                                new InputFilter.LengthFilter(10),
                                new InputFilter.AllCaps(),
                                (source, start, end, dest, dstart, dend) -> {
                                    // Check if the input matches PAN pattern
                                    String newText = dest.toString().substring(0, dstart) +
                                            source.toString().substring(start, end) +
                                            dest.toString().substring(dend);

                                    // Only validate if we have full 10 characters
                                    if (newText.length() == 10) {
                                        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
                                        if (!newText.matches(panPattern)) {
                                            return "";
                                        }
                                    }
                                    return source;
                                }
                        });

                        // Set input type to force uppercase
                        panEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                        // Add text change listener for real-time validation
                        panEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
                                String input = s.toString();
                                if (input.length() == 10) {
                                    if (!input.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
                                        panInput.setError("Invalid PAN format. Format: AAAAA9999A");
                                    } else {
                                        panInput.setError(null);
                                    }
                                } else {
                                    panInput.setError(null);
                                }
                            }
                        });
                    }

                    layout.addView(panInput);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.date_of_joining),
                getString(R.string.select_date_of_joining),
                InputType.TYPE_CLASS_DATETIME,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));
                    TextInputLayout dojLayout = createTextInputLayout(
                            ctx,
                            getString(R.string.date_of_joining),
                            getString(R.string.select_date_of_joining),
                            R.drawable.ic_calender,
                            false
                    );

                    TextInputEditText dojInput = (TextInputEditText) dojLayout.getEditText();
                    if (dojInput != null) {
                        dojInput.setFocusable(false);
                        dojInput.setClickable(true);

                        // Add focus change listener for validation
                        dojInput.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus && dojInput.getText().toString().isEmpty()) {
                                dojLayout.setError("Date of joining is required");
                            } else {
                                dojLayout.setError(null);
                            }
                        });

                        dojInput.setOnClickListener(v -> {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ctx,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        Calendar selectedDate = Calendar.getInstance();
                                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                                        // Validate selected date
                                        if (selectedDate.after(Calendar.getInstance())) {
                                            dojLayout.setError("Date cannot be in the future");
                                            dojInput.setText("");
                                        } else {
                                            String formattedDate = String.format(Locale.getDefault(),
                                                    "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                                            dojInput.setText(formattedDate);
                                            dojLayout.setError(null);
                                        }
                                    },
                                    year, month, day
                            );

                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        });
                    }

                    layout.addView(dojLayout);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.emergency_person_name),
                getString(R.string.enter_person_name),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = new LinearLayout(ctx);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextView headerText = new TextView(ctx);
                    headerText.setText(getString(R.string.emergency_contact_person_details));
                    headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    headerText.setTypeface(null, Typeface.BOLD);
                    headerText.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
                    headerText.setPadding(0, 0, 0, dpToPx(ctx, 10));
                    layout.addView(headerText);

                    View divider = new View(ctx);
                    divider.setBackgroundColor(Color.LTGRAY);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 1)
                    ));
                    layout.addView(divider);

                    TextInputLayout nameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.emergency_person_name),
                            getString(R.string.enter_person_name),
                            R.drawable.ic_username_one_one,
                            true
                    );

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    nameInput.setLayoutParams(layoutParams);

                    TextInputEditText nameEditText = (TextInputEditText) nameInput.getEditText();
                    if (nameEditText != null) {
                        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                        // Add focus change listener for validation
                        nameEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String name = nameEditText.getText().toString().trim();
                                if (name.isEmpty()) {
                                    nameInput.setError("Emergency contact name is required");
                                } else if (name.length() < 2) {
                                    nameInput.setError("Name must be at least 2 characters");
                                } else if (!name.matches("^[a-zA-Z\\s]*$")) {
                                    nameInput.setError("Only letters and spaces are allowed");
                                } else {
                                    nameInput.setError(null);
                                }
                            }
                        });

                        // Add text change listener for real-time validation
                        nameEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String input = s.toString();
                                if (!input.matches("^[a-zA-Z\\s]*$")) {
                                    nameInput.setError("Only letters and spaces are allowed");
                                } else {
                                    nameInput.setError(null);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                    }

                    layout.addView(nameInput);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.emergency_contact_no),
                getString(R.string.enter_contact_no),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout contactInput = createTextInputLayout(
                            ctx,
                            getString(R.string.emergency_contact_no),
                            getString(R.string.enter_contact_no),
                            R.drawable.ic_phone_two,
                            true
                    );

                    TextInputEditText contactEditText = (TextInputEditText) contactInput.getEditText();
                    if (contactEditText != null) {
                        contactEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        contactEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add focus change listener for validation
                        contactEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String contact = contactEditText.getText().toString();
                                if (contact.isEmpty()) {
                                    contactInput.setError("Emergency contact number is required");
                                } else if (contact.length() != 10) {
                                    contactInput.setError("Contact number must be 10 digits");
                                } else if (!contact.matches("^[6-9]\\d{9}$")) {
                                    contactInput.setError("Invalid mobile number format");
                                } else {
                                    contactInput.setError(null);
                                }
                            }
                        });

                        // Add text change listener for real-time validation
                        contactEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() > 0 && !s.toString().matches("^[0-9]*$")) {
                                    contactInput.setError("Only numbers are allowed");
                                } else {
                                    contactInput.setError(null);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                    }

                    layout.addView(contactInput);
                    return layout;
                }
        ));

        fields.add(new FormField(
                getString(R.string.emergency_relation),
                getString(R.string.select_relation),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout mainLayout = createHorizontalLayout(ctx);
                    mainLayout.setPadding(dpToPx(ctx, 2), dpToPx(ctx, 2), dpToPx(ctx, 2), dpToPx(ctx, 0));
                    mainLayout.setWeightSum(2);

                    MaterialCardView relationCard = new MaterialCardView(ctx);
                    relationCard.setCardElevation(dpToPx(ctx, 2));
                    relationCard.setRadius(dpToPx(ctx, 12));
                    relationCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    relationCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams relationCardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 100),
                            1.0f
                    );
                    relationCardParams.setMarginStart(dpToPx(ctx, 4));
                    relationCard.setLayoutParams(relationCardParams);

                    LinearLayout relationSpinnerLayout = new LinearLayout(ctx);
                    relationSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    relationSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    relationSpinnerLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8));

                    ImageView relationIcon = new ImageView(ctx);
                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    iconParams.setMarginEnd(dpToPx(ctx, 12));
                    relationIcon.setLayoutParams(iconParams);
                    relationIcon.setImageResource(R.drawable.ic_relation);
                    relationIcon.setColorFilter(null);
                    relationIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    // Create an error TextView for displaying validation messages
                    TextView errorText = new TextView(ctx);
                    errorText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    errorText.setTextColor(ContextCompat.getColor(ctx, R.color.error_red));
                    errorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    errorText.setVisibility(View.GONE);

                    Spinner relationSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    relationSpinner.setLayoutParams(spinnerParams);
                    relationSpinner.setTag("relation_spinner");

                    List<String> relationList = new ArrayList<>(Arrays.asList(
                            "Select Relation", "Father", "Mother", "Brother", "Sister",
                            "Spouse", "Son", "Daughter", "Friend", "Other"
                    ));

                    ArrayAdapter<String> relationAdapter = new ArrayAdapter<String>(ctx, R.layout.custom_spinner_item_1, relationList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getView(position, convertView, parent);
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_medium));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            view.setTextColor(ContextCompat.getColor(ctx, position == 0 ?
                                    R.color.text_hint : R.color.text_primary));
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                            view.setPadding(dpToPx(ctx, 16), dpToPx(ctx, 12), dpToPx(ctx, 16), dpToPx(ctx, 12));
                            view.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
                            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                            if (position == relationSpinner.getSelectedItemPosition()) {
                                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.selected_item_bg));
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.red));
                            } else {
                                view.setBackgroundColor(Color.TRANSPARENT);
                                view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                            }
                            return view;
                        }
                    };

                    relationAdapter.setDropDownViewResource(R.layout.custom_dropdown_item);
                    relationSpinner.setAdapter(relationAdapter);

                    // Add validation for the spinner
                    relationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        private boolean isInitialSelection = true;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Skip validation for the initial automatic selection
                            if (isInitialSelection) {
                                isInitialSelection = false;
                                return;
                            }

                            if (position == 0) {
                                errorText.setText("Please select a relation");
                                errorText.setVisibility(View.VISIBLE);
                                relationCard.setStrokeColor(ContextCompat.getColor(ctx, R.color.error_red));
                                relationCard.setStrokeWidth(dpToPx(ctx, 1));
                            } else {
                                errorText.setVisibility(View.GONE);
                                relationCard.setStrokeWidth(0);
                                String selectedRelation = relationList.get(position);
                                Log.d(TAG, "Selected relation: " + selectedRelation);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            errorText.setText("Please select a relation");
                            errorText.setVisibility(View.VISIBLE);
                            relationCard.setStrokeColor(ContextCompat.getColor(ctx, R.color.error_red));
                            relationCard.setStrokeWidth(dpToPx(ctx, 1));
                        }
                    });

                    // Add views to their respective layouts
                    relationSpinnerLayout.addView(relationIcon);
                    relationSpinnerLayout.addView(relationSpinner);
                    relationCard.addView(relationSpinnerLayout);

                    // Create a vertical layout to hold the card and error text
                    LinearLayout containerLayout = new LinearLayout(ctx);
                    containerLayout.setOrientation(LinearLayout.VERTICAL);
                    containerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,  // Width matches parent
                            dpToPx(ctx, 80)
                    ));

                    containerLayout.addView(relationCard);
                    containerLayout.addView(errorText);


                    mainLayout.addView(containerLayout);

                    return mainLayout;
                }
        ));

        return fields;
    }


    private List<FormField> createPastDetails() {
        List<FormField> fields = new ArrayList<>();

        fields.add(new FormField(
                "past_details_section",
                "Company Details Section",
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout mainContainer = new LinearLayout(ctx);
                    mainContainer.setOrientation(LinearLayout.VERTICAL);
                    mainContainer.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 12), dpToPx(ctx, 8), dpToPx(ctx, 12));
                    mainContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    // Create layouts first with proper LayoutParams
                    LinearLayout companyDetailsLayout = createCompanyDetailsLayout(ctx);
                    companyDetailsLayout.setTag("companyDetailsLayout");
                    companyDetailsLayout.setVisibility(View.GONE);
                    companyDetailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    LinearLayout relationDetailsLayout = createRelationDetailsLayout(ctx);
                    relationDetailsLayout.setTag("relationDetailsLayout");
                    relationDetailsLayout.setVisibility(View.GONE);
                    relationDetailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    // Past Work Question Section
                    LinearLayout pastWorkSection = createRadioSection(
                            ctx,
                            getString(R.string.did_you_work_with_us_in_past),
                            "pastWorkRadioGroup",
                            View.generateViewId(),
                            View.generateViewId(),
                            (group, checkedId) -> {
                                View selectedButton = group.findViewById(checkedId);
                                if (selectedButton instanceof RadioButton) {
                                    boolean isVisible = "Yes".equals(selectedButton.getTag());
                                    companyDetailsLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                                    mainContainer.post(() -> {
                                        companyDetailsLayout.requestLayout();
                                        mainContainer.requestLayout();
                                        notifyViewPagerHeightChanged(mainContainer);
                                    });
                                }
                            }
                    );

                    // Relation Question Section
                    LinearLayout relationSection = createRadioSection(
                            ctx,
                            getString(R.string.do_you_have_any_relation_in_our_company),
                            "relationRadioGroup",
                            View.generateViewId(),
                            View.generateViewId(),
                            (group, checkedId) -> {
                                View selectedButton = group.findViewById(checkedId);
                                if (selectedButton instanceof RadioButton) {
                                    boolean isVisible = "Yes".equals(selectedButton.getTag());
                                    relationDetailsLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                                    mainContainer.post(() -> {
                                        relationDetailsLayout.requestLayout();
                                        mainContainer.requestLayout();
                                        notifyViewPagerHeightChanged(mainContainer);
                                    });
                                }
                            }
                    );

                    // Add all views in order
                    mainContainer.addView(pastWorkSection);
                    mainContainer.addView(companyDetailsLayout);
                    mainContainer.addView(relationSection);
                    mainContainer.addView(relationDetailsLayout);

                    return mainContainer;
                }
        ));

        return fields;
    }

    private List<FormField> createBankDetails() {
        List<FormField> fields = new ArrayList<>();

        // Account Number
        fields.add(new FormField(
                getString(R.string.bank_a_c_number),
                getString(R.string.enter_account_number),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout accountInput = createTextInputLayout(
                            ctx,
                            getString(R.string.bank_a_c_number),
                            getString(R.string.enter_account_number),
                            R.drawable.ic_bank_account_number,
                            true
                    );

                    TextInputEditText accountEditText = (TextInputEditText) accountInput.getEditText();
                    if (accountEditText != null) {
                        accountEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                        accountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add validation on focus change
                        accountEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String accountNumber = accountEditText.getText().toString();
                                if (accountNumber.isEmpty()) {
                                    accountInput.setError("Account number is required");
                                } else if (accountNumber.length() < 9) {
                                    accountInput.setError("Account number must be at least 9 digits");
                                } else if (!accountNumber.matches("^[0-9]{9,18}$")) {
                                    accountInput.setError("Invalid account number format");
                                } else {
                                    accountInput.setError(null);
                                    accountInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(accountInput);
                    return layout;
                }
        ));

        // IFSC Code
        fields.add(new FormField(
                getString(R.string.ifsc_code),
                getString(R.string.enter_ifsc_code),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout ifscInput = createTextInputLayout(
                            ctx,
                            getString(R.string.ifsc_code),
                            getString(R.string.enter_ifsc_code),
                            R.drawable.ic_ifsc_code,
                            true
                    );

                    ifscInput.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ifscInput.setEndIconDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.search_icon, null));

                    TextInputEditText ifscEditText = (TextInputEditText) ifscInput.getEditText();
                    if (ifscEditText != null) {
                        ifscEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                        ifscEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                        // Add validation on focus change
                        ifscEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                String ifsc = ifscEditText.getText().toString().toUpperCase();
                                if (ifsc.isEmpty()) {
                                    ifscInput.setError("IFSC code is required");
                                } else if (!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
                                    ifscInput.setError("Invalid IFSC format. Should be like ABCD0123456");
                                } else {
                                    ifscInput.setError(null);
                                    ifscInput.setErrorEnabled(false);
                                }
                            }
                        });
                    }

                    layout.addView(ifscInput);
                    return layout;
                }
        ));

        // UAN Number (Optional)
        fields.add(new FormField(
                getString(R.string.uan_number),
                getString(R.string.enter_uan_number),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = new LinearLayout(ctx);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 30), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextView headerText = new TextView(ctx);
                    headerText.setText(getString(R.string.provident_fund_details_optional));
                    headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    headerText.setTypeface(null, Typeface.BOLD);
                    headerText.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
                    headerText.setPadding(0, 0, 0, dpToPx(ctx, 10));
                    layout.addView(headerText);

                    View divider = new View(ctx);
                    divider.setBackgroundColor(Color.LTGRAY);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 1)
                    ));
                    layout.addView(divider);

                    TextInputLayout uanInput = createTextInputLayout(
                            ctx,
                            getString(R.string.uan_number),
                            getString(R.string.enter_uan_number),
                            R.drawable.ic_epfo_logo,
                            false
                    );
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    uanInput.setPadding(0, dpToPx(ctx, 20), 0, dpToPx(ctx, 0));
                    uanInput.setLayoutParams(layoutParams);

                    TextInputEditText uanEditText = (TextInputEditText) uanInput.getEditText();
                    if (uanEditText != null) {
                        uanEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        uanEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add validation on focus change
                        uanEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus && !uanEditText.getText().toString().isEmpty()) {
                                String uan = uanEditText.getText().toString();
                                if (!uan.matches("^[0-9]{12}$")) {
                                    uanInput.setError("UAN must be exactly 12 digits");
                                } else {
                                    uanInput.setError(null);
                                    uanInput.setErrorEnabled(false);
                                }
                            } else {
                                uanInput.setError(null);
                                uanInput.setErrorEnabled(false);
                            }
                        });
                    }

                    layout.addView(uanInput);
                    return layout;
                }
        ));

        // ESIC Number (Optional)
        fields.add(new FormField(
                getString(R.string.esic_no),
                getString(R.string.enter_esic_number),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout esicInput = createTextInputLayout(
                            ctx,
                            getString(R.string.esic_no),
                            getString(R.string.enter_esic_number),
                            R.drawable.ic_bank_account_number,
                            false
                    );

                    TextInputEditText esicEditText = (TextInputEditText) esicInput.getEditText();
                    if (esicEditText != null) {
                        esicEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        esicEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        // Add validation on focus change
                        esicEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus && !esicEditText.getText().toString().isEmpty()) {
                                String esic = esicEditText.getText().toString();
                                if (!esic.matches("^[0-9]{10}$")) {
                                    esicInput.setError("ESIC number must be exactly 10 digits");
                                } else {
                                    esicInput.setError(null);
                                    esicInput.setErrorEnabled(false);
                                }
                            } else {
                                esicInput.setError(null);
                                esicInput.setErrorEnabled(false);
                            }
                        });
                    }

                    layout.addView(esicInput);
                    return layout;
                }
        ));

        return fields;
    }


    private void notifyViewPagerHeightChanged(View view) {


        view.post(() -> {
            try {
                ViewParent parent = view.getParent();
                while (parent != null && !(parent instanceof ViewPager2)) {
                    parent = parent.getParent();
                }

                if (parent != null) {
                    ViewPager2 viewPager = (ViewPager2) parent;
                    RecyclerView.Adapter<?> adapter = viewPager.getAdapter();

                    if (adapter instanceof FormCardAdapter) {
                        adjustHeight(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    // Updated createRadioSection to include radio button IDs
    private LinearLayout createRadioSection(Context ctx, String title, String tag,
                                            int yesButtonId, int noButtonId,
                                            RadioGroup.OnCheckedChangeListener listener) {
        LinearLayout section = new LinearLayout(ctx);
        section.setOrientation(LinearLayout.HORIZONTAL);
        section.setGravity(Gravity.CENTER_VERTICAL);
        section.setPadding(0, dpToPx(ctx, 16), 0, dpToPx(ctx, 16));
        section.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView questionText = new TextView(ctx);
        questionText.setText(title);
        questionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        questionText.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        questionText.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        RadioGroup radioGroup = new RadioGroup(ctx);
        radioGroup.setTag(tag);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setOnCheckedChangeListener(listener);
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        RadioButton yesButton = createStylishRadioButton(ctx, "Yes", true);
        yesButton.setId(yesButtonId);
        yesButton.setTag("Yes");  // Important: Set the tag for identification

        RadioButton noButton = createStylishRadioButton(ctx, "No", false);
        noButton.setId(noButtonId);
        noButton.setTag("No");    // Important: Set the tag for identification

        radioGroup.addView(yesButton);
        radioGroup.addView(noButton);

        section.addView(questionText);
        section.addView(radioGroup);

        return section;
    }

    private RadioButton createStylishRadioButton(Context ctx, String text, boolean isFirst) {
        MaterialRadioButton radioButton = new MaterialRadioButton(ctx);
        radioButton.setText(text);
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        radioButton.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primary)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(isFirst ? dpToPx(ctx, 8) : dpToPx(ctx, 16), 0, 0, 0);
        radioButton.setLayoutParams(params);

        // Add padding to the radio button for better touch target
        radioButton.setPadding(
                dpToPx(ctx, 4),
                dpToPx(ctx, 4),
                dpToPx(ctx, 4),
                dpToPx(ctx, 4)
        );

        return radioButton;
    }

    private LinearLayout createCompanyDetailsLayout(Context ctx) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setPadding(0, dpToPx(ctx, 16), 0, dpToPx(ctx, 16));

        // Department input
        TextInputLayout departmentInput = createTextInputLayout(
                ctx,
                getString(R.string.department),
                getString(R.string.enter_department),
                R.drawable.ic_department_one,
                true
        );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(ctx, 8), 0, dpToPx(ctx, 8), dpToPx(ctx, 8));

        departmentInput.setLayoutParams(params);

        layout.addView(departmentInput);

        // Designation input
        TextInputLayout designationInput = createTextInputLayout(
                ctx,
                getString(R.string.previous_designation),
                getString(R.string.enter_designation),
                R.drawable.ic_designation_one,
                true
        );

        designationInput.setLayoutParams(params);
        layout.addView(designationInput);

        // Date of Joining input
        TextInputLayout dojInput = createDateInput(ctx, getString(R.string.previous_date_of_joining));


        dojInput.setLayoutParams(params);
        layout.addView(dojInput);

        // Date of Leaving input
        TextInputLayout dolInput = createDateInput(ctx, getString(R.string.previous_date_of_leaving));


        dolInput.setLayoutParams(params);
        layout.addView(dolInput);


        return layout;
    }

    private LinearLayout createRelationDetailsLayout(Context ctx) {
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setPadding(0, dpToPx(ctx, 16), 0, dpToPx(ctx, 16));
        // Name Input
        TextInputLayout nameInput = createTextInputLayout(
                ctx,
                getString(R.string.relative_name),
                getString(R.string.enter_name),
                R.drawable.ic_username_one_one,
                true
        );

        // Create LayoutParams object
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPx(ctx, 8), 0, dpToPx(ctx, 8), dpToPx(ctx, 8));
        nameInput.setLayoutParams(params);
        layout.addView(nameInput);






        TextInputLayout relationInput = createTextInputLayout(
                ctx,
                getString(R.string.employee_relation),
                getString(R.string.enter_relation),
                R.drawable.ic_relation,
                true
        );

        relationInput.setLayoutParams(params);
        layout.addView(relationInput);


        TextInputLayout designationInput = createTextInputLayout(
                ctx,
                getString(R.string.employee_designation),
                getString(R.string.enter_designation),
                R.drawable.ic_designation_one,
                true
        );
        designationInput.setLayoutParams(params);
        layout.addView(designationInput);


        return layout;
    }



    private TextInputLayout createDateInput(Context ctx, String hint) {
        TextInputLayout inputLayout = createTextInputLayout(
                ctx,
                hint,
                getString(R.string.enter_date),
                R.drawable.ic_calender,
               true
        );

        TextInputEditText editText = (TextInputEditText) inputLayout.getEditText();
        if (editText != null) {
            editText.setFocusable(false);
            editText.setClickable(true);
            editText.setOnClickListener(v -> showDatePicker(ctx, editText));
        }

        return inputLayout;
    }

    private void showDatePicker(Context ctx, TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ctx,
                (view, year, month, day) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", day, month + 1, year);
                    editText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }





    private TextInputLayout createTextInputLayout(Context context, String hint, String helperText,
                                                  int startIconRes, boolean required) {
        TextInputLayout textInputLayout = new TextInputLayout(
                new ContextThemeWrapper(context, R.style.Widget_Custom_TextInputLayout_OutlinedBox)
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        params.setMargins(0, 0, 0, dpToPx(context, 16));
        textInputLayout.setLayoutParams(params);

        textInputLayout.setHint(hint);
        textInputLayout.setHelperText(helperText);
        textInputLayout.setStartIconDrawable(startIconRes);
        textInputLayout.setStartIconTintList(null); // Ensure no tint is applied
        textInputLayout.setStartIconTintMode(null); // Remove any tint mode
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

        // Set required status as a tag
        textInputLayout.setTag(R.id.required_field_tag, required);
        
        // Add asterisk to hint if required
        if (required) {
            textInputLayout.setHint(hint + " *");
        } else {
            textInputLayout.setHint(hint);
        }

        TextInputEditText editText = new TextInputEditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (required) {
            editText.setAllCaps(true);
        }
        editText.setPadding(
                dpToPx(context, 16),
                dpToPx(context, 12),
                dpToPx(context, 16),
                dpToPx(context, 12)
        );

        textInputLayout.addView(editText);
        return textInputLayout;
    }



    private static LinearLayout createHorizontalLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private void setupViewPager() {
        FormCardAdapter adapter = new FormCardAdapter(this, cards);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new CardPageTransformer());

        // Adjust height for initial load
        viewPager.post(() -> adjustHeight(0));

        // Update progress, titles, and handle dynamic height
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Update progress and step info
                updateStepInfo(position);
                updateProgress(position);

                // Adjust height for page changes
                adjustHeight(position);
            }
        });
    }

    // Extracted method for height adjustment
    private void adjustHeight(int position) {
        try {
            // Get the current page view
            RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            if (viewHolder != null) {
                View currentView = viewHolder.itemView;

                // Measure the content
                currentView.post(() -> {
                    currentView.measure(
                            View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    );

                    // Set the height of the ViewPager
                    ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                    int minHeight = getResources().getDimensionPixelSize(R.dimen.min_card_height);
                    params.height = Math.max(minHeight, currentView.getMeasuredHeight());
                    viewPager.setLayoutParams(params);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateStepInfo(int position) {
        stepTitle.setText(cards.get(position).getTitle());
        stepIndicator.setText(String.format("Step %d of %d", position + 1, cards.size()));
        swipe_icon.setVisibility(View.VISIBLE);

       if (position+1 == cards.size()) {
            swipe_icon.setVisibility(View.GONE);
            addSubmitButton();
        }
    }

    private void updateProgress(int position) {
        float progress = (position + 1) * 100f / cards.size();
        progressIndicator.setProgress((int) progress);
    }

    private void addSubmitButton() {

        if (submitButton == null) {
            submitButton = new SlidingButton(this);

            // Create layout params with proper margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            // Add bottom margin to lift it from the bottom
            params.topMargin = (int) (10 * getResources().getDisplayMetrics().density); // 24dp margin
            submitButton.setLayoutParams(params);

            submitButton.setOnSlideCompleteListener(this::onSubmitForm);

            // Get the last card view
            View lastCard = ((FormCardAdapter) Objects.requireNonNull(viewPager.getAdapter()))
                    .getCardView(viewPager.getAdapter().getItemCount() - 1);

            if (lastCard != null) {
                LinearLayout formContainer = lastCard.findViewById(R.id.formContainer);
                if (formContainer != null) {
                    formContainer.addView(submitButton);
                }
            }
        }
    }

    private void onSubmitForm() {
        Log.d(TAG, "Starting form submission process...");
        
        // Validate all fields
        boolean isValid = validateAllFields();
        Log.d(TAG, "Form validation result: " + isValid);

        if (isValid) {
            // Collect all form data
            Log.d(TAG, "Collecting form data...");
            FormData formData = collectFormData();
            
            // Process the form submission
            processFormSubmission(formData);
        }
    }

    private boolean validateAllFields() {
        boolean isValid = true;
        FormCardAdapter adapter = (FormCardAdapter) viewPager.getAdapter();

        if (adapter != null) {
            for (int i = 0; i < cards.size(); i++) {
                View cardView = adapter.getCardView(i);
                if (cardView != null) {
                    LinearLayout formContainer = cardView.findViewById(R.id.formContainer);
                    if (formContainer != null) {
                        for (int j = 0; j < formContainer.getChildCount(); j++) {
                            View field = formContainer.getChildAt(j);
                            try {
                                if (field instanceof TextInputLayout) {
                                    TextInputLayout textInputLayout = (TextInputLayout) field;
                                    TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

                                    if (editText != null) {
                                        String value = editText.getText() != null ? 
                                                     editText.getText().toString().trim() : "";
                                        
                                        boolean isRequired = Boolean.TRUE.equals(textInputLayout.getTag(R.id.required_field_tag));
                                        if (isRequired && value.isEmpty()) {
                                            textInputLayout.setError(getString(R.string.field_required));
                                            isValid = false;
                                            Log.d(TAG, "Validation Failed - Empty required field: " + textInputLayout.getHint());
                                        } else {
                                            textInputLayout.setError(null);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error validating field: " + field, e);
                                isValid = false;
                            }
                        }
                    }
                }
            }
        }

        if (!isValid) {
            Log.d(TAG, "Form validation failed - some required fields are empty");
            showError(getString(R.string.please_fill_required_fields));
        } else {
            Log.d(TAG, "Form validation successful");
        }

        return isValid;
    }

    private FormData collectFormData() {
        FormData formData = new FormData();
        FormCardAdapter adapter = (FormCardAdapter) viewPager.getAdapter();

        Log.d(TAG, "=== Starting Form Data Collection ===");

        if (adapter != null) {
            for (int i = 0; i < cards.size(); i++) {
                Log.d(TAG, "Collecting data from card " + (i + 1));
                View cardView = adapter.getCardView(i);
                if (cardView != null) {
                    LinearLayout formContainer = cardView.findViewById(R.id.formContainer);
                    if (formContainer != null) {
                        collectDataFromContainer(formContainer, formData);
                    }
                }
            }
        }

        // Log all collected data
        Log.d(TAG, "=== Collected Form Data Summary ===");
        for (Map.Entry<String, String> entry : formData.getFields().entrySet()) {
            Log.d(TAG, String.format("Field: '%s' = '%s'", entry.getKey(), entry.getValue()));
        }
        Log.d(TAG, "=== End Form Data Summary ===");

        return formData;
    }

    private void collectDataFromContainer(ViewGroup container, FormData formData) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);

            try {
                if (child instanceof TextInputLayout) {
                    // Existing TextInputLayout handling code remains the same
                    TextInputLayout til = (TextInputLayout) child;
                    TextInputEditText editText = (TextInputEditText) til.getEditText();
                    CharSequence hint = til.getHint();

                    if (editText != null && hint != null) {
                        String fieldName = hint.toString()
                                .split("/")[0]
                                .toLowerCase()
                                .replace(" ", "_")
                                .replace("*", "")
                                .trim();
                        String value = editText.getText() != null ? editText.getText().toString().trim() : "";
                        formData.addField(fieldName, value);
                        Log.d(TAG, String.format("TextInput - %s: %s", fieldName, value));
                    }
                } else if (child instanceof LinearLayout) {
                    // Handle the emergency relation spinner within CardView
                    ViewGroup layout = (ViewGroup) child;
                    for (int j = 0; j < layout.getChildCount(); j++) {
                        View cardView = layout.getChildAt(j);
                        if (cardView instanceof CardView) {
                            // Get the LinearLayout inside CardView that contains the spinner
                            ViewGroup cardContent = (ViewGroup) ((CardView) cardView).getChildAt(0);
                            if (cardContent != null) {
                                // Find Spinner within the card content
                                for (int k = 0; k < cardContent.getChildCount(); k++) {
                                    View spinnerView = cardContent.getChildAt(k);
                                    if (spinnerView instanceof Spinner) {
                                        Spinner spinner = (Spinner) spinnerView;
                                        if ("relation_spinner".equals(spinner.getTag())) {
                                            Object selectedItem = spinner.getSelectedItem();
                                            if (selectedItem != null && !"Select Relation".equals(selectedItem.toString())) {
                                                formData.addField("emergency_relation", selectedItem.toString());
                                                Log.d(TAG, "Added emergency_relation: " + selectedItem.toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Handle existing combined fields container
                    if ("combined_fields_container".equals(child.getTag())) {
                        // Existing combined fields handling code remains the same
                        ViewGroup containerGroup = (ViewGroup) child;
                        for (int j = 0; j < containerGroup.getChildCount(); j++) {
                            View rowLayout = containerGroup.getChildAt(j);
                            if (rowLayout instanceof LinearLayout) {
                                for (int l = 0; l < ((LinearLayout) rowLayout).getChildCount(); l++) {
                                    View cardView = ((LinearLayout) rowLayout).getChildAt(l);
                                    if (cardView instanceof CardView) {
                                        ViewGroup cardContent = (ViewGroup) ((CardView) cardView).getChildAt(0);
                                        for (int k = 0; k < cardContent.getChildCount(); k++) {
                                            View spinnerContainer = cardContent.getChildAt(k);
                                            if (spinnerContainer instanceof Spinner) {
                                                Spinner spinner = (Spinner) spinnerContainer;
                                                Object selectedItem = spinner.getSelectedItem();

                                                if (selectedItem instanceof GenderItem) {
                                                    GenderItem genderItem = (GenderItem) selectedItem;
                                                    formData.addField("gender", genderItem.getText());
                                                    Log.d(TAG, "Added gender: " + genderItem.getText());
                                                } else if (selectedItem instanceof String) {
                                                    String value = (String) selectedItem;
                                                    if (value.contains("+") || value.contains("-")) {
                                                        formData.addField("blood_group", value);
                                                        Log.d(TAG, "Added blood_group: " + value);
                                                    } else if ("Single".equals(value) || "Married".equals(value)) {
                                                        formData.addField("marital_status", value);
                                                        Log.d(TAG, "Added marital_status: " + value);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Handle other existing fields
                    Spinner titleSpinner = findSpinnerByTag((ViewGroup) child, "title_spinner");
                    if (titleSpinner != null && titleSpinner.getSelectedItem() != null) {
                        String title = titleSpinner.getSelectedItem().toString();
                        formData.addField("initial", title);
                        Log.d(TAG, "Added initial: " + title);
                    }

                    // Handle Radio Groups
                    RadioGroup pastWorkRadioGroup = findRadioGroupByTag((ViewGroup) child, "pastWorkRadioGroup");
                    if (pastWorkRadioGroup != null) {
                        int selectedId = pastWorkRadioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedButton = pastWorkRadioGroup.findViewById(selectedId);
                            String value = selectedButton != null ? selectedButton.getTag().toString() : "";
                            formData.addField("past_work_experience", value);
                            Log.d(TAG, "Added past_work_experience: " + value);
                        }
                    }

                    RadioGroup relationRadioGroup = findRadioGroupByTag((ViewGroup) child, "relationRadioGroup");
                    if (relationRadioGroup != null) {
                        int selectedId = relationRadioGroup.getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            RadioButton selectedButton = relationRadioGroup.findViewById(selectedId);
                            String value = selectedButton != null ? selectedButton.getTag().toString() : "";
                            formData.addField("has_company_relation", value);
                            Log.d(TAG, "Added has_company_relation: " + value);
                        }
                    }

                    // Continue recursive search
                    collectDataFromContainer((ViewGroup) child, formData);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error collecting data from view: " + child.getClass().getSimpleName(), e);
            }
        }
    }

    // Helper method to find spinner by tag
    private Spinner findSpinnerByTag(ViewGroup container, String targetTag) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof Spinner) {
                String tag = (String) child.getTag();
                if (targetTag.equals(tag)) {
                    return (Spinner) child;
                }
            } else if (child instanceof ViewGroup) {
                Spinner result = findSpinnerByTag((ViewGroup) child, targetTag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Helper method to find RadioGroup by tag
    private RadioGroup findRadioGroupByTag(ViewGroup container, String targetTag) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof RadioGroup) {
                String tag = (String) child.getTag();
                if (targetTag.equals(tag)) {
                    return (RadioGroup) child;
                }
            } else if (child instanceof ViewGroup) {
                RadioGroup result = findRadioGroupByTag((ViewGroup) child, targetTag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private void processFormSubmission(FormData formData) {
        // Show loading dialog
        showLoadingDialog();

        // Log all collected form data with clear separation
        Log.d(TAG, "\n======= Form Submission Data =======");
        for (Map.Entry<String, String> entry : formData.getFields().entrySet()) {
            Log.d(TAG, String.format("Field: %-30s | Value: %s", entry.getKey(), entry.getValue()));
        }
        Log.d(TAG, "====== End Form Data ======\n");

        // Create JSON object from form data
        JSONObject jsonBody = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : formData.getFields().entrySet()) {
                // Ensure null values are handled properly
                String value = entry.getValue();
                jsonBody.put(entry.getKey(), value != null ? value.trim() : "");
            }
            jsonBody.put("userId",userId);
            Log.d(TAG, "Request JSON Body: " + jsonBody.toString(2)); // Pretty print JSON
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            showError(getString(R.string.error_processing_form));
            hideLoadingDialog();
            return;
        }

        // Make API request
        String apiUrl = "https://vivorajonbording.com/api/vso_api/abhinav_test_api/submit_basic_info.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                apiUrl,
                jsonBody,
                response -> {
                    hideLoadingDialog();
                    Log.d(TAG, "API Response: " + response.toString());

                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (success) {
                            Log.i(TAG, "Form submission successful: " + message);
                            showSuccess(message);
                            finish();
                        } else {
                            // Handle validation errors
                            if (response.has("errors")) {
                                JSONArray errors = response.getJSONArray("errors");
                                StringBuilder errorLog = new StringBuilder("\nValidation Errors:\n");
                                StringBuilder userMessage = new StringBuilder();

                                for (int i = 0; i < errors.length(); i++) {
                                    String error = errors.getString(i);
                                    errorLog.append("- ").append(error).append("\n");
                                    userMessage.append("• ").append(error).append("\n");
                                }

                                Log.e(TAG, errorLog.toString());
                                showValidationErrors(userMessage.toString());
                            } else {
                                Log.e(TAG, "API Error: " + message);
                                showError(message);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing API response", e);
                        Log.e(TAG, "Raw response: " + response.toString());
                        showError(getString(R.string.error_processing_response));
                    }
                },
                error -> {
                    hideLoadingDialog();
                    String errorMessage = getVolleyError(error);

                    // Log detailed network error information
                    Log.e(TAG, "\n====== API Error Details ======");
                    Log.e(TAG, "Error Type: " + error.getClass().getSimpleName());
                    Log.e(TAG, "Error Message: " + errorMessage);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                        Log.e(TAG, "Headers: " + error.networkResponse.headers);
                    }
                    Log.e(TAG, "====== End Error Details ======\n");

                    showError(errorMessage);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Set timeout for the request
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                0,     // no retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add request to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Helper method to show validation errors
    private void showValidationErrors(String errorMessage) {
        // Implement your UI logic to show validation errors
        // For example, using a custom dialog or error view
        submitButton.resetSlide();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.validation_error_title)
                .setMessage(errorMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.submitting_form));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showSuccess(String message) {

    }

    private void showError(String message) {
        submitButton.resetSlide();
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), null)
            .show();
    }

    private String getVolleyError(VolleyError error) {
        if (error instanceof NetworkError) {
            return getString(R.string.error_network);
        } else if (error instanceof ServerError) {
            return getString(R.string.error_server);
        } else if (error instanceof TimeoutError) {
            return getString(R.string.error_timeout);
        } else {
            return getString(R.string.error_unknown);
        }
    }



    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    // Helper class to store form data
    private static class FormData {
        private final Map<String, String> fields = new HashMap<>();

        public void addField(String fieldId, String value) {
            if (fieldId != null && !fieldId.isEmpty()) {
                fields.put(fieldId, value != null ? value : "");
            }
        }

        public Map<String, String> getFields() {
            return fields;
        }
    }
}