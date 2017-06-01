package com.vampire.shareforwechat.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 群控
 * Created by Vampire on 2017/5/27.
 */

public interface GroupControlUrl {
    /**
     * 根据类型查询接口
     */
    @GET("content/getContentTime")
    Call<ResponseBody> getContentTime();

    /**
     * 根据类型查询接口
     */
    @GET("content/findContentById")
    Call<ResponseBody> findContentById(@Query("id") int id);
}
