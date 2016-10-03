package com.realizer.schoolgeine.teacher.funcenter.model;

import android.graphics.Bitmap;

/**
 * Created by Win on 20/04/2016.
 */
public class TeacherFunCenterGalleryModel
{
    String image;
    String date;
    String status;
    Bitmap bitmap;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
