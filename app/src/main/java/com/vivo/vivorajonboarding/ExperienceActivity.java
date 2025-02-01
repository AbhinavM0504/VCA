package com.vivo.vivorajonboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.foysaldev.cropper.CropImage;
import com.foysaldev.cropper.CropImageView;
import com.vivo.vivorajonboarding.adapter.ExperienceAdapter;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.ExperienceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ExperienceActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private JSONObject jsonObject;
    private Dialog dialog;
    private static final int IMAGE_COMPRESSION_QUALITY = 40;

    // Initialize all view variables
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn;
    private RecyclerView experienceRv;
    private CardView noDataCardView;
    private ExtendedFloatingActionButton addExperienceImgBtn;
    private TextInputEditText companyNameEt, jobTitleEt, startDateEt, endDateEt;
    private ImageButton addExpImgBtn;
    private ImageView experienceImageIv;
    private Button saveExperienceBtn;
    private TextView warningTv;
    //ArrayList
    String[] cameraPermissions;
    String[] cameraPermissions13;
    String[] storagePermissions;
    String[] storagePermissions13;
    ArrayList<ExperienceModel> experienceList = new ArrayList<>();

    //Adapters
    private ExperienceAdapter experienceAdapter;

    //IMAGE CONSTANTS
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    private Uri imageUri = null, experienceImageUri = null;
    RequestQueue rQueue;

    //Loading dialog
    private LoadingDialog loadingDialog;

    //Required Variables
    DatePickerDialog datePickerDialog;
    String userid = "", request = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);

        initialization();

        if (getIntent() != null && getIntent().getExtras() != null) {
            getIntentData();
        }

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //get All Experience
        getAllExperience();

        //handle add experience image button
        addExperienceImgBtn.setOnClickListener(view -> openAddExperienceForm());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            //getAllExperience
            getAllExperience();
            swipeRefreshLayout.setRefreshing(false);
        });
    }


    private void openAddExperienceForm() {
        addExperienceImgBtn.setEnabled(false);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_experience_form);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

// Option 1: Set width to match parent with margins
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.horizontalMargin = 16;
        // Add horizontal margin in dp if needed
        window.setAttributes(params);
        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);
        companyNameEt = dialog.findViewById(R.id.companyNameEt);
        jobTitleEt = dialog.findViewById(R.id.jobTitleEt);
        startDateEt = dialog.findViewById(R.id.startDateEt);
        endDateEt = dialog.findViewById(R.id.endDateEt);
        addExpImgBtn = dialog.findViewById(R.id.addExpImgBtn);
        experienceImageIv = dialog.findViewById(R.id.experienceImageIv);
        saveExperienceBtn = dialog.findViewById(R.id.saveExperienceBtn);
        warningTv = dialog.findViewById(R.id.warningTv);

        //setup start date
        setupDate(startDateEt);

        //setup end date
        setupDate(endDateEt);

        //handle addExpImgBtn Button
        addExpImgBtn.setOnClickListener(v -> showImagePopupMenu(addExpImgBtn));

        //check if request is blank or not
        if (!request.isEmpty()) {
            warningTv.setVisibility(View.VISIBLE);
            //hide saveExperienceBtn
            saveExperienceBtn.setAlpha(0.6f);
            saveExperienceBtn.setEnabled(false);
        } else {
            warningTv.setVisibility(View.GONE);
            //show saveExperienceBtn
            saveExperienceBtn.setAlpha(1f);
            saveExperienceBtn.setEnabled(true);
        }

        //handle saveExperienceBtn click
        saveExperienceBtn.setOnClickListener(v -> validateData());

        //handle close btn
        closeBtn.setOnClickListener(v -> {
            addExperienceImgBtn.setEnabled(true);
            dialog.dismiss();
        });

        dialog.setOnCancelListener(dialog -> addExperienceImgBtn.setEnabled(true));

        //set properties of dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void setupDate(TextInputEditText dateViewEt) {
        final Calendar fDobCal = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener fDobSetListener = (view, year, month, dayOfMonth) -> {
            fDobCal.set(Calendar.YEAR, year);
            fDobCal.set(Calendar.MONTH, month);
            fDobCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat(" dd/MM/yyyy", Locale.US);
            dateViewEt.setText(sdf.format(fDobCal.getTime()));
            dateViewEt.setError(null);
            dateViewEt.clearFocus();
        };
        dateViewEt.setOnClickListener(v -> {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                datePickerDialog = new DatePickerDialog(ExperienceActivity.this,
                        R.style.MyDatePickerStyle,
                        fDobSetListener,
                        fDobCal.get(Calendar.YEAR), fDobCal.get(Calendar.MONTH),
                        fDobCal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.toString());
            }
        });
    }

    private void validateData() {
        String companyName = Objects.requireNonNull(companyNameEt.getText()).toString().trim();
        String jobTitle = Objects.requireNonNull(jobTitleEt.getText()).toString().trim();
        String startDate = Objects.requireNonNull(startDateEt.getText()).toString().trim();
        String endDate = Objects.requireNonNull(endDateEt.getText()).toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            companyNameEt.setError("Required");
            companyNameEt.requestFocus();
        } else if (TextUtils.isEmpty(jobTitle)) {
            jobTitleEt.setError("Required");
            jobTitleEt.requestFocus();
        } else if (TextUtils.isEmpty(startDate)) {
            startDateEt.setError("Required");
            startDateEt.requestFocus();
        } else if (TextUtils.isEmpty(endDate)) {
            endDateEt.setError("Required");
            endDateEt.requestFocus();
        } else if (experienceImageUri == null) {
            showToast("Experience Image Required");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ExperienceActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure to save this experience?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();

                //save experience data to database
                loadingDialog.showDialog("Uploading...");
                new SaveExperienceData(companyName, jobTitle, startDate, endDate, experienceImageUri).execute();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        }
    }

    private class SaveExperienceData extends AsyncTask<String, Void, Boolean> {

        String companyName, jobTitle, startDate, endDate;
        Uri experienceImageUri;
        Bitmap bitmap = null;

        public SaveExperienceData(String companyName, String jobTitle, String startDate, String endDate, Uri experienceImageUri) {
            this.companyName = companyName;
            this.jobTitle = jobTitle;
            this.startDate = startDate;
            this.endDate = endDate;
            this.experienceImageUri = experienceImageUri;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), experienceImageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("userid", userid);
                    jsonObject.put("value", "experience");
                    jsonObject.put("name", "experience_" + System.currentTimeMillis() + ".jpg");
                    jsonObject.put("image", encodedImage);
                    jsonObject.put("companyName", companyName);
                    jsonObject.put("jobTitle", jobTitle);
                    jsonObject.put("startDate", startDate);
                    jsonObject.put("endDate", endDate);
                } catch (JSONException e) {
                    Log.e("JSON EXCEPTION", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.SAVE_EXPERIENCE_DATA_URL, jsonObject,
                        jsonObject -> {
                            Log.d("IMAGE RESPONSE", jsonObject.toString());
                            rQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                String message = jsonObject.getString("message");
                                if (message.equalsIgnoreCase("Yes")) {

                                    showToast(message);//show message
                                    resetAllFields();//resetAllFields
                                    getAllExperience();//getAllExperience

                                } else {
                                    showToast(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast(e.toString());
                            }
                        }, volleyError -> {
                    loadingDialog.hideDialog();
                    Log.e("IMAGE ERROR", volleyError.toString());
                    showToast(volleyError.toString());
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rQueue = Volley.newRequestQueue(ExperienceActivity.this);
                rQueue.add(jsonObjectRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void resetAllFields() {
        //reset all fields
        companyNameEt.setText("");
        jobTitleEt.setText("");
        startDateEt.setText("");
        endDateEt.setText("");
        experienceImageUri = null;
        Picasso.get().load(R.drawable.ic_raw_image).into(experienceImageIv);
    }

    //onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //picked from gallery
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAutoZoomEnabled(true)
                        .start(this);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAutoZoomEnabled(true)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult crop_result = CropImage.getActivityResult(data);
                Uri resultUri = crop_result.getUri();
                imageUri = resultUri;

                experienceImageUri = resultUri;
                Picasso.get().load(experienceImageUri).fit().into(experienceImageIv);
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//only image
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image description");
        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, cameraPermissions13, CAMERA_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
        }
    }

    private boolean checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        }
    }


    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, storagePermissions13, STORAGE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    try {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                        if (cameraAccepted && storageAccepted) {
                            pickFromCamera();
                        } else {
                            showToast("Camera & Storage permissions are required");
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        showToast("Storage permissions is required");
                    }
                }
            }
            break;
        }
    }

    private void showImagePopupMenu(ImageButton button) {
        PopupMenu imageMenu = new PopupMenu(this, button);
        imageMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        imageMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        imageMenu.show();
        //handle menu item clicks
        imageMenu.setOnMenuItemClickListener(imageMenuItem -> {
            int i = imageMenuItem.getItemId();
            if (i == 0) {
                //camera clicked
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (i == 1) {
                //gallery clicked
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
            return false;
        });
    }

    private void getAllExperience() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_EXPERIENCE_DATA_URL, response -> {
            Log.d("EXPERIENCE RESPONSE", response);
            loadingDialog.hideDialog();
            try {
                experienceList.clear();
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");

                if (status.equalsIgnoreCase("true")) {
                    JSONArray jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ExperienceModel experienceModel = new ExperienceModel();
                        experienceModel.setId(object.getString("id"));
                        experienceModel.setUserid(object.getString("userid"));
                        experienceModel.setCompany_name(object.getString("company_name"));
                        experienceModel.setCompany_job_title(object.getString("company_job_title"));
                        experienceModel.setCompany_start_date(object.getString("company_start_date"));
                        experienceModel.setCompany_end_date(object.getString("company_end_date"));
                        experienceModel.setExperience_letter(object.getString("experience_letter"));
                        experienceModel.setTime(object.getString("time"));
                        experienceModel.setRequest(object.getString("request"));
                        experienceModel.setHo_request(object.getString("ho_request"));
                        experienceList.add(experienceModel);

                        request = object.getString("request");
                    }
                    if (experienceList.size() > 0) {
                        noDataCardView.setVisibility(View.GONE);
                        experienceRv.setVisibility(View.VISIBLE);
                        experienceAdapter = new ExperienceAdapter(ExperienceActivity.this, experienceList);
                        experienceRv.setAdapter(experienceAdapter);
                        experienceAdapter.notifyDataSetChanged();
                    } else {
                        noDataCardView.setVisibility(View.VISIBLE);
                        experienceRv.setVisibility(View.GONE);
                    }
                } else {
                    showToast(message);
                    noDataCardView.setVisibility(View.VISIBLE);
                    experienceRv.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("EXPERIENCE ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) {
                Log.e("RETRY ERROR", error.toString());
                showToast(error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userid = intent.getStringExtra("userid");
        }
    }

    private void showToast(String message) {
        Toast.makeText(ExperienceActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        backBtn = findViewById(R.id.backBtn);
        experienceRv = findViewById(R.id.experienceRv);
        noDataCardView = findViewById(R.id.noDataCardView);
        addExperienceImgBtn = findViewById(R.id.addExperienceFab);

        loadingDialog = new LoadingDialog(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermissions13 = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

    }
}