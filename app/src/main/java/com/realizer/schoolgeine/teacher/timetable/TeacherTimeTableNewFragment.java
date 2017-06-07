package com.realizer.schoolgeine.teacher.timetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.PhotoAlbumActivity;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.homework.newhomework.NewHomeworkActivity;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.homework.newhomework.adapter.NewHomeworkGalleryAdapter;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    int REQUEST_CAMERA = 100;
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

    GridView gridView;
    ArrayList<String> templist;
    ArrayList<TeacherHomeworkModel> hwimage;
    NewHomeworkGalleryAdapter adapter;
    ArrayList<String> base64imageList;
    File cameraCapturedFile;

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
        gridView= (GridView) rootView.findViewById(R.id.gallerygridView);
        /**
         * Capture image button click event
         */
        qr = new DatabaseQueries(getActivity());
        imgsyllabus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                //getOption();
              /*  Intent intent = new Intent(getActivity(),PhotoAlbumActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("FunCenter", false);
                b.putBoolean("Homework",false);
                intent.putExtras(b);
                getActivity().startActivity(intent);*/
                final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.imagepickerdialog_layout, null);
                ImageButton camera_btn = (ImageButton) dialoglayout.findViewById(R.id.img_camera);
                ImageButton gallery_btn = (ImageButton) dialoglayout.findViewById(R.id.img_gallary);
                Button cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                cancel.setTypeface(face);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialoglayout);

                final AlertDialog alertDialog = builder.create();


                camera_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cameraCapturedFile= new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "temp.png");
                        Uri tempURI = Uri.fromFile(cameraCapturedFile);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempURI);
                        startActivityForResult(intent, REQUEST_CAMERA);
                        alertDialog.dismiss();
                    }
                });


                gallery_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), PhotoAlbumActivity.class);
                        Bundle b = new Bundle();
                        b.putBoolean("FunCenter", false);
                        b.putBoolean("Homework", true);
                        intent.putExtras(b);
                        getActivity().startActivity(intent);
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


        return rootView;
    }

    @Override
    public void onClick(View v) {

    }

    public class GetImagesTimeTable extends AsyncTask<Void, Void,Void>
    {


        ArrayList<String> temp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            templist = new ArrayList<>();
            base64imageList = new ArrayList<>();
            templist.addAll(Singlton.getImageList());
            Singlton.setImageList(new ArrayList<String>());
            hwimage = new ArrayList<>();
            temp = new ArrayList<>();

            for(int i=0;i<templist.size();i++)
            {
                String path = templist.get(i).toString();
                Bitmap bitmap =BitmapFactory.decodeFile(path);

                TeacherHomeworkModel obj = new TeacherHomeworkModel();
                obj.setPic(bitmap);

                hwimage.add(i, obj);
                temp.add(i,path);
            }
            if(templist.size()<10)
            {
                Bitmap icon = BitmapFactory.decodeResource(TeacherTimeTableNewFragment.this.getResources(),
                        R.drawable.addimageicon);
                TeacherHomeworkModel obj = new TeacherHomeworkModel();
                obj.setPic(icon);
                obj.setHwTxtLst("NoIcon");
                hwimage.add(templist.size(),obj);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if(templist.size()>0) {
                imgsyllabus.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                adapter = new NewHomeworkGalleryAdapter(getActivity(), hwimage,temp,false,TeacherTimeTableNewFragment.this);
                gridView.setAdapter(adapter);
                gridView.setFastScrollEnabled(true);
            }
            else
            {
                imgsyllabus.setVisibility(View.VISIBLE);
            }
            //loading.setVisibility(View.GONE);
        }
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
        else if (description.getText().toString().equals(""))
        {
            Config.alertDialog(Singlton.getContext(), "Time Table", "Please Enter Description");
           // Toast.makeText(getActivity(), "Please Enter Description", Toast.LENGTH_LONG).show();
            //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.Timetabledescription)).toString());
        }
        else if ( Singlton.getFialbitmaplist().size()<=0)
        {
            Config.alertDialog(Singlton.getContext(), "Time Table", "Please Add Image");
           // Toast.makeText(getActivity(), "Please insert image", Toast.LENGTH_LONG).show();
        }
        else {
            ArrayList<TeacherHomeworkModel> tempImageList = new ArrayList<>();
            tempImageList = Singlton.getFialbitmaplist();
            for (int i = 0; i < tempImageList.size(); i++) {
                if (tempImageList.get(i).getHwTxtLst().equals("NoIcon")) {
                    tempImageList.remove(i);
                }
            }
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar1 = Calendar.getInstance();

            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM hh:mm:ss a");
            Calendar calendar = Calendar.getInstance();

            for (int i = 0; i < tempImageList.size(); i++)
            {

            encodedImage = ImageStorage.saveEventToSdCard(tempImageList.get(i).getPic(), "TimeTable", getActivity());

            long n = qr.insertTimeTable(txtstd.getText().toString(), txtclss.getText().toString(), timetableName.getText().toString(), encodedImage, givenby, df2.format(calendar1.getTime()), description.getText().toString());

            if (n > 0) {
                n = -1;
                ttid = qr.getTimeTableId();
                n = qr.insertQueue(ttid, "TimeTable", "1", df1.format(calendar.getTime()));
              }
            }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
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

        if(Singlton.getImageList().size()>0)
        {
            new GetImagesTimeTable().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            imgsyllabus.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
    }





    private void onCaptureImageResult(Intent data) {
       /* Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getActivity(), thumbnail);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));*/
        ArrayList<String> imageList = new ArrayList<>();
        imageList = Singlton.getImageList();

        imageList.add(imageList.size(), cameraCapturedFile.getAbsolutePath());
        Singlton.setImageList(imageList);


        if(Singlton.getImageList().size()>0)
        {
            new GetImagesTimeTable().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            imgsyllabus.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


}
