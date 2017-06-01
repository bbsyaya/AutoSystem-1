package com.shuangyou.material.interfaces;

/**
 * 转发的图片成不成功的接口
 * Created by Vampire on 2017/6/1.
 */

public interface OnLoadListener {
    void onSuccess();
    void onFailuer(String error);
}
