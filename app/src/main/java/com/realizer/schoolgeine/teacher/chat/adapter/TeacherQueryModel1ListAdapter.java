package com.realizer.schoolgeine.teacher.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Win on 11/20/2015.
 */
public class TeacherQueryModel1ListAdapter extends BaseAdapter {


    private static ArrayList<TeacherQuery1model> pList;
    private LayoutInflater publicholidayDetails;
    private Context context1;
    boolean isImageFitToScreen;
    View convrtview;
    PhotoViewAttacher mAttacher;
    private String Currentdate;


    public TeacherQueryModel1ListAdapter(Context context, ArrayList<TeacherQuery1model> dicatationlist) {
        pList = dicatationlist;
        publicholidayDetails = LayoutInflater.from(context);
        context1 = context;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        Currentdate = df.format(c.getTime());
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
            convertView = publicholidayDetails.inflate(R.layout.teacher_querygen_list_layout, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txtthreadname);
            holder.unreadcount = (TextView) convertView.findViewById(R.id.txtunreadcount);
            holder.initial = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.lastmsgtime = (TextView) convertView.findViewById(R.id.txtdate);
            holder.lstmsgsendername = (TextView) convertView.findViewById(R.id.txtsendername);
            holder.useImage = (ImageView)convertView.findViewById(R.id.img_user_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        String name[] = pList.get(position).getUname().trim().split(" ");

        if(pList.get(position).getProfilrImage() != null && !pList.get(position).getProfilrImage().equals("") && !pList.get(position).getProfilrImage().equalsIgnoreCase("null"))
        {
            String urlString = pList.get(position).getProfilrImage();
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
                new GetImages(newURL,holder.useImage,holder.initial,pList.get(position).getUname(),newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
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
            char fchar = name[0].toUpperCase().charAt(0);
            char lchar = name[0].toUpperCase().charAt(0);
            for (int i = 0; i < name.length; i++) {
                if (!name[i].equals("") && i == 0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if (!name.equals("") && i == (name.length - 1))
                    lchar = name[i].toUpperCase().charAt(0);

            }

            holder.initial.setVisibility(View.VISIBLE);
            holder.useImage.setVisibility(View.GONE);
            holder.initial.setText(fchar + "" + lchar);
        }
        String userName = "";

        for(int i=0;i<name.length;i++)
        {
            userName = userName+" "+name[i];
        }
        holder.name.setText(userName.trim());
        holder.lstmsgsendername.setText(pList.get(position).getSendername()+": "+pList.get(position).getLastMessage());
        if(Config.getDate(pList.get(position).getLastMsgDate(),"D").equalsIgnoreCase("Today"))
            holder.lastmsgtime.setText(Config.getDate(pList.get(position).getLastMsgDate(),"T"));
        else
            holder.lastmsgtime.setText(Config.getDate(pList.get(position).getLastMsgDate(), "D"));
        //holder.lastmsg.setText(pList.get(position).getLastMessage());

        if(pList.get(position).getUnreadCount() != 0)
        {
            holder.unreadcount.setVisibility(View.VISIBLE);
            holder.unreadcount.setText(""+pList.get(position).getUnreadCount());

        }
        else
        {
            holder.unreadcount.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {

       TextView name,unreadcount,initial,lastmsgtime,lstmsgsendername;
        ImageView useImage;

    }
}
