package com.rabbit.fans.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.rabbit.fans.model.TimeEntity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Vampire on 2017/5/27.
 */

public class GetFansService extends Service {
    private static final String TAG = "GetFansService";
    private List<TimeEntity> timeList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e(TAG, "onStartCommand: 服务来了" );
        timeList = DataSupport.findAll(TimeEntity.class);
        Log.e(TAG, "onStartCommand: size"+timeList.size() );
        for (TimeEntity entity:timeList){
            stopAlarm(entity.getId());
        }
        for (TimeEntity entity:timeList){
            setAlarm(entity);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setAlarm(TimeEntity entity) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AddFansService.class);
        PendingIntent pi = PendingIntent.getService(this, entity.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        LogTool.d("闹钟时间"+entity.getHour()+"h"+entity.getMinute()+"min"+entity.getSecond()+"s");
        Log.e(TAG, "闹钟时间"+entity.getHour()+"h"+entity.getMinute()+"min"+entity.getSecond()+"s" );
        calendar.set(Calendar.HOUR_OF_DAY, entity.getHour());
        calendar.set(Calendar.MINUTE, entity.getMinute());
        calendar.set(Calendar.SECOND, entity.getSecond());
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            LogTool.d("时间过了,闹钟不会执行");
            return;
        }
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    private void stopAlarm(int requestCode){
        Intent intent = new Intent(this, AddFansService.class);
        PendingIntent pi = PendingIntent.getService(this, requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
