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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.foysaldev.cropper.CropImage;
import com.foysaldev.cropper.CropImageView;
import com.vivo.vivorajonboarding.adapter.InsuranceAdapter;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.InsuranceModel;

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

public class InsuranceActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton backBtn;
    Button yesIncludeBtn, noIncludeBtn;
    LinearLayout membersLayout;
    RecyclerView insuranceRv;
    CardView noDataCardView;
    ImageView addInsuranceImgBtn;

    //Dialog views
    Dialog dialog;
    Spinner memberSpinner;
    TextInputEditText nameEt, dobEt;
    ImageButton frontCameraImgBtn, backCameraImgBtn;
    ImageView aadharCardFrontIv, aadharCardBackIv;
    TextView warningTv;
    Button submitBtn;

    //Loading Dialog
    LoadingDialog loadingDialog;

    //ArrayList
    String[] cameraPermissions;
    String[] storagePermissions;

    String[] cameraPermission13;
    String[] storagePermission13;

    ArrayList<String> memberRelationList;
    ArrayList<InsuranceModel> insuranceMembersList;

    //Adapters
    ArrayAdapter<String> memberAdapter;
    InsuranceAdapter insuranceAdapter;

    //Camera Variables
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    String choice = "";
    Uri imageUri = null, aadharFrontImgUri = null, aadharBackImgUri = null;
    JSONObject jsonObject;
    RequestQueue rQueue;

    //Required Variables
    DatePickerDialog datePickerDialog;
    String userid = "", memberRelationChoice = "", request = "", member_included_for_insurance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);

        initialization();

        //getIntentData
        getIntentData();

        //handle back button
        backBtn.setOnClickListener(v -> onBackPressed());

        //getAllInsuranceMembers
        getAllInsuranceMembers();

        //handle yesIncludeBtn click
        yesIncludeBtn.setOnClickListener(v -> {
            yesIncludeBtn.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();
                yesIncludeBtn.setEnabled(true);
                //update yes to database
                updateMemberSelectionForInsurance("Yes");
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                yesIncludeBtn.setEnabled(true);
                dialog.cancel();
            });
            builder.setOnCancelListener(dialog -> yesIncludeBtn.setEnabled(true));
            builder.show();
        });

        //handle noIncludeBtn click
        noIncludeBtn.setOnClickListener(v -> {
            noIncludeBtn.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();
                noIncludeBtn.setEnabled(true);
                //update no to database
                updateMemberSelectionForInsurance("No");
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                noIncludeBtn.setEnabled(true);
                dialog.cancel();
            });
            builder.setOnCancelListener(dialog -> noIncludeBtn.setEnabled(true));
            builder.show();
        });

        //handle noIncludeBtn click
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //getAllInsuranceMembers
            getAllInsuranceMembers();
            swipeRefreshLayout.setRefreshing(false);
        });

        //handle add insurance member
        addInsuranceImgBtn.setOnClickListener(v -> openAddMemberForm());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userid = intent.getStringExtra("userid");
        }
    }

    private void updateMemberSelectionForInsurance(String choice) {


        loadingDialog.showDialog("Updating...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.UPDATE_MEMBER_SELECTION_FOR_INSURANCE_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("M.SELECTION RESPONSE", response.trim());
            try {
                JSONObject object = new JSONObject(response);
                String status = object.getString("status");
                String message = object.getString("message");
                String updatedChoice = object.getString("choice");

                if (status.equalsIgnoreCase("true")) {
                    showToast(message);
                    setButtonSelection("", updatedChoice);//set the updated choice
                } else {
                    showToast(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.toString());
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("M.SELECTION ERROR", error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("insurance_choice", choice);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openAddMemberForm() {
        addInsuranceImgBtn.setEnabled(false);//disable button

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_member_insurance);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        memberSpinner = dialog.findViewById(R.id.memberSpinner);
        nameEt = dialog.findViewById(R.id.nameEt);
        dobEt = dialog.findViewById(R.id.dobEt);
        frontCameraImgBtn = dialog.findViewById(R.id.frontCameraImgBtn);
        backCameraImgBtn = dialog.findViewById(R.id.backCameraImgBtn);
        aadharCardFrontIv = dialog.findViewById(R.id.aadharCardFrontIv);
        aadharCardBackIv = dialog.findViewById(R.id.aadharCardBackIv);
        submitBtn = dialog.findViewById(R.id.submitBtn);
        ImageButton closeBtn = dialog.findViewById(R.id.closeBtn);

        //setup dob
        setupDate(dobEt);

        //setup relation spinner
        setupRelationSpinner();

        //handle frontIv Button
        frontCameraImgBtn.setOnClickListener(v -> {
            choice = "front";
            showImagePopupMenu(frontCameraImgBtn);
        });

        //handle backIv Button
        backCameraImgBtn.setOnClickListener(v -> {
            choice = "back";
            showImagePopupMenu(backCameraImgBtn);
        });

        //handle submit btn click
        submitBtn.setOnClickListener(v -> {
            addInsuranceImgBtn.setEnabled(true);
            validateData(submitBtn);
        });

        //handle close btn
        closeBtn.setOnClickListener(v -> {
            addInsuranceImgBtn.setEnabled(true);
            dialog.dismiss();
        });

        //set properties of dialog
        dialog.setOnCancelListener(dialog -> addInsuranceImgBtn.setEnabled(true));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void validateData(Button submitBtn) {
        String memberName = Objects.requireNonNull(nameEt.getText()).toString().trim();
        String memberDob = Objects.requireNonNull(dobEt.getText()).toString().trim();

        if (memberRelationChoice.equalsIgnoreCase("Select Member")) {
            showToast("Please select member relation...");
        } else if (memberName.isEmpty()) {
            showToast("Please enter member name");
        } else if (memberDob.isEmpty()) {
            showToast("Please enter member dob");
        } else if (aadharFrontImgUri == null) {
            showToast("Please add member aadhar card front image...");
        } else if (aadharBackImgUri == null) {
            showToast("Please add member aadhar card back image...");
        } else {
            //save insurance data to db
            submitBtn.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure to add this member ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.cancel();

                submitBtn.setEnabled(true);
                loadingDialog.showDialog("Saving Insurance Data...");
                new SaveInsuranceData(memberName, memberDob, aadharFrontImgUri, aadharBackImgUri).execute();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                submitBtn.setEnabled(true);
                dialog.cancel();
            });
            builder.setOnCancelListener(dialog -> submitBtn.setEnabled(true));
            builder.show();
        }
    }

    private class SaveInsuranceData extends AsyncTask<String, Void, Boolean> {

        String memberName, memberDob;
        Uri aadharFrontImgUri, aadharBackImgUri;
        Bitmap aadharFrontBitmap = null, aadharBackBitmap = null;

        public SaveInsuranceData(String memberName, String memberDob, Uri aadharFrontImgUri, Uri aadharBackImgUri) {
            this.memberName = memberName;
            this.memberDob = memberDob;
            this.aadharFrontImgUri = aadharFrontImgUri;
            this.aadharBackImgUri = aadharBackImgUri;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                //get encoded aadhar card front image
                aadharFrontBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharFrontImgUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                aadharFrontBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String acFrontEncodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                //get encoded aadhar card back image
                aadharBackBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharBackImgUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                aadharBackBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                String acBackEncodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("userid", getIntent().getStringExtra("userid"));
                    jsonObject.put("acFrontImage", acFrontEncodedImage);
                    jsonObject.put("acFrontImageName", "aadharCardFront_I_" + System.currentTimeMillis() + ".jpg");
                    jsonObject.put("acBackImage", acBackEncodedImage);
                    jsonObject.put("acBackImageName", "aadharCardBack_I_" + System.currentTimeMillis() + ".jpg");
                    jsonObject.put("memberRelation", memberRelationChoice);
                    jsonObject.put("memberName", memberName);
                    jsonObject.put("memberDob", memberDob);

                } catch (JSONException e) {
                    Log.e("JSON EXCEPTION : ", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.SAVE_INSURANCE_DATA_URL, jsonObject,
                        jsonObject -> {
                            Log.d("IMAGE RESPONSE", jsonObject.toString());
                            rQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");

                                if (status.equalsIgnoreCase("true")) {
                                    Log.d("IMAGE RESPONSE", message);
                                    showToast(message);

                                    //reset all fields
                                    resetAllFields();

                                    //close dialog
                                    dialog.dismiss();

                                    //getAllInsuranceData
                                    getAllInsuranceMembers();

                                } else {
                                    showToast(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingDialog.hideDialog();
                            }
                        }, volleyError -> {
                    Log.e("UPLOAD ERROR", volleyError.toString());
                    showToast(volleyError.toString());
                    loadingDialog.hideDialog();
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rQueue = Volley.newRequestQueue(InsuranceActivity.this);
                rQueue.add(jsonObjectRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void getAllInsuranceMembers() {
        loadingDialog.showDialog("Loading...");
        insuranceMembersList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_INSURANCE_DATA_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("INSURANCE RESPONSE", response);
            try {
                JSONObject object = new JSONObject(response);
                String status = object.getString("status");
                String message = object.getString("message");
                request = object.getString("request");
                member_included_for_insurance = object.getString("member_included_for_insurance");

                //set member include selection
                setButtonSelection(request, member_included_for_insurance);

                if (status.equalsIgnoreCase("true")) {
                    JSONArray jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        InsuranceModel model = new InsuranceModel();
                        model.setId(obj.getString("id"));
                        model.setUserid(obj.getString("userid"));
                        model.setMemberRelation(obj.getString("member_relation"));
                        model.setMemberName(obj.getString("member_name"));
                        model.setMemberDob(obj.getString("member_dob"));
                        model.setMemberAcFrontImage(obj.getString("member_ac_front_image"));
                        model.setMemberAcBackImage(obj.getString("member_ac_back_image"));
                        model.setRequest(obj.getString("request"));
                        model.setTime(obj.getString("time"));
                        insuranceMembersList.add(model);
                    }

                    if (insuranceMembersList.size() > 0) {
                        membersLayout.setVisibility(View.VISIBLE);
                        insuranceRv.setVisibility(View.VISIBLE);
                        noDataCardView.setVisibility(View.GONE);
                        insuranceAdapter = new InsuranceAdapter(this, insuranceMembersList);
                        insuranceRv.setAdapter(insuranceAdapter);
                    } else {
                        membersLayout.setVisibility(View.GONE);
                        insuranceRv.setVisibility(View.GONE);
                        noDataCardView.setVisibility(View.VISIBLE);
                    }
                } else {
                    membersLayout.setVisibility(View.GONE);
                    insuranceRv.setVisibility(View.GONE);
                    noDataCardView.setVisibility(View.VISIBLE);
                    showToast(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("INSURANCE EXCEPTION", e.toString());
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("INSURANCE ERROR", error.toString());
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

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setButtonSelection(String request, String member_included_for_insurance) {
        if (!request.isEmpty()) {
            //request already submitted
            warningTv.setVisibility(View.VISIBLE);//show warning
            yesIncludeBtn.setEnabled(false);
            yesIncludeBtn.setAlpha(0.4f);
            noIncludeBtn.setEnabled(false);
            noIncludeBtn.setAlpha(0.4f);
            addInsuranceImgBtn.setVisibility(View.GONE);

            if (member_included_for_insurance.equalsIgnoreCase("yes")) {
                //member included
                yesIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selectedColor)));//set selected color yesIncludeBtn
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.white));//set text color white
                membersLayout.setVisibility(View.VISIBLE);//show members layout
                noIncludeBtn.setBackgroundTintList(null);//set background color to null
                noIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color

            } else if (member_included_for_insurance.equalsIgnoreCase("No")) {
                //member not included
                noIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unSelectedColor)));//set unselected color noIncludeBtn
                noIncludeBtn.setTextColor(getResources().getColor(R.color.white));//set text color white
                membersLayout.setVisibility(View.GONE);//show members layout
                yesIncludeBtn.setBackgroundTintList(null);//set background color to null
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
            } else {
                //member choice not selected
                yesIncludeBtn.setBackgroundTintList(null);//set background color to null
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
                membersLayout.setVisibility(View.GONE);//show members layout
                noIncludeBtn.setBackgroundTintList(null);//set background color to null
                noIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
            }
        } else {
            //request not submitted
            warningTv.setVisibility(View.GONE);//hide warning
            yesIncludeBtn.setEnabled(true);
            yesIncludeBtn.setAlpha(1f);
            noIncludeBtn.setEnabled(true);
            noIncludeBtn.setAlpha(1);

            if (member_included_for_insurance.equalsIgnoreCase("yes")) {
                //member included
                addInsuranceImgBtn.setVisibility(View.VISIBLE);
                yesIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selectedColor)));//set selected color yesIncludeBtn
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.white));//set text color white
                membersLayout.setVisibility(View.VISIBLE);//show members layout
                noIncludeBtn.setBackgroundTintList(null);//set background color to null
                noIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color

            } else if (member_included_for_insurance.equalsIgnoreCase("No")) {
                //member not included
                addInsuranceImgBtn.setVisibility(View.GONE);
                noIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unSelectedColor)));//set unselected color noIncludeBtn
                noIncludeBtn.setTextColor(getResources().getColor(R.color.white));//set text color white
                membersLayout.setVisibility(View.GONE);//show members layout
                yesIncludeBtn.setBackgroundTintList(null);//set background color to null
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
            } else {
                //member choice not selected
                addInsuranceImgBtn.setVisibility(View.GONE);
                yesIncludeBtn.setBackgroundTintList(null);//set background color to null
                yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
                membersLayout.setVisibility(View.GONE);//show members layout
                noIncludeBtn.setBackgroundTintList(null);//set background color to null
                noIncludeBtn.setTextColor(getResources().getColor(R.color.black));//set text black color
            }
        }
        /*if (member_included_for_insurance.equalsIgnoreCase("yes")) {
            yesIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.selectedColor)));
            yesIncludeBtn.setTextColor(getResources().getColor(R.color.white));
            noIncludeBtn.setBackgroundTintList(null);
            noIncludeBtn.setTextColor(getResources().getColor(R.color.black));
            //show family members list
            membersLayout.setVisibility(View.VISIBLE);

            //check if request is empty or not
            if (!request.isEmpty()) {
                //disable all button
                yesIncludeBtn.setEnabled(false);
                yesIncludeBtn.setAlpha(0.4f);
                warningTv.setVisibility(View.VISIBLE);//show warning
                addInsuranceImgBtn.setVisibility(View.GONE);

            } else {
                //enable all button
                yesIncludeBtn.setEnabled(true);
                yesIncludeBtn.setAlpha(1f);
                warningTv.setVisibility(View.GONE);//hide warning
                addInsuranceImgBtn.setVisibility(View.VISIBLE);
            }

        } else if (member_included_for_insurance.equalsIgnoreCase("no")) {
            yesIncludeBtn.setBackgroundTintList(null);
            yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));
            noIncludeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unSelectedColor)));
            noIncludeBtn.setTextColor(getResources().getColor(R.color.white));
            //hide family members list
            membersLayout.setVisibility(View.GONE);
            addInsuranceImgBtn.setVisibility(View.GONE);
            //checkRequestAndHideButton();
            if (!request.isEmpty()) {
                //disable all button
                noIncludeBtn.setEnabled(false);
                noIncludeBtn.setAlpha(0.4f);
                warningTv.setVisibility(View.VISIBLE);//show warning
            } else {
                //enable all button
                noIncludeBtn.setEnabled(true);
                noIncludeBtn.setAlpha(1f);
                warningTv.setVisibility(View.GONE);//hide warning
            }

        } else {
            yesIncludeBtn.setBackgroundTintList(null);
            yesIncludeBtn.setTextColor(getResources().getColor(R.color.black));
            noIncludeBtn.setBackgroundTintList(null);
            noIncludeBtn.setTextColor(getResources().getColor(R.color.black));
            //hide addInsuranceBtn
            membersLayout.setVisibility(View.GONE);
            addInsuranceImgBtn.setVisibility(View.GONE);
            //checkRequestAndHideButton();
            if (!request.isEmpty()) {
                //disable all button
                warningTv.setVisibility(View.VISIBLE);//show warning
            } else {
                //enable all button
                warningTv.setVisibility(View.GONE);//hide warning
            }

        }
        //checkRequestAndHideButton();*/
    }

    private void checkRequestAndHideButton() {
        //hide button based on request status
        if (!request.isEmpty()) {
            //disable all button
            yesIncludeBtn.setEnabled(false);
            yesIncludeBtn.setAlpha(0.4f);
            noIncludeBtn.setEnabled(false);
            noIncludeBtn.setAlpha(0.4f);
            warningTv.setVisibility(View.VISIBLE);//show warning
        } else {
            //enable all button
            yesIncludeBtn.setEnabled(true);
            yesIncludeBtn.setAlpha(1f);
            noIncludeBtn.setEnabled(true);
            noIncludeBtn.setAlpha(1f);
            warningTv.setVisibility(View.GONE);//hide warning
        }
    }

    private void resetAllFields() {
        memberSpinner.setSelection(0);
        nameEt.setText("");
        dobEt.setText("");
        aadharFrontImgUri = null;
        Picasso.get().load(R.drawable.ic_raw_image).into(aadharCardFrontIv);
        aadharBackImgUri = null;
        Picasso.get().load(R.drawable.ic_raw_image).into(aadharCardBackIv);
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

                if (choice.equalsIgnoreCase("front")) {
                    aadharFrontImgUri = resultUri;
                    Picasso.get().load(aadharFrontImgUri).fit().into(aadharCardFrontIv);
                } else if (choice.equalsIgnoreCase("back")) {
                    aadharBackImgUri = resultUri;
                    Picasso.get().load(aadharBackImgUri).fit().into(aadharCardBackIv);
                }
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
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
            ActivityCompat.requestPermissions(this, cameraPermission13, CAMERA_REQUEST_CODE);
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
            ActivityCompat.requestPermissions(this, storagePermission13, STORAGE_REQUEST_CODE);
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

    private void showImagePopupMenu(ImageButton imgBtn) {
        PopupMenu imageMenu = new PopupMenu(this, imgBtn);
        imageMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        imageMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");
        imageMenu.show();
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

    private void setupRelationSpinner() {
        memberRelationList = new ArrayList<>();
        memberRelationList.add("Select Member");
        memberRelationList.add("Father");
        memberRelationList.add("Mother");
        memberRelationList.add("Spouse");
        memberRelationList.add("Child");
        memberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, memberRelationList);
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(memberAdapter);
        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memberRelationChoice = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                datePickerDialog = new DatePickerDialog(InsuranceActivity.this,
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        backBtn = findViewById(R.id.backBtn);
        warningTv = findViewById(R.id.warningTv);
        yesIncludeBtn = findViewById(R.id.yesIncludeBtn);
        noIncludeBtn = findViewById(R.id.noIncludeBtn);
        membersLayout = findViewById(R.id.membersLayout);
        insuranceRv = findViewById(R.id.insuranceRv);
        noDataCardView = findViewById(R.id.noDataCardView);
        addInsuranceImgBtn = findViewById(R.id.addInsuranceImgBtn);

        loadingDialog = new LoadingDialog(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission13 = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
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