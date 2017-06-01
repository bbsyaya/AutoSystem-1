package com.shuangyou.material.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    Call<ResponseBody> getContentTime(@Query("companyId") String companyId);

    /**
     * 根据类型查询接口
     */
    @GET("content/findContentById")
    Call<ResponseBody> findContentById(@Query("id")  String id);


    /**
     * 注册接口
     */
    @FormUrlEncoded
    @POST("companyuser/register")
    Call<ResponseBody> register(@Field("companyPhone") String companyPhone
            , @Field("companyId") String companyId
            , @Field("registrationId") String registrationId);



}
