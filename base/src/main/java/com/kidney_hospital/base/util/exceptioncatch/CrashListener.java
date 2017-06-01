package com.kidney_hospital.base.util.exceptioncatch;/**
 * Created by tarena on 2016/12/3.
 */

import java.io.File;

/**
 * created by Vampire
 * on: 2016/12/3 下午3:47
 */
public interface CrashListener {
    String TAG = "CrashListener-vampire";

    /**
     * 保存异常的日志。
     *
     * @param file
     */
    void afterSaveCrash(File file);
}
