package com.realizer.schoolgeine.teacher.myclass.model;

/**
 * Created by Bhagyashri on 1/27/2016.
 */
public class TeacherMyClassAttModel {

    private String AttDate="";
    private String Prsnt="";
    private String Abscnt="";
    private String rollno="";
    private int rno = 0;
    String profileimage;

    public String getAttDate() {
        return AttDate;
    }

    public void setAttDate(String attDate) {
        AttDate = attDate;
    }

    public String getPrsnt() {
        return Prsnt;
    }

    public void setPrsnt(String prsnt) {
        Prsnt = prsnt;
    }

    public String getAbscnt() {
        return Abscnt;
    }

    public void setAbscnt(String abscnt) {
        Abscnt = abscnt;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public int getRno() {
        return rno;
    }

    public void setRno(int rno) {
        this.rno = rno;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
