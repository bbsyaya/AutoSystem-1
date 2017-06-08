package com.rabbit.fans.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.interfaces.OnReceiveTimeListener;
import com.rabbit.fans.model.PhoneBean;
import com.rabbit.fans.network.PhoneUrl;
import com.rabbit.fans.receiver.JpushReceiver;
import com.rabbit.fans.util.InsertLinkMan;
import com.rabbit.fans.util.LoadResultUtil;
import com.rabbit.fans.util.ShellUtils;
import com.rabbit.fans.util.WorkManager;
import com.tinkerpatch.sdk.TinkerPatch;

import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MainActivity extends AppBaseActivity implements KeyValue, LoadResultUtil.OnLoadListener, OnReceiveTimeListener {
    private static final String TAG = "MainActivity";
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

    private String companyId;
    private String wxId;//微信号
    private ProgressDialog progDialog = null;// 进度条
    private String frequency = "";
    @Override
    protected void loadData() {

    }


    @Override
    protected void initViews() {
        int patchVersion = 0;//补丁号
        try {
            patchVersion = TinkerPatch.with().getPatchVersion();
        } catch (Exception e) {
            //tinker 为 false 的情况
            patchVersion = -1;
            e.printStackTrace();
        }
        Log.e(TAG, "initViews 73:" + patchVersion);
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
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this) + "_" + patchVersion);

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
                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
                } else {
                    tvResult.setText("极光集成成功,等待推送...");
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
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
    public void onSuccess(String type, String frequency) {
        Log.e(TAG, "onSuccess: " + type + "," + frequency);
        this.frequency = frequency;
        if (type.equals("1")) {
            showProgressDialog();
            doHttp(RetrofitUtils.createApi(PhoneUrl.class).myList(companyId), HttpIdentifier.GET_PHONE_NUM);
        }
        if (type.equals("-2")){
            String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this,SEND_IMPORT_PHONE_ID,"");
            String content = "收到第二次推送,但是第一次已经导入成功了!";
            doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_TWICE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);

        }

    }

    @Override
    public void onResponse(int identifier, final String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case HttpIdentifier.GET_PHONE_NUM:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final PhoneBean bean = JSONObject.parseObject(strReuslt, PhoneBean.class);
                        LogTool.d("获取号码的数量" + bean.getList().size());
                        Log.e(TAG, "获取号码的数量: " + bean.getList().size());
                        for (int i = 0; i < bean.getList().size(); i++) {
                            Log.e(TAG, "onResponse: " + bean.getList().get(i));
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progDialog.setMessage("正在导入" + finalI + "/" + bean.getList().size());
                                }
                            });

                            InsertLinkMan.insert(bean.getList().get(i), "fans-" + bean.getList().get(i));

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("导入完成!");
                                progDialog.dismiss();

                                if (frequency.equals("1")) {
                                    String content = "一次性成功";
                                    tvError.setText(content);
                                    String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                                } else {
                                    String content = "二推成功";
                                    tvError.setText(content);
                                    String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_TWICE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                                }




                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        String[] shell = new String[]{"am start -n com.tencent.mm/com.tencent.mm.ui.LauncherUI"};
                                        int i = ShellUtils.execCommand(shell, true).result;

                                    }
                                }, 1000 * 10);//先是10秒之后打开微信

                            }
                        });
                    }
                }).start();
                break;
            case HttpIdentifier.HAND:
                try {
                    org.json.JSONObject object = new org.json.JSONObject(strReuslt);
                    if (object.getString("result").equals("0000")) {
                        String latelyData = object.getString("latelyData");
                        org.json.JSONObject object1 = new org.json.JSONObject(latelyData);
                        String typeRemark = object1.getString("typeRemark");
                        String type = object1.getString("type");
                        String sendImportPhoneId = object1.getString("sendImportPhoneId");
                        String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                        if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                            new AlertDialog.Builder(this)
                                    .setTitle("您已经导过本次号段了,是否再导入?")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //手动转发
                                            SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                            showProgress();
                                            doHttp(RetrofitUtils.createApi(PhoneUrl.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND);
                                        }
                                    })
                                    .setNegativeButton("否", null)
                                    .show();
                            return;
                        }
                        SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
                        tvResult.setText("手动导号类型:" + type + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
                        tvError.setText("手动成功");
                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, "手动成功", companyId, LOG_FLAG_SUCCESS_HAND, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                        if (type.equals("1")) {
                            showProgressDialog();
                            doHttp(RetrofitUtils.createApi(PhoneUrl.class).myList(companyId), HttpIdentifier.GET_PHONE_NUM);
                        }


                    } else if (object.getString("result").equals("3001")) {
                        showToast("没有号段");
                        tvError.setText("没有号段!");

                    } else {
                        String content = wxId + "  手动导号失败--返回" + object.getString("result");
                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE_HAND, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
            case ERROR:
                showToast("服务器异常");
                break;
        }
    }

    private void showProgressDialog() {
        progDialog = new ProgressDialog(this);
//         progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.setMessage("正在请求服务器,请稍候...");
        progDialog.show();

    }

    @Override
    public void onFailuer(String error) {
        //转发失败的回调
        String content = wxId + "  失败--" + error;
        tvError.setText("  失败--" + error);
        String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);

    }

    @Override
    public void onUpdate(String str) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前

    }

    @OnClick({R.id.tv_hand, R.id.tv_is_open, R.id.tv_version, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hand:
                showProgress();
                doHttp(RetrofitUtils.createApi(PhoneUrl.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND);
                break;
            case R.id.tv_is_open:
//                TinkerPatch.with().fetchPatchUpdate(true);
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
                break;
            case R.id.tv_version:
                showLongToast("补丁后台下载,不要连续点击,请稍等片刻...");
                TinkerPatch.with().fetchPatchUpdate(true);
                break;
            case R.id.tv_logout:
                new AlertDialog.Builder(this)
                        .setTitle("确定要退出登录吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
        tvResult.setText(time);
    }
}
