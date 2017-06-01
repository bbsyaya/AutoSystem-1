package com.shuangyou.material.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.ShareUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/5/31.
 */

public class JpushReceiver extends BroadcastReceiver{
    private static final String TAG = "JpushReceiver";
    public static OnReceiveTimeListener onReceiveTimeListener;
    private Context mContext;
    public List<File> filePictures = new ArrayList<>();
    public static void setOnReceiveTimeListener(OnReceiveTimeListener onReceiveTimeListener) {
        JpushReceiver.onReceiveTimeListener = onReceiveTimeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "onReceive: 有推送" );
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {


        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message"+message );
            Log.e(TAG, "onReceive: extra"+extras );
            LogTool.d("onReceive: extra47----"+extras);


            try {
                JSONObject object = new JSONObject(extras);
                String title = object.getString("title");
                String content = object.getString("content");
                String url = object.getString("url");
                String type = object.getString("type");
                String picUrl = object.getString("picUrl");
                if (onReceiveTimeListener!=null) {
                    onReceiveTimeListener.onReceiveTime("转发类型是:"+type+"\n 转发时间是:"+ DateUtils.formatDate(System.currentTimeMillis()));
                }
                if (type.equals("1")){//转发图文的
                    Log.e(TAG, "loadData: 转发图文的到了" );

//                    we
                    LogTool.d("loadData: 转发图文的到了" );
                    sendForPhotoText(content, picUrl);
                }else{//转发连接的
                    Log.e(TAG, "loadData: 转发链接的到了" );
                    LogTool.d("loadData: 转发链接的到了" );
                    if (picUrl.indexOf(",")>0) {
                        String[] pictures = picUrl.split(",");
                        picUrl = pictures[0];
                    }
                    ShareUtils.sendToFriends(mContext,
                            url,
                            content,
                            content,
                            picUrl);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())){
            Log.d(TAG, "推送到的是通知");
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e(TAG, "onReceive: message"+message );
            Log.e(TAG, "onReceive: extra"+extras );

        }

    }
    private void sendForPhotoText(final String content, final String picUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                String[] pictures = picUrl.split(",");
                for (String picture : pictures) {
                    byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                    Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    FileUtils.saveFile(mContext, System.currentTimeMillis() + ".png", bmp);
                }
                LogTool.d("获取的 content110:"+content);
                File folder = new File(SavePath.SAVE_PIC_PATH);
                addToList(folder);
                ShareUtils.shareMultipleToMoments(mContext, content, filePictures);


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