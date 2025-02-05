package com.vivo.vivorajonboarding.model;

public class RelationItem {
    private String name;
    private int iconRes;

    public RelationItem(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationItem that = (RelationItem) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
