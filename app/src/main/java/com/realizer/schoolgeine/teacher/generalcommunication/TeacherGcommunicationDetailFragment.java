package com.realizer.schoolgeine.teacher.generalcommunication;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherGcommunicationDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtCategory,txtDate,txtTeacherName,txtDescription;
    TextView txtstd ,txtclss;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.teacher_generalcommunication_detail_layout, container, false);
        initiateView(rootView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));

        Bundle bundle= getArguments();


        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Alert", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        if(bundle.getString("CategoryName").equals("CA")) {
            txtCategory.setText("Cultural Activity");
        }

        else if(bundle.getString("CategoryName").equals("SD")) {
            txtCategory.setText("Sports Day");
        }
        else if(bundle.getString("CategoryName").equals("FDC")) {
            txtCategory.setText("Fancy Dress Competitions");
        }
        else if(bundle.getString("CategoryName").equals("CM")) {
            txtCategory.setText("Class Meeting");
        }
        else
        {
            txtCategory.setText("Others");
        }


        txtDate.setText(bundle.getString("AlertDate").split(" ")[0]);
        txtTeacherName.setText(bundle.getString("TeacherName"));
        txtDescription.setText(bundle.getString("AlertText"));

        return rootView;
    }

    public void initiateView(View view)
    {
        txtCategory = (TextView)view.findViewById(R.id.txtcatname);
        txtDate = (TextView)view.findViewById(R.id.txtalertdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
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
}
