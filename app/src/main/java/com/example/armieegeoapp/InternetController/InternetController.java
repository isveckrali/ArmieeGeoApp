package com.example.armieegeoapp.InternetController;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetController {

    //Return internet accessing
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
