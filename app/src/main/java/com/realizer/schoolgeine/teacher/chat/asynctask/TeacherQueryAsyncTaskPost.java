package com.realizer.schoolgeine.teacher.chat.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.NetworkException;
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
public class TeacherQueryAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherQuerySendModel obj ;
    Context myContext;
    private OnTaskCompleted callback;
    String flag;

    public TeacherQueryAsyncTaskPost(TeacherQuerySendModel o, Context myContext, OnTaskCompleted cb, String flag) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
        this.flag = flag;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();

       // dialog=ProgressDialog.show(myContext,"","Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"AddConversation";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);



        String json = "";
       /* Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());*/
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {


            jsonobj.put("SchoolCode",obj.getSchoolCode());
            jsonobj.put("ConversationId",obj.getConversationId());
            jsonobj.put("fromTeacher",obj.getFromTeacher());
            jsonobj.put("from",obj.getFrom());
            jsonobj.put("to",obj.getTo());
            jsonobj.put("text",obj.getText());
            String date = obj.getSentTime();
            String date1[] = date.split("/");
            String resdate = date1[1]+"/"+date1[0]+"/"+date1[2];
            jsonobj.put("sentTime",resdate);

            json = jsonobj.toString();
            Log.d("RES", json);
            se = new StringEntity(json);
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

                if(!resultLogin.toString().equalsIgnoreCase("true"))
                {
                    StringBuilder exceptionString = new StringBuilder();
                    exceptionString.append("URL: "+url.toString()+"\nInput: "+jsonobj.toString()+"\nException: ");
                    exceptionString.append(resultLogin.toString());
                    NetworkException.insertNetworkException(myContext,exceptionString.toString());
                }

            }
            else
            {
                StringBuilder exceptionString = new StringBuilder();
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                exceptionString.append("URL: "+url.toString()+"\nInput: "+jsonobj.toString()+"\nException: ");

                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext,exceptionString.toString());
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
//        dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        QueueListModel obj1 = new QueueListModel();
        obj1.setId(Integer.valueOf(obj.getConversationId()));
        obj1.setType("Query");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
