package com.realizer.schoolgeine.teacher.gallaryimagepicker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.adapters.SelectPhotoAdapter;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStoreHelperMethods;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.utils.MediaStorePhoto;

import java.util.ArrayList;




public class SelectPhotoActivity extends AppCompatActivity {

    private ArrayList<MediaStorePhoto> selectedPhotoList;

    private String bucketId;
    private ArrayList<MediaStorePhoto> bucketPhotoList;

    private RecyclerView mRecyclerView;
    private SelectPhotoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private int count;
    private ActionBar ab;
    MenuItem search,switchclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(SelectPhotoActivity.this));
        ab = getSupportActionBar();

        bucketId = getIntent().getStringExtra("bucket_id");
        selectedPhotoList = getIntent().getParcelableArrayListExtra("selected_photo_list");
        bucketPhotoList = MediaStoreHelperMethods.getAllPhotosInBucket(bucketId, getContentResolver());

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
                    Config.alertDialog(SelectPhotoActivity.this, "Gallery", "Please Select at least one image");
                }
                else
                {
                    if (Singlton.getImageList().size()+selectedItems.size()>10)
                    {
                        Config.alertDialog(SelectPhotoActivity.this, "Gallery", "Please Select only 10 image");
                    }
                    else if (Singlton.getImageList().size()>10||selectedItems.size()>10)
                    {
                        Config.alertDialog(SelectPhotoActivity.this, "Gallery", "Please Select only 10 image");
                    }
                    else {
                        if (Singlton.getImageList().size() > 0) {
                            ArrayList<String> newList = new ArrayList<>();
                            newList.addAll(selectedItems);
                            newList.addAll(Singlton.getImageList());
                            Singlton.setImageList(newList);
                        } else
                            Singlton.setImageList(selectedItems);

                        Singlton.setSelectedFragment(Singlton.getMainFragment());
                        finish();
                    }
                }
                // Singlton.setIsDonclick(Boolean.TRUE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
