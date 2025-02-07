package com.vivo.vivorajonboarding.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<FeedPost> data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FeedPost> getData() {
        return data;
    }

    public void setData(List<FeedPost> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}