package com.vampire.shareforwechat.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.thread.ThreadPool;
import com.vampire.shareforwechat.BaseApp;
import com.vampire.shareforwechat.activity.group.GroupSendingActivity;
import com.vampire.shareforwechat.interfaces.KeyValue;
import com.vampire.shareforwechat.receiver.GroupControlReceiver;

import java.util.Calendar;

/**
 * Created by Vampire on 2017/5/27.
 */

public class GroupControlService extends Service implements KeyValue {
    private static final String TAG = "GroupControlService";
    private static Context mContext = BaseApp.getContext();
    private int year, month, day, hour, minute, second;


    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        year = (int) SPUtil.get(mContext, GROUP_YEAR, -1);
        month = (int) SPUtil.get(mContext, GROUP_MONTH, -1);
        day = (int) SPUtil.get(mContext, GROUP_DAY, -1);
        hour = (int) SPUtil.get(mContext, GROUP_HOUR, -1);
        minute = (int) SPUtil.get(mContext, GROUP_MINUTE, -1);
        second = (int) SPUtil.get(mContext, GROUP_SECOND, -1);
        Log.e(TAG, "onStartCommand: " + year);
        Log.e(TAG, "onStartCommand: " + month);
        Log.e(TAG, "onStartCommand: " + day);
        Log.e(TAG, "onStartCommand: " + hour);
        Log.e(TAG, "onStartCommand: " + minute);
        Log.e(TAG, "onStartCommand: " + second);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,  GroupSendingActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {

        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
        return super.onStartCommand(i, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        ThreadPool.thredP.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Log.e(TAG, "run: 软件在后台运行" );
//                        Thread.sleep(30000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
