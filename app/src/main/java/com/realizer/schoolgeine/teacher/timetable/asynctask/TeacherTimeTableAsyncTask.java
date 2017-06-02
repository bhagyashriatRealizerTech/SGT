package com.realizer.schoolgeine.teacher.timetable.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.exceptionhandler.NetworkException;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by shree on 8/26/2016.
 */
public class TeacherTimeTableAsyncTask extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherTimeTableExamListModel obj;
    Context myContext;
    String Userid;
    ArrayList<String> presence;
    ArrayList<String> absent;
    int precount, abcount;
    private OnTaskCompleted callback;
    String flag;

    public TeacherTimeTableAsyncTask(TeacherTimeTableExamListModel o, Context myContext, OnTaskCompleted cb, String flag) {

        this.myContext = myContext;
        this.callback = cb;
        obj = o;
        this.flag = flag;

    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        if(flag.equals("true")) {
            dialog = ProgressDialog.show(myContext, "", "Please Wait saving ....");
        }

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "AddTimeTable";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");

        String tempDate[] = obj.getDate().split("/");
        String date = tempDate[1]+"/"+tempDate[0]+"/"+tempDate[2];
        int currentyear = Calendar.getInstance().get(Calendar.YEAR);

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("schoolCode",scode);
            jobj.put("AcademicYr",currentyear);
            jobj.put("Std",obj.getStandard());
            jobj.put("division",obj.getDivision());
            jobj.put("TimeTableText",obj.getTitle());
            jobj.put("Description",obj.getDescription());
            jobj.put("UploadDate",date);
            jobj.put("TimeTableImage",obj.getSharedLink());
            jobj.put("UploadedBy",obj.getTeacher());
            jobj.put("fileName","Sample.jpg");


            json = jobj.toString();

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

                if(!resultLogin.toString().equalsIgnoreCase("true"))
                {
                    StringBuilder exceptionString = new StringBuilder();
                    exceptionString.append("URL: "+url.toString()+"\nInput: "+jobj.toString()+"\nException: ");
                    exceptionString.append(resultLogin.toString());
                    NetworkException.insertNetworkException(myContext,exceptionString.toString());
                }

            } else {
                // Log.e("Error", "Failed to Login");

                StringBuilder exceptionString = new StringBuilder();
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                exceptionString.append("URL: "+url.toString()+"\nInput: "+jobj.toString()+"\nException: ");

                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext, exceptionString.toString());

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
        obj1.setId(Integer.valueOf(obj.getTtid()));
        obj1.setType("TimeTable");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
