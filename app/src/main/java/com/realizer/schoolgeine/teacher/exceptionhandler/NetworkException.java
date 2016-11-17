package com.realizer.schoolgeine.teacher.exceptionhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.model.ExceptionModel;

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

  }
}
