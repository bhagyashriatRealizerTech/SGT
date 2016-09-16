package com.realizer.schoolgeine.teacher.myclass;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.Utils.TeacherStudentList;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.myclass.adapter.TeacherMyClassAttDetailListAdapter;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassAttModel;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassStudentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Bhagyashri on 1/27/2016.
 */
public class TeacherMyClassAttDetails extends Fragment implements FragmentBackPressedListener {

    TextView txtstd,txtclss;
    DatabaseQueries qr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_myclass_detail_att_layout, container, false);

        ListView lstatt = (ListView) rootView.findViewById(R.id.lsttdetailatt);
        txtstd = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Attendance", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));



        Bundle b = getArguments();
        String pre = b.getString("PRS");
        String abc = b.getString("ABS");

        qr = new DatabaseQueries(getActivity());

        List<TeacherMyClassAttModel> listat = new ArrayList<>();
        List<TeacherMyClassStudentModel> studlst = TeacherStudentList.getStudentList(getActivity(), txtstd.getText().toString(), txtclss.getText().toString());

        String arrp[] = pre.split(",,");
        String arra[] = abc.split(",,");

        for(int i=0;i<arrp.length;i++)
        {

            for(int j=0;j<studlst.size();j++)
            {
                if(arrp[i].equalsIgnoreCase(studlst.get(j).getUserId()))
                {
                    TeacherMyClassAttModel o = new TeacherMyClassAttModel();
                    String name = studlst.get(j).getStdName();
                    o.setRollno(studlst.get(j).getSrnoStd());
                    o.setAttDate(name);
                    o.setAbscnt("P");
                    if(!studlst.get(j).getSrnoStd().equalsIgnoreCase("null"))
                    o.setRno(Integer.valueOf(studlst.get(j).getSrnoStd()));
                    o.setProfileimage(studlst.get(j).getProfileimage());
                    listat.add(o);
                    break;
                }
            }
        }

        for(int i=0;i<arra.length;i++)
        {

            for(int j=0;j<studlst.size();j++)
            {
                if(arra[i].equals(studlst.get(j).getUserId()))
                {
                    TeacherMyClassAttModel o = new TeacherMyClassAttModel();
                    String name = studlst.get(j).getStdName();
                    o.setRollno(studlst.get(j).getSrnoStd());
                    o.setAttDate(name);
                    o.setAbscnt("A");
                    if(!studlst.get(j).getSrnoStd().equalsIgnoreCase("null"))
                    o.setRno(Integer.valueOf(studlst.get(j).getSrnoStd()));
                    o.setProfileimage(studlst.get(j).getProfileimage());
                    listat.add(o);
                    break;
                }
            }
        }
       // listat = getHardcodeValue();

        Collections.sort(listat, new ChatNoCaseComparator());

        lstatt.setAdapter(new TeacherMyClassAttDetailListAdapter(getActivity(),listat));

        return rootView;
    }

    public class ChatNoCaseComparator implements Comparator<TeacherMyClassAttModel> {
        public int compare(TeacherMyClassAttModel s1, TeacherMyClassAttModel s2) {
            return s1.getAttDate().compareToIgnoreCase(s2.getAttDate());
           // return s1.getRno() - (s2.getRno());
        }
    }

    public List<TeacherMyClassAttModel> getHardcodeValue()
    {
        List<TeacherMyClassAttModel> templist = new ArrayList<>();

        TeacherMyClassAttModel obj1 = new TeacherMyClassAttModel();
        obj1.setAttDate("Rudra Shinde");
        obj1.setAbscnt("P");
        obj1.setPrsnt("1");
        templist.add(obj1);
        TeacherMyClassAttModel obj2 = new TeacherMyClassAttModel();
        obj2.setAttDate("Vivaan Salgare");
        obj2.setAbscnt("P");
        obj2.setPrsnt("2");
        templist.add(obj2);
        TeacherMyClassAttModel obj3 = new TeacherMyClassAttModel();
        obj3.setAttDate("Arrav Mahargude");
        obj3.setAbscnt("P");
        obj3.setPrsnt("3");
        templist.add(obj3);
        TeacherMyClassAttModel obj4 = new TeacherMyClassAttModel();
        obj4.setAttDate("Farhan Bodale");
        obj4.setAbscnt("A");
        obj4.setPrsnt("4");
        templist.add(obj4);
        TeacherMyClassAttModel obj5 = new TeacherMyClassAttModel();
        obj5.setAttDate("Pravin Jadhav");
        obj5.setAbscnt("P");
        obj5.setPrsnt("5");
        templist.add(obj5);
        TeacherMyClassAttModel obj6 = new TeacherMyClassAttModel();
        obj6.setAttDate("Ramchandra Magar");
        obj6.setAbscnt("A");
        obj6.setPrsnt("6");
        templist.add(obj6);
        TeacherMyClassAttModel obj7 = new TeacherMyClassAttModel();
        obj7.setAttDate("Kishor Gunjal");
        obj7.setAbscnt("P");
        obj7.setPrsnt("7");
        templist.add(obj7);
        return templist;
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
