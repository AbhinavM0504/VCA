package com.vivo.vivorajonboarding.transformer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.vivo.vivorajonboarding.model.Document;

import java.util.List;

public class ProgressUpdateHandler {
    private ProgressBar progressBar;
    private TextView progressText;
    private List<Document> documents;
    private FrameLayout container;
    private ObjectAnimator progressAnimator;

    public ProgressUpdateHandler(FrameLayout container, ProgressBar progressBar,
                                 TextView progressText, List<Document> documents) {
        this.container = container;
        this.progressBar = progressBar;
        this.progressText = progressText;
        this.documents = documents;

        // Initially hide the progress text
        this.progressText.setVisibility(View.GONE);
    }

    public void updateProgress() {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        int uploadedCount = 0;
        for (Document doc : documents) {
            if (doc.isUploaded()) {
                uploadedCount++;
            }
        }

        // Calculate progress percentage
        int progress = (uploadedCount * 100) / documents.size();

        // Update progress bar
        progressBar.setProgress(progress);

        // Update text visibility and position only if there are uploaded documents
        if (uploadedCount > 0) {
            progressText.setVisibility(View.VISIBLE);

            // Update text
            progressText.setText(uploadedCount + "/" + documents.size());

            // Calculate TextView position based on progress
            float progressWidth = (progressBar.getWidth() * progress) / 100f;
            float xPosition = progressBar.getLeft() + progressWidth - (progressText.getWidth() / 2f);

            // Ensure TextView stays within bounds
            float minX = progressBar.getLeft();
            float maxX = progressBar.getRight() - progressText.getWidth();
            xPosition = Math.max(minX, Math.min(xPosition, maxX));

            // Update TextView position with animation
            progressText.animate()
                    .x(xPosition)
                    .setDuration(200)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .start();
        } else {
            progressText.setVisibility(View.GONE);
        }
    }

    public void animateToCompletion(long duration, Runnable onComplete) {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        // Ensure text is visible
        progressText.setVisibility(View.VISIBLE);

        // Animate progress bar with bounce effect
        progressAnimator = ObjectAnimator.ofInt(
                progressBar,
                "progress",
                progressBar.getProgress(),
                105,  // Overshoot slightly
                100   // Settle at 100
        );
        progressAnimator.setDuration(duration);
        progressAnimator.setInterpolator(new BounceInterpolator());

        if (onComplete != null) {
            progressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onComplete.run();
                }
            });
        }

        progressAnimator.start();

        // Animate text with smooth movement
        float finalPosition = progressBar.getRight() - progressText.getWidth();
        progressText.animate()
                .x(finalPosition)
                .setDuration(duration)
                .setInterpolator(new FastOutSlowInInterpolator())
                .start();
    }

    public void initializeTextPosition() {
        progressText.post(() -> {
            // Center the TextView vertically
            float yPosition = progressBar.getY() + (progressBar.getHeight() - progressText.getHeight()) / 2f;
            progressText.setY(yPosition);

            // Initially hide the text
            progressText.setVisibility(View.GONE);

            // Set initial X position based on current progress
            updateProgress();
        });
    }
}