package com.vivo.vivorajonboarding.model;

public class InsuranceModel {
    String id, userid, memberRelation, memberName, memberDob, memberAcFrontImage, memberAcBackImage, time, request, member_included_for_insurance;

    public InsuranceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMemberRelation() {
        return memberRelation;
    }

    public void setMemberRelation(String memberRelation) {
        this.memberRelation = memberRelation;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberDob() {
        return memberDob;
    }

    public void setMemberDob(String memberDob) {
        this.memberDob = memberDob;
    }

    public String getMemberAcFrontImage() {
        return memberAcFrontImage;
    }

    public void setMemberAcFrontImage(String memberAcFrontImage) {
        this.memberAcFrontImage = memberAcFrontImage;
    }

    public String getMemberAcBackImage() {
        return memberAcBackImage;
    }

    public void setMemberAcBackImage(String memberAcBackImage) {
        this.memberAcBackImage = memberAcBackImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getMember_included_for_insurance() {
        return member_included_for_insurance;
    }

    public void setMember_included_for_insurance(String member_included_for_insurance) {
        this.member_included_for_insurance = member_included_for_insurance;
    }
}
