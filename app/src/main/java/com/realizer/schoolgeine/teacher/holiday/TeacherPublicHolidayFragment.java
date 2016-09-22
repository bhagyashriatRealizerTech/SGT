package com.realizer.schoolgeine.teacher.holiday;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.holiday.adapter.TeacherPublicHolidayListAdapter;
import com.realizer.schoolgeine.teacher.holiday.model.TeacherPublicHolidayListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Win on 11/27/2015.
 */
public class TeacherPublicHolidayFragment extends Fragment implements FragmentBackPressedListener {
    private int pYear;
    private int pMonth;
    private int pDay;
    TextView txtsDate;
    TextView txteDate;
    Calendar cal;
    String formattedDate;
    long totalDays;
    long days;
    DatabaseQueries qr;
    SimpleDateFormat formatter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_holiday_layout, container, false);

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Holiday", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        qr = new DatabaseQueries(getActivity());
        //populate list
        ArrayList<TeacherPublicHolidayListModel> publiholiday = qr.GetHolidayData();
        final ListView listpublicholiday = (ListView) rootView.findViewById(R.id.lsttpublicholiday);
        listpublicholiday.setAdapter(new TeacherPublicHolidayListAdapter(getActivity(), publiholiday));

        return rootView;
    }

    //Callback received when the user "picks" search_layout date in the dialog */
    private DatePickerDialog.OnDateSetListener dpStartDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {

            pMonth = monthOfYear;
            pDay = dayOfMonth;
            pYear = year;
            updateDisplayForStartDate();
            CheckDate();
        }
    };

    private DatePickerDialog.OnDateSetListener dpEndDateListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            pMonth = monthOfYear;
            pDay = dayOfMonth;
            pYear = year;
            updateDisplayForEndDate();
            CheckDate();
        }
    };
    //Updates the date in the TextView (Month is 0 based so add 1)
    private void
    updateDisplayForStartDate()
    {
        txtsDate.setText(new StringBuilder()
                .append(pMonth + 1).append("/")
                .append(pDay).append("/")
                .append(pYear));
    }

    private void updateDisplayForEndDate()
    {
        txteDate.setText(new StringBuilder()
                .append(pMonth + 1).append("/")
                .append(pDay).append("/")
                .append(pYear));
    }

    public void CheckDate()
    {
        try {
            formatter= new SimpleDateFormat("dd/MM/yyyy");
            String startdate = txtsDate.getText().toString();
            Date date1 = formatter.parse(startdate);
            String enddate = txteDate.getText().toString();
            Date date2 = formatter.parse(enddate);
            Calendar c = Calendar.getInstance();
            c.setTime(date2);


            if(date2.compareTo(date1)<0) {
                Config.alertDialog(Singlton.getContext(), "holiday", "Selected dates are invalid");
                //Toast.makeText(getActivity(), "Selected dates are invalid!!!", Toast.LENGTH_SHORT).show();
                txtsDate.setText((cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/"
                        + cal.get(Calendar.YEAR));
                txteDate.setText((cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/"
                        + cal.get(Calendar.YEAR));
            }
            else
            {
                totalDays = (date2.getTime() - date1.getTime());
                days = (TimeUnit.MILLISECONDS.toDays(totalDays));

            }

        }
        catch (ParseException e1){
            e1.printStackTrace();
        }
    }


    private ArrayList<TeacherPublicHolidayListModel> GetPublicHolidayList() {

        Bundle b = this.getArguments();
        //"Marathi,,lesson no 2 and 3_English,,lesson no 4 and 5_Hindi,,hindi homework_History,,history homework_Math,,Math homework"
        String[] holidaylist = (b.getString("PublicHolidayList")).toString().split("_");
        ArrayList<TeacherPublicHolidayListModel> results = new ArrayList<>();

        for (String holiday : holidaylist) {
            String[] hHoliday = holiday.toString().split(",,");
            TeacherPublicHolidayListModel hDetail = new TeacherPublicHolidayListModel();
            hDetail.setDesc(hHoliday[0]);
            hDetail.setStartDate(hHoliday[1]);
            hDetail.setEndDate(hHoliday[2]);

            results.add(hDetail);
        }
        return results;
    }

    @Override
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}


