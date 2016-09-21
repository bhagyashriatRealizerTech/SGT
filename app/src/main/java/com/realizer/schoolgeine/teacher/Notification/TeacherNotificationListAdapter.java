package com.realizer.schoolgeine.teacher.Notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkListModel;
import com.realizer.schoolgeine.teacher.view.SwipeLayout;
import com.realizer.schoolgenie.teacher.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class TeacherNotificationListAdapter extends BaseAdapter {

   private static ArrayList<NotificationModel> notifications;
   private LayoutInflater mNotification;
   private Context context1;
   boolean isImageFitToScreen;
   public SwipeLayout prevSwipedLayout;
   public SwipeLayout swipeLayout;
   View convrtview;
   float x1, x2;

    public TeacherNotificationListAdapter(Context context, ArrayList<NotificationModel> notificationList) {
            notifications = notificationList;
            mNotification = LayoutInflater.from(context);
            context1 = context;
        swipeLayout = null;
        prevSwipedLayout = null;

        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Object getItem(int position) {

            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

    @Override
    public int getViewTypeCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mNotification.inflate(R.layout.teacher_notification_list_layout, null);
            holder = new ViewHolder();
            holder.notificationText = (TextView) convertView.findViewById(R.id.txtmessage);
            holder.notificationDate = (TextView) convertView.findViewById(R.id.txtdate);
            holder.type = (TextView) convertView.findViewById(R.id.txtnotificationtype);
            holder.unreadCount = (TextView) convertView.findViewById(R.id.txtunreadcount);
            holder.notificationImage = (ImageView) convertView.findViewById(R.id.img_user_image);
           /* holder.swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipeLayout);
            holder.swipeLayout.setTag(position);*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String notificationData = "";
        String date = notifications.get(position).getNotificationDate();
        holder.unreadCount.setVisibility(View.INVISIBLE);

        if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Homework") || notifications.get(position).getNotificationtype().equalsIgnoreCase("Classwork"))
        {

            notificationData = notifications.get(position).getAdditionalData1().split("@@@")[2]+" "+
                    notifications.get(position).getNotificationtype()+" "+notifications.get(position).getMessage()
                    +" "+notifications.get(position).getAdditionalData1().split("@@@")[0]+" "+
                    notifications.get(position).getAdditionalData1().split("@@@")[1];

            holder.notificationImage.setImageResource(R.drawable.homework_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("TimeTable"))
        {
            notificationData = notifications.get(position).getAdditionalData1().split("@@@")[0]+" "+
                    notifications.get(position).getNotificationtype()+" "+notifications.get(position).getMessage()
                    +" "+notifications.get(position).getAdditionalData1().split("@@@")[1]+" "+
                    notifications.get(position).getAdditionalData1().split("@@@")[2];
            holder.notificationImage.setImageResource(R.drawable.timetable_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Query"))
        {
            notificationData = "Recieved Message From "+
                    notifications.get(position).getAdditionalData1().split("@@@")[0]+"\nMessage : "+notifications.get(position).getMessage();

            String imageurl[]= notifications.get(position).getAdditionalData1().trim().split("@@@");
            if(imageurl.length == 3) {
                if (imageurl[2] != null && !imageurl[2].equals("") && !imageurl[2].equalsIgnoreCase("null")) {
                    String urlString = imageurl[2];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < urlString.length(); i++) {
                        char c = '\\';
                        if (urlString.charAt(i) == '\\') {
                            urlString.replace("\"", "");
                            sb.append("/");
                        } else {
                            sb.append(urlString.charAt(i));
                        }
                    }
                    String newURL = sb.toString();
                    holder.notificationImage.setVisibility(View.VISIBLE);
                    if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL, holder.notificationImage, newURL.split("/")[newURL.split("/").length - 1]).execute(newURL);
                    else {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                        holder.notificationImage.setImageBitmap(bitmap);
                    }
                } else
                    holder.notificationImage.setImageResource(R.drawable.chat_icon);
            }
            else
                holder.notificationImage.setImageResource(R.drawable.chat_icon);


            date = notifications.get(position).getNotificationDate().trim().split(" ")[0];
            if(notifications.get(position).getAdditionalData1().split("@@@")[1].equals("0"))
            {
                holder.unreadCount.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText(notifications.get(position).getAdditionalData1().split("@@@")[1]);
            }
        }

        if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Query"))
            holder.type.setText("Chat");
            else
        holder.type.setText(notifications.get(position).getNotificationtype());

        holder.notificationDate.setText(Config.getDate(date,"D"));
        holder.notificationText.setText(notificationData);

        return convertView;
    }

    static class ViewHolder
        {
            TextView notificationText,notificationDate,unreadCount,type;
            ImageView notificationImage;
            SwipeLayout swipeLayout;
        }
    }

