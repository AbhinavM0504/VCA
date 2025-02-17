package com.vivo.vivorajonboarding.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.InsuranceNominationActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.SlidingButton;
import com.vivo.vivorajonboarding.SlidingButtonOne;
import com.vivo.vivorajonboarding.model.InsuranceNomineeCard;
import com.vivo.vivorajonboarding.model.NomineeCard;
import com.vivo.vivorajonboarding.model.RelationItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InsuranceNomineeCardFragment extends Fragment {
    private static final String ARG_CARD = "card";
    private static final String ARG_POSITION = "position";

    private CardView relationSpinnerCard;
    private LinearLayout relationSpinnerContainer;
    private TextInputLayout relationInputLayout, nameInputLayout, dobInputLayout;
    private TextInputEditText relativeNameEt, dobEt;
    private MaterialButton addButton, deleteButton;
    private TextView cardTitleText;
    private FloatingActionButton addAadhaarFrontImgBtn, addAadhaarBackImgBtn;
    private ImageView aadhaarFrontImageIv, aadhaarBackImageIv;

    private InsuranceNomineeCard nomineeCard;
    private int position;
    private String currentRelation;
    private LinearLayout submitContainer;
    private SlidingButtonOne submitButton;

    private final RelationItem[] relations = {
            new RelationItem("Father", R.drawable.ic_father_one),
            new RelationItem("Mother", R.drawable.ic_mother_one),
            new RelationItem("Spouse", R.drawable.ic_spouse_one),
            new RelationItem("Son", R.drawable.ic_son_one),
            new RelationItem("Daughter", R.drawable.ic_daughter_one)
    };

    private ActivityResultLauncher<Intent> frontImageLauncher;
    private ActivityResultLauncher<Intent> backImageLauncher;

    public static InsuranceNomineeCardFragment newInstance(InsuranceNomineeCard card, int position) {
        InsuranceNomineeCardFragment fragment = new InsuranceNomineeCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerImageLaunchers();
    }

    private void registerImageLaunchers() {
        frontImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            handleImageResult(data.getData(), true);
                        }
                    }
                }
        );

        backImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            handleImageResult(data.getData(), false);
                        }
                    }
                }
        );
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insurance_nomination_card, container, false);

        if (getArguments() != null) {
            nomineeCard = getArguments().getParcelable(ARG_CARD);
            position = getArguments().getInt(ARG_POSITION);
        }

        initializeViews(view);
        setupRelationSpinner();
        setupDatePicker();
        setupButtons();
        setupImageButtons();
        setupValidation();
        initializeSubmitButton(view); // Add this new method call

        if (nomineeCard != null && nomineeCard.getRelation() != null) {
            populateExistingData();
        }

        return view;
    }
    private void initializeSubmitButton(View view) {
        submitContainer = view.findViewById(R.id.submitContainer);
        submitButton = view.findViewById(R.id.submitButton);

        if (submitButton != null) {
            submitButton.setOnSlideCompleteListener(() -> {
                if (validateFields()) {
                    ((InsuranceNominationActivity) requireActivity()).onSubmitForm();
                }
            });
        }
    }

    private void initializeViews(View view) {
        relationSpinnerCard = view.findViewById(R.id.relationSpinnerCard);
        relationSpinnerContainer = view.findViewById(R.id.relationSpinnerContainer);
        relationInputLayout = view.findViewById(R.id.relationInputLayout);
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        dobInputLayout = view.findViewById(R.id.dobInputLayout);
        relativeNameEt = view.findViewById(R.id.relativeNameEt);
        dobEt = view.findViewById(R.id.dobEt);
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        cardTitleText = view.findViewById(R.id.cardTitle);
        addAadhaarFrontImgBtn = view.findViewById(R.id.addAadhaarFrontImgBtn);
        addAadhaarBackImgBtn = view.findViewById(R.id.addAadhaarBackImgBtn);
        aadhaarFrontImageIv = view.findViewById(R.id.aadhaarFrontImageIv);
        aadhaarBackImageIv = view.findViewById(R.id.aadhaarBackImageIv);


        cardTitleText.setText(String.format(Locale.getDefault(), "Nominee %d", position + 1));
        deleteButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    private void setupRelationSpinner() {
        relationSpinnerContainer.removeAllViews();
        List<RelationItem> availableRelations = new ArrayList<>();
        InsuranceNominationActivity activity = (InsuranceNominationActivity) requireActivity();
        Set<String> selectedRelations = activity.getSelectedRelations();

        // Check for existing child relations
        boolean hasChild = selectedRelations.contains("Son") || selectedRelations.contains("Daughter");

        for (RelationItem relation : relations) {
            String relationName = relation.getName();
            boolean isChild = relationName.equals("Son") || relationName.equals("Daughter");

            // Add relation if:
            // 1. It's a child relation and more children are allowed
            // 2. It's not a child relation and not already selected
            // 3. It's the current card's relation
            if ((isChild && activity.canAddChildren() && !hasChild) ||
                    (!isChild && !selectedRelations.contains(relationName)) ||
                    (nomineeCard.getRelation() != null && nomineeCard.getRelation().equals(relationName))) {
                availableRelations.add(relation);
            }
        }

        for (RelationItem relation : availableRelations) {
            CardView relationCard = createRelationCard(relation);
            relationSpinnerContainer.addView(relationCard);

            relationCard.setOnClickListener(v -> handleRelationSelection(relation));
        }

        // Update initial selection if exists
        if (currentRelation != null && !currentRelation.isEmpty()) {
            RelationItem selectedRelation = findRelationByName(currentRelation);
            if (selectedRelation != null) {
                updateRelationSelection(relationSpinnerContainer, selectedRelation);
            }
        }
    }

    private CardView createRelationCard(RelationItem relation) {
        float density = getResources().getDisplayMetrics().density;

        // Create CardView
        CardView card = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                (int) (80 * density),
                (int) (80 * density)
        );
        cardParams.setMargins(
                (int) (4 * density),
                (int) (4 * density),
                (int) (4 * density),
                (int) (4 * density)
        );
        card.setLayoutParams(cardParams);
        card.setRadius(12 * density);
        card.setCardElevation(2 * density);
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                currentRelation != null && currentRelation.equals(relation.getName())
                        ? R.color.relation_bg_purple
                        : R.color.selected_relation_bg_pruple));

        // Create content container
        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER);
        content.setPadding(
                (int) (8 * density),
                (int) (8 * density),
                (int) (8 * density),
                (int) (8 * density)
        );

        // Add icon
        AppCompatImageView icon = new AppCompatImageView(requireContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (32 * density),
                (int) (32 * density)
        );
        iconParams.bottomMargin = (int) (4 * density);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(relation.getIconRes());

        // Add text
        TextView text = new TextView(requireContext());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        text.setText(relation.getName());
        text.setTextSize(12);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextColor(ContextCompat.getColor(requireContext(),
                currentRelation != null && currentRelation.equals(relation.getName())
                        ? R.color.selected_relation_text_purple
                        : R.color.text_primary));

        content.addView(icon);
        content.addView(text);
        card.addView(content);

        return card;
    }

    private void handleRelationSelection(RelationItem newRelation) {
        InsuranceNominationActivity activity = (InsuranceNominationActivity) requireActivity();
        String newRelationName = newRelation.getName();
        boolean isChild = newRelationName.equals("Son") || newRelationName.equals("Daughter");

        // Remove old relation if exists
        if (currentRelation != null && !currentRelation.isEmpty()) {
            activity.removeSelectedRelation(currentRelation);
        }

        // Add new relation
        currentRelation = newRelationName;
        nomineeCard.setRelation(currentRelation);
        activity.addSelectedRelation(currentRelation);

        // Update UI
        updateRelationSelection(relationSpinnerContainer, newRelation);


        // If the selection affects available relations (like for children),
        // refresh the entire spinner
        if (isChild) {
            setupRelationSpinner();
        }
    }

    private void updateRelationSelection(LinearLayout container, RelationItem selectedRelation) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof CardView) {
                CardView card = (CardView) child;
                LinearLayout content = (LinearLayout) card.getChildAt(0);
                TextView text = (TextView) content.getChildAt(1);

                boolean isSelected = text.getText().toString().equals(selectedRelation.getName());
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                        isSelected ? R.color.relation_bg_purple : R.color.selected_relation_bg_pruple));
                text.setTextColor(ContextCompat.getColor(requireContext(),
                        isSelected ? R.color.white : R.color.text_primary));
            }
        }
    }

    private RelationItem findRelationByName(String name) {
        for (RelationItem relation : relations) {
            if (relation.getName().equals(name)) {
                return relation;
            }
        }
        return null;
    }

    private void setupDatePicker() {
        dobEt.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        dobEt.setText(date);
                        nomineeCard.setDateOfBirth(date);

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
            if (validateFields()) {
                ((InsuranceNominationActivity) requireActivity()).addNewNomineeCard();
            }
        });

        deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Nominee")
                    .setMessage("Are you sure you want to delete this nominee?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Get activity before removing the card
                        InsuranceNominationActivity activity =
                                (InsuranceNominationActivity) requireActivity();
                        activity.removeCard(position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupImageButtons() {
        addAadhaarFrontImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            frontImageLauncher.launch(intent);
        });

        addAadhaarBackImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            backImageLauncher.launch(intent);
        });
    }

    private void handleImageResult(Uri imageUri, boolean isFront) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    imageUri
            );

            if (isFront) {
                aadhaarFrontImageIv.setImageBitmap(bitmap);
                nomineeCard.setAadhaarFrontImage(imageUri.toString());
            } else {
                aadhaarBackImageIv.setImageBitmap(bitmap);
                nomineeCard.setAadhaarBackImage(imageUri.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupValidation() {
        relativeNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                nomineeCard.setRelativeName(s.toString());
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (currentRelation == null || currentRelation.isEmpty()) {
            relationInputLayout.setError("Please select a relation");
            isValid = false;
        } else {
            relationInputLayout.setError(null);
        }

        String name = relativeNameEt.getText().toString().trim();
        if (name.isEmpty()) {
            nameInputLayout.setError("Please enter relative name");
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

        String dob = dobEt.getText().toString().trim();
        if (dob.isEmpty()) {
            dobInputLayout.setError("Please select date of birth");
            isValid = false;
        } else {
            dobInputLayout.setError(null);
        }

        if (nomineeCard.getAadhaarFrontImage() == null) {
            Toast.makeText(requireContext(), "Please upload Aadhaar front image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (nomineeCard.getAadhaarBackImage() == null) {
            Toast.makeText(requireContext(), "Please upload Aadhaar back image", Toast.LENGTH_SHORT).show();
            isValid = false;
        }



        return isValid;
    }

    private void populateExistingData() {
        currentRelation = nomineeCard.getRelation();
        relativeNameEt.setText(nomineeCard.getRelativeName());
        dobEt.setText(nomineeCard.getDateOfBirth());

        if (nomineeCard.getAadhaarFrontImage() != null) {
            try {
                Uri frontImageUri = Uri.parse(nomineeCard.getAadhaarFrontImage());
                Bitmap frontBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        frontImageUri
                );
                aadhaarFrontImageIv.setImageBitmap(frontBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (nomineeCard.getAadhaarBackImage() != null) {
            try {
                Uri backImageUri = Uri.parse(nomineeCard.getAadhaarBackImage());
                Bitmap backBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        backImageUri
                );
                aadhaarBackImageIv.setImageBitmap(backBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void toggleSubmitButton(boolean show) {
        if (submitContainer == null || submitButton == null) {
            // Views might not be initialized yet
            return;
        }

        // Always reset the button state when toggling visibility
        submitButton.resetSlide();

        // Update visibility
        submitContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        submitButton.setVisibility(show ? View.VISIBLE : View.GONE);


    }

    // RelationItem class
    public static class RelationItem {
        private final String name;
        private final int iconRes;

        public RelationItem(String name, int iconRes) {
            this.name = name;
            this.iconRes = iconRes;
        }

        public String getName() {
            return name;
        }

        public int getIconRes() {
            return iconRes;
        }
    }
}
