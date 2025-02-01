package com.vivo.vivorajonboarding;

import static com.android.volley.VolleyLog.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import com.vivo.vivorajonboarding.transformer.CardPageTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import timber.log.Timber;


public class BasicInfoActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView stepTitle;
    private Chip stepIndicator;
    private LinearProgressIndicator progressIndicator;
    private SlidingButton submitButton;
    private List<FormCard> cards;
    private ImageView swipe_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        initializeViews();

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

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            ctx,
                            R.array.initials_array,
                            android.R.layout.simple_spinner_item
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(adapter);

                    // Name Input
                    TextInputLayout nameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.name),
                            getString(R.string.enter_name),
                            R.drawable.ic_username_one_one,
                            true
                    );

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

                        // Setup date picker
                        dobInput.setOnClickListener(v -> {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ctx,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        String selectedDate = String.format(Locale.getDefault(),
                                                "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                                        dobInput.setText(selectedDate);
                                    },
                                    year, month, day
                            );

                            // Set max date to current date
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
                    // Main container
                    LinearLayout mainLayout = createHorizontalLayout(ctx);
                    mainLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));
                    mainLayout.setWeightSum(2);

                    // Gender Spinner Section
                    CardView genderCard = new CardView(ctx);
                    genderCard.setCardElevation(dpToPx(ctx, 2));
                    genderCard.setRadius(dpToPx(ctx, 12));
                    genderCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    genderCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams genderCardParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx,60),
                            dpToPx(ctx, 80), // Fixed height in dp, converted to pixels
                            1.0f
                    );
                    genderCardParams.setMarginEnd(dpToPx(ctx, 4));
                    genderCard.setLayoutParams(genderCardParams);

                    LinearLayout genderSpinnerLayout = new LinearLayout(ctx);
                    genderSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    genderSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    genderSpinnerLayout.setPadding(dpToPx(ctx, 0), dpToPx(ctx, 0), dpToPx(ctx, 0), dpToPx(ctx, 0));

                    // Gender Spinner
                    Spinner genderSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx,60),
                            dpToPx(ctx, 80), // Fixed height in dp, converted to pixels
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
                    LinearLayout.LayoutParams genderArrowParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    genderArrowParams.setMarginStart(dpToPx(ctx, 0));
                    genderDropdownArrow.setLayoutParams(genderArrowParams);
                    genderDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    genderDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Blood Group Spinner Section
                    CardView bloodGroupCard = new CardView(ctx);
                    bloodGroupCard.setCardElevation(dpToPx(ctx, 2));
                    bloodGroupCard.setRadius(dpToPx(ctx, 12));
                    bloodGroupCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    bloodGroupCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams bloodGroupCardParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx,60),
                            dpToPx(ctx, 80), // Fixed height in dp, converted to pixels
                            1.0f
                    );
                    bloodGroupCardParams.setMarginStart(dpToPx(ctx, 4));
                    bloodGroupCard.setLayoutParams(bloodGroupCardParams);

                    LinearLayout bloodGroupSpinnerLayout = new LinearLayout(ctx);
                    bloodGroupSpinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    bloodGroupSpinnerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    bloodGroupSpinnerLayout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8));

                    ImageView bloodGroupIcon = new ImageView(ctx);
                    LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    iconParams.setMarginEnd(dpToPx(ctx, 12));
                    bloodGroupIcon.setLayoutParams(iconParams);
                    bloodGroupIcon.setImageResource(R.drawable.ic_blood);
                    bloodGroupIcon.setColorFilter(null);
                    bloodGroupIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

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
                    LinearLayout.LayoutParams bloodGroupArrowParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    bloodGroupArrowParams.setMarginStart(dpToPx(ctx, 8));
                    bloodGroupDropdownArrow.setLayoutParams(bloodGroupArrowParams);
                    bloodGroupDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    bloodGroupDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Set up animations for both spinners
                    setupSpinnerAnimation(genderSpinner, genderDropdownArrow);
                    setupSpinnerAnimation(bloodGroupSpinner, bloodGroupDropdownArrow);

                    // Add views to their respective layouts
                    genderSpinnerLayout.addView(genderSpinner);
                    genderSpinnerLayout.addView(genderDropdownArrow);
                    genderCard.addView(genderSpinnerLayout);

                    bloodGroupSpinnerLayout.addView(bloodGroupIcon);
                    bloodGroupSpinnerLayout.addView(bloodGroupSpinner);
                    bloodGroupSpinnerLayout.addView(bloodGroupDropdownArrow);
                    bloodGroupCard.addView(bloodGroupSpinnerLayout);

                    // Add both cards to main layout
                    mainLayout.addView(genderCard);
                    mainLayout.addView(bloodGroupCard);

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
                        addressEditText.setGravity(  Gravity.START);
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

                    // Checkbox
                    MaterialCheckBox sameAsCurrentCheckbox = new MaterialCheckBox(ctx);
                    sameAsCurrentCheckbox.setText(getString(R.string.same_as_permanent_address)); // Using getString instead of direct R.string
                    sameAsCurrentCheckbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    sameAsCurrentCheckbox.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
                    sameAsCurrentCheckbox.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
                    LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    checkboxParams.setMargins(dpToPx(ctx, 4), 0, 0, dpToPx(ctx, 8));
                    sameAsCurrentCheckbox.setLayoutParams(checkboxParams);

                    // Permanent Address Input with proper layout params
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
                    }

                    // Checkbox listener
                    sameAsCurrentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        View rootView = mainLayout.getRootView();
                        TextInputLayout currentAddressLayout = rootView.findViewWithTag("current_address_input");

                        if (isChecked) {
                            addressInput.setEnabled(false);
                            if (addressEditText != null) {
                                addressEditText.setEnabled(false);
                                if (currentAddressLayout != null && currentAddressLayout.getEditText() != null) {
                                    String currentAddress = currentAddressLayout.getEditText().getText().toString();
                                    addressEditText.setText(currentAddress);
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
                            R.drawable.ic_aadhar_number,
                            true
                    );

                    TextInputEditText aadharEditText = (TextInputEditText) aadharInput.getEditText();
                    if (aadharEditText != null) {
                        aadharEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        aadharEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
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

                        // Setup date picker
                        dojInput.setOnClickListener(v -> {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ctx,
                                    (view, selectedYear, selectedMonth, selectedDay) -> {
                                        String selectedDate = String.format(Locale.getDefault(),
                                                "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                                        dojInput.setText(selectedDate);
                                    },
                                    year, month, day
                            );

                            // Set max date to current date
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();
                        });
                    }

                    layout.addView(dojLayout);
                    return layout;
                }
        ));

        // Person Name Field
        fields.add(new FormField(
                getString(R.string.person_name),
                getString(R.string.enter_person_name),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout layout = new LinearLayout(ctx); // Changed to vertical layout
                    layout.setOrientation(LinearLayout.VERTICAL); // Set vertical orientation
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextView headerText = new TextView(ctx);
                    headerText.setText(getString(R.string.emergency_contact_person_details));
                    headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    headerText.setTypeface(null, Typeface.BOLD);
                    headerText.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
                    headerText.setPadding(0, 0, 0, dpToPx(ctx, 10));
                    layout.addView(headerText);

                    // Divider
                    View divider = new View(ctx);
                    divider.setBackgroundColor(Color.LTGRAY);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 1)
                    ));
                    layout.addView(divider);

                    TextInputLayout nameInput = createTextInputLayout(
                            ctx,
                            getString(R.string.person_name),
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
                    }

                    layout.addView(nameInput);
                    return layout;
                }
        ));

// Contact Number Field
        fields.add(new FormField(
                getString(R.string.contact_no),
                getString(R.string.enter_contact_no),
                InputType.TYPE_CLASS_NUMBER,
                ctx -> {
                    LinearLayout layout = createHorizontalLayout(ctx);
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextInputLayout contactInput = createTextInputLayout(
                            ctx,
                            getString(R.string.contact_no),
                            getString(R.string.enter_contact_no),
                            R.drawable.ic_phone_two,
                            true
                    );

                    TextInputEditText contactEditText = (TextInputEditText) contactInput.getEditText();
                    if (contactEditText != null) {
                        contactEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                        contactEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }

                    layout.addView(contactInput);
                    return layout;
                }
        ));

// Relation Spinner Field
        fields.add(new FormField(
                getString(R.string.relation),
                getString(R.string.select_relation),
                InputType.TYPE_CLASS_TEXT,
                ctx -> {
                    LinearLayout mainLayout = createHorizontalLayout(ctx);
                    mainLayout.setPadding(dpToPx(ctx, 2), dpToPx(ctx, 2), dpToPx(ctx, 2), dpToPx(ctx, 0));
                    mainLayout.setWeightSum(2);

                    CardView relationCard = new CardView(ctx);
                    relationCard.setCardElevation(dpToPx(ctx, 2));
                    relationCard.setRadius(dpToPx(ctx, 12));
                    relationCard.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
                    relationCard.setUseCompatPadding(true);
                    LinearLayout.LayoutParams relationCardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(ctx, 80),
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
                    relationIcon.setImageResource(R.drawable.ic_relation);  // Make sure you have this icon
                    relationIcon.setColorFilter(null);
                    relationIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    Spinner relationSpinner = new Spinner(ctx, Spinner.MODE_DROPDOWN);
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    relationSpinner.setLayoutParams(spinnerParams);

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
                            view.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
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
                    relationSpinner.setBackgroundResource(R.drawable.spinner_ripple_bg);

                    ImageView relationDropdownArrow = new ImageView(ctx);
                    LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                            dpToPx(ctx, 24),
                            dpToPx(ctx, 24)
                    );
                    arrowParams.setMarginStart(dpToPx(ctx, 8));
                    relationDropdownArrow.setLayoutParams(arrowParams);
                    relationDropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow);
                    relationDropdownArrow.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary));

                    // Set up animation for the spinner
                    setupSpinnerAnimation(relationSpinner, relationDropdownArrow);

                    // Add views to their respective layouts
                    relationSpinnerLayout.addView(relationIcon);
                    relationSpinnerLayout.addView(relationSpinner);
                    relationSpinnerLayout.addView(relationDropdownArrow);
                    relationCard.addView(relationSpinnerLayout);
                    mainLayout.addView(relationCard);

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
                                        Timber.tag(TAG).d("createPastDetails: Function Called");
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
                                        Timber.tag(TAG).d("createPastDetails: Function Called");
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
                            R.drawable.ic_ifsc_code_one,
                            true
                    );

                    // Add search icon at the end
                    ifscInput.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
                    ifscInput.setEndIconDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.search_icon, null));

                    TextInputEditText ifscEditText = (TextInputEditText) ifscInput.getEditText();
                    if (ifscEditText != null) {
                        ifscEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                        ifscEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
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
                    LinearLayout layout = new LinearLayout(ctx); // Changed to vertical layout
                    layout.setOrientation(LinearLayout.VERTICAL); // Set vertical orientation
                    layout.setPadding(dpToPx(ctx, 8), dpToPx(ctx, 30), dpToPx(ctx, 8), dpToPx(ctx, 0));

                    TextView headerText = new TextView(ctx);
                    headerText.setText(getString(R.string.provident_fund_details_optional));
                    headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    headerText.setTypeface(null, Typeface.BOLD);
                    headerText.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
                    headerText.setPadding(0, 0, 0, dpToPx(ctx, 10));
                    layout.addView(headerText);

                    // Divider
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
                    uanInput.setPadding(0,dpToPx(ctx,20),0,dpToPx(ctx,0));

                    uanInput.setLayoutParams(layoutParams);


                    TextInputEditText uanEditText = (TextInputEditText) uanInput.getEditText();
                    if (uanEditText != null) {
                        uanEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        uanEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                        Timber.tag(TAG).d("Initiating ViewPager height adjustment");
                        ((FormCardAdapter) adapter).adjustViewPagerHeight(viewPager);
                    } else {
                        Timber.tag(TAG).w("ViewPager adapter is not FormCardAdapter");
                    }
                }
            } catch (Exception e) {
                Timber.tag(TAG).e(e, "Error in notifyViewPagerHeightChanged");
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
                getString(R.string.designation),
                getString(R.string.enter_designation),
                R.drawable.ic_designation_one,
                true
        );

        designationInput.setLayoutParams(params);
        layout.addView(designationInput);

        // Date of Joining input
        TextInputLayout dojInput = createDateInput(ctx, getString(R.string.date_of_joining));


        dojInput.setLayoutParams(params);
        layout.addView(dojInput);

        // Date of Leaving input
        TextInputLayout dolInput = createDateInput(ctx, getString(R.string.date_of_leaving));


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
                getString(R.string.name),
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
                getString(R.string.relation),
                getString(R.string.enter_relation),
                R.drawable.ic_relation,
                true
        );

        relationInput.setLayoutParams(params);
        layout.addView(relationInput);


        TextInputLayout designationInput = createTextInputLayout(
                ctx,
                getString(R.string.designation),
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
                                                  int startIconRes, boolean allCaps) {
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

        TextInputEditText editText = new TextInputEditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (allCaps) {
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
        // Validate all fields across all cards
        boolean isValid = validateAllFields();

        if (isValid) {
            // Collect all form data
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
                        // Validate each field in the form container
                        for (int j = 0; j < formContainer.getChildCount(); j++) {
                            View field = formContainer.getChildAt(j);
                            if (field instanceof TextInputLayout textInputLayout) {
                                TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

                                if (editText != null && Objects.requireNonNull(editText.getText()).toString().trim().isEmpty()) {
                                    textInputLayout.setError(getString(R.string.field_required));
                                    isValid = false;
                                } else {
                                    textInputLayout.setError(null);
                                }
                            }
                        }
                    }
                }
            }
        }

        return isValid;
    }

    private FormData collectFormData() {
        FormData formData = new FormData();
        FormCardAdapter adapter = (FormCardAdapter) viewPager.getAdapter();

        if (adapter != null) {
            for (int i = 0; i < cards.size(); i++) {
                View cardView = adapter.getCardView(i);
                if (cardView != null) {
                    LinearLayout formContainer = cardView.findViewById(R.id.formContainer);
                    if (formContainer != null) {
                        // Collect data from each field
                        for (int j = 0; j < formContainer.getChildCount(); j++) {
                            View field = formContainer.getChildAt(j);
                            if (field instanceof TextInputLayout textInputLayout) {
                                TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

                                if (editText != null) {
                                    String fieldId = editText.getResources().getResourceEntryName(editText.getId());
                                    String value = Objects.requireNonNull(editText.getText()).toString().trim();
                                    formData.addField(fieldId, value);
                                }
                            } else if (field instanceof LinearLayout) {
                                // Handle spinner containers
                                Spinner spinner = findSpinnerInContainer((LinearLayout) field);
                                if (spinner != null) {
                                    String fieldId = spinner.getResources().getResourceEntryName(spinner.getId());
                                    String value = spinner.getSelectedItem().toString();
                                    formData.addField(fieldId, value);
                                }
                            }
                        }
                    }
                }
            }
        }

        return formData;
    }

    private Spinner findSpinnerInContainer(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof Spinner) {
                return (Spinner) child;
            }
        }
        return null;
    }

    private void processFormSubmission(FormData formData) {
        // Here you would typically:
        // 1. Show a loading indicator
        showLoadingDialog();

        // 2. Make an API call or save to database
        saveFormData(formData);
    }

    private void showLoadingDialog() {
        // Show a loading dialog
        // You can implement your own loading dialog here
        Toast.makeText(this, getString(R.string.submitting_form), Toast.LENGTH_SHORT).show();
    }

    private void saveFormData(FormData formData) {
        // Implement your data saving logic here
        // This could be an API call, database operation, etc.

        // For demo purposes, we'll just show a success message
        Toast.makeText(this, getString(R.string.form_submitted_successfully), Toast.LENGTH_LONG).show();

        // Close the activity or navigate to the next screen
        finish();
    }

    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    // Helper class to store form data
    private static class FormData {
        private final Map<String, String> fields = new HashMap<>();

        public void addField(String fieldId, String value) {
            fields.put(fieldId, value);
        }

        public Map<String, String> getFields() {
            return fields;
        }
    }
}