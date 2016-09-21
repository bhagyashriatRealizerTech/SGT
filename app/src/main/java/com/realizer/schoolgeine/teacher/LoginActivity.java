package com.realizer.schoolgeine.teacher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.service.AutoSyncService;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.backend.SqliteHelper;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Win on 11/27/2015.
 */
public class LoginActivity extends Activity implements OnTaskCompleted {

    EditText userName, password;
    Button loginButton;
    CheckBox checkBox;
    DatabaseQueries dbqr;
    int num;
    ProgressWheel loading;
    AlertDialog.Builder adbdialog;
    SharedPreferences sharedpreferences;
    TextView forgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        setContentView(R.layout.login_activity);

        userName = (EditText) findViewById(R.id.edtEmpId);
        password = (EditText) findViewById((R.id.edtPassword));
        loginButton = (Button) findViewById(R.id.btnLogin);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        loginButton.setTypeface(face);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        loading = (ProgressWheel) findViewById(R.id.loading);
        forgotPassword = (TextView) findViewById(R.id.txtForgetPswrd);
        dbqr = new DatabaseQueries(LoginActivity.this);
        num =0;
        //About Remember me in login page
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("DeviceId", telephonyManager.getDeviceId());
        edit.commit();


        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    userName.setText(result);
                    userName.setSelection(result.length());
                    // alert the user
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    password.setText(result);
                    password.setSelection(result.length());
                    // alert the user
                }
            }
        });



        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sharedpreferences.edit();
                if (checkBox.isChecked()) {

                    edit.putString("Username", userName.getText().toString().trim());
                    edit.putString("Password", password.getText().toString().trim());
                    edit.putString("CHKSTATE", "true");
                    edit.commit();
                }
                else
                {
                    edit.putString("Username", "");
                    edit.putString("Password", "");
                    edit.putString("CHKSTATE", "false");
                    edit.commit();
                }
            }
        });


        String chk = sharedpreferences.getString("CHKSTATE","");
        Log.d("CHECKED", chk);
        if(chk.equals("true"))
        {
            checkBox.setChecked(true);
            userName.setText(sharedpreferences.getString("Username",""));
            password.setText(sharedpreferences.getString("Password", ""));
        }
        else
        {
            checkBox.setChecked(false);
            userName.setText("");
            password.setText("");
        }



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.forgotpwd_recoveryoption, null);
                Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
                Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
                final RadioButton mail = (RadioButton)dialoglayout.findViewById(R.id.rb_option_mail);
                final RadioButton magicword = (RadioButton)dialoglayout.findViewById(R.id.rb_option_magic_word);
                submit.setTypeface(face);
                cancel.setTypeface(face);
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(dialoglayout);

                final AlertDialog alertDialog = builder.create();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mail.isChecked()) {
                            alertDialog.dismiss();
                            recoverPasswordByEmail();
                        }
                        if (magicword.isChecked()) {

                        }

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


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              boolean res = isConnectingToInternet();
                if(!res)
                {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }

                else if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username/Password", Toast.LENGTH_LONG).show();
                }
                else if (userName.getText().toString().equals("") ) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
                }
                else if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                }
                else
                {
                    final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    String logchk = sharedpreferences.getString("LogChk", "");
                    String uname = sharedpreferences.getString("Username","");
                    if(logchk.equals("true") && !uname.equals(userName.getText().toString().trim()))
                    {
                        adbdialog = new AlertDialog.Builder(LoginActivity.this);
                        adbdialog.setTitle("Login Alert");
                        adbdialog.setMessage("All the Data of Previous User will be Deleted,\nDo You want to Proceed?");
                        adbdialog.setIcon(android.R.drawable.ic_dialog_alert);
                        adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                loading.setVisibility(View.VISIBLE);

                                new NewLoginAsync().execute();

                            } });


                        adbdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                userName.setText("");
                                password.setText("");
                            } });
                        adbdialog.show();

                    }
                    else {
                        loading.setVisibility(View.VISIBLE);
                        LoginAsyncTaskGet obj = new LoginAsyncTaskGet(userName.getText().toString().replaceAll(" ", ""), password.getText().toString().replaceAll(" ", ""), LoginActivity.this, LoginActivity.this);
                        obj.execute();
                    }

                }
            }
        });

    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel)  {
        boolean b = false;
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("UidName", userName.getText().toString());
        edit.putString("Username", userName.getText().toString().trim());
        edit.putString("Password", password.getText().toString().trim());

        edit.commit();

            String logchk = sharedpreferences.getString("LogChk","");
            if(logchk.equals("true"))
            {

                try {
                    JSONObject rootObj = new JSONObject(s);
                    String validate = rootObj.getString("isLoginSuccessfull");
                    if(validate.equals("valid"))
                    {
                        b=true;
                    }
                    else
                    {
                        String Schoolcode = rootObj.getString("SchoolCode");
                        if(Schoolcode.length()==0)
                        {
                            num=1;
                        }
                        b=false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            else {
                b = parsData(s);
            }

        if(b==true) {

            loading.setVisibility(View.GONE);
                   GCMReg();
                   edit.putString("Login", "true");
                   edit.commit();
                   Intent ser = new Intent(LoginActivity.this,AutoSyncService.class);
                   Singlton.setAutoserviceIntent(ser);
                   startService(ser);
                   Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                   startActivity(i);

                   /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                   String temp = preferences.getString("DisplayName","");
                   *//*Intent i = new Intent(LoginActivity.this, AddPhotDisplayNameActivity.class);
                   startActivity(i);*//*
                  if(temp.length()==0)
                   {
                       Intent i = new Intent(LoginActivity.this, AddPhotDisplayNameActivity.class);
                       startActivity(i);
                   }
                   else
                   {
                       Intent i = new Intent(LoginActivity.this, TeacherActivity.class);
                       startActivity(i);
                   }
*/
               }

        else {
                   if(num==0)
                       Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                   else if(num==1)
                       Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
        }



    }

    public void GCMReg()
    {

        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
        GCMRegistrar.register(LoginActivity.this, Config.SENDER_ID);

    }



    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    /*String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
                    Toast.makeText(context,newMessage,Toast.LENGTH_SHORT).show();*/
                }
            };

    public boolean parsData(String json) {
        long n =-1;
       DatabaseQueries qr = new DatabaseQueries(getApplicationContext());
        String validate="";
        String Schoolcode="";
        JSONObject rootObj = null;
       Log.d("String", json);
        try {
            rootObj = new JSONObject(json);

            validate = rootObj.getString("isLoginSuccessfull");
            Schoolcode = rootObj.getString("SchoolCode");
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("SchoolCode", Schoolcode);
            edit.commit();
            //qr.deleteTable();

            JSONArray publicholiday = rootObj.getJSONArray("Phs");
            for(int i=0;i<publicholiday.length();i++)
            {
                JSONObject obj = publicholiday.getJSONObject(i);
                String holiday= obj.getString("Holiday");
                //\/Date(1452188220000-0600)\/
                String jsondate= obj.getString("PHStartDate").split("\\(")[1].split("\\-")[0];
                String jsondate1= obj.getString("PHStartDate").split("\\(")[1].split("\\-")[0];
                Log.d("JSONDATE", jsondate);

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                Date createdOn = new Date(Long.parseLong(jsondate));
                String hsdate = sdf.format(createdOn);
                Date createdOn1 = new Date(Long.parseLong(jsondate1));
                String hedate = sdf.format(createdOn1);

                n = qr.insertHoliday(holiday,hsdate,hedate);
                if(n>=0)
                {
                   // Toast.makeText(getApplicationContext(),"Holiday Inserted Successfully",Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getApplicationContext(),"Holiday Inserted Successfully",Toast.LENGTH_SHORT).show();
                    n=-1;
                }
            }

            //parsing json data and loaded in database
            JSONObject teacherInfo  = rootObj.getJSONObject("Teacher");
            String activetill= teacherInfo.getString("ActiveTill").split("\\(")[1].split("\\-")[0];
            String classon= teacherInfo.getString("ClassTeacherOn");
            String tname= teacherInfo.getString("Name");
            String qual= teacherInfo.getString("Qualification");
            String thumbnail= teacherInfo.getString("ThumbnailURL");
            String userid= teacherInfo.getString("UserId");
            String contact= teacherInfo.getString("contactNo");
            String dob= teacherInfo.getString("dob");
            String emailId= teacherInfo.getString("emailId");
            String isactive= teacherInfo.getString("isActive");
            n = qr.insertDetailTeacherInfo(activetill,classon,tname,qual,thumbnail,userid,contact,dob,emailId,isactive);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DisplayName", tname);
            editor.putString("ThumbnailID", thumbnail);
            editor.commit();
            if(n>=0)
            {
                // Toast.makeText(getApplicationContext(), "Teacher Inserted Successfully", Toast.LENGTH_SHORT).show();
                n=-1;
            }

            JSONObject sdlist = rootObj.getJSONObject("sdsLst");
            JSONArray stds = sdlist.getJSONArray("stdDivStudentLst");
            for(int i =0;i<stds.length();i++)
            {
                JSONObject obj = stds.getJSONObject(i);
                String std= obj.getString("Std");
                String div= obj.getString("Div");
                JSONArray stud  = obj.getJSONArray("studLst");
                Log.d("Arr", obj.getString("studLst"));
                n =qr.insertStudInfo(std,div,stud.toString());

                editor.putString("STANDARD", std);
                editor.putString("DIVISION", div);
                editor.commit();
                if(n>=0)
                {
                   // Toast.makeText(getApplicationContext(),"Student Inserted Successfully",Toast.LENGTH_SHORT).show();
                    n=-1;
                }

            }

            JSONObject stddiv = rootObj.getJSONObject("sdsa");
            JSONArray subAllocation = stddiv.getJSONArray("subjAllocation");
            String teacherid="",Stndard="",division="";
            int i=subAllocation.length();
            for(int j=0;j<i;j++)
            {
                JSONObject obj = subAllocation.getJSONObject(j);
                teacherid = obj.getString("TeacherUserId");
               /* SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("DisplayName", teacherid);
                editor.commit();*/
                Stndard=obj.getString("Std");
                division=obj.getString("division");
                String subject=obj.getString("subject");
                n= qr.insertSubInfo(Stndard,division,subject);
                if(n>=0)
                {
                   // Toast.makeText(getApplicationContext(),"Subject Inserted Successfully",Toast.LENGTH_SHORT).show();
                    n=-1;
                }
            }
            n = qr.insertTeacherInfo(Stndard,division,teacherid);
            if(n>=0)
            {
                //Toast.makeText(getApplicationContext(),"Teacher Inserted Successfully",Toast.LENGTH_SHORT).show();
                n=-1;
            }
            String sub = "[{\"subject\":\"Marathi\"},{\"subject\":\"English\"},{\"subject\":\"Hindi\"},{\"subject\":\"Mathmatics\"}]";
            //JSONArray allsub = new JSONArray(sub);
            n = qr.insertStdSubjcet(Stndard, sub);
            if(n>=0)
            {
                //Toast.makeText(getApplicationContext(),"All Subjcet Inserted Successfully",Toast.LENGTH_SHORT).show();
                n=-1;
            }

            Stndard="";
            division="";
            try {
                for (int m = 0; m < subAllocation.length(); m++) {
                    JSONObject objarr = subAllocation.getJSONObject(m);
                    if (m > 0) {
                        if (Stndard.equalsIgnoreCase(objarr.getString("Std")) && division.equalsIgnoreCase(objarr.getString("division"))) {

                        } else {
                            Stndard = objarr.getString("Std");
                            division = objarr.getString("division");
                            JSONArray arr = new JSONArray(qr.GetAllTableData(Stndard, division));
                            for (int num = 0; num < arr.length(); num++) {
                                JSONObject obj = arr.getJSONObject(num);
                                String name = obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName");
                                //String name = obj.getString("classRollNo") + " " + obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName");
                                String uid = obj.getString("userId");
                                String thumbnailUrl = obj.getString("ThumbnailURL");
                                n = qr.insertInitiatechat(name, "false", Stndard, division, uid, 0,thumbnailUrl);
                                if (n > 0) {
                                    //Toast.makeText(getApplicationContext(),"All Subjcet Inserted Successfully",Toast.LENGTH_SHORT).show();
                                    n = -1;
                                }
                            }
                        }

                    }
                    else {
                        Stndard = objarr.getString("Std");
                        division = objarr.getString("division");
                        JSONArray arr = new JSONArray(qr.GetAllTableData(Stndard, division));
                        for (int num = 0; num < arr.length(); num++) {
                            JSONObject obj = arr.getJSONObject(num);
                            String name =  obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName");
                            String uid = obj.getString("userId");
                            String thumbnailUrl = obj.getString("ThumbnailURL");
                            n = qr.insertInitiatechat(name, "false", Stndard, division, uid, 0,thumbnailUrl);
                            if (n > 0) {
                                //Toast.makeText(getApplicationContext(),"All Subjcet Inserted Successfully",Toast.LENGTH_SHORT).show();
                                n = -1;
                            }
                        }
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
                num = 1;
               // Toast.makeText(getApplicationContext(),"Server Not Responding",Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.toString());

        }

        if(validate.equals("valid"))
        return true;
        else
            return false;
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getSystemService(
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
    public void onBackPressed() {
        super.onBackPressed();
    }


    public class NewLoginAsync extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            dbqr.deleteAllData();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("LogChk", "false");
            edit.commit();
            LoginAsyncTaskGet obj = new LoginAsyncTaskGet(userName.getText().toString().replaceAll(" ",""),password.getText().toString().replaceAll(" ", ""),LoginActivity.this,LoginActivity.this);
            obj.execute();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(loading != null  && loading.isShown())
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // condition to lock the screen at the time of refreshing
        if ((loading != null && loading.isShown())) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void recoverPasswordByEmail()
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_rmailpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        final EditText email = (EditText)dialoglayout.findViewById(R.id.edtmailid);
        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        final AlertDialog alertDialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String userEmail =  email.getText().toString().trim();
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
}
