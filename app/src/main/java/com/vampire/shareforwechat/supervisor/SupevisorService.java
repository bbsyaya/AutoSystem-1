package com.vampire.shareforwechat.supervisor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vampire.shareforwechat.receiver.AlarmReceiver;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 焕焕 on 2017/5/22.
 */

public class SupevisorService extends Service {
    public static final String EXTRA_ID= "id";
    private static final String TAG ="SupevisorService" ;

    private List<SupevisorEntity> supevisorList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        supevisorList = DataSupport.findAll(SupevisorEntity.class);
        for (SupevisorEntity entity : supevisorList) {
            stopAlarm(entity.getId());
        }
        for (SupevisorEntity entity : supevisorList) {
            setAlarm(entity);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setAlarm(SupevisorEntity entity) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SupevisorDialogActivity.class);
        intent.putExtra(EXTRA_ID, entity.getId());
        PendingIntent pi = PendingIntent.getActivity(this, entity.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, entity.getYear());
        calendar.set(Calendar.MONTH, entity.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, entity.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, entity.getHour());
        calendar.set(Calendar.MINUTE, entity.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            return;
        }
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    private void stopAlarm(int id) {
        Intent intent = new Intent(this, SupevisorDialogActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
    }
}
