package com.vivo.vivorajonboarding.model;

import android.os.Parcel;
import android.os.Parcelable;

public class EducationCard implements Parcelable {
    private String educationLevel;
    private String schoolName;
    private String percentage;
    private String board;
    private String yearOfPassing;
    private String documentUri;
    private boolean mandatory;

    public EducationCard(String educationLevel, boolean mandatory) {
        this.educationLevel = educationLevel;
        this.mandatory = mandatory;
    }

    protected EducationCard(Parcel in) {
        educationLevel = in.readString();
        schoolName = in.readString();
        percentage = in.readString();
        board = in.readString();
        yearOfPassing = in.readString();
        documentUri = in.readString();
        mandatory = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(educationLevel);
        dest.writeString(schoolName);
        dest.writeString(percentage);
        dest.writeString(board);
        dest.writeString(yearOfPassing);
        dest.writeString(documentUri);
        dest.writeByte((byte) (mandatory ? 1 : 0));
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

    // Getters and Setters
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }
    public String getBoard() { return board; }
    public void setBoard(String board) { this.board = board; }
    public String getYearOfPassing() { return yearOfPassing; }
    public void setYearOfPassing(String yearOfPassing) { this.yearOfPassing = yearOfPassing; }
    public String getDocumentUri() { return documentUri; }
    public void setDocumentUri(String documentUri) { this.documentUri = documentUri; }
    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }
}