package com.realizer.schoolgeine.teacher.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;

/**
 * Created by Win on 12/21/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SchoolDiaryTeacher";
    private static final int DATABASE_VERSION =45;
    static Context mycontext;
    private static SqliteHelper mInstance = null;
    private static final String STUDINFO ="CREATE TABLE StudInfo(Std TEXT,Div TEXT, StudArr TEXT)";
    private static final String STDSUBALLOCATE ="CREATE TABLE StdDivSub(Std TEXT,Div TEXT, Sub TEXT)";
    private static final String TEACHERINFO ="CREATE TABLE TeacherInfo(Id TEXT,DispName TEXT, Pic TEXT)";
    private static final String STDSUBJECT ="CREATE TABLE StdSubject(Std TEXT,Subject TEXT)";
    private static final String Announcement ="CREATE TABLE Announcement(Year TEXT,AnnounceID INTEGER PRIMARY KEY   AUTOINCREMENT,Std TEXT,Div TEXT,SDate Text,Message Text,SendBy TEXT,Category TEXT,HasSyncedUp TEXT)";
    private static final String Chatting ="CREATE TABLE Chatting(Year TEXT,ChatID INTEGER PRIMARY KEY   AUTOINCREMENT,SDate Text,Message Text,SendBy TEXT,SendTo TEXT,HasSyncedUp TEXT)";
    private static final String SyncUPQueue ="CREATE TABLE SyncUPQueue(Id INTEGER,Type TEXT,SyncPriority TEXT,Time TEXT)";
    private static final String Attendance ="CREATE TABLE Attendance(AttendanceId INTEGER PRIMARY KEY   AUTOINCREMENT,attendanceDate TEXT,SchoolCode TEXT,Std TEXT,Div TEXT,AttendanceBy TEXT,Attendees TEXT,Absenties TEXT,AttendanceCnt INTEGER,AbsentCnt TEXT,HasSyncedUp TEXT)";
    private static final String Query ="CREATE TABLE Query(QueryId INTEGER PRIMARY KEY   AUTOINCREMENT,fromTeacher TEXT,sendfrom TEXT,sendto TEXT,msg TEXT,sentTime TEXT,sentDate INTEGER,HasSyncedUp TEXT,ThumbnailUrl TEXT,MsgSenderName TEXT)";
    private static final String Holiday ="CREATE TABLE Holiday(Id INTEGER PRIMARY KEY   AUTOINCREMENT,holiday TEXT,hsdate TEXT,hedate TEXT)";
    private static final String GiveStar ="CREATE TABLE GiveStar(GiveStarId INTEGER PRIMARY KEY   AUTOINCREMENT,TeacherLoginId TEXT,StudentLoginId TEXT,Comment TEXT,star TEXT,StarDate TEXT,Subject TEXT,Std TEXT,Div TEXT,StarTime TEXT,HasSyncedUp TEXT)";
    private static final String Homework ="CREATE TABLE Homework(HomeworkId INTEGER PRIMARY KEY   AUTOINCREMENT,Std Text,Div Text,subject TEXT,textlst TEXT,Imglst TEXT,Givenby TEXT,hwDate TEXT,HasSyncedUp TEXT,Work TEXT,ShredLink TEXT)";
    private static final String InitiatedChat ="CREATE TABLE InitiatedChat(Id INTEGER,Useranme TEXT,Initiated TEXT,STD TEXT,Div TEXT,Uid TEXT,UnreadCount INTEGER,ThumbnailUrl TEXT)";
    private static final String TIMETABLE ="CREATE TABLE TimeTable(TTId INTEGER PRIMARY KEY   AUTOINCREMENT,Std Text,Div Text,TmTbleName TEXT,Imglst TEXT,Givenby TEXT,TTDate TEXT,HasSyncedUp TEXT,Description TEXT)";
    private static final String FunCenter ="CREATE TABLE EventMaster(Class TEXT,Div TEXT,Event_Id INTEGER PRIMARY KEY   AUTOINCREMENT,Event TEXT,Date TEXT,Thumbnail TEXT,Create_Date TEXT,HasSyncedUp TEXT,AcademicYear INTEGER,EvntuuId TEXT,FileName TEXT,ShredLink TEXT)";
    private static final String FunCenter1 ="CREATE TABLE EventImages(Image_id INTEGER PRIMARY KEY   AUTOINCREMENT,Eventid INTEGER ,EventUUID TEXT,Image TEXT,Upload_Date  TEXT,Is_Uploaded TEXT,HasSyncedUp TEXT,AcademicYear INTEGER,SrNo INTEGER,ImageCaption TEXT,Imguuid TEXT,File_Name TEXT,ShredLink TEXT)";
    private static final String TEACHERFULLINFO ="CREATE TABLE TeacherFullInfo(ActiveDate TEXT,ClassTeacherOn TEXT,Name TEXT,Qualification TEXT,ThumbnailURL TEXT,UserId TEXT,ContactNo TEXT,DOB TEXT,EmailId TEXT,IsActive TEXT)";
    private static final String Notification ="CREATE TABLE Notification(ID INTEGER PRIMARY KEY   AUTOINCREMENT,NotificationId INTEGER,Type TEXT,Message TEXT,Date TEXT,AdditionalData1 TEXT,AdditionalData2 TEXT,IsRead TEXT)";
    private static final String ExceptionHandler = "CREATE TABLE Exception(ExceptionId INTEGER PRIMARY KEY   AUTOINCREMENT,UserId TEXT,ExceptionDetails TEXT,DeviceModel TEXT,AndroidVersion TEXT,ApplicationSource TEXT,DeviceBrand TEXT,HasSyncedUp TEXT)";

    private SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(context));
        this.mycontext = context;
    }

    public static SqliteHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SqliteHelper(ctx.getApplicationContext());
        }
        mycontext = ctx;
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(STUDINFO);
        db.execSQL(STDSUBALLOCATE);
        db.execSQL(TEACHERINFO);
        db.execSQL(STDSUBJECT);
        db.execSQL(Announcement);
        db.execSQL(Chatting);
        db.execSQL(SyncUPQueue);
        db.execSQL(Attendance);
        db.execSQL(Query);
        db.execSQL(Holiday);
        db.execSQL(GiveStar);
        db.execSQL(Homework);
        db.execSQL(InitiatedChat);
        db.execSQL(FunCenter);
        db.execSQL(FunCenter1);
        db.execSQL(TIMETABLE);
        db.execSQL(TEACHERFULLINFO);
        db.execSQL(Notification);
        db.execSQL(ExceptionHandler);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE if exists " + "StudInfo");
        db.execSQL("DROP TABLE if exists " + "StdDivSub");
        db.execSQL("DROP TABLE if exists " + "TeacherInfo");
        db.execSQL("DROP TABLE if exists " + "StdSubject");
        db.execSQL("DROP TABLE if exists " + "Announcement");
        db.execSQL("DROP TABLE if exists " + "Chatting");
        db.execSQL("DROP TABLE if exists " + "SyncUPQueue");
        db.execSQL("DROP TABLE if exists " + "Attendance");
        db.execSQL("DROP TABLE if exists " + "Query");
        db.execSQL("DROP TABLE if exists " + "Holiday");
        db.execSQL("DROP TABLE if exists " + "GiveStar");
        db.execSQL("DROP TABLE if exists " + "Homework");
        db.execSQL("DROP TABLE if exists " + "InitiatedChat");
        db.execSQL("DROP TABLE if exists " + "EventMaster");
        db.execSQL("DROP TABLE if exists " + "EventImages");
        db.execSQL("DROP TABLE if exists " + "TimeTable");
        db.execSQL("DROP TABLE if exists " + "TeacherFullInfo");
        db.execSQL("DROP TABLE if exists " + "Notification");
        db.execSQL("DROP TABLE if exists " + "Exception");
        onCreate(db);
    }
}
