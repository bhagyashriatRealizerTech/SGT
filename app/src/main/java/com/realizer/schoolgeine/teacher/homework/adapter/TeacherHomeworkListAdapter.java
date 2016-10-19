package com.realizer.schoolgeine.teacher.homework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkListModel;

import java.util.ArrayList;

public class TeacherHomeworkListAdapter extends BaseAdapter {


        private static ArrayList<TeacherHomeworkListModel> hList;
        private LayoutInflater mhomeworkdetails;
        private Context context1;
        boolean isImageFitToScreen;
        View convrtview;
    String wherFrom;



        public TeacherHomeworkListAdapter(Context context, ArrayList<TeacherHomeworkListModel> homeworklist,String wherFrom) {
            hList = homeworklist;
            mhomeworkdetails = LayoutInflater.from(context);
            context1 = context;
            this.wherFrom = wherFrom;

        }
        @Override
        public int getCount() {
            return hList.size();
        }

        @Override
        public Object getItem(int position) {

            return hList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

    @Override
    public int getViewTypeCount() {
        return hList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_homework_list_layout, null);
            holder = new ViewHolder();
            holder.subject = (TextView) convertView.findViewById(R.id.txthomeworksubject);
            //holder.homework = (TextView) convertView.findViewById(R.id.txthomework);
            holder.image = (ImageView) convertView.findViewById(R.id.imghomework);
            holder.homeworktext = (TextView) convertView.findViewById(R.id.txthomework1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        if(hList.get(position).getHasSync().equalsIgnoreCase("true")) {
           holder.image.setImageResource(R.drawable.homework_send);
        }
        else
            holder.image.setImageResource(R.drawable.homework_pending);


        //holder.subject.setText(hList.get(position).getSubject()+" : "+hList.get(position).getGivenBy());
        holder.subject.setText(hList.get(position).getSubject() + " :");




        if(hList.get(position).getHomework().equals("NoText"))
        {
            holder.homeworktext.setText(hList.get(position).getSubject()+" "+ wherFrom);
        }
        else {
            holder.homeworktext.setText(hList.get(position).getHomework());
        }

       /* if(hList.get(position).getImage().equals("NoImage"))
        {
            holder.homework.setText("");
        }
        else {
            String s =holder.homework.getText().toString();
            holder.homework.setText("Click Here To View Image");
            holder.homework.setPaintFlags(   holder.homework.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        }*/


        return convertView;
    }

    static class ViewHolder
        {
            TextView subject;
            //TextView homework;
            TextView homeworktext;
            ImageView image;

        }
    }

