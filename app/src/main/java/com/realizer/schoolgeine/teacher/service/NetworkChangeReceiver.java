package com.realizer.schoolgeine.teacher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.realizer.schoolgeine.teacher.Utils.Singlton;

/**
 * Created by Bhagyashri on 2/17/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        Singlton obj = Singlton.getInstance();

        if (wifi.getState().equals(NetworkInfo.State.CONNECTED) || mobile.getState().equals(NetworkInfo.State.CONNECTED)) {

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String getValueBack = sharedpreferences.getString("Login", "");
            if(getValueBack.length()==0)
                getValueBack="false";
            // Do something
            try {
                if (!getValueBack.equalsIgnoreCase("false")) {
                    Intent xmppserviceintent = new Intent(context, AutoSyncService.class);
                    Singlton.setAutoserviceIntent(xmppserviceintent);
                    context.startService(xmppserviceintent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (Singlton.getAutoserviceIntent() != null)
                context.stopService(Singlton.getAutoserviceIntent());
        }
    }
}