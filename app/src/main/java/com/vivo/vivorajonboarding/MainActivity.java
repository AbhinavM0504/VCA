package com.vivo.vivorajonboarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    //views
    GifImageView getStartedBtn;

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
        setContentView(R.layout.activity_main);

        initialization();

        //check if user is logged in
        checkUserLoggedIn();

        //handle get Started button click
        getStartedBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

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

                        //redirect user to dashboard
                        startActivity(new Intent(MainActivity.this, UserDashboardActivity.class)
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
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void initialization() {
        getStartedBtn = findViewById(R.id.getStartedBtn);

        loadingDialog = new LoadingDialog(this);
    }
}