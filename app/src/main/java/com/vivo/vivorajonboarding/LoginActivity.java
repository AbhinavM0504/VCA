package com.vivo.vivorajonboarding;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private TextInputEditText usernameEt, passwordEt;
    private CheckBox rememberCbx;
    private Button loginBtn;
    private TextView forgotPwdTv;

    //Loading Dialog
    LoadingDialog loadingDialog;

    //Shared Preference variable
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "prefs";

    public static final String user_id = "id";
    public static final String user_password = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        initialization();

        //check if user is logged in
        checkUserLoggedIn();

        //handle login button
        loginBtn.setOnClickListener(view -> {
            String username = Objects.requireNonNull(usernameEt.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEt.getText()).toString().trim();

            if (TextUtils.isEmpty(username)) {
                usernameEt.setError("Username Required...");
                usernameEt.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                passwordEt.setError("Password Required...");
                passwordEt.requestFocus();
            } else {
                login(username, password);
            }
        });
    }

    private void checkUserLoggedIn() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String userid = sharedPreferences.getString(user_id, "");
        final String password = sharedPreferences.getString(user_password, "");

        //check for logged in or not
        if (!userid.isEmpty() || !password.isEmpty()) {
            login(userid, password);
        }
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

    private void login(String username, String password) {
        loadingDialog.showDialog("Signing In");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.LOGIN_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("LOGIN RESPONSE", response);

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    UserModel userModel = new UserModel();
                    userModel.setId(object.getString("id"));
                    userModel.setUserid(object.getString("userid"));
                    userModel.setPassword(object.getString("password"));
                    userModel.setDesignation(object.getString("designation"));
                    userModel.setDepartment(object.getString("department"));
                    userModel.setGrade(object.getString("grade"));
                    userModel.setBranch(object.getString("branch"));
                    userModel.setZone(object.getString("zone"));
                    userModel.setEmployee_level(object.getString("employee_level"));
                    userModel.setCategory(object.getString("category"));
                    userModel.setUser_status(object.getString("user_status"));
                    userModel.setSalary(object.getString("salary"));
                    userModel.setCreate_at(object.getString("create_at"));
                    userModel.setUpdate_at(object.getString("update_at"));
                    userModel.setCandidate_category(object.getString("candidate_category"));
                    userModel.setApp_version(object.getString("app_version"));
                    userModel.setImage(object.getString("image"));

                    //store current login details
                    Prevalent.currentOnlineUser = userModel;

                    if (userModel.getCategory().equalsIgnoreCase("user")) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(user_id, userModel.getUserid());
                        editor.putString(user_password, userModel.getPassword());

                        //save login details to shared preference
                        if (rememberCbx.isChecked()) {
                            editor.apply();
                        }

                        Log.d("DATA LOGIN : ", userModel.getUserid() + " : " + userModel.getCategory());

                        //redirect user to dashboard
                        startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class)
                                .putExtra("userid", userModel.getUserid())
                                .putExtra("category", userModel.getCategory()));
                        finish();

                    } else {
                        showToast(response.trim());
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("Please check your details..!!!");
            }
        }, error -> {
            Log.e("LOGIN ERROR", error.toString());
            loadingDialog.hideDialog();
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("versionCode", String.valueOf(BuildConfig.VERSION_CODE));
                params.put("versionName", BuildConfig.VERSION_NAME);
                params.put("androidVersion", Build.VERSION.RELEASE);
                params.put("androidSDK", String.valueOf(Build.VERSION.SDK_INT));
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
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        usernameEt = findViewById(R.id.usernameEt);
        passwordEt = findViewById(R.id.passwordEt);
        rememberCbx = findViewById(R.id.rememberCbx);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPwdTv = findViewById(R.id.forgotPwdTv);

        loadingDialog = new LoadingDialog(this);
    }
}