package com.realizer.schoolgeine.teacher.generalcommunication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGeneralCommunicationListModel;

import java.util.ArrayList;

public class TeacherGeneralCommunicationListAdapter extends BaseAdapter {


        private static ArrayList<TeacherGeneralCommunicationListModel> hList;
        private LayoutInflater mhomeworkdetails;

        public TeacherGeneralCommunicationListAdapter(Context context, ArrayList<TeacherGeneralCommunicationListModel> homeworklist) {
            hList = homeworklist;
            mhomeworkdetails = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {

            Log.d("SIZE", "" + hList.size());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_generalcommunication_list_layout, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.txtdate);
            holder.subject = (TextView) convertView.findViewById(R.id.txtsub);
            holder.initial = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.fullName = (TextView) convertView.findViewById(R.id.txtFullName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(hList.get(position).getCategory().equals("CA")) {
            holder.initial.setText("CA");
            holder.fullName.setText("Cultural Activity");
        }

        else if(hList.get(position).getCategory().equals("SD")) {
            holder.initial.setText("SD");
            holder.fullName.setText("Sports Day");
        }
        else if(hList.get(position).getCategory().equals("FDC")) {
            holder.initial.setText("FDC");
            holder.fullName.setText("Fancy Dress Competitions");
        }
        else if(hList.get(position).getCategory().equals("CM")) {
            holder.initial.setText("CM");
            holder.fullName.setText("Class Meeting");
        }
        else
        {
            holder.initial.setText("O");
            holder.fullName.setText("Others");
        }

        String df[] = hList.get(position).getAnnouncementTime().split(" ");
        holder.date.setText(Config.getDate(df[0], "D"));
        holder.subject.setText(hList.get(position).getAnnouncementText());

        return convertView;
    }


    static class ViewHolder
        {
            TextView date;
            TextView subject;
            TextView initial;
            TextView fullName;
        }
    }

