package com.rabbit.fans.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.wechat.AddByLinkMan;
import com.kidney_hospital.base.util.wechat.SupportUtil;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.util.LoadResultUtil;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class AutoService extends AccessibilityService implements KeyValue {
    private static final String TAG = "AutoService";
    private Context mContext;
    private AccessibilityService mService;
    private SupportUtil supportUtil;
    private int greetedNum = 0;//记录已打招呼的人数
    private int page = 1;//记录附近的人列表页码,初始页码为1
    private int prepos = -1;//记录页面跳转来源，0--从附近的人页面跳转到详细资料页，1--从打招呼页面跳转到详细资料页
    private String hello = " 你好";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mContext = this;
        mService = this;
        String version = getVersion(mContext);
        Log.d(TAG, "微信版本 : " + version);
        supportUtil = new SupportUtil(version);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            String atyName = event.getClassName().toString();
            Log.e(TAG,     "activity  ------------ " + atyName);
            handleImportNum(atyName);

//            handleNearby(atyName);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleNearby(String atyName) {
        boolean isNearbyOpen = (boolean) SPUtil.get(mContext, IS_NEARBY_OPEN, false);
        Log.e(TAG, "handleNearby: " + isNearbyOpen);
        if (isNearbyOpen) {
            if (atyName.equals(supportUtil.getLauncherUI())) {
                //记录打招呼人数置零
                greetedNum = 0;
                //当前在微信聊天页就点开发现
                openNext("发现");
                //然后跳转到附近的人
                openDelay(1000, "附近的人");
            } else if (atyName.equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendShowSayHiUI")) {
                openDelay(1000, "查看附近的人");
            } else if (atyName.equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI")) {
                Log.e(TAG, "greetedNum: " + greetedNum);
                prepos = 0;
                //当前在附近的人界面就点选人打招呼
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("米以内");
                Log.d("name", "附近的人列表人数: " + list.size());
                if (greetedNum < (list.size() * page)) {
                    list.get(greetedNum % list.size()).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(greetedNum % list.size()).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else if (greetedNum == list.size() * page) {
                    //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
                    for (int i = 0; i < nodeInfo.getChild(0).getChildCount(); i++) {
                        if (nodeInfo.getChild(0).getChild(i).getClassName().equals("android.widget.ListView")) {
                            AccessibilityNodeInfo node_lsv = nodeInfo.getChild(0).getChild(i);
                            node_lsv.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            page++;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException mE) {
                                        mE.printStackTrace();
                                    }
                                    AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                                    List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByText("米以内");
                                    Log.d("name", "列表人数: " + list_.size());
                                    //滑动之后，上一页的最后一个item为当前的第一个item，所以从第二个开始打招呼
                                    list_.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    list_.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }).start();
                        }
                    }
                }
            } else if (atyName.equals(supportUtil.getContactInfoUI())) {
                if (prepos == 1) {
                    //从打招呼界面跳转来的，则点击返回到附近的人页面
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    greetedNum++;
                } else if (prepos == 0) {
                    //从附近的人跳转来的，则点击打招呼按钮
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.d(TAG, "rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("打招呼");
                    if (list.size() > 0) {
                        list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        //如果遇到已加为好友的则界面的“打招呼”变为“发消息"，所以直接返回上一个界面并记录打招呼人数+1
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        greetedNum++;
                    }
                }
            } else if (atyName.equals("com.tencent.mm.ui.contact.SayHiEditUI")) {
                //当前在打招呼页面
                prepos = 1;
                //输入打招呼的内容并发送
                inputHello(hello);
                openDelay(2000, "发送");
//                openNext("发送");
            }
        }
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
            LogTool.d("辅助功能好友数-->>"+AddByLinkMan.getInstence().jumpRemarkNum);
            if (LoadResultUtil.onLoadListener!=null){
                LoadResultUtil.onLoadListener.addedNum(AddByLinkMan.getInstence().jumpRemarkNum);
            }

            new Thread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    AddByLinkMan addLinkMan = AddByLinkMan.getInstence();
                    addLinkMan.startAdd(supportUtil, mService, atyName);

                }
            }).start();
        } else {
//            AddByLinkMan.getInstence().isJumpLauncherUI = false;
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

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNext(String str) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.d(TAG, "rootWindow为空");
            Toast.makeText(this, "rootWindow为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list != null && list.size() > 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            Toast.makeText(this, "找不到有效的节点", Toast.LENGTH_SHORT).show();
        }
    }

    //延迟打开界面
    private void openDelay(final int delaytime, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException mE) {
                    mE.printStackTrace();
                }
                try {
                    openNext(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //自动输入打招呼内容
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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


}
