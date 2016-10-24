package com.realizer.schoolgeine.teacher.timetable.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.PhotoAlbumActivity;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.view.FullImageViewPager;

import java.util.ArrayList;

/**
 * Created by Win on 08/04/2016.
 */
public class TimeTableDetailAdapter extends BaseAdapter
{
    private ArrayList<TeacherHomeworkModel> elementDetails;
    private LayoutInflater mInflater;
    Context context;
    String imagelist;
    String title;
    public TimeTableDetailAdapter(Context context, ArrayList<TeacherHomeworkModel> results , String imagelist,String title)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.imagelist = imagelist;
        this.title = title;
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

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.row_multi_photo_item_hw, null);
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imgThumb);
        ImageButton imgdelete = (ImageButton)convertView.findViewById(R.id.chkImage);
        imgdelete.setTag(position);
        imgdelete.setVisibility(View.GONE);

        if(elementDetails.get(position).getPic() != null)
            imageview.setImageBitmap(elementDetails.get(position).getPic());


            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context,FullImageViewPager.class);
                    intent.putExtra("HEADERTEXT",title);
                    intent.putExtra("HWUUID", elementDetails.get(position).getHid()+position);

                    context.startActivity(intent);
                }
            });




        return convertView;
    }




}
