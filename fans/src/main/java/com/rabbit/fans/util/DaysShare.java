package com.rabbit.fans.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;

import com.kidney_hospital.base.util.wechat.PerformClickUtils;
import com.kidney_hospital.base.util.wechat.SupportUtil;


/**
 * Created by Vampire on 2017/5/20.
 */

public class DaysShare {
    private static final String TAG = "DaysShare";
    private static DaysShare instence = new DaysShare();

    public static DaysShare getInstence() {
        return instence;
    }

//    public static boolean isRun = false;//辅助功能是否要运行,以免与微信其他操作冲突

    public void share(SupportUtil supportUtil, AccessibilityService service, String className) {
//        LogTool.d("share21按钮的 id"+supportUtil.getSendRequestBtnId());
        boolean isClick = PerformClickUtils.findViewIdAndClick(service, supportUtil.getSendRequestBtnId());
        Log.e(TAG, "share: "+isClick );
        if (isClick) {
            if (LoadResultUtil.onLoadListener!=null){
                LoadResultUtil.onLoadListener.onMaterialSuccess();
            }
//            isRun = false;
        }


    }
}
