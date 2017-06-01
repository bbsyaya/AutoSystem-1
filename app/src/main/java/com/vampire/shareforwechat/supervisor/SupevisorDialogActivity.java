package com.vampire.shareforwechat.supervisor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vampire.shareforwechat.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by 焕焕 on 2017/5/22.
 */

public class SupevisorDialogActivity extends Activity {
    private int id;
    MediaPlayer player;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载音乐播放器
        player = MediaPlayer.create(this, R.raw.ding);
        player.setLooping(true);//循环播放
        Intent intent = getIntent();
        id = intent.getIntExtra(SupevisorService.EXTRA_ID,-1);
        List<SupevisorEntity> supevisorList = DataSupport.findAll(SupevisorEntity.class);
        for (SupevisorEntity entity:supevisorList){
            if (id==entity.getId()){
                setDialog(entity);
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }
    private void setDialog(SupevisorEntity entity) {
        new AlertDialog.Builder(this)
                .setTitle(entity.getRemark())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.stop();//停止音乐
                        player.release();//释放声音播放硬件资源
                        Bundle bundle = new Bundle();
                        bundle.putInt(SupevisorService.EXTRA_ID, id);//将id传过去
                        startActivity(SupevisorAddActivity.class,bundle);
                        finish();
                    }
                })
                .setNegativeButton("十分钟后提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.stop();//停止音乐
                        player.release();//释放声音播放硬件资源
                        delayAlarm();
                        finish();
                    }
                })
                .show();
    }

    private void delayAlarm() {
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, SupevisorDialogActivity.class);
        intent.putExtra(SupevisorService.EXTRA_ID,id);
        PendingIntent pi = PendingIntent.getActivity(this,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10*60*1000, pi);
    }
    /**
     * 启动一个Activity
     *
     * @param className 将要启动的Activity的类名
     * @param options   传到将要启动Activity的Bundle，不传时为null
     */
    public float startActivity(Class<?> className, Bundle options) {
        Intent intent = new Intent(this, className);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivity(intent);
        return 0;
    }
}
