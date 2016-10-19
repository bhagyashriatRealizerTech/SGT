package com.realizer.schoolgeine.teacher.view;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;

/**
 * Created by Win on 11/25/2015.
 */
public class FullImageViewActivity extends FragmentActivity {
    static int NUM_ITEMS ;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    static String[] IMG;
    static ImageView imageView;
    static ActionBar bar;
    static Bitmap decodedByte;
    static TextView txtcnt;
    static Bitmap barr[];
    static ImageView imgv[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String path = preferences.getString("ImageString","");
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.fragment_page);

        try {
            JSONArray jarr = new JSONArray(path);
            NUM_ITEMS = jarr.length();
            IMG = new String[NUM_ITEMS];
            barr = new Bitmap[NUM_ITEMS];
            imgv = new ImageView[NUM_ITEMS];

            for(int i=0;i<NUM_ITEMS;i++)
            {
                IMG[i] = jarr.getString(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        viewPager.setAdapter(imageFragmentPagerAdapter);
        if(path.split("@@@").length>1) {
            int pos = Integer.valueOf(path.split("@@@")[1]);
            viewPager.setCurrentItem(pos);
        }
    }
    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.fullimageview_parent, container, false);
            imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            txtcnt = (TextView) swipeView.findViewById(R.id.txtcounter);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            Log.d("FILENAME", "" + IMG[position]);

            String filePath = IMG[position];
            byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
             decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            barr[position] = decodedByte;
            imageView.setImageBitmap(decodedByte);
            txtcnt.setText("" + (position + 1) + " / " + NUM_ITEMS);

            imgv[position] = imageView;
            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((FullImageViewActivity) getActivity()).getSupportActionBar().show();
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // DO DELAYED STUFF
                            ((FullImageViewActivity) getActivity()).getSupportActionBar().hide();
                        }
                    }, 5000);
                }
            });
*/
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imageview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rotate:
                RotateImg();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void RotateImg()
    {

        Bitmap bitmap = barr[viewPager.getCurrentItem()];
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        imgv[viewPager.getCurrentItem()].setImageBitmap(rotated);
    }

    @Override
    public void onBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        finish();
    }
}
