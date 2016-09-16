package com.realizer.schoolgeine.teacher.homework.model;

/**
 * Created by Win on 1/8/2016.
 */
public class TeacherHomeworkModel {


    public String SchoolCode ="";
    public String hwDate="";
    public String Std ="";
    public String div ="";
    public String subject ="";
    public String hwTxtLst= "";
    public String hwImage64Lst ="";
    public String givenBy ="";
    public int hid =0;
    public String Work="";
    public String isSync="";

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }

    public String getHwDate() {
        return hwDate;
    }

    public void setHwDate(String hwDate) {
        this.hwDate = hwDate;
    }

    public String getStd() {
        return Std;
    }

    public void setStd(String std) {
        Std = std;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHwTxtLst() {
        return hwTxtLst;
    }

    public void setHwTxtLst(String hwTxtLst) {
        this.hwTxtLst = hwTxtLst;
    }

    public String getHwImage64Lst() {
        return hwImage64Lst;
    }

    public void setHwImage64Lst(String hwImage64Lst) {
        this.hwImage64Lst = hwImage64Lst;
    }

    public String getGivenBy() {
        return givenBy;
    }

    public void setGivenBy(String givenBy) {
        this.givenBy = givenBy;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public String getWork() {
        return Work;
    }

    public void setWork(String work) {
        Work = work;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }
}
