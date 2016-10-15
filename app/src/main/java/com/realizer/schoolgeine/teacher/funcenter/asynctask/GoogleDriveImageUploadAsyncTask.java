package com.realizer.schoolgeine.teacher.funcenter.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.funcenter.model.GoogleDriveUploadClass;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterImageModel;

import java.util.Arrays;

/**
 * Created by Bhagyashri on 10/14/2016.
 */
public class GoogleDriveImageUploadAsyncTask extends AsyncTask<Void,Void,File>
{
    private Drive mService = null;
    private Exception mLastError = null;
    OnTaskCompleted cb;
    GoogleDriveUploadClass o;
    Activity activity;
    GoogleAccountCredential credential;

    public GoogleDriveImageUploadAsyncTask(GoogleAccountCredential credential,OnTaskCompleted cb,GoogleDriveUploadClass o) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        this.cb = cb;
        this.o = o;
        activity = Singlton.getActivity();
        this.credential = credential;
        mService = new Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Teacher")
                        .build();

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(file != null)

            new GoogleDriveShareFileAsyncTask(file,mService,cb,o).execute();
    }

    @Override
    protected File doInBackground(Void... params) {

        File file = null;
        String filename1[] = o.getGdfilename().split(".");
        try {
            file = insertFile(mService,o.getGdfilename(),"", o.getFoldername(),"image/jpeg",filename1[0]);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            file = null;
        }
        return file;
    }

    /**
     * Insert new file.
     *
     * @param service Drive API service instance.
     * @param title Title of the file to insert, including the extension.
     * @param description Description of the file to insert.
     * @param parentId Optional parent folder's ID.
     * @param mimeType MIME type of the file to insert.
     * @param filename Filename of the file to insert.
     * @return Inserted file metadata if successful, {@code null} otherwise.
     */
    private  File insertFile(Drive service, String title, String description,
                             String parentId, String mimeType, String filename) {
        // File's metadata.
        File file = null;
        filename = o.getGdfilename();
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);
        body.setShareable(true);
        // Set the parent folder.
        if (parentId != null && parentId.length() > 0) {
            body.setParents(
                    Arrays.asList(new ParentReference().setId(parentId)));
        }

        // File's content.
        java.io.File fileContent = new java.io.File(filename);
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        try {
            file = service.files().insert(body, mediaContent).execute();

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: " + file.getId());

            //return file;
        }
        catch (UserRecoverableAuthIOException e){

            activity.startActivityForResult(e.getIntent(), DrawerActivity.REQUEST_AUTHORIZATION);
        }

        catch (Exception e) {

            System.out.println("An error occured: " + e);
            file= null;
        }
        Log.d("Upload Image", file.toString());
        return  file;
    }


}
