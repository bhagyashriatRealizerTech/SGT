package com.realizer.schoolgeine.teacher.myclass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassModel;

import java.util.ArrayList;

/**
 * Created by shree on 11/26/2015.
 */
public class TeacherMyClassListAdapter extends BaseAdapter {
    private static ArrayList<TeacherMyClassModel> classList;
    private LayoutInflater inflaterClassList;
    Context context;

    public TeacherMyClassListAdapter(Context _context, ArrayList<TeacherMyClassModel> _classnamelist)
    {
        context =_context;
        classList = _classnamelist;
        inflaterClassList = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return classList.size();
    }

    @Override
    public Object getItem(int position) {
        return classList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflaterClassList.inflate(R.layout.teacher_myclass_list_layout, null);
            holder = new ViewHolder();
            holder.Srno = (TextView) convertView.findViewById(R.id.srno);
            holder.Standard = (TextView) convertView.findViewById(R.id.standard);
            holder.Division = (TextView) convertView.findViewById(R.id.division);
            holder.Subject = (TextView) convertView.findViewById(R.id.subjectname);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Srno.setText(classList.get(position).getSrno());
        holder.Standard.setText(classList.get(position).getStandard());
        holder.Division.setText(classList.get(position).getDivisioin());
        holder.Subject.setText(classList.get(position).getSubjectName());
        return convertView;
    }
    class ViewHolder
    {
        TextView Srno;
        TextView Standard;
        TextView Division;
        TextView Subject;
    }
}

