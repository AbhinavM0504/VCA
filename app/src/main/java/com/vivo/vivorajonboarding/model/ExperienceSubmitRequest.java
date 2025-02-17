package com.vivo.vivorajonboarding.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExperienceSubmitRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("experiences")
    private List<ExperienceData> experiences;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ExperienceData> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceData> experiences) {
        this.experiences = experiences;
    }

    public static class ExperienceData {
        @SerializedName("companyName")
        private String companyName;

        @SerializedName("jobTitle")
        private String jobTitle;

        @SerializedName("startDate")
        private String startDate;

        @SerializedName("endDate")
        private String endDate;

        @SerializedName("experienceLetter")
        private String experienceLetter;

        public ExperienceData(ExperienceCard card) {
            this.companyName = card.getCompanyName();
            this.jobTitle = card.getJobTitle();
            this.startDate = card.getStartDate();
            this.endDate = card.getEndDate();
            this.experienceLetter = card.getFileUri();
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getExperienceLetter() {
            return experienceLetter;
        }

        public void setExperienceLetter(String experienceLetter) {
            this.experienceLetter = experienceLetter;
        }
    }

    public ExperienceSubmitRequest(String userId, List<ExperienceData> experiences) {
        this.userId = userId;
        this.experiences = experiences;
    }
}
