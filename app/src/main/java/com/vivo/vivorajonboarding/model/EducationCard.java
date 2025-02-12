package com.vivo.vivorajonboarding.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EducationCard implements Parcelable {
    private String educationLevel;
    private boolean mandatory;

    // Fields for 10th and 12th
    private String schoolName;
    private String board;
    private String percentage;
    private String yearOfPassing;
    private String marksheetUri;

    // Additional fields for graduation and above
    private String course;
    private String university;
    private String college;

    public EducationCard(String educationLevel, boolean mandatory) {
        this.educationLevel = educationLevel;
        this.mandatory = mandatory;
    }

    protected EducationCard(Parcel in) {
        educationLevel = in.readString();
        mandatory = in.readByte() != 0;
        schoolName = in.readString();
        board = in.readString();
        percentage = in.readString();
        yearOfPassing = in.readString();
        marksheetUri = in.readString();
        course = in.readString();
        university = in.readString();
        college = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(educationLevel);
        dest.writeByte((byte) (mandatory ? 1 : 0));
        dest.writeString(schoolName);
        dest.writeString(board);
        dest.writeString(percentage);
        dest.writeString(yearOfPassing);
        dest.writeString(marksheetUri);
        dest.writeString(course);
        dest.writeString(university);
        dest.writeString(college);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EducationCard> CREATOR = new Creator<EducationCard>() {
        @Override
        public EducationCard createFromParcel(Parcel in) {
            return new EducationCard(in);
        }

        @Override
        public EducationCard[] newArray(int size) {
            return new EducationCard[size];
        }
    };

    // Helper method to check if it's school education (10th or 12th)
    public boolean isSchoolEducation() {
        return "10th".equals(educationLevel) || "12th".equals(educationLevel);
    }

    // Getters and setters with appropriate field names based on education level
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }

    // School name / College getters and setters
    public String getSchoolName() {
        return isSchoolEducation() ? schoolName : college;
    }
    public void setSchoolName(String name) {
        if (isSchoolEducation()) {
            this.schoolName = name;
        } else {
            this.college = name;
        }
    }

    // Board / University getters and setters
    public String getBoard() {
        return isSchoolEducation() ? board : university;
    }
    public void setBoard(String name) {
        if (isSchoolEducation()) {
            this.board = name;
        } else {
            this.university = name;
        }
    }

    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }

    public String getYearOfPassing() { return yearOfPassing; }
    public void setYearOfPassing(String yearOfPassing) { this.yearOfPassing = yearOfPassing; }

    public String getDocumentUri() { return marksheetUri; }
    public void setDocumentUri(String uri) { this.marksheetUri = uri; }

    // Graduation specific getters and setters
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    // Helper methods to get the appropriate field names based on education level
    public String getSchoolFieldName() {
        return isSchoolEducation() ?
                educationLevel.toLowerCase().replace("th", "") + "_school" :
                educationLevel.toLowerCase() + "_college";
    }

    public String getBoardFieldName() {
        return isSchoolEducation() ?
                educationLevel.toLowerCase().replace("th", "") + "_board" :
                educationLevel.toLowerCase() + "_university";
    }

    public String getPercentageFieldName() {
        return (isSchoolEducation() ?
                educationLevel.toLowerCase().replace("th", "") :
                educationLevel.toLowerCase()) + "_percentage";
    }

    public String getYearPassingFieldName() {
        return (isSchoolEducation() ?
                educationLevel.toLowerCase().replace("th", "") :
                educationLevel.toLowerCase()) + "_year_passing";
    }

    public String getMarksheetFieldName() {
        return (isSchoolEducation() ?
                educationLevel.toLowerCase().replace("th", "") :
                educationLevel.toLowerCase()) + "_marksheet";
    }

    public String getCourseFieldName() {
        return educationLevel.toLowerCase() + "_course";
    }
}