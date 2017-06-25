package com.rabbit.fans.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.interfaces.OnReceiveTimeListener;
import com.rabbit.fans.util.LoadResultUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/6/6.
 */

public class JpushReceiver extends BroadcastReceiver implements KeyValue {
    private static final String TAG = "JpushReceiver";
    private Context mContext;
    public static OnReceiveTimeListener onReceiveTimeListener;

    public static void setOnReceiveTimeListener(OnReceiveTimeListener onReceiveTimeListener) {
        JpushReceiver.onReceiveTimeListener = onReceiveTimeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);
            LogTool.d("onReceive: extra-fans----" + extras);
//            try {
//                AppManger.getInstance().isAddActivity(MainActivity.class);
//            } catch (Exception e) {
//                Intent in = new Intent(mContext, MainActivity.class);
//                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(in);
//                e.printStackTrace();
//                return;
//            }
            Log.e(TAG, "onReceive: 58");

            try {
                JSONObject object = new JSONObject(extras);
                String sendImportPhoneId = object.getString("sendImportPhoneId");
                String frequency = object.getString("frequency");
                if (frequency.equals("0")) {
                    Toast.makeText(mContext, "账号在其他设备登录!", Toast.LENGTH_SHORT).show();
                    SPUtil.putAndApply(mContext, IS_LOGIN, false);
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onUpdate("");
                    }

                    return;
                }
                String type = object.getString("type");//现在只考虑类型为1的情况
                String sp_sendImportPhoneId = (String) SPUtil.get(mContext, SEND_IMPORT_PHONE_ID, "");
                if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onSuccess("-2", "-1");
                    }
                    //素材重了  也有可能第二次推送把第一次推送失败的激活了
                    Toast.makeText(mContext, "已经收到了第一次推送!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (judgeNull(sendImportPhoneId, frequency, type)) {
                    return;
                }
                SPUtil.putAndApply(mContext, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
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
            if (!connected) {
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("网络断开连接!");
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
