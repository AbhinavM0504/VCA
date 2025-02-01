package com.vivo.vivorajonboarding;

public class HotSpot {
    private long id;                  // Unique ID for the hotspot
    private float yaw;                // Horizontal angle in degrees
    private float pitch;              // Vertical angle in degrees
    private int targetLocationIndex;  // Index of the target location
    private String description;       // Description of the hotspot
    private int imageResourceId;      // Resource ID for the hotspot image (optional)

    // Constructor with image resource
    public HotSpot(long id, float yaw, float pitch, int targetLocationIndex, String description, int imageResourceId) {
        this.id = id;
        this.yaw = yaw;
        this.pitch = pitch;
        this.targetLocationIndex = targetLocationIndex;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    // Constructor without image resource
    public HotSpot(long id, float yaw, float pitch, int targetLocationIndex, String description) {
        this(id, yaw, pitch, targetLocationIndex, description, 0); // Default image resource ID as 0
    }

    // Getters
    public long getId() { return id; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public int getTargetLocationIndex() { return targetLocationIndex; }
    public String getDescription() { return description; }
    public int getImageResourceId() { return imageResourceId; }

    // Setters (if required)
    public void setId(long id) { this.id = id; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public void setTargetLocationIndex(int targetLocationIndex) { this.targetLocationIndex = targetLocationIndex; }
    public void setDescription(String description) { this.description = description; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
}
