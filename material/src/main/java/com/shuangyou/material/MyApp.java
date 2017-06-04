package com.shuangyou.material;


import com.kidney_hospital.base.BaseApp;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/5/29.
 */

public class MyApp extends BaseApp {
    private static final String TAG ="MyApp" ;
    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this,  SendingService.class));
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);

    }


}
