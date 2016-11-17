package com.realizer.schoolgeine.teacher.star.asynctask;

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
import com.realizer.schoolgeine.teacher.star.model.TeacherGiveStarModel;

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
public class TeacherGiveStarAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherGiveStarModel obj ;
    Context myContext;
    private OnTaskCompleted callback;
    String flag;

    public TeacherGiveStarAsyncTaskPost(TeacherGiveStarModel o, Context myContext, OnTaskCompleted cb, String flag) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
        this.flag=flag;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        if(flag.equals("true")) {
            dialog= ProgressDialog.show(myContext, "", "Please Wait ...");
        }
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"RateStar";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);



        String json = "";
       /* Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());*/
        StringEntity se = null;
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String accesstoken = sharedpreferences.getString("AccessToken", "");
        JSONObject jsonobj = new JSONObject();
        try {


            jsonobj.put("StartId",obj.getGivestarid());
            jsonobj.put("TeacherLoginId",obj.getTeacheruid());
            jsonobj.put("StudentLoginId",obj.getStuduid());
            jsonobj.put("Comment",obj.getComment());
            jsonobj.put("star",obj.getStar());
            String date = obj.getStardate();
            String date1[] = date.split("/");
            String resdate = date1[1]+"/"+date1[0]+"/"+date1[2];
            jsonobj.put("StarDate",resdate);
            jsonobj.put("Subject",obj.getSubject());
            jsonobj.put("UserId",sharedpreferences.getString("UidName",""));

            json = jsonobj.toString();
            Log.d("STARRES", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("AccessToken", accesstoken);

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

                if(!resultLogin.toString().equalsIgnoreCase("true"))
                    NetworkException.insertNetworkException(myContext, resultLogin.toString());

            }
            else
            {
                // Log.e("Error", "Failed to Login");

                StringBuilder exceptionString = new StringBuilder();
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext, exceptionString.toString());

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
        obj1.setId(Integer.valueOf(obj.getGivestarid()));
        obj1.setType("GiveStar");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
