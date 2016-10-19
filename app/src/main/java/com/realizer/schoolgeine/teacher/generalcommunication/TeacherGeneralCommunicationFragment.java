package com.realizer.schoolgeine.teacher.generalcommunication;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.generalcommunication.adapter.TeacherGeneralCommunicationListAdapter;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGeneralCommunicationListModel;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;

import java.util.ArrayList;

/**
 * Created by Win on 11/25/2015.
 */
public class TeacherGeneralCommunicationFragment extends Fragment implements FragmentBackPressedListener {

    DatabaseQueries qr;
    TextView txtstd,txtclss,noDataText;;
    FloatingActionButton txtnew;
    MenuItem search,done;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.teacher_generalcommunication_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Alerts", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        setHasOptionsMenu(true);
        qr = new DatabaseQueries(getActivity());
        txtnew = (FloatingActionButton) rootView.findViewById(R.id.txtnewcommunication);
        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        noDataText = (TextView)rootView.findViewById(R.id.tvNoDataMsg);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
        ArrayList<TeacherGeneralCommunicationListModel> msg = qr.GetAnnouncement(txtstd.getText().toString(),txtclss.getText().toString());
        final ListView listHoliday = (ListView) rootView.findViewById(R.id.lsttgeneralcommunication);
        if(msg.size()>0) {
            listHoliday.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
            listHoliday.setAdapter(new TeacherGeneralCommunicationListAdapter(getActivity(), msg));
        }
        else
        {
            listHoliday.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        }
        listHoliday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listHoliday.getItemAtPosition(position);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                TeacherGeneralCommunicationListModel gcommunication = (TeacherGeneralCommunicationListModel) o;
                TeacherGcommunicationDetailFragment fragment = new TeacherGcommunicationDetailFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                bundle.putString("CategoryName",gcommunication.getCategory());
                bundle.putString("AlertDate",Config.getMediumDate(gcommunication.getAnnouncementTime()));
                bundle.putString("TeacherName",preferences.getString("DisplayName",""));
                bundle.putString("AlertText",gcommunication.getAnnouncementText());
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });



        txtnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TeacherGCommunicationNewFragment fragment = new TeacherGCommunicationNewFragment();
                Singlton.setSelectedFragment(fragment);
                //String categorylist = "SD,,Sports Day_CA,,Cultural Activity_O,,Other_FDC,,Fancy Dress Competition";
                String categorylist = "CA,,Cultural Activity_CM,,Class Meeting_FDC,,Fancy Dress Competition_SD,,Sports Day_O,,Other";
                Bundle bundle = new Bundle();
                bundle.putString("CategorNameList", categorylist);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
    private ArrayList<TeacherGeneralCommunicationListModel> GetGeneralCommunicationList()
    {

        Bundle b = this.getArguments();
        ArrayList<TeacherGeneralCommunicationListModel> results = new ArrayList<>();
        return results;
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
        b.putInt("MYCLASS",8);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
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
    public void onFragmentBackPressed() {

        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }
}
