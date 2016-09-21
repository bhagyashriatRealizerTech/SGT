package com.realizer.schoolgeine.teacher.forgotpassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SetMagicWordAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String uName, magicWord;
    Context myContext;
    private OnTaskCompleted callback;
    String jsonData;

    public SetMagicWordAsyncTaskGet(String uName, String magicWord, String jsonData,Context myContext, OnTaskCompleted cb)
    {
        this.uName = uName;
        this.magicWord = magicWord;
        this.myContext = myContext;
        this.callback = cb;
        this.jsonData = jsonData;
    }

    @Override
    protected void onPreExecute() {
       // super.onPreExecute();
     // dialog= ProgressDialog.show(myContext, "", "Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String my= Config.URL+"setTeacherMagicWord/"+ uName + "/" +magicWord;
        Log.d("URL", my);
        HttpGet httpGet = new HttpGet(my);
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("Status Code",""+statusCode);
            if(statusCode == 200)
            {

                HttpEntity entity = response.getEntity();
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

        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
       // dialog.dismiss();
        //Pass here result of async task
        QueueListModel obj1 = new QueueListModel();
        obj1.setType("SetMagicWord");
        obj1.setTime(jsonData);
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }

}
