// EducationCardFragment.java
package com.vivo.vivorajonboarding.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.EducationActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.EducationCard;

public class EducationCardFragment extends Fragment {
    private TextInputEditText schoolEt, percentageEt, boardEt, yearOfPassingEt;
    private MaterialButton uploadButton, addButton, deleteButton, cameraButton;
    private MaterialCardView uploadCard;
    private TextView titleText, selectedFileText,cardTitleText;
    private ActivityResultLauncher<String> documentPicker;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private EducationCard educationCard;
    private Uri selectedDocumentUri;
    private int position;
    private static final String ARG_CARD = "card";
    private static final String ARG_POSITION = "position";
  
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public static EducationCardFragment newInstance(EducationCard card, int position) {
        EducationCardFragment fragment = new EducationCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_education_card, container, false);

        if (getArguments() != null) {
            educationCard = getArguments().getParcelable(ARG_CARD);
            position = getArguments().getInt(ARG_POSITION);
        }

        initializeViews(view);
        setupDocumentPickers();
        setupPermissionLauncher();
        setupButtons();
        populateExistingData();
        updateUploadCardState();

        return view;
    }

    private void setupPermissionLauncher() {
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(requireContext(),
                                "Camera permission is required to take photos",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startCamera() {

        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews(View view) {
        schoolEt = view.findViewById(R.id.schoolEt);
        percentageEt = view.findViewById(R.id.percentageEt);
        boardEt = view.findViewById(R.id.boardEt);
        yearOfPassingEt = view.findViewById(R.id.yearOfPassingEt);
        uploadButton = view.findViewById(R.id.uploadButton);
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        cameraButton = view.findViewById(R.id.cameraButton);
        uploadCard = view.findViewById(R.id.uploadCard);
        titleText = view.findViewById(R.id.titleText);
        selectedFileText = view.findViewById(R.id.selectedFileText);
        cardTitleText=view.findViewById(R.id.cardTitle);

        cardTitleText.setText(String.format("%s Academic Background", educationCard != null ? educationCard.getEducationLevel() : ""));

        // Set title based on education level
        titleText.setText(String.format("Upload your %s Marksheet",
                educationCard != null ? educationCard.getEducationLevel() : ""));

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
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Handle camera photo
                        handleDocument(selectedDocumentUri);
                    }
                }
        );
    }

    private void setupButtons() {
        uploadCard.setOnClickListener(v -> showDocumentOptions());
        uploadButton.setOnClickListener(v -> {
            if (validateFields()) {
                saveEducationDetails();
                Toast.makeText(requireContext(), "Details saved successfully",
                        Toast.LENGTH_SHORT).show();
            }
        });

        addButton.setOnClickListener(v -> {
            if (validateFields()) {
                saveEducationDetails();
                ((EducationActivity) requireActivity()).addNewEducationCard();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (!educationCard.isMandatory()) {
                showDeleteConfirmation();
            }
        });

        cameraButton.setOnClickListener(v -> launchCamera());
    }

    private void showDocumentOptions() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Document Type")
                .setItems(new String[]{"PDF Document", "Image File"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            documentPicker.launch("application/pdf");
                            break;
                        case 1:
                            documentPicker.launch("image/*");
                            break;
                    }
                })
                .show();
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDocument(Uri uri) {
        selectedDocumentUri = uri;
        String fileName = uri.getLastPathSegment();
        selectedFileText.setText(fileName);
        selectedFileText.setVisibility(View.VISIBLE);
        Toast.makeText(requireContext(), "Document selected successfully",
                Toast.LENGTH_SHORT).show();
        updateUploadCardState();
    }

    private void updateUploadCardState() {
        if (selectedDocumentUri != null) {
            uploadCard.setStrokeColor(requireContext().getColor(R.color.primary));
            uploadCard.setStrokeWidth(2);
        }
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Education Record")
                .setMessage("Are you sure you want to delete this education record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (getActivity() instanceof EducationActivity) {
                        ((EducationActivity) getActivity()).removeCard(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void populateExistingData() {
        if (educationCard != null) {
            schoolEt.setText(educationCard.getSchoolName());
            percentageEt.setText(educationCard.getPercentage());
            boardEt.setText(educationCard.getBoard());
            yearOfPassingEt.setText(educationCard.getYearOfPassing());

            if (educationCard.getDocumentUri() != null) {
                selectedDocumentUri = Uri.parse(educationCard.getDocumentUri());
                updateUploadCardState();
            }
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Clear previous errors
        schoolEt.setError(null);
        percentageEt.setError(null);
        boardEt.setError(null);
        yearOfPassingEt.setError(null);

        if (schoolEt.getText().toString().trim().isEmpty()) {
            schoolEt.setError("School name is required");
            isValid = false;
        }

        String percentageStr = percentageEt.getText().toString().trim();
        if (percentageStr.isEmpty()) {
            percentageEt.setError("Percentage is required");
            isValid = false;
        } else {
            try {
                float percentage = Float.parseFloat(percentageStr);
                if (percentage < 0 || percentage > 100) {
                    percentageEt.setError("Percentage must be between 0 and 100");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                percentageEt.setError("Invalid percentage format");
                isValid = false;
            }
        }

        if (boardEt.getText().toString().trim().isEmpty()) {
            boardEt.setError("Board name is required");
            isValid = false;
        }

        String yearStr = yearOfPassingEt.getText().toString().trim();
        if (yearStr.isEmpty()) {
            yearOfPassingEt.setError("Year of passing is required");
            isValid = false;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                int currentYear = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currentYear = java.time.Year.now().getValue();
                }
                if (year < 1900 || year > currentYear) {
                    yearOfPassingEt.setError("Invalid year");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                yearOfPassingEt.setError("Invalid year format");
                isValid = false;
            }
        }

        if (educationCard.isMandatory() && selectedDocumentUri == null) {
            Toast.makeText(requireContext(),
                    "Please upload your marksheet document", Toast.LENGTH_SHORT).show();
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