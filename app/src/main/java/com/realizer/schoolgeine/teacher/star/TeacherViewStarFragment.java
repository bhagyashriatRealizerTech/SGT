package com.realizer.schoolgeine.teacher.star;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.TeacherMyClassDialogBoxActivity;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;
import com.realizer.schoolgeine.teacher.star.adapter.TeacherViewStarListAdapter;
import com.realizer.schoolgeine.teacher.star.model.TeacherGiveStarModel;
import com.realizer.schoolgeine.teacher.star.model.TeacherViewStarModel;

import java.util.ArrayList;

public class TeacherViewStarFragment extends Fragment implements FragmentBackPressedListener {
    ImageView blinkingImageView, StarGiven1, StarGiven2, StarGiven3;
    String label;
    TextView txtusername, txtteacher, txtcomment, txtdate;
    DatabaseQueries qr;
    TextView txtclss, txtstd,noDataText;
    String getValueBack;
    Spinner date, sub;
    ArrayList<TeacherViewStarModel> viewStarList;
    ArrayList<TeacherQuery1model> studlst;
    ListView lststar;
    FloatingActionButton newStar;
    SharedPreferences sharedpreferences;
    MenuItem search,done;
    String selectedDate;

    public TeacherViewStarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootView = inflater.inflate(R.layout.parent_viewstar_layout, container, false);
        setHasOptionsMenu(true);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("View Star", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        date = (Spinner) rootView.findViewById(R.id.spinnerdate);
        sub = (Spinner) rootView.findViewById(R.id.spinnersub);
        lststar = (ListView) rootView.findViewById(R.id.lstviewStar);
        newStar = (FloatingActionButton) rootView.findViewById(R.id.fab_rewardstar);

        txtstd = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);
        noDataText = (TextView)rootView.findViewById(R.id.tvNoDataMsg);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));
        qr = new DatabaseQueries(getActivity());

        studlst = qr.GetstudList(txtstd.getText().toString(), txtclss.getText().toString());

        ArrayList<String> listofSubject = qr.GetSub(txtstd.getText().toString(), txtclss.getText().toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listofSubject);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);

        for (int i = 0; i < adapter.toString().length(); i++) {
            sub.setAdapter(adapter);
            break;
        }
        sub.setSelection(0);

        final ArrayList<String> listofDate = qr.GetAllStarDate(txtstd.getText().toString(), txtclss.getText().toString(), sub.getSelectedItem().toString());

        ArrayList<String> listofDay = getStarDate(listofDate);
        if(listofDay.size()==0)
        {
            listofDay.add("No Dates");
            listofDate.add("No Dates");
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listofDay);
        adapter1.setDropDownViewResource(R.layout.viewstar_subject_spiner);

        for (int i = 0; i < adapter1.toString().length(); i++) {
            date.setAdapter(adapter1);
            break;
        }
        date.setSelection(0);
        selectedDate = listofDate.get(0).toString();

        sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getList(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = listofDate.get(position).toString();
                getList(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherGiveStarFragment fragment = new TeacherGiveStarFragment();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

        return rootView;
    }

    public ArrayList<String> getStarDate(ArrayList<String> dateList)
    {
        ArrayList<String> result =  new ArrayList<>();

        for(int i=0;i<dateList.size();i++)
        {
            result.add(i,Config.getDate(dateList.get(i).toString(),"D"));
        }

        return result;
    }


    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",12);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
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
    public boolean onOptionsItemSelected(MenuItem item)
    {

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



    public void getList(String date) {
        if (date.length() == 0) {
            noDataText.setVisibility(View.VISIBLE);
            lststar.setVisibility(View.INVISIBLE);
        } else {
            ArrayList<TeacherGiveStarModel> strlst = qr.GetAllStar(date, txtstd.getText().toString(), txtclss.getText().toString(), sub.getSelectedItem().toString());

            ArrayList<TeacherViewStarModel> flst = new ArrayList<>();
            for (int i = 0; i < strlst.size(); i++) {
                for (int j = 0; j < studlst.size(); j++) {
                    String arr[] = strlst.get(i).getStuduid().split(",");
                    for (int k = 0; k < arr.length; k++) {
                        if (arr[k].equals(studlst.get(j).getUid())) {
                            TeacherViewStarModel obj = new TeacherViewStarModel();
                            obj.setcomment(strlst.get(i).getComment());
                            obj.setgivenStar(strlst.get(i).getStar());
                            obj.setteachername(studlst.get(j).getUname());
                            obj.setProfilrImage(studlst.get(j).getProfilrImage());
                            flst.add(obj);

                        }
                    }
                }
            }

            if(flst.size()>0) {
                noDataText.setVisibility(View.GONE);
                lststar.setVisibility(View.VISIBLE);
                lststar.setAdapter(new TeacherViewStarListAdapter(getActivity(), flst));
            }
            else
            {
                noDataText.setVisibility(View.VISIBLE);
                lststar.setVisibility(View.INVISIBLE);
            }
        }
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



