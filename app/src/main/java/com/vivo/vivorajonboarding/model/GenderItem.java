package com.vivo.vivorajonboarding.model;

import android.graphics.drawable.Drawable;

public class GenderItem {
    private String text;
    private Drawable icon;

    public GenderItem(String text, Drawable icon) {
        this.text = text;
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
