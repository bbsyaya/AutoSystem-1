package com.kidney_hospital.base.util.wechat;

import android.accessibilityservice.AccessibilityService;


/**
 * Created by Vampire on 2017/5/20.
 */

public class DaysShare {
    private static final String TAG = "DaysShare";
    private static DaysShare instence = new DaysShare();

    public static DaysShare getInstence() {
        return instence;
    }

    public void share(SupportUtil supportUtil, AccessibilityService service, String className) {
//        LogTool.d("share21按钮的 id"+supportUtil.getSendRequestBtnId());
        PerformClickUtils.findViewIdAndClick(service, supportUtil.getSendRequestBtnId());

    }
}
