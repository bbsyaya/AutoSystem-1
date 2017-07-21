package com.rabbit.fans.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.util.AppManger;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.JumpToWeChatUtil;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.rabbit.fans.activity.LoginActivity;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.interfaces.OnReceiveTimeListener;
import com.rabbit.fans.util.LoadResultUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/6/6.
 */

public class JpushReceiver extends BroadcastReceiver implements KeyValue {
    private static final String TAG = "JpushReceiver";
    private Context mContext;
    private String overTime = "";
    public static OnReceiveTimeListener onReceiveTimeListener;

    public static void setOnReceiveTimeListener(OnReceiveTimeListener onReceiveTimeListener) {
        JpushReceiver.onReceiveTimeListener = onReceiveTimeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
//        Log.e(TAG, "onReceive: "+ AppManger.getInstance().isOpenActivity());
        //TODO 提交的时候需要更改
//        try {
//            JumpToWeChatUtil.jumpToFansMainActivity();
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogTool.d("fans---Jpush52>>"+e.toString());
//            Log.e(TAG, "eeeeee57: "+e.toString() );
//        }


        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);
            LogTool.d("onReceive: extra-fans----" + extras);
            Log.e(TAG, "onReceive: 58");
            boolean isLogin = (boolean) SPUtil.get(mContext, IS_LOGIN, true);
            if (!isLogin) {
                return;
            }


            try {
                JSONObject object = new JSONObject(extras);
                String sendTime = object.getString("sendTime");
                long pushTime = DateUtils.stringToStamp(sendTime);
                long currentTime = System.currentTimeMillis();
                Log.e(TAG, "pushTime: " + pushTime);
                Log.e(TAG, "currentTime: " + currentTime);


                String sendImportPhoneId = object.getString("sendImportPhoneId");
                String frequency = object.getString("frequency");
                try {
                    overTime = object.getString("overTime");
                } catch (JSONException e) {
                    overTime = "30";
                    e.printStackTrace();
                }
                if (frequency.equals("0")) {
                    Toast.makeText(mContext, "账号在其他设备登录!", Toast.LENGTH_SHORT).show();
                    SPUtil.putAndApply(mContext, IS_LOGIN, false);
//                    if (LoadResultUtil.onLoadListener != null) {
//                        LoadResultUtil.onLoadListener.onUpdate("");
//                    }

                    Intent i = new Intent(mContext, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    Process.killProcess(Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
                    return;
                }

                if (!AppManger.getInstance().isOpenActivity()) {
                    try {
                        JumpToWeChatUtil.jumpToFansMainActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogTool.d("fans---Jpush52>>" + e.toString());
                        Log.e(TAG, "eeeeee57: " + e.toString());
                    }
                    if (!AppManger.getInstance().isOpenActivity()) {
                        Log.e(TAG, "onReceive: 没打开着 app");
                        LogTool.d("JpushReceiver74--->>>fans-没打开着 app");
                        SPUtil.putAndApply(mContext, IS_ON, true);
                    }
                }


                String type = object.getString("type");//现在只考虑类型为1的情况
                String sp_sendImportPhoneId = (String) SPUtil.get(mContext, SEND_IMPORT_PHONE_ID, "");
                if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onSuccess("-2", "-1");
                    }
//                    try {//TODO  ????
//                        JumpToWeChatUtil.jumpToLauncherUi();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    //素材重了  也有可能第二次推送把第一次推送失败的激活了
                    LogTool.d("素材重了 也有可能第二次推送把第一次推送失败的激活了");
                    Toast.makeText(mContext, "已经收到了第一次推送!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (judgeNull(sendImportPhoneId, frequency, type)) {
                    return;
                }
                SPUtil.putAndApply(mContext, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
                if (currentTime - pushTime > Integer.parseInt(overTime) * 60 * 1000) {
                    LogTool.d("fans推送信息超时!");
                    Toast.makeText(mContext, "推送信息超时!", Toast.LENGTH_SHORT).show();
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onSuccess("-3", "-1");
                    }
                    return;
                }
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("类型:通讯录导入号码" + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
                }
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onSuccess(type, frequency);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "推送到的是通知");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected:" + connected);
            LogTool.d("fans网络-->>" + connected);
            if (!connected) {
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("网络断开连接!");
                }
            } else {
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("网络又连上了!");
                }
            }

        }
    }

    private boolean judgeNull(String sendImportPhoneId, String frequency, String type) {
        if (TextUtils.isNull(sendImportPhoneId)) {
            if (LoadResultUtil.onLoadListener != null) {
                LoadResultUtil.onLoadListener.onFailuer("sendImportPhoneId为空");
            }
            return true;
        }
        if (TextUtils.isNull(frequency)) {
            if (LoadResultUtil.onLoadListener != null) {
                LoadResultUtil.onLoadListener.onFailuer("frequency为空");
            }
            return true;
        }
        if (TextUtils.isNull(type)) {
            if (LoadResultUtil.onLoadListener != null) {
                LoadResultUtil.onLoadListener.onFailuer("type为空");
            }
            return true;
        }
        return false;
    }
}
