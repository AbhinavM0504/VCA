package com.vivo.vivorajonboarding.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.PersonalDataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BankDetailsFragment extends Fragment implements StepperFragment {
    // UI Components
    private TextInputEditText bankAccNoEt, ifscCodeEt, uanNoEt, esicNoEt;
    private TextInputLayout bankAccNoLayout, ifscCodeLayout, uanNoLayout, esicNoLayout;


    // Validation patterns
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{9,18}$");
    private static final Pattern IFSC_CODE_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern UAN_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern ESIC_PATTERN = Pattern.compile("^\\d{10}$");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stepper_bank_details, container, false);
        initializeViews(view);
        setupInputValidation();
        setupBankLookup();
        return view;
    }

    private void initializeViews(View view) {
        // Initialize EditTexts and their layouts
        bankAccNoEt = view.findViewById(R.id.bankAccNoEt);
        ifscCodeEt = view.findViewById(R.id.ifscCodeEt);
        uanNoEt = view.findViewById(R.id.uanNoEt);
        esicNoEt = view.findViewById(R.id.esicNoEt);

        // Initialize TextInputLayouts
        bankAccNoLayout = (TextInputLayout) bankAccNoEt.getParent().getParent();
        ifscCodeLayout = (TextInputLayout) ifscCodeEt.getParent().getParent();
        uanNoLayout = (TextInputLayout) uanNoEt.getParent().getParent();
        esicNoLayout = (TextInputLayout) esicNoEt.getParent().getParent();

        // Initialize bank details preview

    }

    private void setupInputValidation() {
        // Account number validation
        bankAccNoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateAccountNumber(s.toString());
            }
        });

        // IFSC code validation
        ifscCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateIFSCCode(s.toString());
            }
        });

        // UAN validation (optional)
        uanNoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    validateUANNumber(s.toString());
                } else {
                    uanNoLayout.setError(null);
                }
            }
        });

        // ESIC validation (optional)
        esicNoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    validateESICNumber(s.toString());
                } else {
                    esicNoLayout.setError(null);
                }
            }
        });
    }

    private void setupBankLookup() {
        ifscCodeLayout.setEndIconOnClickListener(v -> {
            String ifscCode = ifscCodeEt.getText().toString().trim();
            if (validateIFSCCode(ifscCode)) {
                performBankLookup(ifscCode);
            }
        });
    }

    private void performBankLookup(String ifscCode) {
        // TODO: Implement actual bank lookup API call
        // For demonstration, showing dummy data

    }

    private boolean validateAccountNumber(String accountNumber) {
        if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            bankAccNoLayout.setError("Please enter a valid account number (9-18 digits)");
            return false;
        }
        bankAccNoLayout.setError(null);
        return true;
    }

    private boolean validateIFSCCode(String ifscCode) {
        if (!IFSC_CODE_PATTERN.matcher(ifscCode).matches()) {
            ifscCodeLayout.setError("Please enter a valid IFSC code");
            return false;
        }
        ifscCodeLayout.setError(null);
        return true;
    }

    private boolean validateUANNumber(String uanNumber) {
        if (!uanNumber.isEmpty() && !UAN_PATTERN.matcher(uanNumber).matches()) {
            uanNoLayout.setError("Please enter a valid 12-digit UAN number");
            return false;
        }
        uanNoLayout.setError(null);
        return true;
    }

    private boolean validateESICNumber(String esicNumber) {
        if (!esicNumber.isEmpty() && !ESIC_PATTERN.matcher(esicNumber).matches()) {
            esicNoLayout.setError("Please enter a valid 10-digit ESIC number");
            return false;
        }
        esicNoLayout.setError(null);
        return true;
    }

    @Override
    public boolean isStepValid() {
        String accountNumber = bankAccNoEt.getText().toString().trim();
        String ifscCode = ifscCodeEt.getText().toString().trim();
        String uanNumber = uanNoEt.getText().toString().trim();
        String esicNumber = esicNoEt.getText().toString().trim();

        boolean isValid = validateAccountNumber(accountNumber) && validateIFSCCode(ifscCode);

        if (!uanNumber.isEmpty() && !validateUANNumber(uanNumber)) {
            isValid = false;
        }

        if (!esicNumber.isEmpty() && !validateESICNumber(esicNumber)) {
            isValid = false;
        }

        if (!isValid) {
            showToast("Please correct the errors in the form");
        }

        return isValid;
    }

    @Override
    public Map<String, String> getStepData() {
        Map<String, String> data = new HashMap<>();
        data.put("bankAccountNo", bankAccNoEt.getText().toString().trim());
        data.put("ifscCode", ifscCodeEt.getText().toString().trim());
        data.put("uanNo", uanNoEt.getText().toString().trim());
        data.put("esicNo", esicNoEt.getText().toString().trim());
        return data;
    }

    @Override
    public void setData(PersonalDataModel data) {
        if (data != null) {
            bankAccNoEt.setText(data.getBank_account_no());
            ifscCodeEt.setText(data.getBank_ifsc());
            uanNoEt.setText(data.getUan_no());
            esicNoEt.setText(data.getEsic_no());

            // Trigger bank lookup if IFSC code is present
            if (!data.getBank_ifsc().isEmpty()) {
                performBankLookup(data.getBank_ifsc());
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}