package com.vivo.vivorajonboarding;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;
import com.vivo.vivorajonboarding.model.UserModel;


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

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
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

        LoadingDialog loadingDialog = new LoadingDialog(this);
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
        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            // Get all user details
            UserModel user = sessionManager.getUserDetails();
            SessionManager.AppVersionInfo appInfo = sessionManager.getAppVersionInfo();

            // Log all session values
            Log.d("SessionCheck", "User is logged in");
            Log.d("SessionCheck", "ID: " + user.getId());
            Log.d("SessionCheck", "Username: " + user.getUserName());
            Log.d("SessionCheck", "UserID: " + user.getUserid());
            Log.d("SessionCheck", "Employee Level: " + user.getEmployee_level());
            Log.d("SessionCheck", "Category: " + user.getCategory());
            Log.d("SessionCheck", "User Status: " + user.getUser_status());
            Log.d("SessionCheck", "Candidate Category: " + user.getCandidate_category());
            Log.d("SessionCheck", "Remember Me: " + sessionManager.isRememberMeEnabled());

            // Log app version information
            Log.d("SessionCheck", "App Version Code: " + appInfo.getVersionCode());
            Log.d("SessionCheck", "App Version Name: " + appInfo.getVersionName());
            Log.d("SessionCheck", "Android Version: " + appInfo.getAndroidVersion());
            Log.d("SessionCheck", "Android SDK: " + appInfo.getAndroidSDK());

            // Redirect to LandingPageActivity
            Intent intent = new Intent(this, LandingPageActivity.class);
            startActivity(intent);
            finish();
        }
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