package com.vivo.vivorajonboarding.model;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;

public class FormField implements Serializable {
    private String label;
    private String hint;
    private int inputType;
    private transient ViewCreator viewCreator;  // transient since View objects aren't serializable

    // Constructor for standard fields
    public FormField(String label, String hint, int inputType) {
        this.label = label;
        this.hint = hint;
        this.inputType = inputType;
    }

    // Constructor for custom view fields
    public FormField(String label, String hint, int inputType, ViewCreator viewCreator) {
        this.label = label;
        this.hint = hint;
        this.inputType = inputType;
        this.viewCreator = viewCreator;
    }

    // Getters and setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    public ViewCreator getViewCreator() {
        return viewCreator;
    }

    public void setViewCreator(ViewCreator viewCreator) {
        this.viewCreator = viewCreator;
    }

    // Method to create custom view
    public View createCustomView(Context context) {
        return viewCreator != null ? viewCreator.createView(context) : null;
    }

    public View createView(Context context) {
        // Check if a custom view creator is provided
        if (viewCreator != null) {
            return viewCreator.createView(context);
        }

        // Default view creation logic
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(16, 16, 16, 16);

        switch (inputType) {
            case InputType.TYPE_CLASS_TEXT:
            case InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
            case InputType.TYPE_TEXT_VARIATION_PASSWORD:
                TextInputLayout textInputLayout = new TextInputLayout(context);
                textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textInputLayout.setHint(hint);

                TextInputEditText editText = new TextInputEditText(context);
                editText.setHint(hint);
                editText.setInputType(inputType);
                editText.setId(View.generateViewId());
                textInputLayout.addView(editText);
                container.addView(textInputLayout);
                return container;

            case InputType.TYPE_CLASS_DATETIME:
                TextInputLayout dateInputLayout = new TextInputLayout(context);
                dateInputLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                dateInputLayout.setHint(hint);

                TextInputEditText dateEditText = new TextInputEditText(context);
                dateEditText.setHint(hint);
                dateEditText.setInputType(inputType);
                dateEditText.setId(View.generateViewId());
                dateEditText.setFocusable(false);
                dateEditText.setClickable(true);
                dateInputLayout.addView(dateEditText);
                container.addView(dateInputLayout);
                return container;

            case InputType.TYPE_NULL: // For spinners
                LinearLayout spinnerContainer = new LinearLayout(context);
                spinnerContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                spinnerContainer.setOrientation(LinearLayout.HORIZONTAL);

                Spinner spinner = new Spinner(context);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                spinner.setId(View.generateViewId());
                spinnerContainer.addView(spinner);
                container.addView(spinnerContainer);
                return container;

            default:
                return null;
        }
    }


    // Interface for custom view creation
    @FunctionalInterface
    public interface ViewCreator {
        View createView(Context context);
    }
}