package com.realizer.schoolgeine.teacher.timetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkListModel;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.timetable.adapter.TeacherTimeTableExamListAdapter;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win on 11/25/2015.
 */
public class TeacherTimeTableFragment extends Fragment implements FragmentBackPressedListener {

    String enamelist="";
    DatabaseQueries qr;
    View root;
    private ListView listsyllabus;
    private FloatingActionButton btnnew;
    int j;
    MenuItem done,search;
    MessageResultReceiver resultReceiver;
    TextView noData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_timetable_layout, container, false);


        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        root = rootView;
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        listsyllabus = (ListView) root.findViewById(R.id.lstexamsyllabus);
        btnnew =  (FloatingActionButton) root.findViewById(R.id.btnnewfull);
        noData = (TextView) root.findViewById(R.id.tvNoDataMsg);
        qr = new DatabaseQueries(getActivity());
        TextView txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        TextView txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
        Bundle b = getArguments();
        int i = b.getInt("Checked", 0);

        j=i;
        Log.d("Checked", "" + i);
       /* ArrayList<TeacherTimeTableExamListModel> syllabus = GetSyllabusList(preferences.getString("STANDARD", ""),preferences.getString("DIVISION", ""));
        listsyllabus.setVisibility(View.VISIBLE);
        if(syllabus.size()>0) {
            noData.setVisibility(View.GONE);
            listsyllabus.setVisibility(View.VISIBLE);
            listsyllabus.setAdapter(new TeacherTimeTableExamListAdapter(getActivity(), syllabus));
        }
        else
        {
            noData.setVisibility(View.VISIBLE);
            listsyllabus.setVisibility(View.INVISIBLE);
        }*/
        new GetTimeTableAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Singlton obj = Singlton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);


        listsyllabus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = listsyllabus.getItemAtPosition(position);
                TeacherTimeTableExamListModel homeworkObj = (TeacherTimeTableExamListModel) o;

                TeacherTimeTableDetailFragment fragment = new TeacherTimeTableDetailFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("Title",homeworkObj.getTitle());
                bundle.putString("TimeTableDate",Config.getMediumDate(homeworkObj.getDate()));
                bundle.putString("TeacherName",homeworkObj.getTeacher());
                bundle.putString("TimeTableImage",homeworkObj.getImage());
                bundle.putString("TimeTableText",homeworkObj.getDescription());
                bundle.putInt("ImageCount", homeworkObj.getImageCount());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherTimeTableNewFragment fragment = new TeacherTimeTableNewFragment();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });

        return rootView;

    }
    private ArrayList<TeacherTimeTableExamListModel> GetSyllabusList(String std,String div)
    {

        ArrayList<TeacherTimeTableExamListModel> ttlst=qr.GetTimeTableData(std,div);
        List<TeacherHomeworkModel> imageList = new ArrayList<>();

        for(int i=0;i<ttlst.size();i++)
        {
            TeacherHomeworkModel obj = new TeacherHomeworkModel();
            obj.setHwImage64Lst(ttlst.get(i).getImage());
            obj.setSubject(ttlst.get(i).getTitle());
            imageList.add(i,obj);
        }

        Singlton.setHomeworkthumbnailList(imageList);

        ArrayList<TeacherTimeTableExamListModel> results = new ArrayList<>();
        ArrayList<TeacherTimeTableExamListModel> result1 = new ArrayList<>();

        for(int i=0;i<ttlst.size();i++)
        {
            TeacherTimeTableExamListModel hDetail = new TeacherTimeTableExamListModel();
            TeacherTimeTableExamListModel obj = ttlst.get(i);
            hDetail.setTitle(obj.getTitle());
            hDetail.setTeacher(obj.getTeacher());
            hDetail.setDate(obj.getDate());
            hDetail.setDescription(obj.getDescription());
            hDetail.setHasUploaded(obj.getHasUploaded());

            if(obj.getImage().length()==0)
                hDetail.setImage("NoImage");
            else
                hDetail.setImage(obj.getImage());
            results.add(hDetail);
        }
        if(results.size()>1) {
            String sub = results.get(0).getDescription();
            JSONArray arr = new JSONArray();
            try {
                arr.put(0, results.get(0).getImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String images = arr.toString();
            JSONArray arr1 = new JSONArray();
            int forcounter = 0;
            TeacherTimeTableExamListModel hD = results.get(0);
            hD.setImage(arr.toString());
            hD.setImageCount(0);
            result1.add(forcounter, hD);
            try {

                    for (int j = 1; j < results.size(); j++) {
                        TeacherTimeTableExamListModel hDetail = results.get(j);
                        if (sub.equalsIgnoreCase(results.get(j).getDescription())) {
                            if (results.get(j).getImage().equalsIgnoreCase("NoImage")) {

                            } else {
                                arr1 = new JSONArray(images);
                                arr1.put(arr1.length(), results.get(j).getImage());
                                hDetail.setImage(arr1.toString());
                            }

                            result1.remove(forcounter);
                        } else {
                            forcounter = forcounter + 1;

                            if (results.get(j).getImage().equalsIgnoreCase("NoImage")) {

                            } else {
                                arr1 = new JSONArray();
                                arr1.put(0, results.get(j).getImage());
                                hDetail.setImage(arr1.toString());
                            }
                        }
                        if (forcounter != 0) {
                            if (result1.get(forcounter - 1).getImage().equalsIgnoreCase("NoImage")) {
                                hD.setImageCount(0);
                            } else {
                                JSONArray temp = new JSONArray(result1.get(forcounter - 1).getImage());
                                hDetail.setImageCount(result1.get(forcounter - 1).getImageCount() + temp.length());
                            }
                        } else {
                            hD.setImageCount(0);
                        }

                        result1.add(forcounter, hDetail);
                        sub = results.get(j).getDescription();
                        images = arr1.toString();
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            else if(results.size() == 1) {

            JSONArray arr = new JSONArray();
            try {
                arr.put(0, results.get(0).getImage());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int forcounter = 0;
            TeacherTimeTableExamListModel hD = results.get(0);
            hD.setImage(arr.toString());
            hD.setImageCount(0);
            result1.add(forcounter, hD);
        }
        else
        result1 = results;

            return result1;

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
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b = new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",4);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
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

                new GetTimeTableAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }
    }

    public class GetTimeTableAsyncTask extends AsyncTask<Void,Void,Void>
    {
        ArrayList<TeacherTimeTableExamListModel> syllabus;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            syllabus = GetSyllabusList(preferences.getString("STANDARD", ""), preferences.getString("DIVISION", ""));



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(syllabus.size()>0) {
                noData.setVisibility(View.GONE);
                listsyllabus.setVisibility(View.VISIBLE);
                listsyllabus.setAdapter(new TeacherTimeTableExamListAdapter(getActivity(), syllabus));
            }
            else
            {
                noData.setVisibility(View.VISIBLE);
                listsyllabus.setVisibility(View.INVISIBLE);
            }
        }


    }

}
