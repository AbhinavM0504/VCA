package com.vivo.vivorajonboarding.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponseAlbum {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<AlbumModel> data;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AlbumModel> getData() {
        return data;
    }

    public void setData(List<AlbumModel> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}