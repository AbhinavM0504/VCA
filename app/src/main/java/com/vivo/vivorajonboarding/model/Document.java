package com.vivo.vivorajonboarding.model;

import android.net.Uri;

public class Document {
    private String id;
    private String title;
    private String fileName;
    private Uri fileUri;
    private boolean isUploaded;
    private String fieldName; // Database field name
    private String mimeType;
    private int iconResource;
    private long fileSize;// Add this field for document-specific icons

    public Document(String id, String title, String fieldName, int iconResource) {
        this.id = id;
        this.title = title;
        this.fieldName = fieldName;
        this.iconResource = iconResource;
        this.isUploaded = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Uri getFileUri() { return fileUri; }
    public void setFileUri(Uri fileUri) { this.fileUri = fileUri; }
    public boolean isUploaded() { return isUploaded; }
    public void setUploaded(boolean uploaded) { isUploaded = uploaded; }
    public String getFieldName() { return fieldName; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public int getIconResource() { return iconResource; }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}