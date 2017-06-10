package com.shuangyou.material.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.kidney_hospital.base.util.wechat.DaysShare;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.network.GroupControlUrl;
import com.shuangyou.material.receiver.JpushReceiver;
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
 * Created by Vampire on 2017/5/31.
 */

public class GetTimeActivity extends AppBaseActivity implements OnReceiveTimeListener, LoadResultUtil.OnLoadListener, KeyValue {
    private static final String TAG = "GetTimeActivity";
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.tv_is_open)
    Button tvIsOpen;
    @BindView(R.id.tv_registration_id)
    TextView tvRegistrationId;
    private ProgressDialog progDialog = null;// 进度条
    private String companyId;
    private String wxId;//微信号
    public List<File> filePictures = new ArrayList<>();


    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        wxId = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_ID);
        initJpush();
        JpushReceiver.setOnReceiveTimeListener(this);
        LoadResultUtil.setOnLoadListener(this);
        String registrationId = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(registrationId)) {
            tvRegistrationId.setText("推送注册Id获取失败");
        } else {
            tvRegistrationId.setText("推送注册Id:" + registrationId);
        }
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this) );

        companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        tvCompany.setText("企业码:" + companyId);
    }

    private void initJpush() {
        Log.e(TAG, "initJpush single: " + wxId);
        tvDevice.setText("微信号:" + wxId);
        Set<String> set = new HashSet<>();
        set.add(wxId);//手机的机器码
        JPushInterface.setAliasAndTags(this, wxId, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.e(TAG, "code==" + i);
                LogTool.d("极光返回码------>" + i);
                if (i != 0) {
                    String content = "极光集成失败,错误码为:" + i;
                    tvError.setText(content + "\n请杀死软件后重新打开,多试几次!");
                    tvError.setTextColor(0xffFF4081);
                    doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);
                } else {
                    tvResult.setText("极光集成成功,等待推送...");
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_control;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean flag = WorkManager.getInstance().isAccessibilitySettingsOn();
        if (!flag) {
            tvIsOpen.setText("辅助功能未开启,点击开启!");
            tvIsOpen.setTextColor(0xffFF4081);

        } else {
            tvIsOpen.setText("辅助功能已开启!");
            tvIsOpen.setTextColor(0xff000000);
        }
    }

    @Override
    public void onReceiveTime(String time) {
        DaysShare.isRun = true;

        tvResult.setText(time);
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
                String content = wxId + "  转发成功--" + error;
                tvError.setText("转发成功--" + error);
                tvError.setTextColor(0xff000000);
                String sp_sendCompanyContentId = (String) SPUtil.get(GetTimeActivity.this, SEND_COMPANY_CONTENT_ID, "");
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
                String content = wxId + "  转发失败--" + error;
                tvError.setText("  转发失败--" + error);
                tvError.setTextColor(0xffFF4081);
                String sp_sendCompanyContentId = (String) SPUtil.get(GetTimeActivity.this, SEND_COMPANY_CONTENT_ID, "");
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, sp_sendCompanyContentId, LOG_KIND_MATERIAL), HttpIdentifier.LOG);

            }
        });

    }

    @Override
    public void onUpdate(String str) {
        Log.e(TAG, "onUpdate: " + str);
        LogTool.d("onUpdate----->" + str);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
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
                        String sp_sendCompanyContentId = (String) SPUtil.get(GetTimeActivity.this, SEND_COMPANY_CONTENT_ID, "");
                        if (sendCompanyContentId.equals(sp_sendCompanyContentId)) {
                            new AlertDialog.Builder(this)
                                    .setTitle("您已经转发了本素材了,是否再转发?")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //手动转发
                                            SPUtil.putAndApply(GetTimeActivity.this, SEND_COMPANY_CONTENT_ID, "");
                                            showProgressDialog();
                                            doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND);
                                        }
                                    })
                                    .setNegativeButton("否", null)
                                    .show();
                            progDialog.dismiss();
                            return;
                        }
                        SPUtil.putAndApply(GetTimeActivity.this, SEND_COMPANY_CONTENT_ID, sendCompanyContentId);

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
                            ShareUtils.sendToFriendsByHand(GetTimeActivity.this,
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

    @OnClick({R.id.tv_hand, R.id.tv_is_open, R.id.tv_version, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hand:
                //手动转发
                showProgressDialog();
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND);

                break;
            case R.id.tv_is_open:
//                TinkerPatch.with().fetchPatchUpdate(true);
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
                break;
            case R.id.tv_version:
                showLongToast("新版本下载,请看通知栏,不要连续点击,请稍等片刻...");
                checkUpdate();
                break;
            case R.id.tv_logout:
                new AlertDialog.Builder(this)
                        .setTitle("确定要退出登录吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPUtil.putAndApply(GetTimeActivity.this, IS_LOGIN, false);
                                startActivity(LoginActivity.class, null);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
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
            }

            @Override
            public void isUpdate(String result) {
//                Looper.prepare();
                Log.e(TAG, "isUpdate: ");
//                Toast.makeText(mContext, "正在更新,请看通知栏,不要多次点击!", Toast.LENGTH_LONG).show();
//                Looper.loop();

            }

            @Override
            public void isNoUpdate() {
//                Looper.prepare();
                Log.e(TAG, "isNoUpdate: ");
//                Toast.makeText(mContext, "没有最新版本!", Toast.LENGTH_SHORT).show();
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
                    FileUtils.saveFile(GetTimeActivity.this, System.currentTimeMillis() + ".png", bmp);
                }
                LogTool.d("获取的 content110:" + content);
                File folder = new File(SavePath.SAVE_PIC_PATH);
                addToList(folder);
                boolean isSend = ShareUtils.shareMultipleToMoments(GetTimeActivity.this, content, filePictures);
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
