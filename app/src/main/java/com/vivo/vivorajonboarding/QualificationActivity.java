package com.vivo.vivorajonboarding;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.foysaldev.cropper.CropImage;
import com.foysaldev.cropper.CropImageView;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.QualificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class QualificationActivity extends AppCompatActivity {


    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn;

    //10 views
    private LinearLayout layout10;
    private TextInputEditText school10Et, percentage10Et, board10Et, yearOfPassing10Et;
    private ImageView image10Iv;
    private ProgressBar image10Pg;
    private Button addImage10Btn, save10DataBtn, addMore12Btn;

    //12 views
    private LinearLayout layout12;
    private TextInputEditText school12Et, percentage12Et, board12Et, yearOfPassing12Et;
    private ImageView image12Iv;
    private ProgressBar image12Pg;
    private Button addImage12Btn, save12DataBtn, addMoreGBtn;

    //Graduation views
    private LinearLayout layoutG;
    private TextInputEditText courseGEt, collegeGEt, percentageGEt, universityGEt, yearOfPassingGEt;
    private ImageView imageGIv;
    private ProgressBar imageGPg;
    private Button addImageGBtn, saveGDataBtn, addMorePGBtn;

    //Post Graduation views
    private LinearLayout layoutPG;
    private TextInputEditText coursePGEt, collegePGEt, percentagePGEt, universityPGEt, yearOfPassingPGEt;
    private ImageView imagePGIv;
    private ProgressBar imagePGPg;
    private Button addImagePGBtn, savePGDataBtn, addMoreOtherBtn;

    //Other Qualification views
    private LinearLayout layoutOther;
    private TextInputEditText courseOtherEt, collegeOtherEt, percentageOtherEt, universityOtherEt, yearOfPassingOtherEt;
    private ImageView imageOtherIv;
    private ProgressBar imageOtherPg;
    private Button addImageOtherBtn, saveOtherDataBtn;

    //warning textview
    private TextView warningTv;

    //permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private String[] cameraPermissions13;
    private String[] storagePermissions13;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    private Uri imageUri = null, image10Uri = null, image12Uri = null, imageGUri = null, imagePGUri = null, imageOtherUri = null;
//    String imageName = System.currentTimeMillis() + ".jpg";
//    String image10Name = "image_10_" + imageName, image12Name = "image_12_" + imageName, imageGName = "image_g_" + imageName, imagePGName = "image_pg_" + imageName,
//            imageOtherName = "image_other_" + imageName;

    String image10Name = "image_10_", image12Name = "image_12_", imageGName = "image_g_", imagePGName = "image_pg_",
            imageOtherName = "image_other_";

    JSONObject jsonObject;
    RequestQueue rQueue;

    String value = "";
    boolean view12 = false, viewG = false, viewPG = false, viewOther = false;


    //Loading Bar
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualification);

        initialization();

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //get All Qualification
        getAllQualification();

        //handle add 10 image
        addImage10Btn.setOnClickListener(view -> {
            value = "image_10";
            PopupMenu popupMenu = new PopupMenu(this, addImage10Btn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
                return false;
            });
        });
        save10DataBtn.setOnClickListener(view -> {
            String school10 = school10Et.getText().toString().trim();
            String percentage10 = percentage10Et.getText().toString().trim();
            String board10 = board10Et.getText().toString().trim();
            String yearOfPassing10 = yearOfPassing10Et.getText().toString().trim();

            if (TextUtils.isEmpty(school10)) {
                showToast("10th school required");
            } else if (TextUtils.isEmpty(percentage10)) {
                showToast("10th percentage required");
            } else if (TextUtils.isEmpty(board10)) {
                showToast("10th board required");
            } else if (TextUtils.isEmpty(yearOfPassing10)) {
                showToast("10th year of passing required");
            } else if (image10Uri == null) {
                showToast("10th Marksheet Required");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualificationActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("image_10", image10Name, image10Uri, school10, percentage10, board10, yearOfPassing10, "").execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });
        addMore12Btn.setOnClickListener(view -> {
            if (view12) {
                layout12.setVisibility(View.VISIBLE);
            } else {
                showToast("Please fill 10th details first...");
            }
        });

        //handle add 12 image
        addImage12Btn.setOnClickListener(view -> {
            value = "image_12";
            PopupMenu popupMenu = new PopupMenu(this, addImage12Btn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
                return false;
            });
        });
        save12DataBtn.setOnClickListener(view -> {
            String school12 = school12Et.getText().toString().trim();
            String percentage12 = percentage12Et.getText().toString().trim();
            String board12 = board12Et.getText().toString().trim();
            String yearOfPassing12 = yearOfPassing12Et.getText().toString().trim();

            if (TextUtils.isEmpty(school12)) {
                showToast("12th school required");
            } else if (TextUtils.isEmpty(percentage12)) {
                showToast("12th percentage required");
            } else if (TextUtils.isEmpty(board12)) {
                showToast("12th board required");
            } else if (TextUtils.isEmpty(yearOfPassing12)) {
                showToast("12th year of passing required");
            } else if (image12Uri == null) {
                showToast("12th Marksheet Required");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualificationActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("image_12", image12Name, image12Uri, school12, percentage12, board12, yearOfPassing12, "").execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });
        addMoreGBtn.setOnClickListener(view -> {
            if (viewG) {
                layoutG.setVisibility(View.VISIBLE);
            } else {
                showToast("Please fill 12th details first...");
            }
        });

        //handle add graduation image
        addImageGBtn.setOnClickListener(view -> {
            value = "image_g";
            PopupMenu popupMenu = new PopupMenu(this, addImageGBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
                return false;
            });
        });
        saveGDataBtn.setOnClickListener(view -> {
            String courseG = courseGEt.getText().toString().trim();
            String collegeG = collegeGEt.getText().toString().trim();
            String percentageG = percentageGEt.getText().toString().trim();
            String universityG = universityGEt.getText().toString().trim();
            String yearOfPassingG = yearOfPassingGEt.getText().toString().trim();

            if (TextUtils.isEmpty(courseG)) {
                showToast("Graduation Course required");
            } else if (TextUtils.isEmpty(collegeG)) {
                showToast("Graduation college required");
            } else if (TextUtils.isEmpty(percentageG)) {
                showToast("Graduation percentage required");
            } else if (TextUtils.isEmpty(yearOfPassingG)) {
                showToast("Graduation year of passing required");
            } else if (TextUtils.isEmpty(universityG)) {
                showToast("Graduation university required");
            } else if (imageGUri == null) {
                showToast("Graduation Marksheet Required");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualificationActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("image_g", imageGName, imageGUri, collegeG, percentageG, universityG, yearOfPassingG, courseG).execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }

        });
        addMorePGBtn.setOnClickListener(view -> {
            if (viewPG) {
                layoutPG.setVisibility(View.VISIBLE);
            } else {
                showToast("Please fill graduation details first...");
            }
        });

        //handle add post graduation image
        addImagePGBtn.setOnClickListener(view -> {
            value = "image_pg";
            PopupMenu popupMenu = new PopupMenu(this, addImagePGBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
                return false;
            });
        });
        savePGDataBtn.setOnClickListener(view -> {
            String coursePG = coursePGEt.getText().toString().trim();
            String collegePG = collegePGEt.getText().toString().trim();
            String percentagePG = percentagePGEt.getText().toString().trim();
            String universityPG = universityPGEt.getText().toString().trim();
            String yearOfPassingPG = yearOfPassingPGEt.getText().toString().trim();

            if (TextUtils.isEmpty(coursePG)) {
                showToast("Post Graduation course required");
            } else if (TextUtils.isEmpty(collegePG)) {
                showToast("Post Graduation college required");
            } else if (TextUtils.isEmpty(percentagePG)) {
                showToast("Post Graduation percentage required");
            } else if (TextUtils.isEmpty(yearOfPassingPG)) {
                showToast("Post Graduation year of passing required");
            } else if (TextUtils.isEmpty(universityPG)) {
                showToast("Post Graduation university required");
            } else if (imagePGUri == null) {
                showToast("Post Graduation Marksheet Required");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualificationActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("image_pg", imagePGName, imagePGUri, collegePG, percentagePG, universityPG, yearOfPassingPG, coursePG).execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });
        addMoreOtherBtn.setOnClickListener(view -> {
            if (viewOther) {
                layoutOther.setVisibility(View.VISIBLE);
            } else {
                showToast("Please fill PG details first...");
            }
        });

        //handle add other qualification image
        addImageOtherBtn.setOnClickListener(view -> {
            value = "image_other";
            PopupMenu popupMenu = new PopupMenu(this, addImageOtherBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    //gallery clicked
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
                return false;
            });
        });
        saveOtherDataBtn.setOnClickListener(view -> {
            String courseOther = courseOtherEt.getText().toString().trim();
            String collegeOther = collegeOtherEt.getText().toString().trim();
            String percentageOther = percentageOtherEt.getText().toString().trim();
            String universityOther = universityOtherEt.getText().toString().trim();
            String yearOfPassingOther = yearOfPassingOtherEt.getText().toString().trim();

            if (TextUtils.isEmpty(courseOther)) {
                showToast("Other course required");
            } else if (TextUtils.isEmpty(collegeOther)) {
                showToast("Other college required");
            } else if (TextUtils.isEmpty(percentageOther)) {
                showToast("Other percentage required");
            } else if (TextUtils.isEmpty(yearOfPassingOther)) {
                showToast("Other year of passing required");
            } else if (TextUtils.isEmpty(universityOther)) {
                showToast("Other university required");
            } else if (imageOtherUri == null) {
                showToast("Other Marksheet Required");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(QualificationActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("image_other", imageOtherName, imageOtherUri, collegeOther, percentageOther, universityOther, yearOfPassingOther, courseOther).execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            //load all qualification
            getAllQualification();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    /*private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of camera intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("ActivityResult: Camera", String.valueOf(imageUri));
                        Intent data = result.getData();//no need here as in camera case we already have image in image uri variable
                        //profileImage.setImageURI(imageUri);
                        if (imageUri != null) {
                            if (value.equalsIgnoreCase("image_10")) {
                                image10Uri = imageUri;
                                image10Iv.setVisibility(View.VISIBLE);
                                Picasso.get().load(image10Uri).fit().into(image10Iv);
                                addImage10Btn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_12")) {
                                image12Uri = imageUri;
                                image12Iv.setVisibility(View.VISIBLE);
                                Picasso.get().load(image12Uri).fit().into(image12Iv);
                                addImage12Btn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_g")) {
                                imageGUri = imageUri;
                                imageGIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imageGUri).fit().into(imageGIv);
                                addImageGBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_pg")) {
                                imagePGUri = imageUri;
                                imagePGIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imagePGUri).fit().into(imagePGIv);
                                addImagePGBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_other")) {
                                imageOtherUri = imageUri;
                                imageOtherIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imageOtherUri).fit().into(imageOtherIv);
                                addImageOtherBtn.setText("Update");
                            }
                        }
                    } else {
                        showToast("Cancelled");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();//no need here as in camera case we already have image in image uri variable
                        assert data != null;
                        imageUri = data.getData();
                        Log.d("ActivityResult: Gallery", String.valueOf(imageUri));
                        if (imageUri != null) {
                            if (value.equalsIgnoreCase("image_10")) {
                                image10Uri = imageUri;
                                image10Iv.setVisibility(View.VISIBLE);
                                Picasso.get().load(image10Uri).fit().into(image10Iv);
                                addImage10Btn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_12")) {
                                image12Uri = imageUri;
                                image12Iv.setVisibility(View.VISIBLE);
                                Picasso.get().load(image12Uri).fit().into(image12Iv);
                                addImage12Btn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_g")) {
                                imageGUri = imageUri;
                                imageGIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imageGUri).fit().into(imageGIv);
                                addImageGBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_pg")) {
                                imagePGUri = imageUri;
                                imagePGIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imagePGUri).fit().into(imagePGIv);
                                addImagePGBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("image_other")) {
                                imageOtherUri = imageUri;
                                imageOtherIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(imageOtherUri).fit().into(imageOtherIv);
                                addImageOtherBtn.setText("Update");
                            }
                        }
                    } else {
                        showToast("Cancelled");
                    }
                }
            }
    );*/

    private class UploadImage extends AsyncTask<String, Void, Boolean> {

        String value, imageName, school, percentage, board, yearOfPassing, course;
        Uri imageUri;
        Bitmap bitmap = null;

        public UploadImage(String value, String imageName, Uri imageUri, String school, String percentage, String board, String yearOfPassing, String course) {
            this.value = value;
            this.imageName = imageName;
            this.imageUri = imageUri;
            this.school = school;
            this.percentage = percentage;
            this.board = board;
            this.yearOfPassing = yearOfPassing;
            this.course = course;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("userid", Prevalent.currentOnlineUser.getUserid());
                    jsonObject.put("value", value);
                    jsonObject.put("name", imageName + System.currentTimeMillis() + ".jpg");
                    jsonObject.put("school", school);
                    jsonObject.put("percentage", percentage);
                    jsonObject.put("board", board);
                    jsonObject.put("yearOfPassing", yearOfPassing);
                    jsonObject.put("course", course);
                    jsonObject.put("image", encodedImage);
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.SAVE_QUALIFICATION_DATA_URL, jsonObject,
                        jsonObject -> {
                            Log.d("IMAGE RESPONSE", jsonObject.toString());
                            rQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                if (jsonObject.getString("message").equalsIgnoreCase("Yes")) {
                                    Log.d("IMAGE JSON RESPONSE", jsonObject.getString("message"));
                                    showToast(jsonObject.getString("message").trim());

                                    //again load all qualification
                                    getAllQualification();

                                } else {
                                    showToast(jsonObject.getString("message").trim());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, volleyError -> Log.e("Error Image Response", volleyError.toString()));
                jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
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

                    }
                });
                rQueue = Volley.newRequestQueue(QualificationActivity.this);
                rQueue.add(jsonObjectRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    ///onActivityResult
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
                File f = new File(String.valueOf(imageUri));
                //imageName = f.getName();

                if (value.equalsIgnoreCase("image_10")) {
                    image10Uri = imageUri;
                    image10Iv.setVisibility(View.VISIBLE);
                    Picasso.get().load(image10Uri).fit().into(image10Iv);
                    addImage10Btn.setText("Update");
                }
                if (value.equalsIgnoreCase("image_12")) {
                    image12Uri = imageUri;
                    image12Iv.setVisibility(View.VISIBLE);
                    Picasso.get().load(image12Uri).fit().into(image12Iv);
                    addImage12Btn.setText("Update");
                }
                if (value.equalsIgnoreCase("image_g")) {
                    imageGUri = imageUri;
                    imageGIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(imageGUri).fit().into(imageGIv);
                    addImageGBtn.setText("Update");
                }
                if (value.equalsIgnoreCase("image_pg")) {
                    imagePGUri = imageUri;
                    imagePGIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(imagePGUri).fit().into(imagePGIv);
                    addImagePGBtn.setText("Update");
                }
                if (value.equalsIgnoreCase("image_other")) {
                    imageOtherUri = imageUri;
                    imageOtherIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(imageOtherUri).fit().into(imageOtherIv);
                    addImageOtherBtn.setText("Update");
                }

                copyFileOrDirectory("" + imageUri.getPath(), "" + getDir("VivoImages", MODE_PRIVATE));
            }
        }

    }

    private void copyFileOrDirectory(String srcDir, String desDir) {
        try {
            File src = new File(srcDir);
            File des = new File(desDir, src.getName());
            if (src.isDirectory()) {
                String[] files = src.list();
                int filesLength = files.length;
                for (String file : files) {
                    String src1 = new File(src, file).getPath();
                    String dst1 = des.getPath();

                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, des);
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(File srcDir, File desDir) throws IOException {
        if (!desDir.getParentFile().exists()) {
            desDir.mkdirs();
        }
        if (!desDir.exists()) {
            desDir.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(srcDir).getChannel();
            destination = new FileOutputStream(desDir).getChannel();
            destination.transferFrom(source, 0, source.size());

            imageUri = Uri.parse(desDir.getPath());
            Log.d("ImagePath", "copyFile:" + imageUri);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//only image
        //galleryActivityResultLauncher.launch(intent);
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
        //cameraActivityResultLauncher.launch(intent);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
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


    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
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

    private void setValueToFields(QualificationModel qualificationModel) {
        //10th details
        if (qualificationModel.getSchool_10() != null && !qualificationModel.getSchool_10().equalsIgnoreCase("")) {
            //visible the 10th layout
            layout10.setVisibility(View.VISIBLE);

            //set values to fields
            school10Et.setText(qualificationModel.getSchool_10());
            percentage10Et.setText(qualificationModel.getPercentage_10());
            board10Et.setText(qualificationModel.getBoard_10());
            yearOfPassing10Et.setText(qualificationModel.getYear_passing_10());
            if (qualificationModel.getImage_10() != null && !qualificationModel.getImage_10().equalsIgnoreCase("")) {
                image10Pg.setVisibility(View.VISIBLE);
                Picasso.get().load(qualificationModel.getImage_10()).fit().into(image10Iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        image10Uri = null;
                        addImage10Btn.setText("Update");
                        image10Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("IMAGE 10 ERROR", e.toString());
                        showToast(e.toString());
                    }
                });

            }

            //update view12 field
            view12 = true;
        }

        //12th details
        if (qualificationModel.getChoice_12() != null && !qualificationModel.getChoice_12().equalsIgnoreCase("")) {
            //visible the 12th layout
            layout12.setVisibility(View.VISIBLE);

            //set values to fields
            school12Et.setText(qualificationModel.getSchool_12());
            percentage12Et.setText(qualificationModel.getPercentage_12());
            board12Et.setText(qualificationModel.getBoard_12());
            yearOfPassing12Et.setText(qualificationModel.getYear_passing_12());
            if (qualificationModel.getImage_12() != null && !qualificationModel.getImage_12().equalsIgnoreCase("")) {
                image12Pg.setVisibility(View.VISIBLE);
                Picasso.get().load(qualificationModel.getImage_12()).fit().into(image12Iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        image12Uri = null;
                        addImage12Btn.setText("Update");
                        image12Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("IMAGE 12 ERROR", e.toString());
                        showToast(e.toString());
                    }
                });

            }

            //update viewG field
            viewG = true;
        }

        //graduation details
        if (qualificationModel.getGraduation_choice() != null && !qualificationModel.getGraduation_choice().equalsIgnoreCase("")) {
            //visible the graduation layout
            layoutG.setVisibility(View.VISIBLE);

            //set values to fields
            courseGEt.setText(qualificationModel.getGraduation_course());
            collegeGEt.setText(qualificationModel.getGraduation_university());
            percentageGEt.setText(qualificationModel.getGraduation_percentage());
            universityGEt.setText(qualificationModel.getGraduation_board());
            yearOfPassingGEt.setText(qualificationModel.getGraduation_year_passing());
            if (qualificationModel.getGraduation_image() != null && !qualificationModel.getGraduation_image().equalsIgnoreCase("")) {
                imageGPg.setVisibility(View.VISIBLE);
                Picasso.get().load(qualificationModel.getGraduation_image()).fit().into(imageGIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageGUri = null;
                        addImageGBtn.setText("Update");
                        imageGPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("IMAGE G ERROR", e.toString());
                        showToast(e.toString());
                    }
                });

            }

            //update viewPG field
            viewPG = true;
        }

        //post graduation details
        if (qualificationModel.getPost_graduation_choice() != null && !qualificationModel.getPost_graduation_choice().equalsIgnoreCase("")) {
            //visible the post graduation layout
            layoutPG.setVisibility(View.VISIBLE);

            //set values to fields
            coursePGEt.setText(qualificationModel.getPost_graduation_course());
            collegePGEt.setText(qualificationModel.getPost_graduation_university());
            percentagePGEt.setText(qualificationModel.getPost_graduation_percentage());
            universityPGEt.setText(qualificationModel.getPost_graduation_board());
            yearOfPassingPGEt.setText(qualificationModel.getPost_graduation_year_passing());
            if (qualificationModel.getPost_graduation_image() != null && !qualificationModel.getPost_graduation_image().equalsIgnoreCase("")) {
                imagePGPg.setVisibility(View.VISIBLE);
                Picasso.get().load(qualificationModel.getPost_graduation_image()).fit().into(imagePGIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        imagePGUri = null;
                        addImagePGBtn.setText("Update");
                        imagePGPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("IMAGE PG ERROR", e.toString());
                        showToast(e.toString());
                    }
                });

            }

            //update viewOther field
            viewOther = true;
        }

        //other qualification details
        if (qualificationModel.getOther_qualification_choice() != null && !qualificationModel.getOther_qualification_choice().equalsIgnoreCase("")) {
            //visible the other qualification layout
            layoutOther.setVisibility(View.VISIBLE);

            //set values to fields
            courseOtherEt.setText(qualificationModel.getOther_qualification_course());
            collegeOtherEt.setText(qualificationModel.getOther_qualification_university());
            percentageOtherEt.setText(qualificationModel.getOther_qualification_percentage());
            universityOtherEt.setText(qualificationModel.getOther_qualification_board());
            yearOfPassingOtherEt.setText(qualificationModel.getOther_qualification_year_passing());
            if (qualificationModel.getOther_qualification_image() != null && !qualificationModel.getOther_qualification_image().equalsIgnoreCase("")) {
                imageOtherPg.setVisibility(View.VISIBLE);
                Picasso.get().load(qualificationModel.getOther_qualification_image()).fit().into(imageOtherIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageOtherUri = null;
                        addImageOtherBtn.setText("Update");
                        imageOtherPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("IMAGE G ERROR", e.toString());
                        showToast(e.toString());
                    }
                });

            }
        }

        //check if request is pending or not
        if (!qualificationModel.getRequest().isEmpty()) {
            warningTv.setVisibility(View.VISIBLE);

            //disable 10 detail save button
            save10DataBtn.setAlpha(0.6f);
            save10DataBtn.setEnabled(false);

            //disable 12 detail save button
            save12DataBtn.setAlpha(0.6f);
            save12DataBtn.setEnabled(false);

            //disable G detail save button
            saveGDataBtn.setAlpha(0.6f);
            saveGDataBtn.setEnabled(false);

            //disable PG detail save button
            savePGDataBtn.setAlpha(0.6f);
            savePGDataBtn.setEnabled(false);

            //disable Other detail save button
            saveOtherDataBtn.setAlpha(0.6f);
            saveOtherDataBtn.setEnabled(false);

        } else {
            warningTv.setVisibility(View.GONE);

            //disable 10 detail save button
            save10DataBtn.setAlpha(1f);
            save10DataBtn.setEnabled(true);

            //disable 12 detail save button
            save12DataBtn.setAlpha(1f);
            save12DataBtn.setEnabled(true);

            //disable G detail save button
            saveGDataBtn.setAlpha(1f);
            saveGDataBtn.setEnabled(true);

            //disable PG detail save button
            savePGDataBtn.setAlpha(1f);
            savePGDataBtn.setEnabled(true);

            //disable Other detail save button
            saveOtherDataBtn.setAlpha(1f);
            saveOtherDataBtn.setEnabled(true);
        }
    }

    private void getAllQualification() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_QUALIFICATION_DATA_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("QUALIFICATION DATA", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    QualificationModel qualificationModel = new QualificationModel();
                    //10 details
                    qualificationModel.setSchool_10(object.getString("10_school"));
                    qualificationModel.setBoard_10(object.getString("10_board"));
                    qualificationModel.setPercentage_10(object.getString("10_percentage"));
                    qualificationModel.setYear_passing_10(object.getString("10_year_passing"));
                    qualificationModel.setImage_10(object.getString("10_image"));

                    //12 details
                    qualificationModel.setChoice_12(object.getString("12_choice"));
                    qualificationModel.setSchool_12(object.getString("12_school"));
                    qualificationModel.setBoard_12(object.getString("12_board"));
                    qualificationModel.setPercentage_12(object.getString("12_percentage"));
                    qualificationModel.setYear_passing_12(object.getString("12_year_passing"));
                    qualificationModel.setImage_12(object.getString("12_image"));

                    //graduation details
                    qualificationModel.setGraduation_choice(object.getString("graduation_choice"));
                    qualificationModel.setGraduation_course(object.getString("graduation_course"));
                    qualificationModel.setGraduation_university(object.getString("graduation_university"));
                    qualificationModel.setGraduation_board(object.getString("graduation_board"));
                    qualificationModel.setGraduation_percentage(object.getString("graduation_percentage"));
                    qualificationModel.setGraduation_year_passing(object.getString("graduation_year_passing"));
                    qualificationModel.setGraduation_image(object.getString("graduation_image"));

                    //post graduation details
                    qualificationModel.setPost_graduation_choice(object.getString("post_graduation_choice"));
                    qualificationModel.setPost_graduation_course(object.getString("post_graduation_course"));
                    qualificationModel.setPost_graduation_university(object.getString("post_graduation_university"));
                    qualificationModel.setPost_graduation_board(object.getString("post_graduation_board"));
                    qualificationModel.setPost_graduation_percentage(object.getString("post_graduation_percentage"));
                    qualificationModel.setPost_graduation_year_passing(object.getString("post_graduation_year_passing"));
                    qualificationModel.setPost_graduation_image(object.getString("post_graduation_image"));

                    //other qualification details
                    qualificationModel.setOther_qualification_choice(object.getString("other_qualification_choice"));
                    qualificationModel.setOther_qualification_course(object.getString("other_qualification_course"));
                    qualificationModel.setOther_qualification_university(object.getString("other_qualification_university"));
                    qualificationModel.setOther_qualification_board(object.getString("other_qualification_board"));
                    qualificationModel.setOther_qualification_percentage(object.getString("other_qualification_percentage"));
                    qualificationModel.setOther_qualification_year_passing(object.getString("other_qualification_year_passing"));
                    qualificationModel.setOther_qualification_image(object.getString("other_qualification_image"));

                    qualificationModel.setRequest(object.getString("request"));
                    qualificationModel.setHo_request(object.getString("ho_request"));


                    //set values to fields
                    setValueToFields(qualificationModel);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, error -> {
            loadingDialog.hideDialog();
            Log.e("QUALIFICATION ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", Prevalent.currentOnlineUser.getUserid());
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

    private void showToast(String message) {
        Toast.makeText(QualificationActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        backBtn = findViewById(R.id.backBtn);

        // 10th views
        layout10 = findViewById(R.id.layout10);
        school10Et = findViewById(R.id.school10Et);
        percentage10Et = findViewById(R.id.percentage10Et);
        board10Et = findViewById(R.id.board10Et);
        yearOfPassing10Et = findViewById(R.id.yearOfPassing10Et);
        image10Iv = findViewById(R.id.image10Iv);
        image10Pg = findViewById(R.id.image10Pg);
        addImage10Btn = findViewById(R.id.addImage10Btn);
        save10DataBtn = findViewById(R.id.save10DataBtn);
        addMore12Btn = findViewById(R.id.addMore12Btn);

        // 12th views
        layout12 = findViewById(R.id.layout12);
        school12Et = findViewById(R.id.school12Et);
        percentage12Et = findViewById(R.id.percentage12Et);
        board12Et = findViewById(R.id.board12Et);
        yearOfPassing12Et = findViewById(R.id.yearOfPassing12Et);
        image12Iv = findViewById(R.id.image12Iv);
        image12Pg = findViewById(R.id.image12Pg);
        addImage12Btn = findViewById(R.id.addImage12Btn);
        save12DataBtn = findViewById(R.id.save12DataBtn);
        addMoreGBtn = findViewById(R.id.addMoreGBtn);

        // Graduation views
        layoutG = findViewById(R.id.layoutG);
        courseGEt = findViewById(R.id.courseGEt);
        collegeGEt = findViewById(R.id.collegeGEt);
        percentageGEt = findViewById(R.id.percentageGEt);
        universityGEt = findViewById(R.id.universityGEt);
        yearOfPassingGEt = findViewById(R.id.yearOfPassingGEt);
        imageGIv = findViewById(R.id.imageGIv);
        imageGPg = findViewById(R.id.imageGPg);
        addImageGBtn = findViewById(R.id.addImageGBtn);
        saveGDataBtn = findViewById(R.id.saveGDataBtn);
        addMorePGBtn = findViewById(R.id.addMorePGBtn);

        // Post Graduation views
        layoutPG = findViewById(R.id.layoutPG);
        coursePGEt = findViewById(R.id.coursePGEt);
        collegePGEt = findViewById(R.id.collegePGEt);
        percentagePGEt = findViewById(R.id.percentagePGEt);
        universityPGEt = findViewById(R.id.universityPGEt);
        yearOfPassingPGEt = findViewById(R.id.yearOfPassingPGEt);
        imagePGIv = findViewById(R.id.imagePGIv);
        imagePGPg = findViewById(R.id.imagePGPg);
        addImagePGBtn = findViewById(R.id.addImagePGBtn);
        savePGDataBtn = findViewById(R.id.savePGDataBtn);
        addMoreOtherBtn = findViewById(R.id.addMoreOtherBtn);

        // Other Qualification views
        layoutOther = findViewById(R.id.layoutOther);
        courseOtherEt = findViewById(R.id.courseOtherEt);
        collegeOtherEt = findViewById(R.id.collegeOtherEt);
        percentageOtherEt = findViewById(R.id.percentageOtherEt);
        universityOtherEt = findViewById(R.id.universityOtherEt);
        yearOfPassingOtherEt = findViewById(R.id.yearOfPassingOtherEt);
        imageOtherIv = findViewById(R.id.imageOtherIv);
        imageOtherPg = findViewById(R.id.imageOtherPg);
        addImageOtherBtn = findViewById(R.id.addImageOtherBtn);
        saveOtherDataBtn = findViewById(R.id.saveOtherDataBtn);

        warningTv = findViewById(R.id.warningTv);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermissions13 = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

        //loading bar
        loadingDialog = new LoadingDialog(this);
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
}