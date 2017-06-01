package com.vampire.shareforwechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.SPUtil;
import com.vampire.shareforwechat.BaseApp;
import com.vampire.shareforwechat.interfaces.KeyValue;
import com.vampire.shareforwechat.util.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vampire on 2017/5/27.
 */

public class GroupControlReceiver extends BroadcastReceiver implements KeyValue {
    private static final String TAG = "GroupControlReceiver";
    public List<File> filePictures = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: 收到广播" );
//        String jsonPic = (String) SPUtil.get(BaseApp.getApplicationCotext(),GROUP_PICTURES,"");
//        List<String> list = JSON.parseArray(jsonPic,String.class);
        File folder = new File(SavePath.SAVE_PIC_PATH);
        addToList(folder);
        String content = (String) SPUtil.get(BaseApp.getApplicationCotext(),GROUP_CONTENT,"");
        ShareUtils.shareMultipleToMoments(BaseApp.getApplicationCotext(), content, filePictures);

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
