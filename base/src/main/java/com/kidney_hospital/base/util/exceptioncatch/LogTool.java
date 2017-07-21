package com.kidney_hospital.base.util.exceptioncatch;

import android.util.Log;

import com.kidney_hospital.base.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Vampire on 2017/5/19.
 */

public class LogTool {
    private final static String TAG = "LOG_TOOL";

    public static void d(String info) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String date = "[" + df.format(new Date()) + "]:  ";
        WriteFileUtil.wrieFileByBufferedWriter(date + info + "\n", "auto_merge_log"+ DateUtils.getTodayDate());
        Log.e(TAG, info);
    }
    /**
     * 展示到界面上的的日志
     * @param kind 朋友圈为0,导号为1
     */
    public static void m(String info,int kind){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";
        if (kind==0){
             date = "[" + df.format(new Date()) + "朋友圈]:";
        }else if (kind==1){
             date = "[" + df.format(new Date()) + "导号]:";
        }else {
             date = "[" + df.format(new Date()) + "]:";
        }
        WriteFileUtil.wrieFileByBufferedWriter(date + info + "\n", "merge_log");
        WriteFileUtil.wrieFileByBufferedWriter(date + info + "\n", "auto_merge_log"+ DateUtils.getTodayDate());
        Log.e("LOG_MERGE", info);
    }
}
