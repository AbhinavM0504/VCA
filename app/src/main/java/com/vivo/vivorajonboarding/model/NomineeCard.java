package com.vivo.vivorajonboarding.model;

import android.os.Parcel;
import android.os.Parcelable;


public class NomineeCard implements Parcelable {
    private String relation;
    private String relativeName;
    private String dateOfBirth;
    private String percentage;

    public NomineeCard() {
        // Default constructor
    }

    public NomineeCard(String relation, String relativeName, String dateOfBirth, String percentage) {
        this.relation = relation;
        this.relativeName = relativeName;
        this.dateOfBirth = dateOfBirth;
        this.percentage = percentage;
    }

    protected NomineeCard(Parcel in) {
        relation = in.readString();
        relativeName = in.readString();
        dateOfBirth = in.readString();
        percentage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(relation);
        dest.writeString(relativeName);
        dest.writeString(dateOfBirth);
        dest.writeString(percentage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NomineeCard> CREATOR = new Creator<NomineeCard>() {
        @Override
        public NomineeCard createFromParcel(Parcel in) {
            return new NomineeCard(in);
        }

        @Override
        public NomineeCard[] newArray(int size) {
            return new NomineeCard[size];
        }
    };

    // Getters and Setters
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelativeName() {
        return relativeName;
    }

    public void setRelativeName(String relativeName) {
        this.relativeName = relativeName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}