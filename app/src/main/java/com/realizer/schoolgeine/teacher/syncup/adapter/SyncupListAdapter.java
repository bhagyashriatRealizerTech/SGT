package com.realizer.schoolgeine.teacher.syncup.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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
public class SyncupListAdapter extends BaseAdapter {


    private static ArrayList<QueueListModel> syncuplist;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;


    public SyncupListAdapter(Context context, ArrayList<QueueListModel> syncuplist1) {
        syncuplist = syncuplist1;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
    }

    @Override
    public int getCount() {
        return syncuplist.size();
    }

    @Override
    public Object getItem(int position) {

        return syncuplist.get(position);
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
            convertView = publicholidayDetails.inflate(R.layout.teacher_syncup_list_layout, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.txtname);
            holder.time = (TextView) convertView.findViewById(R.id.txttime);
            holder.status = (TextView) convertView.findViewById(R.id.txtstatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.title.setText(syncuplist.get(position).getType());
        holder.time.setText(syncuplist.get(position).getTime());
        if(syncuplist.get(position).getType().equalsIgnoreCase("Feature")) {
            holder.status.setText("Status");
            holder.status.setTextColor(Color.BLACK);
            holder.title.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
        }
        else
            holder.status.setText("Pending");
        if(syncuplist.get(position).getType().equalsIgnoreCase("GiveStar"))
        {
            holder.title.setText("Star");
        }
        else if(syncuplist.get(position).getType().equalsIgnoreCase("EventMaster"))
        {
            holder.title.setText("FunCenter Event");
        }
        else if(syncuplist.get(position).getType().equalsIgnoreCase("EventImages"))
        {
            holder.title.setText("FunCenter Event Image");
        }


        return convertView;
    }

    static class ViewHolder {

        TextView title,time,status;

    }
}
