package com.vampire.shareforwechat.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vampire.shareforwechat.R;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by ${Yuanyx} on 2016/10/31.
 */

public class GlideUtil {
    /**
     * 加载本地图片
     *
     * @param context
     * @param url
     * @param target
     */
    public static void loadlocalImage(Context context, File url, ImageView target) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_logo)
                .into(target);
    }
    public static void loadlocalImage(Context context, String url, ImageView target) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_logo)
                .into(target);
    }

    /**
     * 加载圆形头像
     *
     * @param context
     * @param url
     */
    public static void loadCircleAvatar(Context context, String url, ImageView target) {
        Glide.with(context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(context))//裁剪圆形
                .placeholder(R.mipmap.ic_logo)
                .into(target);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param url
     */
    public static void loadImage(Context context, String url, ImageView target) {
        Glide.with(context)
                .load(url)
                .placeholder(R.mipmap.ic_logo)
                .into(target);
    }
}
