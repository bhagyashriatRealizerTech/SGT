package com.realizer.schoolgeine.teacher.gallaryimagepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.adapters.PhotoAlbumAdapter;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStoreHelperMethods;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStorePhoto;
import com.realizer.schoolgeine.teacher.homework.newhomework.NewHomeworkActivity;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;

import java.util.ArrayList;




public class PhotoAlbumActivity extends AppCompatActivity {

    private ArrayList<MediaStorePhoto> selectedPhotoList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    public ProgressWheel loading;

    private ArrayList<MediaStorePhoto> bucketItemList = new ArrayList<>();
    private ArrayList<Integer> bucketTotalImageCount = new ArrayList<>();
    private ArrayList<Integer> bucketSelectedImageCount = new ArrayList<>();

    private ActionBar ab;

    private int photoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(PhotoAlbumActivity.this));
        photoType = 1;

        ab = getSupportActionBar();
        ab.setTitle(Config.actionBarTitle("Gallery", PhotoAlbumActivity.this));
        Bundle b = getIntent().getExtras();
        boolean flag = b.getBoolean("FunCenter", false);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PhotoAlbumAdapter(bucketItemList, selectedPhotoList, this,flag,b);
        mLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loading = (ProgressWheel)findViewById(R.id.loading);



        new GetBuketsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (selectedPhotoList.size() != 0) new AlertDialog.Builder(PhotoAlbumActivity.this)
                    .setTitle("Cancel selection?")
                    .setMessage("Your selection will be lost. Do you want to continue?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_cancel_dark)
                    .show();
            else finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPhotoAlbumData() {
        bucketItemList.clear();
        bucketTotalImageCount.clear();
        bucketSelectedImageCount.clear();

        bucketTotalImageCount = getBucketTotalImageCount();
        bucketSelectedImageCount = getBucketSelectedImageCount();

        ArrayList<MediaStorePhoto> mList = new ArrayList<>(MediaStoreHelperMethods.getBucketCoverItems(getContentResolver()));

        for (int i = 0; i < mList.size(); i++) {
            MediaStorePhoto photo = mList.get(i);
            photo.setBucket("(" + bucketSelectedImageCount.get(i) + "/" + bucketTotalImageCount.get(i) + ") " + photo.getBucket());
            bucketItemList.add(photo);
        }

        mAdapter.notifyDataSetChanged();

        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            selectedPhotoList.clear();
            ArrayList<MediaStorePhoto> photoList = data.getParcelableArrayListExtra("selected_photo_list");
            for (MediaStorePhoto mPhoto : photoList) {
                selectedPhotoList.add(mPhoto);
            }
            ab.setTitle(String.valueOf(selectedPhotoList.size()) + " Photos Selected");
            //getPhotoAlbumData();
            new GetBuketsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (requestCode == 3000 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public ArrayList<Integer> getBucketTotalImageCount() {
        ArrayList<Integer> countList = new ArrayList<>();
        for (MediaStorePhoto photo : MediaStoreHelperMethods.getBucketCoverItems(getContentResolver())) {
            countList.add(MediaStoreHelperMethods.getAllPhotosInBucket(photo.getBucketId(), getContentResolver()).size());
        }
        return countList;
    }

    public ArrayList<Integer> getBucketSelectedImageCount() {
        ArrayList<Integer> countList = new ArrayList<>();
        for (MediaStorePhoto photo : MediaStoreHelperMethods.getBucketCoverItems(getContentResolver())) {
            int count = 0;
            for (MediaStorePhoto mPhoto : selectedPhotoList) {
                if (photo.getBucketId().equals(mPhoto.getBucketId()))
                    count++;
            }
            countList.add(count);
        }
        return countList;
    }

    public class GetBuketsAsyncTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bucketItemList.clear();
            bucketTotalImageCount.clear();
            bucketSelectedImageCount.clear();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            bucketTotalImageCount = getBucketTotalImageCount();
            bucketSelectedImageCount = getBucketSelectedImageCount();

            ArrayList<MediaStorePhoto> mList = new ArrayList<>(MediaStoreHelperMethods.getBucketCoverItems(getContentResolver()));

            for (int i = 0; i < mList.size(); i++) {
                MediaStorePhoto photo = mList.get(i);
                photo.setBucket("(" + bucketSelectedImageCount.get(i) + "/" + bucketTotalImageCount.get(i) + ") " + photo.getBucket());
                bucketItemList.add(photo);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loading.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();

        }
    }
}
