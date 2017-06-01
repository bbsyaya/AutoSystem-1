package com.shuangyou.material.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.model.TimeBean;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.ShareUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * Created by Vampire on 2017/5/31.
 */

public class SendingService extends Service {

    private static final String TAG = "SendingService";
    private Context mContext;
    public List<File> filePictures = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e(TAG, "SendingService:到服务里了" );
        loadData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadData() {
        List<TimeBean> timeBeanList = DataSupport.findAll(TimeBean.class);
        String type = timeBeanList.get(0).getType();
        String title = timeBeanList.get(0).getTitle();
        final String content = timeBeanList.get(0).getContent();
        String url = timeBeanList.get(0).getUrl();
        String picUrl = timeBeanList.get(0).getPicUrl();

        LogTool.d("SendingService---------"+type+"\n"+title+"\n"+content+"\n"+url+"\n"+picUrl+"\n");
        if (type.equals("1")){//转发图文的
            Log.e(TAG, "loadData: 转发图文的到了" );
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
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
