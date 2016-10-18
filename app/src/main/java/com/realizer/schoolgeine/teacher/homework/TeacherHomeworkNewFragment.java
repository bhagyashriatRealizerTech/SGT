/*
package com.realizer.schoolgeine.teacher.homework;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class TeacherHomeworkNewFragment extends Fragment implements View.OnClickListener ,OnTaskCompleted, FragmentBackPressedListener {

    // LogCat tag
    private static final String TAG = TeacherHomeworkNewFragment.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    int cpature1,cpature2,cpature3;
    int hid = 0;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    Spinner spinnersub, spinnerdate;
    DatabaseQueries qr ;
    TextView txtstd,txtclss;
    EditText edthwork;
    String htext;

    ArrayList<String> listofDate = new ArrayList<>();
    ArrayList<String> listofDay = new ArrayList<>();
    MenuItem search;

    private Uri fileUri; // file url to store image/video

    private ImageView btnCapturePicture, btnCapturePicture1,btnCapturePicture2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_newhomework_layout, container, false);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setHasOptionsMenu(true);

        btnCapturePicture = (ImageView) rootView.findViewById(R.id.btnCapturePicture1);
        btnCapturePicture1 = (ImageView) rootView.findViewById(R.id.btnCapturePicture2);
        btnCapturePicture2 = (ImageView) rootView.findViewById(R.id.btnCapturePicture3);

        spinnersub = (Spinner) rootView.findViewById(R.id.spinnersub);
        spinnerdate = (Spinner) rootView.findViewById(R.id.spinnerdate);
        edthwork = (EditText) rootView.findViewById(R.id.edtthomework);

        cpature1=0;cpature2=0;cpature3=0;
        txtstd  = (TextView) rootView.findViewById(R.id.txtstdname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);


        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");
        edthwork.setHint(htext);


        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        qr = new DatabaseQueries(getActivity());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));



       FillSubjectTypes();
        FillDates();

        Config.hideSoftKeyboardWithoutReq(getActivity(), edthwork);
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                if (cpature1 != 2) {
                    cpature1 = 1;
                    getOption();
                }
                else {
                    open(btnCapturePicture);
                }
            }
        });
        btnCapturePicture1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                if (cpature2 != 2) {
                    cpature2 = 1;
                    getOption();
                }
                else {
                    open(btnCapturePicture1);
                }
            }
        });
        btnCapturePicture2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                if (cpature3 != 2) {
                    cpature3 = 1;
                    getOption();
                }
                else {
                    open(btnCapturePicture2);
                }
            }
        });


        return rootView;
    }

    public void saveHomework()
    {
        if(txtstd.getText().toString().isEmpty() )
        {
            Config.alertDialog(Singlton.getContext(), "New "+htext, "Please Select Standard");
           // Toast.makeText(getActivity(), "Please Select Standard", Toast.LENGTH_SHORT).show();
        }
        else if(txtclss.getText().toString().isEmpty())
        {
            Config.alertDialog(Singlton.getContext(), "New "+htext, "Please Select Division");
           // Toast.makeText(getActivity(), "Please Select Division", Toast.LENGTH_SHORT).show();
        }
        else if( spinnersub.getSelectedItem().toString().isEmpty())
        {
            Config.alertDialog(Singlton.getContext(), "New "+htext, "Please Select Subject");
           // Toast.makeText(getActivity(), "Please Select Subject", Toast.LENGTH_SHORT).show();
        }
        else if( spinnerdate.getSelectedItem().toString().isEmpty())
        {
            Config.alertDialog(Singlton.getContext(), "New "+htext, "Please Select Date");
           // Toast.makeText(getActivity(), "Please Select Date", Toast.LENGTH_SHORT).show();
        }
        else if(edthwork.getText().toString().isEmpty())
        {
            Config.alertDialog(Singlton.getContext(), "New "+htext, "Please Enter "+htext+" Description");
            //Toast.makeText(getActivity(), "Please Enter Description", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            String sub = spinnersub.getSelectedItem().toString();
            ArrayList<String> imglst = GetImgLst();
            JSONArray imglstbase64 = new JSONArray();
            String txtlst = edthwork.getText().toString();
            String date = listofDate.get(spinnerdate.getSelectedItemPosition());
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String givenby = sharedpreferences.getString("UidName", "");

            for (int i = 0; i < imglst.size(); i++) {

                try {
                    imglstbase64.put(i, imglst.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            long n = qr.insertHomework(givenby, sub, date, txtlst, imglstbase64.toString(), txtstd.getText().toString(), txtclss.getText().toString(), htext);
            if (n > 0) {
                // Toast.makeText(getActivity(), "Homework Inserted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;

                hid = qr.getHomeworkId();
                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                n = qr.insertQueue(hid, "Homework", "1", df1.format(calendar.getTime()));
                if (n > 0) {
                    // Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                    n = -1;
if (isConnectingToInternet()) {
                        TeacherHomeworkModel o = qr.GetHomework(hid);
                        if (o.getWork().equalsIgnoreCase("Homework")) {
                            TeacherHomeworkAsyncTaskPost obj = new TeacherHomeworkAsyncTaskPost(o, getActivity(), TeacherHomeworkNewFragment.this, "true");
                            obj.execute();
                        } else if (o.getWork().equalsIgnoreCase("Classwork")) {
                            TeacherClassworkAsyncTaskPost obj = new TeacherClassworkAsyncTaskPost(o, getActivity(), TeacherHomeworkNewFragment.this, "true");
                            obj.execute();
                        }
                    } else {

                        TeacherHomeworkFragment fragment = new TeacherHomeworkFragment();
                        Singlton.setMainFragment(fragment);
                        Singlton.setSelectedFragment(fragment);
                        Bundle bundle = new Bundle();
                        bundle.putString("HEADERTEXT", htext);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragment.setArguments(bundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.frame_container, fragment);
                        fragmentTransaction.commit();
                       // Toast.makeText(getActivity(), "" + htext + " Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    //}
                }
            }
        }
    }


    public ArrayList<String> GetImgLst()
    {

        ArrayList<String> Test = new ArrayList<>();

        int i=0;

        if(cpature1==2)
        {
            Bitmap bitmap = ((BitmapDrawable)btnCapturePicture.getDrawable()).getBitmap();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Test.add(i,encodedImage);
            i= i+1;
        }
        if(cpature2==2)
        {
            Bitmap bitmap = ((BitmapDrawable)btnCapturePicture1.getDrawable()).getBitmap();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Test.add(i,encodedImage);
            i= i+1;

        }
        if(cpature3==2)
        {
            Bitmap bitmap = ((BitmapDrawable)btnCapturePicture2.getDrawable()).getBitmap();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Test.add(i,encodedImage);
            i= i+1;
        }

        return Test;
    }

*
     * Checking device has camera hardware or not


    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has search_layout camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //Option camera or gallery

public void getOption() {
    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
    galleryIntent.setType("image
");
    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);//
    //startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"),'a' );
   // galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);


    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    Intent chooser = new Intent(Intent.ACTION_CHOOSER);
    chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
    chooser.putExtra(Intent.EXTRA_TITLE, "Choose Action");

    Intent[] intentArray = {cameraIntent};

    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
    startActivityForResult(chooser, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
}
*
     * Launching camera app to capture image


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

*
     * Here we store the file url as it will be null after returning from camera
     * app



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
         if(data==null) {
             BitmapFactory.Options options = new BitmapFactory.Options();

             // down sizing image as it throws OutOfMemory Exception for larger
             // images
             options.inSampleSize = 8;
             final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
             Log.d("PATH", fileUri.getPath());
             setPhoto(bitmap);
             if(cpature1==1) {
                 btnCapturePicture.setImageBitmap(bitmap);
                 cpature1=2;
             }
             else if(cpature2==1) {
                 btnCapturePicture1.setImageBitmap(bitmap);
                 cpature2=2;
             }
             else if(cpature3==1) {
                 btnCapturePicture2.setImageBitmap(bitmap);
                 cpature3=2;
             }
             }
                else
             launchUploadActivity(data);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

                // user cancelled Image capture
  Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();


            } else {
                // failed to capture image
                Config.alertDialog(Singlton.getContext(), "Camera", "Sorry, Failed to Capture Image");
   Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();

            }

        }
    }


    private void launchUploadActivity(Intent data){
        ClipData clipData = data.getClipData();

        if(clipData != null)
        {
            try
            {
                int lengthCount = clipData.getItemCount();
                if(lengthCount==1)
                {
                    ClipData.Item item = clipData.getItemAt(0);
                    InputStream stream = getActivity().getContentResolver().openInputStream(item.getUri());
                    bitmap = BitmapFactory.decodeStream(stream);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }

                    stream.close();
                }
                else if(lengthCount==2)
                {
                    ClipData.Item item = clipData.getItemAt(0);
                    InputStream stream = getActivity().getContentResolver().openInputStream(item.getUri());
                    bitmap = BitmapFactory.decodeStream(stream);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }

                    ClipData.Item item1 = clipData.getItemAt(1);
                    InputStream stream1 = getActivity().getContentResolver().openInputStream(item1.getUri());
                    bitmap = BitmapFactory.decodeStream(stream1);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }
                    stream.close();

                }
                else if(lengthCount==3)
                {
                    ClipData.Item item = clipData.getItemAt(0);
                    InputStream stream = getActivity().getContentResolver().openInputStream(item.getUri());
                    bitmap = BitmapFactory.decodeStream(stream);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }

                    ClipData.Item item1 = clipData.getItemAt(1);
                    InputStream stream1 = getActivity().getContentResolver().openInputStream(item1.getUri());
                    bitmap = BitmapFactory.decodeStream(stream1);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }


                    ClipData.Item item2 = clipData.getItemAt(2);
                    InputStream stream2 = getActivity().getContentResolver().openInputStream(item2.getUri());
                    bitmap = BitmapFactory.decodeStream(stream2);
                    if(cpature1 != 2) {
                        btnCapturePicture.setImageBitmap(bitmap);
                        cpature1=2;
                    }
                    else if(cpature2 != 2){
                        btnCapturePicture1.setImageBitmap(bitmap);
                        cpature2=2;
                    }
                    else if(cpature3 != 2){
                        btnCapturePicture2.setImageBitmap(bitmap);
                        cpature3=2;
                    }
                    stream.close();

                }
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
       else if(data.getData()!=null)
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

                if(cpature1==1) {
                    btnCapturePicture.setImageBitmap(bitmap);
                    cpature1=2;
                }
                else if(cpature2==1) {
                    btnCapturePicture1.setImageBitmap(bitmap);
                    cpature2=2;
                }
                else if(cpature3==1) {
                    btnCapturePicture2.setImageBitmap(bitmap);
                    cpature3=2;
                }

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

        
    }



*
     * ------------ Helper Methods ----------------------
     *


*
     * Creating file uri to store image/video


    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

*
     * returning image / video


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

        // Create search_layout media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }


    @Override
    public void onClick(View v) {

    }


    //Encode image to Base64 to send to server
    private void setPhoto(Bitmap bitmapm) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");

            }
        }
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpeg");

            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    public void FillDates() {

        listofDate.clear();
        listofDay.clear();
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        int currentdate = c.get(Calendar.DATE);
        if(currentdate>=1 && currentdate<10) {
            if(month>=1 && month<10)
                listofDate.add(0, "0" + currentdate + "/0" + month + "/" + year);
            else
                listofDate.add(0, "0" + currentdate + "/" + month + "/" + year);
        }
        else
        {
            if(month>=1 && month<10)
                listofDate.add(0, "" + currentdate + "/0" + month + "/" + year);
            else
                listofDate.add(0, "" + currentdate + "/" + month + "/" + year);
        }
        listofDay.add(0,"Today"+" ("+currentdate+" "+Config.getMonth(month)+")");
        for (int i = 1; i <7; i++) {
            c.add(Calendar.DATE, 1);
            int month1 = c.get(Calendar.MONTH) + 1;
            int year1 = c.get(Calendar.YEAR);
            int currentdate1 = c.get(Calendar.DATE);
            if(currentdate1>=1 && currentdate1<10) {
                if(month1>=1 && month1<10)
                listofDate.add(i, "0" + currentdate1 + "/0" + month1 + "/" + year1);
                else
                    listofDate.add(i, "0" + currentdate1 + "/" + month1 + "/" + year1);
            }
            else {
                if(month1>=1 && month1<10)
                    listofDate.add(i, "" + currentdate1 + "/0" + month1 + "/" + year1);
                else
                    listofDate.add(i, "" + currentdate1 + "/" + month1 + "/" + year1);
            }

            listofDay.add(i, Config.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK)) + " (" +currentdate1+" "+ Config.getMonth(month1) + ")");
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listofDay);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        for (int i = 0; i < adapter.toString().length(); i++) {
            spinnerdate.setAdapter(adapter);
            break;
        }

        spinnerdate.setSelection(0);
    }

    public void FillSubjectTypes()
    {
        // set adapter

        ArrayList<String> listofSubject = qr.GetSub(txtstd.getText().toString(),txtclss.getText().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listofSubject);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        for(int i=0;i<adapter.toString().length();i++) {
            spinnersub.setAdapter(adapter);
            break;
        }
        spinnersub.setSelection(0);
    }


    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

         s =s.replace("\"","");
        if(s.equals("success"))
        {
            long n = qr.deleteQueueRow(hid,htext);
            if(n>0) {
                n = -1;
                n = qr.updateHomeworkSyncFlag(qr.GetHomework(hid));
                if(n>0) {
                    TeacherHomeworkFragment fragment = new TeacherHomeworkFragment();
                    Singlton.setMainFragment(fragment);
                    Singlton.setSelectedFragment(fragment);
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    bundle.putString("HEADERTEXT", htext);
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();

                }
            }
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

    public void open(final ImageView v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Delete Image");

        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (v == btnCapturePicture) {
                    btnCapturePicture.setImageBitmap(null);
                    cpature1 = 0;
                }
                else if (v == btnCapturePicture1) {
                    btnCapturePicture1.setImageBitmap(null);
                    cpature2=0;
                }
                else if (v == btnCapturePicture2) {
                    btnCapturePicture2.setImageBitmap(null);
                    cpature3=0;
                }

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",10);
        b.putString("HeaderText",htext);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
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
                Config.hideSoftKeyboardWithoutReq(getActivity(),edthwork);
                SwitchClass();
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(),edthwork);
                saveHomework();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
*/
