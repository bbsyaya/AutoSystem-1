package com.kidney_hospital.base.network;


import android.os.AsyncTask;
import android.util.Log;

import com.kidney_hospital.base.config.DefaultConfig;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络任务
 * Created by Yuanyx on 2016/8/16.
 */
public abstract class HttpAbstractTask<T> extends AsyncTask<Void, Void, T> {

    private static final OkHttpClient CLIENT = new OkHttpClient();
    public static String sFallbackResponse = DefaultConfig.HTTP_FALLBACK_RESPONSE;

    private WeakReference<OnResponseCallback> mCallback;

    public void setOnResponseCallback(OnResponseCallback callback) {
        mCallback = new WeakReference<>(callback);
    }

    public boolean hasCallback() {
        return mCallback != null;
    }

    /**
     * 创建请求体
     * <p>
     * <b>GET SAMPLE</b>
     * <div>
     * Request request = new Request.Builder()
     * .url(url)
     * .build();
     * <div/>
     * </p>
     * <p>
     * <b>POST SAMPLE</b>
     * <div>
     * RequestBody body = new FormBody.Builder().add("a","1").addEncoded("b","b").build();
     * Request request = new Request.Builder()
     * .url(url)
     * .post(body)
     * .build();
     * <div/>
     * </p>
     *
     * @return 请求体
     */
    protected abstract Request createRequest();

    /**
     * 将返回的数据解析为实体
     *
     * @param responseBody Http响应体，Json格式
     * @return 响应的实例
     */
    protected abstract T parse(String responseBody);

    /**
     * 任务标识
     *
     * @return 应用程序惟一的任务标识码
     */
    protected abstract int identifier();

    private String execute() {
        Request request = createRequest();
        if (request == null) {
            throw new IllegalStateException("createRequest() should not return null.");
        }
        try {
            Response response = CLIENT.newCall(request).execute();
            String body = response.body().string().trim();
            if (String.valueOf(response.code()).startsWith("2")&& body.length()!=0&&body.startsWith("{")) {
                return body;
            } else {
                return wrap(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sFallbackResponse;
    }

    private String wrap(Response response) {
        return String.format(Locale.CHINA, "{\"code\": -2,\"msg\":\"%s\",\"status\":%d}", response.message(), response.code());
    }

    @Override
    protected final T doInBackground(Void... voids) {
        String responseBody = execute();
        Log.e("HttpTask","response:"+responseBody);
        return parse(responseBody);
    }

    @Override
    protected final void onPostExecute(T response) {
        if (mCallback != null) {
            OnResponseCallback callback = mCallback.get();
            if (callback != null) {
                callback.onResponse(identifier(), response);
            }
        }
    }

    /**
     * 立即执行
     */
    public void executeNow() {
        executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    public interface OnResponseCallback {
        void onResponse(int identifier, Object response);
    }
}
