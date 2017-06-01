package com.rabbit.fans.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.DefaultConfig;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.model.TimeBean;
import com.rabbit.fans.model.TimeEntity;
import com.rabbit.fans.network.PhoneUrl;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Vampire on 2017/5/28.
 */

public class GetTimeService extends Service implements KeyValue {
    private static final String TAG = "GetTimeService";
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTool.d("获取号码的服务到了55");
        Log.e(TAG, "onStartCommand: 获取号码的服务到了");
        String companyId = (String) SPUtil.get(this, COMPANY_ID, "");
        Log.e(TAG, "onStartCommand ci: "+companyId );
        RetrofitUtils.createApi(PhoneUrl.class).getTimeList(companyId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String strResult = getResponseResult(response);
                TimeBean timeBean = JSONObject.parseObject(strResult, TimeBean.class);
                if (timeBean.getResult().equals(HttpIdentifier.REQUEST_SUCCESS)) {
                    DataSupport.deleteAll(TimeEntity.class);//删除表中所有数据
                    for (TimeBean.ListBean bean : timeBean.getList()) {
                        String[] timeSplit = bean.getTimeContent().split(":");
                        TimeEntity timeEntity = new TimeEntity();
                        int hour = Integer.parseInt(timeSplit[0]);
                        int minute = (int) (Math.random() * 60);
                        int second = (int) (Math.random() * 60);
                        timeEntity.setHour(hour);
                        timeEntity.setMinute(minute);
                        timeEntity.setSecond(second);
                        timeEntity.save();
                    }
                    startService(new Intent(mContext, GetFansService.class));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
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


    /**
     * 获取okhttp 的请求体
     */
    private String getRequestFrom(Request request) {
        RequestBody requestBody = request.body();
        okio.Buffer buffer = new okio.Buffer();
        try {
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String paramsStr = buffer.readString(charset);
            return URLDecoder.decode(paramsStr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
