package com.realizer.schoolgeine.teacher.generalcommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.generalcommunication.TeacherGCommunicationNewFragment;
import com.realizer.schoolgeine.teacher.generalcommunication.model.TeacherGCommunicationNewListModel;

import java.util.ArrayList;

/**
 * Created by Win on 11/25/2015.
 */
public class TeacherGCommunicationNewListAdapter extends BaseAdapter {


    private static ArrayList<TeacherGCommunicationNewListModel> hList;
    private LayoutInflater mhomeworkdetails;
    private boolean userSelected = false;
    ViewHolder holder;
    RadioButton mSelectedRB;
    int mSelectedPosition=-1;
    TeacherGCommunicationNewFragment frag;
    Context context;

    public TeacherGCommunicationNewListAdapter(Context context, ArrayList<TeacherGCommunicationNewListModel> homeworklist, TeacherGCommunicationNewFragment frag) {
        hList = homeworklist;
        this.frag = frag;
        mhomeworkdetails = LayoutInflater.from(context);
        this.context=context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_newgcommunication_list_layout, null);
            holder = new ViewHolder();
            //holder.catfullname = (TextView) convertView.findViewById(R.id.txtcategoryname);
            holder.catshorname = (RadioButton) convertView.findViewById(R.id.rbtcategoryname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

            }
       /* if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/

       // holder.catfullname.setText(hList.get(position).getCatshortname());
        holder.catshorname.setText(hList.get(position).getCatfullname());
        holder.catshorname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(position != mSelectedPosition && mSelectedRB != null){
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton)v;
                frag.setData(mSelectedRB.getText().toString());
            }
        });

        if(mSelectedPosition != position){
            holder.catshorname.setChecked(false);
        }else{
            holder.catshorname.setChecked(true);
            if(mSelectedRB != null && holder.catshorname != mSelectedRB){
                mSelectedRB = holder.catshorname;
            }
        }

      /*  if(holder.catshorname.getText().toString().isEmpty())
        {
            Toast.makeText(this.context,"Slect Catagory",Toast.LENGTH_SHORT).show();
        }*/
        return convertView;
    }

    static class ViewHolder {
        TextView catfullname;
        RadioButton catshorname;
    }
}