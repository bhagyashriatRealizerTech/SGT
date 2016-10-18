package com.realizer.schoolgeine.teacher.homework.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
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
public class TeacherClassworkAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherHomeworkModel obj;
    Context myContext;
    String Userid;
    ArrayList<String> presence;
    ArrayList<String> absent;
    int precount, abcount;
    private OnTaskCompleted callback;
    String flag,accesstoken;

    public TeacherClassworkAsyncTaskPost(TeacherHomeworkModel o, Context myContext, OnTaskCompleted cb, String flag) {

        this.myContext = myContext;
        this.callback = cb;
        obj = o;
        this.flag = flag;

    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
       if(flag.equals("true")) {
           dialog = ProgressDialog.show(myContext, "", "Please Wait saving " + obj.getWork() + " ....");
       }

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "uploadClassWork";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");
        accesstoken = sharedpreferences.getString("AccessToken","");

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("SchoolCode",scode);
            jobj.put("UserId",sharedpreferences.getString("UidName",""));
            jobj.put("DeviceId",sharedpreferences.getString("DeviceId",""));
            String date = obj.getHwDate();
            String date1[] = date.split("/");
            String resdate = date1[1]+"/"+date1[0]+"/"+date1[2];
            jobj.put("cwDate",resdate);
            jobj.put("Std",obj.getStd());
            jobj.put("div",obj.getDiv());
            jobj.put("subject",obj.getSubject());
            jobj.put("givenBy",obj.getGivenBy());

            JSONArray arr = new JSONArray();
            arr.put(0,obj.getSharedLink());
           /* arr.put(0,encodedImage);
            arr.put(1,encodedImage);*/

            jobj.put("cwImage64Lst",arr);

            JSONArray arr1 = new JSONArray();
            arr1.put(0,obj.getHwTxtLst());

            /*arr1.put(1,"TEXT2");*/

            jobj.put("CwTxtLst",arr1);



           json = jobj.toString();

            Log.d("STRINGOP", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("AccessToken", accesstoken);

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
        obj1.setId(obj.getHid());
        obj1.setType("Homework");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
