package com.realizer.schoolgeine.teacher.funcenter.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.funcenter.BaseActivity;

import java.util.ArrayList;

/**
 * Created by Win on 28/03/2016.
 */

public class TeacherFunCenterAdapter extends BaseAdapter
{
    private ArrayList<String> allElementDetails;
    Bitmap bitmap;
    private LayoutInflater mInflater;
    Context context;
    private DisplayImageOptions options;
    TeacherFunCenterAdapter funCenterAdapter;
    BaseActivity baseActivity;

    public TeacherFunCenterAdapter(Context context, ArrayList<String> results) {
        allElementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;

    }

    public int getCount()
    {
        return allElementDetails.size();
    }

    public Object getItem(int position)
    {
        return allElementDetails.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.teacher_funcenter_adapter, null);
        final ImageView imageview = (ImageView) convertView.findViewById(R.id.grid_item_image);
       // TextView textview = (TextView) convertView.findViewById(R.id.grid_item_title);
        String data;

        data = allElementDetails.get(position);
       // File file=new File(data.get)

        try {

              bitmap = BitmapFactory.decodeFile(data);

                 imageview.setImageBitmap(bitmap);
                   imageview.setVisibility(View.VISIBLE);
                   //textview.setText("Images");
        }catch (Exception e)
        {

        }
        imageview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getString(R.string.FunCenterAlert));
                alert.setMessage(context.getString(R.string.FunCenterDeleteImages));
                alert.setPositiveButton(context.getString(R.string.FunCenterDelete), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allElementDetails.remove(position);
                        notifyDataSetChanged();
                    }
                });
                alert.setNegativeButton(context.getString(R.string.FunCenterCancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });


        return convertView;
    }


}