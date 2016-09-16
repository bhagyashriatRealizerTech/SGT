package com.realizer.schoolgeine.teacher.myclass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.TeacherQueryFragment;
import com.realizer.schoolgeine.teacher.chat.TeacherQueryFragment1;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.generalcommunication.TeacherGCommunicationNewFragment;
import com.realizer.schoolgeine.teacher.generalcommunication.TeacherGeneralCommunicationFragment;
import com.realizer.schoolgeine.teacher.homework.TeacherHomeworkFragment;
import com.realizer.schoolgeine.teacher.homework.TeacherHomeworkNewFragment;
import com.realizer.schoolgeine.teacher.myclass.adapter.TeacherMyClassListAdapter;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherMyClassModel;
import com.realizer.schoolgeine.teacher.star.TeacherGiveStarFragment;
import com.realizer.schoolgeine.teacher.star.TeacherViewStarFragment;
import com.realizer.schoolgeine.teacher.timetable.TeacherTimeTableFragment;
import com.realizer.schoolgeine.teacher.timetable.TeacherTimeTableNewFragment;

import java.util.ArrayList;

/**
 * Created by shree on 12/4/2015.
 */
public class TeacherMyClassDialogBoxActivity extends DialogFragment implements FragmentBackPressedListener {
    ArrayList<TeacherMyClassModel> dailyClassList;
    ListView lstClassList;
    Button cancel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.teacher_myclass_dialog_layout, null);
        lstClassList =(ListView)view.findViewById(R.id.teacherclassname);
        cancel=(Button)view.findViewById(R.id.CancelList);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        cancel.setTypeface(face);
        dailyClassList =qr.GetAllSubDivStd();
        lstClassList.setAdapter(new TeacherMyClassListAdapter(getActivity(), dailyClassList));
        Bundle b = this.getArguments();
        final int k = b.getInt("MYCLASS",0);
        lstClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lstClassList.getItemAtPosition(position);
                TeacherMyClassModel obj = (TeacherMyClassModel)o;
                String std = obj.getStandard();
                String div= obj.getDivisioin();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("STANDARD",std);
                editor.putString("DIVISION",div);
                editor.commit();

                if(k==1)
                {
                    RefreshMyClass(std,div);
                }
                else if(k==2)
                {
                    RefreshHomework(std,div);
                }
                else if(k==3)
                {
                    RefreshGiveStar(std,div);
                }
                else if(k==4)
                {
                    RefreshTimeTable(std,div);
                }
                else if(k==5)
                {
                    //RefreshNewDailyTimeTable(std,div);
                }
                else if(k==6)
                {
                    RefreshNewExamTimeTable(std,div);
                }
                else if(k==7)
                {
                    RefreshQuery(std,div,1);
                }
                else if(k==8)
                {
                    RefreshGCommunication(std,div);
                }
                else if(k==9)
                {
                    RefreshNewGCommunication(std,div);

                }

                else if(k==10)
                {
                    RefreshNewHomework(std,div);
                }

                else if(k==11)
                {
                    RefreshQuery(std,div,2);
                }
                else if(k==12)
                {
                    RefreshViewStar(std,div);
                }

                dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singlton.setSelectedFragment(Singlton.getMainFragment());
                dismiss();
            }
        });
        builder.setTitle("Select Class");
        builder.setView(view);
        return builder.create();
    }

    public ArrayList<TeacherMyClassModel> GetDailyHomeworkList()
    {
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        ArrayList<TeacherMyClassModel> res = new ArrayList<>();
        ArrayList<TeacherMyClassModel> listForDailyHomework =  new ArrayList<>();
        for(int i=0;i<res.size();i++)
        {
            TeacherMyClassModel singleClassname = new TeacherMyClassModel();
            singleClassname.setSrno(res.get(0).getSrno());
            singleClassname.setStandard(res.get(0).getStandard());
            singleClassname.setDivisioin(res.get(0).getDivisioin());
            singleClassname.setSubjectName(res.get(0).getSubjectName());
            listForDailyHomework.add(singleClassname);
        }
        return listForDailyHomework;
        /*Bundle b = this.getArguments();
        String classList = b.getString("StudentClassList");
        ArrayList<TeacherMyClassModel> listForDailyHomework =  new ArrayList<>();
        String [] classnamelist = (classList.toString().split("@@@"));

        for(String studDailyAtt :classnamelist )
        {
            TeacherMyClassModel singleClassname = new TeacherMyClassModel();
            String [] classDetails = studDailyAtt.split(",,");
            singleClassname.setSrno(classDetails[0]);
            singleClassname.setStandard(classDetails[1]);
            singleClassname.setDivisioin(classDetails[2]);
            singleClassname.setSubjectName(classDetails[3]);
            listForDailyHomework.add(singleClassname);
        }
        return listForDailyHomework;*/
    }

//My Class
    public void RefreshMyClass(String std,String div)
    {
        String allStudentList = "1.,,Ajay Shah@@@2.,,Nilam Jadhav@@@3.,,Farhan Bodale@@@4.,,Pravin Jadhav@@@5.,,Ram Magar@@@6.,,Sahil Kadam@@@7.,,Hisha Mulye@@@8.,,Supriya Vichare@@@9.,,Ajay Shah@@@10.,,Nilam Jadhav@@@11.,,Farhan Bodale@@@12.,,Pravin Jadhav@@@13.,,Ram Magar@@@14.,,Sahil Kadam@@@15.,,Hisha Mulye@@@16.,,Supriya Vichare";
        TeacherMyClassStudentFragment fragment = new TeacherMyClassStudentFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentClassList", allStudentList);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

//Give Star
    public void RefreshGiveStar(String std,String div)
    {
        TeacherGiveStarFragment fragment = new TeacherGiveStarFragment();
        Singlton.setSelectedFragment(fragment);
        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    //View Star
    public void RefreshViewStar(String std,String div)
    {
        TeacherViewStarFragment fragment = new TeacherViewStarFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    //Homework
    public void RefreshHomework(String std,String div)
    {
        TeacherHomeworkFragment fragment = new TeacherHomeworkFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT","Homework");
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    //New Homework

    //Homework
    public void RefreshNewHomework(String std,String div)
    {
        TeacherHomeworkNewFragment fragment = new TeacherHomeworkNewFragment();
        Singlton.setSelectedFragment(fragment);
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", "Homework");
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    //TimeTable
    public void RefreshTimeTable(String std,String div)
    {
        Bundle b = this.getArguments();
        int k = b.getInt("FLAG", 0);
        TeacherTimeTableFragment fragment = new TeacherTimeTableFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        String syllabuslist = "First Unit Test Time Table,,NoImage_First Unit Test Syllabus,,Image_Second Unit Test Time Table,,Image_Second Unit Test Syllabus,,Image_First Semester Time Table,,NoImage_First Semester Syllabus,,Image" +
                "_Third Unit Test Time Table,,NoImage_Third unit Test Syllabus,,NoImage_Fourth Unit Test Time Table,,Image_Fourth Unit Test Syllabus,,Image_Second Semester Time Table,,Image_Second sSemester Syllabus,,Image";
        String dictationlist = "Marathi,,lesson1 and 2_Hindi,,lesson no 4 and 5_English,,lesson no 7,8 and9_History,,lesson no 1,2,3,4 and 5";
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("SyllabusList", syllabuslist);
        bundle.putString("DictationList",dictationlist);
        bundle.putInt("Checked",k);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    /*//NewDailyTimeTable
    public void RefreshNewDailyTimeTable(String std,String div)
    {
        TeacherTimeTableNewDailyFragment fragment = new TeacherTimeTableNewDailyFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }
*/
    //NewExamTimeTable
    public void RefreshNewExamTimeTable(String std,String div)
    {
        TeacherTimeTableNewFragment fragment = new TeacherTimeTableNewFragment();
        Singlton.setSelectedFragment(fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }

    //Query
    public void RefreshQuery(String std,String div,int type)
    {
        Fragment fragment=null;
        if(type==1) {
           fragment = new TeacherQueryFragment();
            Singlton.setSelectedFragment(fragment);
        }
        else
        {
           fragment = new TeacherQueryFragment1();
            Singlton.setSelectedFragment(fragment);
            Singlton.setMainFragment(fragment);

        }
        String quries = "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi_" +
                "Vivaan Vishal Salgare_Arrav Amit Mahargude_Pankhudi Pravin Surywanshi_Rudra Sachin Surywanshi" ;
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentNameList", quries);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    //General Communication
    public void RefreshGCommunication(String std,String div)
    {
        TeacherGeneralCommunicationFragment fragment = new TeacherGeneralCommunicationFragment();
        Singlton.setSelectedFragment(fragment);
        Singlton.setMainFragment(fragment);
        String communication = "20/11/2015,,sports day on 21 november,,Sports Day_19/11/2015,,paraents teacher1 meeting at 2 pm on 20 november,,PTA_18/11/2015,,Story reading competition,,Other_17/11/2015,,paraents teacher1 meeting at 2 pm on 18 november,,PTA" +
                "_15/11/2015,,Singing talent competition,,Other_14/11/2015,,sports day on 15 november,,Sports Day_13/11/2015,,paraents teacher1 meeting at 2 pm on 14 november,,PTA_12/11/2015,,sports day on 13 november,,Sports Day";
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("GeneralCommunicationList", communication);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

  //New General Communication
    public void RefreshNewGCommunication(String std,String div)
    {
        TeacherGCommunicationNewFragment fragment = new TeacherGCommunicationNewFragment();
        Singlton.setSelectedFragment(fragment);
        String categorylist = "PTA,,Parent Teacher Assosciation,,SD,,Sports Day_CA,,Cultural Activity_O,,Other_FDC,,Fancy Dress Competition";
        Bundle bundle = new Bundle();
        bundle.putString("CategorNameList", categorylist);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onFragmentBackPressed() {
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
    }
}
