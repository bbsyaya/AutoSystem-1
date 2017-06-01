package com.shuangyou.material.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.kidney_hospital.base.activity.BaseActivity;
import com.kidney_hospital.base.config.DefaultConfig;
import com.kidney_hospital.base.util.AppManger;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public abstract class AppBaseActivity extends BaseActivity {
    private static final String NETWORK_ERROR = "network_error";
    private static final String REQUEST_RESULT = "request_result";
    private static final String GET_REQUEST_BODY = "get_request_body";
    private static final String POST_REQUEST_BODY = "post_request_body";
    public String cyIds,uIds;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManger.getInstance().addActivity(this);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        //设置ToolBar222
//        initToolBar();222
        //初始化View
        initViews();
        loadData();
        cyIds  = "1";
        uIds ="8f814257cdf942149ff6cd1ba348fc1d";

    }
    /**
     * 加载网络数据
     * */
    protected abstract void loadData();

    protected abstract void initViews();
    /**
     * 布局文件ID
     *
     * @return
     */
    public abstract int getLayoutId();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回键
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 网络请求
     *
     * @param bodyCall 请求的体
     * @param what     请求what
     */
    public void doHttp(Call<ResponseBody> bodyCall, int what) {
        bodyCall.enqueue(new MyCallback(what));
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
            showToast("网络异常!");
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

    /**
     * 启动一个Activity
     *
     * @param className 将要启动的Activity的类名
     * @param options   传到将要启动Activity的Bundle，不传时为null
     */
    public float startActivity(Class<?> className, Bundle options) {
        Intent intent = new Intent(this, className);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivity(intent);
        return 0;
    }

    /**
     * 启动一个有会返回值的Activity
     *
     * @param className   将要启动的Activity的类名
     * @param options     传到将要启动Activity的Bundle，不传时为null
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class<?> className, Bundle options,
                                       int requestCode) {
        Intent intent = new Intent(this, className);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivityForResult(intent, requestCode);
    }

    public class MyCallback implements Callback<ResponseBody> {
        private int mWhat;

        MyCallback(int what) {
            mWhat = what;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            String strResult = getResponseResult(response);
            AppBaseActivity.this.onResponse(mWhat, strResult);
            hideProgress();
            logRequestInfo(call, strResult);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.e(NETWORK_ERROR, NETWORK_ERROR + "--->" + t.getMessage());
            AppBaseActivity.this.onResponse(ERROR, t.getMessage());
            hideProgress();
        }

        /**
         * 打印请求的网络信息
         *
         * @param call
         * @param strResult
         */
        private void logRequestInfo(Call<ResponseBody> call, String strResult) {
            Request request = call.request();
            RequestBody body = request.body();
            if (body == null) {
                Log.w(GET_REQUEST_BODY, GET_REQUEST_BODY + "--->" + call.request().url().toString());
            } else {
                Log.w(POST_REQUEST_BODY, POST_REQUEST_BODY + "--->" + call.request().url().toString() +
                        '\n' + getRequestFrom(request));
            }
            Log.i(REQUEST_RESULT, REQUEST_RESULT + "--->" + strResult);
        }
    }
}
