package com.wangsc.quicknote.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyReceiver extends BroadcastReceiver {

    //android 中网络变化时所发的Intent的名字
    private static final String netACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            android.util.Log.i("info", "网络状态已经改变");
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                String name = info.getTypeName();
                android.util.Log.i("info", "当前网络名称：" + name);
            } else {
                android.util.Log.i("info", "没有可用网络");
            }
        }
    }
}


