package com.ashtech.sahayak_updated.HelperClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {


    public boolean isConnected(Context context) {

        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if((wifiConn!=null&&( wifiConn.isConnected())) || (dataConn!=null && (dataConn.isConnected())))
        {
            return true;
        }
        else
            return false;
    }
}
