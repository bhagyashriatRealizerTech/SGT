package com.realizer.schoolgeine.teacher.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.GetImages;
import com.realizer.schoolgeine.teacher.Utils.ImageStorage;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.List;



public class FullImageViewPagerAdapter extends PagerAdapter {

    private static List<TeacherHomeworkModel> attachmentList;
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder holder;
    String[] IMG;

    public FullImageViewPagerAdapter(Context context, List<TeacherHomeworkModel> attachmentList) {
        this.context = context;
        FullImageViewPagerAdapter.attachmentList = attachmentList;
        IMG = new String[attachmentList.size()];
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        //Inflate the view
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fullimageview_homework, container, false);

        holder = new ViewHolder();

        holder.imgview = (ImageView) itemView.findViewById(R.id.imageView);
        holder.txtcnt = (TextView) itemView.findViewById(R.id.txtcounter);
        holder.txtTitle = (TextView) itemView.findViewById(R.id.txttitle);
        holder.txtcnt.setMovementMethod(new ScrollingMovementMethod());
        holder.txtTitle.setMovementMethod(new ScrollingMovementMethod());

        holder.txtcnt.setText("" + (position + 1) + " / " + attachmentList.size());
        holder.txtTitle.setText(attachmentList.get(position).getSubject());

                    String temp = attachmentList.get(position).getHwImage64Lst();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap decodedByte = BitmapFactory.decodeFile(temp, bmOptions);
                    holder.imgview.setImageBitmap(decodedByte);

        (container).addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((RelativeLayout) object);
    }

    class ViewHolder {
        TextView txtcnt,txtTitle;
        ImageView imgview;
    }
}
