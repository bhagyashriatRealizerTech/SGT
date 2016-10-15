package com.realizer.schoolgeine.teacher;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Notification.NotificationModel;
import com.realizer.schoolgeine.teacher.Notification.TeacherNotificationListAdapter;
import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.TeacherQueryViewFragment;
import com.realizer.schoolgeine.teacher.view.Action;
import com.realizer.schoolgeine.teacher.view.SwipeDetector;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.chat.TeacherQueryFragment1;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.funcenter.TeacherFunCenterFolderFragment;
import com.realizer.schoolgeine.teacher.generalcommunication.TeacherGeneralCommunicationFragment;
import com.realizer.schoolgeine.teacher.holiday.TeacherPublicHolidayFragment;
import com.realizer.schoolgeine.teacher.homework.TeacherHomeworkFragment;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassStudentFragment;
import com.realizer.schoolgeine.teacher.star.TeacherViewStarFragment;
import com.realizer.schoolgeine.teacher.timetable.TeacherTimeTableFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Win on 11/5/2015.
 */
public class TeacherDashboardFragment extends Fragment implements View.OnClickListener {

    TextView myclass, homework, giveStar, timeTable, queries, funCenter, communication, trackPupil, publicHoliday, classwork;
    ListView notificationList;
    SwipeDetector swipeDetector;
    ArrayList<NotificationModel> notificationData;
    int onStartCount = 0;
    TeacherNotificationListAdapter notificationAdapter;
    LinearLayout userInfoLayout;
    ImageView picUser;
    TextView nameUSer,userInitials;
    MessageResultReceiver resultReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_dashboard_fragment, container, false);
        Controls(rootView);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        myclass.setOnClickListener(this);
        homework.setOnClickListener(this);
        giveStar.setOnClickListener(this);
        timeTable.setOnClickListener(this);
        queries.setOnClickListener(this);
        funCenter.setOnClickListener(this);
        communication.setOnClickListener(this);
        publicHoliday.setOnClickListener(this);
        classwork.setOnClickListener(this);

        notificationData = new ArrayList<>();

        swipeDetector = new SwipeDetector();
        notificationList.setOnTouchListener(swipeDetector);

        Singlton obj = Singlton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected()) {

                    if (swipeDetector.getAction() == Action.LR) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.RL) {
                        // perform any task


                        final Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.anim_slide_out_left);
                        view.startAnimation(animation);

                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if(notificationData.size() >0) {
                                DatabaseQueries qr = new DatabaseQueries(getActivity());
                                qr.deleteNotificationRow(notificationData.get(position).getId());
                                    if (notificationData.size() == 1)
                                        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    else {
                                        notificationData.remove(position);
                                        notificationAdapter.notifyDataSetChanged();
                                    }
                                }

                            }
                        }, 500);


                    }
                    else if (swipeDetector.getAction() == Action.TB) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.BT) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.None) {
                        // perform any task

                    }

                }
                else {

                    if (position<notificationData.size()){
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        DatabaseQueries qr = new DatabaseQueries(getActivity());

                        if (notificationData.get(position).getNotificationtype().equalsIgnoreCase("Homework")
                                || notificationData.get(position).getNotificationtype().equalsIgnoreCase("Classwork")) {

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("STANDARD", notificationData.get(position).getAdditionalData1().split("@@@")[0]);
                            editor.putString("DIVISION", notificationData.get(position).getAdditionalData1().split("@@@")[1]);
                            editor.commit();

                            qr.deleteNotificationRow(notificationData.get(position).getId());

                            Homework(notificationData.get(position).getNotificationtype());
                        } else if (notificationData.get(position).getNotificationtype().equalsIgnoreCase("TimeTable")) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("STANDARD", notificationData.get(position).getAdditionalData1().split("@@@")[1]);
                            editor.putString("DIVISION", notificationData.get(position).getAdditionalData1().split("@@@")[2]);
                            editor.commit();

                            qr.deleteNotificationRow(notificationData.get(position).getId());
                            TimeTable("b");
                        } else if (notificationData.get(position).getNotificationtype().equalsIgnoreCase("Query")) {
                            String uid = notificationData.get(position).getAdditionalData2();
                            String urlImage = null;
                            String userData[] = notificationData.get(position).getAdditionalData1().trim().split("@@@");
                            if (userData.length > 2)
                                urlImage = userData[2];

                            qr.updateInitiatechat(preferences.getString("STANDARD", ""), preferences.getString("DIVISION", ""), userData[0], "true", uid, 0, urlImage);
                            Bundle bundle = new Bundle();
                            bundle.putString("USERID", uid);
                            bundle.putString("SENDERNAME", userData[0]);
                            bundle.putString("Stand", preferences.getString("STANDARD", ""));
                            bundle.putString("Divi", preferences.getString("DIVISION", ""));
                            bundle.putString("UrlImage", urlImage);

                            qr.deleteNotificationRow(notificationData.get(position).getId());

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();
                            Singlton.setSelectedFragment(fragment);
                            fragment.setArguments(bundle);
                            transaction.replace(R.id.frame_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                    }
                }
            }
        });


        return rootView;
    }



    public void Controls(View v) {
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        myclass = (TextView) v.findViewById(R.id.txttdashmyclass);
        myclass.setTypeface(face);
        homework = (TextView) v.findViewById(R.id.txttdashhomework);
        homework.setTypeface(face);
        giveStar = (TextView) v.findViewById(R.id.txttdashviewstar);
        giveStar.setTypeface(face);
        timeTable = (TextView) v.findViewById(R.id.txttdashtimetable);
        timeTable.setTypeface(face);
        queries = (TextView) v.findViewById(R.id.txttdashqueries);
        queries.setTypeface(face);
        funCenter = (TextView) v.findViewById(R.id.txttdashfuncenter);
        funCenter.setTypeface(face);
        communication = (TextView) v.findViewById(R.id.txttdashcommunication);
        communication.setTypeface(face);
        publicHoliday = (TextView) v.findViewById(R.id.txttdashpublicholiday);
        publicHoliday.setTypeface(face);
        classwork = (TextView) v.findViewById(R.id.txttdashclasswork);
        classwork.setTypeface(face);

        notificationList = (ListView) v.findViewById(R.id.lst_notification);
        userInfoLayout = (LinearLayout)v.findViewById(R.id.linuserlayout);
        picUser = (ImageView)v.findViewById(R.id.iv_uImage);
        nameUSer = (TextView)v.findViewById(R.id.txtuName);
        userInitials = (TextView)v.findViewById(R.id.img_user_text_image);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txttdashmyclass:
               MyClass("b");
                break;

            case R.id.txttdashhomework:
                Homework("Homework");
                break;
            case R.id.txttdashviewstar:
                GiveStar("b");
                break;
            case R.id.txttdashtimetable:

                TimeTable("b");
                break;
            case R.id.txttdashqueries:
                Queries("b");
                break;
            case R.id.txttdashfuncenter:
                FunCenter("b");
                break;
            case R.id.txttdashcommunication:
                Communication("b");
                break;
          /*  case R.id.txttdashtrackpupil:
                //Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                TrackPupil("b");
                break;*/
            case R.id.txttdashpublicholiday:
                PublicHoliday("b");
                break;

            case R.id.txttdashclasswork:
                Homework("Classwork");
                break;

        }

    }

    public void MyClass(String res) {

       /* String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",1);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");*/

        String allStudentList = "1.,,Ajay Shah@@@2.,,Nilam Jadhav@@@3.,,Farhan Bodale@@@4.,,Pravin Jadhav@@@5.,,Ram Magar@@@6.,,Sahil Kadam@@@7.,,Hisha Mulye@@@8.,,Supriya Vichare@@@9.,,Ajay Shah@@@10.,,Nilam Jadhav@@@11.,,Farhan Bodale@@@12.,,Pravin Jadhav@@@13.,,Ram Magar@@@14.,,Sahil Kadam@@@15.,,Hisha Mulye@@@16.,,Supriya Vichare";
        TeacherMyClassStudentFragment fragment = new TeacherMyClassStudentFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentClassList", allStudentList);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

    }
    // For Homework
    public void Homework(String res) {
        // Get Output as
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        TeacherHomeworkFragment fragment = new TeacherHomeworkFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT",res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    // For Give Star
    public void GiveStar(String res) {
        // Get Output as

        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        TeacherViewStarFragment fragment = new TeacherViewStarFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }


    // For Time Table
    public void TimeTable(String res) {
        // Get Output as
        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                               "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        TeacherTimeTableFragment fragment = new TeacherTimeTableFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putInt("Checked", 1);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }


    // For Queries
    public void Queries(String res) {
        // Get Output as
        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }



    // For General Communication
    public void Communication(String res) {
        // Get Output as
        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher1 meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher1 meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher1 meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        TeacherGeneralCommunicationFragment fragment = new TeacherGeneralCommunicationFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }
    // For Fun Center
    public void FunCenter(String res) {
        String images = "Gathering@@@Arts@@@Designs@@@Sports@@@Plantation@@@Gathering@@@Arts@@@Designs@@@Sports@@@Plantation" ;
        TeacherFunCenterFolderFragment fragment = new TeacherFunCenterFolderFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("ImageActivityList", images);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

    }

    // For Public Holiday
    public void PublicHoliday(String res) {
        // Get Output as
        String publicholiday = "Independence Day,,15/08/2015,,15/08/2015_Ganesh Chaturthi,,17/09/2015,,17/09/2015_Gandhi Jayanti,,02/10/2015,,02/10/2015_Dussehra" +
                ",,22/10/2015,,22/10/2015_Diwali Holiday,,09/11/2015,,26/11/2015_Merry Christmas,,25/12/2015,,25/12/2015_Republic Day,,26/01/2016,,26/01/2016_Mahashivratri,,17/02/2016,,17/02/2016_Holi,,06/03/2016,,06/03/2016_Gudi Padawa,,21/03/2016,,21/03/2016" +
                "_Good Friday,,03/04/2016,,03/04/2016";
        TeacherPublicHolidayFragment fragment = new TeacherPublicHolidayFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("PublicHolidayList", publicholiday);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    public class GetNotificationList extends AsyncTask<Void, Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            DatabaseQueries qr = new DatabaseQueries(getActivity());
            notificationData = qr.GetNotificationsData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(notificationData.size()>0) {
                notificationList.setVisibility(View.VISIBLE);
                userInfoLayout.setVisibility(View.GONE);
                notificationAdapter = new TeacherNotificationListAdapter(getActivity(), notificationData);
                notificationList.setAdapter(notificationAdapter);
            }
            else
            {
                notificationList.setVisibility(View.GONE);
                userInfoLayout.setVisibility(View.VISIBLE);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                nameUSer.setText(preferences.getString("DisplayName", ""));

                String urlString = preferences.getString("ThumbnailID","");
                Log.d("Image URL", urlString);

                if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                {
                    picUser.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    String name[]=nameUSer.getText().toString().split(" ");
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
                    picUser.setVisibility(View.VISIBLE);
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
                    if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL,picUser,userInitials,nameUSer.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
                    else
                    {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        picUser.setImageBitmap(bitmap);
                    }


                }
            }

        }

    }




    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if (update.equals("UpdateNotification")) {

                new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    //Recive the result when new Message Arrives
    class MessageResultReceiver extends ResultReceiver
    {
        public MessageResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 1){
                getActivity().runOnUiThread(new UpdateUI("UpdateNotification"));
            }
        }
    }


}