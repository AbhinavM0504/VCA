package com.vivo.vivorajonboarding.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.PastExperienceDetailsActivity;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.ExperienceCard;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

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
        fileNameTv = new TextView(requireContext()); // Add this TextView to your layout
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
            if (getActivity() instanceof PastExperienceDetailsActivity) {
                ((PastExperienceDetailsActivity) getActivity()).addNewExperienceCard();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (getActivity() instanceof PastExperienceDetailsActivity) {
                ((PastExperienceDetailsActivity) getActivity()).removeCard(position);
            }
        });

        saveExperienceBtn.setOnClickListener(v -> {
            // Implement save functionality
            saveExperienceData();
        });

        // Date picker listeners
        setupDatePickers(view);
    }

    private void updateButtonsVisibility(View view) {
        MaterialButton deleteButton = view.findViewById(R.id.deleteButton);
        MaterialButton saveExperienceBtn = view.findViewById(R.id.saveExperienceBtn);

        // Show delete button only for non-mandatory cards (after first card)
        deleteButton.setVisibility(experienceCard.isMandatory() ? View.GONE : View.VISIBLE);

        // Show save button only on the last card
        saveExperienceBtn.setVisibility(isLast ? View.VISIBLE : View.GONE);
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

    private void saveExperienceData() {
        TextInputEditText companyNameEt = requireView().findViewById(R.id.companyNameEt);
        TextInputEditText jobTitleEt = requireView().findViewById(R.id.jobTitleEt);
        TextInputEditText startDateEt = requireView().findViewById(R.id.startDateEt);
        TextInputEditText endDateEt = requireView().findViewById(R.id.endDateEt);

        // Validate fields
        if (companyNameEt.getText().toString().isEmpty() ||
                jobTitleEt.getText().toString().isEmpty() ||
                startDateEt.getText().toString().isEmpty() ||
                endDateEt.getText().toString().isEmpty() ||
                fileUri == null) {

            Toast.makeText(requireContext(), "Please fill all fields and upload experience letter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update experience card data
        experienceCard.setCompanyName(companyNameEt.getText().toString());
        experienceCard.setJobTitle(jobTitleEt.getText().toString());
        experienceCard.setStartDate(startDateEt.getText().toString());
        experienceCard.setEndDate(endDateEt.getText().toString());
        experienceCard.setFileUri(fileUri.toString());
        experienceCard.setPDF(isPDF);

        // Notify activity of save

    }
}