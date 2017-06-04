package com.shuangyou.material.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.model.TimeBean;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.ShareUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.kidney_hospital.base.util.FileUtils.saveFile;
import static com.shuangyou.material.util.DownPIcUtils.buildTransaction;

/**
 * Created by Vampire on 2017/5/31.
 */

public class SendingService extends Service {

    private static final String TAG = "SendingService";
    private Context mContext;
    public List<File> filePictures = new ArrayList<>();
    public static String sendContent = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "SendingService:到服务里了");
//        loadData();
        sendContent = "我成功了!";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String picture = "http://www.downxia.com/uploadfiles/2016/0125/20160125033750715.jpg";
                byte[] b = DownPIcUtils.getHtmlByteArray(picture);

            }
        }).start();


//        ShareUtils.sendToFriends(mContext,
//                "http://product.pconline.com.cn/itbk/software/chrome/1206/2831200.html",
//                "ceshi",
//                "ceshi",
//                "http://www.downxia.com/uploadfiles/2016/0125/20160125033750715.jpg");

        return super.onStartCommand(intent, flags, startId);
    }

    public static void sendForUrl(byte[] b, Context context, String content, String url) {

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

    private void loadData() {
        List<TimeBean> timeBeanList = DataSupport.findAll(TimeBean.class);
        String type = timeBeanList.get(0).getType();
        String title = timeBeanList.get(0).getTitle();
        final String content = timeBeanList.get(0).getContent();
        String url = timeBeanList.get(0).getUrl();
        String picUrl = timeBeanList.get(0).getPicUrl();

        LogTool.d("SendingService---------" + type + "\n" + title + "\n" + content + "\n" + url + "\n" + picUrl + "\n");
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
                    saveFile(mContext, System.currentTimeMillis() + ".png", bmp);
                }
                LogTool.d("获取的 content110:" + content);
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
