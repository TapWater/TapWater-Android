package com.example.cody.tapwater.objects;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

public class Helper {

    private Context context;
    private Activity activity;

    public final String ROOT = "http://tapwater.herokuapp.com";

    public final String CREATE_USER_URL = ROOT + "/api/v1/users.json";
    public final String AUTHENTICATE_USER_URL = ROOT + "/api/v1/users/authenticate.json";
    public final String GET_STATUS = ROOT + "/api/v1/me.json?device_token=";
    public final String CREATE_DRINK_URL = ROOT + "/api/v1/drinks.json";
    public final String GET_DRINKS_URL = ROOT + "/api/v1/drinks.json?device_token=";
    public final String SYNC_DRINKS_URL = ROOT + "/api/v1/drinks/sync.json";

    public Helper(Context c) {
        context = c;
        activity = (Activity) context;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void lockScreenRotation() {
        switch (context.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    public void enableScreenRotation() {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
