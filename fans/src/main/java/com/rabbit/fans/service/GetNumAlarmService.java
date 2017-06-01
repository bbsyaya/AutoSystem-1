package com.rabbit.fans.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

/**
 * 获取号码的服务
 * Created by Vampire on 2017/5/28.
 */

public class GetNumAlarmService extends Service {
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    private static final String TAG = "GetNumAlarmService";

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Log.e(TAG, "onStartCommand: 获取号码的服务到了" );
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, GetTimeService.class);
        PendingIntent pi = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);//这个100是随便设置的
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            Log.e(TAG, "onStartCommand: 到这里了34" );
            calendar.add(Calendar.DAY_OF_MONTH, 1);// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
            startService(new Intent(this,  GetTimeService.class));

        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                INTERVAL, pi);
        return super.onStartCommand(i, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
