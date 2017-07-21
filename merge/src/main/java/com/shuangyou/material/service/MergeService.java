package com.shuangyou.material.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.thread.ThreadPool;
import com.kidney_hospital.base.util.wechat.AddByLinkMan;
import com.kidney_hospital.base.util.wechat.PerformClickUtils;
import com.kidney_hospital.base.util.wechat.SupportUtil;
import com.shuangyou.material.receiver.JpushReceiver;
import com.shuangyou.material.util.DaysShare;
import com.shuangyou.material.util.LoadResultUtil;

import java.util.List;

/**
 * Created by Vampire on 2017/7/12.
 */

public class MergeService extends AccessibilityService {
    private static final String TAG = "MergeService";
    private Context mContext;
    private AccessibilityService mService;
    private SupportUtil supportUtil;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mContext = this;
        mService = this;
        String version = getVersion(mContext);
        Log.e(TAG, "微信版本 : " + version);
        LogTool.d("微信版本" + version);
        supportUtil = new SupportUtil(version);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            String atyName = event.getClassName().toString();
            Log.e(TAG, "activity  ------------ " + atyName);
            handleImportNum(atyName);
            if (supportUtil.getSnsUploadUi().equals(atyName)) {//后期要做好适配(已做好四个版本的)
                LogTool.d("material-- 辅助功能开了");
                Log.e(TAG, "onAccessibilityEvent: 我来了");
                String etContent = PerformClickUtils.getText(mService, supportUtil.getEtContentId());
                Log.e(TAG, "onAccessibilityEvent52: " + etContent);
                try {
                    if (etContent.equals("这一刻的想法...")) {
                        //TODO 下面这行代码有些板子崩
                        try {
                            String content  = WriteFileUtil.readeTXT(SavePath.SAVE_HTTP_CONTENT);
//                            PerformClickUtils.setText(mService, supportUtil.getEtContentId(), JpushReceiver.sContent);
                            inputHello(content);
                        } catch (Exception e) {
                            LogTool.d("错了--->>" + e.toString());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                DaysShare.getInstence().share(supportUtil, mService, atyName);


            }

        } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (AddByLinkMan.getInstence().jumpRemarkNum < 11) {
                try {
                    String toastMsg = (String) event.getText().get(0);
                    Log.e(TAG, "toastMsg: " + toastMsg);
                    if (toastMsg.contains("黑名单")) {
                        LogTool.d("有黑名单");

                    }
                } catch (Exception e) {
                    LogTool.d("有黑名单崩了" + e.toString());
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 处理自动导号的辅助功能
     */
    private void handleImportNum(final String atyName) {
        try {
            Log.e(TAG, "onAccessibilityEvent: " + AddByLinkMan.jumpRemarkNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (AddByLinkMan.getInstence().jumpRemarkNum < 11) {
            LogTool.d("辅助功能好友数-->>" + AddByLinkMan.getInstence().jumpRemarkNum);
            if (LoadResultUtil.onLoadListener != null) {
                LoadResultUtil.onLoadListener.addedNum(AddByLinkMan.getInstence().jumpRemarkNum);
            }
            ThreadPool.thredP.execute(new Runnable() {
                @Override
                public void run() {
                    AddByLinkMan addLinkMan = AddByLinkMan.getInstence();
                    try {
                        addLinkMan.startAdd(supportUtil, mService, atyName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
//            AddByLinkMan.getInstence().isJumpLauncherUI = false;
        }
    }

    //自动输入打招呼内容
    private void inputHello(String hello) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        //找到当前获取焦点的view
        AccessibilityNodeInfo target = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (target == null) {
            Log.d(TAG, "inputHello: null");
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", hello);
        clipboard.setPrimaryClip(clip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
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
