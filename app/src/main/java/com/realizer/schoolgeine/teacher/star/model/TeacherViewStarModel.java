package com.realizer.schoolgeine.teacher.star.model;

/**
 * Created by shree on 11/21/2015.
 */
public class TeacherViewStarModel {
    private String subjectName;
    private String teachername;
    private String comment;
    private String date;
    private String givenStar;
    private String profilrImage = null;

    public void setStudSubject(String subjectName){this.subjectName =subjectName;}
    public String getStudSubject(){return subjectName; }

    public void setteachername(String teachername){this.teachername =teachername;}
    public String getteachername(){return teachername; }

    public void setcomment(String comment){this.comment=comment;}
    public String getcomment(){return comment;}

    public void setgivenStar(String givenStar){this.givenStar = givenStar;}
    public String getgivenStar(){return givenStar;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilrImage() {
        return profilrImage;
    }

    public void setProfilrImage(String profilrImage) {
        this.profilrImage = profilrImage;
    }
}
