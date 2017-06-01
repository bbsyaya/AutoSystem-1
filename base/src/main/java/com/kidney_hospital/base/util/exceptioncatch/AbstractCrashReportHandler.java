package com.kidney_hospital.base.util.exceptioncatch;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by Vampire on 2017/5/19.
 */

public class AbstractCrashReportHandler implements CrashListener {
    private Context mContext;

    public AbstractCrashReportHandler(Context context) {
        mContext = context;
        CrashHandler handler = CrashHandler.getInstance();
        Log.e(TAG, "getLogDir(mContext):" + getLogDir(mContext));
        File logDir = getLogDir(context);
        if (logDir.exists()) {
            logDir.delete();
            getLogDir(mContext);
        }
        handler.init(logDir, this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    protected File getLogDir(Context context) {
        return new File("/storage/emulated/0/Download/", "crash.log");
    }

    @Override
    public void afterSaveCrash(File file) {

    }
}
