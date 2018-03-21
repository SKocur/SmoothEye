package com.kocur.szymon.smootheye;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <h1>Tools</h1>
 *
 * This class contains simple methods which can be accessed in a static way.
 */
public class Tools {

    /**
     * Checks if application has access to the internet.
     *
     * @param context Context of activity which calls this method.
     * @return true if network is available.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
