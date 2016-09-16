package com.realizer.schoolgeine.teacher.generalcommunication.model;

/**
 * Created by Win on 11/17/2015.
 */
public class TeacherGeneralCommunicationListModel {

    private String AnnouncementTime = "";
    private String AnnouncementText = "";
    private String Category = "";
    private int AnnouncementId;
    private String AcademicYr = "";
    private String Std = "";
    private String division = "";
    private String SyncUpFlag = "";
    private String sentBy = "";
    private String SchoolCode = "";

    public String getAnnouncementTime() {
        return AnnouncementTime;
    }

    public void setAnnouncementTime(String announcementTime) {
        AnnouncementTime = announcementTime;
    }

    public String getAnnouncementText() {
        return AnnouncementText;
    }

    public void setAnnouncementText(String announcementText) {
        AnnouncementText = announcementText;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public int getAnnouncementId() {
        return AnnouncementId;
    }

    public void setAnnouncementId(int announcementId) {
        AnnouncementId = announcementId;
    }

    public String getAcademicYr() {
        return AcademicYr;
    }

    public void setAcademicYr(String academicYr) {
        AcademicYr = academicYr;
    }

    public String getStd() {
        return Std;
    }

    public void setStd(String std) {
        Std = std;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSyncUpFlag() {
        return SyncUpFlag;
    }

    public void setSyncUpFlag(String syncUpFlag) {
        SyncUpFlag = syncUpFlag;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }
}

