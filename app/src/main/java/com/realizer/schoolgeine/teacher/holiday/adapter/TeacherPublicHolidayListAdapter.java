package com.realizer.schoolgeine.teacher.holiday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.holiday.model.TeacherPublicHolidayListModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class TeacherPublicHolidayListAdapter extends BaseAdapter {


    private static ArrayList<TeacherPublicHolidayListModel> pList;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;

    public TeacherPublicHolidayListAdapter(Context context, ArrayList<TeacherPublicHolidayListModel> dicatationlist) {
        pList = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return pList.size();
    }

    @Override
    public Object getItem(int position) {

        return pList.get(position);
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
            convertView = publicholidayDetails.inflate(R.layout.teacher_publicholiday_list_layout, null);
            holder = new ViewHolder();
            holder.desc = (TextView) convertView.findViewById(R.id.txtholidayname);
            holder.sDate = (TextView) convertView.findViewById(R.id.txtstartdate);
            holder.eDate = (TextView) convertView.findViewById(R.id.txtenddate);
            holder.initial = (TextView)convertView.findViewById(R.id.txtinitial);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String holidayName[] = pList.get(position).getDesc().trim().split(" ");
        if(holidayName.length>1)
        {
            holder.initial.setText(holidayName[0].toUpperCase().charAt(0) + "" + holidayName[1].toUpperCase().charAt(0));
        }
        else if(holidayName.length ==1)
        {
            holder.initial.setText(holidayName[0].toUpperCase().charAt(0)+""+holidayName[0].toUpperCase().charAt(1));
        }

        holder.desc.setText(pList.get(position).getDesc());
        holder.sDate.setText(pList.get(position).getStartDate().trim().split(" ")[0]+" "+pList.get(position).getStartDate().trim().split(" ")[1]);
        holder.eDate.setText(pList.get(position).getEndDate().trim().split(" ")[0]+" "+pList.get(position).getEndDate().trim().split(" ")[1]);

        return convertView;
    }

    static class ViewHolder {

        TextView desc;
        TextView sDate;
        TextView eDate;
        TextView initial;

    }
}
