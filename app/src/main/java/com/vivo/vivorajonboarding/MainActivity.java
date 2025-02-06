package com.vivo.vivorajonboarding;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout logoContainer;

    private TextView welcomeTitle;
    private TextView welcomeDescription;
    private ShimmerFrameLayout shimmerFrameLayout;
    private MaterialCardView slideButton;
    private float initialX;
    private float slideButtonInitialX;
    private boolean isSliding = false;
    private static final float SLIDE_THRESHOLD = 0.7f; // 70% of screen width

    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "prefs";
    public static final String user_id = "id";
    public static final String user_password = "password";
    GifImageView gifImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_main);

        initializeViews();
        setupAnimations();
        checkUserLoggedIn();
        setupSlideAnimation();
    }

    private void setupWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
    }

    private void initializeViews() {
        logoContainer = findViewById(R.id.logoContainer);
        welcomeTitle = findViewById(R.id.welcomeTitle);
        welcomeDescription = findViewById(R.id.welcomeDescription);
        shimmerFrameLayout = findViewById(R.id.shimmerContainer);
        slideButton = findViewById(R.id.slideButton);
        gifImageView = findViewById(R.id.getStartedBtn);

        try {
            // Get the GIF drawable from the background
            GifDrawable gifDrawable = (GifDrawable) gifImageView.getBackground();

            // Set looping behavior to infinite
            gifDrawable.setLoopCount(0); // 0 means infinite loops

            // Start the animation
            gifDrawable.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadingDialog = new LoadingDialog(this);
    }

    private void setupAnimations() {
        // Initial state - views are slightly offset and invisible
        float translationDistance = 50f;

        logoContainer.setTranslationY(-translationDistance);
        logoContainer.setAlpha(0f);

        gifImageView.setTranslationY(translationDistance);
        gifImageView.setAlpha(0f);

        welcomeTitle.setTranslationY(translationDistance);
        welcomeTitle.setAlpha(0f);

        welcomeDescription.setTranslationY(translationDistance);
        welcomeDescription.setAlpha(0f);

        // Animate views sequentially
        logoContainer.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .withEndAction(() -> {
                    gifImageView.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(800)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                    welcomeTitle.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setStartDelay(200)
                            .setDuration(800)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                    welcomeDescription.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setStartDelay(400)
                            .setDuration(800)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();

        // Start shimmer effect
        shimmerFrameLayout.startShimmer();
    }

    private void setupSlideAnimation() {
        CardView cardContainer = findViewById(R.id.cardContainer);
        slideButton.post(() -> slideButtonInitialX = slideButton.getX());

        slideButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getRawX();
                    isSliding = true;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (isSliding) {
                        float deltaX = event.getRawX() - initialX;
                        float newX = slideButtonInitialX + deltaX;
                        float maxSlide = cardContainer.getWidth() - slideButton.getWidth() - cardContainer.getPaddingEnd();

                        // Constrain movement
                        newX = Math.max(slideButtonInitialX, Math.min(maxSlide, newX));
                        slideButton.setX(newX);

                        // Calculate progress for alpha
                        float progress = (newX - slideButtonInitialX) / (maxSlide - slideButtonInitialX);
                        slideButton.setAlpha(1 - (progress * 0.5f));

                        // Check if slide is complete
                        if (progress > SLIDE_THRESHOLD) {
                            isSliding = false;
                            animateSuccess();
                        }
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isSliding) {
                        isSliding = false;
                        // Reset position with animation
                        slideButton.animate()
                                .x(slideButtonInitialX)
                                .alpha(1f)
                                .setDuration(300)
                                .setInterpolator(new OvershootInterpolator())
                                .start();
                    }
                    return true;
            }
            return false;
        });
    }
    private void animateSuccess() {
        // Animate slide button fade out
        slideButton.animate()
                .alpha(0f)
                .setDuration(200)
                .start();

        // Start exit animations
        animateViewsForExit();
    }

    private void animateViewsForExit() {
        float translationDistance = -100f;

        // Background color animation
        ValueAnimator colorAnimation = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                Color.WHITE,
                getResources().getColor(R.color.blue_shade)
        );
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(animator ->
                getWindow().setBackgroundDrawableResource(android.R.color.white));
        colorAnimation.start();

        // Animate views with sequence and springs
        SpringForce springForce = new SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

        // Logo animation
        SpringAnimation logoSpring = new SpringAnimation(logoContainer, DynamicAnimation.TRANSLATION_Y);
        logoSpring.setSpring(springForce).setStartValue(0f).animateToFinalPosition(translationDistance);
        logoContainer.animate().alpha(0f).setDuration(500).start();

        // Animation container scale and fade
        gifImageView.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .alpha(0f)
                .setDuration(400)
                .setInterpolator(new AnticipateInterpolator())
                .start();

        // Title and description animations with spring effect
        SpringAnimation titleSpring = new SpringAnimation(welcomeTitle, DynamicAnimation.TRANSLATION_Y);
        titleSpring.setSpring(springForce).setStartValue(0f).animateToFinalPosition(translationDistance);
        welcomeTitle.animate().alpha(0f).setDuration(500).start();

        SpringAnimation descSpring = new SpringAnimation(welcomeDescription, DynamicAnimation.TRANSLATION_Y);
        descSpring.setSpring(springForce).setStartValue(0f).animateToFinalPosition(translationDistance);
        welcomeDescription.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction(() -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                    // Create activity transition animation
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(
                            MainActivity.this,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    );

                    startActivity(intent, options.toBundle());
                    finish();
                })
                .start();
    }

    private void checkUserLoggedIn() {
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String userid = sharedPreferences.getString(user_id, "");
        final String password = sharedPreferences.getString(user_password, "");

        if (!userid.isEmpty() || !password.isEmpty()) {
            login(userid, password);
        }
    }

    // Rest of the login implementation remains the same
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

    @Override
    protected void onResume() {
        super.onResume();
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.startShimmer();
        }
    }

    @Override
    protected void onPause() {
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmer();
        }
        super.onPause();
    }
}