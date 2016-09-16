/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.realizer.schoolgeine.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.realizer.schoolgeine.teacher.Notification.NotificationModel;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";


    public GCMIntentService() {
        super(Config.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {

        Log.d(TAG, "Device registered: regId = " + registrationId);
        Config.displayMessage(context, getString(R.string.gcm_registered));

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String getValueBack = sharedpreferences.getString("UidName", "");
        String empID =getValueBack;

        ServerUtilities.register(context, registrationId, empID);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        //Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        //SharedPreferences Pref =
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            //ServerUtilities.unregister(context, registrationId);  commented today
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
           // Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
       // Log.i(TAG, "Received message");
         String message = intent.getStringExtra("message");
         Log.d("Received message", message);
       // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
       // Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
       // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
       // Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
       // Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,
                //errorId))
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues search_layout notification to inform the user that server has sent search_layout message.
     */
    private static void generateNotification(Context context, String message) {

        DatabaseQueries qr  = new DatabaseQueries(context);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
        String date = df.format(calendar.getTime());
        Date sendDate =  new Date();
        try {
            sendDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String msg[] = message.split("@@@");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stdC=preferences.getString("STANDARD", "");
        String divC = preferences.getString("DIVISION", "");

        String uname[]=qr.Getuname(msg[2]).split("@@@");
        String imageurl = null;
        if(uname.length>1)
            imageurl = uname[1];

        long n = qr.insertQuery("false", msg[2], msg[3], msg[4], date, "true", sendDate, imageurl, uname[0]);
            if(n>0) {
                n=0;
                int unread = qr.GetUnreadCount(stdC, divC, msg[2]);
                n = qr.updateInitiatechat(stdC, divC,uname[0],"true",msg[2],unread+1,imageurl);
                if(n>0)
                {
                    NotificationModel obj = qr.GetNotificationByUserId(msg[2]);
                    if(obj.getId() == 0)
                    {
                       n =0;
                        NotificationModel notification = new NotificationModel();
                        notification.setNotificationId(1);
                        notification.setNotificationDate(date);
                        notification.setNotificationtype("Query");
                        notification.setMessage(msg[4]);
                        notification.setIsRead("false");
                        notification.setAdditionalData2(msg[2]);
                        notification.setAdditionalData1(uname[0]+"@@@"+(unread+1)+"@@@"+imageurl);
                        n = qr.InsertNotification(notification);

                        if(Singlton.getResultReceiver() != null)
                            Singlton.getResultReceiver().send(1,null);
                    }
                    else
                    {
                        n =0;
                        obj.setMessage(msg[4]);
                        obj.setNotificationDate(date);
                        obj.setAdditionalData1(uname[0] + "@@@" + (unread + 1) + "@@@" + imageurl);

                        n = qr.UpdateNotification(obj);

                        Bundle b = new Bundle();
                        b.putInt("NotificationId",1);
                        b.putString("NotificationDate", date);
                        b.putString("NotificationType", "Query");
                        b.putString("NotificationMessage", msg[4]);
                        b.putString("IsNotificationread", "false");
                        b.putString("AdditionalData1",uname[0]+"@@@"+(unread+1)+"@@@"+imageurl);
                        b.putString("AdditionalData2",msg[2]);

                        if(Singlton.getResultReceiver() != null)
                            Singlton.getResultReceiver().send(1,b);
                    }
                }

            }

        Singlton obj = Singlton.getInstance();
        if(obj.getResultReceiver() != null)
        {
            obj.getResultReceiver().send(100,null);
        }
    }

}
