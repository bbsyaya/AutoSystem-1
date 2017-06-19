package com.shuangyou.response.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.wechat.PerformClickUtils;
import com.kidney_hospital.base.util.wechat.SupportUtil;
import com.shuangyou.response.activity.MainActivity;
import com.shuangyou.response.utils.ResponseCallBackUtil;

import java.util.List;


/**
 * Created by Vampire on 2017/6/11.
 */

public class ResponseService extends AccessibilityService {
    private static final String TAG = "ResponseService";
    private Context mContext;
    private AccessibilityService mService;
    private SupportUtil supportUtil;
    private boolean isMessageSend = false;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String atyName = event.getClassName().toString();
        Log.e(TAG, "atyName: " + atyName);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();//聊天内容
                        Log.e(TAG, "onAccessibilityEvent: " + content);
                        if (ResponseCallBackUtil.onCallBackListener != null) {
                            ResponseCallBackUtil.onCallBackListener.onCallBack(content);
                        }


                        if (event.getPackageName().equals("com.tencent.mm")) {
                            // 监听到微信红包的notification，打开通知
                            if (event.getParcelableData() != null
                                    && event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event
                                        .getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    pendingIntent.send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                PerformClickUtils.setText(mService, "com.tencent.mm:id/a49", MainActivity.sReplyContent);
                PerformClickUtils.findViewIdAndClick(mService, "com.tencent.mm:id/a4e");



                findChatContent();


                findRedPoint();

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:


                Log.e(TAG, "onAccessibilityEvent:changeaksjdk ");
                findChatContent();


                break;

        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private String findChatContent() {
        String content = "";
        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.e(TAG, "92: have");
            return "";
        }
        List<AccessibilityNodeInfo> chatItemList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/im");
        if (!chatItemList.isEmpty()) {
//                    for (AccessibilityNodeInfo nodeInfo : chatItemList) {
//                        String content = nodeInfo.getText().toString();
//                        Log.e(TAG, "onAccessibilityEvent: " + content);
//                    }

            content = chatItemList.get(chatItemList.size() - 1).getText().toString();
            Log.e(TAG, "content102: " + content);
        }
        return content;
    }

    /**
     * 寻找小红点
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void findRedPoint() {
        Log.e(TAG, "findRedPoint: have");

        AccessibilityNodeInfo rootInActiveWindow = mService.getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            Log.e(TAG, "111addFriend: have");
            return;
        }
        List<AccessibilityNodeInfo> redPointList = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ia");
        Log.e(TAG, "findRedPoint: " + redPointList.size());


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
