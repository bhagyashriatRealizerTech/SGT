package com.realizer.schoolgeine.teacher.homework.newhomework.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.gallaryimagepicker.PhotoAlbumActivity;
import com.realizer.schoolgeine.teacher.homework.model.TeacherHomeworkModel;
import com.realizer.schoolgeine.teacher.R;

import java.util.ArrayList;

/**
 * Created by Win on 08/04/2016.
 */
public class NewHomeworkGalleryAdapter extends BaseAdapter
{
    private ArrayList<TeacherHomeworkModel> elementDetails;
    private ArrayList<String> imageList;
    private LayoutInflater mInflater;
    Context context;
    Bitmap decodedByte;
    String data;
    TextView datetext;
    ImageView status;
    boolean flag;
    int REQUEST_CAMERA = 100;
    Fragment fragment;
    public NewHomeworkGalleryAdapter(Context context, ArrayList<TeacherHomeworkModel> results,ArrayList<String> imageList,boolean flag,Fragment fragment)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.imageList = imageList;
        Singlton.setFialbitmaplist(elementDetails);
        this.flag = flag;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return elementDetails.size();
    }

    public Object getItem(int position)
    {
        return elementDetails.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.row_multi_photo_item_hw, null);
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imgThumb);
        ImageButton imgdelete = (ImageButton)convertView.findViewById(R.id.chkImage);
        imgdelete.setTag(position);
        if(elementDetails.get(position).getPic() != null)
            imageview.setImageBitmap(elementDetails.get(position).getPic());

        if(elementDetails.get(position).getHwTxtLst().equalsIgnoreCase("NoIcon")) {
            imgdelete.setVisibility(View.GONE);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singlton.setImageList(imageList);

                    final Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");

                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.imagepickerdialog_layout, null);
                    ImageButton camera_btn = (ImageButton) dialoglayout.findViewById(R.id.img_camera);
                    ImageButton gallery_btn = (ImageButton) dialoglayout.findViewById(R.id.img_gallary);
                    Button cancel = (Button) dialoglayout.findViewById(R.id.btn_cancel);
                    cancel.setTypeface(face);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialoglayout);

                    final AlertDialog alertDialog = builder.create();


                    camera_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fragment.startActivityForResult(intent, REQUEST_CAMERA);
                            alertDialog.dismiss();
                        }
                    });


                    gallery_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, PhotoAlbumActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("FunCenter", false);
                            b.putBoolean("Homework",flag);
                            intent.putExtras(b);
                            context.startActivity(intent);
                            alertDialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });
        }

        imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (Integer)v.getTag();

                elementDetails.remove(pos);
                Singlton.setFialbitmaplist(elementDetails);
                imageList.remove(pos);
                notifyDataSetChanged();

            }
        });


        return convertView;
    }


}
