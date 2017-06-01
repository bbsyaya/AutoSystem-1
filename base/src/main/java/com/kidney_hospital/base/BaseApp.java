package com.kidney_hospital.base;

import android.content.Context;

import com.kidney_hospital.base.util.exceptioncatch.AbstractCrashReportHandler;
import com.kidney_hospital.base.util.server.RetrofitUtils;

import org.litepal.LitePalApplication;

/**
 * Created by Vampire on 2017/5/27.
 */

public class BaseApp extends LitePalApplication {
    private static BaseApp instance;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = this;
        new AbstractCrashReportHandler(this);
        RetrofitUtils.init();
//        startService(new Intent(this,  GetNumAlarmService.class));
//        startService(new Intent(this, SupevisorService.class));//
    }
    public static Context getApplicationCotext() {
        return instance.getApplicationContext();

    }
    public static Context getContext() {
        return mContext;
    }
}
