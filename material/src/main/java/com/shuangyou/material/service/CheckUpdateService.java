package com.shuangyou.material.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.UserManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.update.AppUpdateUtil;
import com.kidney_hospital.base.update.UpdateCallBack;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.thread.ThreadPool;

import okhttp3.FormBody;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * 检查更新
 * Created by Vampire on 2017/5/29.
 */

public class CheckUpdateService extends Service {
    private static final String TAG = "CheckUpdateService";
    private static final String TYPE = "2";//2是朋友圈的app
    private Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand检查 name:" + AppUtils.getVersionCode(mContext));
        String versionName = AppUtils.getVersionCode(mContext);
        FormBody formBody = new FormBody.Builder()
                .add("version", versionName)
                .add("type", TYPE)
                .build();

        AppUpdateUtil updateUtil = new AppUpdateUtil(mContext, HttpApi.UPDATE_URL, formBody);

        updateUtil.checkUpdate(new UpdateCallBack() {
            @Override
            public void onError() {
                Log.e(TAG, "onError: " );
            }

            @Override
            public void isUpdate(String result) {
                Log.e(TAG, "isUpdate: " );
            }

            @Override
            public void isNoUpdate() {
                Log.e(TAG, "isNoUpdate: " );
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
