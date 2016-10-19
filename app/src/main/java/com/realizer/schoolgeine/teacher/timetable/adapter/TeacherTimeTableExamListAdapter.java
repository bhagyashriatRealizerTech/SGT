package com.realizer.schoolgeine.teacher.timetable.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.timetable.model.TeacherTimeTableExamListModel;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class TeacherTimeTableExamListAdapter extends BaseAdapter {


    private static ArrayList<TeacherTimeTableExamListModel> sList;
    private LayoutInflater syllabusDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;


    public TeacherTimeTableExamListAdapter(Context context, ArrayList<TeacherTimeTableExamListModel> syllabuslist) {
        sList = syllabuslist;
        syllabusDetails = LayoutInflater.from(context);
        context1 = context;
    }
    @Override
    public int getCount() {
        return sList.size();
    }

    @Override
    public Object getItem(int position) {

        return sList.get(position);
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
            convertView = syllabusDetails.inflate(R.layout.teacher_timetable_exam_list_layout, null);
            holder = new ViewHolder();
            holder.TTName = (TextView) convertView.findViewById(R.id.timetablename);
            holder.TTDate = (TextView) convertView.findViewById(R.id.timetableDate);
            holder.TTTeacher = (TextView) convertView.findViewById(R.id.tachername);
            holder.uploadstatus = (ImageView) convertView.findViewById(R.id.uploadstatus);
          //  holder.syllabus = (ImageView) convertView.findViewById(R.id.imgsyllabus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.TTName.setText(sList.get(position).getTitle());
        holder.TTDate.setText(Config.getDate(sList.get(position).getDate(), "D"));
        holder.TTTeacher.setText(sList.get(position).getDescription());
        holder.TTTeacher.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_attachment, 0);
        holder.TTTeacher.setCompoundDrawablePadding(7);

        if(sList.get(position).getHasUploaded().equalsIgnoreCase("true")) {
            holder.uploadstatus.setImageResource(R.drawable.homework_send);
        }
        else
            holder.uploadstatus.setImageResource(R.drawable.homework_pending);

        return convertView;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    static class ViewHolder
    {
        TextView TTName;
        TextView TTDate;
        TextView TTTeacher;
        ImageView uploadstatus;
    }
}

