package com.shuangyou.material.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.update.AppUpdateUtil;
import com.kidney_hospital.base.update.UpdateCallBack;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.kidney_hospital.base.view.switchbutton.SwitchButton;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.network.GroupControlUrl;
import com.shuangyou.material.receiver.JpushReceiver;
import com.shuangyou.material.util.DaysShare;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.LoadResultUtil;
import com.shuangyou.material.util.ShareUtils;
import com.shuangyou.material.util.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.FormBody;

import static com.shuangyou.material.receiver.JpushReceiver.onReceiveTimeListener;
import static com.shuangyou.material.util.LoadResultUtil.onLoadListener;

/**
 * Created by Vampire on 2017/6/19.
 */

public class MainActivity extends AppBaseActivity implements OnReceiveTimeListener, LoadResultUtil.OnLoadListener, KeyValue {
    private static final String TAG = "MainActivity";
    private static final int MSG_SET_ALIAS = 1;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.tv_registration_id)
    TextView tvRegistrationId;
    @BindView(R.id.sbtn_access)
    SwitchButton sbtnAccess;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.btn_hand)
    Button btnHand;
    private ProgressDialog progDialog = null;// 进度条
    private String companyId;
    private String wxId;//微信号
    public List<File> filePictures = new ArrayList<>();
    private String registrationId;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
//                    logs = "Set tag and alias success";
//                    Log.e(TAG, logs);
                    tvResult.setText("集成成功,等待推送...");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e(TAG, logs);
                    String content1 = "集成失败,错误码为:" + code;
                    tvError.setText(content1 + "\n请杀死软件后重新打开,多试几次!");
                    tvError.setTextColor(0xffFF4081);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    String content = "集成失败,错误码为:" + code;
                    tvError.setText(content + "\n请杀死软件后重新打开,多试几次!");
                    tvError.setTextColor(0xffFF4081);
                    doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);
//
                    break;
            }

        }
    };
    @Override
    protected void loadData() {
        String wxPsw = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_PSW);
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).login(wxId, companyId, wxPsw,registrationId,LOG_KIND_MATERIAL), HttpIdentifier. LOGIN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    boolean isConnection = JPushInterface.getConnectionState(MainActivity.this);
                    if (!isConnection){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText("网络断开,请检查网络!");
                                LogTool.d("material网络断开");
                                Log.e(TAG, "run:网络断开 " );

                            }
                        });
                        break;
                    }
                    try {
                        Thread.sleep(1000*60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void initViews() {
        wxId = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_ID);
        initJpush();
        JpushReceiver.setOnReceiveTimeListener(this);
        LoadResultUtil.setOnLoadListener(this);
        registrationId = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(registrationId)) {
            tvRegistrationId.setText("推送注册Id获取失败");
        } else {
            tvRegistrationId.setText("推送注册Id:" + registrationId);
        }
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this));

        companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        tvCompany.setText("企业码:" + companyId);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initJpush() {
        Log.e(TAG, "initJpush single: " + wxId);
        tvDevice.setText("微信号:" + wxId);
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, wxId));


//        Set<String> set = new HashSet<>();
//        set.add(wxId);//手机的机器码
//        JPushInterface.setAliasAndTags(this, wxId, set, new TagAliasCallback() {
//            @Override
//            public void gotResult(int i, String s, Set<String> set) {
//                Log.e(TAG, "code==" + i);
//                LogTool.d("极光返回码------>" + i);
//                if (i != 0) {
//                    String content = "集成失败,错误码为:" + i;
//                    tvError.setText(content + "\n请杀死软件后重新打开,多试几次!");
//                    tvError.setTextColor(0xffFF4081);
//                    doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);
//                } else {
//                    tvResult.setText("集成成功,等待推送...");
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean flag = WorkManager.getInstance().isAccessibilitySettingsOn();
        if (!flag) {
            sbtnAccess.setChecked(false);
        } else {
            sbtnAccess.setChecked(true);
        }
    }

    @OnClick({R.id.sbtn_access, R.id.tv_update, R.id.btn_hand, R.id.iv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sbtn_access:
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
                break;
            case R.id.btn_hand:
                //手动转发
                showProgressDialog();
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND);

                break;
            case R.id.tv_update:
//                showLongToast("新版本下载,请看通知栏,不要连续点击,请稍等片刻...");
                checkUpdate();
                break;
            case R.id.iv_logout:
                new AlertDialog.Builder(this)
                        .setTitle("确定要退出登录吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //清除极光推送信息
                                JPushInterface.setAlias(MainActivity.this, "", null);
                                Set<String> set = new HashSet<>();
                                JPushInterface.setTags(MainActivity.this, set, null);

                                SPUtil.putAndApply(MainActivity.this, IS_LOGIN, false);
                                startActivity(LoginActivity.class, null);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    @Override
    public void onReceiveTime(String time) {
        try {
            DaysShare.getInstence().isRun = true;
            DaysShare.isRun = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvResult.setText(time);
        tvError.setText("准备就绪...");
        if (time.equals("网络断开连接!")){
            Log.e(TAG, "onReceiveTime:网络断开连接 " );
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,  wxId), 1000 * 60);
        }

    }

    @Override
    public void onSuccess(final String error, final String flag) {
        //转发成功的回调

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String content = wxId + "  推送成功--" + error;
                tvError.setText("推送成功--" + error);
                tvError.setTextColor(0xff999999);
                String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId,
                        flag, sp_sendCompanyContentId, LOG_KIND_MATERIAL), HttpIdentifier.LOG);
            }
        });
    }

    @Override
    public void onFailuer(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //转发失败的回调
                String content = wxId + "  失败--" + error;
                tvError.setText("  失败--" + error);
                tvError.setTextColor(0xffFF4081);
                String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, sp_sendCompanyContentId, LOG_KIND_MATERIAL), HttpIdentifier.LOG);

            }
        });
    }

    @Override
    public void onUpdate(String str) {
        Log.e(TAG, "onUpdate: " + str);
        LogTool.d("onUpdate----->" + str);
        //清除极光推送信息
        JPushInterface.setAlias(MainActivity.this, "", null);
        Set<String> set = new HashSet<>();
        JPushInterface.setTags(MainActivity.this, set, null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    @Override
    public void onAccess(String str) {
        tvError.setText("点击发送成功!");
        String content = wxId + "  点击发送成功";
        String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId,
                LOG_FLAG_OTHER, sp_sendCompanyContentId, LOG_KIND_MATERIAL), HttpIdentifier.LOG);
    }

    @Override
    public void onNetChanged(boolean isNet) {
        Log.e(TAG, "onNetChanged: "+isNet );
        if (!isNet){
            LogTool.d("import没有网,请重新开启!");
            tvResult.setText("网络断开,请重新开启!");
        }

    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case HttpIdentifier.LOGIN:
                try {
                    JSONObject jsonObject = new JSONObject(strReuslt);
                    String result = jsonObject.getString("result");
                    if (result.equals("0000")){
//                        showToast("登录成功");
                        SPUtil.putAndApply(this, IS_LOGIN, true);
                        SPUtil.putAndApply(this, COMPANY_ID, companyId);
                        tvCompany.setText("企业码:" + companyId);
                    }else{
                        showToast(jsonObject.getString("retMessage"));
                        SPUtil.putAndApply(MainActivity.this, IS_LOGIN, false);
                        startActivity(LoginActivity.class, null);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HttpIdentifier.HAND:
                LogTool.d("手动转发 json" + strReuslt);
                try {
                    JSONObject object = new JSONObject(strReuslt);
                    if (object.getString("result").equals("0000")) {
                        String latelyData = object.getString("latelyData");
                        JSONObject object1 = new JSONObject(latelyData);
                        String title = object1.getString("title");
                        String content = object1.getString("content");
                        String url = object1.getString("url");
                        String type = object1.getString("type");
                        String picUrl = object1.getString("picUrl");
                        String sendCompanyContentId = object1.getString("sendCompanyContentId");
                        String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                        if (sendCompanyContentId.equals(sp_sendCompanyContentId)) {
                            new AlertDialog.Builder(this)
                                    .setTitle("您已经转发了本素材了,是否再转发?")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //手动转发
                                            SPUtil.putAndApply(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                                            showProgressDialog();
                                            doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND);
                                        }
                                    })
                                    .setNegativeButton("否", null)
                                    .show();
                            progDialog.dismiss();
                            return;
                        }
                        SPUtil.putAndApply(MainActivity.this, SEND_COMPANY_CONTENT_ID, sendCompanyContentId);

                        if (onReceiveTimeListener != null) {
                            onReceiveTimeListener.onReceiveTime("手动转发类型:" + type + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
                        }

//                        tvResult.setText("手动转发类型:" + type + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
                        if (type.equals("1")) {//转发图文类型的
                            sendForPhotoText(content, picUrl);
                        } else {
                            if (picUrl.indexOf(",") > 0) {
                                String[] pictures = picUrl.split(",");
                                picUrl = pictures[0];
                            }
                            JpushReceiver.sContent = content;
                            ShareUtils.sendToFriendsByHand(MainActivity.this,
                                    url,
                                    title,
                                    title,
                                    picUrl);

                        }

                    } else if (object.getString("result").equals("3001")) {
                        showToast("没有需要转发的素材!");
                    } else {
                        String content = wxId + "  手动转发失败--返回" + object.getString("result");
                        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ERROR:
                progDialog.dismiss();
                break;
        }
    }

    private void showProgressDialog() {
        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.setMessage("请稍候...");
        progDialog.show();
    }

    private void checkUpdate() {
        String versionName = AppUtils.getVersionCode(this);
        FormBody formBody = new FormBody.Builder()
                .add("version", versionName)
                .add("type", "2")//1是导号,2是朋友圈
                .build();

        AppUpdateUtil updateUtil = new AppUpdateUtil(this, HttpApi.UPDATE_URL, formBody);

        updateUtil.checkUpdate(new UpdateCallBack() {
            @Override
            public void onError() {
//                Looper.prepare();
//                Toast.makeText(MainActivity.this, "服务器错误!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onError: ");
//                Looper.loop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "服务器错误!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void isUpdate(String result) {
//                Looper.prepare();
                Log.e(TAG, "isUpdate: ");
//                Toast.makeText(mContext, "正在更新,请看通知栏,不要多次点击!", Toast.LENGTH_LONG).show();
//                Looper.loop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "正在更新,请看通知栏,不要多次点击!", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void isNoUpdate() {
//                Looper.prepare();
                Log.e(TAG, "isNoUpdate: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "没有最新版本!", Toast.LENGTH_SHORT).show();
                    }
                });

//                Looper.loop();
            }
        });
    }

    private void sendForPhotoText(final String content, final String picUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                if (TextUtils.isNull(picUrl)) {
                    LogTool.d("素材图片为空");
                    if (onLoadListener != null) {
                        onLoadListener.onFailuer("素材图片是空的!");
                    }
                    return;
                }
                String[] pictures = picUrl.split(",");
                for (String picture : pictures) {
                    byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                    Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    FileUtils.saveFile(MainActivity.this, System.currentTimeMillis() + ".png", bmp);
                }
                LogTool.d("获取的 content110:" + content);
                File folder = new File(SavePath.SAVE_PIC_PATH);
                addToList(folder);
                boolean isSend = ShareUtils.shareMultipleToMoments(MainActivity.this, content, filePictures);
                if (isSend) {
                    if (onLoadListener != null) {
                        onLoadListener.onSuccess("手动转发成功", LOG_FLAG_SUCCESS_HAND);
                    }
                }

            }
        }).start();
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
