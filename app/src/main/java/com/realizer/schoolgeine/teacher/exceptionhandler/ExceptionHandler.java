package com.realizer.schoolgeine.teacher.exceptionhandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.model.ExceptionModel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExceptionHandler implements
		Thread.UncaughtExceptionHandler {
	private final Context myContext;
	private final String LINE_SEPARATOR = "\n";

	public ExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

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
			n =0;
			n = qr.insertQueue(qr.getExceptionId(),"Exception","1",date);
		}


		Intent i = new Intent(myContext,AnotherActivity.class);
		i.putExtra("error", errorReport.toString());
		myContext.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

}