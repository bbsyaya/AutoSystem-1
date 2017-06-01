package com.kidney_hospital.base.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.kidney_hospital.base.R;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by yzy on 2016/5/20.
 */
public class DownLoadService extends Service {


    private static final String TAG = "DownLoadService";
    private Context mContext;
    // notification 名字
    private String notify_name;
    /* 下载包安装路径 */
    private static String saveFileName;
    private String apkUrl;
    private Thread downLoadThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        notify_name = AppUtils.getAppName(mContext);
        saveFileName = SavePath.savePath + notify_name + ".apk";
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(mContext, "正在下载最新apk...", Toast.LENGTH_SHORT).show();
//        Log.i("DownloadService", "intent=" + intent.toString() + " ;           flags= " + flags + " ;    startId" + startId);
        if (intent.hasExtra("url")) {
            apkUrl = (String) intent.getExtras().get("url");
            Log.e(TAG, "onStartCommand apkUrl: " + apkUrl);
        }
        new Thread() {
            public void run() {
                // 开始下载
                startDownload();
            }
        }.start();

        return startId;

    }

    private void startDownload() {
        downloadApk();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 下载完毕
                    installApk();
                    break;

            }
        }
    };

    /**
     * 安装apk
     */
    private void installApk() {


        SilentInstall silentInstall = new SilentInstall();//静默安装
        boolean isSilent = silentInstall.install(saveFileName);
        Log.e(TAG, "installApk: " + isSilent);
        if (!isSilent) {
            File apkfile = new File(saveFileName);
            if (!apkfile.exists()) {
                return;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            mContext.startActivity(i);
        }

//        stopSelf();

    }

    private int lastRate = 0;
    private InputStream is = null;
    private FileOutputStream fos = null;

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();

                File file = new File(SavePath.savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                fos = new FileOutputStream(ApkFile);

                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    if (numread <= 0) {
                        mHandler.sendEmptyMessage(0);// 下载完成通知安装
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (true);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (Exception e) {

                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                mHandler.sendMessage(msg);
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    is.close();
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


}

