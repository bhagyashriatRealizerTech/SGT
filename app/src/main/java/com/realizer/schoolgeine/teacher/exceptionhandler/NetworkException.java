package com.realizer.schoolgeine.teacher.exceptionhandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.model.ExceptionModel;
import com.realizer.schoolgeine.teacher.mailsender.MailSender;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Bhagyashri on 11/17/2016.
 */
public class NetworkException {


    public static void insertNetworkException(Context myContext,String stackTrace) {
    DatabaseQueries qr = new DatabaseQueries(myContext);
    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
    ExceptionModel obj = new ExceptionModel();
    obj.setUserId(sharedpreferences.getString("UidName",""));
    obj.setExceptionDetails(stackTrace.toString());
    obj.setDeviceModel(Build.MODEL);
    obj.setAndroidVersion(Build.VERSION.SDK);
    obj.setApplicationSource("Teacher");
    obj.setDeviceBrand(Build.BRAND);

    SimpleDateFormat df1 = new SimpleDateFormat("dd MMM hh:mm:ss a");
    String date = df1.format(Calendar.getInstance().getTime());

    long n = qr.insertException(obj);

    if(n>0)
    {
        n = 0;
        n = qr.insertQueue(qr.getExceptionId(), "Exception", "1", date);
    }

    if(Config.isConnectingToInternet(myContext))
        sendEmail(obj);


  }


    public static void sendEmail(final ExceptionModel obj)
    {
        new Thread(new Runnable() {

            public void run() {

                try {

                    String messageContent = "Application Source: "+obj.getApplicationSource()
                            +"\nDevice Model: "+obj.getDeviceModel()+"\nAndroid Version: "+obj.getAndroidVersion()
                            +"\nDevice Brand: "+obj.getDeviceBrand()+"\nUserID: "+obj.getUserId()
                            +"\nException: "+obj.getExceptionDetails();

                    String TO = "bhagyashri.salgare@realizertech.com,sachin.shinde@realizertech.com";

                    MailSender sender = new MailSender("realizertech1@gmail.com","realizer@15");

                   // sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");

                    sender.sendMail("Critical Network Error: Teacher App",messageContent,"realizertech1@gmail.com",TO);

                } catch (Exception e) {

                    Log.d("Exception Mail",e.toString());
                   // Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();

                }

                               }

        }).start();
    }
}
