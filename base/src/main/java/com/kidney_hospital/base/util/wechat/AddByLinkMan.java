package com.kidney_hospital.base.util.wechat;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.findViewIdAndClick;
import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * Created by Vampire on 2017/4/28.
 */

public class AddByLinkMan {
    private static final String TAG = "AddByLinkMan";
    private static AddByLinkMan instence;
    private boolean flagRemarkSave = false;//判断是否备注保存了
    public static int jumpRemarkNum = 11;//TODO  记得改成11
    public static boolean isJumpLauncherUI = false;//判断是否跳转到了主页
    private boolean flagScroll = false;
    private boolean isPrivate = false;//显示出隐私对话框
    private int scrollFalseNum = 0;

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

//        if (!atyName.equals(supportUtil.getLauncherUI())) {
//            if (!isJumpLauncherUI) {
//                PerformClickUtils.performBack(mService);
//            }
//        }
//        if (jumpRemarkNum>=10){
//            isJumpLauncherUI = false;
//        }

        if (atyName.equals(supportUtil.getLauncherUI())) {
            isJumpLauncherUI = true;
            Log.e(TAG, "startAdd: 已经到这里了40");
            launcherInfo(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getFMesssageConversationUI())
                || atyName.equals("android.widget.ListView")) {
            Log.e(TAG, "startAdd: 已经到这里了43");
            addFriend(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getContactInfoUI())
                || atyName.equals(supportUtil.getFMesssageConversationUI())) {

            //返回
//            sleep(2000);
            if (flagRemarkSave) {
                PerformClickUtils.performBack(mService);
                flagRemarkSave = false;
            } else {
                boolean isHave = PerformClickUtils.findViewIdAndClick(mService, supportUtil.getTv_setRemark());
                if (!isHave) {
                    PerformClickUtils.performBack(mService);
                }
                flagRemarkSave = true;
            }


        } else if (atyName.equals(supportUtil.getContactRemarkInfoModUi())
                || atyName.equals(supportUtil.getModRemarkNameUI())) {

            //备注信息页面
//            sleep(2000);
            findViewIdAndClick(mService, supportUtil.getTv_fillIn());
//            sleep(2000);
            findViewIdAndClick(mService, supportUtil.getSendRequestBtnId());
            findViewIdAndClick(mService, supportUtil.getTv_remark_save());
            flagRemarkSave = true;
            Log.e(TAG, "startAdd: " + jumpRemarkNum);
            jumpRemarkNum++;


        } else if (atyName.equals(supportUtil.getPrivateDialog())) {
            Log.e(TAG, "startAdd: 隐私");
            isPrivate = true;
//            PerformClickUtils.findViewIdAndClick(mService, supportUtil.getPrivateDialogButtonOK());
            PerformClickUtils.performBack(mService);

        } else if (atyName.equals(supportUtil.getDeleteDialog())) {
            PerformClickUtils.findViewIdAndClick(mService, supportUtil.getTv_Delete());

        } else {
            if (atyName.equals(supportUtil.getProgressDialog())){
                return;
            }
            if (!isJumpLauncherUI) {
                PerformClickUtils.performBack(mService);
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addFriend(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.e(TAG, "111addFriend: have");
            return;
        }
        List<AccessibilityNodeInfo> addBtnList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(supportUtil.getFMesssageConversationUI_ADD_BTN_ID());
        Log.e(TAG, "addFriend: " + addBtnList.size());
        if (!addBtnList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : addBtnList) {

                if (nodeInfo != null && nodeInfo.getText().toString().equals("添加")) {
                    Log.e(TAG, "addFriend: 我到了");


                    if (isPrivate) {
                        try {
                            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                            isPrivate = false;
                            Log.e(TAG, "addFriend: 我到了隐私22222");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        try {
                            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Log.e(TAG, "addFriend: 我到了22222");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }


        PerformClickUtils.findTextAndClick(mService, "接受");

        AccessibilityNodeInfo lv = PerformClickUtils.getNode(mService, supportUtil.getFMesssageConversationUI_LV_ID());

        if (lv == null)
            return;
        Log.e(TAG, "addFriend: have");
//        sleep(2000);
        boolean isCanScroll = lv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        Log.e(TAG, "addFriend: " + isCanScroll);
        if (isCanScroll) {
            flagScroll = true;
        } else {
            if (flagScroll) {
                jumpRemarkNum = 11;
                flagScroll = false;
            }

            scrollFalseNum++;
            if (scrollFalseNum > 2) {
                jumpRemarkNum = 11;
                scrollFalseNum = 0;
            }

        }


//        sleep(500);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void launcherInfo(SupportUtil supportUtil, AccessibilityService mService) {
        try {
            AccessibilityNodeInfo page = PerformClickUtils.getNode(mService, supportUtil.getLauncherPagerId());
            sleep(200);
            //首页前滚一页
            page.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            sleep(600);
            findViewIdAndClick(mService, supportUtil.getLauncherNewFriendId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
