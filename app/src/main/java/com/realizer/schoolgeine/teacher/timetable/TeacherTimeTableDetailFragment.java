package com.realizer.schoolgeine.teacher.timetable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.homework.newhomework.adapter.NewHomeworkGalleryAdapter;
import com.realizer.schoolgeine.teacher.timetable.adapter.TimeTableDetailAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherTimeTableDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtTitle,txtDate,txtTeacherName,txtDescription;
    TextView txtstd ,txtclss;
    String title,image;
    ImageView image1;
    GridView gridView;
    TimeTableDetailAdapter adapter;
    int imageCount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_timetable_detail_layout, container, false);
        initiateView(rootView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));

        Bundle bundle= getArguments();


        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        txtTitle.setText(bundle.getString("Title"));
        txtDate.setText(bundle.getString("TimeTableDate"));
        txtTeacherName.setText(bundle.getString("TeacherName"));
        txtDescription.setText(bundle.getString("TimeTableText"));

        title = bundle.getString("Title");
        image = bundle.getString("TimeTableImage");

        imageCount = bundle.getInt("ImageCount");

        new GetImagesTimeTable().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return rootView;
    }

    public void initiateView(View view)
    {
        txtTitle = (TextView)view.findViewById(R.id.txttitle);
        txtDate = (TextView)view.findViewById(R.id.txttimetabledate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        image1 = (ImageView) view.findViewById(R.id.btnCapturePicture1);
        gridView= (GridView) view.findViewById(R.id.gallerygridView);

        txtstd  = (TextView) view.findViewById(R.id.txttclassname);
        txtclss = (TextView) view.findViewById(R.id.txttdivname);

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
                temp = new JSONArray(image);

            for(int i=0;i<temp.length();i++)
            {
                String path = temp.get(i).toString();
                Bitmap bitmap = null;
                if(!TextUtils.isEmpty(path) && path != null)
                    bitmap =BitmapFactory.decodeFile(path);

                TeacherHomeworkModel obj = new TeacherHomeworkModel();

                if(bitmap == null)
                {
                    bitmap = ((BitmapDrawable)getActivity().getResources().getDrawable(R.drawable.sorryimage)).getBitmap();
                }

                if(bitmap!= null)
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
                adapter = new TimeTableDetailAdapter(getActivity(),elementDetails,image,"TimeTable");
                gridView.setAdapter(adapter);
                gridView.setFastScrollEnabled(true);
            }

        }
    }
}
