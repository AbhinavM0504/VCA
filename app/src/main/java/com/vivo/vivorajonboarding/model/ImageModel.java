package com.vivo.vivorajonboarding.model;

import com.google.gson.annotations.SerializedName;

public class ImageModel {
    @SerializedName("image_id")
    private int imageId;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("title")
    private String title;

    @SerializedName("upload_date")
    private String uploadDate;

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
}
