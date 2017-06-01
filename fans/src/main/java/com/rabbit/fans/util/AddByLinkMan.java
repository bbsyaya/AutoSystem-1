package com.rabbit.fans.util;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kidney_hospital.base.util.wechat.PerformClickUtils;
import com.kidney_hospital.base.util.wechat.SupportUtil;

import java.util.List;


/**
 * Created by Vampire on 2017/4/28.
 */

public class AddByLinkMan {
    private static AddByLinkMan instence;


    private AddByLinkMan() {
    }

    public static AddByLinkMan getInstence() {
        if (instence == null) {
            synchronized (AddByLinkMan.class) {
                if (instence == null) {
                    instence = new AddByLinkMan();
                }
            }
        }
        return instence;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startAdd(SupportUtil supportUtil, AccessibilityService mService, String atyName) {
        if (atyName.equals(supportUtil.getLauncherUI())) {
            launcherInfo(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getFMesssageConversationUI())) {
            addFriend(supportUtil, mService);
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addFriend(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return;
        }
        List<AccessibilityNodeInfo> addBtnList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(supportUtil.getFMesssageConversationUI_ADD_BTN_ID());
        if (!addBtnList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : addBtnList) {
                if (nodeInfo != null && nodeInfo.getText().toString().equals("添加")) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleep(1000);
                }
            }
        }
        PerformClickUtils.findTextAndClick(mService, "接受");
        sleep(1000);
        AccessibilityNodeInfo lv = PerformClickUtils.getNode(mService, supportUtil.getFMesssageConversationUI_LV_ID());
        if (lv == null)
            return;
        lv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        sleep(500);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void launcherInfo(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo page = PerformClickUtils.getNode(mService, supportUtil.getLauncherPagerId());
        //首页前滚一页
        page.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        sleep(500);
        PerformClickUtils.findViewIdAndClick(mService, supportUtil.getLauncherNewFriendId());
    }
    public static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
