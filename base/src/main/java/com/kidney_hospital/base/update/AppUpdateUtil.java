package com.kidney_hospital.base.update;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kidney_hospital.base.util.server.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 检测更新的工具类
 */
public class AppUpdateUtil {


    private static final String TAG = "AppUpdateUtil";
    private String vistionUrl;
    private Context mContext;
    private FormBody formBody;

    public AppUpdateUtil(Context context, String vistionUrl, FormBody formBody) {
        this.vistionUrl = vistionUrl;
        this.mContext = context;
        this.formBody = formBody;
    }


    public void checkUpdate(final UpdateCallBack callBack) {


        Request.Builder build = new Request.Builder()
                .url(vistionUrl);

        if (formBody != null) {
            build.post(formBody);
        }

        OkHttpUtils.getInstance().newCall(build.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                Log.e("versionresult", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("2000")) {
                        callBack.isUpdate(result);
                        String db = jsonObject.getString("db");
                        JSONObject jo = new JSONObject(db);
                        String downloadURL = jo.getString("downloadURL");
                        Log.e(TAG, "onResponse url: "+downloadURL );
                        Intent intent = new Intent(mContext, DownLoadService.class);
                        intent.putExtra("url", downloadURL);
                        mContext.startService(intent);

                    } else {
                        callBack.isNoUpdate();
                    }

                } catch (JSONException e) {
                    callBack.onError();
//                    Toast.makeText(mContext, "json字符串异常", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


    }






}
