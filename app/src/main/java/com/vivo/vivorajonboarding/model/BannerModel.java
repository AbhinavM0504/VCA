package com.vivo.vivorajonboarding.model;



import com.google.gson.annotations.SerializedName;

public class BannerModel {
    @SerializedName("offer_image")
    private String offerImage;

    @SerializedName("offer_details")
    private String offerDetails;

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }

    // Getters and setters
}
