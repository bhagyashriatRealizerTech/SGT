package com.realizer.schoolgeine.teacher.funcenter;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.adapters.SelectPhotoAdapter;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStoreHelperMethods;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStorePhoto;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class SelectPhotoActivityFunCenter extends AppCompatActivity {

    private ArrayList<MediaStorePhoto> selectedPhotoList;

    private String bucketId;
    private ArrayList<MediaStorePhoto> bucketPhotoList;

    private RecyclerView mRecyclerView;
    private SelectPhotoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int count;
    private ActionBar ab;
    MenuItem search,switchclass;
    String eventuuid,grid,upload,isupload,image1,imagecaption,eventName;
    DatabaseQueries qr;
    int imgId,acadmicyear,evntgetid;
    long m;
    ProgressWheel loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(SelectPhotoActivityFunCenter.this));
        ab = getSupportActionBar();
        Bundle b = getIntent().getBundleExtra("Event");
        evntgetid = b.getInt("PassEventId", 0);
        eventuuid = b.getString("EventUUID", "");
        eventName = b.getString("EventName", "");
        bucketId = getIntent().getStringExtra("bucket_id");
        selectedPhotoList = getIntent().getParcelableArrayListExtra("selected_photo_list");
        bucketPhotoList = MediaStoreHelperMethods.getAllPhotosInBucket(bucketId, getContentResolver());
        loading = (ProgressWheel) findViewById(R.id.loading);
        qr = new DatabaseQueries(SelectPhotoActivityFunCenter.this);

        /**
         * Set status of checked photos in bucket photo list
         */

        for (int i = 0; i < bucketPhotoList.size(); i++) {
            for (MediaStorePhoto photo : selectedPhotoList) {
                if (photo.getId().equals(bucketPhotoList.get(i).getId())) {
                    bucketPhotoList.get(i).setStatus("checked");
                    count++;
                    break;
                }
            }
        }

        ab.setTitle(count + " Photos Selected");


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new SelectPhotoAdapter(bucketPhotoList, this);
        mLayoutManager = new GridLayoutManager(this, 3);

        mAdapter.setCallback(new SelectPhotoAdapter.SelectPhotoCallback() {
            @Override
            public void selectViewPressed(int position, String status) {
                bucketPhotoList.get(position).setStatus(status);
                if (status.equals("checked")) count++;
                else count--;
                ab.setTitle(count + " Photos Selected");
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        switchclass = menu.findItem(R.id.action_switchclass);
        switchclass.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_done:
                Singlton.setIsDonclick(Boolean.TRUE);
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectImage(String caption)
    {
        ArrayList<String> selectedItems = new ArrayList<>();
        final int len = bucketPhotoList.size();
        int cnt = 0;
        String selectImages = "";
        for (MediaStorePhoto photo : bucketPhotoList) {
            if (photo.getStatus().equals("checked")) {
                selectedItems.add(photo.getDataUri());
            }
        }

        if (selectedItems.size()==0)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Config.alertDialog(SelectPhotoActivityFunCenter.this, "Gallery", "Please Select at least one image");
                }
            });

        }
        else
        {
            if (Singlton.getImageList().size()+selectedItems.size()>10)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Config.alertDialog(SelectPhotoActivityFunCenter.this, "Gallery", "Please Select only 10 image");
                    }
                });

            }
            else if (Singlton.getImageList().size()>10||selectedItems.size()>10)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Config.alertDialog(SelectPhotoActivityFunCenter.this, "Gallery", "Please Select only 10 image");
                    }
                });

            }
            else {

                UUID imguuid = UUID.randomUUID();
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                upload = df.format(c.getTime());

                isupload = "false";

                for (int i = 0; i < selectedItems.size(); i++) {
                    grid = String.valueOf(selectedItems.get(i));
                    imguuid = UUID.randomUUID();
                    File f=new File(grid);
                    Bitmap bitmap = BitmapFactory.decodeFile(grid);

                    image1 = ImageStorage.saveEventToSdCard(bitmap, eventName, SelectPhotoActivityFunCenter.this);
                    String f2[] = image1.split(File.separator);
                    String filename=f.getName();

                    acadmicyear = Calendar.getInstance().get(Calendar.YEAR);
                    imagecaption = caption;



                    m = qr.InsertImage(evntgetid, image1, upload, isupload, acadmicyear, i, imagecaption, imguuid.toString(),filename,eventuuid.toString());
                    if (m > 0)
                    {
                        imgId = qr.getImageId();
                        SimpleDateFormat df1 = new SimpleDateFormat("dd MMM hh:mm:ss a");
                        m = qr.insertQueue(imgId, "EventImages", "7", df1.format(Calendar.getInstance().getTime()));

                    }

                }

                Singlton.setSelectedFragment(Singlton.getMainFragment());
                finish();
            }
        }
    }

    public void showDialog()
    {
        if(count == 0)
        {
            Config.alertDialog(SelectPhotoActivityFunCenter.this, "Gallery", "Please Select at least 1 Image");
            //Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_LONG).show();
        }
        else {

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View alertDialogView = inflater.inflate(R.layout.teacher_funcenter_caption_layout, null);
            adb.setView(alertDialogView);

            adb.setTitle("Image Caption");
            adb.setMessage("Please Enter Caption for Images");
            adb.setIcon(android.R.drawable.ic_dialog_info);

            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    EditText et = (EditText) alertDialogView.findViewById(R.id.edt_caption);
                    if (et.getText().toString().trim().length() > 0)
                        new insertImageAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, et.getText().toString().trim());
                        //selectImages(et.getText().toString().trim());
                    else
                        new insertImageAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imagecaption);
                    //selectImages(imagecaption);
                    dialog.dismiss();
                }
            });


            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    new insertImageAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imagecaption);
                    //selectImages(imagecaption);
                    dialog.dismiss();
                }
            });
            adb.show();
        }
    }

    public class insertImageAsync extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            String caption = params[0];
            selectImage(caption);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.setVisibility(View.GONE);
            Singlton.setSelectedFragment(Singlton.getMainFragment());
            finish();
        }

    }
}
