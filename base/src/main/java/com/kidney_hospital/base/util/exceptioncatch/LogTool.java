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
        WriteFileUtil.wrieFileByBufferedWriter(date + info + "\n", "pointer_log"+ DateUtils.getTodayDate());
        Log.d(TAG, info);
    }
}
