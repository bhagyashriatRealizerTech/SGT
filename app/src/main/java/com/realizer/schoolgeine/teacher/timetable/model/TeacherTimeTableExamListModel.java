package com.realizer.schoolgeine.teacher.timetable.model;

/**
 * Created by Win on 11/20/2015.
 */
public class TeacherTimeTableExamListModel {

    private  int ttid =0;
    private String title = "";
    private String syllabus = "";
    private String teacher = "";
    private String date = "";
    private String image="";
    private String standard = "";
    private String division = "";
    private String description="";
    private String hasUploaded = "false";
    private String sharedLink ="";
    private int imageCount =0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDivision() {
        return division;
    }

    public String getDescription() {
        return description;
    }

    public String getStandard() {
        return standard;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public void setTtid(int ttid) {
        this.ttid = ttid;
    }

    public int getTtid() {
        return ttid;
    }

    public String getHasUploaded() {
        return hasUploaded;
    }

    public void setHasUploaded(String hasUploaded) {
        this.hasUploaded = hasUploaded;
    }

    public String getSharedLink() {
        return sharedLink;
    }

    public void setSharedLink(String sharedLink) {
        this.sharedLink = sharedLink;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
