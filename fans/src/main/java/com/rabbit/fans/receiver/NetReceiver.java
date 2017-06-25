package com.rabbit.fans.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.kidney_hospital.base.util.server.NetWorkUtils;
import com.rabbit.fans.util.LoadResultUtil;

/**
 * Created by Vampire on 2017/6/24.
 */

public class NetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isNet = NetWorkUtils.isNetworkConnected(context);
            if (LoadResultUtil.onLoadListener!=null){
                LoadResultUtil.onLoadListener.onNetChanged(isNet);
            }

        }
    }
}
