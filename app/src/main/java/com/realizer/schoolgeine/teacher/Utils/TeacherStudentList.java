package com.realizer.schoolgeine.teacher.Utils;

import android.content.Context;
import android.util.Log;

import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassStudentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhagyashri on 3/19/2016.
 */
public class TeacherStudentList {


    public static List<TeacherMyClassStudentModel> getStudentList(Context context,String std,String div)
    {
        ArrayList<TeacherMyClassStudentModel> listForDailyHomework = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(context);
        try {
            JSONArray arr = new JSONArray(qr.GetAllTableData(std,div));
            Log.d("JSONARR", qr.GetAllTableData(std, div));
            for(int i=0;i<arr.length();i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                TeacherMyClassStudentModel singleClassname = new TeacherMyClassStudentModel();
                singleClassname.setSrnoStd(obj.getString("classRollNo"));
                singleClassname.setStdName(obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName"));
                singleClassname.setUserId(obj.getString("userId"));
                singleClassname.setProfileimage(obj.getString("ThumbnailURL"));
                listForDailyHomework.add(singleClassname);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listForDailyHomework;
    }
}
