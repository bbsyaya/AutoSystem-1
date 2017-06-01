package com.vampire.shareforwechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kidney_hospital.base.config.SavePath;
import com.vampire.shareforwechat.BaseApp;
import com.vampire.shareforwechat.activity.AlarmDialogActivity;
import com.vampire.shareforwechat.util.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 焕焕 on 2017/5/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_RECEIVER_CONTENT = "receiver_content";
    private static final String TAG = "AlarmReceiver";
    public static String content;
    public List<File> filePictures = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
//        content = intent.getStringExtra(EXTRA_RECEIVER_CONTENT);
//        Intent in = new Intent(context,AlarmDialogActivity.class);
//        intent.putExtra(EXTRA_RECEIVER_CONTENT,content);
//        //当前activity作为新任务启动--在新的任务栈中启动
//        in.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(in);
        Log.e(TAG, "onReceive: 收到广播" );

        File folder = new File(SavePath.SAVE_PIC_PATH);
        addToList(folder);
        ShareUtils.shareMultipleToMoments(BaseApp.getApplicationCotext(), "skdjksjd", filePictures);


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
