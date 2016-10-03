package com.realizer.schoolgeine.teacher.chat;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.chat.asynctask.TeacherQueryAsyncTaskPost;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.adapter.TeacherQueryMessageCenterListAdapter;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQuerySendModel;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQueryViewListModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryViewFragment extends Fragment implements AbsListView.OnScrollListener,OnTaskCompleted, FragmentBackPressedListener {

    DatabaseQueries qr;
    Timer timer;
    Parcelable state;
    int currentPosition;
    ListView lsttname;
    int qid;
    int mCurrentX ;
    int  mCurrentY;
    TextView send;
    EditText msg;
    int lstsize;
    TeacherQueryMessageCenterListAdapter adapter;
    Context context;
    MessageResultReceiver resultReceiver;
    String sname;
    ProgressWheel loading;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_queryview_layout, container, false);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        qr =new DatabaseQueries(getActivity());
        qid=0;

        lsttname = (ListView) rootView.findViewById(R.id.lstviewquery);
        msg = (EditText) rootView.findViewById(R.id.edtmsgtxt);
        send = (TextView) rootView.findViewById(R.id.btnSendText);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);

        Bundle b = getArguments();
        sname = b.getString("SENDERNAME");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(sname, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        Singlton obj = Singlton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

        ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
        lstsize = teachernames.size();
        //adapter = new TeacherQueryViewListAdapter(getActivity(),teachernames);
        adapter = new TeacherQueryMessageCenterListAdapter(getActivity(),teachernames);
        lsttname.setAdapter(adapter);
        lsttname.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lsttname.setFastScrollEnabled(true);
        lsttname.setScrollY(lsttname.getCount());
        lsttname.setSelection(lsttname.getCount() - 1);
        lsttname.smoothScrollToPosition(lsttname.getCount());
        lsttname.setOnScrollListener(this);
        Config.hideSoftKeyboardWithoutReq(getActivity(), msg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.hideSoftKeyboardWithoutReq(getActivity(),msg);
                if(msg.getText().length()!=0)
               {
                   loading.setVisibility(View.VISIBLE);
                   Singlton.setMessageCenter(loading);
                    Bundle b = getArguments();
                    String uidstud = b.getString("USERID");
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
                    String date = df.format(calendar.getTime());
                   Date sendDate =  new Date();
                   try {
                       sendDate = df.parse(date);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String uidname = sharedpreferences.getString("UidName", "");
                    String thumbnailUrl = sharedpreferences.getString("ThumbnailID", "");
                    String senderName = sharedpreferences.getString("DisplayName", "");
                    long n = qr.insertQuery("true", uidname, uidstud, msg.getText().toString(), date, "true",sendDate,thumbnailUrl,senderName);
                    if (n > 0) {
                       // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                        n = -1;

                        qid = qr.getQueryId();
                        n = qr.insertQueue(qid, "Query", "2", date);
                        if (n > 0) {
                           // Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                            n = -1;

                            msg.setText("");

                            if(isConnectingToInternet()) {
                                TeacherQuerySendModel obj = qr.GetQuery(qid);
                                TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj, getActivity(), TeacherQueryViewFragment.this, "true");
                                asyncobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            else
                            {
                                resultReceiver.send(200,null);
                            }
                        }


                    }
                    Log.d("DIFICULTARR", uidstud);
                }
            }
        });

        return rootView;
    }


    private ArrayList<TeacherQueryViewListModel> GetQuery()
    {

        Bundle b = this.getArguments();
        String uid = b.getString("USERID");
        ArrayList<TeacherQueryViewListModel> results = new ArrayList<>();
        ArrayList<TeacherQuerySendModel> qlst = qr.GetQueuryData(uid);
        String tp="AM";

        for(int i=0;i<qlst.size();i++)
        {
            TeacherQuerySendModel obj = qlst.get(i);
            TeacherQueryViewListModel tDetails = new TeacherQueryViewListModel();
            String datet[] = obj.getSentTime().split(" ");
            if(datet.length>2)
                tp = datet[2];
            tDetails.setSenddate(datet[0]);
            String time[] = datet[1].split(":");
            String outtime = "";
            if(time.length>2)
             outtime = "" + time[0] + ":" + time[1] + " " + tp;
            else if(time.length>1)
             outtime = "" + time[0] + ":" + time[1];
            else if(time.length>0)
                outtime = "" + time[0] ;
            tDetails.setTime(outtime);


            if(uid.equals(obj.getFrom()))
            tDetails.setFlag("P");
            else
                tDetails.setFlag("T");
            tDetails.setMsg(obj.getText());
            tDetails.setSendername(obj.getMsgSenderName());
            tDetails.setProfileImage(obj.getProfileImage());
            results.add(tDetails);
           // Log.d("MSGTXT",obj.getText()+ "  "+obj.getSentTime());

        }

        return results;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {



    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mCurrentX = view.getScrollX();
        mCurrentY = view.getScrollY();
        currentPosition = lsttname.getSelectedItemPosition();
        Log.d("Position", "" + currentPosition);

    }


    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
        if(s.equals("true") && queueListModel.getType().equalsIgnoreCase("Query"))
        {
            long n = qr.deleteQueueRow(queueListModel.getId(),"Query");

            if(n>0)
            {
                //Toast.makeText(getActivity(), "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(qid);
                n=-1;

                n = qr.updateQurySyncFlag(o);

                if(n>0)
                {
                    resultReceiver.send(200,null);
                    //Toast.makeText(getActivity(), "Message Sent Successfully", Toast.LENGTH_SHORT).show();

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

    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RecieveMessage")) {
                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size()!=lstsize)
                {

                    Bundle b = getArguments();
                    String stdC= b.getString("Stand");
                    String divC= b.getString("Divi");
                    String uid= b.getString("USERID");
                    String sname = b.getString("SENDERNAME");
                    String thumbnailurl =  b.getString("UrlImage");

                    qr.updateInitiatechat(stdC, divC, sname, "true", uid,0,thumbnailurl);

                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount() - 1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();
                }
            }

            else if(update.equals("SendMessageMessage")) {
                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size() !=lstsize)
                {
                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount() - 1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();
                    loading.setVisibility(View.GONE);
                    Singlton.setMessageCenter(null);
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
