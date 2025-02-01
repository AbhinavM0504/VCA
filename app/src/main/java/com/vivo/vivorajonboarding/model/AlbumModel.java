package com.vivo.vivorajonboarding.model;

import java.util.List;

public class AlbumModel {
    private String albumName;
    private String coverImageUrl;
    private List<String> imageUrls;
    private int imageCount;

    public AlbumModel(String albumName, String coverImageUrl, List<String> imageUrls) {
        this.albumName = albumName;
        this.coverImageUrl = coverImageUrl;
        this.imageUrls = imageUrls;
        this.imageCount = imageUrls.size();
    }

    // Getters and setters
    public String getAlbumName() { return albumName; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public List<String> getImageUrls() { return imageUrls; }
    public int getImageCount() { return imageCount; }
}

