package com.realizer.schoolgeine.teacher.funcenter.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterEventModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

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

/**
 * Created by Win on 1/4/2016.
 */
public class TeacherFunCenterAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherFunCenterEventModel obj;
    Context myContext;
    private OnTaskCompleted callback;
    String flag;

    public TeacherFunCenterAsyncTaskPost(TeacherFunCenterEventModel o, Context myContext, OnTaskCompleted cb, String flag) {

        this.myContext = myContext;
        this.callback = cb;
        obj = o;
        this.flag = flag;

    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
       if(flag.equals("true"))
       {
           dialog = ProgressDialog.show(myContext, "", "Please Wait saving  ....");
       }

    }

    @Override
    protected StringBuilder doInBackground(Void... params)
    {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "AddSchoolEvent";
        HttpPost httpPost = new HttpPost(url);
        System.out.println(url);
        String json = "";
        StringEntity se = null;
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");
        JSONObject jobj = new JSONObject();

        try
        {
            jobj.put("SchoolCode",scode);
            jobj.put("AcademicYear",obj.getAcademicYear());
            jobj.put("Std",obj.getStd());
            jobj.put("Div",obj.getDiv());
            jobj.put("EventId",obj.getEventUUID());
            jobj.put("EventName",obj.getEventName());
            String date = obj.getEventDate();
            jobj.put("EventDate",date);
            jobj.put("ThumbNailImage",obj.getThumbNailImage());
            jobj.put("fileName",obj.getFilename());

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
        obj1.setId(obj.getEventId());
        obj1.setType("EventMaster");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
