package com.vivo.vivorajonboarding.fragment;

import com.vivo.vivorajonboarding.model.PersonalDataModel;

import java.util.Map;

public interface StepperFragment {
    boolean isStepValid();
    Map<String, String> getStepData();
    void setData(PersonalDataModel data);
}
