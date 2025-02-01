package com.vivo.vivorajonboarding.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.PersonalDataModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BasicInfoFragment extends Fragment implements StepperFragment {
    // UI Components
    private Spinner initialSpinner, genderSpinner, bloodGroupSpinner, maritalStatusSpinner;
    private TextInputEditText nameEt, emailEt, contactNoEt, fatherNameEt, motherNameEt;
    private TextInputEditText joiningDateEt, dobEt, aadharNoEt, panNoEt, pinCodeEt;
    private TextInputEditText permanentAddressEt, currentAddressEt;
    private CheckBox addressCheckBox;

    // Spinner Data and Adapters
    private ArrayList<String> initialList, genderList, bloodGroupList, maritalStatusList;
    private ArrayAdapter<String> initialAdapter, genderAdapter, bloodGroupAdapter, maritalStatusAdapter;
    private String initialValue = "", genderValue = "", bloodGroupValue = "", maritalStatusValue = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stepper_basic_info, container, false);
        initializeViews(view);
        setupSpinners();
        setupDatePickers();
        setupAddressCheckbox();
        return view;
    }

    private void initializeViews(View view) {
        // Initialize Spinners
        initialSpinner = view.findViewById(R.id.initialSpinner);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        bloodGroupSpinner = view.findViewById(R.id.bloodGroupSpinner);
        maritalStatusSpinner = view.findViewById(R.id.maritalStatusSpinner);

        // Initialize EditTexts
        nameEt = view.findViewById(R.id.nameEt);
        emailEt = view.findViewById(R.id.emailEt);
        contactNoEt = view.findViewById(R.id.contactNoEt);
        fatherNameEt = view.findViewById(R.id.fatherNameEt);
        motherNameEt = view.findViewById(R.id.motherNameEt);
        joiningDateEt = view.findViewById(R.id.joiningDateEt);
        dobEt = view.findViewById(R.id.dobEt);
        aadharNoEt = view.findViewById(R.id.aadharNoEt);
        panNoEt = view.findViewById(R.id.panNoEt);
        pinCodeEt = view.findViewById(R.id.pinCodeEt);
        permanentAddressEt = view.findViewById(R.id.permanentAddressEt);
        currentAddressEt = view.findViewById(R.id.currentAddressEt);

        // Initialize CheckBox
        addressCheckBox = view.findViewById(R.id.addressCheckBox);
    }

    private void setupSpinners() {
        setupInitialSpinner();
        setupGenderSpinner();
        setupBloodGroupSpinner();
        setupMaritalSpinner();
    }

    private void setupInitialSpinner() {
        initialList = new ArrayList<>();
        initialList.add("Prefix");
        initialList.add("Mr.");
        initialList.add("Ms.");
        initialList.add("Mrs.");
        initialAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, initialList);
        initialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initialSpinner.setAdapter(initialAdapter);
        initialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialValue = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupGenderSpinner() {
        genderList = new ArrayList<>();
        genderList.add("Gender");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");
        genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderValue = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupBloodGroupSpinner() {
        bloodGroupList = new ArrayList<>();
        bloodGroupList.add("Blood Group");
        bloodGroupList.add("A+");
        bloodGroupList.add("A-");
        bloodGroupList.add("B+");
        bloodGroupList.add("B-");
        bloodGroupList.add("AB+");
        bloodGroupList.add("AB-");
        bloodGroupList.add("O+");
        bloodGroupList.add("O-");
        bloodGroupAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, bloodGroupList);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);
        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroupValue = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupMaritalSpinner() {
        maritalStatusList = new ArrayList<>();
        maritalStatusList.add("Marital Status");
        maritalStatusList.add("Married");
        maritalStatusList.add("Unmarried");
        maritalStatusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, maritalStatusList);
        maritalStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maritalStatusSpinner.setAdapter(maritalStatusAdapter);
        maritalStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maritalStatusValue = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener joiningDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                joiningDateEt.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        };

        DatePickerDialog.OnDateSetListener dobListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dobEt.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        };

        joiningDateEt.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    joiningDateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        dobEt.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    dobListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void setupAddressCheckbox() {
        addressCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentAddressEt.setText(permanentAddressEt.getText().toString());
                currentAddressEt.setEnabled(false);
            } else {
                currentAddressEt.setEnabled(true);
            }
        });
    }

    @Override
    public boolean isStepValid() {
        if (initialValue.equals("Prefix")) {
            showToast("Please select prefix");
            return false;
        }
        if (nameEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter name");
            return false;
        }
        if (emailEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter email");
            return false;
        }
        if (contactNoEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter contact number");
            return false;
        }
        if (fatherNameEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter father's name");
            return false;
        }
        if (motherNameEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter mother's name");
            return false;
        }
        if (joiningDateEt.getText().toString().trim().isEmpty()) {
            showToast("Please select joining date");
            return false;
        }
        if (dobEt.getText().toString().trim().isEmpty()) {
            showToast("Please select date of birth");
            return false;
        }
        if (genderValue.equals("Gender")) {
            showToast("Please select gender");
            return false;
        }
        if (bloodGroupValue.equals("Blood Group")) {
            showToast("Please select blood group");
            return false;
        }
        if (aadharNoEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter Aadhar number");
            return false;
        }
        if (panNoEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter PAN number");
            return false;
        }
        if (maritalStatusValue.equals("Marital Status")) {
            showToast("Please select marital status");
            return false;
        }
        if (pinCodeEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter PIN code");
            return false;
        }
        if (permanentAddressEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter permanent address");
            return false;
        }
        if (currentAddressEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter current address");
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> getStepData() {
        Map<String, String> data = new HashMap<>();
        data.put("initial", initialValue);
        data.put("name", nameEt.getText().toString().trim());
        data.put("email", emailEt.getText().toString().trim());
        data.put("contactNo", contactNoEt.getText().toString().trim());
        data.put("fatherName", fatherNameEt.getText().toString().trim());
        data.put("motherName", motherNameEt.getText().toString().trim());
        data.put("joiningDate", joiningDateEt.getText().toString().trim());
        data.put("dob", dobEt.getText().toString().trim());
        data.put("gender", genderValue);
        data.put("bloodGroup", bloodGroupValue);
        data.put("aadharNo", aadharNoEt.getText().toString().trim());
        data.put("panNo", panNoEt.getText().toString().trim());
        data.put("maritalStatus", maritalStatusValue);
        data.put("pinCode", pinCodeEt.getText().toString().trim());
        data.put("permanentAddress", permanentAddressEt.getText().toString().trim());
        data.put("currentAddress", currentAddressEt.getText().toString().trim());
        return data;
    }

    @Override
    public void setData(PersonalDataModel data) {
        if (data != null) {
            initialSpinner.setSelection(initialList.indexOf(data.getInitial()));
            nameEt.setText(data.getName());
            emailEt.setText(data.getEmail());
            contactNoEt.setText(data.getNumber());
            fatherNameEt.setText(data. getFather_name());
            motherNameEt.setText(data.getMother_name());
            joiningDateEt.setText(data.getJoining_date());
            dobEt.setText(data.getDob());
            genderSpinner.setSelection(genderList.indexOf(data.getGender()));
            bloodGroupSpinner.setSelection(bloodGroupList.indexOf(data.getBlood_group()));
            aadharNoEt.setText(data.getAadhar_no());
            panNoEt.setText(data.getPan_no());
            maritalStatusSpinner.setSelection(maritalStatusList.indexOf(data.getMarital_status()));
            pinCodeEt.setText(data.getPin_code());
            permanentAddressEt.setText(data.getPermanent_address());
            currentAddressEt.setText(data.getCurrent_address());
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}