package com.vivo.vivorajonboarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NomineeRequest {
    @SerializedName("userid")
    private String userId;
    @SerializedName("nominees")
    private List<NomineeData> nominees;

    public NomineeRequest(String userId, List<NomineeData> nominees) {
        this.userId = userId;
        this.nominees = nominees;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<NomineeData> getNominees() {
        return nominees;
    }

    public void setNominees(List<NomineeData> nominees) {
        this.nominees = nominees;
    }
}
