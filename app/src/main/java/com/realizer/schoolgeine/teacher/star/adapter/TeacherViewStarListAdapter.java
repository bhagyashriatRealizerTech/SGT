package com.realizer.schoolgeine.teacher.star.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.star.model.TeacherViewStarModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by shree on 11/21/2015.
 */
public class TeacherViewStarListAdapter extends BaseAdapter {
    private static ArrayList<TeacherViewStarModel> dailyAttendance;
    private LayoutInflater inflaterDailyAtt;
    Context context;


    public TeacherViewStarListAdapter(Context _context, ArrayList<TeacherViewStarModel> _dailyAttendance)
    {
        context =_context;
        dailyAttendance = _dailyAttendance;
        inflaterDailyAtt = LayoutInflater.from(_context);
    }
    @Override
    public int getCount() {
        return dailyAttendance.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyAttendance.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflaterDailyAtt.inflate(R.layout.parent_viewstar_list_layout, null);
            holder = new ViewHolder();

            holder.txtComment = (TextView) convertView.findViewById(R.id.txtcomnts);
            holder.txtname = (TextView) convertView.findViewById(R.id.txtnames);
            holder.imgViewStar = (ImageView) convertView.findViewById(R.id.imgstar);
            holder.initial = (TextView)convertView.findViewById(R.id.txtinitial);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.profile_image_view);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name1[] = dailyAttendance.get(position).getteachername().trim().split(" ");
        String userName = "";

        for(int j=0;j<name1.length;j++)
        {
            userName = userName+" "+name1[j];
        }

        String name[] = userName.trim().split(" ");
        if(dailyAttendance.get(position).getProfilrImage() != null && !dailyAttendance.get(position).getProfilrImage().equals("") && !dailyAttendance.get(position).getProfilrImage().equalsIgnoreCase("null"))
        {
            String urlString = dailyAttendance.get(position).getProfilrImage();
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
                new GetImages(newURL,holder.profilepic,holder.initial,userName,newURL.split("/")[newURL.split("/").length-1]).execute(AsyncTask.THREAD_POOL_EXECUTOR,newURL);
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
        holder.txtComment.setText("Comment: "+dailyAttendance.get(position).getcomment());
        holder.txtname.setText(userName);
        String stargiven=dailyAttendance.get(position).getgivenStar();

        if(stargiven.equals("VeryGood"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.verygood)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals("Great"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.great)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals("NiceWork"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.nicework)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals("Terrific"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.terrific)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals("SupreStar"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.superstar)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }
        else if(stargiven.equals("WellDone"))
        {
            Bitmap bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.welldone)).getBitmap();
            holder.imgViewStar.setImageBitmap(bitmap);
        }

        return convertView;
    }
    class ViewHolder
    {
        TextView txtComment;
        TextView txtname,initial;
        ImageView imgViewStar, profilepic;;
    }
}
