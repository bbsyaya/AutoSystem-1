package com.vampire.shareforwechat.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.kidney_hospital.base.config.SavePath;
import com.vampire.shareforwechat.BaseApp;
import com.vampire.shareforwechat.manager.WorkManager;
import com.vampire.shareforwechat.util.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vampire on 2017/5/26.
 */

public class ShareService extends Service {
    public List<File> filePictures = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (WorkManager.getInstance().isAccessibilitySettingsOn()) {
//            File folder = new File(SavePath.SAVE_PIC_PATH);
//            addToList(folder);
//            ShareUtils.shareMultipleToMoments(BaseApp.getApplicationCotext(), "skdjksjd", filePictures);
//
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void addToList(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    filePictures.add(f);
                }
            }
        }
    }

}
