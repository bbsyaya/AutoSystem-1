package com.shuangyou.material.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Vampire on 2017/7/12.
 */

public interface Url {
    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("companyuser/logInNew")
    Call<ResponseBody> login(@Field("companyPhone") String companyPhone
            , @Field("companyId") String companyId
            , @Field("passWord") String passWord
            , @Field("registrationId") String registrationId
            , @Field("version") String version);
    /**
     * 日志接口
     */
    @FormUrlEncoded
    @POST("log/save")
    Call<ResponseBody> save(@Field("type") String type
            , @Field("companyPhone") String companyPhone
            , @Field("content") String content
            , @Field("companyId") String companyId
            , @Field("flag") String flag
            , @Field("sendId") String sendId
            , @Field("kind") String kind);
    /**
     * 手动转发
     */
    @GET("content/getLatelyArticle")
    Call<ResponseBody> getLatelyArticle(@Query("companyId") String companyId
            , @Query("companyPhone") String companyPhone);
    /**
     * 手动请求导号
     */
    @GET("content/getLatelyDaohao")
    Call<ResponseBody> getLatelyDaohao(@Query("companyId") String companyId
            , @Query("companyPhone") String companyPhone);
    /**
     * 获取号码
     */
    @GET("phoneJson/myList")
    Call<ResponseBody> myList(@Query("companyId") String companyId
            , @Query("companyuserclubId") String companyuserclubId
            , @Query("province") String province
            , @Query("city") String city
            , @Query("companyClubId") String companyClubId);
}

