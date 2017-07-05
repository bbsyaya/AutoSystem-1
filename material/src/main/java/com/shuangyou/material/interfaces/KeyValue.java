package com.shuangyou.material.interfaces;

/**
 * Created by Vampire on 2017/5/27.
 */

public interface KeyValue {
    String IS_LOGIN = "is_login";
    String COMPANY_ID = "company_id";
    String USER_ID = "user_id";
    String SEND_COMPANY_CONTENT_ID = "send_company_content_id";
    String LOG_TYPE_LOGIN="2";//日志的登录类型
    String LOG_TYPE_SHARE="4";//日志的分享类型
//    String LOG_FLAG_SUCCESS = "2";
//    String LOG_FLAG_FAILURE = "1";


    String LOG_FLAG_SUCCESS_ONCE = "1";
    String LOG_FLAG_SUCCESS_TWICE = "2";
    String LOG_FLAG_FAILURE = "3";
    String LOG_FLAG_SUCCESS_HAND = "4";
    String LOG_FLAG_FAILURE_HAND = "5";
    String LOG_FLAG_SUCCESS_ONCE_BAD = "6";//以logo形式转发的情况
    String LOG_FLAG_SUCCESS_TWICE_BAD =  "7";
    String LOG_FLAG_OTHER = "8";
    String LOG_FLAG_JAM = "9";
    String LOG_FLAG_ACCESS_OFF = "10";//辅助功能未开启

    String LOG_KIND_MATERIAL = "0";

}
