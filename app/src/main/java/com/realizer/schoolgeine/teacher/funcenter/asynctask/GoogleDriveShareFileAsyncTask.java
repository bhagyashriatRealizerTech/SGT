package com.realizer.schoolgeine.teacher.funcenter.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.exceptionhandler.NetworkException;
import com.realizer.schoolgeine.teacher.funcenter.model.GoogleDriveUploadClass;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterImageModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.io.IOException;

/**
 * Created by Bhagyashri on 10/14/2016.
 */
public class GoogleDriveShareFileAsyncTask extends AsyncTask<Void,Void,File>
{
    File filetemp = null;
    Drive servicetemp = null;
    private OnTaskCompleted callback;
    GoogleDriveUploadClass o;
    String sharedLink=null;
    String tempString;
    Context mcontext;
    public GoogleDriveShareFileAsyncTask(File file,Drive service,OnTaskCompleted cb,GoogleDriveUploadClass o,Context comtext) {

        filetemp = file;
        servicetemp = service;
        this.callback = cb;
        this.o = o;
        tempString = "notdone";
        mcontext =mcontext;
    }
    @Override
    protected File doInBackground(Void... params) {

        shareFile(filetemp,servicetemp);
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        //Toast.makeText(context, "in doInBackground of google drive", Toast.LENGTH_SHORT);
        QueueListModel obj1 = new QueueListModel();
        obj1.setId(Integer.valueOf(o.getGdID()));
        obj1.setType(o.getGdtype());
        obj1.setTime(sharedLink);
        callback.onTaskCompleted(tempString, obj1);

    }


    public void shareFile(final File file,Drive service)
    {
        final String fileId = file.getId();
        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
                NetworkException.insertNetworkException(mcontext, "UploadImageError" + e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                sharedLink = "https://drive.google.com/uc?id=" + fileId;
                tempString = "done";
                Log.d("SharableLink", "https://drive.google.com/uc?id=" + fileId);
            }
        };
        BatchRequest batch = service.batch();
        Permission userPermission = new Permission()
                .setType("anyone")
                .setValue("Anyone")
                .setRole("writer");
        try {
            service.permissions().insert(fileId, userPermission)
                    .setFields("id")
                    .queue(batch, callback);

            batch.execute();
        }
        catch (IOException e) {
            e.printStackTrace();
            NetworkException.insertNetworkException(mcontext, "UploadImageError" + e.getMessage().toString());
        }

    }
}