package com.vivo.vivorajonboarding;

import com.panoramagl.PLImage;
import com.panoramagl.hotspots.PLHotspot;

public class CustomPLHotspot extends PLHotspot {
    private int tag; // Store custom tag

    public CustomPLHotspot(long id, PLImage image, float yaw, float pitch, int tag) {
        super(id, image, yaw, pitch);
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
