package com.shuangyou.material.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.util.thread.ThreadPool;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * Created by Vampire on 2017/5/29.
 */

public class GetTimeService extends Service {
    public static final String TAG = "GetTimeService";
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.e(TAG, "onStartCommand: 到这里了22" );
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.e(TAG, "run: 在运行");
                    sleep(1000 * 5);
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
