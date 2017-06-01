package com.shuangyou.material.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.update.AppUpdateUtil;
import com.kidney_hospital.base.update.UpdateCallBack;
import com.kidney_hospital.base.util.AppUtils;

import okhttp3.FormBody;

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
        checkUpdate();
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkUpdate() {
        String versionName = AppUtils.getVersionCode(mContext);
        FormBody formBody = new FormBody.Builder()
                .add("version", versionName)
                .add("type", TYPE)
                .build();

        AppUpdateUtil updateUtil = new AppUpdateUtil(mContext, HttpApi.UPDATE_URL, formBody);

        updateUtil.checkUpdate(new UpdateCallBack() {
            @Override
            public void onError() {
                Toast.makeText(mContext, "服务器错误!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: ");
            }

            @Override
            public void isUpdate(String  result) {
                Log.e(TAG, "isUpdate: ");
                Toast.makeText(mContext, "正在更新,请看通知栏,不要多次点击!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void isNoUpdate() {
                Log.e(TAG, "isNoUpdate: ");
                Toast.makeText(mContext, "没有最新版本!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断服务是否后台运行
     *
     * @return true 在运行 false 不在运行
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
