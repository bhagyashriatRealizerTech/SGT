package com.realizer.schoolgeine.teacher.funcenter;

import android.app.Fragment;
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
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.PhotoAlbumActivity;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.funcenter.adapter.TeacherFunCenterGalleryAdapter;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterGalleryModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Win on 07/04/2016.
 */
public class TeacherFunCenterGalleryFragment extends Fragment implements FragmentBackPressedListener
{
    GridView gridView;
    DatabaseQueries qr;
    TeacherFunCenterGalleryAdapter adapter1;
    FloatingActionButton btnadd;
    String aa;
    String eventuuid;
    int getid;
    MenuItem search,done,switchclass;
    String getevntName;
    TextView noDataText;
    ProgressWheel loading;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.showimage_gallery_fragment, container, false);
        setHasOptionsMenu(true);
        gridView= (GridView) rootView.findViewById(R.id.gallerygridView);
        btnadd= (FloatingActionButton) rootView.findViewById(R.id.btnaddimage);
        noDataText = (TextView) rootView.findViewById(R.id.tvNoDataMsg);
        loading = (ProgressWheel) rootView.findViewById(R.id.loading);
        noDataText.setVisibility(View.GONE);

        qr=new DatabaseQueries(getActivity());
        Bundle bundle1=getArguments();

         getid=bundle1.getInt("EventId");
         getevntName=bundle1.getString("EventName");
         eventuuid=bundle1.getString("EventUUID");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(getevntName, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();


        new GetImagesForEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // allData.clear();


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhotoAlbumActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("FunCenter", true);
                b.putInt("PassEventId", getid);
                b.putString("EventUUID", eventuuid);
                b.putString("EventName", getevntName);
                intent.putExtras(b);
                startActivity(intent);


            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object o = gridView.getItemAtPosition(position);

                TeacherFunCenterGalleryModel homeworkObj = (TeacherFunCenterGalleryModel)o;
                String path = homeworkObj.getImage();
                loadPhoto(position);

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
        if(Singlton.isDonclick())
        {
            Singlton.setIsDonclick(Boolean.FALSE);

            new GetImagesForEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
           /* ArrayList<TeacherFunCenterGalleryModel> allData=qr.GetImage(getid);
            if(allData.size()>1) {
                noDataText.setVisibility(View.GONE);
                adapter1 = new TeacherFunCenterGalleryAdapter(getActivity(), allData);
                gridView.setAdapter(adapter1);
            }
            else
            {
                noDataText.setVisibility(View.VISIBLE);
            }*/
        }

            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void loadPhoto(int position)
    {
        Intent i = new Intent(getActivity(),TeacherFunCenterImageLargeViewFragment.class);
        i.putExtra("FLAG",1);
        i.putExtra("PositionSelect",position);
        startActivity(i);
    }

    @Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }

    public class GetImagesForEvent extends AsyncTask<Void, Void,Void>
    {


        ArrayList<TeacherFunCenterGalleryModel> allData = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            allData=qr.GetImage(getid);

            for(int i=0;i<allData.size();i++)
            {

                String image1 =allData.get(i).getImage();
                File file = ImageStorage.getEventImage(image1);

                if(file != null) {
                   // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                  //  Bitmap bitmap = BitmapFactory.decodeFile(image1, bmOptions);
                    Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(image1,150,150);
                    TeacherFunCenterGalleryModel obj = new TeacherFunCenterGalleryModel();
                    obj = allData.get(i);
                    obj.setBitmap(bitmap);
                    allData.set(i,obj);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(allData.size()>0) {
                noDataText.setVisibility(View.GONE);
                adapter1 = new TeacherFunCenterGalleryAdapter(getActivity(), allData);
                gridView.setAdapter(adapter1);
                gridView.setFastScrollEnabled(true);
            }
            else
            {
                noDataText.setVisibility(View.VISIBLE);
            }

            loading.setVisibility(View.GONE);
        }


    }
}
