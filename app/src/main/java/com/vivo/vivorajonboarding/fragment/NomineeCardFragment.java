package com.vivo.vivorajonboarding.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.NominationActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.SlidingButton;
import com.vivo.vivorajonboarding.SlidingButtonOne;
import com.vivo.vivorajonboarding.model.NomineeCard;
import com.vivo.vivorajonboarding.model.RelationItem;
import com.vivo.vivorajonboarding.adapter.RelationSpinnerAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NomineeCardFragment extends Fragment {
    private CardView relationSpinnerCard;
    private LinearLayout relationSpinnerContainer;
    private RelationSpinnerAdapter relationAdapter;
    private TextInputLayout relationInputLayout, nameInputLayout, dobInputLayout, percentageInputLayout;
    private TextInputEditText relativeNameEt, dobEt, percentageEt;
    private MaterialButton addButton, deleteButton;
    private TextView cardTitleText;
    private NomineeCard nomineeCard;
    private int position;
    private SlidingButtonOne submitButton;
    private String currentRelation;
    private boolean isInitialValidation = true;
    private boolean hasSiblingSelected = false;

    private static final String ARG_CARD = "card";
    private static final String ARG_POSITION = "position";

    private final RelationItem[] relations = {
            new RelationItem("Father", R.drawable.ic_father_one),
            new RelationItem("Mother", R.drawable.ic_mother_one),
            new RelationItem("Spouse", R.drawable.ic_spouse_one),
            new RelationItem("Son", R.drawable.ic_son_one),
            new RelationItem("Daughter", R.drawable.ic_daughter_one),
            new RelationItem("Brother", R.drawable.ic_brother_one),
            new RelationItem("Sister", R.drawable.ic_sister_one)
    };

    public static NomineeCardFragment newInstance(NomineeCard card, int position) {
        NomineeCardFragment fragment = new NomineeCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nomination_card, container, false);

        if (getArguments() != null) {
            nomineeCard = getArguments().getParcelable(ARG_CARD);
            position = getArguments().getInt(ARG_POSITION);
        }

        initializeViews(view);
        setupRelationSpinner();
        setupDatePicker();
        setupButtons();
        setupTextWatchers();
        // Initial validation
        validatePercentageAndUpdateUI("0");

        // Only populate if we have existing data
        if (nomineeCard != null && nomineeCard.getRelation() != null) {
            populateExistingData();
        }

        // Show/hide delete button based on position
        if (deleteButton != null) {
            deleteButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        }

        return view;
    }
    private void initializeViews(View view) {
        // Find views
        relationSpinnerCard = view.findViewById(R.id.relationSpinnerCard);
        relationSpinnerContainer = view.findViewById(R.id.relationSpinnerContainer);
        cardTitleText = view.findViewById(R.id.cardTitle);

        // TextInputLayouts
        relationInputLayout = view.findViewById(R.id.relationInputLayout);
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        dobInputLayout = view.findViewById(R.id.dobInputLayout);
        percentageInputLayout = view.findViewById(R.id.percentageInputLayout);

        // EditTexts
        relativeNameEt = view.findViewById(R.id.relativeNameEt);
        dobEt = view.findViewById(R.id.dobEt);
        percentageEt = view.findViewById(R.id.percentageEt);

        // Buttons
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        String[] textualNumbers = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
        if (position < textualNumbers.length) {
            cardTitleText.setText(String.format(Locale.getDefault(), "Nominee %s", textualNumbers[position]));
        } else {
            cardTitleText.setText(String.format(Locale.getDefault(), "Nominee %d", position + 1)); // Fallback for larger numbers
        }


        // Initialize submit button
        submitButton = new SlidingButtonOne(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = (int) (24 * getResources().getDisplayMetrics().density);
        params.leftMargin = (int) (16 * getResources().getDisplayMetrics().density);
        params.rightMargin = (int) (16 * getResources().getDisplayMetrics().density);
        submitButton.setLayoutParams(params);
        submitButton.setVisibility(View.GONE);

        // Set up slide complete listener
        submitButton.setOnSlideCompleteListener(() -> {
            if (validateFields() && getActivity() instanceof NominationActivity) {
                ((NominationActivity) getActivity()).onSubmitForm();
            }
        });

        // Add submit button to the layout
        LinearLayout container = view.findViewById(R.id.formContainer);
        container.addView(submitButton);
    }

    private void setupRelationSpinner() {
        List<RelationItem> availableRelations = new ArrayList<>();
        Set<String> selectedRelations = ((NominationActivity) requireActivity()).getSelectedRelations();

        // Check if any sibling is already selected
        boolean siblingSelected = selectedRelations.contains("Brother") || selectedRelations.contains("Sister");

        for (RelationItem relation : relations) {
            String relationName = relation.getName();
            boolean isSibling = relationName.equals("Brother") || relationName.equals("Sister");

            // Add relation if:
            // 1. It's not already selected by another card OR
            // 2. It's the current card's relation OR
            // 3. It's a sibling and no sibling is selected yet
            if (!selectedRelations.contains(relationName) ||
                    (nomineeCard.getRelation() != null && nomineeCard.getRelation().equals(relationName)) ||
                    (isSibling && !siblingSelected && !hasSiblingSelected)) {
                availableRelations.add(relation);
            }
        }

        relationAdapter = new RelationSpinnerAdapter(requireContext(), availableRelations);
        relationSpinnerContainer.removeAllViews();

        for (int i = 0; i < availableRelations.size(); i++) {
            RelationItem relation = availableRelations.get(i);
            CardView relationCard = createRelationCard(relation);
            relationSpinnerContainer.addView(relationCard);

            relationCard.setOnClickListener(v -> {
                String newRelation = relation.getName();
                boolean isNewSibling = newRelation.equals("Brother") || newRelation.equals("Sister");

                // Remove old relation if exists
                if (currentRelation != null && !currentRelation.isEmpty()) {
                    boolean isOldSibling = currentRelation.equals("Brother") || currentRelation.equals("Sister");
                    ((NominationActivity) requireActivity()).removeSelectedRelation(currentRelation);
                    if (isOldSibling) {
                        hasSiblingSelected = false;
                    }
                }

                // Add new relation
                currentRelation = newRelation;
                nomineeCard.setRelation(currentRelation);
                ((NominationActivity) requireActivity()).addSelectedRelation(currentRelation);

                if (isNewSibling) {
                    hasSiblingSelected = true;
                }

                // Update UI
                updateRelationSelection(relationSpinnerContainer, relation);
                if (!isInitialValidation) {
                    validateFields();
                }
            });
        }
    }

    private CardView createRelationCard(RelationItem relation) {
        CardView card = new CardView(requireContext());
        int cardSize = (int) (80 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(cardSize, cardSize);
        cardParams.setMargins(
                (int) (4 * getResources().getDisplayMetrics().density),
                0,
                (int) (4 * getResources().getDisplayMetrics().density),
                0
        );
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected_relation_bg));
        card.setLayoutParams(cardParams);
        card.setRadius((int) (12 * getResources().getDisplayMetrics().density));
        card.setCardElevation((int) (2 * getResources().getDisplayMetrics().density));

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(android.view.Gravity.CENTER);
        content.setPadding(8, 8, 8, 8);

        // Icon
        androidx.appcompat.widget.AppCompatImageView icon = new androidx.appcompat.widget.AppCompatImageView(requireContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (32 * getResources().getDisplayMetrics().density),
                (int) (32 * getResources().getDisplayMetrics().density)
        );
        icon.setLayoutParams(iconParams);
        icon.setImageResource(relation.getIconRes());

        // Text
        TextView text = new TextView(requireContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        text.setLayoutParams(textParams);
        text.setText(relation.getName());
        text.setTextSize(12);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        content.addView(icon);
        content.addView(text);
        card.addView(content);

        return card;
    }

    private void updateRelationSelection(LinearLayout container, RelationItem selectedRelation) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof CardView) {
                CardView card = (CardView) child;
                LinearLayout content = (LinearLayout) card.getChildAt(0);
                TextView text = (TextView) content.getChildAt(1);

                if (text.getText().toString().equals(selectedRelation.getName())) {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.relation_bg));
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.selected_relation_text));
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected_relation_bg));
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
                }
            }
        }
    }

    private void setupDatePicker() {
        dobEt.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    R.style.CustomDatePickerDialog,
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d",
                                dayOfMonth, month + 1, year);
                        dobEt.setText(date);
                        validateFields();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupButtons() {
        addButton.setOnClickListener(v -> {
            isInitialValidation = false;
            if (validateFields()) {
                saveNomineeDetails();
                ((NominationActivity) requireActivity()).addNewNomineeCard();
            }
        });

        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupTextWatchers() {
        percentageEt.addTextChangedListener(new TextWatcher() {
            private String beforeText = "";
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUpdating) {
                    beforeText = s.toString().trim();
                    if (!beforeText.isEmpty()) {
                        try {
                            float value = Float.parseFloat(beforeText);
                            if (value >= 0 && value <= 100) {
                                validatePercentageAndUpdateUI(beforeText);
                            }
                        } catch (NumberFormatException ignored) {
                            // Ignore parsing errors during typing
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update UI while typing
                if (!isUpdating) {
                    String currentText = s.toString().trim();
                    if (!currentText.isEmpty()) {
                        try {
                            float value = Float.parseFloat(currentText);
                            if (value >= 0 && value <= 100) {
                                validatePercentageAndUpdateUI(currentText);
                            }
                        } catch (NumberFormatException ignored) {
                            // Ignore parsing errors during typing
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                String currentText = s.toString().trim();
                isUpdating = true;

                try {
                    // Handle empty input
                    if (currentText.isEmpty()) {
                        percentageEt.setText("0");
                        percentageEt.setSelection(1);
                        validatePercentageAndUpdateUI("0");
                        isUpdating = false;
                        return;
                    }

                    // Prevent multiple dots
                    if (currentText.contains(".")) {
                        int dotCount = currentText.length() - currentText.replace(".", "").length();
                        if (dotCount > 1) {
                            percentageEt.setText(beforeText);
                            percentageEt.setSelection(beforeText.length());
                            isUpdating = false;
                            return;
                        }
                    }

                    // Parse and validate the number
                    float value = Float.parseFloat(currentText);
                    if (value < 0 || value > 100) {
                        percentageEt.setText(beforeText);
                        percentageEt.setSelection(beforeText.length());
                        percentageInputLayout.setError("Percentage must be between 0 and 100");
                    } else {
                        percentageInputLayout.setError(null);
                        validatePercentageAndUpdateUI(currentText);
                    }
                } catch (NumberFormatException e) {
                    percentageEt.setText(beforeText);
                    percentageEt.setSelection(beforeText.length());
                    percentageInputLayout.setError("Invalid percentage format");
                } finally {
                    isUpdating = false;
                }
            }
        });
    }

    private void validatePercentageAndUpdateUI(String percentageStr) {
        if (getActivity() == null) return;

        try {
            NominationActivity activity = (NominationActivity) getActivity();
            float currentPercentage = percentageStr.isEmpty() ? 0 : Float.parseFloat(percentageStr);

            // Get total percentage excluding current nominee
            float totalOtherPercentage = activity.calculateTotalPercentage() -
                    (nomineeCard != null && nomineeCard.getPercentage() != null && !nomineeCard.getPercentage().isEmpty() ?
                            Float.parseFloat(nomineeCard.getPercentage()) : 0);

            float newTotalPercentage = totalOtherPercentage + currentPercentage;

            if (currentPercentage < 0 || currentPercentage > 100) {
                percentageInputLayout.setError("Percentage must be between 0 and 100");
                return;
            }

            if (newTotalPercentage > 100) {
                float maxAllowed = 100 - totalOtherPercentage;
                String formattedMax = String.format(Locale.US, "%.1f", maxAllowed);

                percentageEt.setText(formattedMax);
                percentageEt.setSelection(formattedMax.length());
                percentageInputLayout.setError("Total percentage cannot exceed 100%");
                return;
            }

            // Valid input
            percentageInputLayout.setError(null);
            if (nomineeCard != null) {
                nomineeCard.setPercentage(percentageStr);
            }

            // Update submit button visibility based on total percentage
            activity.checkAndAddSubmitButton(position);
        } catch (NumberFormatException e) {
            percentageInputLayout.setError("Invalid percentage format");
        }
    }


    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle("Delete Nominee")
                .setMessage("Are you sure you want to delete this nominee?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (getActivity() instanceof NominationActivity) {
                        ((NominationActivity) getActivity()).removeCard(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void populateExistingData() {
        if (nomineeCard != null) {
            currentRelation = nomineeCard.getRelation();
            if (currentRelation != null && !currentRelation.isEmpty()) {
                for (RelationItem relation : relations) {
                    if (relation.getName().equals(currentRelation)) {
                        updateRelationSelection(relationSpinnerContainer, relation);
                        break;
                    }
                }
            }

            relativeNameEt.setText(nomineeCard.getRelativeName());
            dobEt.setText(nomineeCard.getDateOfBirth());
            percentageEt.setText(nomineeCard.getPercentage());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Clear previous errors if not in initial state
        if (!isInitialValidation) {
            nameInputLayout.setError(null);
            dobInputLayout.setError(null);
            percentageInputLayout.setError(null);

            if (currentRelation == null || currentRelation.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a relation", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            String name = relativeNameEt.getText().toString().trim();
            if (name.isEmpty()) {
                nameInputLayout.setError("Relative name is required");
                isValid = false;
            }

            String dob = dobEt.getText().toString().trim();
            if (dob.isEmpty()) {
                dobInputLayout.setError("Date of birth is required");
                isValid = false;
            }

            String percentageStr = percentageEt.getText().toString().trim();
            if (percentageStr.isEmpty()) {
                percentageInputLayout.setError("Percentage is required");
                isValid = false;
            } else {
                try {
                    float percentage = Float.parseFloat(percentageStr);
                    if (percentage <= 0 || percentage > 100) {
                        percentageInputLayout.setError("Percentage must be between 0 and 100");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    percentageInputLayout.setError("Invalid percentage format");
                    isValid = false;
                }
            }
        }

        updateSubmitButtonVisibility(isValid);
        return isValid;
    }
    private void updateSubmitButtonVisibility(boolean isValid) {
        if (submitButton != null && !isInitialValidation) {
            if (isValid) {
                // First set visibility to VISIBLE before any animations
                submitButton.setVisibility(View.VISIBLE);
                submitButton.setEnabled(true);

                // Reset the slide position without animation initially
                if (submitButton != null && submitButton.getWidth() > 0) {  // Add this check
                    submitButton.resetSlide();
                }

                // Find and update the slide text
                TextView slideText = submitButton.findViewById(R.id.slideText);
                if (slideText != null) {
                    slideText.setText("Slide to Submit");
                    slideText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    slideText.setAlpha(1.0f);
                }
            } else {
                // If button is currently visible, animate it out
                if (submitButton.getVisibility() == View.VISIBLE) {
                    submitButton.setEnabled(false);
                    submitButton.resetSlide(); // This will trigger the reset animation

                    // Find and update the slide text
                    TextView slideText = submitButton.findViewById(R.id.slideText);
                    if (slideText != null) {
                        slideText.setText("Total percentage must be 100%");
                        slideText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_400));
                        slideText.setAlpha(0.5f);
                    }

                    // Hide the button after animation completes
                    submitButton.postDelayed(() -> {
                        submitButton.setVisibility(View.GONE);
                    }, 300); // Match SLIDE_DURATION from SlidingButton
                } else {
                    submitButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private void saveNomineeDetails() {
        if (nomineeCard != null) {
            nomineeCard.setRelation(currentRelation);
            nomineeCard.setRelativeName(relativeNameEt.getText().toString().trim());
            nomineeCard.setDateOfBirth(dobEt.getText().toString().trim());
            nomineeCard.setPercentage(percentageEt.getText().toString().trim());
        }
    }

    public void toggleSubmitButton(boolean show) {
        if (submitButton == null) return;

        boolean shouldShowButton = show &&
                isValidForSubmission() &&
                getActivity() instanceof NominationActivity &&
                position == ((NominationActivity) getActivity()).getNomineeCards().size() - 1;

        if (shouldShowButton) {
            submitButton.setVisibility(View.VISIBLE);
            submitButton.setEnabled(true);
            submitButton.resetSlide();

            TextView slideText = submitButton.findViewById(R.id.slideText);
            if (slideText != null) {
                slideText.setText("Slide to Submit");
                slideText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                slideText.setAlpha(1.0f);
            }
        } else {
            if (submitButton.getVisibility() == View.VISIBLE) {
                submitButton.setEnabled(false);
                submitButton.resetSlide();
                submitButton.postDelayed(() -> submitButton.setVisibility(View.GONE), 300);
            } else {
                submitButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update delete button visibility when fragment resumes
        if (deleteButton != null) {
            deleteButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        }
        // Check submit button visibility
        if (getActivity() instanceof NominationActivity) {
            ((NominationActivity) getActivity()).checkAndAddSubmitButton(position);
        }
    }

    public void resetRelationSpinner() {
        setupRelationSpinner();
    }

    public String getCurrentRelation() {
        return currentRelation;
    }

    public void setPosition(int position) {
        this.position = position;
        if (cardTitleText != null) {
            cardTitleText.setText(String.format(Locale.getDefault(), "Nominee %d", position + 1));
        }
        // Update delete button visibility when position changes
        if (deleteButton != null) {
            deleteButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        }
    }

    public boolean isValidForSubmission() {
        return validateFields() && nomineeCard != null &&
                nomineeCard.getRelation() != null &&
                !nomineeCard.getRelation().isEmpty() &&
                nomineeCard.getRelativeName() != null &&
                !nomineeCard.getRelativeName().isEmpty() &&
                nomineeCard.getDateOfBirth() != null &&
                !nomineeCard.getDateOfBirth().isEmpty() &&
                nomineeCard.getPercentage() != null &&
                !nomineeCard.getPercentage().isEmpty();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up slide complete listener to prevent memory leaks
        if (submitButton != null) {
            submitButton.setOnSlideCompleteListener(null);
        }
    }
}