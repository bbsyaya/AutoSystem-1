package com.vampire.shareforwechat.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public interface MaterialUrl {
    /**
     * 查询标签列表
     */
    @FormUrlEncoded
    @POST("article/labelList")
    Call<ResponseBody> labelList(@Field("cyIds") String cyId,
                                    @Field("uIds") String uIds);
    /**
     * 根据类型查询接口
     */
    @GET("article/listByTypeAll")
    Call<ResponseBody> listByTypeAll(@Query("cyIds") String cyIds,
                                @Query("tpIds") String tpIds,
                                @Query("pageSize") String pageSize,
                                @Query("currentPage") String currentPage,
                                @Query("lIds") String lIds);

    /**
     * 根据标签、类型查询文章列表接口
     */
    @GET("article/findByLabel")
    Call<ResponseBody> findByLabel(@Query("uIds") String uIds,
                                     @Query("cyIds") String cyIds,
                                     @Query("tpIds") String tpIds,
                                     @Query("pageSize") int pageSize,
                                     @Query("currentPage") int currentPage,
                                     @Query("lIds") String lIds);
    /**
     * 全部的
     */
    @GET("article/listByTypeAll")
    Call<ResponseBody> listByTypeAll(@Query("uIds") String uIds,
                                   @Query("cyIds") String cyIds,
                                   @Query("tpIds") String tpIds,
                                   @Query("pageSize") int pageSize,
                                   @Query("currentPage") int currentPage);
    /**
     * 编辑
     */
    @FormUrlEncoded
    @POST("article/edit")
    Call<ResponseBody> edit(@Field("uIds") String cyId,
                                 @Field("id") String uIds,
                                 @Field("title") String title,
                                 @Field("content") String content);
    /**
     * 删除文章、视频、电台接口
     */
    @GET("article/del")
    Call<ResponseBody> del(@Query("uIds") String uIds,
                                   @Query("cyIds") String cyIds,
                                   @Query("id") String tpIds);
}
