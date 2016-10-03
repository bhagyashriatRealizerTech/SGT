package com.realizer.schoolgeine.teacher.funcenter.model;

import android.graphics.Bitmap;

/**
 * Created by Win on 28/03/2016.
 */
public class TeacherFunCenterModel {
    String image;
    String text;
    String Date;
    String eventuuid;
    int eventid;
    Bitmap bitmap;

    public String getEventuuid() {
        return eventuuid;
    }

    public void setEventuuid(String eventuuid) {
        this.eventuuid = eventuuid;
    }

    public int getEventid()
    {
        return eventid;
    }

    public void setEventid(int eventid)
    {
        this.eventid = eventid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
