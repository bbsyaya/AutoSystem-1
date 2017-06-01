package com.vampire.shareforwechat;

import android.content.Context;
import android.content.Intent;

import com.kidney_hospital.base.util.exceptioncatch.AbstractCrashReportHandler;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.vampire.shareforwechat.service.GroupControlService;
import com.vampire.shareforwechat.service.RemindService;
import com.vampire.shareforwechat.service.ShareService;
import com.vampire.shareforwechat.util.RetrofitUtils;

import org.litepal.LitePalApplication;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class BaseApp extends LitePalApplication {
    private static BaseApp instance;
    private static Context mContext;
    public static IWXAPI wxApi;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = this;
        new AbstractCrashReportHandler(this);
        RetrofitUtils.init();
//        startService(new Intent(this, GroupControlService.class));//后期加到欢迎页上
//        startService(new Intent(this, SupevisorService.class));//
    }
    public static Context getApplicationCotext() {
        return instance.getApplicationContext();

    }
    public static Context getContext() {
        return mContext;
    }
}
