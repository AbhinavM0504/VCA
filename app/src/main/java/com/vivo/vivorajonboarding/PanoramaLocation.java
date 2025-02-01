package com.vivo.vivorajonboarding;

import java.util.List;

public class PanoramaLocation {
    private String id;                 // Unique identifier for the location
    private String locationName;       // Name of the location
    private int imageResourceId;       // Resource ID of the panorama image
    private List<HotSpot> hotspots;    // List of hotspots associated with this location

    // Constructor
    public PanoramaLocation(String id, String locationName, int imageResourceId, List<HotSpot> hotspots) {
        this.id = id;
        this.locationName = locationName;
        this.imageResourceId = imageResourceId;
        this.hotspots = hotspots;
    }

    // Getters
    public String getId() { return id; }
    public String getLocationName() { return locationName; }
    public int getImageResourceId() { return imageResourceId; }
    public List<HotSpot> getHotspots() { return hotspots; }

    // Setters (optional)
    public void setId(String id) { this.id = id; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
    public void setHotspots(List<HotSpot> hotspots) { this.hotspots = hotspots; }
}
