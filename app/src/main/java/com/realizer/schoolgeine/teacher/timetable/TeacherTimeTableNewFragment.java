package com.realizer.schoolgeine.teacher.timetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.homework.newhomework.NewHomeworkActivity;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Win on 11/25/2015.
 */
public class TeacherTimeTableNewFragment extends Fragment implements View.OnClickListener,OnTaskCompleted , FragmentBackPressedListener {

    // LogCat tag
    private static final String TAG = NewHomeworkActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    DatabaseQueries qr ;
    private Uri fileUri; // file url to store image/video
    private EditText timetableName,description;
    private ImageView imgsyllabus;
    String givenby;
    MenuItem search;
    TextView txtstd,txtclss;
    int ttid=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_newtimetable_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Add Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        imgsyllabus = (ImageView) rootView.findViewById(R.id.imgexamsyllabus);
        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        timetableName  = (EditText) rootView.findViewById(R.id.edtTTableName);
        description = (EditText) rootView.findViewById(R.id.edtTTableDescription);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
         givenby = preferences.getString("UidName", "");
        setHasOptionsMenu(true);

        /**
         * Capture image button click event
         */
        qr = new DatabaseQueries(getActivity());
        imgsyllabus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                getOption();
            }
        });


        return rootView;
    }

    public void saveTimeTable()
    {
        String encodedImage="";

        if (timetableName.getText().toString().equals(""))
        {
            Config.alertDialog(Singlton.getContext(), "Time Table", "Please Enter Title");
            //Toast.makeText(getActivity(),"Please enter title", Toast.LENGTH_LONG).show();
            //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.TimetableEnter)).toString());
        }
        else
        if (description.getText().toString().equals(""))
        {
            Config.alertDialog(Singlton.getContext(), "Time Table", "Please Enter Description");
           // Toast.makeText(getActivity(), "Please Enter Description", Toast.LENGTH_LONG).show();
            //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.Timetabledescription)).toString());
        }
        if (imgsyllabus.getDrawable() == null)
        {
            Config.alertDialog(Singlton.getContext(), "Time Table", "Please Add Image");
           // Toast.makeText(getActivity(), "Please insert image", Toast.LENGTH_LONG).show();
        }
        else {
            Bitmap bitmap = ((BitmapDrawable)imgsyllabus.getDrawable()).getBitmap();

            encodedImage = ImageStorage.saveEventToSdCard(bitmap, "TimeTable", getActivity());

            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar1 = Calendar.getInstance();

            long n = qr.insertTimeTable(txtstd.getText().toString(), txtclss.getText().toString(), timetableName.getText().toString(), encodedImage, givenby, df2.format(calendar1.getTime()), description.getText().toString());

            TeacherTimeTableExamListModel tobj=new TeacherTimeTableExamListModel();
            tobj.setStandard(txtstd.getText().toString());
            tobj.setDivision(txtclss.getText().toString());
            tobj.setTitle(timetableName.getText().toString());
            tobj.setImage(encodedImage);
            tobj.setTeacher(givenby);
            tobj.setDate(df2.format(calendar1.getTime()));
            tobj.setDescription(description.getText().toString());

            if (n > 0) {
                // Toast.makeText(getActivity(), "Homework Inserted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;
                ttid = qr.getTimeTableId();
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM hh:mm:ss a");
                Calendar calendar = Calendar.getInstance();
                n = qr.insertQueue(ttid, "TimeTable", "1", df1.format(calendar.getTime()));
                if (n > 0) {
                    // Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                    n = -1;
                   /* if (isConnectingToInternet()) {

                        TeacherTimeTableAsyncTask obj = new TeacherTimeTableAsyncTask(tobj, getActivity(), TeacherTimeTableNewFragment.this, "true");
                        obj.execute();
                    } else {*/
                    TeacherTimeTableFragment fragment = new TeacherTimeTableFragment();
                    Singlton.setSelectedFragment(fragment);
                    Singlton.setMainFragment(fragment);
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    bundle.putInt("Checked", 1);
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    //}
                }
            }
            timetableName.setText("");
            description.setText("");
            imgsyllabus.setImageResource(android.R.color.transparent);
        }
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //Option camera or gallery

    public void getOption() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Choose Action");

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(data);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

                // user cancelled Image capture
               /* Toast.makeText(getActivity(), "user cancle action", Toast.LENGTH_LONG).show();*/
               // Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.CanceledImage)).toString());

            } else {
                // failed to capture image
                Config.alertDialog(Singlton.getContext(), "Camera", "Failed to Capture Image");
                //Toast.makeText(getActivity(), "failed to capture image", Toast.LENGTH_LONG).show();
                //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.FailedImage)).toString());
            }
        }
    }


    private void launchUploadActivity(Intent data){

        if(data.getData()!=null)
        {
            try
            {
                if (bitmap != null)
                {
                    //bitmap.recycle();
                }

                InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                imgsyllabus.setScaleType(ImageView.ScaleType.FIT_XY);
                imgsyllabus.setImageBitmap(bitmap);

            }

            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            bitmap=(Bitmap) data.getExtras().get("data");

            imgsyllabus.setScaleType(ImageView.ScaleType.FIT_XY);
            imgsyllabus.setImageBitmap(bitmap);

        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    public void onClick(View v) {

    }


    //Encode image to Base64 to send to server
    private void setPhoto(Bitmap bitmapm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] byteArrayImage = baos.toByteArray();
            String imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                Config.hideSoftKeyboardWithoutReq(getActivity(),timetableName);
                SwitchClass();
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(),timetableName);
                saveTimeTable();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",6);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }


    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
        s =s.replace("\"","");
        if(s.equals("true"))
        {
            //Toast.makeText(getActivity()," AsyncTask called Successfully", Toast.LENGTH_SHORT).show();
            long n = qr.deleteQueueRow(ttid,"TimeTable");
            if(n>0) {
                 // Toast.makeText(getActivity(), "Queue Deleted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;

                n = qr.updateTimeTableSyncFlag(qr.GetTimeTable(ttid));
                if(n>0) {
                    TeacherTimeTableFragment fragment = new TeacherTimeTableFragment();
                    Singlton.setSelectedFragment(fragment);
                    Singlton.setMainFragment(fragment);
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    bundle.putInt("Checked", 1);
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    n = -1;
                   // Toast.makeText(getActivity(), "Updated TimeTable Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
