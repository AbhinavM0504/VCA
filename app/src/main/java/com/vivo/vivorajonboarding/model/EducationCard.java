package com.vivo.vivorajonboarding.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class EducationCard implements Parcelable {
    private String level;
    private String schoolName;
    private String percentage;
    private String board;
    private String yearOfPassing;
    private boolean isMandatory;
    private String documentUri;
    private boolean isComplete;

    public EducationCard(String level, boolean isMandatory) {
        this.level = level;
        this.isMandatory = isMandatory;
        this.isComplete = false;
    }

    // Getters and Setters with validation
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
        updateCompletionStatus();
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        if (isValidPercentage(percentage)) {
            this.percentage = percentage;
            updateCompletionStatus();
        } else {
            throw new IllegalArgumentException("Invalid percentage value");
        }
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
        updateCompletionStatus();
    }

    public String getYearOfPassing() {
        return yearOfPassing;
    }

    public void setYearOfPassing(String yearOfPassing) {
        if (isValidYear(yearOfPassing)) {
            this.yearOfPassing = yearOfPassing;
            updateCompletionStatus();
        } else {
            throw new IllegalArgumentException("Invalid year");
        }
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public String getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(String documentUri) {
        this.documentUri = documentUri;
        updateCompletionStatus();
    }

    public boolean isComplete() {
        return isComplete;
    }

    // Validation methods
    private boolean isValidPercentage(String percentage) {
        try {
            float percentValue = Float.parseFloat(percentage);
            return percentValue >= 0 && percentValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidYear(String year) {
        try {
            int yearValue = Integer.parseInt(year);
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            return yearValue >= 1900 && yearValue <= currentYear;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateCompletionStatus() {
        isComplete = !TextUtils.isEmpty(schoolName) &&
                !TextUtils.isEmpty(percentage) &&
                !TextUtils.isEmpty(board) &&
                !TextUtils.isEmpty(yearOfPassing) &&
                !TextUtils.isEmpty(documentUri);
    }

    // Parcelable implementation
    protected EducationCard(Parcel in) {
        level = in.readString();
        schoolName = in.readString();
        percentage = in.readString();
        board = in.readString();
        yearOfPassing = in.readString();
        isMandatory = in.readByte() != 0;
        documentUri = in.readString();
        isComplete = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(level);
        dest.writeString(schoolName);
        dest.writeString(percentage);
        dest.writeString(board);
        dest.writeString(yearOfPassing);
        dest.writeByte((byte) (isMandatory ? 1 : 0));
        dest.writeString(documentUri);
        dest.writeByte((byte) (isComplete ? 1 : 0));
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

    @Override
    public String toString() {
        return "EducationCard{" +
                "level='" + level + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", percentage='" + percentage + '\'' +
                ", board='" + board + '\'' +
                ", yearOfPassing='" + yearOfPassing + '\'' +
                ", isMandatory=" + isMandatory +
                ", isComplete=" + isComplete +
                '}';
    }
}