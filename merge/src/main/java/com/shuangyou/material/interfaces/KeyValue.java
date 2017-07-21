package com.shuangyou.material.interfaces;

/**
 * Created by Vampire on 2017/5/27.
 */

public interface KeyValue {
    String IS_LOGIN = "is_login";
    String COMPANY_ID = "company_id";
    String USER_ID = "user_id";
    String SEND_COMPANY_CONTENT_ID = "send_company_content_id";
    String CITY = "city";
    String PROVINCE = "province";
    String COMPANY_USER_CLUB_ID = "companyuserclubId";
    String COMPANY_CLUB_ID = "companyClubId";
    String CLUB_NAME = "clubName";
    String IS_ON = "is_on";//app 是否打开

    String SEND_IMPORT_PHONE_ID = "send_import_phone_id";


    String LOG_TYPE_LOGIN="2";//日志的登录类型
    String LOG_TYPE_SHARE="4";//日志的分享类型


    String LOG_FLAG_SUCCESS_ONCE = "1";
    String LOG_FLAG_SUCCESS_TWICE = "2";
    String LOG_FLAG_FAILURE = "3";
    String LOG_FLAG_SUCCESS_HAND = "4";
    String LOG_FLAG_FAILURE_HAND = "5";
    String LOG_FLAG_SUCCESS_ONCE_BAD = "6";//以logo形式转发的情况
    String LOG_FLAG_SUCCESS_TWICE_BAD =  "7";
    String LOG_FLAG_OTHER = "8";//转发成功,或者添加好友成功
    String LOG_FLAG_JAM = "9";
    String LOG_FLAG_ACCESS_OFF = "10";//辅助功能未开启

    String LOG_KIND_MATERIAL = "0";
    String LOG_KIND_IMPORT = "1";

}
