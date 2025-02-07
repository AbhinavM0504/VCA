package com.vivo.vivorajonboarding.model;

import com.google.gson.annotations.SerializedName;

public class AlbumModel {
    @SerializedName("album_id")
    private int albumId;

    @SerializedName("album_name")
    private String albumName;

    @SerializedName("cover_image_url")
    private String coverImageUrl;

    @SerializedName("image_count")
    private int imageCount;

    // Getters and Setters
    public int getAlbumId() { return albumId; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }

    public String getAlbumName() { return albumName; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public int getImageCount() { return imageCount; }
    public void setImageCount(int imageCount) { this.imageCount = imageCount; }
}