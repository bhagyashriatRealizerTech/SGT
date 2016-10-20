package com.realizer.schoolgeine.teacher.backend;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Notification.NotificationModel;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.model.ExceptionModel;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterEventModel;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterGalleryModel;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterImageModel;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterModel;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGeneralCommunicationListModel;
import com.realizer.schoolgeine.teacher.holiday.model.TeacherPublicHolidayListModel;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherAttendanceListModel;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;
import com.realizer.schoolgeine.teacher.star.model.TeacherGiveStarModel;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Win on 12/21/2015.
 */
public class DatabaseQueries {

    SQLiteDatabase db;
    Context context;
    String scode;

    public DatabaseQueries(Context context) {

        this.context = context;
        SQLiteOpenHelper myHelper = SqliteHelper.getInstance(context);
        this.db = myHelper.getWritableDatabase();

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
         scode= sharedpreferences.getString("SchoolCode", "");
    }

//Insert Student Information
    public long insertStudInfo(String standard,String division,String Studarr)
    {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("StudArr", Studarr);
        long newRowInserted = db.insert("StudInfo", null, conV);

        return newRowInserted;
    }

    //Insert Teacher Information
    public long insertTeacherInfo(String standard,String division,String Studarr)
    {

        ContentValues conV = new ContentValues();
        conV.put("Id", standard);
        conV.put("DispName", division);
        conV.put("Pic", Studarr);
        long newRowInserted = db.insert("TeacherInfo", null, conV);

        return newRowInserted;
    }

    //Insert Detail Teacher Info
    public long insertDetailTeacherInfo(String adate,String classon,String tname,String qual,String thumb,String uid,
                                        String cno,String dob,String emailid,String isactive)
    {
        ContentValues conV = new ContentValues();
        conV.put("ActiveDate", adate);
        conV.put("ClassTeacherOn", classon);
        conV.put("Name", tname);
        conV.put("Qualification", qual);
        conV.put("ThumbnailURL", thumb);
        conV.put("UserId", uid);
        conV.put("ContactNo", cno);
        conV.put("DOB", dob);
        conV.put("EmailId", emailid);
        conV.put("IsActive", isactive);
        long newRowInserted = db.insert("TeacherFullInfo", null, conV);
        return newRowInserted;
    }




    //Insert Subject Allocation Information
    public long insertStdSubjcet(String standard,String subject)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Subject", subject);
        long newRowInserted = db.insert("StdSubject", null, conV);

        return newRowInserted;
    }


    //Select All Data From StudInfo
    public String GetAllTableData(String Std,String Div) {

        Cursor c = db.rawQuery("SELECT * FROM StudInfo ", null);
        String Stud = "";

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String std = c.getString(c.getColumnIndex("Std"));
                    String div = c.getString(c.getColumnIndex("Div"));
                    if(std.equals(Std) && div.equals(Div)) {
                        Stud = c.getString(c.getColumnIndex("StudArr"));
                    }
                }
                while (c.moveToNext());
            }
        } else {
           // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return Stud;
    }
    //Insert TimeTable data
    public long insertTimeTable(String std,String div,String tname,String imglst,String givenby,String tdate,String desc)
    {
        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("TmTbleName", tname);
        conV.put("Imglst", imglst);
        conV.put("Givenby", givenby);
        conV.put("TTDate", tdate);
        conV.put("HasSyncedUp", "false");
        conV.put("Description", desc);
        long newRowInserted = db.insert("TimeTable", null, conV);
        return newRowInserted;
    }



    // get ID
    public int getTimeTableId() {
        Cursor c = db.rawQuery("SELECT TTId FROM TimeTable ", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        return att;
    }

    //Select all Timetables

    public ArrayList<TeacherTimeTableExamListModel> GetTimeTableData(String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM TimeTable WHERE Std='"+std+"' and Div='"+div+"' ", null);

        ArrayList<TeacherTimeTableExamListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherTimeTableExamListModel o = new TeacherTimeTableExamListModel();
                    o.setTtid(c.getInt(c.getColumnIndex("TTId")));
                    o.setStandard(c.getString(c.getColumnIndex("Std")));
                    o.setDivision(c.getString(c.getColumnIndex("Div")));
                    o.setTitle(c.getString(c.getColumnIndex("TmTbleName")));
                    o.setImage(c.getString(c.getColumnIndex("Imglst")));
                    o.setTeacher(c.getString(c.getColumnIndex("Givenby")));
                    o.setDate(c.getString(c.getColumnIndex("TTDate")));
                    o.setDescription(c.getString(c.getColumnIndex("Description")));
                    o.setHasUploaded(c.getString(c.getColumnIndex("HasSyncedUp")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //select Query
    public TeacherTimeTableExamListModel GetTimeTable(int id) {
        Cursor c = db.rawQuery("SELECT * FROM TimeTable WHERE TTId="+id, null);
        TeacherTimeTableExamListModel o = new TeacherTimeTableExamListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setTtid(c.getInt(c.getColumnIndex("TTId")));
                    o.setStandard(c.getString(c.getColumnIndex("Std")));
                    o.setDivision(c.getString(c.getColumnIndex("Div")));
                    o.setTitle(c.getString(c.getColumnIndex("TmTbleName")));
                    o.setImage(c.getString(c.getColumnIndex("Imglst")));
                    o.setTeacher(c.getString(c.getColumnIndex("Givenby")));
                    o.setDate(c.getString(c.getColumnIndex("TTDate")));
                    o.setDescription(c.getString(c.getColumnIndex("Description")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    public long updateTimeTableSyncFlag(TeacherTimeTableExamListModel o) {
        ContentValues conV = new ContentValues();
        conV.put("Std", o.getStandard());
        conV.put("Div", o.getDivision());
        conV.put("TmTbleName", o.getTitle());
        conV.put("Imglst", o.getImage());
        conV.put("Givenby", o.getTeacher());
        conV.put("TTDate", o.getDate());
        conV.put("HasSyncedUp","true");
        conV.put("Description", o.getDescription());

        long newRowUpdate = db.update("TimeTable", conV, "TTId=" + o.getTtid(), null);


        return newRowUpdate;
    }

    // Insert Subject Allocation Information
    public long insertSubInfo(String standard,String division,String sub)
    {
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("Sub", sub);
        long newRowInserted = db.insert("StdDivSub", null, conV);


        return newRowInserted;
    }

    // Select Allocated Std And Div from  STDDivSub
    public ArrayList<TeacherMyClassModel> GetAllSubDivStd() {
        Cursor c = db.rawQuery("SELECT * FROM StdDivSub ", null);
        ArrayList<TeacherMyClassModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String Std = c.getString(c.getColumnIndex("Std"));
                    String Div = c.getString(c.getColumnIndex("Div"));
                    String Sub = c.getString(c.getColumnIndex("Sub"));

                    TeacherMyClassModel o = new TeacherMyClassModel();
                    o.setSrno("" + cnt);
                    o.setStandard(Std);
                    o.setDivisioin(Div);
                    o.setSubjectName(Sub);
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Select subject std specific
    // Select Allocated Std And Div from  STDDivSub
    public ArrayList<String> GetSub(String std,String div) {
        Cursor c = db.rawQuery("SELECT Sub FROM StdDivSub WHERE Std='"+std+"' and Div='"+div+"' ", null);
        ArrayList<String> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String Sub = c.getString(0);
                    result.add(Sub);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }


    //Insert Announcement Information
    public long insertAnnouncement(String year,String sdate,String msg,String standard,String division,String category,String sendby,String hassyncup)
    {
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("Year", year);
        conV.put("SDate", sdate);
        conV.put("Message", msg);
        conV.put("SendBy", sendby);
        conV.put("Category", category);
        conV.put("HasSyncedUp", hassyncup);


        long newRowInserted = db.insert("Announcement", null, conV);


        return newRowInserted;
    }
    public int getAnnouncementId() {
        Cursor c = db.rawQuery("SELECT AnnounceID FROM Announcement ", null);
        ArrayList<TeacherGeneralCommunicationListModel> result = new ArrayList<>();

        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }


    //Select Announcement Information

    // Select Allocated Std And Div from  STDDivSub
    public ArrayList<TeacherGeneralCommunicationListModel> GetAnnouncement(String std,String div) {
        Cursor c = db.rawQuery("SELECT * FROM Announcement Where Std='" + std + "' " + "and Div='" + div + "' ORDER BY AnnounceID DESC", null);
        ArrayList<TeacherGeneralCommunicationListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherGeneralCommunicationListModel o = new TeacherGeneralCommunicationListModel();
                    o.setAnnouncementId(c.getInt(c.getColumnIndex("AnnounceID")));
                    o.setAcademicYr(c.getString(c.getColumnIndex("Year")));
                    o.setSentBy(c.getString(c.getColumnIndex("SendBy")));
                    o.setCategory(c.getString(c.getColumnIndex("Category")));
                    o.setAnnouncementTime(c.getString(c.getColumnIndex("SDate")));
                    o.setDivision(c.getString(c.getColumnIndex("Div")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setAnnouncementText(c.getString(c.getColumnIndex("Message")));
                    o.setSyncUpFlag(c.getString(c.getColumnIndex("HasSyncedUp")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Select Announcement where ID

    public TeacherGeneralCommunicationListModel GetAnnouncementID(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Announcement WHERE AnnounceID=" + id, null);
        TeacherGeneralCommunicationListModel o = new TeacherGeneralCommunicationListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setAnnouncementId(c.getInt(c.getColumnIndex("AnnounceID")));
                    o.setAcademicYr(c.getString(c.getColumnIndex("Year")));
                    o.setSentBy(c.getString(c.getColumnIndex("SendBy")));
                    o.setCategory(c.getString(c.getColumnIndex("Category")));
                    o.setAnnouncementTime(c.getString(c.getColumnIndex("SDate")));
                    o.setDivision(c.getString(c.getColumnIndex("Div")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setAnnouncementText(c.getString(c.getColumnIndex("Message")));
                    o.setSyncUpFlag(c.getString(c.getColumnIndex("HasSyncedUp")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Update Announcement Syncup Flag
    public long updateAnnouncementSyncFlag(TeacherGeneralCommunicationListModel o) {
        ContentValues conV = new ContentValues();
        conV.put("AnnounceID", o.getAnnouncementId());
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDivision());
        conV.put("Year", o.getAcademicYr());
        conV.put("SDate", o.getAnnouncementTime());
        conV.put("Message", o.getAnnouncementText());
        conV.put("SendBy", o.getSentBy());
        conV.put("Category", o.getCategory());
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("Announcement", conV, "AnnounceID=" + o.getAnnouncementId(), null);


        return newRowUpdate;
    }



    //Insert Queue Infromation
    public long insertQueue(int id,String type,String priority,String time) {
        ContentValues conV = new ContentValues();
        conV.put("Id", id);
        conV.put("Type", type);
        conV.put("SyncPriority", priority);
        conV.put("Time", time);

        long newRowInserted = db.insert("SyncUPQueue", null, conV);


        return newRowInserted;
    }

/*
    // Select queue Information
    public ArrayList<QueueListModel> GetQueueData() {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue ORDER BY SyncPriority ASC ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }*/


    //Delete Row from Queue

    public long deleteQueueRow(int id,String type)
    {
        long deleterow = db.delete("SyncUPQueue","Id="+id+" and Type='"+type+"'",null);
        return deleterow;
    }


    //Insert Attendance Information
    public long insertAttendance(String attdate,String schoolcode,String std,String div,String attby,String att,String abscnt,int attcount,int abscnecount) {
        ContentValues conV = new ContentValues();

        conV.put("attendanceDate", attdate);
        conV.put("SchoolCode", schoolcode);
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("AttendanceBy", attby);
        conV.put("Attendees", att);
        conV.put("Absenties", abscnt);
        conV.put("AttendanceCnt", attcount);
        conV.put("AbsentCnt", abscnecount);
        conV.put("HasSyncedUp", "false");


        long newRowInserted = db.insert("Attendance", null, conV);


        return newRowInserted;
    }

    //Select Id from Attendnce
    // Select Allocated Std And Div from  STDDivSub
    public int getAttendanceId() {
        Cursor c = db.rawQuery("SELECT AttendanceId FROM Attendance ", null);
        ArrayList<TeacherGeneralCommunicationListModel> result = new ArrayList<>();

        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                   att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //Select Attendance where ID

    public TeacherAttendanceListModel GetAttendanceID(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Attendance WHERE AttendanceId="+id, null);
        TeacherAttendanceListModel o = new TeacherAttendanceListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setAttendanceId(c.getInt(c.getColumnIndex("AttendanceId")));
                    o.setAttendanceBy(c.getString(c.getColumnIndex("AttendanceBy")));
                    o.setAttendanceDate(c.getString(c.getColumnIndex("attendanceDate")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setAbsentCnt(c.getInt(c.getColumnIndex("AbsentCnt")));
                    o.setPresenceCnt(c.getInt(c.getColumnIndex("AttendanceCnt")));
                    o.setAttendees(c.getString(c.getColumnIndex("Attendees")));
                    o.setAbsenties(c.getString(c.getColumnIndex("Absenties")));
                    o.setSchoolCode(c.getString(c.getColumnIndex("SchoolCode")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Select By Date

    public TeacherAttendanceListModel GetAttendanceByDate(String date) {
        Cursor c = db.rawQuery("SELECT * FROM Attendance WHERE attendanceDate= '" + date + "' ", null);
        TeacherAttendanceListModel o = new TeacherAttendanceListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setAttendanceId(c.getInt(c.getColumnIndex("AttendanceId")));
                    o.setAttendanceBy(c.getString(c.getColumnIndex("AttendanceBy")));
                    o.setAttendanceDate(c.getString(c.getColumnIndex("attendanceDate")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setAbsentCnt(c.getInt(c.getColumnIndex("AbsentCnt")));
                    o.setPresenceCnt(c.getInt(c.getColumnIndex("AttendanceCnt")));
                    o.setAttendees(c.getString(c.getColumnIndex("Attendees")));
                    o.setAbsenties(c.getString(c.getColumnIndex("Absenties")));
                    o.setSchoolCode(c.getString(c.getColumnIndex("SchoolCode")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Select All Dates

    public ArrayList<String> GetAllAdtes(String std,String div) {
        Cursor c = db.rawQuery("SELECT attendanceDate FROM Attendance WHERE Std= '"+std+"' AND Div= '"+div+"'", null);
        ArrayList<String> o = new ArrayList<String>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.add(c.getString(0));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Update Attendance Syncup Flag
    public long updateAttendanceSyncFlag(TeacherAttendanceListModel o) {
        ContentValues conV = new ContentValues();
        conV.put("attendanceDate", o.getAttendanceDate());
        conV.put("SchoolCode", o.getSchoolCode());
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDiv());
        conV.put("AttendanceBy", o.getAttendanceBy());
        conV.put("Attendees", o.getAttendees());
        conV.put("Absenties", o.getAbsenties());
        conV.put("AttendanceCnt", o.getPresenceCnt());
        conV.put("AbsentCnt", o.getAbsentCnt());
        conV.put("HasSyncedUp", o.getSyncflag());

        long newRowUpdate = db.update("Attendance",conV,"AttendanceId="+o.getAttendanceId(),null);


        return newRowUpdate;
    }


    public ArrayList<TeacherAttendanceListModel> GetAttData(String std,String div) {
        Cursor c = db.rawQuery("SELECT * FROM Attendance WHERE Std= '"+std+"' AND Div= '"+div+"' ORDER BY attendanceDate DESC ", null);
        ArrayList<TeacherAttendanceListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherAttendanceListModel o = new TeacherAttendanceListModel();
                    o.setAttendanceId(c.getInt(c.getColumnIndex("AttendanceId")));
                    o.setAttendanceBy(c.getString(c.getColumnIndex("AttendanceBy")));
                    o.setAttendanceDate(c.getString(c.getColumnIndex("attendanceDate")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setAbsentCnt(c.getInt(c.getColumnIndex("AbsentCnt")));
                    o.setPresenceCnt(c.getInt(c.getColumnIndex("AttendanceCnt")));
                    o.setAttendees(c.getString(c.getColumnIndex("Attendees")));
                    o.setAbsenties(c.getString(c.getColumnIndex("Absenties")));
                    o.setSchoolCode(c.getString(c.getColumnIndex("SchoolCode")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }



    //Insert Query Information
    public long insertQuery(String fromTeacher,String from,String to,String text,String dtime,String flag,Date sentDate,String thumbnailurl,String senderName)
    {

        ContentValues conV = new ContentValues();
        conV.put("fromTeacher", fromTeacher);
        conV.put("sendfrom", from);
        conV.put("sendto", to);
        conV.put("msg", text);
        conV.put("sentTime", dtime);
        conV.put("HasSyncedUp", flag);
        conV.put("sentDate" , sentDate.getTime());
        conV.put("ThumbnailUrl" , thumbnailurl);
        conV.put("MsgSenderName", senderName);
        long newRowInserted = db.insert("Query", null, conV);

        return newRowInserted;
    }

    //select Query
    public TeacherQuerySendModel GetQuery(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE QueryId="+id, null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setProfileImage(c.getString(c.getColumnIndex("ThumbnailUrl")));
                    o.setMsgSenderName(c.getString(c.getColumnIndex("MsgSenderName")));
                    o.setSchoolCode(scode);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //select all table data

    // Select queue Information
    public ArrayList<TeacherQuerySendModel> GetQueuryData(String uid) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE sendfrom='"+uid+"' OR sendto='"+uid+"' ORDER BY sentDate Asc", null);
        ArrayList<TeacherQuerySendModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherQuerySendModel o = new TeacherQuerySendModel();
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setProfileImage(c.getString(c.getColumnIndex("ThumbnailUrl")));
                    o.setMsgSenderName(c.getString(c.getColumnIndex("MsgSenderName")));
                    o.setSchoolCode(scode);
                    Log.d("TIME", o.getSentTime());
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public int  GetLAstMessageId(String uid) {
        Cursor c = db.rawQuery("SELECT QueryId FROM Query WHERE sendfrom='"+uid+"' OR sendto='"+uid+"' ORDER BY sentDate " +
                " Desc", null);
        ArrayList<TeacherQuerySendModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherQuerySendModel o = new TeacherQuerySendModel();
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    result.add(o);
                    break;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);

        return result.get(0).getConversationId();
    }


    // Select queue Information
    public TeacherQuerySendModel GetLastMessageData(String uid) {
        int quid = GetLAstMessageId(uid);
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE QueryId='"+quid+"'", null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setFromTeacher(c.getString(c.getColumnIndex("fromTeacher")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setFrom(c.getString(c.getColumnIndex("sendfrom")));
                    o.setTo(c.getString(c.getColumnIndex("sendto")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSentDate(c.getInt(c.getColumnIndex("sentDate")));
                    o.setProfileImage(c.getString(c.getColumnIndex("ThumbnailUrl")));
                    o.setMsgSenderName(c.getString(c.getColumnIndex("MsgSenderName")));
                    o.setSchoolCode(scode);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    // get ID
    public int getQueryId() {
        Cursor c = db.rawQuery("SELECT QueryId FROM Query ", null);
        ArrayList<TeacherGeneralCommunicationListModel> result = new ArrayList<>();

        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //Update Attendance Syncup Flag
    public long updateQurySyncFlag(TeacherQuerySendModel o) {
        ContentValues conV = new ContentValues();
        conV.put("fromTeacher", o.getFromTeacher());
        conV.put("sendfrom",o.getFrom() );
        conV.put("sendto", o.getTo());
        conV.put("msg", o.getText());
        conV.put("sentTime", o.getSentTime());
        conV.put("HasSyncedUp", "true");
        conV.put("ThumbnailUrl" , o.getProfileImage());
        conV.put("MsgSenderName", o.getMsgSenderName());
        long newRowUpdate = db.update("Query", conV, "QueryId=" + o.getConversationId(), null);


        return newRowUpdate;
    }


    //Insert Holiday

    public long insertHoliday(String holiday,String hsdate,String hedate)
    {

        ContentValues conV = new ContentValues();
        conV.put("holiday", holiday);
        conV.put("hsdate", hsdate);
        conV.put("hedate", hedate);
        long newRowInserted = db.insert("Holiday", null, conV);

        return newRowInserted;
    }

    //Select all Holidays

    public ArrayList<TeacherPublicHolidayListModel> GetHolidayData() {

        Cursor c = db.rawQuery("SELECT * FROM Holiday ", null);
        ArrayList<TeacherPublicHolidayListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherPublicHolidayListModel o = new TeacherPublicHolidayListModel();
                    o.setDesc(c.getString(c.getColumnIndex("holiday")));
                    o.setStartDate(c.getString(c.getColumnIndex("hsdate")));
                    o.setEndDate(c.getString(c.getColumnIndex("hedate")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Inser Give Star Data

    public long insertGiveStar(String tuid,String suid,String sdate,String subject,String star,String comment,String std,String div,String time)
    {

        ContentValues conV = new ContentValues();
        conV.put("TeacherLoginId", tuid);
        conV.put("StudentLoginId", suid);
        conV.put("Comment", comment);
        conV.put("star", star);
        conV.put("StarDate", sdate);
        conV.put("Subject", subject);
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("StarTime", time);
        conV.put("HasSyncedUp", "false");
        long newRowInserted = db.insert("GiveStar", null, conV);

        return newRowInserted;
    }

    // get ID
    public int getGiveStarId() {
        Cursor c = db.rawQuery("SELECT GiveStarId FROM GiveStar ", null);
        ArrayList<TeacherGeneralCommunicationListModel> result = new ArrayList<>();

        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //select Query
    public TeacherGiveStarModel GetStar(int id) {
        Cursor c = db.rawQuery("SELECT * FROM GiveStar WHERE GiveStarId="+id, null);
        TeacherGiveStarModel o = new TeacherGiveStarModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setGivestarid(c.getInt(c.getColumnIndex("GiveStarId")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setTeacheruid(c.getString(c.getColumnIndex("TeacherLoginId")));
                    o.setStuduid(c.getString(c.getColumnIndex("StudentLoginId")));
                    o.setStardate(c.getString(c.getColumnIndex("StarDate")));
                    o.setStar(c.getString(c.getColumnIndex("star")));
                    o.setComment(c.getString(c.getColumnIndex("Comment")));
                    o.setTime(c.getString(c.getColumnIndex("StarTime")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    public long updateGiveStarSyncFlag(TeacherGiveStarModel o) {
        ContentValues conV = new ContentValues();
        conV.put("TeacherLoginId", o.getTeacheruid());
        conV.put("StudentLoginId", o.getStuduid());
        conV.put("Comment", o.getComment());
        conV.put("star", o.getStar());
        conV.put("StarDate", o.getStardate());
        conV.put("Subject", o.getSubject());
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDiv());
        conV.put("StarTime", o.getTime());
        conV.put("HasSyncedUp", "ture");

        long newRowUpdate = db.update("GiveStar",conV,"GiveStarId="+o.getGivestarid(),null);


        return newRowUpdate;
    }


    //Select All data
    public ArrayList<String> GetAllStarDate( String std,String div,String sub) {

        Cursor c = db.rawQuery("SELECT DISTINCT  StarDate FROM GiveStar WHERE Std='"+std+"' AND Div='"+div+"' AND Subject='"+sub+"' ", null);
        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                   String d = c.getString(c.getColumnIndex("StarDate"));
                    result.add(d);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public ArrayList<TeacherGiveStarModel> GetAllStar( String date,String std,String div,String sub) {

        Cursor c = db.rawQuery("SELECT * FROM GiveStar WHERE StarDate='"+date+"' AND Std='"+std+"' AND Div='"+div+"' AND Subject='"+sub+"' ORDER BY StarTime DESC ", null);
        ArrayList<TeacherGiveStarModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherGiveStarModel o = new TeacherGiveStarModel();
                    o.setGivestarid(c.getInt(c.getColumnIndex("GiveStarId")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setTeacheruid(c.getString(c.getColumnIndex("TeacherLoginId")));
                    o.setStuduid(c.getString(c.getColumnIndex("StudentLoginId")));
                    o.setStardate(c.getString(c.getColumnIndex("StarDate")));
                    o.setStar(c.getString(c.getColumnIndex("star")));
                    o.setComment(c.getString(c.getColumnIndex("Comment")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

//Insert Homework data
    public long insertHomework(String givenby,String subject,String hdate,String txtlst,String imglst,String std,String div,String work,String msguuid)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("subject", subject);
        conV.put("textlst", txtlst);
        conV.put("Imglst", imglst);
        conV.put("Givenby", givenby);
        conV.put("hwDate", hdate);
        conV.put("HasSyncedUp","false");
        conV.put("Work", work);
        conV.put("ShredLink", "");
        long newRowInserted = db.insert("Homework", null, conV);

        return newRowInserted;
    }

    // get ID
    public int getHomeworkId() {
        Cursor c = db.rawQuery("SELECT HomeworkId FROM Homework ORDER BY HomeworkId ASC", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //select Query
    public TeacherHomeworkModel GetHomework(int id)
    {
        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE HomeworkId="+id, null);
        TeacherHomeworkModel o = new TeacherHomeworkModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");

                do {

                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setSharedLink(c.getString(c.getColumnIndex("ShredLink")));
                    cnt = cnt+1;

                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Select Date Wise Homework

    public ArrayList<TeacherHomeworkModel> GetHomeworkData( String date,String work,String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE hwDate='"+date+"' AND Work='"+work+"' " +
                "AND Std= '"+std+"' AND Div= '"+div+"' ORDER BY HomeworkId DESC", null);


        ArrayList<TeacherHomeworkModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherHomeworkModel o = new TeacherHomeworkModel();
                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setIsSync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSharedLink(c.getString(c.getColumnIndex("ShredLink")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Select Date Wise Homework

    public ArrayList<TeacherHomeworkModel> GetHomeworkDataASC( String date,String work,String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE hwDate='"+date+"' AND Work='"+work+"' " +
                "AND Std= '"+std+"' AND Div= '"+div+"' ORDER BY HomeworkId ASC", null);


        ArrayList<TeacherHomeworkModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherHomeworkModel o = new TeacherHomeworkModel();
                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setIsSync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSharedLink(c.getString(c.getColumnIndex("ShredLink")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    //Get All Homework Data
    public ArrayList<TeacherHomeworkModel> GetAllHomeworkData(String work,String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE Work='"+work+"' " +
                "AND Std= '"+std+"' AND Div= '"+div+"' ORDER BY HomeworkId DESC", null);


        ArrayList<TeacherHomeworkModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherHomeworkModel o = new TeacherHomeworkModel();
                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setIsSync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSharedLink(c.getString(c.getColumnIndex("ShredLink")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public long updateHomeworkSyncFlag(TeacherHomeworkModel o)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDiv());
        conV.put("subject", o.getSubject());
        conV.put("textlst", o.getHwTxtLst());
        conV.put("Imglst", o.getHwImage64Lst());
        conV.put("Givenby", o.getGivenBy());
        conV.put("hwDate", o.getHwDate());
        conV.put("HasSyncedUp","true");
        conV.put("Work", o.getWork());
        conV.put("ShredLink", o.getSharedLink());

        long newRowUpdate = db.update("Homework", conV, "HomeworkId=" + o.getHid(), null);

        return newRowUpdate;
    }

    public long updateHomeworkSharedLink(TeacherHomeworkModel o)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDiv());
        conV.put("subject", o.getSubject());
        conV.put("textlst", o.getHwTxtLst());
        conV.put("Imglst", o.getHwImage64Lst());
        conV.put("Givenby", o.getGivenBy());
        conV.put("hwDate", o.getHwDate());
        conV.put("HasSyncedUp","true");
        conV.put("Work", o.getWork());
        conV.put("ShredLink", o.getSharedLink());

        long newRowUpdate = db.update("Homework", conV, "HomeworkId=" + o.getHid(), null);

        return newRowUpdate;
    }

    //Insert Homework data
    public long insertInitiatechat(String uname,String initiated,String std,String div,String uid,int unreadcount,String thumbnailUrl)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("Useranme", uname);
        conV.put("Initiated", initiated);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailUrl", thumbnailUrl);

        long newRowInserted = db.insert("InitiatedChat", null, conV);

        return newRowInserted;
    }

    public long updateInitiatechat(String std,String div,String uname,String initiate,String uid,int unreadcount,String thumbnailUrl) {
        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("Useranme", uname);
        conV.put("Initiated", initiate);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailUrl", thumbnailUrl);

        long newRowUpdate = db.update("InitiatedChat", conV, "Uid='" + uid + "'", null);


        return newRowUpdate;
    }


    public ArrayList<TeacherQuery1model> GetInitiatedChat( String std,String div,String ini) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Std='"+std+"' AND Div='"+div+"' AND Initiated='"+ini+"' ", null);
        ArrayList<TeacherQuery1model> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                        TeacherQuery1model obj = new TeacherQuery1model();
                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
                    obj.setUid(c.getString(c.getColumnIndex("Uid")));
                    obj.setUnreadCount(c.getInt(c.getColumnIndex("UnreadCount")));
                    obj.setProfilrImage(c.getString(c.getColumnIndex("ThumbnailUrl")));
                    result.add(obj);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int GetUnreadCount(String std,String div,String Uid) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Std='"+std+"' AND Div='"+div+"' AND Uid='"+Uid+"' ", null);
        int result = 0;

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    result = c.getInt(c.getColumnIndex("UnreadCount"));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public ArrayList<TeacherQuery1model> GetstudList( String std,String div)
    {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Std='"+std+"' AND Div='"+div+"'", null);
        ArrayList<TeacherQuery1model> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuery1model obj = new TeacherQuery1model();
                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
                    obj.setUid(c.getString(c.getColumnIndex("Uid")));
                    obj.setProfilrImage(c.getString(c.getColumnIndex("ThumbnailUrl")));
                    result.add(obj);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }


    public String Getuname( String uid) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Uid='"+uid+"' ", null);
        int cnt = 1;
        String temp ="";
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    temp = c.getString(c.getColumnIndex("Useranme"));
                    temp = temp+"@@@"+c.getString(c.getColumnIndex("ThumbnailUrl"));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return temp;
    }


    /*=====Fun Center======*/

    //Insert Event Info
    public long InsertEvent(String clas,String div,String event,String date,String thumbnail,String create_date,int academicyear,String eventuuid,String getimag)
    {
        //delete();
        ContentValues conV = new ContentValues();

        conV.put("Class", clas);
        conV.put("Div",div);
        conV.put("Event",event);
        conV.put("Date",date);
        conV.put("Thumbnail",thumbnail);
        conV.put("Create_Date",create_date);
        conV.put("AcademicYear",academicyear);
        conV.put("EvntuuId",eventuuid);
        conV.put("FileName",getimag);
        conV.put("ShredLink","");
        conV.put("HasSyncedUp", "false");
        long newRow = db.insert("EventMaster", null, conV);

        return newRow;
    }

    //Update Event
    public long updateEventSyncFlag(TeacherFunCenterEventModel obj) {
        ContentValues conV = new ContentValues();
        conV.put("Class", obj.getStd());
        conV.put("Div",obj.getDiv());
        conV.put("Event",obj.getEventName());
        conV.put("Date",obj.getEventDate());
        conV.put("Thumbnail",obj.getThumbNailImage());
        conV.put("Create_Date",obj.getEventDate());
        conV.put("AcademicYear",obj.getAcademicYear());
        conV.put("EvntuuId",obj.getEventUUID());
        conV.put("FileName",obj.getFilename());
        conV.put("ShredLink",obj.getSharedlink());
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("EventMaster", conV, "Event_Id=" + obj.getEventId(), null);


        return newRowUpdate;
    }

    //Update Event
    public long updateEventSharedLink(TeacherFunCenterEventModel obj) {
        ContentValues conV = new ContentValues();
        conV.put("Class", obj.getStd());
        conV.put("Div",obj.getDiv());
        conV.put("Event",obj.getEventName());
        conV.put("Date",obj.getEventDate());
        conV.put("Thumbnail",obj.getThumbNailImage());
        conV.put("Create_Date",obj.getEventDate());
        conV.put("AcademicYear",obj.getAcademicYear());
        conV.put("EvntuuId",obj.getEventUUID());
        conV.put("FileName",obj.getFilename());
        conV.put("ShredLink",obj.getSharedlink());
        conV.put("HasSyncedUp", "false");

        long newRowUpdate = db.update("EventMaster", conV, "Event_Id=" + obj.getEventId(), null);


        return newRowUpdate;
    }


    //Get Event by ID
    public TeacherFunCenterEventModel GetEventByID(int id)
    {

        Cursor c = db.rawQuery("SELECT * FROM EventMaster WHERE Event_Id="+id, null);
        TeacherFunCenterEventModel o = new TeacherFunCenterEventModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setEventId(c.getInt(c.getColumnIndex("Event_Id")));
                    o.setEventUUID(c.getString(c.getColumnIndex("EvntuuId")));
                    o.setStd(c.getString(c.getColumnIndex("Class")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setAcademicYear(c.getInt(c.getColumnIndex("AcademicYear")));
                    o.setEventDate(c.getString(c.getColumnIndex("Date")));
                    o.setEventName(c.getString(c.getColumnIndex("Event")));
                    o.setThumbNailImage(c.getString(c.getColumnIndex("Thumbnail")));
                    o.setFilename(c.getString(c.getColumnIndex("FileName")));
                    o.setSharedlink(c.getString(c.getColumnIndex("ShredLink")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Insert Event Image info
    public long InsertImage(int event_id,String image,String upload_date,String is_upload,int academicyear,int srno,String imagecaption,String imguuid,String filename,String eventuuid)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("Eventid", event_id);
        conV.put("EventUUID", eventuuid);
        conV.put("Image",image);
        conV.put("Upload_Date", upload_date);
        conV.put("Is_Uploaded", is_upload);
        conV.put("AcademicYear",academicyear);
        conV.put("SrNo",srno);
        conV.put("ImageCaption",imagecaption);
        conV.put("Imguuid", imguuid);
        conV.put("File_Name",filename);
        conV.put("ShredLink", "NoData");
        conV.put("HasSyncedUp", "false");


        long newRow1 = db.insert("EventImages", null, conV);

        return newRow1;
    }
    // get ID
    public int getEventId() {
        Cursor c = db.rawQuery("SELECT Event_Id FROM EventMaster ORDER BY Event_Id ASC", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //Update Images
    public long updateSharedImageLink(TeacherFunCenterImageModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("Eventid",obj.getEventId());
        conV.put("Image",obj.getImage());
        conV.put("Upload_Date",obj.getUpload_Date());
        conV.put("AcademicYear",obj.getAcademic_year());
        conV.put("SrNo",obj.getSrno());
        conV.put("ImageCaption",obj.getImagecaption());
        conV.put("Imguuid",obj.getImguuid());
        conV.put("File_Name",obj.getFilename());
        conV.put("ShredLink",obj.getSharedlink());
        conV.put("HasSyncedUp", "false");

        long newRowUpdate = db.update("EventImages", conV, "Image_id=" + obj.getImageid(), null);
        return newRowUpdate;
    }

    //Update Images
    public long updateImageSyncFlag(TeacherFunCenterImageModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("Eventid",obj.getEventId());
        conV.put("Image",obj.getImage());
        conV.put("Upload_Date",obj.getUpload_Date());
        conV.put("AcademicYear",obj.getAcademic_year());
        conV.put("SrNo",obj.getSrno());
        conV.put("ImageCaption",obj.getImagecaption());
        conV.put("Imguuid",obj.getImguuid());
        conV.put("File_Name",obj.getFilename());
        conV.put("ShredLink",obj.getSharedlink());
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("EventImages", conV, "Image_id=" + obj.getImageid(), null);
        return newRowUpdate;
    }

    public TeacherFunCenterImageModel getImageById(int imageId) {
        Cursor c = db.rawQuery("SELECT * FROM EventImages WHERE Image_id = '"+imageId+"'", null);
        int cnt = 1;
        TeacherFunCenterImageModel o=new TeacherFunCenterImageModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    o.setImageid(c.getString(c.getColumnIndex("Image_id")));
                    o.setEventId(c.getString(c.getColumnIndex("Eventid")));
                    o.setImage(c.getString(c.getColumnIndex("Image")));
                    o.setUpload_Date(c.getString(c.getColumnIndex("Upload_Date")));
                    o.setAcademic_year(c.getString(c.getColumnIndex("AcademicYear")));
                    o.setSrno(c.getString(c.getColumnIndex("SrNo")));
                    o.setImagecaption(c.getString(c.getColumnIndex("ImageCaption")));
                    o.setImguuid(c.getString(c.getColumnIndex("Imguuid")));
                    o.setEventuuid(c.getString(c.getColumnIndex("EventUUID")));
                    o.setFilename(c.getString(c.getColumnIndex("File_Name")));
                    o.setSharedlink(c.getString(c.getColumnIndex("ShredLink")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }


    //Get Event Name
    public ArrayList<TeacherFunCenterModel> GetEvent()
    {
        Cursor c = db.rawQuery("SELECT Event,Thumbnail,Event_Id,EvntuuId,Date FROM EventMaster ", null);
        ArrayList<TeacherFunCenterModel> event = new ArrayList<TeacherFunCenterModel>();
        event.clear();
        int cnt = 1;
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {

                    do {
                        TeacherFunCenterModel o = new TeacherFunCenterModel();

                        o.setText(c.getString(c.getColumnIndex("Event")));
                        o.setImage(c.getString(c.getColumnIndex("Thumbnail")));
                        o.setEventid(c.getInt(c.getColumnIndex("Event_Id")));
                        o.setEventuuid(c.getString(c.getColumnIndex("EvntuuId")));
                        o.setDate(c.getString(c.getColumnIndex("Date")));
                        event.add(o);

                        cnt = cnt + 1;
                    }
                    while (c.moveToNext());
                }

            } else {
                // mToast("Table Has No contain");
            }
            c.close();
            //db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return event;
    }

    //Get Images
    //Get Images
    public ArrayList<TeacherFunCenterGalleryModel> GetImage(int evntid)
    {
        // delete();

        Cursor c = db.rawQuery("SELECT Image,Upload_Date,HasSyncedUp FROM EventImages WHERE Eventid = '"+evntid+"' ORDER BY Image_id DESC",null);

        ArrayList<TeacherFunCenterGalleryModel> images= new ArrayList<TeacherFunCenterGalleryModel>();
        String tempData="";
        images.clear();
        try {
            if (c != null && c.getCount() > 0) {

                if (c.moveToFirst()) {
                    do {

                        TeacherFunCenterGalleryModel o = new TeacherFunCenterGalleryModel();

                        String image = c.getString(c.getColumnIndex("Image"));
                        String uploaddate = c.getString(c.getColumnIndex("Upload_Date"));
                        String hassynced = c.getString(c.getColumnIndex("HasSyncedUp"));
                        o.setImage(image);
                        o.setDate(uploaddate);
                        o.setStatus(hassynced);
                        images.add(o);

                        if(c.getPosition() == 0)
                            tempData = image;
                        else
                          tempData = tempData+"@@"+image;

                    }
                    while (c.moveToNext());
                }

            } else {
                // mToast("Table Has No contain");
            }
            c.close();
            // db.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Image", tempData);
        editor.commit();
        return images;
    }

    /*=====End======*/

    public void deleteTable()
    {
        db.delete("StdDivSub", null, null);
        db.delete("StudInfo", null, null);
        db.delete("TeacherInfo", null, null);
        //db.delete("Announcement",null,null);
        db.delete("Attendance", null, null);
        //db.delete("SyncUPQueue",null,null);
        db.delete("Holiday", null, null);
        db.delete("GiveStar", null, null);
        db.delete("InitiatedChat", null, null);
        //db.delete("Query",null,null);
        //db.delete("Homework",null,null);

    }


    // Select queue Information
    public ArrayList<QueueListModel> GetQueueData() {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue ORDER BY SyncPriority ASC ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int getImageId() {
        Cursor c = db.rawQuery("SELECT Image_id FROM EventImages ORDER BY Image_id ASC", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        else
        {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }



    //==================================================  Notification ======================================================== //

    //Insert Event Image info
    public long InsertNotification(NotificationModel obj)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message", obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2", obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());
        long newRow1 = db.insert("Notification", null, conV);

        return newRow1;
    }

    //Update Images
    public long UpdateNotification(NotificationModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("ID", obj.getId());
        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message", obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2", obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());

        long newRowUpdate = db.update("Notification", conV, "ID=" + obj.getId(), null);
        return newRowUpdate;
    }

    //Delete Row from Queue

    public long deleteNotificationRow(int id)
    {
        long deleterow = db.delete("Notification", "ID=" + id, null);
        return deleterow;
    }

    // Select queue Information
    public ArrayList<NotificationModel> GetNotificationsData() {
        Cursor c = db.rawQuery("SELECT * FROM Notification ORDER BY ID DESC ", null);
        ArrayList<NotificationModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public NotificationModel GetNotificationById(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE ID ="+id, null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                   result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }
    // Select queue Information
    public NotificationModel GetNotificationByUserId(String Uid) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE AdditionalData2 = '"+Uid+"'", null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            result.setNotificationId(-1);
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }


    //Insert Homework data
    public long insertException(ExceptionModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp","false");
        long newRowInserted = db.insert("Exception", null, conV);

        return newRowInserted;
    }

    public long updateException(ExceptionModel obj)
    {

        ContentValues conV = new ContentValues();
        conV.put("ExceptionId", obj.getExceptionID());
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp", "true");

        long newRowUpdate = db.update("Exception", conV, "ExceptionId=" + obj.getExceptionID(), null);

        return newRowUpdate;
    }

    public ExceptionModel GetException(int ID) {

        Cursor c = db.rawQuery("SELECT * FROM Exception WHERE ExceptionId="+ID+"", null);
        ExceptionModel result = new ExceptionModel();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    ExceptionModel obj = new ExceptionModel();
                    obj.setExceptionID(c.getInt(c.getColumnIndex("ExceptionId")));
                    obj.setUserId(c.getString(c.getColumnIndex("UserId")));
                    obj.setExceptionDetails(c.getString(c.getColumnIndex("ExceptionDetails")));
                    obj.setDeviceModel(c.getString(c.getColumnIndex("DeviceModel")));
                    obj.setAndroidVersion(c.getString(c.getColumnIndex("AndroidVersion")));
                    obj.setApplicationSource(c.getString(c.getColumnIndex("ApplicationSource")));
                    obj.setDeviceBrand(c.getString(c.getColumnIndex("DeviceBrand")));
                    result = obj;

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int getExceptionId() {
        Cursor c = db.rawQuery("SELECT ExceptionId FROM Exception ORDER BY ExceptionId DESC LIMIT 1;", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    public void deleteAllData()
    {
        long deleterow =0;

        deleterow = db.delete("StudInfo",null, null);
        deleterow = db.delete("StdDivSub",null, null);
        deleterow = db.delete("TeacherInfo",null, null);
        deleterow = db.delete("StdSubject",null, null);
        deleterow = db.delete("Announcement",null, null);
        deleterow = db.delete("Chatting",null, null);
        deleterow = db.delete("TimeTable",null, null);
        deleterow = db.delete("SyncUPQueue",null, null);
        deleterow = db.delete("Attendance",null, null);
        deleterow = db.delete("Query",null, null);
        deleterow = db.delete("Holiday",null, null);
        deleterow = db.delete("GiveStar",null, null);
        deleterow = db.delete("Homework",null, null);
        deleterow = db.delete("InitiatedChat",null, null);
        deleterow = db.delete("EventMaster",null, null);
        deleterow = db.delete("EventImages",null, null);
        deleterow = db.delete("TeacherFullInfo",null, null);
        deleterow = db.delete("Notification",null, null);


    }

}
