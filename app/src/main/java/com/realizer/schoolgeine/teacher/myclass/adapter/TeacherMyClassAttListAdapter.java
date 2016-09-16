package com.realizer.schoolgeine.teacher.myclass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherAttendanceListModel;

import java.util.ArrayList;

public class TeacherMyClassAttListAdapter extends BaseAdapter {


        private static ArrayList<TeacherAttendanceListModel> hList;
        private LayoutInflater mhomeworkdetails;
        private Context context1;
        boolean isImageFitToScreen;
        View convrtview;



        public TeacherMyClassAttListAdapter(Context context, ArrayList<TeacherAttendanceListModel> attlist) {
            hList = attlist;
            mhomeworkdetails = LayoutInflater.from(context);
            context1 = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_myclass_stud_att_list_layout, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.txtattdate);
            holder.prsnt = (TextView) convertView.findViewById(R.id.txtattprsnt);
            holder.absnt = (TextView) convertView.findViewById(R.id.txtattabscnt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/

        holder.date.setText(Config.getMediumDate(hList.get(position).getAttendanceDate()));
        holder.prsnt.setText(""+hList.get(position).getPresenceCnt());
        holder.absnt.setText(""+hList.get(position).getAbsentCnt());
        return convertView;
    }

    static class ViewHolder
        {
            TextView date;
            TextView prsnt;
            TextView absnt;

        }
    }

