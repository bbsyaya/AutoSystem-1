package com.shuangyou.material.util;

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
        void onSuccess(String error,String flag);
        void onFailuer(String error);
        void onUpdate(String str);
        void onAccess(String str);
        void onNetChanged(boolean isNet);
    }
}
