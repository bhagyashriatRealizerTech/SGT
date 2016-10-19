package com.realizer.schoolgeine.teacher.timetable;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherTimeTableDetailFragment extends Fragment implements FragmentBackPressedListener {

    TextView txtTitle,txtDate,txtTeacherName,txtDescription;
    TextView txtstd ,txtclss;
    String title,image;
    ImageView image1;
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
        txtDate.setText(bundle.getString("TimeTableDate").split(" ")[0]);
        txtTeacherName.setText(bundle.getString("TeacherName"));
        txtDescription.setText(bundle.getString("TimeTableText"));

        title = bundle.getString("Title");
        image = bundle.getString("TimeTableImage");

        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //barr[position] = decodedByte;
        image1.setImageBitmap(decodedByte);

        /*txtClickToViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeTableImageViewFragment fragment = new TimeTableImageViewFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                bundle.putString("title", title);
                bundle.putString("image", image);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });*/
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeTableImageViewFragment fragment = new TimeTableImageViewFragment();
                Singlton.setSelectedFragment(fragment);
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                bundle.putString("title", title);
                bundle.putString("image", image);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });


        return rootView;
    }

    public void initiateView(View view)
    {
        txtTitle = (TextView)view.findViewById(R.id.txttitle);
        txtDate = (TextView)view.findViewById(R.id.txttimetabledate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        image1 = (ImageView) view.findViewById(R.id.btnCapturePicture1);

       /* txtClickToViewImage = (TextView)view.findViewById(R.id.txtclicktoviewimage);
        txtClickToViewImage.setPaintFlags(txtClickToViewImage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
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
