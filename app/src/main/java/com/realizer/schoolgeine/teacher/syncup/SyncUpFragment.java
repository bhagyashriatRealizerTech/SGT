package com.realizer.schoolgeine.teacher.syncup;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.holiday.adapter.TeacherPublicHolidayListAdapter;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;
import com.realizer.schoolgeine.teacher.service.ManualSyncService;
import com.realizer.schoolgeine.teacher.syncup.adapter.SyncupListAdapter;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Bhagyashri on 10/21/2016.
 */
public class SyncUpFragment extends Fragment implements FragmentBackPressedListener {
    DatabaseQueries qr;
    ArrayList<QueueListModel> quelist;
    ListView listpublicholiday;
    ProgressWheel loading;
    TextView nextSync,lastSync;
    Button syncnow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.syncup, container, false);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        listpublicholiday = (ListView) rootView.findViewById(R.id.lstdata);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);
        syncnow = (Button)rootView.findViewById(R.id.btnsyncup);
        syncnow.setTypeface(face);
        lastSync = (TextView)rootView.findViewById(R.id.txtlastsyncup);
        nextSync = (TextView)rootView.findViewById(R.id.txtnextsyncup);



        qr = new DatabaseQueries(getActivity());

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lastSync.setText(sharedpreferences.getString("LastSyncUpTime",""));
        nextSync.setText(sharedpreferences.getString("NextSyncUpTime",""));


        new GetSyncUpData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        syncnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getActivity(),ManualSyncService.class);
                Singlton.setManualserviceIntent(service);
                getActivity().startService(service);

            }
        });

        return rootView;
    }

    public class GetSyncUpData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loading.setVisibility(View.GONE);
            if(quelist.size()>0) {
                ArrayList<QueueListModel> temp = new ArrayList<>();
                ArrayList<QueueListModel> temp1 = new ArrayList<>();
                QueueListModel obj = new QueueListModel();
                obj.setType("Feature");
                obj.setTime("Time Added");
                temp1.add(obj);
                temp.addAll(temp1);
                temp.addAll(quelist);
                listpublicholiday.setAdapter(new SyncupListAdapter(getActivity(), temp));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            quelist = qr.GetQueueData();
            return null;
        }
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
