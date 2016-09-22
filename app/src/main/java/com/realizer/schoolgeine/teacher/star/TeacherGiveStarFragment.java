package com.realizer.schoolgeine.teacher.star;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.ChatSectionIndexer;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryAddedContactListAdapter;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryAutoCompleteListAdapter;
import com.realizer.schoolgeine.teacher.chat.model.AddedContactModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQueriesTeacherNameListModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;
import com.realizer.schoolgeine.teacher.star.model.TeacherGiveStarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherGiveStarFragment extends Fragment implements OnTaskCompleted, View.OnClickListener , FragmentBackPressedListener {

    TextView txtclss,txtstd;
    View rootView;
   // Button givestar;
    String studinfo;
    ListView lsttname,nameList;
    DatabaseQueries qr;
    EditText scomment;
    Spinner spinner;
    int LisTCount;
    int giveid;
    String StarText;
    RadioButton nice,great,verygood,terrific,superstar,welldone;
    //ArrayList<String> sendTo;
    String startxt;
    TeacherQueryAddedContactListAdapter adapter;
    String temp;
    ArrayList<String> stud;
    LinearLayout selectStudent;
    ArrayList<AddedContactModel>studentList;
    TeacherQueryAutoCompleteListAdapter autoCompleteAdapter;
    ArrayList<AddedContactModel> studentNameList;
    MenuItem search;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

        rootView = inflater.inflate(R.layout.teacher_givestar_layout, container, false);
        setHasOptionsMenu(true);
        qr = new DatabaseQueries(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        studentList = new ArrayList<>();
        Singlton.setSelectedStudentList(studentList);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Reward Star", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

         lsttname = (ListView) rootView.findViewById(R.id.ivaddedContact);
         scomment = (EditText) rootView.findViewById(R.id.edtcomment);
         nice  = (RadioButton) rootView.findViewById(R.id.chknicework);
         great  = (RadioButton) rootView.findViewById(R.id.chkgreat);
         verygood  = (RadioButton) rootView.findViewById(R.id.chkverygood);
         terrific  = (RadioButton) rootView.findViewById(R.id.chkterrific);
         superstar  = (RadioButton) rootView.findViewById(R.id.chksuper);
         welldone  = (RadioButton) rootView.findViewById(R.id.chkwelldone);

        nameList = (ListView)rootView.findViewById(R.id.lvstudentnamelist);
        selectStudent = (LinearLayout) rootView.findViewById(R.id.layout_select_recipient);

        nice.setOnClickListener(this);
        great.setOnClickListener(this);
        verygood.setOnClickListener(this);
        terrific.setOnClickListener(this);
        superstar.setOnClickListener(this);
        welldone.setOnClickListener(this);
        nice.setChecked(true);
        startxt = "NiceWork";
         Bundle b = getArguments();

        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
       // sendTo = new ArrayList<String>();
        studentNameList = new ArrayList<AddedContactModel>();
        studentNameList = GetStudentList();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
        FillSubjectTypes(rootView);

        Config.hideSoftKeyboardWithoutReq(getActivity(), scomment);

        selectStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Singlton.setSelectedStudeonBackKeyPress(studentList);
                Intent intent = new Intent(getActivity(), ChatSectionIndexer.class);
                getActivity().startActivity(intent);

            }
        });


        return rootView;
    }


    public void rewardStar()
    {
        //sendTo = getList();
        String uidstud="";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("kk:mm:ss");
        String date = df.format(calendar.getTime());
        String time = df1.format(calendar.getTime());
        String commnt = scomment.getText().toString();
        String sub = spinner.getSelectedItem().toString();

        if(studentList.size()==0)
        {
            Config.alertDialog(Singlton.getContext(), "Reward Star", "Please Select t least 1 Student");
            //Toast.makeText(getActivity(), "Select Student", Toast.LENGTH_SHORT).show();
        }
        else if(commnt.equals("")) {
            Config.alertDialog(Singlton.getContext(), "Reward Star", "Please Enter Comment");
            //Toast.makeText(getActivity(), "Please Enter Comment", Toast.LENGTH_SHORT).show();
        }
        else
        {

            for(int i=0;i<studentList.size();i++)
            {
                if (i == studentList.size() - 1) {
                    uidstud = uidstud + studentList.get(i).getUserId();
                }
                else {
                    uidstud = uidstud + studentList.get(i).getUserId() + ",";
                }
            }

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String uidname = sharedpreferences.getString("UidName", "");

            Log.d("INSERT", uidname + uidstud + date + sub + startxt + commnt);
            long n= qr.insertGiveStar(uidname,uidstud,date,sub,startxt,commnt,txtstd.getText().toString(),txtclss.getText().toString(),time);
            if(n>0) {
                //Toast.makeText(getActivity(), "Star Inserted Successfully", Toast.LENGTH_SHORT).show();
                n = -1;
                giveid = qr.getGiveStarId();
                TeacherGiveStarModel o1 = qr.GetStar(giveid);
                n = qr.insertQueue(giveid,"GiveStar","4",o1.getStardate());
                if(n>0) {
                    /*if(isConnectingToInternet())
                    {
                        TeacherGiveStarModel o = qr.GetStar(giveid);
                        TeacherGiveStarAsyncTaskPost obj = new TeacherGiveStarAsyncTaskPost(o, getActivity(), TeacherGiveStarFragment.this,"true");
                        obj.execute();
                    }
                    else {*/
                        TeacherViewStarFragment fragment = new TeacherViewStarFragment();
                        Singlton.setSelectedFragment(fragment);
                        Singlton.setMainFragment(fragment);
                        Bundle bundle = new Bundle();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragment.setArguments(bundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.frame_container, fragment);
                        fragmentTransaction.commit();
                   // }

                }


            }

            lsttname.setAdapter(null);
        }
    }


    public void FillSubjectTypes(View v)
    {
        // set adapter
       ArrayList<String> listofSubject = qr.GetSub(txtstd.getText().toString(),txtclss.getText().toString());
        spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listofSubject);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        for(int i=0;i<adapter.toString().length();i++) {
            spinner.setAdapter(adapter);
            break;
        }
        spinner.setSelection(0);
    }

    public void FillDateOfMonth()
    {
        Calendar c = Calendar.getInstance();
        int monthMaxDays = c.getActualMaximum(Calendar.MONTH);
        Log.d("MONTH", "" + monthMaxDays);

    }



    private ArrayList<TeacherQueriesTeacherNameListModel> GetStudentName()
    {
        Bundle b = getArguments();
        int k = b.getInt("FLAG",0);
        ArrayList<TeacherQueriesTeacherNameListModel> results = new ArrayList<>();
        if(k==1)
        {
            ArrayList<String> temp = b.getStringArrayList("NameList");
            for(int i=0;i<temp.size();i++)
            {
                TeacherQueriesTeacherNameListModel tDetails = new TeacherQueriesTeacherNameListModel();
                tDetails.setName(temp.get(i));
                results.add(tDetails);
            }
        }
        else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String s = preferences.getString("STANDARD", "");
            String d = preferences.getString("DIVISION", "");
            DatabaseQueries qr = new DatabaseQueries(getActivity());
            String sdata = qr.GetAllTableData(s, d);
            studinfo = sdata;
            //ArrayList<TeacherQueriesTeacherNameListModel> results = new ArrayList<>();
            try {
                JSONArray arr = new JSONArray(sdata);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    TeacherQueriesTeacherNameListModel tDetails = new TeacherQueriesTeacherNameListModel();
                    tDetails.setSubname(obj.getString("classRollNo"));
                    tDetails.setName(obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName"));
                    results.add(tDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                Config.hideSoftKeyboardWithoutReq(getActivity(),scomment);
                SwitchClass();
                return true;
            case R.id.action_done:
                Config.hideSoftKeyboardWithoutReq(getActivity(),scomment);
                rewardStar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void getstudinfo()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String s = preferences.getString("STANDARD", "");
        String d = preferences.getString("DIVISION", "");
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        String sdata = qr.GetAllTableData(s, d);
        studinfo = sdata;
    }

    public void open(String s){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(s);
        final String temp = s;
        alertDialogBuilder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                removStud(temp);
                LisTCount =1;
                ArrayList<String> stud = getList();

                ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,stud);
                lsttname.setAdapter(adapter);
                lsttname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String temp = String.valueOf(lsttname.getItemAtPosition(position));
                        open(temp);
                    }
                });

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public ArrayList<String> getList()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> set = preferences.getStringSet("NameList", null);
        Set<String> set1 = preferences.getStringSet("SetList", null);
        Set<String> set2 = preferences.getStringSet("SearchList", null);
        ArrayList<String> result = new ArrayList<String>();

        if (set == null) {

        } else {
            ArrayList<String> sample = new ArrayList<String>(set);
            result.addAll(sample);
        }

        if (set1 == null) {

        } else {
            ArrayList<String> sample1 = new ArrayList<String>(set1);
            result.addAll(sample1);
        }

        if (set2 == null) {

        } else {
            ArrayList<String> sample2 = new ArrayList<String>(set2);
            result.addAll(sample2);
        }
        return result;
    }

    public void removStud(String s)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> setall  = preferences.getStringSet("NameList", null);
        Set<String> setset = preferences.getStringSet("SetList", null);
        Set<String> setsearch = preferences.getStringSet("SearchList", null);
        SharedPreferences.Editor editor = preferences.edit();
        if(setall!=null && !setall.isEmpty()) {
            for (String temp : setall)
                if (s.equals(temp)) {
                    setall.remove(temp);
                    editor.putStringSet("NameList", setall);
                    break;
                }
        }

        if(setsearch!=null && !setsearch.isEmpty()) {
            for (String temp : setsearch)
                if (s.equals(temp)) {
                    setsearch.remove(temp);
                    editor.putStringSet("SearchList", setsearch);
                    break;
                }
        }
        if(setset!=null && !setset.isEmpty()) {
            for (String temp : setset)
                if (s.equals(temp)) {
                    setset.remove(temp);
                    editor.putStringSet("SetList", setset);
                    break;
                }
        }
        editor.commit();
    }


    public void SwitchClass()
    {

            String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
            TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
            Singlton.setSelectedFragment(newTermDialogFragment);
            FragmentManager fragmentManager = getFragmentManager();
            Bundle b =new Bundle();
            b.putString("StudentClassList", classList);
            b.putInt("MYCLASS",3);
            newTermDialogFragment.setArguments(b);
            newTermDialogFragment.setCancelable(false);
            newTermDialogFragment.show(fragmentManager, "Dialog!");
        }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

        if(s.equals("true"))
        {
            long n = qr.deleteQueueRow(giveid,"GiveStar");
            if(n>0)
            {

               // Toast.makeText(getActivity(), "Queue Deleted Successfully", Toast.LENGTH_SHORT).show();
                n =-1;

                TeacherGiveStarModel o = qr.GetStar(giveid);
                n= qr.updateGiveStarSyncFlag(o);

                if(n>0)
                {

                    TeacherViewStarFragment fragment = new TeacherViewStarFragment();
                    Singlton.setMainFragment(fragment);
                    Singlton.setSelectedFragment(fragment);
                    FragmentManager manager = getActivity().getFragmentManager();
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.remove(fragment);
                    manager.popBackStack();
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();

                    scomment.setText(" ");
                    nice.setChecked(true);
                    great.setChecked(false);
                    verygood.setChecked(false);
                    terrific.setChecked(false);
                    superstar.setChecked(false);
                    welldone.setChecked(false);
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
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.chknicework:
                setStar("NiceWork",nice);
                break;
            case R.id.chkgreat:
                setStar("Great",great);
                break;
            case R.id.chkwelldone:
                setStar("WellDone",welldone);
                break;
            case R.id.chkterrific:
                setStar("Terrific",terrific);
                break;
            case R.id.chkverygood:
                setStar("VeryGood",verygood);
            break;
            case R.id.chksuper:
                setStar("SupreStar",superstar);
            break;



        }
    }


    public void setStar(String res,RadioButton name)
    {

        nice.setChecked(false);
        verygood.setChecked(false);
        great.setChecked(false);
        terrific.setChecked(false);
        welldone.setChecked(false);
        superstar.setChecked(false);
        name.setChecked(true);

        startxt = res;

    }

    private ArrayList<AddedContactModel> GetStudentList()
    {
        ArrayList<AddedContactModel> results = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String stdC=preferences.getString("STANDARD", "");
        String divC = preferences.getString("DIVISION", "");
        ArrayList<TeacherQuery1model> temp = qr.GetstudList(stdC,divC);
        for(int i =0;i<temp.size();i++)
        {
            AddedContactModel tDetails = new AddedContactModel();
            TeacherQuery1model o1= temp.get(i);
            tDetails.setUserName(o1.getUname());
            tDetails.setUserId(o1.getUid());
            results.add(tDetails);
        }

        return results;
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
        if(Singlton.isDonclick())
        {
            Singlton.setIsDonclick(Boolean.FALSE);
            if(Singlton.getSelectedStudentList() != null) {
                studentList = Singlton.getSelectedStudentList();
                if(Singlton.getSelectedStudentList().size()>0) {
                    adapter = new TeacherQueryAddedContactListAdapter(getActivity(), studentList);
                    lsttname.setAdapter(adapter);
                }
                else
                lsttname.setAdapter(null);

                lsttname.setVisibility(View.VISIBLE);
            }
        }
        if(search != null)
            search.setVisible(false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
