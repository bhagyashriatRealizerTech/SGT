package com.realizer.schoolgeine.teacher.generalcommunication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.generalcommunication.adapter.TeacherGCommunicationNewListAdapter;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGCommunicationNewListModel;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGeneralCommunicationListModel;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/25/2015.
 */
public class TeacherGCommunicationNewFragment extends Fragment implements OnTaskCompleted, FragmentBackPressedListener {
    TextView txtstd;
    TextView txtclss;
    EditText edtmsg;
    DatabaseQueries qr;
    int qid;
    String cat;
     ListView lsttcategory;
    ArrayList<TeacherGCommunicationNewListModel> teachernames;
    String scode;
    MenuItem search;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_newgcommunication_layout, container, false);

       // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Add Alert", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        setHasOptionsMenu(true);

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
         scode= sharedpreferences.getString("SchoolCode", "");

        //populate list
         teachernames = GetCategoryName();
        qr= new DatabaseQueries(getActivity());
        lsttcategory = (ListView) rootView.findViewById(R.id.lstcategory);
        lsttcategory.setAdapter(new TeacherGCommunicationNewListAdapter(getActivity(), teachernames,TeacherGCommunicationNewFragment.this));
        lsttcategory.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        txtstd = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        edtmsg = (EditText) rootView.findViewById(R.id.edtnewalerts);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
        Config.hideSoftKeyboardWithoutReq(getActivity(), edtmsg);
        return rootView;
    }

    public void setData(String s)
    {
           String name = s;
         String arr[] = name.split(" ");
         String n ="";

        for(int i=0;i<arr.length;i++)
        {
            n= n+arr[i].charAt(0);
        }

        cat = n;
        Log.d("CATE", cat);
    }
    public void InsertData()
    {

        if(txtstd.getText().toString().isEmpty() )
        {
            Config.alertDialog(Singlton.getContext(),"New Alert","Please Select Standard");
            //Toast.makeText(getActivity(), "Select Standard", Toast.LENGTH_SHORT).show();
        }
        else if(txtclss.getText().toString().isEmpty())
        {
            //Toast.makeText(getActivity(), "Select Division", Toast.LENGTH_SHORT).show();
            Config.alertDialog(Singlton.getContext(),"New Alert","Please Select Division");
        }
        else if(edtmsg.getText().toString().isEmpty())
        {
            Config.alertDialog(Singlton.getContext(),"New Alert","Please Enter Description");
           // Toast.makeText(getActivity(), "Enter Discription", Toast.LENGTH_SHORT).show();
        }
        else if(cat==null)
        {
            Config.alertDialog(Singlton.getContext(),"New Alert","Please Select Category");
            //Toast.makeText(getActivity(), "Select Catagory", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            int y = calendar.get(Calendar.YEAR);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            SimpleDateFormat df1 = new SimpleDateFormat("hh:mm");

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String uidname = sharedpreferences.getString("UidName", "");


            String date = df.format(calendar.getTime());
            String year = String.valueOf(y);
            String std = txtstd.getText().toString();
            String div = txtclss.getText().toString();
            String msg = edtmsg.getText().toString();
            String category = cat;
            String sendby = uidname;
            String syncflag = "false";

            String time = df1.format(calendar.getTime());

            long n = qr.insertAnnouncement(year, date, msg, std, div, category, sendby, syncflag);
            if (n >= 0) {
                //Toast.makeText(getActivity(), "Announcement Inserted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;
                int id = qr.getAnnouncementId();
                n = qr.insertQueue(id, "Announcement", "2", time);
                if (n >= 0) {

                    //Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                    n = -1;

                   /* if (isConnectingToInternet()) {
                        TeacherGeneralCommunicationListModel obj = qr.GetAnnouncementID(id);
                        obj.setSchoolCode(scode);
                        TeacherGCommunicationAsyncTaskPost asyncobj = new TeacherGCommunicationAsyncTaskPost(obj, getActivity(), TeacherGCommunicationNewFragment.this, "true");
                        asyncobj.execute();
                    } else {*/
                        TeacherGeneralCommunicationFragment fragment = new TeacherGeneralCommunicationFragment();
                        Singlton.setSelectedFragment(fragment);
                        String categorylist = "SD,,Sports Day_CA,,Cultural Activity_O,,Other_FDC,,Fancy Dress Competition";
                        Bundle bundle = new Bundle();
                        bundle.putString("CategorNameList", categorylist);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.frame_container, fragment);
                        fragmentTransaction.commit();
                        //Toast.makeText(getActivity(), "Announcement Sent Successfully", Toast.LENGTH_SHORT).show();

                    //}


                }


            }

        }
    }


    private ArrayList<TeacherGCommunicationNewListModel> GetCategoryName()
    {

        Bundle b = this.getArguments();
        //"PTA,,Parent Teacher Assosciation,,SD,,Sports Day_CA,,Cultural Activity_O,,Other_FDC,,Fancy Dress Competition"
        String[] categorylist = (b.getString("CategorNameList")).toString().split("_");
        ArrayList<TeacherGCommunicationNewListModel> results = new ArrayList<>();

        for(String category : categorylist)
        {
            String[] catName = category.toString().split(",,");
            TeacherGCommunicationNewListModel catDetails = new TeacherGCommunicationNewListModel();
            catDetails.setCatshortname(catName[0]);
            catDetails.setCatfullname(catName[1]);
            results.add(catDetails);
        }
        return results;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                Config.hideSoftKeyboardWithoutReq(getActivity(),edtmsg);
                SwitchClass();
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(),edtmsg);
                InsertData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",9);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

        long n=-1;
        if(s.equals("true"))
        {
             n = qr.deleteQueueRow(qid,"Announcement");
            if(n>=0)
            {
                //Toast.makeText(getActivity(), "Queue Deleted Successfully", Toast.LENGTH_SHORT).show();
                n=-1;
                TeacherGeneralCommunicationListModel obj = qr.GetAnnouncementID(qid);
                n= qr.updateAnnouncementSyncFlag(obj);
                if(n>=0)
                {
                   // Toast.makeText(getActivity(), "Announcement Updated Successfully", Toast.LENGTH_SHORT).show();
                    n=-1;
                    TeacherGeneralCommunicationFragment fragment = new TeacherGeneralCommunicationFragment();
                    Singlton.setSelectedFragment(fragment);
                    Singlton.setMainFragment(fragment);
                    String categorylist = "SD,,Sports Day_CA,,Cultural Activity_O,,Other_FDC,,Fancy Dress Competition";
                    Bundle bundle = new Bundle();
                    bundle.putString("CategorNameList", categorylist);
                    fragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    //Toast.makeText(getActivity(), "Announcement Sent Successfully", Toast.LENGTH_SHORT).show();
                }

            }

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
    public void onFragmentBackPressed() {
       Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
