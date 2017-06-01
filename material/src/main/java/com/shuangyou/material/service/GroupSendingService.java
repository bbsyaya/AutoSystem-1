package com.shuangyou.material.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.DefaultConfig;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.model.ContentBean;
import com.shuangyou.material.model.TimeBean;
import com.shuangyou.material.network.GroupControlUrl;
import com.shuangyou.material.util.DownPIcUtils;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.util.ShareUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * 转发素材的服务
 * Created by Vampire on 2017/5/27.
 */

public class GroupSendingService extends Service implements KeyValue{
    private static final String TAG = "GroupSendingService";
    private Context mContext;
    public List<File> filePictures = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: 到服务里了");
        LogTool.d("onStartCommand: 到服务里了");
        loadData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadData() {
        List<TimeBean> timeBeanList = DataSupport.findAll(TimeBean.class);
        if (timeBeanList.size()==0){
            return;
        }
        String id = timeBeanList.get(0).getGroupId();
        RetrofitUtils.createApi(GroupControlUrl.class).findContentById(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult = getResponseResult(response);


                LogTool.d("service来了52"+strResult);
                final ContentBean contentBean = JSONObject.parseObject(strResult, ContentBean.class);
                if (HttpIdentifier.REQUEST_SUCCESS.equals(contentBean.getResult())) {
                    DataSupport.deleteAll(TimeBean.class);//删除表中所有数据

                    int type = contentBean.getList().get(0).getType();
                    if (type == 1) {//转发图文的
                        LogTool.d("service来了58");
                        sendForPhotoText(contentBean);

                    } else  {//转发链接的
                        LogTool.d("service来了61");
                        sendForUrl(contentBean);//转发链接类型的
                    }
                } else {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            sleep(1000*20);
//                            loadData();
//                        }
//                    }).start();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        sleep(1000*20);
//                        loadData();
//                    }
//                }).start();
            }
        });
    }
    private void sendForPhotoText(final ContentBean contentBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                String[] pictures = contentBean.getList().get(0).getPicUrl().split(",");
                for (String picture : pictures) {
                    byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                    Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    FileUtils.saveFile(mContext, System.currentTimeMillis() + ".png", bmp);
                }
                String content = contentBean.getList().get(0).getContent();
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
    private void sendForUrl(ContentBean contentBean) {
        String url = contentBean.getList().get(0).getUrl();
        String picUrl = contentBean.getList().get(0).getPicUrl();
        String content = contentBean.getList().get(0).getContent();
        ShareUtils.sendToFriends(mContext,
                url,
                content,
                content,
                picUrl);
//        finish();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private String getResponseResult(Response<ResponseBody> response) {
        try {
            String responseBody = response.body().string().trim();
            if (String.valueOf(response.code()).startsWith("2") && responseBody.length() != 0 && responseBody.startsWith("{")) {
                return responseBody;
            } else {
                return wrap(response.raw());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultConfig.HTTP_FALLBACK_RESPONSE;
    }
    private String wrap(okhttp3.Response response) {
        return String.format(Locale.CHINA, "{\"code\": -2,\"msg\":\"%s\",\"status\":%d}", response.message(), response.code());
    }

}
