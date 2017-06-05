package com.shuangyou.material;


import android.util.Log;

import com.kidney_hospital.base.BaseApp;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.wechat.LoadResultUtil;
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
                    .setFetchPatchIntervalByHours(3)
                    //设置访问后台动态配置的时间间隔,默认为3个小时
                    .setFetchDynamicConfigIntervalByHours(3)
                    //我们可以通过ResultCallBack设置对合成后的回调
                    //例如弹框什么
                    //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
                    .setPatchResultCallback(new ResultCallBack() {
                        @Override
                        public void onPatchResult(PatchResult patchResult) {
//                            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
                            Log.e("MyApp37", patchResult.toString());
                            LogTool.d("update55---->"+patchResult.toString());
                            if (LoadResultUtil.onLoadListener!=null){
                                LoadResultUtil.onLoadListener.onUpdate(patchResult.toString());
                            }
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
