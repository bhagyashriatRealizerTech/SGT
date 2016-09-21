package com.realizer.schoolgeine.teacher.funcenter.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterModel;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by Win on 30/03/2016.
 */
public class TeacherFunCenterFolderAdapter extends BaseAdapter
{
    ImageView image;
    Bitmap bitmap1;
    TextView text,datetext;
    String data;
    ArrayList<TeacherFunCenterModel> getImage1;
    DatabaseQueries qr;
    private LayoutInflater mInflater;
    Context context;
    String eventname;
    static Bitmap decodedByte;
    SimpleDateFormat dfinput;
    SimpleDateFormat dfoutput;
    int IMAGE_MAX_SIZE = 140;

    public TeacherFunCenterFolderAdapter(Context context, ArrayList<TeacherFunCenterModel> setImage1)
    {
        getImage1=setImage1;
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount()
    {
        return getImage1.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        convertView = mInflater.inflate(R.layout.teacher_funcenter_folder, null);

        image = (ImageView) convertView.findViewById(R.id.btnCapturefolder);
        text= (TextView) convertView.findViewById(R.id.event_title);
        datetext = (TextView) convertView.findViewById(R.id.event_date);

        qr=new DatabaseQueries(context);
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(context);
        String cls = preferences1.getString("Images2", "");

        String image1=getImage1.get(position).getImage();
        String eventnm=getImage1.get(position).getText();
        int evntid=getImage1.get(position).getEventid();

        int id = context.getResources().getIdentifier(image1, "drawable", context.getPackageName());
        image.setImageResource(id);

        byte[] decodedString = Base64.decode(image1, Base64.DEFAULT);
        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(decodedByte);
        decodedByte = null;
        System.gc();
        text.setText(eventnm);
        datetext.setText(Config.getMediumDateForImage(getImage1.get(position).getDate()));

        return convertView;
    }

}