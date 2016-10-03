package com.realizer.schoolgeine.teacher.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.realizer.schoolgenie.teacher.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Win on 11/3/2015.
 */
public class Config {
    // File upload url (replace the ip with your server address)
    public static final String URL = "http://104.217.254.180/SJRestWCF/svcEmp.svc/";
    //public static final String URL ="http://192.168.1.14/SJRestWCF/svcEmp.svc/";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "School Genie";

    public static final String SENDER_ID = "817406839541";

    public static final int DRAWABLE_RIGHT = 2;

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCMDemo";

    /**
     * Intent used to display search_layout message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.realizer.schooldiary.teacher1.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";


    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);


    }

    /**
     * @param title to set
     * @return title SpannableString
     */
    public static SpannableString actionBarTitle(String title,Context context) {
        SpannableString s = new SpannableString(title);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
        s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return s;
    }

    public static void hideSoftKeyboardWithoutReq(Context context, View view) {
        try {
            if (view != null) {
                final InputMethodManager inputMethodManager =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    /**
     * @param context
     * @param view
     */
    public static void showSoftKeyboard(Context context, View view) {
        try {
            if (view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getDate(String date, String FLAG) {
        String datetimevalue = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            String Currentdate = df.format(c.getTime());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            String outdate = date.split(" ")[0];
            Date outDateinput = df.parse(outdate);
            String outtime ="";
            if(date.split(" ").length>2) {
                String time[] = date.split(" ")[1].split(":");
                if(time.length>2)
                    outtime = "" + time[0] + ":" + time[1] + " " + date.split(" ")[2];
                else if(time.length>1)
                    outtime = "" + time[0] + ":" + time[1];
                else if(time.length>0)
                    outtime = "" + time[0] ;

            }

            if (FLAG.equals("D") || FLAG.equalsIgnoreCase("DT")) {

                //Current Date Message
                if (outdate.equals(Currentdate)) {
                    datetimevalue = "Today";

                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);

                    //Yesterdays Message
                    if (outdate.equals(df.format(cal.getTime()))) {
                        datetimevalue = "Yesterday";

                    } else {

                        cal.add(Calendar.DATE, -1);

                        for (int i = 0; i < 5; i++) {
                            if (outdate.equals(df.format(cal.getTime()))) {
                                int day = cal.get(Calendar.DAY_OF_WEEK);
                                datetimevalue = getDayOfWeek(day);

                                break;
                            } else {
                                if (i == 4) {
                                    datetimevalue = df1.format(outDateinput);

                                } else
                                    cal.add(Calendar.DATE, -1);
                            }
                        }
                    }
                }
            }

            if (FLAG.equalsIgnoreCase("DT"))
                datetimevalue = datetimevalue + " " + outtime;
            else if (FLAG.equalsIgnoreCase("T"))
                datetimevalue = outtime;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }


    public static String getDayOfWeek(int day) {
        String dayOfWeek = "";

        switch (day) {
            case 1:
                dayOfWeek = "Sunday";
                break;
            case 2:
                dayOfWeek = "Monday";
                break;
            case 3:
                dayOfWeek = "Tuesday";
                break;
            case 4:
                dayOfWeek = "Wednesday";
                break;
            case 5:
                dayOfWeek = "Thursday";
                break;
            case 6:
                dayOfWeek = "Friday";
                break;
            case 7:
                dayOfWeek = "Saturday";
                break;
        }

        return dayOfWeek;
    }

    public static String getMediumDate(String date) {
        String datetimevalue = null;
        try {
            SimpleDateFormat dfinput = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dfoutput = new SimpleDateFormat("dd MMM yyyy");

            Date inDate = dfinput.parse(date);
            datetimevalue = dfoutput.format(inDate);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }

    public static String getMediumDateForEvent(String date) {
        String datetimevalue = null;
        try {
            SimpleDateFormat dfinput = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat dfoutput = new SimpleDateFormat("dd MMMM");

            Date inDate = dfinput.parse(date);
            datetimevalue = dfoutput.format(inDate);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }
    public static String getMediumDateForImage(String date) {
        String datetimevalue = null;
        try {
            SimpleDateFormat dfinput = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat dfoutput = new SimpleDateFormat("dd MMM");

            Date inDate = dfinput.parse(date);
            datetimevalue = dfoutput.format(inDate);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }

    public static boolean isConnectingToInternet(Context context){

        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static String getMonth(int month)
    {
        String mon = "";

        switch (month) {
            case 1:
                mon = "Jan";
                break;
            case 2:
                mon = "Feb";
                break;
            case 3:
                mon = "Mar";
                break;
            case 4:
                mon = "Apr";
                break;
            case 5:
                mon = "May";
                break;
            case 6:
                mon = "Jun";
                break;
            case 7:
                mon = "Jul";
                break;
            case 8:
                mon = "Aug";
                break;
            case 9:
                mon = "Sep";
                break;
            case 10:
                mon = "Oct";
                break;
            case 11:
                mon = "Nov";
                break;
            case 12:
                mon = "Dec";
                break;
        }

        return mon;

    }

    /**
     * @param context
     * @param title
     * @param message
     */
    public static void alertDialog(final Context context, String title, String message) {
        AlertDialog.Builder adbdialog;
        adbdialog = new AlertDialog.Builder(context);
        adbdialog.setTitle(title);
        adbdialog.setMessage(message);
        //adbdialog.setIcon(android.R.drawable.ic_dialog_info);
        adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
            });
        adbdialog.show();

    }

}
