package com.realizer.schoolgeine.teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.exceptionhandler.NetworkException;

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

public class LoginAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String uName, password;
    Context myContext;
    private OnTaskCompleted callback;
    String deviceID;

    public LoginAsyncTaskGet(String uName, String password, Context myContext, OnTaskCompleted cb)
    {
        this.uName = uName;
        this.password = password;
        this.myContext = myContext;
        this.callback = cb;
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        deviceID = sharedpreferences.getString("DeviceId","");
    }

    @Override
    protected void onPreExecute() {
       // super.onPreExecute();
     // dialog= ProgressDialog.show(myContext, "", "Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String my= Config.URL+"TeacherLogin/"+ uName + "/" +password+ "/" +deviceID;;
        //String my= Config.URL+"TeacherLogin/"+ uName + "/" +password;
        Log.d("URL", my);
        HttpGet httpGet = new HttpGet(my);
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
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
               /* if(!resultLogin.toString().equalsIgnoreCase("true"))
                {
                    StringBuilder exceptionString = new StringBuilder();
                    exceptionString.append("URL: "+my.toString()+"\nInput: Get Method");
                    exceptionString.append(resultLogin.toString());
                    NetworkException.insertNetworkException(myContext,exceptionString.toString());
                }*/

            }
            else
            {
               // Log.e("Error", "Failed to Login");
                StringBuilder exceptionString = new StringBuilder();
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                exceptionString.append("URL: "+my.toString()+"\nInput: Get Method\nException: ");

                while((line=reader.readLine()) != null)
                {
                    exceptionString.append(line);
                }

                NetworkException.insertNetworkException(myContext,exceptionString.toString());

            }
        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
            NetworkException.insertNetworkException(myContext, e.getMessage().toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
            NetworkException.insertNetworkException(myContext, e.getMessage().toString());
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
         callback.onTaskCompleted(stringBuilder.toString(),null);

    }

}
