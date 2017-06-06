package com.rabbit.fans.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kidney_hospital.base.util.exceptioncatch.LogTool;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/6/6.
 */

public class JpushReceiver extends BroadcastReceiver{
    private static final String TAG = "JpushReceiver";
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())){
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);
            LogTool.d("onReceive: extra-fans----" + extras);
        }else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "推送到的是通知");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);

        }
    }
}
