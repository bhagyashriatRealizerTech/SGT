package com.realizer.schoolgeine.teacher;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.chat.TeacherQueryFragment1;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.funcenter.TeacherFunCenterFolderFragment;
import com.realizer.schoolgeine.teacher.generalcommunication.TeacherGeneralCommunicationFragment;
import com.realizer.schoolgeine.teacher.holiday.TeacherPublicHolidayFragment;
import com.realizer.schoolgeine.teacher.homework.TeacherHomeworkFragment;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassStudentFragment;
import com.realizer.schoolgeine.teacher.service.AutoSyncService;
import com.realizer.schoolgeine.teacher.service.ManualSyncService;
import com.realizer.schoolgeine.teacher.star.TeacherViewStarFragment;
import com.realizer.schoolgeine.teacher.syncup.SyncUpFragment;
import com.realizer.schoolgeine.teacher.timetable.TeacherTimeTableFragment;
import com.realizer.schoolgeine.teacher.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = TeacherHomeworkFragment.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Bitmap bitmap;
    private int counter=0;
    ImageView iv;
    TextView profile_text;
    TextView dispname;
    StringBuilder result;
    String newString;
    Fragment fragment;
    DrawerLayout drawer;
    TextView userName;
    ImageView userImage;
    TextView userInitials;

    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE_FILE };

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private Uri fileUri; // file url to store image/video
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(DrawerActivity.this));
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Singlton.setContext(DrawerActivity.this);
        Singlton.setActivity(DrawerActivity.this);


        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(Config.Account_Name);
        Singlton.setmCredential(mCredential);

        getResultsFromApi();

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideSoftKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                hideSoftKeyboard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideSoftKeyboard();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        userName = (TextView)header.findViewById(R.id.txt_user_name);
        userImage = (ImageView) header.findViewById(R.id.img_user_image);
        userInitials = (TextView) header.findViewById(R.id.img_user_text_image);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName.setText(preferences.getString("DisplayName",""));

        String urlString = preferences.getString("ThumbnailID","");
        Log.d("Image URL",urlString);

        if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
        {
            userImage.setVisibility(View.GONE);
            userInitials.setVisibility(View.VISIBLE);
            String name[]=userName.getText().toString().split(" ");
            String fname = name[0].trim().toUpperCase().charAt(0)+"";
            if(name.length>1)
            {
                String lname = name[1].trim().toUpperCase().charAt(0)+"";
                userInitials.setText(fname+lname);
            }
            else
                userInitials.setText(fname);

        }
        else
        {
            userImage.setVisibility(View.VISIBLE);
            userInitials.setVisibility(View.GONE);
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<urlString.length();i++)
            {
                char c='\\';
                if (urlString.charAt(i) =='\\')
                {
                    urlString.replace("\"","");
                    sb.append("/");
                }
                else
                {
                    sb.append(urlString.charAt(i));
                }
            }
            String newURL=sb.toString();
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length-1]))
            new GetImages(newURL,userImage,userInitials,userName.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
           else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
              //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                userImage.setImageBitmap(bitmap);
            }


        }

        /* Display First Fragment at Launch*/
        navigationView.setCheckedItem(R.id.nav_home);
        Fragment frag = new TeacherDashboardFragment();
        Singlton.setSelectedFragment(frag);
        if (frag != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, frag).commit();

        }

    }


  /*  //downloading thumbnailurl
    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image1) {

            if(image1 != null){
                userImage.setImageBitmap(image1);
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

       if(Singlton.getSelectedFragment() instanceof TeacherDashboardFragment)
       {
           moveTaskToBack(true);
           finish();
       }
       else if (Singlton.getSelectedFragment() != null && Singlton.getSelectedFragment() instanceof FragmentBackPressedListener) {
            ((FragmentBackPressedListener) Singlton.getSelectedFragment()).onFragmentBackPressed();
        }

        hideSoftKeyboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify search_layout parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // update the main content by replacing fragments
        fragment= null;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragment = new TeacherDashboardFragment();
        }
        else if (id == R.id.nav_myclass)
        {
            fragment = MyClass(1);
        }
        else if (id == R.id.nav_homework)
        {
            fragment = HomeworkList("Homework");
        }
        else if (id == R.id.nav_timetable)
        {
            fragment =Syllabus();
        }
        else if (id == R.id.nav_classwork)
        {
            fragment = HomeworkList("Classwork");
        }
        else if (id == R.id.nav_chat)
        {
            fragment = Quries();
        }
        else if (id == R.id.nav_alert)
        {
            fragment = GeneralCommunicationList();
        }
        else if (id == R.id.nav_funcenter)
        {
            fragment = FunCenter();
        }
        else if (id == R.id.nav_star)
        {
            fragment = GiveStar();
        }
        else if (id == R.id.nav_logout)
        {
            Logout();
        }

        else if (id == R.id.nav_holiday)
        {
            fragment = PublicHoliday();
        }
        else if (id == R.id.nav_sync)
        {
            fragment = SyncUp();
          /*  Intent service = new Intent(DrawerActivity.this,ManualSyncService.class);
            Singlton.setManualserviceIntent(service);
            startService(service);*/
        }
        if (fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

        }

        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // for My Class List
    public Fragment MyClass(int i)
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/


        String allStudentList = "1.,,Ajay Shah@@@2.,,Nilam Jadhav@@@3.,,Farhan Bodale@@@4.,,Pravin Jadhav@@@5.,,Ram Magar@@@6.,,Sahil Kadam@@@7.,,Hisha Mulye@@@8.,,Supriya Vichare@@@9.,,Ajay Shah@@@10.,,Nilam Jadhav@@@11.,,Farhan Bodale@@@12.,,Pravin Jadhav@@@13.,,Ram Magar@@@14.,,Sahil Kadam@@@15.,,Hisha Mulye@@@16.,,Supriya Vichare";

        fragment = new TeacherMyClassStudentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("StudentClassList", allStudentList);
        fragment.setArguments(bundle);

        return fragment;
    }

    // for Homework List
    public Fragment HomeworkList(String name)
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/
       // getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard",DrawerActivity.this));
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        fragment = new TeacherHomeworkFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", name);
        fragment.setArguments(bundle);
        return fragment;
    }



    //General Quries
    public Fragment GiveStar()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        fragment = new TeacherViewStarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }
    //General Communication
    public Fragment GeneralCommunicationList()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher1 meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher1 meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher1 meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        fragment = new TeacherGeneralCommunicationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        return fragment;
    }


    // for Syllabus List
    public Fragment Syllabus()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        String dictationlist = "Marathi,,lesson1 and 2_Hindi,,lesson no 4 and 5_English,,lesson no 7,8 and9_History,,lesson no 1,2,3,4 and 5";
        fragment = new TeacherTimeTableFragment();
        Bundle bundle = new Bundle();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putString("DictationList", dictationlist);
        bundle.putInt("Checked", 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    //General Quries
    public Fragment Quries()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        fragment = new TeacherQueryFragment1();
        Bundle bundle = new Bundle();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        return fragment;
    }

    // for Public Holiday List
    public Fragment PublicHoliday()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String publicholiday = "Independence Day,,15/08/2015,,15/08/2015_Ganesh Chaturthi,,17/09/2015,,17/09/2015_Gandhi Jayanti,,02/10/2015,,02/10/2015_Dussehra" +
                ",,22/10/2015,,22/10/2015_Diwali Holiday,,09/11/2015,,26/11/2015_Merry Christmas,,25/12/2015,,25/12/2015_Republic Day,,26/01/2016,,26/01/2016_Mahashivratri,,17/02/2016,,17/02/2016_Holi,,06/03/2016,,06/03/2016_Gudi Padawa,,21/03/2016,,21/03/2016" +
                "_Good Friday,,03/04/2016,,03/04/2016";
        fragment = new TeacherPublicHolidayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PublicHolidayList", publicholiday);
        fragment.setArguments(bundle);
        return fragment;
    }

    // for Public Holiday List
    public Fragment SyncUp()
    {
        fragment = new SyncUpFragment();
        return fragment;
    }
    // for Fun Center List
    public Fragment FunCenter()
    {
       /* String year =String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        HolidayListAsyncTaskGet obj = new HolidayListAsyncTaskGet(UserGlobalData.getInstance().geCompanyLocation(),UserGlobalData.getInstance().getCompanyCode(),year, MainActivity.this,this);
        try{
            result= obj.execute().get();
            String holidayList =result.toString();
            fragment = new HolidayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AnswerHolidayList", holidayList);
            fragment.setArguments(bundle);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fragment;*/

        String images = "Gathering@@@Arts@@@Designs@@@Sports@@@Plantation@@@Gathering@@@Arts@@@Designs@@@Sports@@@Plantation" ;
        TeacherFunCenterFolderFragment fragment = new TeacherFunCenterFolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ImageActivityList", images);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void Logout()

    {
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("Login", "false");
        edit.putString("LogChk", "true");
        edit.commit();

        Intent intent = new Intent(DrawerActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
   /* // for View Star List
    public void TrackPupil()
    {
        FragmentManager fragmentManager = getFragmentManager();
        TeacherTrackingDialogBoxActivity fragment = new TeacherTrackingDialogBoxActivity();
        fragment.setCancelable(false);
        fragment.show(fragmentManager, "Dialog!");

    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }*/


    public void getOption() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Choose Action");

        Intent[] intentArray = {cameraIntent};

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create search_layout media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");




        return mediaFile;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if(data==null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    Log.d("PATH", fileUri.getPath());
                    setPhoto(bitmap);
                    iv.setImageBitmap(bitmap);
                    String path = encodephoto(bitmap);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ProfilePicPath",path);
                    editor.commit();
                    //launchUploadActivity(data);
                }
                else
                    launchUploadActivity(data);



            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
               /* Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();*/

            } else {
                // failed to capture image
                Config.alertDialog(DrawerActivity.this,"Camera","Sorry, Failed to capture Image");
              /*  Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();*/
            }

        }
        else if(requestCode == REQUEST_AUTHORIZATION) {

            if(Singlton.getManualserviceIntent() != null)
                stopService(Singlton.getManualserviceIntent());


            if (resultCode == RESULT_OK) {

                Intent service = new Intent(DrawerActivity.this,ManualSyncService.class);
                Singlton.setManualserviceIntent(service);
                startService(service);
            }

        }
        else if(requestCode == REQUEST_ACCOUNT_PICKER) {
            // Initialize credentials and service object.
            GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                String accountName =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if (accountName != null) {
                    SharedPreferences settings =
                            getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    mCredential.setSelectedAccountName(accountName);
                    Singlton.setmCredential(mCredential);

                    if(Singlton.getManualserviceIntent() != null)
                        stopService(Singlton.getManualserviceIntent());

                        Intent service = new Intent(DrawerActivity.this,ManualSyncService.class);
                        Singlton.setManualserviceIntent(service);
                        startService(service);

                }
            }
        }
        else if(requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                Config.alertDialog(DrawerActivity.this,"Google Play Service",
                        "This app requires Google Play Services. " +
                                "Please install Google Play Services on your device " +
                                "and relaunch this app.");
            }
            else {
                getResultsFromApi();
            }
        }

    }


    private void launchUploadActivity(Intent data){

        if(data.getData()!=null)
        {
            try
            {
                if (bitmap != null)
                {
                    //bitmap.recycle();
                }

                InputStream stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
                iv.setImageBitmap(bitmap);
                String path = encodephoto(bitmap);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ProfilePicPath",path);
                editor.commit();
            }

            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        else
        {
            bitmap=(Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bitmap);
        }
    }

    //Encode image to Base64 to send to server
    private void setPhoto(Bitmap bitmapm) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");

            }
        }
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpeg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }
    //Encode image to Base64 to send to server
    private String encodephoto(Bitmap bitmapm) {
        String imagebase64string="";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] byteArrayImage = baos.toByteArray();
            imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagebase64string;
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Lock Drawer Avoid opening it
     */
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * UnLockDrawer and allow opening it
     */
    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void showMyActionBar() {
        getSupportActionBar().show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(Singlton.getInitiateChat() != null && Singlton.getInitiateChat().isShown())
        {
            return true;
        }
        else if(Singlton.getMessageCenter() != null && Singlton.getMessageCenter().isShown())
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // condition to lock the screen at the time of refreshing
        if(Singlton.getInitiateChat() != null && Singlton.getInitiateChat().isShown()){
            return true;
        }
        else if(Singlton.getMessageCenter() != null && Singlton.getMessageCenter().isShown())
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        } else if (! Config.isConnectingToInternet(DrawerActivity.this)) {
            Config.alertDialog(DrawerActivity.this, "Network Error", "Server Not Responding Please Try After Some Time");
        }
        else
        {
            Singlton.setmCredential(mCredential);
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
                DrawerActivity.this,
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

}
