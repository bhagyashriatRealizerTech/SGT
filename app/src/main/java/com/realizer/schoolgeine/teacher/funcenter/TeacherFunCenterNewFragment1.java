package com.realizer.schoolgeine.teacher.funcenter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Win on 26/03/2016.
 */
public class TeacherFunCenterNewFragment1 extends Fragment implements View.OnClickListener , OnTaskCompleted, FragmentBackPressedListener
{
    Bitmap bitmap;
    FrameLayout frameLayout;
    String imgDecodableString;
    ImageView imageview;
    VideoView video;
    private ArrayList<String> imagesPathList;
    private Uri fileUri;
    DatabaseQueries qr;
    GridView gridView;
    TextView std,div;
    String thmbnl;
    int count=0;
    //GridAdapter gridAdapter;
     EditText eventname, eventdate;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    private static final String TAG = TeacherFunCenterNewFragment1.class.getSimpleName();
    int eventid;
    MenuItem search,switchClass;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.funcenter_new_fragment, container, false);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Add Event", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        imageview = (ImageView) rootView.findViewById(R.id.btnCapturePicture1);
        eventname = (EditText) rootView.findViewById(R.id.edtevent);
        eventdate = (EditText) rootView.findViewById(R.id.edteventdate);
        std= (TextView) rootView.findViewById(R.id.txttclassname1);
        div= (TextView) rootView.findViewById(R.id.txttdivname1);

        qr = new DatabaseQueries(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        std.setText(preferences.getString("STANDARD", ""));
        div.setText(preferences.getString("DIVISION", ""));
        Config.hideSoftKeyboardWithoutReq(getActivity(), eventname);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOption();

            }
        });

        eventdate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        showTruitonDatePickerDialog(v);
                        break;
                }
                return false;
            }
        });



        eventname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eventname.getText().length() > 0) {
                    eventname.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eventdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (eventdate.getText().length() > 0) {
                    eventdate.setError(null);
                }
            }
        });


        return rootView;
    }


    public void createEvent()
    {
        if(eventname.getText().toString().isEmpty())
        {
            eventname.setError(getString(R.string.FunCenterEventName));
        }
        else if (eventdate.getText().toString().isEmpty())
        {
            eventdate.setError(getString(R.string.FunCenterEventDate));
        }
        else if(imageview.getDrawable()==null)
        {
            Config.alertDialog(Singlton.getContext(), "Gallery", getString(R.string.FunCenterSelectImage));
            //Toast.makeText(getActivity(),getString(R.string.FunCenterSelectImage), Toast.LENGTH_SHORT).show();
            //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.FunCenterSelectImage)).toString());
        }
        else
        {

            Bitmap bitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();

            String encodedImage = ImageStorage.saveEventToSdCard(bitmap, eventname.getText().toString(), getActivity());

            String eventDatenew [] = eventdate.getText().toString().split("/");
            String date = eventDatenew[1];
            String month = eventDatenew[0];
            String newDate = eventdate.getText().toString();

            if(Integer.valueOf(date)>0 && Integer.valueOf(date)<10)
                date = "0"+date;

            if(Integer.valueOf(month)>0 && Integer.valueOf(month)<10)
                month = "0"+month;

            newDate = month+"/"+date+"/"+eventDatenew[2];
            String getimg="";
            if(encodedImage != null) {
                String f2[] = encodedImage.split(File.separator);
                getimg = f2[f2.length - 1];
            }
            int currentyear = Calendar.getInstance().get(Calendar.YEAR);

            UUID id = UUID.randomUUID();

            long n = qr.InsertEvent(std.getText().toString(), div.getText().toString(), eventname.getText().toString(), newDate, encodedImage, newDate, currentyear, id.toString(),getimg);

            if (n > 0) {
                n = -1;
                eventid = qr.getEventId();
                SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                n = qr.insertQueue(eventid, "EventMaster", "7", df1.format(Calendar.getInstance().getTime()));

                try {
                    if (n > 0) {
                        n = -1;

                      /*  if (isConnectingToInternet())
                        {

                            TeacherFunCenterEventModel o = qr.GetEventByID(eventid);
                            TeacherFunCenterAsyncTaskPost objasync = new TeacherFunCenterAsyncTaskPost(o, getActivity(), TeacherFunCenterNewFragment1.this, "true");
                            objasync.execute();

                        } else {*/
                            Bundle bundle = new Bundle();
                            TeacherFunCenterGalleryFragment fragment = new TeacherFunCenterGalleryFragment();
                            Singlton.setSelectedFragment(fragment);
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            bundle.putInt("EventId", eventid);
                            bundle.putString("EventUUID", id.toString());
                            bundle.putString("EventName", eventname.getText().toString());
                            fragment.setArguments(bundle);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.frame_container, fragment);
                            fragmentTransaction.commit();
                      //  }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        switchClass = menu.findItem(R.id.action_switchclass);
        switchClass.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(),eventname);
                createEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }


    @Override
    public void onClick(View v)
    {


    }



    public ArrayList<String> GetImgLst() {

        ArrayList<String> Test = new ArrayList<>();
        Bitmap bitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        Test.add(encodedImage);

        return Test;
    }

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

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
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
        } else {
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
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                if(data==null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                    Log.d("PATH", fileUri.getPath());
                    setPhoto(bitmap);

                    imageview.setImageBitmap(bitmap);


                }
                else
                    launchUploadActivity(data);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

                // user cancelled Image capture
              /*  Toast.makeText(getActivity(),
                        "User cancled action", Toast.LENGTH_SHORT)
                        .show();*/
                //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.CanceledImage)).toString());
            } else {
                // failed to capture image
               /* Toast.makeText(getActivity(),
                        "failed to capture image", Toast.LENGTH_SHORT)
                        .show();*/
                Config.alertDialog(Singlton.getContext(), "Camera", "Sorry , Failed to Capture Image");
                //Utils.alertDialog(getActivity(), "", Utils.actionBarTitle(getString(R.string.FailedImage)).toString());
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

                InputStream stream = getActivity().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();

                imageview.setImageBitmap(bitmap);

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


    }


    public void showTruitonDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }



    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
    try{
        s =s.replace("\"","");
        if(s.equals("success")) {
            long n = qr.deleteQueueRow(eventid, "EventMaster");
            if (n > 0) {
                 //Toast.makeText(getActivity(), "Queue Deleted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;

                n = qr.updateEventSyncFlag(qr.GetEventByID(eventid));
                if (n > 0) {

                    Bundle bundle = new Bundle();
                    TeacherFunCenterGalleryFragment fragment = new TeacherFunCenterGalleryFragment();
                    Singlton.setSelectedFragment(fragment);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    bundle.putInt("EventId",  eventid);
                    bundle.putString("EventName", eventname.getText().toString());
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    //Toast.makeText(getActivity(), "Upload data Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    catch (Exception e)
    {
        e.printStackTrace();}
    }

    @Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    public  class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // Do something with the date chosen by the user
            eventdate.setText((month + 1) + "/" + day + "/" + year);
        }
    }



    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(
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
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}