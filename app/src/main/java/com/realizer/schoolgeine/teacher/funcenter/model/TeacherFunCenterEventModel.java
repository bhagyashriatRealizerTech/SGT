package com.realizer.schoolgeine.teacher.funcenter.model;

/**
 * Created by Dell on 4/25/2016.
 */
public class TeacherFunCenterEventModel {


    public String SchoolCode;
    public String Std;
    public String Div;
    public int AcademicYear;
    public int EventId;
    public String EventName;
    public String EventDate;
    public String ThumbNailImage;
    public String EventUUID;
    public String filename;
    public String sharedlink;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }

    public String getStd() {
        return Std;
    }

    public void setStd(String std) {
        Std = std;
    }

    public String getDiv() {
        return Div;
    }

    public void setDiv(String div) {
        Div = div;
    }

    public int getAcademicYear() {
        return AcademicYear;
    }

    public void setAcademicYear(int academicYear) {
        AcademicYear = academicYear;
    }

    public int getEventId() {
        return EventId;
    }

    public void setEventId(int eventId) {
        EventId = eventId;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getThumbNailImage() {
        return ThumbNailImage;
    }

    public void setThumbNailImage(String thumbNailImage) {
        ThumbNailImage = thumbNailImage;
    }

    public String getEventUUID() {
        return EventUUID;
    }

    public void setEventUUID(String eventUUID) {
        EventUUID = eventUUID;
    }

    public String getSharedlink() {
        return sharedlink;
    }

    public void setSharedlink(String sharedlink) {
        this.sharedlink = sharedlink;
    }
}
