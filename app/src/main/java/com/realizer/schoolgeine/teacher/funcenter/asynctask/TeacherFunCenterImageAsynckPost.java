package com.realizer.schoolgeine.teacher.funcenter.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterImageModel;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Win on 02/05/2016.
 */
public class TeacherFunCenterImageAsynckPost extends AsyncTask<Void, Void,StringBuilder>
{


    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherFunCenterImageModel obj ;
    Context myContext;
    private OnTaskCompleted callback;
    String flag;
    String standard,division;

    public TeacherFunCenterImageAsynckPost(TeacherFunCenterImageModel o,String standard,String division, Context myContext, OnTaskCompleted cb, String flag)
    {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
        this.flag = flag;
        this.standard=standard;
        this.division=division;
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
        String url = Config.URL + "AddEventImage";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");

        File file = ImageStorage.getEventImage(obj.getImage());
        String imagebse64= "";
        if(file != null) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(obj.getImage(), bmOptions);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1); //bm is the bitmap object
            byte[] b1 = baos1.toByteArray();
            imagebse64 = Base64.encodeToString(b1, Base64.DEFAULT);
        }

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("SchoolCode",scode);
            jobj.put("AcademicYear",obj.getAcademic_year());
            jobj.put("Std",standard);
            jobj.put("Div",division);
            jobj.put("EventId",obj.getEventuuid());
            jobj.put("ImageId",obj.getImguuid());
            jobj.put("ImageCaption","no thsaredVP Image");
            jobj.put("SrNo", obj.getSrno());
            String date = obj.getUpload_Date();
            jobj.put("uploadDate",date);
            jobj.put("Base64Image",obj.getSharedlink());
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
        obj1.setId(Integer.valueOf(obj.getImageid()));
        obj1.setType("EventImages");
        callback.onTaskCompleted(stringBuilder.toString(),obj1);

    }
}
