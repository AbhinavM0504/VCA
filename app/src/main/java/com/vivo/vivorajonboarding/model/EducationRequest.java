package com.vivo.vivorajonboarding.model;

public class EducationRequest {
    private String userid;
    private String educationLevel;
    private String school;
    private String board;
    private String percentage;
    private String yearPassing;
    private String marksheetUri;
    private String course;
    private String university;
    private String college;
    private String choice;

    // Constructor
    public EducationRequest(String userid, EducationCard card) {
        this.userid = userid;
        this.educationLevel = card.getEducationLevel();

        if (card.isSchoolEducation()) {
            this.school = card.getSchoolName();
            this.board = card.getBoard();
        } else {
            this.course = card.getCourse();
            this.university = card.getUniversity();
            this.college = card.getCollege();
        }

        this.percentage = card.getPercentage();
        this.yearPassing = card.getYearOfPassing();
        this.marksheetUri = card.getDocumentUri();
        this.choice = "yes"; // Since we're saving, it's always yes
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getYearPassing() {
        return yearPassing;
    }

    public void setYearPassing(String yearPassing) {
        this.yearPassing = yearPassing;
    }

    public String getMarksheetUri() {
        return marksheetUri;
    }

    public void setMarksheetUri(String marksheetUri) {
        this.marksheetUri = marksheetUri;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
