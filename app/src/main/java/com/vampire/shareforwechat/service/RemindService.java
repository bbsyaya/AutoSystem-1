package com.vampire.shareforwechat.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.util.thread.ThreadPool;
import com.vampire.shareforwechat.model.alarm.RemindEntity;
import com.vampire.shareforwechat.receiver.AlarmReceiver;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.vampire.shareforwechat.receiver.AlarmReceiver.EXTRA_RECEIVER_CONTENT;


/**
 * Created by 焕焕 on 2017/5/18.
 */

public class RemindService extends Service {
    private static final String TAG = "RemindService";
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    private List<RemindEntity> remindList = new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e(TAG, "onStartCommand: 开启服务" );
        remindList = DataSupport.findAll(RemindEntity.class);
        for(RemindEntity remindEntity:remindList){
            for (int i = 1; i <6 ; i++) {
                stopAlarm(remindEntity.getId()+i);
            }
        }
        for (RemindEntity remindEntity:remindList){
            if (remindEntity.isRemind()){
                setAlarm(remindEntity.getFirstHour(),remindEntity.getFirstMinute(),remindEntity.getContent(),remindEntity.getId()+1);
                setAlarm(remindEntity.getSecondHour(),remindEntity.getSecondMinute(),remindEntity.getContent(),remindEntity.getId()+2);
                setAlarm(remindEntity.getThirdHour(),remindEntity.getThirdMinute(),remindEntity.getContent(),remindEntity.getId()+3);
                setAlarm(remindEntity.getForthHour(),remindEntity.getForthMinute(),remindEntity.getContent(),remindEntity.getId()+4);
                setAlarm(remindEntity.getFifthHour(),remindEntity.getFifthMinute(),remindEntity.getContent(),remindEntity.getId()+5);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ThreadPool.thredP.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
//                    Log.e(TAG, "run: " );
                }
            }
        });
    }

    private void setAlarm(int hourOfDay, int minute, String content, int requestCode) {
        if (hourOfDay==0){
            return;
        }
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EXTRA_RECEIVER_CONTENT, content+hourOfDay+":"+minute);
        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_MONTH, 1);// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        }
        Log.e(TAG, "onTimeSet: " + calendar.getTimeInMillis()+"@@@"+System.currentTimeMillis());
        Log.e(TAG, calendar.getTimeInMillis()-System.currentTimeMillis()+"$$$");
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                INTERVAL, pi);
    }

    private void stopAlarm(int requestCode){
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
    }
}
