package com.vivo.vivorajonboarding;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.vivo.vivorajonboarding.adapter.DocumentAdapter;
import com.vivo.vivorajonboarding.api.ApiService;
import com.vivo.vivorajonboarding.api.RetrofitClient;
import com.vivo.vivorajonboarding.model.Document;
import com.vivo.vivorajonboarding.model.UploadResponse;
import com.vivo.vivorajonboarding.transformer.ProgressUpdateHandler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity {
    private RecyclerView documentList;
    private DocumentAdapter adapter;
    private List<Document> documents;
    private ActivityResultLauncher<String[]> documentPicker;
    private Document currentDocument;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton submitButton;
    private ApiService apiService;
    private BottomSheetDialog fileTypeDialog;
    private static final String TAG = "DocumentUploadActivity";
    private ProgressUpdateHandler progressHandler;
    private FrameLayout progressContainer;
    private TextView progressText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        initializeViews();
        initializeToolbar();
        setupRecyclerView();
        setupFileTypeDialog();
        setupApiService();
        setupProgressHandler();

        submitButton.setOnClickListener(v -> validateAndSubmitDocuments());
    }

    private void initializeViews() {
        documentList = findViewById(R.id.documentList);
        progressBar = findViewById(R.id.progressBar);
        submitButton = findViewById(R.id.submitButton);
        progressContainer = findViewById(R.id.progressContainer); // Add this ID to your FrameLayout
        progressText = findViewById(R.id.progressText);
    }

    private void setupProgressHandler() {
        progressHandler = new ProgressUpdateHandler(
                progressContainer,
                progressBar,
                progressText,
                documents
        );

        // Initialize the text position after layout is complete
        progressContainer.post(() -> progressHandler.initializeTextPosition());
    }

    private void initializeToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hides the app name
            Objects.requireNonNull(toolbar.getNavigationIcon()).setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFileTypeDialog() {
        fileTypeDialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_file_type_selector, null);

        MaterialCardView btnImage = dialogView.findViewById(R.id.imageUploadCard);
        MaterialCardView btnPdf = dialogView.findViewById(R.id.pdfUploadCard);

        btnImage.setOnClickListener(v -> {
            fileTypeDialog.dismiss();
            launchFilePicker("image/*");
        });

        btnPdf.setOnClickListener(v -> {
            fileTypeDialog.dismiss();
            launchFilePicker("application/pdf");
        });

        fileTypeDialog.setContentView(dialogView);
    }

    private void launchFilePicker(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Select File"));
        } catch (ActivityNotFoundException e) {
            showError("No app found to handle file selection");
        }
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    handleFileSelection(result.getData().getData());
                }
            }
    );

    private void handleFileSelection(Uri uri) {
        if (uri != null && currentDocument != null) {
            try {
                String mimeType = getContentResolver().getType(uri);
                Log.d(TAG, "Selected file mime type: " + mimeType);

                long fileSize = getFileSize(uri);
                Log.d(TAG, "Selected file size: " + fileSize);

                // Validate file size (e.g., 10MB limit)
                if (fileSize > 10 * 1024 * 1024) {
                    showError("File size must be less than 10MB");
                    return;
                }

                // Validate file type
                if (!isValidFileType(mimeType)) {
                    showError("Invalid file type. Please select PDF or Image files only");
                    Log.e(TAG, "Invalid mime type: " + mimeType);
                    return;
                }

                String fileName = getFileNameFromUri(uri);
                Log.d(TAG, "Original filename: " + fileName);

                Uri privateUri = copyFileToPrivateStorage(uri, fileName);
                Log.d(TAG, "Private storage URI: " + privateUri);

                if (privateUri != null) {
                    // Verify file exists and is readable
                    File file = new File(privateUri.getPath());
                    if (!file.exists() || !file.canRead()) {
                        Log.e(TAG, "File does not exist or is not readable: " + file.getAbsolutePath());
                        showError("Failed to save file");
                        return;
                    }

                    currentDocument.setFileUri(privateUri);
                    currentDocument.setFileName(fileName);
                    currentDocument.setMimeType(mimeType);
                    currentDocument.setFileSize(fileSize);
                    currentDocument.setUploaded(true);

                    Log.d(TAG, "Document updated successfully: " + currentDocument.getTitle() + ", Uploaded: " + currentDocument.isUploaded());

                    // Update the document in the list
                    int position = documents.indexOf(currentDocument);
                    if (position != -1) {
                        documents.set(position, currentDocument);
                        adapter.updateDocument(currentDocument);
                    }

                    updateProgress();

                    // Show success animation
                    showUploadSuccess(currentDocument);
                } else {
                    Log.e(TAG, "Failed to copy file to private storage");
                    showError("Failed to process file");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing file", e);
                showError("Error processing file: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Invalid URI or no current document selected");
        }
    }

    private void showUploadSuccess(Document document) {
        View itemView = documentList.findViewHolderForAdapterPosition(
                documents.indexOf(document)
        ).itemView;

        // Scale animation
        itemView.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction(() ->
                        itemView.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                );
    }

    private boolean isValidFileType(String mimeType) {
        return mimeType != null && (
                mimeType.startsWith("image/") ||
                        mimeType.equals("application/pdf")
        );
    }

    private long getFileSize(Uri uri) throws IOException {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    return cursor.getLong(sizeIndex);
                }
            }
        }
        // Fallback to stream length
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            return stream.available();
        }
    }

    private void setupRecyclerView() {
        documents = createDocumentList();
        adapter = new DocumentAdapter(documents, this::startDocumentPicker);

        documentList.setLayoutManager(new LinearLayoutManager(this));
        documentList.setAdapter(adapter);

        // Add animation
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        documentList.setLayoutAnimation(animation);
    }

    private List<Document> createDocumentList() {
        List<Document> docs = new ArrayList<>();
        docs.add(new Document("1", "Passport Size Photo", "passport_size_image", R.drawable.ic_passport_image));
        docs.add(new Document("2", "Employee Signature", "employee_sign_image", R.drawable.ic_signature));
        docs.add(new Document("3", "Aadhar Card Front", "aadhar_card_image", R.drawable.ic_aadhar_card_number));
        docs.add(new Document("4", "Aadhar Card Back", "aadhar_card_back_image", R.drawable.ic_aadhar_card_number));
        docs.add(new Document("5", "PAN Card", "pan_card_image", R.drawable.ic_pan_number));
        docs.add(new Document("6", "Last Company Experience Letter", "last_company_exp_letter_image", R.drawable.ic_experience_letter));
        docs.add(new Document("7", "Last Month Pay Slip", "pay_slip_exp_letter_image", R.drawable.ic_pay_slip));
        docs.add(new Document("8", "Second Last Month Pay Slip", "pay_slip_second_last_month_image", R.drawable.ic_pay_slip));
        docs.add(new Document("9", "Third Last Month Pay Slip", "pay_slip_third_last_month_image", R.drawable.ic_pay_slip));
        docs.add(new Document("10", "Resignation Mail", "resign_mail_image", R.drawable.ic_email_one));
        docs.add(new Document("11", "Bank Statement (Last 3 Months)", "bank_stmt_last_3_mth_image", R.drawable.ic_bank_account_number));
        docs.add(new Document("12", "Offer Letter", "offer_letter_image", R.drawable.ic_offer_letter));
        docs.add(new Document("13", "Appointment Letter", "appointment_letter_image", R.drawable.ic_appointment_letter));
        docs.add(new Document("14", "Bank Proof", "bank_proof_image", R.drawable.ic_bank));
        docs.add(new Document("15", "Other Documents", "other_documents_pdf", R.drawable.ic_other_docs));
        docs.add(new Document("16", "Resume", "resume_file", R.drawable.ic_resume));
        return docs;
    }

    private void initializeDocumentPicker() {
        documentPicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                this::handleDocumentResult
        );
    }

    private void startDocumentPicker(Document document) {
        currentDocument = document;
        fileTypeDialog.show();
    }

    private void handleDocumentResult(Uri uri) {
        if (uri != null && currentDocument != null) {
            // Copy file to app's private storage
            String fileName = getFileNameFromUri(uri);
            Uri privateUri = copyFileToPrivateStorage(uri, fileName);

            currentDocument.setFileUri(privateUri);
            currentDocument.setFileName(fileName);
            currentDocument.setMimeType(getContentResolver().getType(uri));

            adapter.notifyItemChanged(documents.indexOf(currentDocument));
            updateProgress();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        fileName = cursor.getString(index);
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private Uri copyFileToPrivateStorage(Uri sourceUri, String fileName) {
        File destinationFile = new File(getFilesDir(), fileName);
        Log.d(TAG, "Copying file to: " + destinationFile.getAbsolutePath());

        try {
            try (InputStream is = getContentResolver().openInputStream(sourceUri);
                 OutputStream os = new FileOutputStream(destinationFile)) {

                if (is == null) {
                    Log.e(TAG, "Failed to open input stream");
                    return null;
                }

                byte[] buffer = new byte[8192];
                int length;
                long totalBytes = 0;

                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                    totalBytes += length;
                }

                Log.d(TAG, "File copied successfully. Total bytes: " + totalBytes);
                return Uri.fromFile(destinationFile);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
            // Delete partially copied file if it exists
            if (destinationFile.exists()) {
                destinationFile.delete();
            }
            return null;
        }
    }



    private void uploadDocuments() {
        List<MultipartBody.Part> parts = new ArrayList<>();

        Log.d(TAG, "Starting document upload process");
        Log.d(TAG, "User ID: " + getCurrentUserId());

        for (Document doc : documents) {
            if (doc.getFileUri() != null) {
                try {
                    File file = new File(doc.getFileUri().getPath());
                    Log.d(TAG, String.format("Processing file: %s (Field: %s, Size: %d bytes, Type: %s)",
                            doc.getFileName(), doc.getFieldName(), file.length(), doc.getMimeType()));

                    if (!file.exists()) {
                        Log.e(TAG, "File does not exist: " + file.getAbsolutePath());
                        continue;
                    }

                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse(doc.getMimeType()), file);

                    MultipartBody.Part part = MultipartBody.Part.createFormData(
                            doc.getFieldName(), doc.getFileName(), requestFile);
                    parts.add(part);

                    Log.d(TAG, "Added file to upload request: " + doc.getFieldName());
                } catch (Exception e) {
                    Log.e(TAG, "Error processing file: " + doc.getFileName(), e);
                }
            }
        }

        Log.d(TAG, "Total files to upload: " + parts.size());

        RequestBody userId = RequestBody.create(
                MediaType.parse("text/plain"),
                getCurrentUserId()
        );

        // Log the full request
        Log.d(TAG, "Making upload request with user_id: " + getCurrentUserId());

        apiService.uploadDocuments(userId, parts)
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call,
                                           Response<UploadResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UploadResponse uploadResponse = response.body();
                            if (uploadResponse.isSuccess()) {
                                handleUploadSuccess();
                            } else {
                                handleUploadError(uploadResponse.getFirstError());
                            }
                        } else {
                            handleUploadError("Upload failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        handleUploadError("Network error: " + t.getMessage());
                    }
                });
    }

    private void handleUploadError(String error) {
        // Fade out loading overlay
        View loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    // Show error dialog
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Upload Failed")
                            .setMessage(error)
                            .setPositiveButton("Retry", (dialog, which) -> {
                                // Reset UI state
                                resetSubmissionState();
                                // Retry upload
                                startSubmissionProcess();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                resetSubmissionState();
                            })
                            .show();
                });
    }

    private void resetSubmissionState() {
        submitButton.setEnabled(true);
        adapter.setEnabled(true);
        documentList.setEnabled(true);

        // Reset all card positions and alpha
        for (int i = 0; i < documentList.getChildCount(); i++) {
            View card = documentList.getChildAt(i);
            if (card != null) {
                card.setTranslationX(0f);
                card.setAlpha(1f);
                card.setScaleX(1f);
                card.setScaleY(1f);
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean areAllDocumentsUploaded() {
        for (Document doc : documents) {
            if (!doc.isUploaded()) {
                return false;
            }
        }
        return true;
    }

    private void updateProgress() {
        progressHandler.updateProgress();

        // Update submit button state if needed
        boolean allUploaded = areAllDocumentsUploaded();
        submitButton.setEnabled(allUploaded);
    }


    private String getCurrentUserId() {
        SessionManager sessionManager = new SessionManager(this);
        return "test4275";
    }

    private void setupApiService() {
        apiService = RetrofitClient.getInstance().getApi();
    }


    private void validateAndSubmitDocuments() {
        if (!hasInternetConnection()) {
            showError("No internet connection");
            return;
        }

        if (!areAllDocumentsUploaded()) {
            showMissingDocumentsError();
            return;
        }

        // Show confirmation dialog with custom layout
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_submit_confirmation, null);
        builder.setView(dialogView)
               .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        MaterialButton submitBtn = dialogView.findViewById(R.id.submitButton);
        MaterialButton cancelBtn = dialogView.findViewById(R.id.cancelButton);

        submitBtn.setOnClickListener(v -> {
            dialog.dismiss();
            startSubmissionProcess();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    private void startSubmissionProcess() {
        // 1. Disable UI interactions
        submitButton.setEnabled(false);
        adapter.setEnabled(false);
        documentList.setEnabled(false);

        // 2. Show loading overlay with fade in
        View loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingOverlay.setAlpha(0f);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.animate()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new FastOutSlowInInterpolator());

        // 3. Animate progress bar to completion
        progressHandler.animateToCompletion(1500, () -> {
            // After progress completion, start document animations
            animateDocumentCards(() -> {
                uploadDocuments();
            });
        });
    }

    private void animateDocumentCards(Runnable onComplete) {
        int childCount = documentList.getChildCount();
        int[] completedAnimations = {0};

        for (int i = 0; i < childCount; i++) {
            View card = documentList.getChildAt(i);
            if (card == null) continue;

            int delay = i * 100; // Stagger the animations

            card.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(150)
                    .setStartDelay(delay)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        // Scale back to normal and slide right
                        card.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .translationX(card.getWidth())
                                .alpha(0f)
                                .setDuration(300)
                                .setInterpolator(new AccelerateInterpolator())
                                .withEndAction(() -> {
                                    completedAnimations[0]++;
                                    if (completedAnimations[0] == childCount && onComplete != null) {
                                        onComplete.run();
                                    }
                                });
                    });
        }

        // If no cards to animate, call completion immediately
        if (childCount == 0 && onComplete != null) {
            onComplete.run();
        }
    }

    private void showMissingDocumentsError() {
        StringBuilder message = new StringBuilder("Missing documents:\n");
        for (Document doc : documents) {
            if (!doc.isUploaded()) {
                message.append("• ").append(doc.getTitle()).append("\n");
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Required Documents")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (hasUnsavedChanges()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Discard Changes")
                    .setMessage("You have unsaved changes. Are you sure you want to leave?")
                    .setPositiveButton("Leave", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Stay", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void handleUploadSuccess() {
        View loadingOverlay = findViewById(R.id.loadingOverlay);
        View successOverlay = findViewById(R.id.successOverlay);

        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    // 2. Show success overlay with animation
                    successOverlay.setAlpha(0f);
                    successOverlay.setVisibility(View.VISIBLE);

                    // Animate success icon
                    View successIcon = successOverlay.findViewById(R.id.successIcon);
                    TextView successText = successOverlay.findViewById(R.id.successText);

                    successIcon.setScaleX(0.5f);
                    successIcon.setScaleY(0.5f);
                    successText.setAlpha(0f);

                    successOverlay.animate()
                            .alpha(1f)
                            .setDuration(300);

                    successIcon.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(500);

                    successText.animate()
                            .alpha(1f)
                            .setStartDelay(200)
                            .setDuration(300);

                    // 3. Auto-dismiss after delay
                    new Handler().postDelayed(() -> {
                        // Navigate away or dismiss
                        finish();
                    }, 2000);
                });
    }

    private boolean hasUnsavedChanges() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return documents.stream().anyMatch(doc -> !doc.isUploaded()); // Assuming isUploaded() returns true if saved
        }
        // Handle older Android versions without streams
        for (Document doc : documents) {
            if (!doc.isUploaded()) {
                return true;
            }
        }
        return false;
    }

}