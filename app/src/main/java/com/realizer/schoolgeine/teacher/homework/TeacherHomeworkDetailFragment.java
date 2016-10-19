package com.realizer.schoolgeine.teacher.homework;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.view.FullImageViewActivity;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Bhagyashri on 8/29/2016.
 */
public class TeacherHomeworkDetailFragment extends Fragment implements FragmentBackPressedListener{

    TextView txtSubject,txtDate,txtTeacherName,txtUploadStatus,txtDescription,txtdevider;
    TextView txtstd ,txtclss;
    String htext,path;
    ImageView image1,image2,image3;
    LinearLayout imagelayout;
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
        txtDate.setText(Config.getMediumDate(bundle.getString("HomeworkDate")));
        txtTeacherName.setText( bundle.getString("TeacherName"));
        if(bundle.getString("Status").equalsIgnoreCase("true"))
        txtUploadStatus.setText("Uploaded");
        else
        txtUploadStatus.setText("Pending");
        txtDescription.setText(bundle.getString("HomeworkText"));

        if(path.equalsIgnoreCase("NoImage")) {
          /*  frameimageClik.setVisibility(View.GONE);*/
            txtdevider.setVisibility(View.GONE);
            imagelayout.setVisibility(View.GONE);
        }
        else {
          /*  frameimageClik.setVisibility(View.VISIBLE);*/
            txtdevider.setVisibility(View.VISIBLE);
            imagelayout.setVisibility(View.VISIBLE);
            String[] IMG ;
            try {
                JSONArray jarr = new JSONArray(path);

                IMG = new String[jarr.length()];
                for(int i=0;i<jarr.length();i++)
                {
                    IMG[i] = jarr.getString(i);
                }

                if(jarr.length()==1) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.INVISIBLE);
                    image3.setVisibility(View.INVISIBLE);

                    String filePath = IMG[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);
                }
                else if(jarr.length()==2) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.INVISIBLE);

                    String filePath = IMG[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);

                     filePath = IMG[1];
                     decodedString = Base64.decode(filePath, Base64.DEFAULT);
                     decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                     image2.setImageBitmap(decodedByte);
                }
                else if(jarr.length()==3) {
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.VISIBLE);

                    String filePath = IMG[0];
                    byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image1.setImageBitmap(decodedByte);

                    filePath = IMG[1];
                    decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image2.setImageBitmap(decodedByte);

                    filePath = IMG[2];
                    decodedString = Base64.decode(filePath, Base64.DEFAULT);
                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    image3.setImageBitmap(decodedByte);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        /*txtImageClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", path);
                editor.commit();
                loadPhoto(path);
            }
        });*/

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  path+"@@@0";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(tempPath);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  path+"@@@1";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(tempPath);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath =  path+"@@@2";
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ImageString", tempPath);
                editor.commit();
                loadPhoto(tempPath);
            }
        });




        return rootView;
    }

    public void initiateView(View view)
    {
        txtSubject = (TextView)view.findViewById(R.id.txtsubject);
        txtDate = (TextView)view.findViewById(R.id.txthomeworkdate);
        txtTeacherName = (TextView)view.findViewById(R.id.txtteacherName);
        txtUploadStatus = (TextView)view.findViewById(R.id.txtstatus);
     /*   txtImageClick = (TextView)view.findViewById(R.id.txtclicktoviewimage);
        txtImageClick.setPaintFlags(txtImageClick.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);*/
        txtDescription = (TextView)view.findViewById(R.id.txtdescription);
        txtstd  = (TextView) view.findViewById(R.id.txttclassname);
        txtclss = (TextView) view.findViewById(R.id.txttdivname);
        /*frameimageClik = (FrameLayout) view.findViewById(R.id.frameimageClik);*/
        txtdevider = (TextView)view.findViewById(R.id.txtDivider);
        image1 = (ImageView)view.findViewById(R.id.btnCapturePicture1);
        image2 = (ImageView)view.findViewById(R.id.btnCapturePicture2);
        image3 = (ImageView)view.findViewById(R.id.btnCapturePicture3);
        imagelayout = (LinearLayout)view.findViewById(R.id.imagelayout);

    }


    private void loadPhoto(String path_lst) {
        Intent i = new Intent(getActivity(),FullImageViewActivity.class);
        i.putExtra("FLAG",1);
        startActivity(i);
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
