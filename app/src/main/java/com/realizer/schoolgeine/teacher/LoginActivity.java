package com.realizer.schoolgeine.teacher;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.forgotpassword.SetMagicWordAsyncTaskGet;
import com.realizer.schoolgeine.teacher.forgotpassword.SetPasswordAsyncTaskGet;
import com.realizer.schoolgeine.teacher.forgotpassword.SetPasswordByEmailAsyncTaskGet;
import com.realizer.schoolgeine.teacher.forgotpassword.ValidateMagicWordAsyncTaskGet;
import com.realizer.schoolgeine.teacher.service.AutoSyncService;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Win on 11/27/2015.
 */
public class LoginActivity extends Activity implements OnTaskCompleted ,EasyPermissions.PermissionCallbacks {

    EditText userName, password;
    Button loginButton;
    CheckBox checkBox;
    DatabaseQueries dbqr;
    int num;
    ProgressWheel loading;
    AlertDialog.Builder adbdialog;
    SharedPreferences sharedpreferences;
    TextView forgotPassword;
    String defaultMagicWord;
    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE_FILE };

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        setContentView(R.layout.login_activity);


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

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
        defaultMagicWord="";
        //About Remember me in login page
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("DeviceId", telephonyManager.getDeviceId());
        edit.commit();



        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(Config.Account_Name);
        Singlton.setmCredential(mCredential);


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

                mail.setChecked(true);

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
                            alertDialog.dismiss();
                            recoverPasswordByMagicWord("ForgotPassword",false,"");
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
                    Config.alertDialog(LoginActivity.this,"Network Error","No Internet Connection available");
                    //Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }

                else if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                    Config.alertDialog(LoginActivity.this,"Login","Please Enter Username/Password");
                   // Toast.makeText(getApplicationContext(), "Please Enter Username/Password", Toast.LENGTH_LONG).show();
                }
                else if (userName.getText().toString().equals("") ) {
                    Config.alertDialog(LoginActivity.this,"Login","Please Enter Username");
                    //Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
                }
                else if (password.getText().toString().equals("")) {
                    Config.alertDialog(LoginActivity.this, "Login", "Please Enter Password");
                             // Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
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

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.VIBRATE,
                        }, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
        if(queueListModel != null) {
            if (queueListModel.getType().equalsIgnoreCase("SetMagicWord")) {
                if(s.equalsIgnoreCase("true"))
                {

                }
                boolean b = parsData(queueListModel.getTime());
                if (b == true) {
                    loading.setVisibility(View.GONE);
                    GCMReg();
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putString("Login", "true");
                    edit.commit();

                    getResultsFromApi();


                } else {
                    loading.setVisibility(View.GONE);
                    if (num == 0)
                        Config.alertDialog(LoginActivity.this, "Login", "Invalid credentials, Please Try again!");
                        //Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                    else if (num == 1)
                        Config.alertDialog(LoginActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
                       // Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                }
            }
            else  if (queueListModel.getType().equalsIgnoreCase("ValidateMagicWord")) {
                loading.setVisibility(View.GONE);
                if(s.equalsIgnoreCase("true"))
                {
                    resetPassword();
                }
                else
                {
                    Config.alertDialog(LoginActivity.this, "Forgot Password", "Invalid User ID / Magic Word Entered");
                    //Toast.makeText(LoginActivity.this,"Wrong User ID / Wrong Magic Word Entered",Toast.LENGTH_SHORT).show();
                }

            }
            else  if (queueListModel.getType().equalsIgnoreCase("SetPassword")) {
                loading.setVisibility(View.GONE);
                if(s.equalsIgnoreCase("true"))
                {

                }
                else
                {
                    Config.alertDialog(LoginActivity.this, "Reset Password", "Fail to Reset Password");
                   // Toast.makeText(LoginActivity.this,"Fail to Reset Password",Toast.LENGTH_SHORT).show();
                }

            }
            else  if (queueListModel.getType().equalsIgnoreCase("SendEmail")) {
                loading.setVisibility(View.GONE);
                if(s.equalsIgnoreCase("true"))
                {
                    Config.alertDialog(LoginActivity.this, "Forgot Password", "Email Sent to your Email ID.");
                    //Toast.makeText(LoginActivity.this,"Email Sent Successfully",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Config.alertDialog(LoginActivity.this, "Forgot Password", "Fail to Send Email.");
                    //Toast.makeText(LoginActivity.this,"Fail to Send Email",Toast.LENGTH_SHORT).show();
                }

            }
        }
        else{
        boolean b = false;
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("UidName", userName.getText().toString().trim());
        edit.putString("Username", userName.getText().toString().trim());
        edit.putString("Password", password.getText().toString().trim());
        edit.commit();

        String logchk = sharedpreferences.getString("LogChk", "");
        String mWord = "";
            String validate = "";
            JSONObject rootObj = null;
            String accesstoken ="";
            try {
                rootObj = new JSONObject(s);
                validate = rootObj.getString("isLoginSuccessfull");
                JSONObject teacherInfo  = rootObj.getJSONObject("Teacher");
                mWord =teacherInfo.getString("MagicWord");
                accesstoken =  rootObj.getString("AccessToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            edit.putString("AccessToken", accesstoken);
            edit.commit();
        if (logchk.equals("true")) {

            try {

                if (validate.equals("valid")) {
                    b = true;
                } else {
                    String Schoolcode = rootObj.getString("SchoolCode");
                    if (Schoolcode.length() == 0) {
                        num = 1;
                    }
                    b = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(mWord.trim().length()>0 && !mWord.equalsIgnoreCase("null")) {
                if (b == true) {

                    loading.setVisibility(View.GONE);
                    GCMReg();
                    edit.putString("Login", "true");
                    edit.commit();

                    getResultsFromApi();

                   /* Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                    Singlton.setAutoserviceIntent(ser);
                    startService(ser);
                    Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                    startActivity(i);*/

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
                       Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                       startActivity(i);
                   }
*/
                } else {
                    loading.setVisibility(View.GONE);
                    if (num == 0)
                        Config.alertDialog(LoginActivity.this, "Login", "Invalid credentials, Please Try Again");
                        //Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                    else if (num == 1)
                        Config.alertDialog(LoginActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
                        //Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                loading.setVisibility(View.GONE);
                recoverPasswordByMagicWord("FirstLogin", b, s);
            }
        } else {
            loading.setVisibility(View.GONE);
            String Schoolcode = null;
            if(mWord.trim().length()>0 && !mWord.equalsIgnoreCase("null")) {
                try {

                    if (validate.equalsIgnoreCase("valid")) {
                        num =0;
                        Schoolcode = rootObj.getString("SchoolCode");
                        if(Schoolcode.length()==0 || Schoolcode.equalsIgnoreCase("null"))
                            num =1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                 b = parsData(s);
                if (b == true) {
                    GCMReg();
                    edit.putString("Login", "true");
                    edit.commit();

                    getResultsFromApi();

                   /* Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                    Singlton.setAutoserviceIntent(ser);
                    startService(ser);
                    Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                    startActivity(i);*/

                } else {
                    if (num == 0)
                        Config.alertDialog(LoginActivity.this, "Login", "Invalid credentials, Please Try Again");
                        //Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
                    else if (num == 1)
                        Config.alertDialog(LoginActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
                        //Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                }
            }
            else
            recoverPasswordByMagicWord("FirstLogin", b, s);
        }
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
                String jsondate1= obj.getString("PHEndDate").split("\\(")[1].split("\\-")[0];
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
                alertDialog.dismiss();
               if(userID.length()>0 && userEmail.length()>0)
                {
                    loading.setVisibility(View.VISIBLE);
                    new SetPasswordByEmailAsyncTaskGet(userId,userEmail,LoginActivity.this,LoginActivity.this).execute();
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


    public void recoverPasswordByMagicWord(final String from, boolean b1,final String s)
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_mwordpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        userID.setText(userName.getText().toString());
        final EditText magicWord = (EditText)dialoglayout.findViewById(R.id.edtmagicword);

        final TextView titledialog = (TextView)dialoglayout.findViewById(R.id.dialogTitle);
        final TextView infodialog = (TextView)dialoglayout.findViewById(R.id.infodialog);

        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        if(from.equalsIgnoreCase("FirstLogin"))
        {
            titledialog.setText("Set Magic Word");
            infodialog.setText("You  are Logged in First Time ,Please Set Your Magic Word");
            builder.setCancelable(false);
        }

        final AlertDialog alertDialog = builder.create();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String wordMagic =  magicWord.getText().toString().trim();
                alertDialog.dismiss();
                if (from.equalsIgnoreCase("FirstLogin")) {
                    if(userId.length()>0 && wordMagic.length()>0)

                    new SetMagicWordAsyncTaskGet(userId,wordMagic,s,LoginActivity.this,LoginActivity.this).execute();
                }
                else
                {
                    loading.setVisibility(View.VISIBLE);
                    new ValidateMagicWordAsyncTaskGet(userId,wordMagic,LoginActivity.this,LoginActivity.this).execute();
                }
                //resetPassword();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                if (from.equalsIgnoreCase("FirstLogin")) {
                    boolean b = parsData(s);
                    if (b == true) {
                        loading.setVisibility(View.GONE);
                        GCMReg();
                        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor edit = sharedpreferences.edit();
                        edit.putString("Login", "true");
                        edit.commit();

                        getResultsFromApi();

                       /* Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
                        Singlton.setAutoserviceIntent(ser);
                        startService(ser);
                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        startActivity(i);*/

                    } else {
                        if (num == 0)
                            Config.alertDialog(LoginActivity.this, "Login", "Invalid credentials, Please Try Again");
                            //Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();

                        else if (num == 1)
                            Config.alertDialog(LoginActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
                            //Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alertDialog.show();
    }


    public void resetPassword()
    {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.forgotpwd_resetpassword, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
        final EditText userID = (EditText)dialoglayout.findViewById(R.id.edtuserid);
        final EditText pwd = (EditText)dialoglayout.findViewById(R.id.edtpwd);
        final EditText cPwd = (EditText)dialoglayout.findViewById(R.id.edtconfirmpwd);
        submit.setTypeface(face);
        cancel.setTypeface(face);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);
        final AlertDialog alertDialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID.getText().toString().trim();
                String password =  pwd.getText().toString().trim();
                String cPassword =  cPwd.getText().toString().trim();

                alertDialog.dismiss();

                if(password.equals(cPassword))
                new SetPasswordAsyncTaskGet(userId,password,LoginActivity.this,LoginActivity.this).execute();
                else
                    Config.alertDialog(LoginActivity.this, "Login", "Password Mismatch");
                    //Toast.makeText(LoginActivity.this,"Password Mismatch",Toast.LENGTH_SHORT).show();
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

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! Config.isConnectingToInternet(LoginActivity.this)) {
            Config.alertDialog(LoginActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
        }
        else
        {

            Singlton.setmCredential(mCredential);


            Intent ser = new Intent(LoginActivity.this, AutoSyncService.class);
            Singlton.setAutoserviceIntent(ser);
            startService(ser);
            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
            startActivity(i);
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                LoginActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);

                Singlton.setmCredential(mCredential);


                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Config.alertDialog(LoginActivity.this,"Google Play Service",
                            "This app requires Google Play Services. " +
                                    "Please install Google Play Services on your device " +
                                    "and relaunch this app.");
                }
                else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        Singlton.setmCredential(mCredential);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {

                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

}
