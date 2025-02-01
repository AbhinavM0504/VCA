package com.vivo.vivorajonboarding;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FeedItemSpacing extends RecyclerView.ItemDecoration {
    private final int spacing;

    public FeedItemSpacing(Context context) {
        // Convert 16dp to pixels
        this.spacing = (int) (10 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        // Add top spacing for all items except the first one
        if (position != 0) {
            outRect.top = spacing;
        }

        // Add bottom spacing for all items
        outRect.bottom = spacing;
        outRect.left = spacing;
        outRect.right = spacing;
    }
}