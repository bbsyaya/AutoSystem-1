package com.rabbit.fans;


import android.content.Intent;

import com.kidney_hospital.base.BaseApp;
import com.kidney_hospital.base.util.exceptioncatch.AbstractCrashReportHandler;
import com.rabbit.fans.service.GetFansService;

/**
 * Created by Vampire on 2017/5/29.
 */

public class MyApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();

//        startService(new Intent(this,  GetFansService.class));
    }
}
