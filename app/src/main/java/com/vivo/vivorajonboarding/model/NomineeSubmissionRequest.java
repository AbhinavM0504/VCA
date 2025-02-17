package com.vivo.vivorajonboarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NomineeSubmissionRequest {
    @SerializedName("userid")
    private String userId;

    @SerializedName("nominees")
    private List<NomineeCard> nominees;

    public NomineeSubmissionRequest(String userId, List<NomineeCard> nominees) {
        this.userId = userId;
        this.nominees = nominees;
    }
}
