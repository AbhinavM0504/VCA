package com.vivo.vivorajonboarding.fragment;

import androidx.fragment.app.Fragment;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivorajonboarding.R;
import com.vivo.vivorajonboarding.model.PersonalDataModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CompanyDetailsFragment extends Fragment implements StepperFragment {
    // UI Components for Previous Work Experience
    private RadioGroup companyDetailsRg;
    private RadioButton yesCDRdbtn, noCDRdbtn;
    private LinearLayout companyDetailsLayout;
    private TextInputEditText departmentEt, designationEt, dojEt, dolEt;

    // UI Components for Company Relations
    private RadioGroup companyRelationRg;
    private RadioButton yesRelationRdbtn, noRelationRdbtn;
    private LinearLayout companyRelationLayout;
    private TextInputEditText relationNameEt, relationEt, relationDesignationEt;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stepper_company_details, container, false);
        initializeViews(view);
        setupListeners();
        setupDatePickers();
        return view;
    }

    private void initializeViews(View view) {
        // Previous Work Experience Views
        companyDetailsRg = view.findViewById(R.id.companyDetailsRg);
        yesCDRdbtn = view.findViewById(R.id.yesCDRdbtn);
        noCDRdbtn = view.findViewById(R.id.noCDRdbtn);
        companyDetailsLayout = view.findViewById(R.id.companyDetailsLayout);
        departmentEt = view.findViewById(R.id.departmentEt);
        designationEt = view.findViewById(R.id.designationEt);
        dojEt = view.findViewById(R.id.dojEt);
        dolEt = view.findViewById(R.id.dolEt);

        // Company Relations Views
        companyRelationRg = view.findViewById(R.id.companyRelationRg);
        yesRelationRdbtn = view.findViewById(R.id.yesRelationRdbtn);
        noRelationRdbtn = view.findViewById(R.id.noRelationRdbtn);
        companyRelationLayout = view.findViewById(R.id.companyRelationLayout);
        relationNameEt = view.findViewById(R.id.relationNameEt);
        relationEt = view.findViewById(R.id.relationEt);
        relationDesignationEt = view.findViewById(R.id.relationDesignationEt);

        // Initially hide both layouts
        companyDetailsLayout.setVisibility(View.GONE);
        companyRelationLayout.setVisibility(View.GONE);
    }

    private void setupListeners() {
        companyDetailsRg.setOnCheckedChangeListener((group, checkedId) -> {
            companyDetailsLayout.setVisibility(checkedId == R.id.yesCDRdbtn ? View.VISIBLE : View.GONE);
        });

        companyRelationRg.setOnCheckedChangeListener((group, checkedId) -> {
            companyRelationLayout.setVisibility(checkedId == R.id.yesRelationRdbtn ? View.VISIBLE : View.GONE);
        });
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dojListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dojEt.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        };

        DatePickerDialog.OnDateSetListener dolListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dolEt.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        };

        dojEt.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    dojListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        dolEt.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    dolListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    @Override
    public boolean isStepValid() {
        // Validate Previous Work Experience section
        if (!noCDRdbtn.isChecked() && !yesCDRdbtn.isChecked()) {
            showToast("Please select if you worked with us before");
            return false;
        }

        if (yesCDRdbtn.isChecked()) {
            if (departmentEt.getText().toString().trim().isEmpty()) {
                showToast("Please enter department");
                return false;
            }
            if (designationEt.getText().toString().trim().isEmpty()) {
                showToast("Please enter designation");
                return false;
            }
            if (dojEt.getText().toString().trim().isEmpty()) {
                showToast("Please select date of joining");
                return false;
            }
            if (dolEt.getText().toString().trim().isEmpty()) {
                showToast("Please select date of leaving");
                return false;
            }
        }

        // Validate Company Relations section
        if (!noRelationRdbtn.isChecked() && !yesRelationRdbtn.isChecked()) {
            showToast("Please select if you have any relation in company");
            return false;
        }

        if (yesRelationRdbtn.isChecked()) {
            if (relationNameEt.getText().toString().trim().isEmpty()) {
                showToast("Please enter relation name");
                return false;
            }
            if (relationEt.getText().toString().trim().isEmpty()) {
                showToast("Please enter relation");
                return false;
            }
            if (relationDesignationEt.getText().toString().trim().isEmpty()) {
                showToast("Please enter relation's designation");
                return false;
            }
        }

        return true;
    }

    @Override
    public Map<String, String> getStepData() {
        Map<String, String> data = new HashMap<>();

        // Previous Work Experience data
        data.put("hasWorkedBefore", yesCDRdbtn.isChecked() ? "yes" : "no");
        if (yesCDRdbtn.isChecked()) {
            data.put("department", departmentEt.getText().toString().trim());
            data.put("designation", designationEt.getText().toString().trim());
            data.put("dateOfJoining", dojEt.getText().toString().trim());
            data.put("dateOfLeaving", dolEt.getText().toString().trim());
        }

        // Company Relations data
        data.put("hasCompanyRelation", yesRelationRdbtn.isChecked() ? "yes" : "no");
        if (yesRelationRdbtn.isChecked()) {
            data.put("relationName", relationNameEt.getText().toString().trim());
            data.put("relation", relationEt.getText().toString().trim());
            data.put("relationDesignation", relationDesignationEt.getText().toString().trim());
        }

        return data;
    }

    @Override
    public void setData(PersonalDataModel data) {
        if (data != null) {
            // Set Previous Work Experience data
            if (data.getWork_with_company() != null && data.getWork_with_company().equals("yes")) {
                yesCDRdbtn.setChecked(true);
                departmentEt.setText(data.getDepartment());
                designationEt.setText(data.getDesignation());
                dojEt.setText(data.getWork_with_company_doj());
                dolEt.setText(data.getWork_with_company_dol());
            } else {
                noCDRdbtn.setChecked(true);
            }

            // Set Company Relations data
            if (data.getCompany_relation() != null && data.getCompany_relation().equals("yes")) {
                yesRelationRdbtn.setChecked(true);
                relationNameEt.setText(data.getCompany_relation_name());
                relationEt.setText(data.getCompany_relation_relation());
                relationDesignationEt.setText(data.getCompany_relation_designation());
            } else {
                noRelationRdbtn.setChecked(true);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
