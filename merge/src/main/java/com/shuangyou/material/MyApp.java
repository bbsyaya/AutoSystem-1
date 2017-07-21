package com.shuangyou.material;

import com.kidney_hospital.base.BaseApp;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/7/12.
 */

public class MyApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
    }
}
