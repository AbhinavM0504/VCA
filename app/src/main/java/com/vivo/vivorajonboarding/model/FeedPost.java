// FeedPost.java
package com.vivo.vivorajonboarding.model;

import java.util.List;

public class FeedPost {
    private List<Integer> imageResources;
    private String title;
    private String description;
    private String date;

    public FeedPost(List<Integer> imageResources, String title, String description, String date) {
        this.imageResources = imageResources;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters
    public List<Integer> getImageResources() { return imageResources; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}