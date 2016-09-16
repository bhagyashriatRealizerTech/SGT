package com.realizer.schoolgeine.teacher.timetable;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.realizer.schoolgeine.teacher.view.ZoomImage;

/**
 * Created by shree on 4/9/2016.
 */
public class TimeTableImageViewFragment extends Fragment implements FragmentBackPressedListener {
    static Bitmap decodedByte;
    static ActionBar bar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timetable_image_view, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Time Table", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().hide();

        Bundle b = this.getArguments();
        String title = b.getString("title");
        String filePath = b.getString("image");
        TextView titleTextView = (TextView)rootView.findViewById(R.id.title);
        titleTextView.setText(title);

        ZoomImage imageView = (ZoomImage)rootView.findViewById(R.id.image);
        byte[] decodedString = Base64.decode(filePath, Base64.DEFAULT);
        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        //barr[position] = decodedByte;
        imageView.setImageBitmap(decodedByte);



       /* ByteArrayInputStream imageStream = new ByteArrayInputStream(fullImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(theImage);*/

        return rootView;
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

