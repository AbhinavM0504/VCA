package com.vivo.vivorajonboarding.transformer;

import android.view.View;
import android.view.ViewGroup;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

public class CardPageTransformer implements ViewPager2.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left
            page.setAlpha(0f);
            page.setTranslationX(0f);
            page.setTranslationY(0f);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;

            // Center vertically
            page.setTranslationY(position < 0
                    ? vertMargin - horzMargin / 2
                    : -vertMargin + horzMargin / 2);

            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            // Add rotation effect
            float rotation = position * -30f; // Rotate up to 30 degrees
            page.setRotationY(rotation);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right
            page.setAlpha(0f);
            page.setTranslationX(0f);
            page.setTranslationY(0f);
        }
    }
}
