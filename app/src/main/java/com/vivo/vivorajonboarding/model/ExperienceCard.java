package com.vivo.vivorajonboarding.model;

import java.io.Serializable;

public class ExperienceCard implements Serializable {
    private String companyName;
    private String jobTitle;
    private String startDate;
    private String endDate;
    private String experienceLetterPath;
    private String fileUri;        // For storing the URI of uploaded file
    private boolean isPDF;         // Flag to identify file type
    private boolean mandatory;
    private String fileName;       // For storing the name of uploaded file
    private boolean isSubmitted;    // Add this field

    public ExperienceCard(boolean mandatory) {
        this.mandatory = mandatory;
        this.isSubmitted = false;
    }

    // Existing getters and setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getExperienceLetterPath() { return experienceLetterPath; }
    public void setExperienceLetterPath(String path) { this.experienceLetterPath = path; }

    public boolean isMandatory() { return mandatory; }

    // New getters and setters for file handling
    public String getFileUri() { return fileUri; }
    public void setFileUri(String fileUri) { this.fileUri = fileUri; }

    public boolean isPDF() { return isPDF; }
    public void setPDF(boolean isPDF) { this.isPDF = isPDF; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    // Helper method to check if a file is attached
    public boolean hasAttachment() {
        return fileUri != null && !fileUri.isEmpty();
    }

    // Clear file data
    public void clearFileData() {
        fileUri = null;
        fileName = null;
        experienceLetterPath = null;
        isPDF = false;
    }

    // Add getter and setter
    public boolean isSubmitted() { return isSubmitted; }
    public void setSubmitted(boolean submitted) { isSubmitted = submitted; }

    @Override
    public String toString() {
        return "ExperienceCard{" +
                "companyName='" + companyName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", fileType='" + (isPDF ? "PDF" : "Image") + '\'' +
                ", fileName='" + fileName + '\'' +
                ", mandatory=" + mandatory +
                '}';
    }
}