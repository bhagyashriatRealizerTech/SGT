package com.realizer.schoolgeine.teacher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;

/**
 * Created by Bhagyashri on 4/5/2016.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(SplashScreen.this));
        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                  /*  Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);*/
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
                    String getValueBack = sharedpreferences.getString("Login", "");
                    if(getValueBack.length()==0)
                        getValueBack="false";

                    if(getValueBack.equals("false"))
                    {
                        Intent i = new Intent(SplashScreen.this,LoginActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Intent i = new Intent(SplashScreen.this,DrawerActivity.class);
                        startActivity(i);
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
