package com.rabbit.fans.util;

/**
 * Created by Vampire on 2017/6/3.
 */

public class LoadResultUtil {
    public static OnLoadListener onLoadListener;

    public static void setOnLoadListener(OnLoadListener onLoadListener) {
        LoadResultUtil.onLoadListener = onLoadListener;
    }

    /**
     * 转发的图片成不成功的接口
     * Created by Vampire on 2017/6/1.
     */

    public interface OnLoadListener {
        void onSuccess(String type,String frequency);
        void onFailuer(String error);
        void onUpdate(String str);
        void addedNum(int num);
        void onNetChanged(boolean isNet);
    }
}
