package com.realizer.schoolgeine.teacher.myclass.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassAttModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class TeacherMyClassAttDetailListAdapter extends BaseAdapter {


    private static List<TeacherMyClassAttModel> pList;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;
    ArrayList<String> finalS;

    public TeacherMyClassAttDetailListAdapter(Context context, List<TeacherMyClassAttModel> dicatationlist) {
        pList = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
        finalS=new ArrayList<>();
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
            convertView = publicholidayDetails.inflate(R.layout.teacher_myclass_attdetail_list_layout, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txtname);
            holder.stat = (TextView) convertView.findViewById(R.id.txtstatus);
            holder.initial = (TextView) convertView.findViewById(R.id.txtrNo);
            holder.useImage = (ImageView)convertView.findViewById(R.id.img_user_image);
            /*holder.profilepic = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.profilepic.setTag(position);*/
           // holder.rollNo = (TextView) convertView.findViewById(R.id.txtrNo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if(pList.get(position).getProfileimage() != null && !pList.get(position).getProfileimage().equals("") && !pList.get(position).getProfileimage().equalsIgnoreCase("null"))
        {
            String urlString = pList.get(position).getProfileimage();
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<urlString.length();i++)
            {
                char c='\\';
                if (urlString.charAt(i) =='\\')
                {
                    urlString.replace("\"","");
                    sb.append("/");
                }
                else
                {
                    sb.append(urlString.charAt(i));
                }
            }
            String newURL=sb.toString();
            holder.initial.setVisibility(View.GONE);
            holder.useImage.setVisibility(View.VISIBLE);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,holder.useImage,holder.initial,pList.get(position).getAttDate(),newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                holder.initial.setVisibility(View.GONE);
                holder.useImage.setVisibility(View.VISIBLE);
                holder.useImage.setImageBitmap(bitmap);
            }
        }
        else
        {
            holder.initial.setVisibility(View.VISIBLE);
            holder.useImage.setVisibility(View.GONE);

            String name[] = pList.get(position).getAttDate().trim().split(" ");
            char fchar  = name[0].toUpperCase().charAt(0);
            char lchar  = name[0].toUpperCase().charAt(0);
            for(int i =0;i<name.length;i++)
            {
                if(!name[i].equals("") && i==0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if(!name.equals("") && i==(name.length-1))
                    lchar = name[i].toUpperCase().charAt(0);

            }

            holder.initial.setText(fchar+""+lchar);
        }


        holder.name.setText(pList.get(position).getAttDate());
        holder.stat.setText(pList.get(position).getAbscnt());
       // holder.initial.setText(pList.get(position).getRollno());
        if(pList.get(position).getAbscnt().equals("P")) {
            holder.stat.setText("Present");
            holder.stat.setTextColor(Color.parseColor("#008000"));
        }
        else {
            holder.stat.setText("Absent");
            holder.stat.setTextColor(Color.RED);
        }

        return convertView;
    }

    static class ViewHolder {

        TextView name,initial;
        TextView stat;
        ImageView useImage;
        //TextView profilepic;
    }
}
