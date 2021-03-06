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
import com.kidney_hospital.base.constant.HttpApi;
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
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.LoadResultUtil;
import com.shuangyou.material.util.ShareUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.shuangyou.material.util.DownPIcUtils.buildTransaction;
import static com.shuangyou.material.util.LoadResultUtil.onLoadListener;

/**
 * Created by Vampire on 2017/5/31.
 */

public class JpushReceiver extends BroadcastReceiver implements KeyValue {
    private static final String TAG = "JpushReceiver";
    public static OnReceiveTimeListener onReceiveTimeListener;
    private Context mContext;
    private String overTime = "";
    public List<File> filePictures = new ArrayList<>();
//    public static String sContent = "";
    //    private String frequency = "";
    public static String sFrequency = "";

    public static void setOnReceiveTimeListener(OnReceiveTimeListener onReceiveTimeListener) {
        JpushReceiver.onReceiveTimeListener = onReceiveTimeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "onReceive: 有推送");
//        File file_wxId = new File(SavePath.SAVE_WX_CHAT_ID);
//        if (file_wxId.exists()) {
//            file_wxId.delete();
//        }
//        File file_wxPsw = new File(SavePath.SAVE_WX_CHAT_CONTENT);
//        if (file_wxPsw.exists()){
//            file_wxPsw.delete();
//        }
//        //wxid_b5k58sv0i7v622 为绚丽的id
//        //wxid_zj2sz9d34zuz22 为美人自醉的 id
//        WriteFileUtil.wrieFileUserIdByBufferedWriter("wxid_zj2sz9d34zuz22", SavePath.SAVE_WX_CHAT_ID);
//        WriteFileUtil.wrieFileUserIdByBufferedWriter("我是剧京测试的是\n考我虑到是看到楼上的拉开了打卡了打卡拉斯凯迪拉克打了卡来得快阿拉山口达拉斯看到啦"+Math.random()*1000,SavePath.SAVE_WX_CHAT_CONTENT);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {


        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message" + message);
            Log.e(TAG, "onReceive: extra" + extras);
            LogTool.d("onReceive: extra-material47----" + extras);
            boolean isLogin = (boolean) SPUtil.get(mContext, IS_LOGIN, true);
            if (!isLogin) {
                return;
            }





            try {
                JSONObject object = new JSONObject(extras);
                String sendTime = object.getString("sendTime");
                long pushTime = DateUtils.stringToStamp(sendTime);
                long currentTime = System.currentTimeMillis();
                String title = object.getString("title");
                String content = object.getString("content");
                String url = object.getString("url");
                String type = object.getString("type");
                String picUrl = object.getString("picUrl");
                String sendCompanyContentId = object.getString("sendCompanyContentId");
                String frequency = object.getString("frequency");
                try {
                    overTime = object.getString("overTime");
                } catch (JSONException e) {
                    overTime = "30";
                    e.printStackTrace();
                }
                try {
//                    DaysShare.getInstence().isRun = true;
//                    DaysShare.isRun = true;//确保这个值为true!!!
                } catch (Exception e) {
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
                    android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
                    return;
                }

                if (!AppManger.getInstance().isOpenActivity()){

                    try {
                        JumpToWeChatUtil.jumpToMaterialMainActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogTool.d("material---Jpush52>>"+e.toString());
                        Log.e(TAG, "eeeeee57: "+e.toString() );
                    }
                    Log.e(TAG, "onReceive: 没打开着 app" );
                    LogTool.d("JpushReceiver92--->>>material-没打开着 app");
                }


                sFrequency = frequency;
                String sp_sendCompanyContentId = (String) SPUtil.get(mContext, SEND_COMPANY_CONTENT_ID, "");
                if (sendCompanyContentId.equals(sp_sendCompanyContentId)) {
                    //素材重了  也有可能第二次推送把第一次推送失败的激活了
                    Toast.makeText(mContext, "同一素材不可转发两次!", Toast.LENGTH_SHORT).show();

                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onSuccess("收到第二次推送,但是第一次已经收到了!", LOG_FLAG_SUCCESS_ONCE);
                    }
                    return;
                }
                if (TextUtils.isNull(sendCompanyContentId)) {
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("sendCompanyContentId为空");
                    }
                    return;
                }
                if (TextUtils.isNull(frequency)) {
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("frequency为空");
                    }
                    return;
                }

                SPUtil.putAndApply(mContext, SEND_COMPANY_CONTENT_ID, sendCompanyContentId);

                if (TextUtils.isNull(title)) {
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("title为空");
                    }
                    return;
                }
                if (TextUtils.isNull(content)) {
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("content为空");
                    }
                    return;
                }
                if (!type.equals("1")) {
                    if (TextUtils.isNull(url)) {
                        if (onLoadListener != null) {
                            onLoadListener.onFailuer("url为空");
                        }
                        return;
                    }
                }
                if (TextUtils.isNull(picUrl)) {
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("picUrl为空");
                    }
                    return;
                }
                if (currentTime-pushTime>Integer.parseInt(overTime)*60*1000){
                    LogTool.d("material推送信息超时!");
                    Toast.makeText(mContext, "推送信息超时!", Toast.LENGTH_SHORT).show();
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onAccess("-3");
                    }
                    return;
                }
                if (onReceiveTimeListener != null) {
                    String mType = "";
                    if (type.equals("1")){
                        mType = "图文";
                    }else if (type.equals("2")){
                        mType = "文章";
                    }else if (type.equals("3")){
                        mType = "视频";
                    }else if (type.equals("4")){
                        mType  ="电台";
                    }
                    onReceiveTimeListener.onReceiveTime("类型:" + mType + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
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
//                    sContent = content;
                    WriteFileUtil.wrieFileUserIdByBufferedWriter(content,SavePath.SAVE_HTTP_CONTENT);
                    ShareUtils.sendToFriends(mContext,
                            url,
                            title,
                            title,
                            picUrl);
                }


//                boolean flag = WorkManager.getInstance().isAccessibilitySettingsOn();
//                Log.e(TAG, "onReceive: " + flag);
//                if (!flag) {
//                    LogTool.d("辅助功能未开启receiver59");
//                    Toast.makeText(mContext, "辅助功能未开启!", Toast.LENGTH_SHORT).show();
//                    if (onLoadListener != null) {
//                        onLoadListener.onFailuer("辅助功能未开启!");
//                    }
//                    return;
//                }

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
            LogTool.d("material网络-->>"+connected);
            if (!connected) {
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("网络断开连接!");
                }
            }else{
                if (onReceiveTimeListener != null) {
                    onReceiveTimeListener.onReceiveTime("网络又连上了!");
                }
            }

        }


    }

    private void sendForUrl(Context context, String content, String url, String picUrl) {
        byte[] b = DownPIcUtils.getHtmlByteArray(picUrl);

        IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = content;
        msg.description = content;
        msg.thumbData = b;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    private void sendForPhotoText(final String content, final String picUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                if (TextUtils.isNull(picUrl)) {
                    LogTool.d("素材图片为空");
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("素材图片是空的!");
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
//                            onLoadListener.onSuccess("接收推送成功,待转发2", LOG_FLAG_SUCCESS_TWICE);
                            onLoadListener.onSuccess("成功2", LOG_FLAG_SUCCESS_TWICE);
                        } else {
//                            onLoadListener.onSuccess("接收推送成功,待转发1", LOG_FLAG_SUCCESS_ONCE);
                            onLoadListener.onSuccess("成功1", LOG_FLAG_SUCCESS_ONCE);
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
