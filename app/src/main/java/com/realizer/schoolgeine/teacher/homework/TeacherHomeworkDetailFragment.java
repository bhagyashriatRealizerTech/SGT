package com.realizer.schoolgeine.teacher.homework;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.timetable.adapter.TimeTableDetailAdapter;
import com.realizer.schoolgeine.teacher.view.FullImageViewActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherHomeworkDetailFragment extends Fragment implements FragmentBackPressedListener{

    TextView txtSubject,txtDate,txtTeacherName,txtUploadStatus,txtDescription,txtdevider;
    TextView txtstd ,txtclss;
    String htext,path;
    LinearLayout imagelayout;

    GridView gridView;
    TimeTableDetailAdapter adapter;
    int imageCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_homework_detail_layout, container, false);
        initiateView(rootView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));

        Bundle bundle= getArguments();
        htext = bundle.getString("HEADERTEXT");
        path = bundle.getString("HomeworkImage");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        txtSubject.setText(bundle.getString("SubjectName"));
        txtDate.setText(bundle.getString("HomeworkDate"));
        txtTeacherName.setText( bundle.getString("TeacherName"));

        if(bundle.getString("Status").equalsIgnoreCase("true"))
        txtUploadStatus.setText("Uploaded");
        else
        txtUploadStatus.setText("Pending");

        txtDescription.setText(bundle.getString("HomeworkText"));

        imageCount = bundle.getInt("HWUUID");

        new GetImagesTimeTable().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return rootView;
    }

    public void initiateView(View view)
    {
        txtSubject = (TextView)view.findViewById(R.id.txtsubject);
        txtDate = (TextView)view.findViewById(R.id.txthomeworkdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtUploadStatus = (TextView)view.findViewById(R.id.txtstatus);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        txtstd  = (TextView) view.findViewById(R.id.txttclassname);
        txtclss = (TextView) view.findViewById(R.id.txttdivname);
        txtdevider = (TextView)view.findViewById(R.id.txtDivider);

        imagelayout = (LinearLayout)view.findViewById(R.id.imagelayout);
        gridView= (GridView) view.findViewById(R.id.gallerygridView);

    }

    public class GetImagesTimeTable extends AsyncTask<Void, Void,Void>
    {
        private ArrayList<TeacherHomeworkModel> elementDetails = new ArrayList<>();
        JSONArray temp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                temp = new JSONArray(path);

                for(int i=0;i<temp.length();i++)
                {
                    String path = temp.get(i).toString();
                    Bitmap bitmap =BitmapFactory.decodeFile(path);

                    TeacherHomeworkModel obj = new TeacherHomeworkModel();
                    obj.setPic(bitmap);
                    obj.setHid(imageCount);
                    elementDetails.add(obj);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(elementDetails.size()>0) {
                gridView.setVisibility(View.VISIBLE);
                adapter = new TimeTableDetailAdapter(getActivity(),elementDetails,path,"Homework");
                gridView.setAdapter(adapter);
                gridView.setFastScrollEnabled(true);
            }

        }
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
