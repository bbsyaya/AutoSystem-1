package com.rabbit.fans.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.kidney_hospital.base.util.wechat.AddByLinkMan;
import com.kidney_hospital.base.util.wechat.PerformClickUtils;
import com.kidney_hospital.base.util.wechat.SupportUtil;
import com.rabbit.fans.interfaces.KeyValue;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class AutoService extends AccessibilityService implements KeyValue {
    private static final String TAG = "AutoService";
    private Context mContext;
    private AccessibilityService mService;
    private SupportUtil supportUtil;


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mContext = this;
        mService = this;
        String version = getVersion(mContext);
        Log.d(TAG, "微信版本 : " + version);
        supportUtil = new SupportUtil(version);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            final String atyName = event.getClassName().toString();
            Log.e(TAG, "activity  ------------ " + atyName);
            Log.e(TAG, "onAccessibilityEvent: "+AddByLinkMan.jumpRemarkNum );
            if (AddByLinkMan.jumpRemarkNum < 11) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PerformClickUtils.sleep(1500);
                        AddByLinkMan addLinkMan = AddByLinkMan.getInstence();
                        addLinkMan.startAdd(supportUtil, mService, atyName);
                    }
                }).start();
            }else{
                AddByLinkMan.isJumpLauncherUI = false;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 获取微信的版本号
     *
     * @param context
     * @return
     */
    public String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packageInfoList) {
            if ("com.tencent.mm".equals(packageInfo.packageName)) {
                return packageInfo.versionName;
            }
        }
        return "6.3.25";
    }
}
