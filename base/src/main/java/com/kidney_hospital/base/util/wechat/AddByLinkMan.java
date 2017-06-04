package com.kidney_hospital.base.util.wechat;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * Created by Vampire on 2017/4/28.
 */

public class AddByLinkMan {
    private static final String TAG = "AddByLinkMan";
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
            Log.e(TAG, "startAdd: 已经到这里了40" );
            launcherInfo(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getFMesssageConversationUI())
                ||atyName.equals("android.widget.ListView")){
            Log.e(TAG, "startAdd: 已经到这里了43" );
            addFriend(supportUtil, mService);
        }else if (atyName.equals(supportUtil.getContactInfoUI())
                ||atyName.equals(supportUtil.getFMesssageConversationUI())){
            //返回
            PerformClickUtils.performBack(mService);
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addFriend(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.e(TAG, "111addFriend: have" );
            return;
        }
        List<AccessibilityNodeInfo> addBtnList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(supportUtil.getFMesssageConversationUI_ADD_BTN_ID());
        Log.e(TAG, "addFriend: "+addBtnList.size());
        if (!addBtnList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : addBtnList) {
                if (nodeInfo != null && nodeInfo.getText().toString().equals("添加")) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleep(2000);
                }
            }
        }
        PerformClickUtils.findTextAndClick(mService, "接受");
        sleep(2000);
        AccessibilityNodeInfo lv = PerformClickUtils.getNode(mService, supportUtil.getFMesssageConversationUI_LV_ID());
        if (lv == null)
            return;
        Log.e(TAG, "addFriend: have" );
        lv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        sleep(500);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void launcherInfo(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo page = PerformClickUtils.getNode(mService, supportUtil.getLauncherPagerId());
        sleep(200);
        //首页前滚一页
        page.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        sleep(600);
        PerformClickUtils.findViewIdAndClick(mService, supportUtil.getLauncherNewFriendId());
    }

}
