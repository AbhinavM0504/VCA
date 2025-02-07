// LoginActivity.java
package com.vivo.vivorajonboarding;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputEditText usernameEt;
    private TextInputEditText passwordEt;
    private CheckBox rememberCbx;

    private SessionManager sessionManager;
    private LoadingDialog loadingDialog;
    private NetworkChangeListener networkChangeListener;
    private VolleyRequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_login);

        initializeComponents();
        checkPreviousLogin();
        setupLoginButton();
    }

    private void setupWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    private void initializeComponents() {
        usernameEt = findViewById(R.id.usernameEt);
        passwordEt = findViewById(R.id.passwordEt);
        rememberCbx = findViewById(R.id.rememberCbx);

        sessionManager = new SessionManager(this);
        loadingDialog = new LoadingDialog(this);
        networkChangeListener = new NetworkChangeListener();
        requestManager = new VolleyRequestManager(this);
    }

    private void checkPreviousLogin() {
        if (sessionManager.isLoggedIn() && sessionManager.isRememberMeEnabled()) {
            UserModel savedUser = sessionManager.getUserDetails();
            if (savedUser.getUserid() != null && savedUser.getPassword() != null) {
                performLogin(savedUser.getUserid(), savedUser.getPassword());
            }
        }
    }

    private void setupLoginButton() {
        findViewById(R.id.loginBtn).setOnClickListener(view -> {
            String username = Objects.requireNonNull(usernameEt.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordEt.getText()).toString().trim();

            if (validateInputs(username, password)) {
                performLogin(username, password);
            }
        });
    }

    private boolean validateInputs(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            usernameEt.setError("Username Required...");
            usernameEt.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Password Required...");
            passwordEt.requestFocus();
            return false;
        }
        return true;
    }

    private void performLogin(String username, String password) {
        loadingDialog.showDialog("Signing In");

        StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                URLs.LOGIN_URL,
                this::handleLoginResponse,
                this::handleLoginError
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                return createLoginParams(username, password);
            }
        };

        requestManager.addToRequestQueue(loginRequest);
    }

    private void handleLoginResponse(String response) {
        loadingDialog.hideDialog();
        Log.d(TAG, "Login response: " + response);

        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0) {
                processUserData(jsonArray.getJSONObject(0));
            } else {
                showToast("Invalid credentials");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: ", e);
            showToast("Invalid response from server");
        }
    }

    private void processUserData(JSONObject userObject) throws JSONException {
        UserModel userModel = parseUserModel(userObject);

        if ("user".equalsIgnoreCase(userModel.getCategory())) {
            sessionManager.createLoginSession(userModel, rememberCbx.isChecked());
            saveAppVersionInfo();
            navigateToUserDashboard(userModel);
        } else {
            showToast("Invalid user category");
        }
    }

    private UserModel parseUserModel(JSONObject object) throws JSONException {
        UserModel userModel = new UserModel();
        userModel.setId(object.getString("id"));
        userModel.setUserName(object.getString("username"));
        userModel.setUserid(object.getString("userid"));
        userModel.setPassword(object.getString("password"));
        userModel.setEmployee_level(object.getString("employee_level"));
        userModel.setCategory(object.getString("category"));
        userModel.setUser_status(object.getString("user_status"));
        userModel.setCandidate_category(object.getString("candidate_category"));
        return userModel;
    }

    private void saveAppVersionInfo() {
        sessionManager.saveAppVersionInfo(
                String.valueOf(BuildConfig.VERSION_CODE),
                BuildConfig.VERSION_NAME,
                Build.VERSION.RELEASE,
                String.valueOf(Build.VERSION.SDK_INT)
        );
    }

    private void navigateToUserDashboard(UserModel userModel) {
        Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class)
                .putExtra("userid", userModel.getUserid())
                .putExtra("category", userModel.getCategory());
        startActivity(intent);
        finish();
    }

    private void handleLoginError(VolleyError error) {
        loadingDialog.hideDialog();
        Log.e(TAG, "Login error: ", error);
        showToast("Login failed: " + error.getMessage());
    }

    private Map<String, String> createLoginParams(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("versionCode", String.valueOf(BuildConfig.VERSION_CODE));
        params.put("versionName", BuildConfig.VERSION_NAME);
        params.put("androidVersion", Build.VERSION.RELEASE);
        params.put("androidSDK", String.valueOf(Build.VERSION.SDK_INT));
        return params;
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerNetworkReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeListener);
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
    }
}