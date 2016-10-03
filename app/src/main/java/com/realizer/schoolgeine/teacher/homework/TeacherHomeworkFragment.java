package com.realizer.schoolgeine.teacher.homework;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryModel1ListAdapter;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.homework.adapter.TeacherHomeworkListAdapter;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkListModel;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherHomeworkFragment extends Fragment implements View.OnClickListener , FragmentBackPressedListener {

    DatabaseQueries qr;
    Spinner spinner;
    TextView txtstd ,txtclss ,noHwMsg;
    ListView listHoliday;
    FloatingActionButton newHomework;
    String htext;
    ArrayList<String> listofDate = new ArrayList<>();
    ArrayList<String> listofDay = new ArrayList<>();
    String selectedDate;
    MenuItem done,search;
    MessageResultReceiver resultReceiver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_homework_layout, container, false);
        setHasOptionsMenu(true);
        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        qr = new DatabaseQueries(getActivity());
        spinner = (Spinner) rootView.findViewById(R.id.spLeaveType);
        listHoliday = (ListView) rootView.findViewById(R.id.lstthomework);
       // header = (TextView) rootView.findViewById(R.id.txtheadtext);
        noHwMsg=(TextView) rootView.findViewById(R.id.tvNoDataMsg);
        newHomework = (FloatingActionButton) rootView.findViewById(R.id.imgbtnAddHw);

        Singlton obj = Singlton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

       // header.setText(htext);

        // Spinner Drop down elements
       FillDates();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = listofDate.get(position);
                ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkList(listofDate.get(position));
                if (homewok.size()!=0) {
                    listHoliday.setVisibility(View.VISIBLE);
                    listHoliday.setAdapter(new TeacherHomeworkListAdapter(getActivity(), homewok,htext));
                    noHwMsg.setVisibility(View.GONE);
                }
                else
                {
                    noHwMsg.setVisibility(View.VISIBLE);
                    noHwMsg.setText("No "+htext+" Provided");
                    listHoliday.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));

        //populate list
        selectedDate = listofDate.get(listofDate.size()-1);
        ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkList(listofDate.get(listofDate.size()-1));
            if(homewok.size()>0)
        listHoliday.setAdapter(new TeacherHomeworkListAdapter(getActivity(), homewok,htext));



        listHoliday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listHoliday.getItemAtPosition(position);
                TeacherHomeworkListModel homeworkObj = (TeacherHomeworkListModel) o;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                TeacherHomeworkDetailFragment fragment = new TeacherHomeworkDetailFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("HEADERTEXT",htext);
                bundle.putString("SubjectName",homeworkObj.getSubject());
                bundle.putString("HomeworkDate",listofDate.get(spinner.getSelectedItemPosition()));
                bundle.putString("TeacherName",preferences.getString("DisplayName", ""));
                bundle.putString("Status",homeworkObj.getHasSync());
                bundle.putString("HomeworkImage",homeworkObj.getImage());
                bundle.putString("HomeworkText",homeworkObj.getHomework());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });



        newHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = getArguments();
                String htext = b.getString("HEADERTEXT");

                TeacherHomeworkNewFragment fragment = new TeacherHomeworkNewFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("HEADERTEXT",htext);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return rootView;
    }
    private ArrayList<TeacherHomeworkListModel> GetHomeWorkList(String date)
    {
        Bundle b = this.getArguments();
        ArrayList<TeacherHomeworkModel> hwlst = qr.GetHomeworkData(date, htext, txtstd.getText().toString(), txtclss.getText().toString());

        ArrayList<TeacherHomeworkListModel> results = new ArrayList<>();

        for(int i=0;i<hwlst.size();i++)
        {
            TeacherHomeworkListModel hDetail = new TeacherHomeworkListModel();
            TeacherHomeworkModel obj = hwlst.get(i);
            hDetail.setSubject(obj.getSubject());
            hDetail.setGivenBy(obj.getGivenBy());
            hDetail.setHasSync(obj.getIsSync());
            try {

                if(obj.getHwTxtLst().length()==0)
                    hDetail.setHomework("NoText");
                else
            hDetail.setHomework(obj.getHwTxtLst());

                JSONArray jarr1 = new JSONArray(obj.getHwImage64Lst());
            if(jarr1.length()==0)
            hDetail.setImage("NoImage");
            else
            hDetail.setImage(obj.getHwImage64Lst());
            results.add(hDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }


        public void FillDates() {

            listofDate.clear();
            listofDay.clear();
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);
            int currentdate = c.get(Calendar.DATE);

            if(currentdate>=1 && currentdate<10) {
                if(month>=1 && month<10)
                    listofDate.add(0, "0" + currentdate + "/0" + month + "/" + year);
                else
                    listofDate.add(0, "0" + currentdate + "/" + month + "/" + year);
            }
            else
            {
                if(month>=1 && month<10)
                    listofDate.add(0, "" + currentdate + "/0" + month + "/" + year);
                else
                    listofDate.add(0, "" + currentdate + "/" + month + "/" + year);
            }

            listofDay.add(0, "Today"+" ("+currentdate+" "+Config.getMonth(month)+")");
            for (int i = 1; i <= 6; i++) {
                c.add(Calendar.DATE, -1);
                int month1 = c.get(Calendar.MONTH) + 1;
                int year1 = c.get(Calendar.YEAR);
                int currentdate1 = c.get(Calendar.DATE);
                if(currentdate1>=1 && currentdate1<10) {
                    if(month1>=1 && month1<10)
                        listofDate.add(i, "0" + currentdate1 + "/0" + month1 + "/" + year1);
                    else
                        listofDate.add(i, "0" + currentdate1 + "/" + month1 + "/" + year1);
                }
                else {
                    if(month1>=1 && month1<10)
                        listofDate.add(i, "" + currentdate1 + "/0" + month1 + "/" + year1);
                    else
                        listofDate.add(i, "" + currentdate1 + "/" + month1 + "/" + year1);
                }

                if(i == 1)
                    listofDay.add(i,"Yesterday"+" ("+currentdate1+" "+Config.getMonth(month1)+")");
                else
                    listofDay.add(i, Config.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK))+" ("+currentdate1+" "+Config.getMonth(month1)+")");
                Log.d("DATET", "" + currentdate1 + "/" + month1 + "/" + year1);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, listofDay);
            adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
            for (int i = 0; i < adapter.toString().length(); i++) {
                spinner.setAdapter(adapter);
                break;
            }

            spinner.setSelection(0);
        }

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",2);
        b.putString("HeaderText",htext);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);
        done = menu.findItem(R.id.action_done);
        done.setVisible(false);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                SwitchClass();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

            if(resultCode == 1000){
                getActivity().runOnUiThread(new UpdateUI("RefreshUI"));
            }

        }
    }


    //Update UI
    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RefreshUI")) {
                ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkList(selectedDate);
                if (homewok.size()!=0) {
                    listHoliday.setVisibility(View.VISIBLE);
                    listHoliday.setAdapter(new TeacherHomeworkListAdapter(getActivity(), homewok,htext));
                    noHwMsg.setVisibility(View.GONE);
                }
                else
                {
                    noHwMsg.setVisibility(View.VISIBLE);
                    noHwMsg.setText("No "+htext+" Provided");
                    listHoliday.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(done != null)
            done.setVisible(false);
        if(search != null)
            search.setVisible(false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
