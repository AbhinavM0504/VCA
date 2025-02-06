package com.vivo.vivorajonboarding.model;


import android.os.Parcel;
import android.os.Parcelable;

public class InsuranceNomineeCard implements Parcelable {
    private String relation;
    private String relativeName;
    private String dateOfBirth;
    private String aadhaarFrontImage;
    private String aadhaarBackImage;

    public InsuranceNomineeCard() {
    }

    protected InsuranceNomineeCard(Parcel in) {
        relation = in.readString();
        relativeName = in.readString();
        dateOfBirth = in.readString();
        aadhaarFrontImage = in.readString();
        aadhaarBackImage = in.readString();
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

    public String getAadhaarFrontImage() {
        return aadhaarFrontImage;
    }

    public void setAadhaarFrontImage(String aadhaarFrontImage) {
        this.aadhaarFrontImage = aadhaarFrontImage;
    }

    public String getAadhaarBackImage() {
        return aadhaarBackImage;
    }

    public void setAadhaarBackImage(String aadhaarBackImage) {
        this.aadhaarBackImage = aadhaarBackImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(relation);
        dest.writeString(relativeName);
        dest.writeString(dateOfBirth);
        dest.writeString(aadhaarFrontImage);
        dest.writeString(aadhaarBackImage);
    }
}

