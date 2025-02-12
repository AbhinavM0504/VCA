// EducationCardFragment.java
package com.vivo.vivorajonboarding.fragment;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.EducationActivity;

import com.vivo.vivorajonboarding.R;

import com.vivo.vivorajonboarding.api.ApiService;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.model.ApiResponse1;
import com.vivo.vivorajonboarding.model.DocumentUploadResponse;
import com.vivo.vivorajonboarding.model.EducationCard;
import com.vivo.vivorajonboarding.model.EducationRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationCardFragment extends Fragment {
    private TextInputEditText schoolEt, percentageEt, boardEt, yearOfPassingEt;
    private TextInputEditText courseEt, universityEt, collegeEt;
    private MaterialButton uploadButton, addButton, deleteButton,exitButton;
    private MaterialCardView uploadCard;
    private TextView titleText, selectedFileText, cardTitleText;
    private LinearLayout schoolFields, graduationFields;
    private ActivityResultLauncher<String> documentPicker;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private EducationCard educationCard;
    private Uri selectedDocumentUri;
    private int position;
    private static final String ARG_CARD = "card";
    private static final String ARG_POSITION = "position";
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private boolean isDataSaved = false;
    private ApiService apiService;
    private ProgressDialog progressDialog;


    public static EducationCardFragment newInstance(EducationCard card, int position) {
        EducationCardFragment fragment = new EducationCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getInstance().getApi();
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
        updateFieldVisibility();
        updateButtonVisibility();
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

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews(View view) {
        // Initialize existing fields
        schoolEt = view.findViewById(R.id.schoolEt);
        percentageEt = view.findViewById(R.id.percentageEt);
        boardEt = view.findViewById(R.id.boardEt);
        yearOfPassingEt = view.findViewById(R.id.yearOfPassingEt);

        // Initialize new graduation fields
        courseEt = view.findViewById(R.id.courseEt);
        universityEt = view.findViewById(R.id.universityEt);
        collegeEt = view.findViewById(R.id.collegeEt);

        // Initialize containers
        schoolFields = view.findViewById(R.id.schoolFields);
        graduationFields = view.findViewById(R.id.graduationFields);

        // Initialize other views
        uploadButton = view.findViewById(R.id.uploadButton);
        addButton = view.findViewById(R.id.addButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        uploadCard = view.findViewById(R.id.uploadCard);
        titleText = view.findViewById(R.id.titleText);
        selectedFileText = view.findViewById(R.id.selectedFileText);
        cardTitleText = view.findViewById(R.id.cardTitle);
        exitButton=view.findViewById(R.id.exitButton);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Saving education details...");
        progressDialog.setCancelable(false);

        updateLabels();
    }


    private void updateLabels() {
        if (educationCard != null) {
            cardTitleText.setText(String.format("%s Academic Background", educationCard.getEducationLevel()));
            titleText.setText(String.format("Upload your %s Marksheet", educationCard.getEducationLevel()));
            if (educationCard.isMandatory()) {
                deleteButton.setVisibility(View.GONE);
            }
        }
    }

    public void updateButtonVisibility() {
        boolean isLastCard = (getActivity() instanceof EducationActivity) &&
                ((EducationActivity) getActivity()).isLastCard(position);

        if (position == 0 && !isDataSaved) {
            // First mandatory card, not saved
            uploadButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            exitButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else if (isDataSaved) {
            // After successful save
            uploadButton.setVisibility(View.GONE);
            addButton.setVisibility(isLastCard ? View.VISIBLE : View.GONE);
            // Show exit button only if this is the last card AND data is saved
            exitButton.setVisibility(isLastCard ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(View.GONE);
        } else {
            // Optional cards that aren't saved yet
            uploadButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
            exitButton.setVisibility(View.GONE);
            deleteButton.setVisibility(educationCard.isMandatory() ? View.GONE : View.VISIBLE);
        }
    }

    private void updateFieldVisibility() {
        if (educationCard != null) {
            if (educationCard.isSchoolEducation()) {
                schoolFields.setVisibility(View.VISIBLE);
                graduationFields.setVisibility(View.GONE);
            } else {
                schoolFields.setVisibility(View.GONE);
                graduationFields.setVisibility(View.VISIBLE);
            }
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
                uploadDocumentAndSaveDetails();
            }
        });

        addButton.setOnClickListener(v -> {
            ((EducationActivity) requireActivity()).addNewEducationCard();
        });

        deleteButton.setOnClickListener(v -> {
            if (!educationCard.isMandatory() && !isDataSaved) {
                showDeleteConfirmation();
            }
        });

        exitButton.setOnClickListener(v -> showExitConfirmation());


    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    requireActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void uploadDocumentAndSaveDetails() {
        if (selectedDocumentUri == null) {
            Toast.makeText(requireContext(), "Please select a document first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        try {
            // Create RequestBody from URI using input stream
            ContentResolver contentResolver = requireContext().getContentResolver();
            String mimeType = contentResolver.getType(selectedDocumentUri);

            // Validate mime type against allowed types
            Set<String> allowedTypes = new HashSet<>(Arrays.asList(
                    "image/jpeg", "image/png", "image/jpg", "application/pdf"
            ));

            if (!allowedTypes.contains(mimeType)) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Invalid file type. Please select an image (JPG/PNG) or PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            String fileName = getFileNameFromUri(selectedDocumentUri);

            InputStream inputStream = contentResolver.openInputStream(selectedDocumentUri);
            if (inputStream == null) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Could not read the selected file", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] bytes = IOUtils.toByteArray(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);

            // Create multipart form data with "document" as the key to match PHP $_FILES['document']
            MultipartBody.Part documentPart = MultipartBody.Part.createFormData("document", fileName, requestFile);

            apiService.uploadDocument(documentPart).enqueue(new Callback<DocumentUploadResponse>() {
                @Override
                public void onResponse(Call<DocumentUploadResponse> call, Response<DocumentUploadResponse> response) {
                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        DocumentUploadResponse uploadResponse = response.body();

                        if (uploadResponse.isSuccess()) {
                            // Document uploaded successfully, now save education details
                            saveEducationDetails(uploadResponse.getDocumentUrl());
                        } else {
                            // API returned success=false
                            String errorMessage = uploadResponse.getMessage();
                            Toast.makeText(requireContext(),
                                    errorMessage.isEmpty() ? "Upload failed" : errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // HTTP error
                        try {
                            String errorBody = response.errorBody() != null ?
                                    response.errorBody().string() : "Unknown error";
                            Toast.makeText(requireContext(),
                                    "Upload failed: " + errorBody,
                                    Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(requireContext(),
                                    "Upload failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DocumentUploadResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(),
                    "Error preparing file: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void showDocumentOptions() {
        if (!isDataSaved) {
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
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDocument(Uri uri) {
        if (!isDataSaved) {
            selectedDocumentUri = uri;
            String fileName = uri.getLastPathSegment();
            selectedFileText.setText(fileName);
            selectedFileText.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Document selected successfully",
                    Toast.LENGTH_SHORT).show();
            updateUploadCardState();
        }
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
            if (educationCard.isSchoolEducation()) {
                schoolEt.setText(educationCard.getSchoolName());
                boardEt.setText(educationCard.getBoard());
            } else {
                courseEt.setText(educationCard.getCourse());
                universityEt.setText(educationCard.getUniversity());
                collegeEt.setText(educationCard.getCollege());
            }

            percentageEt.setText(educationCard.getPercentage());
            yearOfPassingEt.setText(educationCard.getYearOfPassing());

            if (educationCard.getDocumentUri() != null) {
                selectedDocumentUri = Uri.parse(educationCard.getDocumentUri());
                updateUploadCardState();

                // If document URI exists, it means data was previously saved
                if (isDataSaved) {
                    disableAllInputs();
                }
            }
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Clear previous errors
        if (educationCard.isSchoolEducation()) {
            schoolEt.setError(null);
            boardEt.setError(null);

            if (schoolEt.getText().toString().trim().isEmpty()) {
                schoolEt.setError("School name is required");
                isValid = false;
            }
            if (boardEt.getText().toString().trim().isEmpty()) {
                boardEt.setError("Board name is required");
                isValid = false;
            }
        } else {
            courseEt.setError(null);
            universityEt.setError(null);
            collegeEt.setError(null);

            if (courseEt.getText().toString().trim().isEmpty()) {
                courseEt.setError("Course name is required");
                isValid = false;
            }
            if (universityEt.getText().toString().trim().isEmpty()) {
                universityEt.setError("University name is required");
                isValid = false;
            }
            if (collegeEt.getText().toString().trim().isEmpty()) {
                collegeEt.setError("College name is required");
                isValid = false;
            }
        }

        // Common field validation
        percentageEt.setError(null);
        yearOfPassingEt.setError(null);

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

        String yearStr = yearOfPassingEt.getText().toString().trim();
        if (yearStr.isEmpty()) {
            yearOfPassingEt.setError("Year of passing is required");
            isValid = false;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                int currentYear = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentYear = Year.now().getValue();
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

        if (selectedDocumentUri == null) {
            Toast.makeText(requireContext(),
                    "Please upload your marksheet document", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void saveEducationDetails(String documentUrl) {
        // Save the current form data to educationCard
        updateEducationCard();
        String userId = "";
        if (getActivity() instanceof EducationActivity) {
           userId= ((EducationActivity) getActivity()).getUserId();
        }
        EducationRequest request = new EducationRequest(userId, educationCard);
        request.setMarksheetUri(documentUrl);

        apiService.saveEducation(request).enqueue(new Callback<ApiResponse1>() {
            @Override
            public void onResponse(Call<ApiResponse1> call, Response<ApiResponse1> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isDataSaved = true;
                    disableAllInputs();
                    updateButtonVisibility();

                    // Enable adding next card in activity
                    ((EducationActivity) requireActivity()).enableAddButton();

                    Toast.makeText(requireContext(), "Details saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse1> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableAllInputs() {
        // Disable school education fields
        schoolEt.setEnabled(false);
        percentageEt.setEnabled(false);
        boardEt.setEnabled(false);
        yearOfPassingEt.setEnabled(false);

        // Disable graduation fields
        courseEt.setEnabled(false);
        universityEt.setEnabled(false);
        collegeEt.setEnabled(false);

        // Disable upload functionality
        uploadCard.setEnabled(false);
        uploadCard.setClickable(false);


        // Update the visual appearance to indicate disabled state
        uploadCard.setAlpha(0.5f);

    }

    private void updateEducationCard() {
        if (educationCard.isSchoolEducation()) {
            educationCard.setSchoolName(schoolEt.getText().toString().trim());
            educationCard.setBoard(boardEt.getText().toString().trim());
        } else {
            educationCard.setCourse(courseEt.getText().toString().trim());
            educationCard.setUniversity(universityEt.getText().toString().trim());
            educationCard.setCollege(collegeEt.getText().toString().trim());
        }

        educationCard.setPercentage(percentageEt.getText().toString().trim());
        educationCard.setYearOfPassing(yearOfPassingEt.getText().toString().trim());
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDataSaved() {
        return isDataSaved;
    }
}