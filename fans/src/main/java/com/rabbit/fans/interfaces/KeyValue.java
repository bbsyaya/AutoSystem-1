package com.rabbit.fans.interfaces;

/**
 * Created by Vampire on 2017/5/27.
 */

public interface KeyValue {
    String IS_LOGIN = "is_login";
    String COMPANY_ID ="company_id";
    String CITY = "city";
    String PROVINCE = "province";
    String COMPANY_USER_CLUB_ID = "companyuserclubId";
    String COMPANY_CLUB_ID = "companyClubId";

    String SEND_IMPORT_PHONE_ID = "send_import_phone_id";
    String LOG_TYPE_LOGIN="2";//日志的登录类型
    String LOG_TYPE_SHARE="4";//日志的分享/加粉类型

    String LOG_FLAG_SUCCESS_ONCE = "1";
    String LOG_FLAG_SUCCESS_TWICE = "2";
    String LOG_FLAG_FAILURE = "3";
    String LOG_FLAG_SUCCESS_HAND = "4";
    String LOG_FLAG_FAILURE_HAND = "5";
    String LOG_FLAG_SUCCESS_ONCE_BAD = "6";//以logo形式转发的情况
    String LOG_FLAG_SUCCESS_TWICE_BAD =  "7";
    String LOG_FLAG_OTHER = "8";

    String LOG_KIND_IMPORT = "1";


}
