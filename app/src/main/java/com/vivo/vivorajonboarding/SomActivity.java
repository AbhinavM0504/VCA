package com.vivo.vivorajonboarding;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SomActivity extends AppCompatActivity {

    private LinearLayout subDepartmentsContainer;
    private final String[] departmentNames = {
            "Digital Marketing Department",
            "VBA Training Department",
            "Promotion Sales Service",
            "VBA Management Department",
            "Branding and Promotion Department"
    };

    private final String[] managerNames = {
            "Mudit Sharma",
            "Aditya Sharma",
            "Anil Kumar",
            "Manpreet Kaur",
            "Mr. Shyam Sharma"
    };

    private final String[] managerPhones = {
            "+91 98765 43210",
            "+91 98765 43211",
            "+91 98765 43212",
            "+91 98765 43213",
            "+91 98765 43214"
    };

    private final String[] managerEmails = {
            "mudit.sharma@vivo.com",
            "aditya.sharma@vivo.com",
            "anil.kumar@vivo.com",
            "manpreet.kaur@vivo.com",
            "shyam.sharma@vivo.com"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_som);

        subDepartmentsContainer = findViewById(R.id.containerSubDept);
        setupToolbar();

        // Start with main cards animation
        animateMainCards();
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(toolbar.getNavigationIcon()).setTint(getResources().getColor(android.R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
    private void animateMainCards() {
        View departmentHead = findViewById(R.id.cardDepartmentHead);
        View deputyHead = findViewById(R.id.cardDeputyHead);
        View line1 = findViewById(R.id.lineToDeputy);
        View line2 = findViewById(R.id.lineToSubDepts);

        // Initial states
        departmentHead.setAlpha(0f);
        departmentHead.setTranslationY(-100f);
        deputyHead.setAlpha(0f);
        deputyHead.setTranslationY(-50f);
        line1.setScaleY(0f);
        line2.setScaleY(0f);

        // Animate department head
        ObjectAnimator fadeIn1 = ObjectAnimator.ofFloat(departmentHead, "alpha", 0f, 1f);
        ObjectAnimator slideDown1 = ObjectAnimator.ofFloat(departmentHead, "translationY", -100f, 0f);

        // Animate first line
        ObjectAnimator lineScale1 = ObjectAnimator.ofFloat(line1, "scaleY", 0f, 1f);

        // Animate deputy head
        ObjectAnimator fadeIn2 = ObjectAnimator.ofFloat(deputyHead, "alpha", 0f, 1f);
        ObjectAnimator slideDown2 = ObjectAnimator.ofFloat(deputyHead, "translationY", -50f, 0f);

        // Animate second line
        ObjectAnimator lineScale2 = ObjectAnimator.ofFloat(line2, "scaleY", 0f, 1f);

        // Create animator sets for combined animations
        AnimatorSet headAnimSet = new AnimatorSet();
        headAnimSet.playTogether(fadeIn1, slideDown1);

        AnimatorSet deputyAnimSet = new AnimatorSet();
        deputyAnimSet.playTogether(fadeIn2, slideDown2);

        // Main animator set to play everything in sequence
        AnimatorSet mainAnimSet = new AnimatorSet();
        mainAnimSet.setDuration(800);
        mainAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        mainAnimSet.playSequentially(headAnimSet, lineScale1, deputyAnimSet, lineScale2);

        // Add listener to start department cards after main animation
        mainAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Add sub department cards with staggered animation
                for (int i = 0; i < departmentNames.length; i++) {
                    addSubDepartmentCard(departmentNames[i], managerNames[i], i);
                }
            }
        });

        mainAnimSet.start();
    }

    private void addSubDepartmentCard(String departmentName, String managerName, int index) {
        MaterialCardView card = new MaterialCardView(this);
        MaterialCardView.LayoutParams cardParams = new MaterialCardView.LayoutParams(
                MaterialCardView.LayoutParams.MATCH_PARENT,
                MaterialCardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        card.setLayoutParams(cardParams);

        // Enhanced card styling with dynamic elevation
        card.setCardElevation(dpToPx(2));
        card.setRadius(dpToPx(16));
        card.setStrokeWidth(dpToPx(1));
        card.setStrokeColor(getColor(R.color.pink_500));
        card.setCardBackgroundColor(getColor(R.color.white));

        // Add shimmer effect background
        ShimmerFrameLayout shimmerLayout = new ShimmerFrameLayout(this);
        shimmerLayout.setShimmer(new Shimmer.AlphaHighlightBuilder()
                .setBaseAlpha(1f)
                .setHighlightAlpha(0.7f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .setDuration(1500L)
                .build());

        // For custom shimmer effect
        LayoutInflater inflater = LayoutInflater.from(this);
        View shimmerView = inflater.inflate(R.layout.shimmer_layout, null);
        shimmerLayout.addView(shimmerView);

        // Main container with enhanced padding and background
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
        container.setBackgroundResource(R.drawable.card_gradient_background);

        // Enhanced header layout with modern spacing
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Animated icon implementation
        LottieAnimationView icon = new LottieAnimationView(this);
        icon.setAnimation(R.raw.department_icon_anim);
        icon.setRepeatCount(0);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(32), dpToPx(32));
        iconParams.setMarginEnd(dpToPx(12));
        icon.setLayoutParams(iconParams);

        // Enhanced department name with custom font and shadow
        TextView deptNameView = new TextView(this);
        deptNameView.setText(departmentName);
        deptNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        deptNameView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_bold));
        deptNameView.setTextColor(getColor(R.color.text_primary));
        deptNameView.setShadowLayer(1.5f, 0f, 1f, getColor(R.color.text_shadow));

        // Enhanced manager info layout
        LinearLayout managerInfoLayout = new LinearLayout(this);
        managerInfoLayout.setOrientation(LinearLayout.VERTICAL);
        managerInfoLayout.setPadding(dpToPx(8), dpToPx(16), dpToPx(8), dpToPx(8));
        managerInfoLayout.setBackgroundResource(R.drawable.manager_info_background);
        managerInfoLayout.setAlpha(0f);
        managerInfoLayout.setVisibility(View.GONE);

        // Manager avatar
        CircleImageView managerAvatar = new CircleImageView(this);
        managerAvatar.setImageResource(R.drawable.ic_profile_placeholder);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48));
        avatarParams.gravity = Gravity.CENTER_HORIZONTAL;
        managerAvatar.setLayoutParams(avatarParams);

        // Enhanced manager name with role
        TextView managerNameView = new TextView(this);
        managerNameView.setText(managerName);
        managerNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        managerNameView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_medium));
        managerNameView.setTextColor(getColor(R.color.text_secondary));
        managerNameView.setGravity(Gravity.CENTER);
        managerNameView.setPadding(0, dpToPx(8), 0, dpToPx(4));

        TextView managerRoleView = new TextView(this);
        managerRoleView.setText("Department Manager");
        managerRoleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        managerRoleView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));
        managerRoleView.setTextColor(getColor(R.color.text_tertiary));
        managerRoleView.setGravity(Gravity.CENTER);

        // Contact information container layouts
        LinearLayout phoneContainer = new LinearLayout(this);
        phoneContainer.setOrientation(LinearLayout.HORIZONTAL);
        phoneContainer.setGravity(Gravity.CENTER);
        phoneContainer.setVisibility(View.GONE);
        phoneContainer.setAlpha(0f);

        LinearLayout emailContainer = new LinearLayout(this);
        emailContainer.setOrientation(LinearLayout.HORIZONTAL);
        emailContainer.setGravity(Gravity.CENTER);
        emailContainer.setVisibility(View.GONE);
        emailContainer.setAlpha(0f);

        // Contact information views
        TextView phoneNumberView = new TextView(this);
        phoneNumberView.setText(managerPhones[index]);
        phoneNumberView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        phoneNumberView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_medium));
        phoneNumberView.setTextColor(getColor(R.color.text_secondary));
        phoneNumberView.setGravity(Gravity.CENTER);
        phoneNumberView.setPadding(dpToPx(30), dpToPx(8), dpToPx(0), dpToPx(8));

        TextView emailAddressView = new TextView(this);
        emailAddressView.setText(managerEmails[index]);
        emailAddressView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        emailAddressView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_medium));
        emailAddressView.setTextColor(getColor(R.color.text_secondary));
        emailAddressView.setGravity(Gravity.CENTER);
        emailAddressView.setPadding(dpToPx(30), dpToPx(8), dpToPx(0), dpToPx(8));

        // Copy buttons
        MaterialButton copyPhoneButton = createCopyButton();
        MaterialButton copyEmailButton = createCopyButton();

        // Set click listeners for copy buttons
        copyPhoneButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number", managerPhones[index]);
            clipboard.setPrimaryClip(clip);

        });

        copyEmailButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email Address", managerEmails[index]);
            clipboard.setPrimaryClip(clip);

        });

        // Add views to containers
        phoneContainer.addView(phoneNumberView);
        phoneContainer.addView(copyPhoneButton);

        emailContainer.addView(emailAddressView);
        emailContainer.addView(copyEmailButton);

        // Action buttons container
        LinearLayout actionButtons = new LinearLayout(this);
        actionButtons.setBackgroundColor(Color.TRANSPARENT);
        actionButtons.setOrientation(LinearLayout.HORIZONTAL);
        actionButtons.setGravity(Gravity.CENTER);
        actionButtons.setPadding(0, dpToPx(12), 0, dpToPx(12));

        // Create and customize buttons
        MaterialButton callButton = createActionButton(R.drawable.ic_phone, "Call");
        MaterialButton emailButton = createActionButton(R.drawable.ic_email, "Email");

        // Set click listeners for contact buttons
        callButton.setOnClickListener(v -> toggleContactInfo(phoneContainer, emailContainer, true));
        emailButton.setOnClickListener(v -> toggleContactInfo(emailContainer, phoneContainer, false));

        // Style the buttons
        callButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        emailButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
        callButton.setStrokeWidth(2);
        emailButton.setStrokeWidth(2);
        callButton.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        emailButton.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        // Add buttons to action layout
        actionButtons.addView(callButton);
        actionButtons.addView(emailButton);

        // Add spacing between buttons
        LinearLayout.LayoutParams buttonParams = (LinearLayout.LayoutParams) callButton.getLayoutParams();
        buttonParams.setMargins(0, 0, dpToPx(16), 0);
        callButton.setLayoutParams(buttonParams);

        // Assemble all views
        headerLayout.addView(icon);
        headerLayout.addView(deptNameView);

        managerInfoLayout.addView(managerAvatar);
        managerInfoLayout.addView(managerNameView);
        managerInfoLayout.addView(managerRoleView);
        managerInfoLayout.addView(actionButtons);
        managerInfoLayout.addView(phoneContainer);
        managerInfoLayout.addView(emailContainer);

        container.addView(headerLayout);
        container.addView(managerInfoLayout);
        shimmerLayout.addView(container);
        card.addView(shimmerLayout);

        // Enhanced touch feedback
        card.setClickable(true);
        card.setFocusable(true);
        card.setStateListAnimator(createCustomStateListAnimator());

        // Card click interaction
        card.setOnClickListener(v -> {
            float targetElevation = managerInfoLayout.getVisibility() == View.VISIBLE ? dpToPx(2) : dpToPx(8);
            ValueAnimator elevationAnimator = ValueAnimator.ofFloat(card.getCardElevation(), targetElevation);
            elevationAnimator.setDuration(200);
            elevationAnimator.addUpdateListener(animation ->
                    card.setCardElevation((Float) animation.getAnimatedValue()));
            elevationAnimator.start();

            if (managerInfoLayout.getVisibility() == View.VISIBLE) {
                // Hide contact info views when collapsing
                phoneContainer.setVisibility(View.GONE);
                emailContainer.setVisibility(View.GONE);

                // Collapse animation
                managerInfoLayout.animate()
                        .alpha(0f)
                        .scaleY(0.8f)
                        .setDuration(250)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .withEndAction(() -> managerInfoLayout.setVisibility(View.GONE))
                        .start();

                // Reset icon animation
                icon.pauseAnimation();
                icon.setProgress(0f);
            } else {
                // Expand animation
                managerInfoLayout.setVisibility(View.VISIBLE);
                managerInfoLayout.setScaleY(0.8f);
                managerInfoLayout.animate()
                        .alpha(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator(0.8f))
                        .start();

                // Play icon animation
                icon.playAnimation();
            }
        });

        // Initial entry animation
        card.setAlpha(0f);
        card.setScaleX(0.8f);
        card.setScaleY(0.8f);
        card.setTranslationY(dpToPx(50));

        subDepartmentsContainer.addView(card);

        card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(100 * index)
                .setInterpolator(new SpringInterpolator())
                .start();

        // Start shimmer effect
        shimmerLayout.startShimmer();
    }

    private MaterialButton createCopyButton() {
        MaterialButton copyButton = new MaterialButton(this);
        copyButton.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_copy));
        copyButton.setIconTint(null);
        copyButton.setBackgroundTintList(null);
        copyButton.setBackground(null);  // Remove any background
        copyButton.setMinWidth(0);  // Remove minimum width constraint
        copyButton.setMinHeight(0);  // Remove minimum height constraint
        copyButton.setInsetTop(0);   // Remove internal padding
        copyButton.setInsetBottom(0);
        copyButton.setPadding(0, 0, 0, 0);  // Remove all padding
        copyButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        copyButton.setIconSize(dpToPx(20));
        copyButton.setStrokeWidth(0);  // Remove stroke
        return copyButton;
    }

    // Helper method to show copy success toast
    private void showCopySuccessToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void toggleContactInfo(LinearLayout showContainer, LinearLayout hideContainer, boolean isPhone) {
        hideContainer.setVisibility(View.GONE);
        hideContainer.setAlpha(0f);

        if (showContainer.getVisibility() == View.VISIBLE) {
            showContainer.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> showContainer.setVisibility(View.GONE))
                    .start();
        } else {
            showContainer.setVisibility(View.VISIBLE);
            showContainer.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        }
    }

    // Helper method to create action buttons
    private MaterialButton createActionButton(int iconRes, String contentDesc) {
        MaterialButton button = new MaterialButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(48), dpToPx(48));
        params.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        button.setLayoutParams(params);
        button.setIcon(getDrawable(iconRes));
        button.setIconTint(ColorStateList.valueOf(getColor(R.color.primary)));
        button.setIconSize(dpToPx(24));
        button.setContentDescription(contentDesc);
        button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.surface_variant)));
        button.setRippleColor(ColorStateList.valueOf(getColor(R.color.ripple_color)));
        return button;
    }


    // Custom spring interpolator for more natural animation
    private class SpringInterpolator implements Interpolator {
        private final float tension = 7f;

        @Override
        public float getInterpolation(float x) {
            return (float) (1 - Math.exp(-tension * x) * Math.cos(2 * Math.PI * x));
        }
    }

    // Create custom state list animator for enhanced touch feedback
    private StateListAnimator createCustomStateListAnimator() {
        StateListAnimator animator = new StateListAnimator();

        // Pressed state
        animator.addState(new int[]{android.R.attr.state_pressed},
                ObjectAnimator.ofFloat(null, "translationZ", dpToPx(4)));

        // Hover state
        animator.addState(new int[]{android.R.attr.state_hovered},
                ObjectAnimator.ofFloat(null, "translationZ", dpToPx(2)));

        // Default state
        animator.addState(new int[]{},
                ObjectAnimator.ofFloat(null, "translationZ", 0));

        return animator;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}