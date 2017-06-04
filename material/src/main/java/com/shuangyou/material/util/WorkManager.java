package com.shuangyou.material.util;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.kidney_hospital.base.BaseApp;
import com.shuangyou.material.service.CloudService;


/**
 * Created by Vampire on 2017/5/25.
 */

public class WorkManager {
    private static final String TAG = "WorkManager";
    private static WorkManager instance;
    public static WorkManager getInstance() {
        if (instance == null) {
            synchronized (WorkManager.class) {
                if (instance == null) {
                    instance = new WorkManager();
                }
            }
        }
        return instance;
    }



    /**
     * 检测辅助功能是否开启<br>
     * 方 法 名：isAccessibilitySettingsOn <br>
     * 创 建 人 <br>
     * 创建时间：2016-6-22 下午2:29:24 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     *
     * @return boolean
     */
    public boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = BaseApp.getApplicationCotext().getPackageName() + "/" + CloudService.class.getCanonicalName();
        Log.i(TAG, "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(BaseApp.getApplicationCotext().getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(BaseApp.getApplicationCotext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            turnToService();
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
    private void turnToService() {
//        Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        BaseApp.getContext().getApplicationContext().startActivity(service);
    }
}
