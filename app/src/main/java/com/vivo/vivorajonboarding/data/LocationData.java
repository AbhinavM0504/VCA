package com.vivo.vivorajonboarding.data;



import java.util.ArrayList;
import java.util.List;

public class LocationData {
    public int resourceId;
    public String title;
    public String description;
    public List<HotspotData> hotspots;
    public List<String> features;
    public int thumbnailResourceId;
    public float defaultPitch;
    public float defaultYaw;

    public LocationData(int resourceId, String title, String description,
                        int thumbnailResourceId, float pitch, float yaw) {
        this.resourceId = resourceId;
        this.title = title;
        this.description = description;
        this.thumbnailResourceId = thumbnailResourceId;
        this.hotspots = new ArrayList<>();
        this.features = new ArrayList<>();
        this.defaultPitch = pitch;
        this.defaultYaw = yaw;
    }
}
