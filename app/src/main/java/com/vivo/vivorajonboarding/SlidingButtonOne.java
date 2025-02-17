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

public class SlidingButtonOne extends FrameLayout {
    private View slideButton;
    private TextView slideText;
    private float initialX;
    private float touchDownX;
    private boolean isSlideComplete = false;
    private OnSlideCompleteListener listener;
    private ValueAnimator resetAnimator;
    private View backgroundView;
    private int baseColor,textColor;

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

    public SlidingButtonOne(Context context) {
        super(context);
        init(context);
    }

    public SlidingButtonOne(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sliding_button_layout_green, this, true);
        slideButton = findViewById(R.id.slideButton);
        slideText = findViewById(R.id.slideText);
        backgroundView = findViewById(R.id.backgroundView);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        baseColor = ContextCompat.getColor(context, R.color.relation_bg);
        textColor=ContextCompat.getColor(context,R.color.selected_relation_text);

        // Set initial background color
        resetBackgroundColor();

        setupTouchListener();
    }

    // New method to reset background color to original state
    private void resetBackgroundColor() {
        try {
            backgroundView.setBackgroundTintList(ColorStateList.valueOf(baseColor));
            slideText.setTextColor(ColorUtils.setAlphaComponent(textColor, 255));
            slideText.setAlpha(1.0f);
            slideButton.setScaleX(1.0f);
            slideButton.setScaleY(1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // Ensure progress is within valid bounds
        float boundedProgress = Math.max(0f, Math.min(1f, progress));

        // Apply exponential easing with validation
        float smoothProgress = (float) Math.pow(boundedProgress, 1.3);

        // Ensure smoothProgress is within bounds after pow operation
        smoothProgress = Math.max(0f, Math.min(1f, smoothProgress));

        // Calculate alpha with bounds checking
        int alpha = (int) (smoothProgress * 255);
        alpha = Math.max(0, Math.min(255, alpha));

        int color = ColorUtils.setAlphaComponent(baseColor, alpha);

        // Apply background color
        backgroundView.setBackgroundTintList(ColorStateList.valueOf(color));

        // Update text alpha with validation
        float textAlpha = Math.max(0f, Math.min(1f, 1 - smoothProgress));
        slideText.setAlpha(textAlpha);

        // Calculate scale with validation to prevent NaN
        float maxScale = 1.1f;
        float scale = 1f + (Math.max(0f, Math.min(0.1f, 0.1f * smoothProgress)));

        // Validate scale before applying
        if (!Float.isNaN(scale) && scale >= 1f && scale <= maxScale) {
            slideButton.setScaleX(scale);
            slideButton.setScaleY(scale);
        }
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
        // Cancel any existing animation
        if (resetAnimator != null && resetAnimator.isRunning()) {
            resetAnimator.cancel();
        }

        // Get current translation with validation
        float currentTranslation = slideButton.getTranslationX();
        if (Float.isNaN(currentTranslation)) {
            currentTranslation = 0f;
        }

        // Create new reset animator
        resetAnimator = ValueAnimator.ofFloat(currentTranslation, 0f);
        resetAnimator.setDuration(SLIDE_DURATION);
        resetAnimator.setInterpolator(new DecelerateInterpolator());

        float finalWidth = getWidth() - slideButton.getWidth();
        // Ensure we don't divide by zero
        final float maxWidth = finalWidth <= 0 ? 1f : finalWidth;

        resetAnimator.addUpdateListener(animation -> {
            try {
                float value = (float) animation.getAnimatedValue();
                if (!Float.isNaN(value)) {
                    slideButton.setTranslationX(value);
                    updateBackground(value / maxWidth);
                }
            } catch (Exception e) {
                // Log the error but don't crash
                e.printStackTrace();
            }
        });

        resetAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    // Reset everything to initial state
                    slideButton.setTranslationX(0f);
                    slideButton.setScaleX(1f);
                    slideButton.setScaleY(1f);
                    resetBackgroundColor();
                    isSlideComplete = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Start the animation
        try {
            resetAnimator.start();
        } catch (Exception e) {
            // If animation fails, reset to initial state immediately
            slideButton.setTranslationX(0f);
            slideButton.setScaleX(1f);
            slideButton.setScaleY(1f);
            resetBackgroundColor();
            isSlideComplete = false;
        }
    }

    public void setOnSlideCompleteListener(OnSlideCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (resetAnimator != null) {
            resetAnimator.cancel();
            resetAnimator = null;
        }
    }
}