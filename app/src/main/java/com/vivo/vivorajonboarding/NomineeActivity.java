package com.vivo.vivorajonboarding;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.NomineeModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class NomineeActivity extends AppCompatActivity {


    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    SwipeRefreshLayout swipeRefreshLayout;

    ImageButton backBtn;
    //Nominee 1 Views
    TextInputEditText fatherEt, fatherNameEt, fatherDobEt, fatherAmountEt;
    //Nominee 2 Views
    TextInputEditText motherEt, motherNameEt, motherDobEt, motherAmountEt;
    //Nominee 3 Views
    TextInputEditText spouseEt, spouseNameEt, spouseDobEt, spouseAmountEt;
    //Nominee 4 Views
    TextInputEditText guardianEt, guardianNameEt, guardianDobEt, guardianAmountEt;
    //Nominee 5 Views
    Spinner siblingSpinner;
    TextInputEditText siblingNameEt, siblingDobEt, siblingAmountEt;
    //Nominee 6 Views
    TextInputEditText childOneEt, childOneNameEt, childOneDobEt, childOneAmountEt;
    //Nominee 7 Views
    TextInputEditText childTwoEt, childTwoNameEt, childTwoDobEt, childTwoAmountEt;
    private Button submitBtn;
    private TextView warningTv;

    //ArrayList
    ArrayList<String> siblingList;

    //Adapters
    ArrayAdapter<String> siblingAdapter;

    //DatePicker dialog
    DatePickerDialog datePickerDialog;
    String minDate = "2021-11-25";
    String maxDate = "2021-12-23";

    //Loading Dialog
    private LoadingDialog loadingDialog;

    //Required Variables
    String userid = "", siblingChoice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nominee);

        initialization();

        //getIntentData
        getIntentData();

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //setup relation spinner
        setupRelationSpinner();

        //setup Father Date
        setupDate(fatherDobEt);

        //setup Mother Date
        setupDate(motherDobEt);

        //setup Spouse Date
        setupDate(spouseDobEt);

        //setup Guardian Date
        setupDate(guardianDobEt);

        //setup Relation Date
        setupDate(siblingDobEt);

        //setup ChildOne Date
        setupDate(childOneDobEt);

        //setup childTwo Date
        setupDate(childTwoDobEt);

        //handle submit button click
        submitBtn.setOnClickListener(view -> {
            try {
                String father = Objects.requireNonNull(fatherEt.getText()).toString().trim();
                String fatherName = Objects.requireNonNull(fatherNameEt.getText()).toString().trim();
                String fatherDob = Objects.requireNonNull(fatherDobEt.getText()).toString().trim();
                String fatherAmt = Objects.requireNonNull(fatherAmountEt.getText()).toString().trim();

                String mother = Objects.requireNonNull(motherEt.getText()).toString().trim();
                String motherName = Objects.requireNonNull(motherNameEt.getText()).toString().trim();
                String motherDob = Objects.requireNonNull(motherDobEt.getText()).toString().trim();
                String motherAmt = Objects.requireNonNull(motherAmountEt.getText()).toString().trim();

                String spouse = Objects.requireNonNull(spouseEt.getText()).toString().trim();
                String spouseName = Objects.requireNonNull(spouseNameEt.getText()).toString().trim();
                String spouseDob = Objects.requireNonNull(spouseDobEt.getText()).toString().trim();
                String spouseAmt = Objects.requireNonNull(spouseAmountEt.getText()).toString().trim();

                String guardian = Objects.requireNonNull(guardianEt.getText()).toString().trim();
                String guardianName = Objects.requireNonNull(guardianNameEt.getText()).toString().trim();
                String guardianDob = Objects.requireNonNull(guardianDobEt.getText()).toString().trim();
                String guardianAmt = Objects.requireNonNull(guardianAmountEt.getText()).toString().trim();

                String siblingName = Objects.requireNonNull(siblingNameEt.getText()).toString().trim();
                String siblingDob = Objects.requireNonNull(siblingDobEt.getText()).toString().trim();
                String siblingAmt = Objects.requireNonNull(siblingAmountEt.getText()).toString().trim();

                String childOne = Objects.requireNonNull(childOneEt.getText()).toString().trim();
                String childOneName = Objects.requireNonNull(childOneNameEt.getText()).toString().trim();
                String childOneDob = Objects.requireNonNull(childOneDobEt.getText()).toString().trim();
                String childOneAmt = Objects.requireNonNull(childOneAmountEt.getText()).toString().trim();

                String childTwo = Objects.requireNonNull(childTwoEt.getText()).toString().trim();
                String childTwoName = Objects.requireNonNull(childTwoNameEt.getText()).toString().trim();
                String childTwoDob = Objects.requireNonNull(childTwoDobEt.getText()).toString().trim();
                String childTwoAmt = Objects.requireNonNull(childTwoAmountEt.getText()).toString().trim();

                double totalAmt;
                totalAmt = Double.parseDouble(fatherAmt.isEmpty() ? "0.0" : fatherAmt)
                        + Double.parseDouble(motherAmt.isEmpty() ? "0.0" : motherAmt)
                        + Double.parseDouble(spouseAmt.isEmpty() ? "0.0" : spouseAmt)
                        + Double.parseDouble(guardianAmt.isEmpty() ? "0.0" : guardianAmt)
                        + Double.parseDouble(siblingAmt.isEmpty() ? "0.0" : siblingAmt)
                        + Double.parseDouble(childOneAmt.isEmpty() ? "0.0" : childOneAmt)
                        + Double.parseDouble(childTwoAmt.isEmpty() ? "0.0" : childTwoAmt);

                if (totalAmt == 0.0) {
                    showToast("Please fill any one of the nominee details....");
                } else if (totalAmt == 100.00) {
                    if (!fatherAmt.isEmpty() && (fatherName.isEmpty() || fatherDob.isEmpty())) {
                        showToast("Please fill father nominee details");
                        return;
                    } else if (!motherAmt.isEmpty() && (motherName.isEmpty() || motherDob.isEmpty())) {
                        showToast("Please fill mother nominee details");
                        return;
                    } else if (!spouseAmt.isEmpty() && (spouseName.isEmpty() || spouseDob.isEmpty())) {
                        showToast("Please fill wife nominee details");
                        return;
                    } else if (!guardianAmt.isEmpty() && (guardianName.isEmpty() || guardianDob.isEmpty())) {
                        showToast("Please fill guardian details");
                        return;
                    } else if (!siblingAmt.isEmpty() && (siblingChoice.equalsIgnoreCase("Select Relation") || siblingName.isEmpty() || siblingDob.isEmpty())) {
                        showToast("Please fill relation/sibling nominee details");
                        return;
                    } else if (!childOneAmt.isEmpty() && (childOneName.isEmpty() || childOneDob.isEmpty())) {
                        showToast("Please fill children one nominee details");
                        return;
                    } else if (!childTwoAmt.isEmpty() && (childTwoName.isEmpty() || childTwoDob.isEmpty())) {
                        showToast("Please fill children two nominee details");
                        return;
                    } else {
                        submitBtn.setEnabled(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(NomineeActivity.this);
                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure ?");
                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            dialog.dismiss();

                            loadingDialog.showDialog("Uploading...");
                            //save nominee details to db
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SAVE_NOMINEE_DATA_URL, response -> {
                                Log.d("NOMINEE RESPONSE", response);
                                loadingDialog.hideDialog();
                                showToast(response);

                                getAllNomineeData();//get all nominee data

                            }, error -> {
                                loadingDialog.hideDialog();
                                Log.e("NOMINEE ERROR", error.toString());
                                showToast(error.toString());
                            }) {
                                @NonNull
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("userid", userid);
                                    params.put("father", father);
                                    params.put("fatherName", fatherName);
                                    params.put("fatherDob", fatherDob);
                                    params.put("fatherAmt", fatherAmt);
                                    params.put("mother", mother);
                                    params.put("motherName", motherName);
                                    params.put("motherDob", motherDob);
                                    params.put("motherAmt", motherAmt);
                                    params.put("wife", spouse);
                                    params.put("wifeName", spouseName);
                                    params.put("wifeDob", spouseDob);
                                    params.put("wifeAmt", spouseAmt);
                                    params.put("guardian", guardian);
                                    params.put("guardianName", guardianName);
                                    params.put("guardianDob", guardianDob);
                                    params.put("guardianAmt", guardianAmt);
                                    params.put("sibling", siblingChoice);
                                    params.put("siblingName", siblingName);
                                    params.put("siblingDob", siblingDob);
                                    params.put("siblingAmt", siblingAmt);
                                    params.put("childOne", childOne);
                                    params.put("childOneName", childOneName);
                                    params.put("childOneDob", childOneDob);
                                    params.put("childOneAmt", childOneAmt);
                                    params.put("childTwo", childTwo);
                                    params.put("childTwoName", childTwoName);
                                    params.put("childTwoDob", childTwoDob);
                                    params.put("childTwoAmt", childTwoAmt);
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

                        });
                        builder.setNegativeButton("No", (dialog, which) -> {
                            submitBtn.setEnabled(false);
                            dialog.cancel();
                        });
                        builder.setOnCancelListener(dialog -> submitBtn.setEnabled(true));
                        builder.show();
                    }
                } else {
                    showToast("Total percent of nominee share will be 100%");
                }

            } catch (Exception e) {
                showToast(e.toString());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getAllNomineeData();//getAllNomineeData
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            userid = intent.getStringExtra("userid");
        }
    }

    private void setupRelationSpinner() {
        siblingList = new ArrayList<>();
        siblingList.add("Select Relation");
        siblingList.add("Brother");
        siblingList.add("Sister");

        siblingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, siblingList);
        siblingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siblingSpinner.setAdapter(siblingAdapter);
        siblingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                siblingChoice = parent.getSelectedItem().toString();
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
                datePickerDialog = new DatePickerDialog(NomineeActivity.this,
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

    private void getAllNomineeData() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_NOMINEE_DATA_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("NOMINEE DATA RESPONSE", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    NomineeModel nomineeModel = new NomineeModel();
                    nomineeModel.setId(object.getString("id"));
                    nomineeModel.setUserid(object.getString("userid"));
                    //father details
                    nomineeModel.setFather_relation(object.getString("father_relation"));
                    nomineeModel.setFather_n_name(object.getString("father_n_name"));
                    nomineeModel.setFather_dob(object.getString("father_dob"));
                    nomineeModel.setFather_amount(object.getString("father_amount"));
                    //mother details
                    nomineeModel.setMother_relation(object.getString("mother_relation"));
                    nomineeModel.setMother_n_name(object.getString("mother_n_name"));
                    nomineeModel.setMother_dob(object.getString("mother_dob"));
                    nomineeModel.setMother_amount(object.getString("mother_amount"));
                    //wife details
                    nomineeModel.setWife_relation(object.getString("wife_relation"));
                    nomineeModel.setWife_n_name(object.getString("wife_n_name"));
                    nomineeModel.setWife_dob(object.getString("wife_dob"));
                    nomineeModel.setWife_amount(object.getString("wife_amount"));
                    //guardian details
                    nomineeModel.setGuardian_relation(object.getString("guardian_relation"));
                    nomineeModel.setGuardian_name(object.getString("guardian_name"));
                    nomineeModel.setGuardian_dob(object.getString("guardian_dob"));
                    nomineeModel.setGuardian_amount(object.getString("guardian_amount"));
                    //sibling details
                    nomineeModel.setSibling_relation(object.getString("sibling_relation"));
                    nomineeModel.setSibling_name(object.getString("sibling_name"));
                    nomineeModel.setSibling_dob(object.getString("sibling_dob"));
                    nomineeModel.setSibling_amount(object.getString("sibling_amount"));
                    //child one details
                    nomineeModel.setChild_one_relation(object.getString("child_one_relation"));
                    nomineeModel.setChild_one_name(object.getString("child_one_name"));
                    nomineeModel.setChild_one_dob(object.getString("child_one_dob"));
                    nomineeModel.setChild_one_amount(object.getString("child_one_amount"));
                    //child two details
                    nomineeModel.setChild_two_relation(object.getString("child_two_relation"));
                    nomineeModel.setChild_two_name(object.getString("child_two_name"));
                    nomineeModel.setChild_two_dob(object.getString("child_two_dob"));
                    nomineeModel.setChild_two_amount(object.getString("child_two_amount"));

                    nomineeModel.setRequest(object.getString("request"));
                    nomineeModel.setHo_request(object.getString("ho_request"));

                    //set father details
                    fatherEt.setText(nomineeModel.getFather_relation());
                    fatherNameEt.setText(nomineeModel.getFather_n_name());
                    fatherDobEt.setText(nomineeModel.getFather_dob());
                    fatherAmountEt.setText(nomineeModel.getFather_amount());

                    //set mother details
                    motherEt.setText(nomineeModel.getMother_relation());
                    motherNameEt.setText(nomineeModel.getMother_n_name());
                    motherDobEt.setText(nomineeModel.getMother_dob());
                    motherAmountEt.setText(nomineeModel.getMother_amount());

                    //set wife details
                    spouseEt.setText(nomineeModel.getWife_relation());
                    spouseNameEt.setText(nomineeModel.getWife_n_name());
                    spouseDobEt.setText(nomineeModel.getWife_dob());
                    spouseAmountEt.setText(nomineeModel.getWife_amount());

                    //set guardian details
                    guardianEt.setText(nomineeModel.getGuardian_relation());
                    guardianNameEt.setText(nomineeModel.getGuardian_name());
                    guardianDobEt.setText(nomineeModel.getGuardian_dob());
                    guardianAmountEt.setText(nomineeModel.getGuardian_amount());

                    //set sibling details
                    siblingSpinner.setSelection(siblingList.indexOf(!nomineeModel.getSibling_relation().isEmpty() ? nomineeModel.getSibling_relation() : 0));
                    siblingNameEt.setText(nomineeModel.getSibling_name());
                    siblingDobEt.setText(nomineeModel.getSibling_dob());
                    siblingAmountEt.setText(nomineeModel.getSibling_amount());

                    //set child one details
                    childOneEt.setText(nomineeModel.getChild_one_relation());
                    childOneNameEt.setText(nomineeModel.getChild_one_name());
                    childOneDobEt.setText(nomineeModel.getChild_one_dob());
                    childOneAmountEt.setText(nomineeModel.getChild_one_amount());

                    //set child two details
                    childTwoEt.setText(nomineeModel.getChild_two_relation());
                    childTwoNameEt.setText(nomineeModel.getChild_two_name());
                    childTwoDobEt.setText(nomineeModel.getChild_two_dob());
                    childTwoAmountEt.setText(nomineeModel.getChild_two_amount());

                    //check if request is pending or not
                    if (!nomineeModel.getRequest().isEmpty()) {
                        warningTv.setVisibility(View.VISIBLE);
                        //hide save nominee button
                        submitBtn.setAlpha(0.6f);
                        submitBtn.setEnabled(false);
                    } else {
                        warningTv.setVisibility(View.GONE);
                        //show save nominee button
                        submitBtn.setAlpha(1f);
                        submitBtn.setEnabled(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("NOMINEE DATA ERROR", error.toString());
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

    private void showToast(String message) {
        Toast.makeText(NomineeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);

        //get All Nominee Data
        getAllNomineeData();

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

        //Nominee one
        fatherEt = findViewById(R.id.fatherEt);
        fatherNameEt = findViewById(R.id.fatherNameEt);
        fatherDobEt = findViewById(R.id.fatherDobEt);
        fatherAmountEt = findViewById(R.id.fatherAmountEt);
        motherEt = findViewById(R.id.motherEt);
        motherNameEt = findViewById(R.id.motherNameEt);
        motherDobEt = findViewById(R.id.motherDobEt);
        motherAmountEt = findViewById(R.id.motherAmountEt);
        spouseEt = findViewById(R.id.spouseEt);
        spouseNameEt = findViewById(R.id.spouseNameEt);
        spouseDobEt = findViewById(R.id.spouseDobEt);
        spouseAmountEt = findViewById(R.id.spouseAmountEt);
        guardianEt = findViewById(R.id.guardianEt);
        guardianNameEt = findViewById(R.id.guardianNameEt);
        guardianDobEt = findViewById(R.id.guardianDobEt);
        guardianAmountEt = findViewById(R.id.guardianAmountEt);
        siblingSpinner = findViewById(R.id.siblingSpinner);
        siblingNameEt = findViewById(R.id.siblingNameEt);
        siblingDobEt = findViewById(R.id.siblingDobEt);
        siblingAmountEt = findViewById(R.id.siblingAmountEt);
        childOneEt = findViewById(R.id.childOneEt);
        childOneNameEt = findViewById(R.id.childOneNameEt);
        childOneDobEt = findViewById(R.id.childOneDobEt);
        childOneAmountEt = findViewById(R.id.childOneAmountEt);
        childTwoEt = findViewById(R.id.childTwoEt);
        childTwoNameEt = findViewById(R.id.childTwoNameEt);
        childTwoDobEt = findViewById(R.id.childTwoDobEt);
        childTwoAmountEt = findViewById(R.id.childTwoAmountEt);
        submitBtn = findViewById(R.id.submitBtn);
        warningTv = findViewById(R.id.warningTv);
        loadingDialog = new LoadingDialog(this);
    }
}