package com.vampire.shareforwechat.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vampire.shareforwechat.receiver.AlarmReceiver;

import static com.vampire.shareforwechat.receiver.AlarmReceiver.EXTRA_RECEIVER_CONTENT;


/**
 * Created by 焕焕 on 2017/5/17.
 */

public class AlarmDialogActivity extends Activity {
    private static final String TAG = "AlarmDialogActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this)
                .setTitle(AlarmReceiver.content)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("贪睡十分钟", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delayAlarm();
                        finish();
                    }
                })
                .show();




    }

    private void delayAlarm() {
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EXTRA_RECEIVER_CONTENT,AlarmReceiver.content);
        PendingIntent pi = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10*60*1000, pi);
    }

}
