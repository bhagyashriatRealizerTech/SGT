package com.realizer.schoolgeine.teacher.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.chat.asynctask.TeacherQueryAsyncTaskPost;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.ChatSectionIndexer;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryAddedContactListAdapter;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryAutoCompleteListAdapter;
import com.realizer.schoolgeine.teacher.chat.model.AddedContactModel;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryFragment extends Fragment implements OnTaskCompleted, FragmentBackPressedListener {

    ImageView selectStudent;
    EditText autocomplteTextView;
    EditText message;
    TextView send;
    String studinfo;
    ListView addedStudent,nameList;
    ArrayList<AddedContactModel> studentList,selectedList;
    TeacherQueryAutoCompleteListAdapter autoCompleteAdapter;
    TeacherQueryAddedContactListAdapter adapter;
    int qid[];
    MenuItem search,done;
    ProgressWheel loading;
    DatabaseQueries qr;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_query_new_message, container, false);
        setHasOptionsMenu(true);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("New Message", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        initiateView(rootView);
         studentList = new ArrayList<>();
         studentList = GetStudentList();
         selectedList = new ArrayList<AddedContactModel>();
         qr = new DatabaseQueries(getActivity());
         Singlton.setSelectedStudentList(selectedList);

        Collections.sort(studentList, new ChatNoCaseComparator());
        autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(),studentList);
        Config.hideSoftKeyboardWithoutReq(getActivity(), autocomplteTextView);
        Config.hideSoftKeyboardWithoutReq(getActivity(), message);
        autocomplteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()>0)
                {
                    ArrayList <AddedContactModel> listClone = new ArrayList<AddedContactModel>();
                    for(AddedContactModel d : studentList){
                        if(d.getUserName() != null && d.getUserName().contains(s.toString()))
                        //something here
                        listClone.add(d);
                    }

                    if(listClone.size()>0) {
                        nameList.setVisibility(View.VISIBLE);
                        autoCompleteAdapter = new TeacherQueryAutoCompleteListAdapter(getActivity(), listClone);
                        nameList.setAdapter(autoCompleteAdapter);
                    }

                }
                else
                {
                    addedStudent.setVisibility(View.VISIBLE);
                    nameList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = nameList.getItemAtPosition(position);

                AddedContactModel addedContactModel = (AddedContactModel) o;
                selectedList.add(addedContactModel);
                Singlton.setSelectedStudentList(selectedList);

                adapter = new TeacherQueryAddedContactListAdapter(getActivity(), selectedList);
                addedStudent.setAdapter(adapter);
                addedStudent.setVisibility(View.VISIBLE);
                nameList.setVisibility(View.GONE);
                Config.hideSoftKeyboardWithoutReq(getActivity(),autocomplteTextView);
                autocomplteTextView.setText("");
            }
        });

        selectStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ChatSectionIndexer.class);
                getActivity().startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.hideSoftKeyboardWithoutReq(getActivity(),message);
                if (addedStudent.getCount() == 0) {
                    Toast.makeText(getActivity(), "No Student Added", Toast.LENGTH_SHORT).show();
                } else if (message.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    // sendTo = getList();
                    if (selectedList.size() == 0 || message.getText().toString().trim().length() == 0) {

                    } else {

                        loading.setVisibility(View.VISIBLE);
                        Singlton.setInitiateChat(loading);
                        //String uidstud = "";
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
                        String date = df.format(calendar.getTime());
                        Date sendDate = new Date();
                        try {
                            sendDate = df.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                       // String uidstudarr[] = new String[selectedList.size()];
                        int qidcount = 0;

                        try {

                            qid = new int[selectedList.size()];
                            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            String uidname = sharedpreferences.getString("UidName", "");
                            String stdC=sharedpreferences.getString("STANDARD", "");
                            String divC = sharedpreferences.getString("DIVISION", "");
                            String thumbnailUrl = sharedpreferences.getString("ThumbnailID", "");
                            String senderName = sharedpreferences.getString("DisplayName", "");
                            long n = -1;

                            for (int k = 0; k < selectedList.size(); k++) {
                                n = qr.insertQuery("true", uidname, selectedList.get(k).getUserId(), message.getText().toString().trim(), date, "true", sendDate,thumbnailUrl,senderName);
                                if (n > 0) {
                                    // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                                    n = -1;

                                    qid[qidcount] = qr.getQueryId();
                                    n = qr.insertQueue(qid[qidcount], "Query", "2", date);

                                    qidcount = qidcount + 1;

                                }
                            }
                            if (n > 0) {
                                // Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                                n = -1;
                                if (Config.isConnectingToInternet(getActivity())) {
                                    for (int i = 0; i < qid.length; i++) {
                                        TeacherQuerySendModel obj = qr.GetQuery(qid[i]);
                                        TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, getActivity(), TeacherQueryFragment.this, "true");
                                        asyncobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                    }
                                } else {
                                    //String uid[] = univsersalid.split(",");
                                    for (int i = 0; i < selectedList.size(); i++) {
                                        String uname[] = qr.Getuname(selectedList.get(i).getUserId()).split("@@@");
                                        String urlImage = null;
                                        if(uname.length>1)
                                            urlImage = uname[1];
                                        n = qr.updateInitiatechat(stdC, divC.toString(), uname[0], "true", selectedList.get(i).getUserId(), 0,urlImage);
                                    }
                                    if (n > 0) {

                                        loading.setVisibility(View.GONE);
                                        Singlton.setInitiateChat(null);
                                        TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
                                        Singlton.setSelectedFragment(fragment);
                                        Singlton.setMainFragment(fragment);
                                        Bundle bundle = new Bundle();
                                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                        fragment.setArguments(bundle);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_container, fragment);
                                        fragmentTransaction.commit();

                                    }

                               }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        });

        return rootView;
    }

    public class ChatNoCaseComparator implements Comparator<AddedContactModel> {
        public int compare(AddedContactModel s1, AddedContactModel s2) {
            return s1.getUserName().compareToIgnoreCase(s2.getUserName());
        }
    }

    public void initiateView(View view)
    {
        selectStudent = (ImageView) view.findViewById(R.id.imgbtnAddContact);
        autocomplteTextView = (EditText) view.findViewById(R.id.edt_select_contact);
        send = (TextView) view.findViewById(R.id.btnSendText);
        message = (EditText)view.findViewById(R.id.edtmsgtxt);
        addedStudent = (ListView) view.findViewById(R.id.ivaddedContact);
        addedStudent = (ListView) view.findViewById(R.id.ivaddedContact);
        nameList = (ListView)view.findViewById(R.id.lvstudentnamelist);
        loading = (ProgressWheel)view.findViewById(R.id.loading);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
                Config.hideSoftKeyboardWithoutReq(getActivity(),message);
                SwitchClass();
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
        b.putInt("MYCLASS",7);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }


    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String stdC=sharedpreferences.getString("STANDARD", "");
        String divC = sharedpreferences.getString("DIVISION", "");
        if(s.equals("true") && queueListModel.getType().equalsIgnoreCase("Query"))
        {

            long n = qr.deleteQueueRow(queueListModel.getId(),"Query");

            if(n>0)
            {

                //Toast.makeText(getActivity(), "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(queueListModel.getId());
                n=-1;

                n = qr.updateQurySyncFlag(o);

                if(n>0) {
                    // Toast.makeText(getActivity(), "Query updated Successfully", Toast.LENGTH_SHORT).show();

                    n = -1;

                    String uname[] = qr.Getuname(o.getTo()).split("@@@");
                    String urlImage = null;
                    if(uname.length>1)
                        urlImage = uname[1];
                    n = qr.updateInitiatechat(stdC,divC.toString(), uname[0], "true",o.getTo(),0,urlImage);



                }


            }
            loading.setVisibility(View.GONE);
            Singlton.setInitiateChat(null);
            TeacherQueryFragment1 fragment = new TeacherQueryFragment1();
            Singlton.setSelectedFragment(fragment);
            Singlton.setMainFragment(fragment);
            Bundle bundle = new Bundle();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.frame_container,fragment);
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
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
            String name[] = o1.getUname().split(" ");
            String userName = "";
            for(int j=1;j<name.length;j++)
            {
                userName = userName+" "+name[j];
            }
            tDetails.setUserName(userName.trim());
            tDetails.setRollNo(name[0]);
            tDetails.setUserId(o1.getUid());
            tDetails.setProfileimage(o1.getProfilrImage());
            results.add(tDetails);

        }

        return results;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Singlton.isDonclick())
        {
            Singlton.setIsDonclick(Boolean.FALSE);
            if(Singlton.getSelectedStudentList() != null ) {
                selectedList = Singlton.getSelectedStudentList();
                adapter = new TeacherQueryAddedContactListAdapter(getActivity(), selectedList);
                if(Singlton.getSelectedStudentList().size()>0)
                addedStudent.setAdapter(adapter);
                else
                addedStudent.setAdapter(null);
                addedStudent.setVisibility(View.VISIBLE);
                nameList.setVisibility(View.GONE);
            }
        }
        else
        {
            selectedList = Singlton.getSelectedStudentList();
        }

        if(done != null)
            done.setVisible(false);
        if(search != null)
            search.setVisible(false);


        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}
