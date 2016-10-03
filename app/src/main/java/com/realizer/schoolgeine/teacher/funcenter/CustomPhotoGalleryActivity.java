package com.realizer.schoolgeine.teacher.funcenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Bhagyashri on 9/3/2016.
 */
public class CustomPhotoGalleryActivity extends AppCompatActivity implements OnTaskCompleted {
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;
    private GridView grdImages;
    MenuItem search,switchclass;
    int eventid1;
    String eventuuid,grid,upload,isupload,image1,imagecaption,eventName;
    DatabaseQueries qr;
    int imgId,acadmicyear,evntgetid;
    Cursor imagecursor[],cursor;
    Calendar c;
    SimpleDateFormat df;
    long m;
    int totalselectedImageCount;
    ProgressWheel loading;
    Bitmap bitma[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(CustomPhotoGalleryActivity.this));
        setContentView(R.layout.ac_image_grid);
        getSupportActionBar().setTitle(Config.actionBarTitle("Select Image", CustomPhotoGalleryActivity.this));
        getSupportActionBar().show();
        Bundle b = getIntent().getExtras();
        eventid1=b.getInt("PassEventId");
        eventuuid=b.getString("EventUUID");
        eventName=b.getString("EventName");
        evntgetid = eventid1;
        grdImages = (GridView) findViewById(R.id.gridview);
        qr = new DatabaseQueries(CustomPhotoGalleryActivity.this);
        totalselectedImageCount = 0;

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Thumbnails._ID };
        final String orderBy = MediaStore.Images.Thumbnails._ID;

        imagecursor = new Cursor[2];
        imagecursor[0] = getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null,  MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        imagecursor[1] = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,  MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        cursor =  new MergeCursor(imagecursor);


        loading = (ProgressWheel) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        setBitmaps(cursor);


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
        switch (item.getItemId()) {
            case R.id.action_done:
                showDialog();
                Singlton.setIsDonclick(Boolean.TRUE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showDialog()
    {
        if(totalselectedImageCount == 0)
        {
            Config.alertDialog(CustomPhotoGalleryActivity.this, "Gallery", "Please Select at least 1 Image");
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
            selectImages(caption);
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
    public void selectImages(String caption)
    {
        final int len = thumbnailsselection.length;
        int cnt = 0;
        ArrayList<String> selectedItems = new ArrayList<>();
        String selectImages = "";
        for (int i = 0; i < len; i++) {
            if (thumbnailsselection[i]) {
                cnt++;
                selectedItems.add(arrPath[i]);
            }
        }
        if (cnt == 0) {

        } else {

            UUID imguuid = UUID.randomUUID();
            c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            df = new SimpleDateFormat("MM/dd/yyyy");
            upload = df.format(c.getTime());

            isupload = "false";

            for (int i = 0; i < selectedItems.size(); i++) {
                grid = String.valueOf(selectedItems.get(i));
                imguuid = UUID.randomUUID();
                File f=new File(grid);
                Bitmap bitmap = BitmapFactory.decodeFile(grid);

               image1 = ImageStorage.saveEventToSdCard(bitmap, eventName, CustomPhotoGalleryActivity.this);
                String f2[] = image1.split(File.separator);
                String filename=f.getName();

                acadmicyear = Calendar.getInstance().get(Calendar.YEAR);
                imagecaption = caption;

                m = qr.InsertImage(evntgetid, image1, upload, isupload, acadmicyear, i, imagecaption, imguuid.toString(),filename,eventuuid.toString());
                if (m > 0)
                {
                    imgId = qr.getImageId();
                    SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                    m = qr.insertQueue(imgId, "EventImages", "7", df1.format(Calendar.getInstance().getTime()));


                    /*try {
                        if (m  > 0)
                        {
                            m  = -1;

                            if (Config.isConnectingToInternet(CustomPhotoGalleryActivity.this))
                            {

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CustomPhotoGalleryActivity.this);
                                TeacherFunCenterImageModel o = qr. getImageById(imgId);
                                TeacherFunCenterImageAsynckPost objasync = new TeacherFunCenterImageAsynckPost(o,preferences.getString("STANDARD", ""),preferences.getString("DIVISION", ""), CustomPhotoGalleryActivity.this, CustomPhotoGalleryActivity.this,"true");
                                objasync.execute();

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

            }

        }
    }

    /**
     * Class method
     */

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void setBitmaps(final Cursor cursor) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {

                int image_column_index = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID);
                count = cursor.getCount();
                arrPath = new String[count];
                ids = new int[count];
                thumbnailsselection = new boolean[count];
                bitma = new Bitmap[count];

                for (int i = 0; i < count; i++) {
                    cursor.moveToPosition(i);
                    ids[i] = cursor.getInt(image_column_index);
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                    arrPath[i] = cursor.getString(dataColumnIndex);
                    bitma[i] = MediaStore.Images.Thumbnails.getThumbnail(
                            getApplicationContext().getContentResolver(), cursor.getInt(image_column_index),
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                loading.setVisibility(View.GONE);
                imageAdapter = new ImageAdapter(CustomPhotoGalleryActivity.this);
                grdImages.setAdapter(imageAdapter);
                imagecursor[0].close();
                imagecursor[1].close();
                cursor.close();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
        try{
            s =s.replace("\"","");
            String splitData[]=s.split("@@");
            int imgid=Integer.valueOf(splitData[1]);
            if(splitData[0].equalsIgnoreCase("success")) {
                long m = qr.deleteQueueRow(imgid, "EventImages");
                if (m > 0) {
                    m=qr.updateImageSyncFlag(qr.getImageById(imgid));
                    if (m > 0) {

                        if(imgid == imgId) {
                            Singlton.setSelectedFragment(Singlton.getMainFragment());
                            finish();
                        }
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }


    /**
     * List adapter
     * @author tasol
     */

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.row_multi_photo_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);


            holder.chkImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        totalselectedImageCount = totalselectedImageCount-1;
                    } else {
                        if(totalselectedImageCount == 10)
                        {
                            Config.alertDialog(CustomPhotoGalleryActivity.this, "Gallery", "Please Select only 10 image");
                            //Toast.makeText(CustomPhotoGalleryActivity.this,"Please Select only 10 image",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            cb.setChecked(true);
                            thumbnailsselection[id] = true;
                            totalselectedImageCount = totalselectedImageCount+1;
                        }
                    }
                }
            });

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                        totalselectedImageCount = totalselectedImageCount-1;
                    } else {
                        if(totalselectedImageCount == 10)
                        {
                            Config.alertDialog(CustomPhotoGalleryActivity.this, "Gallery", "Please Select only 10 image");
                            //Toast.makeText(CustomPhotoGalleryActivity.this,"Please Select only 10 image",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            holder.chkImage.setChecked(true);
                            thumbnailsselection[id] = true;
                            totalselectedImageCount = totalselectedImageCount+1;
                        }
                    }
                }
            });


          /*  try {
               // if(!holder.isSet) {
                    //setBitmap(holder.imgThumb, ids[position]);
                    Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(arrPath[position],150,150);
                    holder.imgThumb.setImageBitmap(bitmap);
                    bitmap = null;
                    holder.isSet = true;
                //}
            } catch (Throwable e) {
            }*/
            holder.imgThumb.setImageBitmap(bitma[position]);
            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();

    }
    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
        boolean isSet;
    }

    @Override
    public void onResume() {
        super.onResume();
       getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
