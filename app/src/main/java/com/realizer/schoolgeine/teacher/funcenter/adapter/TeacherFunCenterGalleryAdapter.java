package com.realizer.schoolgeine.teacher.funcenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterGalleryModel;

import java.util.ArrayList;

/**
 * Created by Win on 08/04/2016.
 */
public class TeacherFunCenterGalleryAdapter extends BaseAdapter
{
    private ArrayList<TeacherFunCenterGalleryModel> elementDetails;
    private LayoutInflater mInflater;
    Context context;
    Bitmap decodedByte;
    String data;
    TextView datetext;
    ImageView status;
    public TeacherFunCenterGalleryAdapter(Context context, ArrayList<TeacherFunCenterGalleryModel> results)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount() {
        return elementDetails.size();
    }

    public Object getItem(int position)
    {
        return elementDetails.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.teacher_funcenter_gallery_adapter, null);
        ImageView imageview = (ImageView) convertView.findViewById(R.id.gallery_item_image);
        datetext = (TextView) convertView.findViewById(R.id.event_date);
        status = (ImageView) convertView.findViewById(R.id.uploaded);

        imageview.setImageBitmap(elementDetails.get(position).getBitmap());
        datetext.setText(Config.getMediumDateForImage(elementDetails.get(position).getDate()));
        if(elementDetails.get(position).getStatus().equalsIgnoreCase("true"))
            status.setImageResource(R.drawable.homework_send);
        else
            status.setImageResource(R.drawable.homework_pending);

        return convertView;
    }
}
