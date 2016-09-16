package com.realizer.schoolgeine.teacher.myclass.model;

/**
 * Created by Win on 1/4/2016.
 */
public class TeacherAttendanceListModel {
    public int AttendanceId ;
    public String attendanceDate ;
    public String SchoolCode ;
    public String Std ;
    public String Div ;
    public String AttendanceBy ;
    public String Attendees ;
    public String Absenties ;
    public int PresenceCnt ;
    public int AbsentCnt ;
    public String Syncflag;


    public int getAttendanceId() {
        return AttendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        AttendanceId = attendanceId;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
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

    public String getAttendanceBy() {
        return AttendanceBy;
    }

    public void setAttendanceBy(String attendanceBy) {
        AttendanceBy = attendanceBy;
    }

    public String getAttendees() {
        return Attendees;
    }

    public void setAttendees(String attendees) {
        Attendees = attendees;
    }

    public String getAbsenties() {
        return Absenties;
    }

    public void setAbsenties(String absenties) {
        Absenties = absenties;
    }


    public int getAbsentCnt() {
        return AbsentCnt;
    }

    public void setAbsentCnt(int absentCnt) {
        AbsentCnt = absentCnt;
    }

    public int getPresenceCnt() {
        return PresenceCnt;
    }

    public void setPresenceCnt(int presenceCnt) {
        PresenceCnt = presenceCnt;
    }

    public String getSyncflag() {
        return Syncflag;
    }

    public void setSyncflag(String syncflag) {
        Syncflag = syncflag;
    }
}

