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
    Call<ResponseBody> findContentById(@Query("id") String id);


    /**
     * 注册接口
     */
    @FormUrlEncoded
    @POST("companyuser/register")
    Call<ResponseBody> register(@Field("companyPhone") String companyPhone
            , @Field("companyId") String companyId
            , @Field("registrationId") String registrationId);


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
     * 卸载接口
     */
    @FormUrlEncoded
    @POST("companyuser/delCompanyuser")
    Call<ResponseBody> delCompanyuser(@Field("companyPhone") String companyPhone
            , @Field("companyId") String companyId);


    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("companyuser/logIn")
    Call<ResponseBody> login(@Field("companyPhone") String companyPhone
            , @Field("companyId") String companyId
            , @Field("passWord") String passWord
            , @Field("registrationId") String registrationId);

}
