package com.realizer.schoolgeine.teacher.chat;

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
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryModel1ListAdapter;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryFragment1 extends Fragment implements OnTaskCompleted, FragmentBackPressedListener {
    ArrayList<String> sendTo;
    String studinfo;
    int LisTCount;
    ListView lsttname;
    FloatingActionButton initiate;
    int qid;
    DatabaseQueries qr;
    ArrayAdapter<String> adapter;
    MessageResultReceiver resultReceiver;
    TextView txtstd,txtclss;
    MenuItem search,done;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_queries_layout1, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Chat", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        lsttname = (ListView) rootView.findViewById(R.id.lsttname);
        initiate = (FloatingActionButton) rootView.findViewById(R.id.txtinitiatechat);
        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        setHasOptionsMenu(true);
        qr =new DatabaseQueries(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String stdC=preferences.getString("STANDARD", "");
        final String divC = preferences.getString("DIVISION", "");
        txtstd.setText(stdC);
        txtclss.setText(divC);
        ArrayList<TeacherQuery1model>  temp = qr.GetInitiatedChat(stdC,divC,"true");
        ArrayList<TeacherQuery1model>  chat =getThreadList(temp);

     /*  ArrayList<TeacherQuery1model> temp = getTempList();*/
        if(temp.size()!=0)
        {

            lsttname.setAdapter(new TeacherQueryModel1ListAdapter(getActivity(), chat));
        }

        Singlton obj = Singlton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);



        lsttname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = lsttname.getItemAtPosition(position);

                TeacherQuery1model homeworkObj = (TeacherQuery1model)o;
                String uid = homeworkObj.getUid();

                qr.updateInitiatechat(stdC,divC,homeworkObj.getUname(),"true",uid,0,homeworkObj.getProfilrImage());

                    Bundle bundle = new Bundle();
                    bundle.putString("USERID", uid);
                    bundle.putString("SENDERNAME",homeworkObj.getUname());
                    bundle.putString("Stand",stdC);
                    bundle.putString("Divi",divC);
                    bundle.putString("UrlImage",homeworkObj.getProfilrImage());

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();
                    Singlton.setSelectedFragment(fragment);
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();


            }
        });

        initiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
                TeacherQueryFragment fragment = new TeacherQueryFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //bundle.putString("HomeworkList", homewrklist);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });


        return rootView;
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

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",11);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }


    //Update UI
    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RecieveMessage")) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String stdC=preferences.getString("STANDARD", "");
                String divC = preferences.getString("DIVISION", "");
                    ArrayList<TeacherQuery1model> temp = qr.GetInitiatedChat(stdC, divC, "true");
                    ArrayList<TeacherQuery1model> temp1 = getThreadList(temp);
                    if(temp1.size()!=0)
                    {
                        lsttname.setAdapter(new TeacherQueryModel1ListAdapter(getActivity(), temp1));
                    }

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

            if(resultCode == 100){
                getActivity().runOnUiThread(new UpdateUI("RecieveMessage"));
            }
            if(resultCode == 200){
                getActivity().runOnUiThread(new UpdateUI("SendMessageMessage"));
            }

        }
    }

    public ArrayList<TeacherQuery1model> getThreadList(ArrayList<TeacherQuery1model>  userId)
        {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
            ArrayList<TeacherQuery1model> temp = new ArrayList<>();
            for(int i=0;i<userId.size();i++) {
                TeacherQuery1model temp1 =  new TeacherQuery1model();
                TeacherQuerySendModel qlst = qr.GetLastMessageData(userId.get(i).getUid());
                temp1.setUid(userId.get(i).getUid());
                String name[] = userId.get(i).getUname().trim().split(" ");
                String userName = "";
                for(int j=0;j<name.length;j++)
                {
                    userName = userName+" "+name[j];
                }
                temp1.setUname(userName);
                temp1.setUnreadCount(userId.get(i).getUnreadCount());
                temp1.setLastMsgDate(qlst.getSentTime());
                temp1.setLastMessage(qlst.getText());
                temp1.setSendername(qlst.getMsgSenderName());
                temp1.setProfilrImage(qlst.getProfileImage());
                try {
                    temp1.setSenddate(df.parse(qlst.getSentTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                temp.add(i,temp1);
            }

            Collections.sort(temp, new ChatNoCaseComparator());
            return temp;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(search != null)
            search.setVisible(false);
        if(done != null)
            done.setVisible(false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public class ChatNoCaseComparator implements Comparator<TeacherQuery1model> {
        public int compare(TeacherQuery1model s1, TeacherQuery1model s2) {
            return s2.getSenddate().compareTo(s1.getSenddate());
        }
    }
}
