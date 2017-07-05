package com.kidney_hospital.base.util.wechat;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kidney_hospital.base.util.RandomUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;

import java.util.List;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.findViewIdAndClick;

/**
 * Created by Vampire on 2017/4/28.
 */

public class AddByLinkMan {
    private static final String TAG = "AddByLinkMan";
    private static AddByLinkMan instence;
    private boolean flagRemarkSave = false;//判断是否备注保存了
    public static int jumpRemarkNum = 11;//TODO  记得改成11
    //    public static boolean isJumpLauncherUI = false;//判断是否跳转到了主页
    private boolean flagScroll = false;
    public boolean isPrivate = false;//显示出隐私对话框
    private int scrollFalseNum = 0;
    //    public static boolean flagNewFriendsClick = false;
//    private boolean isHaveNewFriend = false;

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

    public void startAdd(SupportUtil supportUtil, AccessibilityService mService, String atyName) {


        if (atyName.equals(supportUtil.getLauncherUI())) {
//            isJumpLauncherUI = true;
            Log.e(TAG, "startAdd: 已经到这里了40");
//            if (flagNewFriendsClick) {
//                jumpRemarkNum = 100;
//                LogTool.d("jumpRemarkNum56--->>"+jumpRemarkNum);
//                flagNewFriendsClick = false;
//            }
            launcherInfo(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getFMesssageConversationUI())
                || atyName.equals("android.widget.ListView")) {
            PerformClickUtils.sleep(1000 * RandomUtil.randomNumber(2, 15));

            Log.e(TAG, "startAdd: 已经到这里了43");
            addFriend(supportUtil, mService);
        } else if (atyName.equals(supportUtil.getContactInfoUI())
                || atyName.equals(supportUtil.getFMesssageConversationUI())) {
            PerformClickUtils.sleep(1000 * RandomUtil.randomNumber(2, 5));
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
            PerformClickUtils.sleep(1000 * RandomUtil.randomNumber(2, 6));
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

        } else if (supportUtil.getSnsUploadUi().equals(atyName)) {// TODO 避免和转发朋友圈的冲突
            LogTool.d("fans导号与朋友圈辅助功能冲突!");
        } else {
            if (atyName.equals(supportUtil.getProgressDialog())) {
                return;
            }
//            if (!isJumpLauncherUI) {
            PerformClickUtils.performBack(mService);
//            }
        }


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void addFriend(SupportUtil supportUtil, AccessibilityService mService) {
        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.e(TAG, "111addFriend: have");
            return;
        }
        List<AccessibilityNodeInfo> addBtnList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(supportUtil.getFMesssageConversationUI_ADD_BTN_ID());
        Log.e(TAG, "addFriend: " + addBtnList.size());
        //TODO
        LogTool.d("微信当前页面识别的新好友数--->>"+addBtnList.size());
        if (!addBtnList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : addBtnList) {

                if (nodeInfo != null && nodeInfo.getText().toString().equals("添加")) {
                    Log.e(TAG, "addFriend: 我到了");
                    LogTool.d("添加按钮132!!");

                    if (isPrivate) {
                        try {
                            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                            LogTool.d("隐私----");
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
                            LogTool.d("普通点击149--->>>>");
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
//                jumpRemarkNum = 11;
                flagScroll = false;
            }

            scrollFalseNum++;
            if (scrollFalseNum > 2) {
//                jumpRemarkNum = 11;
                scrollFalseNum = 0;
            }

        }


//        sleep(500);
    }

    private void launcherInfo(SupportUtil supportUtil, AccessibilityService mService) {
        try {
            AccessibilityNodeInfo page = PerformClickUtils.getNode(mService, supportUtil.getLauncherPagerId());
            //首页前滚一页
//            page.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            openNext("通讯录", mService);


            boolean flagClick = findViewIdAndClick(mService, supportUtil.getLauncherNewFriendId());
            if (flagClick) {
//                flagNewFriendsClick = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNext(String str, AccessibilityService mService) {
        AccessibilityNodeInfo nodeInfo = mService.getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.d(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list != null && list.size() > 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
        }
    }

}
