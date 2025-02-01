
package com.vivo.vivorajonboarding.data;

public class HotspotData {
    public long id;
    public float pitch;
    public float yaw;
    public String description;
    public int iconResourceId;
    public int targetLocationIndex;

    public HotspotData(long id, float pitch, float yaw, String description,
                       int iconResourceId, int targetLocationIndex) {
        this.id = id;
        this.pitch = pitch;
        this.yaw = yaw;
        this.description = description;
        this.iconResourceId = iconResourceId;
        this.targetLocationIndex = targetLocationIndex;
    }
}