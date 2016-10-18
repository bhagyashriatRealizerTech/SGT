package com.realizer.schoolgeine.teacher.homework.newhomework;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgenie.teacher.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Bhagyashri on 9/3/2016.
 */
public class CustomPhotoGalleryActivityHw extends AppCompatActivity {
    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;
    private GridView grdImages;
    MenuItem search,switchclass;
    int eventid1;
    String eventuuid,grid,upload,isupload,image1,imagecaption,eventName;
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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(CustomPhotoGalleryActivityHw.this));
        setContentView(R.layout.ac_image_grid);
        getSupportActionBar().setTitle(Config.actionBarTitle("Select Image", CustomPhotoGalleryActivityHw.this));
        getSupportActionBar().show();

        grdImages = (GridView) findViewById(R.id.gridview);
      //  qr = new DatabaseQueries(CustomPhotoGalleryActivityHw.this);
        /*if(Singleton.getImageList().size()>0)
            totalselectedImageCount = Singleton.getImageList().size();
        else*/
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
                new insertImageAsync().execute();
               // Singlton.setIsDonclick(Boolean.TRUE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




    public class insertImageAsync extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            selectImages("");
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

        if(Singlton.getImageList().size()>0)
        {
            ArrayList<String> newList =  new ArrayList<>();
            newList.addAll(selectedItems);
            newList.addAll(Singlton.getImageList());
            Singlton.setImageList(newList);
        }
        else
            Singlton.setImageList(selectedItems);
    }

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
                imageAdapter = new ImageAdapter(CustomPhotoGalleryActivityHw.this);
                grdImages.setAdapter(imageAdapter);
                imagecursor[0].close();
                imagecursor[1].close();
                cursor.close();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                            Config.alertDialog(CustomPhotoGalleryActivityHw.this, "Gallery", "Please Select only 10 image");
                            //Toast.makeText(CustomPhotoGalleryActivityHw.this,"Please Select only 10 image",Toast.LENGTH_SHORT).show();
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
                            Config.alertDialog(CustomPhotoGalleryActivityHw.this, "Gallery", "Please Select only 10 image");
                            //Toast.makeText(CustomPhotoGalleryActivityHw.this,"Please Select only 10 image",Toast.LENGTH_SHORT).show();
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
