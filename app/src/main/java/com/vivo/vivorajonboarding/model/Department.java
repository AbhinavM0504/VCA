package com.vivo.vivorajonboarding.model;

public class Department {
    private String name;
    private String manager;
    private int icon;
    private int color;

    public Department(String name, String manager, int icon, int color) {
        this.name = name;
        this.manager = manager;
        this.icon = icon;
        this.color = color;
    }

    // Getters
    public String getName() { return name; }
    public String getManager() { return manager; }
    public int getIcon() { return icon; }
    public int getColor() { return color; }
}