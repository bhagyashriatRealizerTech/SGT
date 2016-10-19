package com.realizer.schoolgeine.teacher.funcenter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.funcenter.adapter.TeacherFunCenterFolderAdapter;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Win on 18/04/2016.
 */
public class TeacherFunCenterFolderFragment extends Fragment implements FragmentBackPressedListener
{
    FloatingActionButton newevent;
    GridView folderdgridview;
    TeacherFunCenterFolderAdapter adapter2;
    DatabaseQueries qr;
    MenuItem search,done,switchclass;
    String data1;
    ProgressWheel loading;
    ArrayList<TeacherFunCenterModel> allData1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_funcenter_folder_fragment, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Fun Center", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        newevent= (FloatingActionButton) rootView.findViewById(R.id.txtnewthumbnail);
        folderdgridview= (GridView) rootView.findViewById(R.id.foldergridView);
        loading = (ProgressWheel) rootView.findViewById(R.id.loading);

        qr=new DatabaseQueries(getActivity());

        new GetEvents().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        folderdgridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                 Bundle b=new Bundle();
                TeacherFunCenterGalleryFragment fragment = new  TeacherFunCenterGalleryFragment();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                int putid=allData1.get(position).getEventid();
                String evntname=allData1.get(position).getText();
                b.putInt("EventId",putid);
                b.putString("EventUUID", allData1.get(position).getEventuuid());
                b.putString("EventName",evntname);
                fragment.setArguments(b);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

        newevent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle b=new Bundle();
                TeacherFunCenterNewFragment1 fragment = new TeacherFunCenterNewFragment1();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragment.setArguments(b);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        switchclass = menu.findItem(R.id.action_switchclass);
        switchclass.setVisible(false);
        done = menu.findItem(R.id.action_done);
        done.setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(done != null && search != null && switchclass != null)
        {
            done.setVisible(false);
            search.setVisible(false);
            switchclass.setVisible(false);
        }

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    public class GetEvents extends AsyncTask<Void, Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            allData1 = new ArrayList<>();

        }


        @Override
        protected Void doInBackground(Void... params) {

            //setting list to adapter
            allData1=qr.GetEvent();

            for(int i=0;i<allData1.size();i++)
            {

                String image1 =allData1.get(i).getImage();
                File file = ImageStorage.getEventImage(image1);

                if(file != null) {
                   /* BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image1, bmOptions);*/
                    Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(image1,150,150);
                    TeacherFunCenterModel obj = new TeacherFunCenterModel();
                    obj = allData1.get(i);
                    obj.setBitmap(bitmap);
                    allData1.set(i,obj);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter2=new TeacherFunCenterFolderAdapter(getActivity(),allData1);
            folderdgridview.setAdapter(adapter2);
            loading.setVisibility(View.GONE);
        }


    }
}
