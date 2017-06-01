package com.kidney_hospital.base.network;

import android.util.Log;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * POST 请求
 * Created by Yuanyx on 2016/8/16.
 */
public abstract class HttpPost<T> extends HttpTask<T> {

    public abstract Map<String, String> getBody();

    @Override
    protected Request createRequest() {
        String url = createUrl();
        Map<String, String> params = getBody();

        FormBody.Builder builder = new FormBody.Builder();
        StringBuilder sb = new StringBuilder();
        sb.append(" \nform:\n");
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
            }
        }
        Log.i("POST", "url:" + url + sb.toString());
        return new Request.Builder().url(url).post(builder.build()).build();
    }
}
