package com.realizer.schoolgeine.teacher.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.chat.model.TeacherQueryViewListModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryMessageCenterListAdapter extends BaseAdapter {


    private static ArrayList<TeacherQueryViewListModel> messageList;
    private LayoutInflater mhomeworkdetails;
    private String Currentdate;
    String date;
    int counter;
    int datepos;
    ViewHolder holder;

    public TeacherQueryMessageCenterListAdapter(Context context, ArrayList<TeacherQueryViewListModel> messageList1) {
        messageList = messageList1;
        mhomeworkdetails = LayoutInflater.from(context);
        Calendar c = Calendar.getInstance();
        counter = 0;
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Currentdate = df.format(c.getTime());
        date = Currentdate;
        datepos = -1;
        Log.d("Date", Currentdate);
    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {

        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getViewTypeCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.chat_messgagecenter_list_layout, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.txtdate);
            holder.date.setTag(position);
            holder.sendername = (TextView) convertView.findViewById(R.id.txtsenderName);
            holder.time = (TextView) convertView.findViewById(R.id.txttime);
            holder.message = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.newmessage = (LinearLayout) convertView.findViewById(R.id.linlayoutnewmsgbar);
            holder.datelayout = (LinearLayout) convertView.findViewById(R.id.linlayoutdate);
            holder.initial = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.profile_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("Date", Currentdate);
        Log.d("Date1", messageList.get(position).getSenddate());

        holder.newmessage.setVisibility(View.GONE);
        if(position==0)
        {
            if(messageList.get(position).getSenddate().equals(Currentdate))
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(false, "");
                }
            }
            else
            {
                hideShowDate(true,messageList.get(position).getSenddate());
            }
        }
       else if(position>0) {
            if (messageList.get(position - 1).getSenddate().equals(messageList.get(position).getSenddate()) )
            {
                hideShowDate(false, "");
            }
            else
            {
                int datetag = (Integer)holder.date.getTag();
                if (counter == 0 || datetag == datepos)
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                    counter = counter + 1;
                    datepos= (Integer)holder.date.getTag();
                }
                else
                {
                    hideShowDate(true,messageList.get(position).getSenddate());
                }

            }
        }

        String name[] = messageList.get(position).getSendername().trim().split(" ");

        if(messageList.get(position).getProfileImage() != null && !messageList.get(position).getProfileImage().equals("") && !messageList.get(position).getProfileImage().equalsIgnoreCase("null"))
        {
            String urlString = messageList.get(position).getProfileImage();
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
            holder.profilepic.setVisibility(View.VISIBLE);
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,holder.profilepic,holder.initial,messageList.get(position).getSendername(),newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                holder.initial.setVisibility(View.GONE);
                holder.profilepic.setVisibility(View.VISIBLE);
                holder.profilepic.setImageBitmap(bitmap);
            }
        }
        else {
            holder.initial.setVisibility(View.VISIBLE);
            holder.profilepic.setVisibility(View.GONE);

            char fchar = name[0].toUpperCase().charAt(0);
            char lchar = name[0].toUpperCase().charAt(0);
            for (int i = 0; i < name.length; i++) {
                if (!name[i].equals("") && i == 0)
                    fchar = name[i].toUpperCase().charAt(0);
                else if (!name.equals("") && i == (name.length - 1))
                    lchar = name[i].toUpperCase().charAt(0);

            }

            holder.initial.setText(fchar + "" + lchar);

        }

        holder.time.setText(messageList.get(position).getTime());
        holder.sendername.setText(messageList.get(position).getSendername());
        holder.message.setText(messageList.get(position).getMsg());




        return convertView;
    }

    //

    public void hideShowDate(boolean flag,String setdate)
    {
        if(flag)
        {
            ViewGroup.LayoutParams layoutParams = holder.datelayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.datelayout.setLayoutParams(layoutParams);
            holder.date.setText(Config.getDate(setdate, "D"));
            date = setdate;



        }

        else
        {

            ViewGroup.LayoutParams layoutParams = holder.datelayout.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            holder.datelayout.setLayoutParams(layoutParams);


        }
    }


    static class ViewHolder
    {
        TextView date,sendername,time,initial,message;
        LinearLayout newmessage,datelayout;
        ImageView profilepic;

    }
}

