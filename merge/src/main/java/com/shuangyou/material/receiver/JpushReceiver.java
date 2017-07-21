package com.shuangyou.material.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.AppManger;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.JumpToWeChatUtil;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.shuangyou.material.activity.LoginActivity;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.LoadResultUtil;
import com.shuangyou.material.util.ShareUtils;
import com.shuangyou.material.util.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.shuangyou.material.util.LoadResultUtil.onLoadListener;


/**
 * Created by Vampire on 2017/7/12.
 */

public class JpushReceiver extends BroadcastReceiver implements KeyValue {
    private static final String TAG = "JpushReceiver";
    private Context mContext;

    public List<File> filePictures = new ArrayList<>();
//    public static String sContent = "";
    //    private String frequency = "";
    public static String sFrequency = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "onReceive: 有推送");




        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {


        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogTool.d("onReceive: extra-material47----" + extras);
            boolean isLogin = (boolean) SPUtil.get(mContext, IS_LOGIN, true);
            if (!isLogin) {
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    WorkManager.getInstance().setAccessibilitySettingsOn();
                }
            }).start();

            if (!AppManger.getInstance().isOpenActivity()) {

                try {
                    JumpToWeChatUtil.jumpToMaterialMainActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogTool.d("material---Jpush52>>" + e.toString());
                    Log.e(TAG, "eeeeee57: " + e.toString());
                }
                Log.e(TAG, "onReceive: 没打开着 app");
                LogTool.m("app未运行!", -1);
            }
            try {
                JSONObject object = new JSONObject(extras);
                String kind = object.getString("kind");
                if (kind.equals("0")) {//朋友圈
                    if (handleMaterial(object)) return;

                } else if (kind.equals("1")) {//导号
                    LogTool.m("收到导号的推送", 1);
                    String overTime = "";
                    String sendTime = object.getString("sendTime");
                    long pushTime = DateUtils.stringToStamp(sendTime);
                    long currentTime = System.currentTimeMillis();
                    String sendImportPhoneId = object.getString("sendImportPhoneId");
                    String frequency = object.getString("frequency");
                    try {
                        overTime = object.getString("overTime");
                    } catch (JSONException e) {
                        overTime = "30";
                        e.printStackTrace();
                    }
//                    String type = object.getString("type");//现在只考虑类型为1的情况
                    String sp_sendImportPhoneId = (String) SPUtil.get(mContext, SEND_IMPORT_PHONE_ID, "");
                    if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                        if (LoadResultUtil.onLoadListener != null) {
                            LoadResultUtil.onLoadListener.onFansResult("收到第二次推送", LOG_FLAG_SUCCESS_ONCE);
                        }
                        try {//TODO ???
                            JumpToWeChatUtil.jumpToLauncherUi();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //素材重了  也有可能第二次推送把第一次推送失败的激活了
                        Toast.makeText(mContext, "已经收到了第一次推送!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SPUtil.putAndApply(mContext, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
                    if (currentTime - pushTime > Integer.parseInt(overTime)*60*1000) {
                        LogTool.d("fans推送信息超时!");
                        Toast.makeText(mContext, "推送信息超时!", Toast.LENGTH_SHORT).show();
                        if (LoadResultUtil.onLoadListener != null) {
                            onLoadListener.onFansResult("推送信息超时", LOG_FLAG_JAM);
                        }
                        return;
                    }
                    if (LoadResultUtil.onLoadListener != null) {
                        onLoadListener.onFansResult("getjpush", frequency);
                    }


                }


            } catch (Exception e) {
                LogTool.d("merge极光推送的异常-->>" + e.toString());
                e.printStackTrace();
            }

        }else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected:" + connected);
            LogTool.d("material网络-->>"+connected);
            if (!connected) {
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onNetChanged(false);
                }
            }else{
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onNetChanged(true);
                }
            }

        }

    }

    private boolean handleMaterial(JSONObject object) throws JSONException {
        LogTool.m("收到朋友圈的推送", 0);
        String overTime = "";
        String sendTime = object.getString("sendTime");
        long pushTime = DateUtils.stringToStamp(sendTime);
        long currentTime = System.currentTimeMillis();
        String title = object.getString("title");
        String content = object.getString("content");
        String url = object.getString("url");
        String type = object.getString("typeP");
        String picUrl = object.getString("picUrl");
        String sendCompanyContentId = object.getString("sendCompanyContentId");
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
//            if (onLoadListener != null) {
//                onLoadListener.onReplace(//TODO );
//            }
            LogTool.m("账号在其他设备登录", -1);
            Intent i= new Intent(mContext, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前

            return true;
        }
        sFrequency = frequency;
        String sp_sendCompanyContentId = (String) SPUtil.get(mContext, SEND_COMPANY_CONTENT_ID, "");
        if (sendCompanyContentId.equals(sp_sendCompanyContentId)) {
            //素材重了  也有可能第二次推送把第一次推送失败的激活了
            Toast.makeText(mContext, "同一素材不可转发两次!", Toast.LENGTH_SHORT).show();

            if (onLoadListener != null) {
                onLoadListener.onMaterialResult("收到第二次推送!", LOG_FLAG_SUCCESS_ONCE);
            }
            return true;
        }
        SPUtil.putAndApply(mContext, SEND_COMPANY_CONTENT_ID, sendCompanyContentId);
        if (currentTime - pushTime > Integer.parseInt(overTime) * 60 * 1000) {
            LogTool.d("material推送信息超时!");
            Toast.makeText(mContext, "推送信息超时!", Toast.LENGTH_SHORT).show();
            if (onLoadListener != null) {
                onLoadListener.onMaterialResult("推送信息超时", LOG_FLAG_JAM);
            }
            return true;
        }
        if (onLoadListener != null) {
            String mType = "";
            if (type.equals("1")) {
                mType = "图文";
            } else if (type.equals("2")) {
                mType = "文章";
            } else if (type.equals("3")) {
                mType = "视频";
            } else if (type.equals("4")) {
                mType = "电台";
            }
            onLoadListener.onMaterialResult("收到推送,类型:" + mType, "-1");
        }
        if (type.equals("1")) {//转发图文的
            Log.e(TAG, "loadData: 转发图文的到了");
            LogTool.d("loadData: 转发图文的到了");
            sendForPhotoText(content, picUrl);
        } else {//转发连接的
            Log.e(TAG, "loadData: 转发链接的到了");
            LogTool.d("loadData: 转发链接的到了");
            if (picUrl.indexOf(",") > 0) {
                String[] pictures = picUrl.split(",");
                picUrl = pictures[0];
            }
//            sContent = content;
            WriteFileUtil.wrieFileUserIdByBufferedWriter(content,SavePath.SAVE_HTTP_CONTENT);
            ShareUtils.sendToFriends(mContext,
                    url,
                    title,
                    title,
                    picUrl);
        }
        return false;
    }

    private void sendForPhotoText(final String content, final String picUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                if (TextUtils.isNull(picUrl)) {
                    LogTool.d("素材图片为空");
                    if (onLoadListener != null) {
                        onLoadListener.onMaterialResult("素材图片是空的!", "-1");
                    }
                    return;
                }
                String[] pictures = picUrl.split(",");
                for (String picture : pictures) {
                    byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                    Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    FileUtils.saveFile(mContext, System.currentTimeMillis() + ".png", bmp);
                }
                LogTool.d("获取的 content110:" + content);
                File folder = new File(SavePath.SAVE_PIC_PATH);
                addToList(folder);
                boolean isSend = ShareUtils.shareMultipleToMoments(mContext, content, filePictures);
                if (isSend) {
                    if (onLoadListener != null) {
                        if (sFrequency.equals("2")) {
                            onLoadListener.onMaterialResult("接收推送成功,待转发2", LOG_FLAG_SUCCESS_TWICE);
                        } else {
                            onLoadListener.onMaterialResult("接收推送成功,待转发1", LOG_FLAG_SUCCESS_ONCE);
                        }
                    }
                }

            }
        }).start();
    }

    private void addToList(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    filePictures.add(f);
                }
            }
        }
    }

}
