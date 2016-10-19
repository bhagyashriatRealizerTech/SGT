package com.realizer.schoolgeine.teacher.gallarypicker;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.realizer.schoolgeine.teacher.R;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_grid);
        Log.d("MyTag", "MainActivity");
        addBucketFragment();

    }

    private void addBucketFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ImagesBucketFragment fb = ImagesBucketFragment.newInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.view_holder, fb);
        ft.addToBackStack(null);
        ft.commit();
    }
}