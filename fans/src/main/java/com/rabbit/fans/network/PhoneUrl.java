package com.rabbit.fans.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vampire on 2017/5/27.
 */

public interface PhoneUrl {
    /**
     * 获取号码
     */
    @GET("phoneJson/myList")
    Call<ResponseBody> myList(@Query("companyId") String companyId);

    /**
     * 获取时间
     */
    @GET("phoneJson/getTimeList")
    Call<ResponseBody> getTimeList(@Query("companyId") String companyId);
}
