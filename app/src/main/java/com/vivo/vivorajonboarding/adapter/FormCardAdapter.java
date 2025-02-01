package com.vivo.vivorajonboarding.adapter;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.FormCard;
import com.vivo.vivorajonboarding.model.FormField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FormCardAdapter extends RecyclerView.Adapter<FormCardAdapter.FormCardViewHolder> {
    private final List<FormCard> cards;
    private final Map<Integer, View> cardViews;
    private final Context context;

    public FormCardAdapter(Context context, List<FormCard> cards) {
        this.context = context;
        this.cards = cards;
        this.cardViews = new HashMap<>();
    }



    @NonNull
    @Override
    public FormCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new FormCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormCardViewHolder holder, int position) {
        FormCard card = cards.get(position);
        holder.bind(card);
        cardViews.put(position, holder.itemView);

        // Request layout measurement after binding
        holder.itemView.requestLayout();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public View getCardView(int position) {
        return cardViews.get(position);
    }

    class FormCardViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout formContainer;

        FormCardViewHolder(@NonNull View itemView) {
            super(itemView);
            formContainer = itemView.findViewById(R.id.formContainer);
        }

        void bind(FormCard card) {
            formContainer.removeAllViews();

            for (FormField field : card.getFields()) {
                View fieldView = field.createView(context);
                if (fieldView != null) {
                    formContainer.addView(fieldView);

                    // Add a global layout listener to handle dynamic content
                    fieldView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            fieldView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            // Notify parent ViewPager2 to adjust its height
                            ViewGroup.LayoutParams params = fieldView.getLayoutParams();
                            if (params != null) {
                                notifyHeightChanged();
                            }
                        }
                    });
                }
            }
        }

        private void notifyHeightChanged() {
            // Find the parent ViewPager2
            itemView.post(() -> {
                // Find the parent ViewPager2
                View current = itemView;
                while (current != null && !(current instanceof ViewPager2)) {
                    current = (View) current.getParent();
                }

                if (current instanceof ViewPager2) {
                    ViewPager2 viewPager = (ViewPager2) current;
                    adjustViewPagerHeight(viewPager);
                }
            });
        }
    }

    public void adjustViewPagerHeight(ViewPager2 viewPager) {
        if (viewPager == null) return;

        // Tag format consistent with your implementation
        View currentView = viewPager.findViewWithTag("page:" + viewPager.getCurrentItem());

        if (currentView != null) {
            currentView.post(() -> {
                try {
                    // Measure the current view
                    int width = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    currentView.measure(width, height);

                    // Get minimum height from resources
                    int minHeight = viewPager.getContext().getResources()
                            .getDimensionPixelSize(R.dimen.min_card_height);

                    // Calculate new height
                    int newHeight = Math.max(minHeight, currentView.getMeasuredHeight());

                    // Update ViewPager height if changed
                    ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                    if (params != null && params.height != newHeight) {
                        params.height = newHeight;
                        viewPager.setLayoutParams(params);
                        Log.d(TAG, "adjustViewPagerHeight: ViewPager height adjusted to: $newHeight");
                    }
                } catch (Exception e) {
                    Timber.tag(TAG).e(e, "Error adjusting ViewPager height");
                }
            });
        } else {
            Timber.tag(TAG).w("Current view not found for position: %d", viewPager.getCurrentItem());
        }
    }


    // Helper method to validate a specific card
    public boolean validateCard(int position) {
        View cardView = getCardView(position);
        if (cardView == null) return false;

        LinearLayout formContainer = cardView.findViewById(R.id.formContainer);
        if (formContainer == null) return false;

        boolean isValid = true;
        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View field = formContainer.getChildAt(i);
            if (field instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) field;
                TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

                if (editText != null && editText.getText().toString().trim().isEmpty()) {
                    textInputLayout.setError(context.getString(R.string.field_required));
                    isValid = false;
                } else {
                    textInputLayout.setError(null);
                }
            }
        }
        return isValid;
    }

    // Helper method to collect data from a specific card
    public Map<String, String> getCardData(int position) {
        Map<String, String> data = new HashMap<>();
        View cardView = getCardView(position);
        if (cardView == null) return data;

        LinearLayout formContainer = cardView.findViewById(R.id.formContainer);
        if (formContainer == null) return data;

        for (int i = 0; i < formContainer.getChildCount(); i++) {
            View field = formContainer.getChildAt(i);
            if (field instanceof TextInputLayout) {
                TextInputLayout textInputLayout = (TextInputLayout) field;
                TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();

                if (editText != null) {
                    String fieldId = editText.getResources().getResourceEntryName(editText.getId());
                    String value = editText.getText().toString().trim();
                    data.put(fieldId, value);
                }
            } else if (field instanceof LinearLayout) {
                // Handle spinner containers
                Spinner spinner = findSpinnerInContainer((LinearLayout) field);
                if (spinner != null) {
                    String fieldId = spinner.getResources().getResourceEntryName(spinner.getId());
                    String value = spinner.getSelectedItem().toString();
                    data.put(fieldId, value);
                }
            }
        }
        return data;
    }

    private Spinner findSpinnerInContainer(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof Spinner) {
                return (Spinner) child;
            }
        }
        return null;
    }
}