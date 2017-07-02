package com.rabbit.fans.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.update.AppUpdateUtil;
import com.kidney_hospital.base.update.UpdateCallBack;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.JumpToWeChatUtil;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.kidney_hospital.base.util.wechat.AddByLinkMan;
import com.kidney_hospital.base.view.switchbutton.SwitchButton;
import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.interfaces.OnReceiveTimeListener;
import com.rabbit.fans.model.PhoneBean;
import com.rabbit.fans.model.UserInfo;
import com.rabbit.fans.network.PhoneUrl;
import com.rabbit.fans.receiver.JpushReceiver;
import com.rabbit.fans.util.InsertLinkMan;
import com.rabbit.fans.util.LoadResultUtil;
import com.rabbit.fans.util.WorkManager;

import org.json.JSONException;

import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.FormBody;


public class MainActivity extends AppBaseActivity implements KeyValue, LoadResultUtil.OnLoadListener, OnReceiveTimeListener {
    private static final String TAG = "MainActivity";
    private static final int MSG_SET_ALIAS = 1;
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
    @BindView(R.id.tv_registration_id)
    TextView tvRegistrationId;
    @BindView(R.id.tv_count_time)
    TextView tvCountTime;
    @BindView(R.id.sbtn_access)
    SwitchButton sbtnAccess;
    @BindView(R.id.tv_count_end)
    TextView tvCountEnd;
    private String companyId;
    private String wxId;//微信号
    private ProgressDialog progDialog = null;// 进度条
    private String frequency = "";
    private TimeCount timeCount;
    private TimeWxCount timeWxCount;
    private boolean flagHand = false;//手动的
    private String city, province, companyuserclubId;
    private String companyClubId;
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
                    logs = "Set tag and alias succes";
                    Log.e(TAG, logs);
                    LogTool.d("集成成功, alias-->>"+alias);
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
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, wxId), 1000 * 60);
                    break;
                case 6003:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    String content2 = "集成失败,错误码为:" + code;
                    tvError.setText(content2 + "\n包含非法字符,请联系开发人员!");
                    tvError.setTextColor(0xffFF4081);
                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content2, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    String content = "集成失败,错误码为:" + code;
                    tvError.setText(content + "\n请联系开发人员!");
                    tvError.setTextColor(0xffFF4081);
                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
//
                    break;
            }

        }
    };

    @Override
    protected void loadData() {
        boolean isOn = (boolean) SPUtil.get(MainActivity.this,IS_ON,false);
        if (isOn){
            frequency = "1";
            getNumber();
            SPUtil.putAndApply(MainActivity.this,IS_ON,false);
        }


        //自动登录,再走一遍登录接口,以防万一
        String wxPsw = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_PSW);
        doHttp(RetrofitUtils.createApi(PhoneUrl.class).login(wxId, companyId, wxPsw, registrationId, LOG_KIND_IMPORT), HttpIdentifier.LOGIN);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    boolean isConnection = JPushInterface.getConnectionState(MainActivity.this);
//                    if (!isConnection) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                LogTool.d("fans网络断开");
//                                tvResult.setText("网络断开,请检查网络!");
//                                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, wxId), 1000 * 90);
//                            }
//                        });
//
//                    }
//                    try {
//                        Thread.sleep(1000 * 60);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTool.d("fans杀死了ondestroy");
    }

    @Override
    protected void initViews() {

        LogTool.d("fans--开始onCreate");
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
        city = (String) SPUtil.get(this, CITY, "");
        province = (String) SPUtil.get(this, PROVINCE, "");
        companyuserclubId = (String) SPUtil.get(this, COMPANY_USER_CLUB_ID, "");
        companyClubId = (String) SPUtil.get(this, COMPANY_CLUB_ID, "");
        Log.e(TAG, "initViews: " + city + "@@" + province + "@@" + companyuserclubId + "@@" + companyClubId);
        LogTool.d("loginInfo110--->>>" + city + "@@" + province + "@@" + companyuserclubId + "@@" + companyClubId);
        tvCompany.setText("企业码:" + companyId + "  " + province + "  " + city);
        SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("lat", "38.06859");  //114.314491,38.06859
        e.putString("lon", "114.314491");
        e.commit();

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
//                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
//                } else {
//                    tvResult.setText("集成成功,等待推送...");
//                }
//            }
//        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_new;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(MainActivity.this);
        Log.e(TAG, "onResume:");
//        if (TextUtils.isNull(tvCountEnd.getText().toString().trim())) {
        if (tvCountEnd.getText().toString().trim().contains("加粉结束")
                || TextUtils.isNull(tvCountEnd.getText().toString().trim())) {
            LogTool.d("fans--onResume 加粉结束");
            Log.e(TAG, "onResume: 加粉结束");
            try {
                AddByLinkMan.getInstence().jumpRemarkNum = 100;
                AddByLinkMan.jumpRemarkNum = 100;
//                AddByLinkMan.getInstence().flagNewFriendsClick = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tvCountEnd.getText().toString().trim().contains("正在加粉")) {

            //TODO 开一个线程睡眠
//            JumpToWeChatUtil.jumpToLauncherUi();
        }
        boolean flag = WorkManager.getInstance().isAccessibilitySettingsOn();
        if (!flag) {
            sbtnAccess.setChecked(false);
        } else {
            sbtnAccess.setChecked(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(MainActivity.this);
    }

    @Override
    public void onSuccess(String type, String frequency) {
        Log.e(TAG, "onSuccess: " + type + "," + frequency);
        this.frequency = frequency;
        if (type.equals("1")) {
            getNumber();
        } else if (type.equals("-2")) {
            String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
            String content = "收到第二次推送,但是第一次已经推送成功了!";
            doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
        } else if (type.equals("4")) {//附近的人
            SPUtil.putAndApply(MainActivity.this, IS_NEARBY_OPEN, true);//开启附近的人辅助功能
            String content = "收到推送";
            tvError.setText(content);
        } else if (type.equals("-3")) {//阻塞
            String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
            String content = wxId + "-->因为极光阻塞,推送信息超时!";
            tvError.setText("因为极光阻塞,推送信息超时!");
            doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_JAM, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
        }

    }

    @Override
    public void onResponse(int identifier, final String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case HttpIdentifier.LOG:
                LogTool.d("fans_log277--->>" + strReuslt);
                break;
            case HttpIdentifier.LOGIN:
                UserInfo userInfo = JSONObject.parseObject(strReuslt, UserInfo.class);
                if (userInfo.getResult() == null) {
                    showToast("网络异常");
                    tvResult.setText("没有网,请重新开启!");
                    return;
                }
                if (userInfo.getResult().equals(HttpIdentifier.REQUEST_SUCCESS)) {
//                    showToast("登录成功");
                    SPUtil.putAndApply(this, IS_LOGIN, true);
                    SPUtil.putAndApply(this, COMPANY_ID, companyId);
                    SPUtil.putAndApply(this, CITY, userInfo.getDb().getCity());
                    SPUtil.putAndApply(this, PROVINCE, userInfo.getDb().getProvince());
                    SPUtil.putAndApply(this, COMPANY_USER_CLUB_ID, userInfo.getDb().getCompanyuserclubId() + "");
                    SPUtil.putAndApply(this, COMPANY_CLUB_ID, userInfo.getDb().getCompanyClubId() + "");
                    tvCompany.setText("企业码:" + companyId + "  " + province + "  " + city);
                } else {
                    showToast(userInfo.getRetMessage());
                    SPUtil.putAndApply(MainActivity.this, IS_LOGIN, false);
                    startActivity(LoginActivity.class, null);
                    finish();
                }
                break;
            case HttpIdentifier.GET_PHONE_NUM:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            AddByLinkMan.getInstence().jumpRemarkNum = 0;
                            AddByLinkMan.jumpRemarkNum = 0;
//                            AddByLinkMan.getInstence().flagNewFriendsClick = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final PhoneBean bean = JSONObject.parseObject(strReuslt, PhoneBean.class);
                        if (null == bean.getResult()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        progDialog.dismiss();
                                        tvError.setText("服务器异常");
                                        tvError.setTextColor(0xffFF4081);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            return;
                        }
                        if (bean.getResult().equals(HttpIdentifier.REQUEST_SUCCESS)) {


                            if (null == bean.getList() || bean.getList().size() == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("无号段");
                                        try {
                                            progDialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                return;
                            }
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

                                InsertLinkMan.insert(bean.getList().get(i), "#" + bean.getList().get(i));

                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("导入完成!");
                                    try {
                                        JumpToWeChatUtil.jumpToFansMainActivity();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (frequency.equals("1")) {
                                        String content = "一次性导入成功";
                                        tvError.setText(content);
                                        tvError.setTextColor(0xff000000);
                                        String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                        LogTool.d("一次性导入成功380--->>>");
                                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                                    } else {
                                        if (flagHand) {
                                            flagHand = false;
                                        } else {
                                            String content = "二次导入成功";
                                            tvError.setText(content);
                                            tvError.setTextColor(0xff000000);
                                            String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                            LogTool.d("二次导入成功390--->>>");
                                            doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_SUCCESS_TWICE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                                        }
                                    }

                                    long time = Long.parseLong(bean.getTime());
                                    Log.e(TAG, "run time: " + time);
                                    showLongToast("跳转到微信加粉还有" + time + "分钟开始,请提前停止其他操作!");
                                    timeCount = new TimeCount(1000 * 60*time, 1000);//TODO 提交的时候需要更改
                                    timeCount.start();

                                }
                            });
                        } else if (bean.getResult().equals("1066")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("号码已经导完了!");
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (bean.getResult().equals("9993")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("设备组未设置省市、省市不可同时为空!");
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (bean.getResult().equals("1003")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("企业未设置省市!");
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (bean.getResult().equals("1013")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("省市已经导完!");
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (bean.getResult().equals("9996")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("企业不存在,不可导号!");
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("返回错误码:" + bean.getResult());
                                    LogTool.d("error---397" + bean.getResult());
                                    try {
                                        progDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }).start();
                break;
            case HttpIdentifier.HAND:
                try {
                    org.json.JSONObject object = new org.json.JSONObject(strReuslt);
                    if (object.getString("result").equals("0000")) {
                        String latelyData = object.getString("latelyData");
                        org.json.JSONObject object1 = new org.json.JSONObject(latelyData);
//                        String typeRemark = object1.getString("typeRemark");
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
                        tvResult.setText("类型:通讯录导入号码" + "\n" + DateUtils.formatDate(System.currentTimeMillis()));
                        tvError.setText("手动导入成功");
                        tvError.setTextColor(0xff000000);
                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, "手动成功", companyId, LOG_FLAG_SUCCESS_HAND, sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                        if (type.equals("1")) {
                            flagHand = true;
                            getNumber();
                        }


                    } else if (object.getString("result").equals("3001")) {
                        showToast("没有号段");
                        tvError.setText("没有号段!");
                        tvError.setTextColor(0xffFF4081);

                    } else {
                        String content = wxId + "  手动导号失败--返回" + object.getString("result");
                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE_HAND, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
            case ERROR:
                SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, "");

                showToast("服务器异常");
                try {
                    LogTool.d("没导出号来exception377-->>" + strReuslt);
                    tvError.setText("服务器异常");
                    tvError.setTextColor(0xffFF4081);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void getNumber() {
        showProgressDialog();
        doHttp(RetrofitUtils.createApi(PhoneUrl.class).myList(companyId, companyuserclubId, province, city, companyClubId), HttpIdentifier.GET_PHONE_NUM);
//        doHttp(RetrofitUtils.createApi(PhoneUrl.class).myList("49", "", "", ""), HttpIdentifier.GET_PHONE_NUM);
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
        Log.e(TAG, "onFailuer435: ");
        //转发失败的回调
        String content = wxId + "  失败--" + error;
        tvError.setText("  失败--" + error);
        tvError.setTextColor(0xffFF4081);
        String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);

    }

    @Override
    public void onUpdate(String str) {
        //清除极光推送信息
//        JPushInterface.setAlias(MainActivity.this, "", null);
//        Set<String> set = new HashSet<>();
//        JPushInterface.setTags(MainActivity.this, set, null);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Process.killProcess(Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前

    }

    @Override
    public void addedNum(int num) {
        LogTool.d("已经加了" + num + "个好友");
        tvCountTime.setText("已经加了" + num + "个好友");
    }

    @Override
    public void onNetChanged(boolean isNet) {
        Log.e(TAG, "onNetChanged: " + isNet);
        if (!isNet) {
            LogTool.d("fans没有网,请重新开启!");
            tvResult.setText("网络断开,请重新开启!");
//            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, wxId), 1000 * 60);
        }

    }

    @OnClick({R.id.btn_hand, R.id.sbtn_access, R.id.tv_update, R.id.iv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_hand:
                showProgress();
                doHttp(RetrofitUtils.createApi(PhoneUrl.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND);
                break;
            case R.id.sbtn_access:
//                TinkerPatch.with().fetchPatchUpdate(true);
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
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
//                                JPushInterface.setAlias(MainActivity.this, "", null);
//                                Set<String> set = new HashSet<>();
//                                JPushInterface.setTags(MainActivity.this, set, null);
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
        tvError.setText("准备就绪...");
//        tvCountTime.setText("");
        if (time.equals("网络断开连接!")) {
            Log.e(TAG, "onReceiveTime:网络断开连接 ");
            LogTool.d("fans-->网络断开连接");
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, wxId), 1000 * 60);
        }
    }

    private void checkUpdate() {
        String versionName = AppUtils.getVersionCode(this);
        FormBody formBody = new FormBody.Builder()
                .add("version", versionName)
                .add("type", "1")//1是导号,2是朋友圈
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
//                Toast.makeText(mContext, "没有最新版本!", Toast.LENGTH_SHORT).show();
//                Looper.loop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "没有最新版本!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvCountTime.setText(millisUntilFinished / 1000 + " 秒后微信自动开启加粉");
        }

        @Override
        public void onFinish() {
            try {
                AddByLinkMan.getInstence().jumpRemarkNum = 0;
                AddByLinkMan.jumpRemarkNum = 0;
//                AddByLinkMan.getInstence().flagNewFriendsClick = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogTool.d("fans--开始加粉");
            tvCountTime.setText("开始加粉");
            timeWxCount = new TimeWxCount(1000 * 60 * 11, 1000);//TODO
            timeWxCount.start();

            JumpToWeChatUtil.jumpToLauncherUi();
        }
    }

    class TimeWxCount extends CountDownTimer {

        public TimeWxCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            tvCountEnd.setText("正在加粉," + l / 1000 + " 秒后结束");
        }

        @Override
        public void onFinish() {
//            try {
//                AddByLinkMan.getInstence().jumpRemarkNum = 100;
//                AddByLinkMan.jumpRemarkNum = 100;
////                AddByLinkMan.getInstence().flagNewFriendsClick = false;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            LogTool.d("fans--结束加粉");
            JumpToWeChatUtil.jumpToFansMainActivity();
            tvCountEnd.setText(DateUtils.formatDate(System.currentTimeMillis()) + "  加粉结束");

        }
    }

}
