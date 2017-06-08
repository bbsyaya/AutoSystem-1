package com.shuangyou.material;


import android.content.Intent;
import android.util.Log;

import com.kidney_hospital.base.BaseApp;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.activity.LoginActivity;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.RollbackCallBack;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;

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
        initTinker();
    }

    private void initTinker() {
        if (BuildConfig.TINKER_ENABLE) {
            // 我们可以从这里获得Tinker加载过程的信息
            ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
            // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
            TinkerPatch.init(tinkerApplicationLike)
                    .reflectPatchLibrary()
                    .setPatchRollbackOnScreenOff(true)
                    .setPatchRestartOnSrceenOff(true)
                    .setFetchPatchIntervalByHours(2)
                    //设置访问后台动态配置的时间间隔,默认为3个小时
                    .setFetchDynamicConfigIntervalByHours(2)
                    //我们可以通过ResultCallBack设置对合成后的回调
                    //例如弹框什么
                    //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
                    .setPatchResultCallback(new ResultCallBack() {
                        @Override
                        public void onPatchResult(PatchResult patchResult) {
                            Log.e("MyApp37", patchResult.toString());
                            LogTool.d("update55---->"+patchResult.toString());
                            //杀死软件,重新打开
                            Intent intent = new Intent(BaseApp.getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前


                        }
                    })
                    //设置收到后台回退要求时,锁屏清除补丁
                    //默认是等主进程重启时自动清除
                    .setPatchRollbackOnScreenOff(true)
                    //我们可以通过RollbackCallBack设置对回退时的回调
                    .setPatchRollBackCallback(new RollbackCallBack() {
                        @Override
                        public void onPatchRollback() {
                            Log.e("MyApp", "onPatchRollback callback here");
                        }
                    });
            // 获取当前的补丁版本
            Log.e("MyApp", "Current patch version is " + TinkerPatch.with().getPatchVersion());
            // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }

    }


}
