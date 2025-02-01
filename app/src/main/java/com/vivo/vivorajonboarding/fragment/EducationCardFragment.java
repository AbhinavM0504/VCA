package com.vivo.vivorajonboarding.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.EducationActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.EducationCard;

public class EducationCardFragment extends Fragment {
    private TextInputEditText schoolEt, percentageEt, boardEt, yearOfPassingEt;
    private MaterialButton uploadButton, addButton, deleteButton;
    private ActivityResultLauncher<String> documentPicker;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private EducationCard educationCard;
    private Uri selectedDocumentUri;
    private int position;

    public static EducationCardFragment newInstance(EducationCard card) {
        EducationCardFragment fragment = new EducationCardFragment();
        Bundle args = new Bundle();
        args.putParcelable("card", card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_education_card, container, false);

        if (getArguments() != null) {
            educationCard = getArguments().getParcelable("card");
        }

        initializeViews(view);
        setupDocumentPickers();
        setupButtons();
        populateExistingData();

        return view;
    }

    private void initializeViews(View view) {
        schoolEt = view.findViewById(R.id.schoolEt);
        percentageEt = view.findViewById(R.id.percentageEt);
        boardEt = view.findViewById(R.id.boardEt);
        yearOfPassingEt = view.findViewById(R.id.yearOfPassingEt);
        uploadButton = view.findViewById(R.id.uploadButton);
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        if (educationCard != null && educationCard.isMandatory()) {
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void setupDocumentPickers() {
        documentPicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedDocumentUri = uri;
                        handleDocument(uri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && selectedDocumentUri != null) {
                        handleDocument(selectedDocumentUri);
                    }
                }
        );
    }

    private void setupButtons() {
        uploadButton.setOnClickListener(v -> showDocumentOptions());
        addButton.setOnClickListener(v -> {
            if (validateFields()) {
                saveEducationDetails();
                ((EducationActivity) requireActivity()).addNewEducationCard();
            }
        });
        deleteButton.setOnClickListener(v -> {
            if (!educationCard.isMandatory()) {
                ((EducationActivity) requireActivity()).removeCard(position);
            }
        });
    }

    private void showDocumentOptions() {
        // Implement document picker or camera launcher based on user choice
        documentPicker.launch("application/pdf,image/*");
    }

    private void handleDocument(Uri uri) {
        // Handle the selected document
        Toast.makeText(requireContext(), "Document selected: " + uri.getLastPathSegment(),
                Toast.LENGTH_SHORT).show();
    }

    private void populateExistingData() {
        if (educationCard != null) {
            schoolEt.setText(educationCard.getSchoolName());
            percentageEt.setText(educationCard.getPercentage());
            boardEt.setText(educationCard.getBoard());
            yearOfPassingEt.setText(educationCard.getYearOfPassing());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (schoolEt.getText().toString().trim().isEmpty()) {
            schoolEt.setError("Required field");
            isValid = false;
        }

        if (percentageEt.getText().toString().trim().isEmpty()) {
            percentageEt.setError("Required field");
            isValid = false;
        } else {
            try {
                float percentage = Float.parseFloat(percentageEt.getText().toString());
                if (percentage < 0 || percentage > 100) {
                    percentageEt.setError("Invalid percentage");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                percentageEt.setError("Invalid number");
                isValid = false;
            }
        }

        if (boardEt.getText().toString().trim().isEmpty()) {
            boardEt.setError("Required field");
            isValid = false;
        }

        if (yearOfPassingEt.getText().toString().trim().isEmpty()) {
            yearOfPassingEt.setError("Required field");
            isValid = false;
        }

        return isValid;
    }

    private void saveEducationDetails() {
        if (educationCard != null) {
            educationCard.setSchoolName(schoolEt.getText().toString().trim());
            educationCard.setPercentage(percentageEt.getText().toString().trim());
            educationCard.setBoard(boardEt.getText().toString().trim());
            educationCard.setYearOfPassing(yearOfPassingEt.getText().toString().trim());

            if (selectedDocumentUri != null) {
                educationCard.setDocumentUri(selectedDocumentUri.toString());
            }
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }
}