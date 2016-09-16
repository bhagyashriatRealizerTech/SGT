package com.realizer.schoolgeine.teacher.myclass.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherAttendanceListModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Win on 1/4/2016.
 */
public class TeacherAttendanceAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherAttendanceListModel obj;
    Context myContext;
    String Userid;
    ArrayList<String> presence;
    ArrayList<String> absent;
    int precount, abcount;
    private OnTaskCompleted callback;
    String flag;

    public TeacherAttendanceAsyncTaskPost(TeacherAttendanceListModel o, Context myContext, OnTaskCompleted cb, String flag) {

        this.myContext = myContext;
        this.callback = cb;
        obj=o;
        this.flag=flag;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
         if(flag.equals("true")) {
             dialog = ProgressDialog.show(myContext, "", "Please Wait Saving Attendance...");
         }
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "AddAttendance";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {

            jsonobj.put("SchoolCode",scode);
            jsonobj.put("AttendanceId",obj.getAttendanceId());
            String date = obj.getAttendanceDate();
            String date1[] = date.split("/");
            String resdate = date1[1]+"/"+date1[0]+"/"+date1[2];
            jsonobj.put("attendanceDate",resdate);
            jsonobj.put("Std",obj.getStd());
            jsonobj.put("Div",obj.getDiv());
            jsonobj.put("AttendanceBy",obj.getAttendanceBy());
            jsonobj.put("PresenceCnt",obj.getPresenceCnt());
            jsonobj.put("AbsentCnt",obj.getAbsentCnt());

            JSONArray presentarray = new JSONArray();
            JSONArray abscentarray = new JSONArray();

            String att = obj.getAttendees();
           if(att.length()>0) {
               String attarr[] = att.split(",,");

               for (int i = 0; i < attarr.length; i++) {
                   JSONObject obj = new JSONObject();
                   obj.put("SId", attarr[i]);
                   presentarray.put(i, obj);
               }
               jsonobj.put("Attendees",presentarray.toString());
           }
            else
           {
               jsonobj.put("Attendees",obj.getAttendees());
           }


            String abc = obj.getAbsenties();
            Log.d("LEN", "" + abc.length());
            if(abc.length()>0) {
                String abcarr[] = abc.split(",,");

                for (int i = 0; i < abcarr.length; i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("SId", abcarr[i]);
                    abscentarray.put(i, obj);
                }
                jsonobj.put("Absenties",abscentarray.toString());
            }
            else
            {
                jsonobj.put("Absenties",obj.getAbsenties());
            }



            json = jsonobj.toString();

            Log.d("STRINGOP", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    resultLogin.append(line);
                }

            } else {
                // Log.e("Error", "Failed to Login");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        if(flag.equals("true")) {
            dialog.dismiss();
        }

        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        QueueListModel obj1 = new QueueListModel();
        obj1.setId(obj.getAttendanceId());
        obj1.setType("Attendance");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
