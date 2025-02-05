package com.vivo.vivorajonboarding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager2.widget.ViewPager2;

public class SlidingButton extends FrameLayout {
    private View slideButton;
    private TextView slideText;
    private float initialX;
    private float touchDownX;
    private boolean isSlideComplete = false;
    private OnSlideCompleteListener listener;
    private ValueAnimator resetAnimator;
    private View backgroundView;
    private int baseColor;

    // Enhanced animation constants
    private static final int SLIDE_DURATION = 300; // Increased for smoother feel
    private final int CLICK_THRESHOLD = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private static final float SLIDE_THRESHOLD = 0.7f; // Reduced threshold for easier sliding

    // Touch handling
    private boolean isDragging = false;
    private float lastTouchX;
    private int touchSlop;
    private boolean isParentViewPager = false;

    public interface OnSlideCompleteListener {
        void onSlideComplete();
    }

    public SlidingButton(Context context) {
        super(context);
        init(context);
    }

    public SlidingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sliding_button_layout, this, true);
        slideButton = findViewById(R.id.slideButton);
        slideText = findViewById(R.id.slideText);
        backgroundView = findViewById(R.id.backgroundView);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        baseColor = ContextCompat.getColor(context, R.color.blue_shade);

        // Set initial background color
        resetBackgroundColor();

        setupTouchListener();
    }

    // New method to reset background color to original state
    private void resetBackgroundColor() {
        backgroundView.setBackgroundTintList(ColorStateList.valueOf(baseColor));
        slideText.setAlpha(1.0f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Check if parent is ViewPager2
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof ViewPager2) {
                isParentViewPager = true;
                break;
            }
            parent = parent.getParent();
        }
    }

    private void updateBackground(float progress) {
        // Smooth out the progress
        float smoothProgress = Math.max(0, Math.min(1, progress));

        // Apply exponential easing for more natural feel
        smoothProgress = (float) Math.pow(smoothProgress, 1.3);

        int alpha = (int) (smoothProgress * 255);
        int color = ColorUtils.setAlphaComponent(baseColor, alpha);

        backgroundView.setBackgroundTintList(ColorStateList.valueOf(color));
        slideText.setAlpha(1 - smoothProgress);

        // Add subtle scale animation to the slide button
        float scale = 1 + (0.1f * smoothProgress);
        slideButton.setScaleX(scale);
        slideButton.setScaleY(scale);
    }

    private void setupTouchListener() {
        slideButton.setOnTouchListener((v, event) -> {
            ViewParent parent = getParent();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getRawX();
                    touchDownX = event.getRawX();
                    lastTouchX = event.getRawX();
                    isDragging = false;

                    // Cancel any running animations
                    if (resetAnimator != null && resetAnimator.isRunning()) {
                        resetAnimator.cancel();
                    }

                    // Request parent to not intercept touch events
                    if (isParentViewPager) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - lastTouchX;
                    float totalDx = event.getRawX() - touchDownX;

                    // Determine if we should start dragging
                    if (!isDragging && Math.abs(totalDx) > touchSlop) {
                        isDragging = true;
                    }

                    if (isDragging) {
                        float newTranslationX = slideButton.getTranslationX() + dx;
                        float maxSlide = getWidth() - slideButton.getWidth();

                        // Constrain movement
                        newTranslationX = Math.max(0, Math.min(maxSlide, newTranslationX));

                        slideButton.setTranslationX(newTranslationX);
                        updateBackground(newTranslationX / maxSlide);

                        // Keep blocking parent interception while actively sliding
                        if (isParentViewPager) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    } else if (isParentViewPager && Math.abs(dx) < Math.abs(event.getRawY() - touchDownX)) {
                        // If moving more vertically, let parent handle it
                        parent.requestDisallowInterceptTouchEvent(false);
                    }

                    lastTouchX = event.getRawX();
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isParentViewPager) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }

                    float finalPosition = slideButton.getTranslationX();
                    float threshold = getWidth() * SLIDE_THRESHOLD;

                    // Only count as click if we haven't started dragging
                    if (!isDragging) {
                        v.performClick();
                    } else if (finalPosition > threshold) {
                        completeSlide();
                    } else {
                        resetSlide();
                    }

                    isDragging = false;
                    return true;
            }
            return false;
        });

        slideButton.setOnClickListener(v -> {
            if (!isSlideComplete) {
                pulseAnimation();
            }
        });
    }

    private void pulseAnimation() {
        slideButton.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(100)
                .withEndAction(() ->
                        slideButton.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start()
                ).start();
    }

    private void completeSlide() {
        isSlideComplete = true;
        float finalX = getWidth() - slideButton.getWidth();

        ValueAnimator animator = ValueAnimator.ofFloat(slideButton.getTranslationX(), finalX);
        animator.setDuration(SLIDE_DURATION);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            slideButton.setTranslationX(value);
            updateBackground(value / finalX);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onSlideComplete();
                }
            }
        });

        animator.start();
    }

    public void resetSlide() {
        resetAnimator = ValueAnimator.ofFloat(slideButton.getTranslationX(), 0);
        resetAnimator.setDuration(SLIDE_DURATION);
        resetAnimator.setInterpolator(new DecelerateInterpolator());

        resetAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            slideButton.setTranslationX(value);
            updateBackground(value / (getWidth() - slideButton.getWidth()));
        });

        resetAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset background color to original state when slide is not completed
                resetBackgroundColor();
            }
        });

        resetAnimator.start();
    }

    public void setOnSlideCompleteListener(OnSlideCompleteListener listener) {
        this.listener = listener;
    }
}