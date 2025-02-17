package com.vivo.vivorajonboarding.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.vivo.vivorajonboarding.PastExperienceDetailsActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.api.ApiService;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.model.ApiResponse3;
import com.vivo.vivorajonboarding.model.ExperienceCard;
import com.vivo.vivorajonboarding.model.ExperienceSubmitRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExperienceCardFragment extends Fragment {
    private static final String ARG_EXPERIENCE_CARD = "experience_card";
    private static final String ARG_POSITION = "position";
    private static final String ARG_IS_LAST = "is_last";

    private ExperienceCard experienceCard;
    private int position;
    private boolean isLast;

    // UI Components
    private ImageView experienceImageIv;
    private TextView fileNameTv;
    private Uri fileUri = null;
    private boolean isPDF = false;

    // Permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private String[] cameraPermissions13;
    private String[] storagePermissions13;
    private View rootView;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> pdfLauncher;

    public static ExperienceCardFragment newInstance(ExperienceCard card, int position, boolean isLast) {
        ExperienceCardFragment fragment = new ExperienceCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXPERIENCE_CARD, (Serializable) card);
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_IS_LAST, isLast);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            experienceCard = (ExperienceCard) getArguments().getSerializable(ARG_EXPERIENCE_CARD);
            position = getArguments().getInt(ARG_POSITION);
            isLast = getArguments().getBoolean(ARG_IS_LAST);
        }

        initializePermissions();
        initializeActivityLaunchers();
    }

    private void initializePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
            storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }


        cameraPermissions13 = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
    }

    private void initializeActivityLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        handleImageResult(fileUri);
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();
                        handleImageResult(fileUri);
                    }
                }
        );

        pdfLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();
                        handlePdfResult(fileUri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_experience_card, container, false);

        initializeViews(rootView);
        setupListeners(rootView);
        updateButtonsVisibility(rootView);

        return rootView;
    }

    private void initializeViews(View view) {
        experienceImageIv = view.findViewById(R.id.experienceImageIv);
        fileNameTv = view.findViewById(R.id.fileNameTv);
        ImageButton addExpImgBtn = view.findViewById(R.id.addExpImgBtn);

        TextInputEditText companyNameEt = view.findViewById(R.id.companyNameEt);
        TextInputEditText jobTitleEt = view.findViewById(R.id.jobTitleEt);
        TextInputEditText startDateEt = view.findViewById(R.id.startDateEt);
        TextInputEditText endDateEt = view.findViewById(R.id.endDateEt);

        // Set existing data if available
        if (experienceCard != null) {
            companyNameEt.setText(experienceCard.getCompanyName());
            jobTitleEt.setText(experienceCard.getJobTitle());
            startDateEt.setText(experienceCard.getStartDate());
            endDateEt.setText(experienceCard.getEndDate());

            // Disable fields if card is submitted
            if (experienceCard.isSubmitted()) {
                disableAllFields(view);
            }

            if (experienceCard.getFileUri() != null) {
                fileUri = Uri.parse(experienceCard.getFileUri());
                if (experienceCard.isPDF()) {
                    handlePdfResult(fileUri);
                } else {
                    handleImageResult(fileUri);
                }
            }
        }

        addExpImgBtn.setOnClickListener(v -> showFilePickerOptions());
    }

    private void disableAllFields(View view) {
        // Disable all input fields
        view.findViewById(R.id.companyNameEt).setEnabled(false);
        view.findViewById(R.id.jobTitleEt).setEnabled(false);
        view.findViewById(R.id.startDateEt).setEnabled(false);
        view.findViewById(R.id.endDateEt).setEnabled(false);
        view.findViewById(R.id.addExpImgBtn).setEnabled(false);
    }

    private void showFilePickerOptions() {
        PopupMenu popup = new PopupMenu(requireContext(), rootView.findViewById(R.id.addExpImgBtn));
        popup.getMenu().add(0, 0, 0, "Camera");
        popup.getMenu().add(0, 1, 0, "Gallery");
        popup.getMenu().add(0, 2, 0, "PDF Document");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    if (checkCameraPermission()) {
                        pickFromCamera();
                    } else {
                        requestCameraPermission();
                    }
                    return true;
                case 1:
                    if (checkStoragePermission()) {
                        pickFromGallery();
                    } else {
                        requestStoragePermission();
                    }
                    return true;
                case 2:
                    if (checkStoragePermission()) {
                        pickPdfDocument();
                    } else {
                        requestStoragePermission();
                    }
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }


    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Experience Letter");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        fileUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        cameraLauncher.launch(cameraIntent);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private void pickPdfDocument() {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("application/pdf");
        pdfLauncher.launch(pdfIntent);
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().requestPermissions(cameraPermissions13, CAMERA_REQUEST_CODE);
        } else {
            requireActivity().requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
        }
    }

    private void handleImageResult(Uri imageUri) {
        if (imageUri != null) {
            isPDF = false;
            fileUri = imageUri;
            experienceImageIv.setVisibility(View.VISIBLE);
            fileNameTv.setVisibility(View.GONE);

            Glide.with(requireContext())
                    .load(imageUri)
                    .centerCrop()
                    .into(experienceImageIv);

            experienceCard.setFileUri(imageUri.toString());
            experienceCard.setPDF(false);
        }
    }

    private void handlePdfResult(Uri pdfUri) {
        if (pdfUri != null) {
            isPDF = true;
            fileUri = pdfUri;
            experienceImageIv.setVisibility(View.GONE);
            fileNameTv.setVisibility(View.VISIBLE);

            String fileName = getFileName(pdfUri);
            fileNameTv.setText(fileName);

            experienceCard.setFileUri(pdfUri.toString());
            experienceCard.setPDF(true);
        }
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().requestPermissions(storagePermissions13, STORAGE_REQUEST_CODE);
        } else {
            requireActivity().requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
        }
    }

    private void setupListeners(View view) {
        MaterialButton addButton = view.findViewById(R.id.addButton);
        MaterialButton deleteButton = view.findViewById(R.id.deleteButton);
        MaterialButton saveExperienceBtn = view.findViewById(R.id.saveExperienceBtn);

        addButton.setOnClickListener(v -> {
            saveCurrentCardData(); // Save before adding new card
            if (getActivity() instanceof PastExperienceDetailsActivity) {
                ((PastExperienceDetailsActivity) getActivity()).addNewExperienceCard();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (!experienceCard.isMandatory()) {
                showDeleteConfirmation();
            }
        });

        // Modified save button listener
        saveExperienceBtn.setOnClickListener(v -> {
            if (getActivity() instanceof PastExperienceDetailsActivity) {
                PastExperienceDetailsActivity activity = (PastExperienceDetailsActivity) getActivity();

                // First save current card data
                saveCurrentCardData();

                // Then get all cards (which will now include the updated current card)
                List<ExperienceCard> allCards = activity.getAllExperienceCards();

                // Add real-time validation logging
                for (ExperienceCard card : allCards) {
                    Log.d("CardData", "Company: " + card.getCompanyName());
                    Log.d("CardData", "Job: " + card.getJobTitle());
                    Log.d("CardData", "Start: " + card.getStartDate());
                    Log.d("CardData", "End: " + card.getEndDate());
                    Log.d("CardData", "File: " + card.getFileUri());
                }

                // Now validate and submit
                if (validateAllCards()) {
                    submitAllExperienceData();
                }
            }
        });

        setupDatePickers(view);
    }


    private void saveCurrentCardData() {
        TextInputEditText companyNameEt = requireView().findViewById(R.id.companyNameEt);
        TextInputEditText jobTitleEt = requireView().findViewById(R.id.jobTitleEt);
        TextInputEditText startDateEt = requireView().findViewById(R.id.startDateEt);
        TextInputEditText endDateEt = requireView().findViewById(R.id.endDateEt);

        String companyName = companyNameEt.getText() != null ? companyNameEt.getText().toString().trim() : "";
        String jobTitle = jobTitleEt.getText() != null ? jobTitleEt.getText().toString().trim() : "";
        String startDate = startDateEt.getText() != null ? startDateEt.getText().toString().trim() : "";
        String endDate = endDateEt.getText() != null ? endDateEt.getText().toString().trim() : "";

        experienceCard.setCompanyName(companyName);
        experienceCard.setJobTitle(jobTitle);
        experienceCard.setStartDate(startDate);
        experienceCard.setEndDate(endDate);

        if (fileUri != null) {
            experienceCard.setFileUri(fileUri.toString());
            experienceCard.setPDF(isPDF);
        }

        // Add debug logging
        Log.d("SaveCardData", "Saving card data:");
        Log.d("SaveCardData", "Company: " + companyName);
        Log.d("SaveCardData", "Job: " + jobTitle);
        Log.d("SaveCardData", "Start: " + startDate);
        Log.d("SaveCardData", "End: " + endDate);
        Log.d("SaveCardData", "File: " + (fileUri != null ? fileUri.toString() : "null"));
    }

    private boolean validateAllCards() {
        if (getActivity() instanceof PastExperienceDetailsActivity) {
            PastExperienceDetailsActivity activity = (PastExperienceDetailsActivity) getActivity();
            List<ExperienceCard> allCards = activity.getAllExperienceCards();

            for (ExperienceCard card : allCards) {
                if (!isCardComplete(card)) {
                    Toast.makeText(requireContext(), "Please complete all experience cards before submitting", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void submitAllExperienceData() {
        if (getActivity() instanceof PastExperienceDetailsActivity) {
            PastExperienceDetailsActivity activity = (PastExperienceDetailsActivity) getActivity();
            List<ExperienceCard> allCards = activity.getAllExperienceCards();

            // Debug logging
            for (int i = 0; i < allCards.size(); i++) {
                ExperienceCard card = allCards.get(i);
                Log.d("CardValidation", "Card " + i + ":");
                Log.d("CardValidation", "Company: " + card.getCompanyName());
                Log.d("CardValidation", "Job Title: " + card.getJobTitle());
                Log.d("CardValidation", "Start Date: " + card.getStartDate());
                Log.d("CardValidation", "End Date: " + card.getEndDate());
                Log.d("CardValidation", "File URI: " + card.getFileUri());
                Log.d("CardValidation", "Is Complete: " + isCardComplete(card));
            }


            


            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Submitting experience data...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Create multipart request parts
            List<MultipartBody.Part> files = new ArrayList<>();
            List<ExperienceSubmitRequest.ExperienceData> experiences = new ArrayList<>();

            try {
                for (int i = 0; i < allCards.size(); i++) {
                    ExperienceCard card = allCards.get(i);

                    // Convert URI to File and compress if it's an image
                    Uri fileUri = Uri.parse(card.getFileUri());
                    File file;

                    if (card.isPDF()) {
                        file = new File(getRealPathFromURI(fileUri));
                        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
                        files.add(MultipartBody.Part.createFormData("file" + i, file.getName(), requestFile));
                    } else {
                        // Compress image before upload
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), fileUri);
                        File cacheDir = requireContext().getCacheDir();
                        file = new File(cacheDir, "image_" + i + ".jpg");

                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();

                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                        files.add(MultipartBody.Part.createFormData("file" + i, file.getName(), requestFile));
                    }

                    // Add experience data
                    ExperienceSubmitRequest.ExperienceData expData = new ExperienceSubmitRequest.ExperienceData(
                         card
                    );
                    experiences.add(expData);
                }

                // Create request body
                String userId = ((PastExperienceDetailsActivity) getActivity()).getUserId();
                ExperienceSubmitRequest request = new ExperienceSubmitRequest(userId, experiences);
                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json"),
                        new Gson().toJson(request)
                );

                // Make API call
                ApiService apiService = RetrofitClient.getInstance().getApi();
                apiService.submitExperience(requestBody, files).enqueue(new Callback<ApiResponse3>() {
                    @Override
                    public void onResponse(Call<ApiResponse3> call, Response<ApiResponse3> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getMessage() != null) {
                                // Mark all cards as submitted
                                for (ExperienceCard card : allCards) {
                                    card.setSubmitted(true);
                                }
                                activity.refreshAllFragments();
                                Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                // Navigate to next screen or finish
                                activity.finish();
                            } else {
                                Toast.makeText(requireContext(), response.body().getError(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error submitting data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse3> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Error processing files: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Helper method to get real path from URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }



    private boolean isCardComplete(ExperienceCard card) {
        if (card == null) return false;

        boolean hasCompanyName = card.getCompanyName() != null && !card.getCompanyName().trim().isEmpty();
        boolean hasJobTitle = card.getJobTitle() != null && !card.getJobTitle().trim().isEmpty();
        boolean hasStartDate = card.getStartDate() != null && !card.getStartDate().trim().isEmpty();
        boolean hasEndDate = card.getEndDate() != null && !card.getEndDate().trim().isEmpty();
        boolean hasFile = card.getFileUri() != null && !card.getFileUri().trim().isEmpty();

        // Enhanced logging
        Log.d("CardValidation", "Validating card:");
        Log.d("CardValidation", "Company Name (" + card.getCompanyName() + "): " + hasCompanyName);
        Log.d("CardValidation", "Job Title (" + card.getJobTitle() + "): " + hasJobTitle);
        Log.d("CardValidation", "Start Date (" + card.getStartDate() + "): " + hasStartDate);
        Log.d("CardValidation", "End Date (" + card.getEndDate() + "): " + hasEndDate);
        Log.d("CardValidation", "File URI (" + card.getFileUri() + "): " + hasFile);

        boolean isComplete = hasCompanyName && hasJobTitle && hasStartDate && hasEndDate && hasFile;
        Log.d("CardValidation", "Is Complete: " + isComplete);

        return isComplete;
    }


    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Education Record")
                .setMessage("Are you sure you want to delete this education record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (getActivity() instanceof PastExperienceDetailsActivity) {
                        ((PastExperienceDetailsActivity) getActivity()).removeCard(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void updateButtonsVisibility(View view) {
        if (view == null) return;

        MaterialButton addButton = view.findViewById(R.id.addButton);
        MaterialButton deleteButton = view.findViewById(R.id.deleteButton);
        MaterialButton saveExperienceBtn = view.findViewById(R.id.saveExperienceBtn);

        // Show delete button only for non-mandatory cards
        deleteButton.setVisibility(!experienceCard.isMandatory() ? View.VISIBLE : View.GONE);

        // Show save button ONLY on the last card
        saveExperienceBtn.setVisibility(isLast ? View.VISIBLE : View.GONE);

        // Show add button ONLY on the last card
        addButton.setVisibility(isLast ? View.VISIBLE : View.GONE);
    }

    private void setupDatePickers(View view) {
        TextInputEditText startDateEt = view.findViewById(R.id.startDateEt);
        TextInputEditText endDateEt = view.findViewById(R.id.endDateEt);

        startDateEt.setOnClickListener(v -> {
            // Show date picker dialog
            showDatePickerDialog(startDateEt);
        });

        endDateEt.setOnClickListener(v -> {
            // Show date picker dialog
            showDatePickerDialog(endDateEt);
        });
    }

    private void showDatePickerDialog(TextInputEditText dateField) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
            dateField.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                dateSetListener,
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    // Add this method to update isLast flag
    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }
}