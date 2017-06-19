package com.shuangyou.response.utils;

/**
 * Created by Vampire on 2017/6/11.
 */

public class ResponseCallBackUtil {
    public static OnCallBackListener onCallBackListener;

    public static void setOnCallBackListener(OnCallBackListener onCallBackListener) {
        ResponseCallBackUtil.onCallBackListener = onCallBackListener;
    }

    public interface OnCallBackListener{
        void onCallBack(String content);
    }
}
