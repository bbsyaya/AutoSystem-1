package com.rabbit.fans.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.DefaultConfig;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.model.PhoneBean;
import com.rabbit.fans.network.PhoneUrl;
import com.rabbit.fans.util.InsertLinkMan;
import com.rabbit.fans.util.ShellUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vampire on 2017/5/27.
 */

public class AddFansService extends Service implements KeyValue{
    private static final String TAG ="AddFansService" ;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        Log.e(TAG, "onStartCommand: 服务到了" );
        LogTool.d("addfans 服务到了");



        loadData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadData() {
        String companyId = (String) SPUtil.get(mContext,COMPANY_ID,"");
        RetrofitUtils.createApi(PhoneUrl.class).myList(companyId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult = getResponseResult(response);
                PhoneBean bean = JSONObject.parseObject(strResult, PhoneBean.class);
                LogTool.d("获取号码的数量"+bean.getList().size());
                for (String num : bean.getList()) {
                    Log.e(TAG, "onResponse: " + num);
                    InsertLinkMan.insert(num,"fans-" + num);
                }
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String[] shell = new String[]{"am start -n com.tencent.mm/com.tencent.mm.ui.LauncherUI"};
                        int i = ShellUtils.execCommand(shell, true).result;

                    }
                }, 1000*60*10);//先是10分钟之后打开微信
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                sleep(1000*30);
//                loadData();
            }
        });
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
