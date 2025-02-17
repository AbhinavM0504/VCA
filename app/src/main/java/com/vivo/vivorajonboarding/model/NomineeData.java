package com.vivo.vivorajonboarding.model;

import com.google.gson.annotations.SerializedName;

public class NomineeData {
    @SerializedName("relation")
    private String relation;
    @SerializedName("name")
    private String name;
    @SerializedName("dob")
    private String dob;
    @SerializedName("aadhaar_front")
    private String aadhaarFront;
    @SerializedName("aadhaar_back")
    private String aadhaarBack;

    public NomineeData(String relation, String name, String dob, String aadhaarFront, String aadhaarBack) {
        this.relation = relation;
        this.name = name;
        this.dob = dob;
        this.aadhaarFront = aadhaarFront;
        this.aadhaarBack = aadhaarBack;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAadhaarFront() {
        return aadhaarFront;
    }

    public void setAadhaarFront(String aadhaarFront) {
        this.aadhaarFront = aadhaarFront;
    }

    public String getAadhaarBack() {
        return aadhaarBack;
    }

    public void setAadhaarBack(String aadhaarBack) {
        this.aadhaarBack = aadhaarBack;
    }
}

    // Getters and setters
