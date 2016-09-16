package com.realizer.schoolgeine.teacher.generalcommunication.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGeneralCommunicationListModel;
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
public class TeacherGCommunicationAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherGeneralCommunicationListModel obj ;
    Context myContext;
    private OnTaskCompleted callback;
    String flag;

    public TeacherGCommunicationAsyncTaskPost(TeacherGeneralCommunicationListModel o, Context myContext, OnTaskCompleted cb, String flag) {
        obj=o;
        this.myContext = myContext;
        this.callback = cb;
        this.flag=flag;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();

        if(flag.equals("true")) {
            dialog = ProgressDialog.show(myContext, "", "Please Wait...");
        }

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"AddAnnouncement";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);
        TeacherGeneralCommunicationListModel objo =obj;

        int pk = obj.getAnnouncementId();
        String year= obj.getAcademicYr();
        String std= obj.getStd();
        String div = obj.getDivision();
        String date = obj.getAnnouncementTime();
        String msg = obj.getAnnouncementText();
        String category = obj.getCategory();
        String flag = obj.getSyncUpFlag();
        String by = obj.getSentBy();

        Log.d("Data", "" + pk + "\n" + year + "\n" + std + "\n" + div + "\n" + date + "\n" + msg + "\n" + category + "\n" + by + "\n" + flag);

        String json = "";
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            //jsonobj.put("Acc",obj);
            jsonobj.put("SchoolCode",obj.getSchoolCode());
            jsonobj.put("AnnouncementId",obj.getAnnouncementId());
            jsonobj.put("Std",obj.getStd());
            jsonobj.put("division",obj.getDivision());
            jsonobj.put("AcademicYr",obj.getAcademicYr());
            jsonobj.put("AnnouncementText",obj.getAnnouncementText());
            jsonobj.put("Category",obj.getCategory());
            //jsonobj.put("AnnouncementTime",obj.getAnnouncementTime());
            jsonobj.put("sentBy",obj.getSentBy());

            json = jsonobj.toString();
            se = new StringEntity(json);
            Log.d("ANNOUNCEMENT", json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if(statusCode == 200)
            {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    resultLogin.append(line);
                }

            }
            else
            {
                // Log.e("Error", "Failed to Login");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
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
        obj1.setId(Integer.valueOf(obj.getAnnouncementId()));
        obj1.setType("Announcement");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
