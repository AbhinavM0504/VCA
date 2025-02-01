package com.vivo.vivorajonboarding.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.FormCard;
import com.vivo.vivorajonboarding.model.FormField;

public class FormCardFragment extends Fragment {
    private static final String ARG_CARD = "card";
    private static final String ARG_POSITION = "position";
    private FormCard card;
    private static final int FIRST_CARD_POSITION = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            card = (FormCard) getArguments().getSerializable(ARG_CARD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_card, container, false);
        LinearLayout formContainer = view.findViewById(R.id.formContainer);

        if (card != null) {
            // Get the card position from arguments
            int position = getArguments().getInt(ARG_POSITION, -1);

            // Dynamically add form fields
            for (FormField field : card.getFields()) {
                // Check if this is the first card and has custom view
                if (position == FIRST_CARD_POSITION && field.getViewCreator() != null) {
                    View customView = field.createCustomView(requireContext());
                    if (customView != null) {
                        formContainer.addView(customView);
                        continue; // Skip the default view creation
                    }
                }

                // Default view creation for other fields
                View fieldView = inflater.inflate(R.layout.item_form_field, formContainer, false);

                TextInputEditText editText = fieldView.findViewById(R.id.fieldInput);

                editText.setHint(field.getHint());
                editText.setInputType(field.getInputType());

                formContainer.addView(fieldView);
            }
        }

        return view;
    }

    public static FormCardFragment newInstance(FormCard card, int position) {
        FormCardFragment fragment = new FormCardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }
}