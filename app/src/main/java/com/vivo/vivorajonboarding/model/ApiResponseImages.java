package com.vivo.vivorajonboarding.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ApiResponseImages {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<ImageModel> data;

    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public List<ImageModel> getData() { return data; }
    public String getMessage() { return message; }
}