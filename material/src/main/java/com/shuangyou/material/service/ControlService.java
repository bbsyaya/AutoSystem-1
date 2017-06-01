package com.shuangyou.material.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.model.TimeBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


/**
 * Created by Vampire on 2017/5/28.
 */

public class ControlService extends Service {
    private List<TimeBean> timeBeanList = new ArrayList<>();
    @Override
    public int onStartCommand(Intent i,int flags, int startId) {
        Log.e(TAG, "ControlService: 到服务里了" );
        timeBeanList = DataSupport.findAll(TimeBean.class);
        if (timeBeanList.size()!=0){
            setAlarm();
        }
        return super.onStartCommand(i, flags, startId);
    }

    private void setAlarm() {
        if (timeBeanList.get(0)==null){
            return;
        }
        int hour = timeBeanList.get(0).getHour();
        int minute = timeBeanList.get(0).getMinute();
        int second = timeBeanList.get(0).getSecond();
        LogTool.d("cs_hour:"+hour);
        LogTool.d("cs_minute:"+minute);
        LogTool.d("cs_second:"+second);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,  SendingService.class);
        PendingIntent pi = PendingIntent.getService(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);//这个5是随便设的值
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            LogTool.d("61走到这里就错了");
//            DataSupport.deleteAll(TimeBean.class);//删除表中所有数据
        } else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
