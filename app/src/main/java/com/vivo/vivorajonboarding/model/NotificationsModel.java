package com.vivo.vivorajonboarding.model;



// NotificationModel.java
public class NotificationsModel {
    private String title;
    private String message;
    private String time;
    private boolean isRead;
    private String date; // "Today" or "Yesterday" or actual date
    private int notificationIcon;

    public NotificationsModel(String title, String message, String time, boolean isRead, String date, int notificationIcon) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.date = date;
        this.notificationIcon = notificationIcon;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getDate() { return date; }
    public int getNotificationIcon() { return notificationIcon; }
}

