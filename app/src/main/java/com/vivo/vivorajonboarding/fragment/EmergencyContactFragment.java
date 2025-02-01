package com.vivo.vivorajonboarding.fragment;

import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.PersonalDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencyContactFragment extends Fragment implements StepperFragment {
    // UI Components
    private TextInputEditText emergencyNameEt, emergencyNoEt;
    private Spinner emergencyRelationSpinner;

    // Spinner Data
    private ArrayList<String> relationList;
    private ArrayAdapter<String> relationAdapter;
    private String selectedRelation = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stepper_emergency_contact, container, false);
        initializeViews(view);
        setupSpinner();
        return view;
    }

    private void initializeViews(View view) {
        // Initialize EditTexts
        emergencyNameEt = view.findViewById(R.id.emergencyNameEt);
        emergencyNoEt = view.findViewById(R.id.emergencyNoEt);
        emergencyRelationSpinner = view.findViewById(R.id.emergencyRelationSpinner);
    }

    private void setupSpinner() {
        relationList = new ArrayList<>();
        relationList.add("Select Relation");
        relationList.add("Father");
        relationList.add("Mother");
        relationList.add("Brother");
        relationList.add("Sister");
        relationList.add("Spouse");
        relationList.add("Son");
        relationList.add("Daughter");
        relationList.add("Friend");
        relationList.add("Other");

        relationAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, relationList);
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergencyRelationSpinner.setAdapter(relationAdapter);

        emergencyRelationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRelation = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean isStepValid() {
        if (emergencyNameEt.getText().toString().trim().isEmpty()) {
            showToast("Please enter emergency contact name");
            return false;
        }

        String contactNo = emergencyNoEt.getText().toString().trim();
        if (contactNo.isEmpty()) {
            showToast("Please enter emergency contact number");
            return false;
        }
        if (contactNo.length() != 10) {
            showToast("Please enter valid 10-digit contact number");
            return false;
        }

        if (selectedRelation.equals("Select Relation")) {
            showToast("Please select relation with emergency contact");
            return false;
        }

        return true;
    }

    @Override
    public Map<String, String> getStepData() {
        Map<String, String> data = new HashMap<>();
        data.put("emergencyContactName", emergencyNameEt.getText().toString().trim());
        data.put("emergencyContactNo", emergencyNoEt.getText().toString().trim());
        data.put("emergencyContactRelation", selectedRelation);
        return data;
    }

    @Override
    public void setData(PersonalDataModel data) {
        if (data != null) {
            emergencyNameEt.setText(data.getPerson_name());
            emergencyNoEt.setText(data.getPerson_number());

            // Set spinner selection
            String relation = data.getPerson_relation();
            if (relation != null && !relation.isEmpty()) {
                int relationPosition = relationList.indexOf(relation);
                if (relationPosition >= 0) {
                    emergencyRelationSpinner.setSelection(relationPosition);
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}