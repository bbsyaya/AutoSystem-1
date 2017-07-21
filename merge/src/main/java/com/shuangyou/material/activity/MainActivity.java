package com.shuangyou.material.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.JumpToWeChatUtil;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.kidney_hospital.base.util.wechat.AddByLinkMan;
import com.kidney_hospital.base.view.switchbutton.SwitchButton;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.model.PhoneBean;
import com.shuangyou.material.model.UserInfo;
import com.shuangyou.material.network.Url;
import com.shuangyou.material.util.DownPIcUtils;
import com.shuangyou.material.util.InsertLinkMan;
import com.shuangyou.material.util.LoadResultUtil;
import com.shuangyou.material.util.ShareUtils;
import com.shuangyou.material.util.ShellUtils;
import com.shuangyou.material.util.WorkManager;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.FormBody;

import static com.shuangyou.material.util.LoadResultUtil.onLoadListener;

public class MainActivity extends AppBaseActivity implements LoadResultUtil.OnLoadListener, KeyValue {
    private static final String TAG = "MainActivity";
    private static final int MSG_SET_ALIAS = 1;
    private static final int MSG_ACCESS = 2;
    private static final int MSG_SET_FANS = 3;
    private static final int MSG_ACCESS_FANS = 4;
    @BindView(R.id.iv_logout)
    ImageView ivLogout;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.sbtn_access)
    SwitchButton sbtnAccess;
    @BindView(R.id.rl_main_log)
    RelativeLayout rlMainLog;
    @BindView(R.id.tv_material_content)
    TextView tvMaterialContent;
    @BindView(R.id.tv_material_time)
    TextView tvMaterialTime;
    @BindView(R.id.tv_fans_content)
    TextView tvFansContent;
    @BindView(R.id.tv_fans_clock)
    TextView tvFansClock;
    @BindView(R.id.tv_fans_time)
    TextView tvFansTime;
    @BindView(R.id.tv_main_error)
    TextView tvMainError;
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    //    @BindView(R.id.btn_hand_fans)
//    Button btnHandFans;
//    @BindView(R.id.btn_hand_material)
//    Button btnHandMaterial;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    private ProgressDialog progDialog = null;// 进度条
    private String companyId;
    private String wxId;//微信号
    public List<File> filePictures = new ArrayList<>();
    private String registrationId;
    private String city, province, clubName;
    private String frequency = "";
    private String companyClubId, companyuserclubId;
    private boolean flagHand = false;//手动导号的
    private TimeCount timeCount;
    private TimeWxCount timeWxCount;
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
                case MSG_ACCESS://辅助功能
                    Log.e(TAG, "errorlaile");
                    if (!tvMaterialContent.getText().toString().trim().contains("转发成功")) {
                        Log.e(TAG, "errorlaile1111");
                        LogTool.m("自动转发无响应,等待辅助功能重启",0);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "run: 111");
                                WorkManager.getInstance().setAccessibilitySettingOffAndOn();
                            }
                        }).start();
                    }



                    break;
                case MSG_ACCESS_FANS://导号对应的辅助功能假开
                    Log.e(TAG, "handleMessage:");
                    if (!tvFansContent.getText().toString().trim().contains("已经加了")){
                        LogTool.m("自动导号无响应,等待辅助功能重启",1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "run: 111");
                                WorkManager.getInstance().setAccessibilitySettingOffAndOn();
                            }
                        }).start();

                    }
                    break;
                case MSG_SET_FANS:
                    JumpToWeChatUtil.jumpToLauncherUi();
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
                    LogTool.m("集成成功! alias:" + alias, -1);
                    tvMainError.setText("");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e(TAG, logs);
                    LogTool.m("集成超时,请检查网络...", -1);
                    tvMainError.setText("集成超时,请检查网络...");
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, wxId), 1000 * 60);
                    break;
                case 6003:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    LogTool.m("包含非法字符,请联系开发人员!", -1);
                    tvMainError.setText("包含非法字符,请联系开发人员!");
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    String content = "集成失败,错误码为:" + code;
                    tvMainError.setText("错误码为:" + code + "--请联系开发人员!");
                    LogTool.m("错误码为:" + code + "--请联系开发人员!", -1);
                    doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);
                    break;
            }

        }
    };

    @Override
    protected void loadData() {
        boolean isOn = (boolean) SPUtil.get(MainActivity.this, IS_ON, false);
        if (isOn) {
            LogTool.d("ison的情况");
            frequency = "1";
            getNumber();
            SPUtil.putAndApply(MainActivity.this, IS_ON, false);
        }

        String wxPsw = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_PSW);
        doHttp(RetrofitUtils.createApi(Url.class).login(wxId, companyId, wxPsw, registrationId, LOG_KIND_MATERIAL), HttpIdentifier.LOGIN);
    }

    @Override
    protected void initViews() {
        LogTool.d("material--开始onCreate");
        LogTool.m("软件开始运行", -1);
        wxId = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_ID);
        LoadResultUtil.setOnLoadListener(this);
        initJpush();
        registrationId = JPushInterface.getRegistrationID(this);
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this));
        companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        city = (String) SPUtil.get(this, CITY, "");
        province = (String) SPUtil.get(this, PROVINCE, "");
        companyuserclubId = (String) SPUtil.get(this, COMPANY_USER_CLUB_ID, "");
        companyClubId = (String) SPUtil.get(this, COMPANY_CLUB_ID, "");
        clubName = (String) SPUtil.get(this, CLUB_NAME, "");
        tvGroupName.setText("设备组:" + clubName);
        tvCompany.setText("企业码:" + companyId + "  " + province + "  " + city);

        new Thread(new Runnable() {
            @Override
            public void run() {
                WorkManager.getInstance().setAccessibilitySettingsOn();
            }
        }).start();

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogTool.m("软件被杀死了!", -1);
    }

    private void initJpush() {
        Log.e(TAG, "initJpush single: " + wxId);
        tvDevice.setText("微信号:" + wxId);
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, wxId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:888 ");
        LogTool.d("fans--onResume--数量--" + AddByLinkMan.getInstence().jumpRemarkNum);
        if (tvFansClock.getText().toString().trim().contains("加粉结束")) {
            LogTool.d("fans--onResume 加粉结束");
            Log.e(TAG, "onResume: 加粉结束");
            try {
                AddByLinkMan.getInstence().jumpRemarkNum = 100;
                AddByLinkMan.jumpRemarkNum = 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tvFansClock.getText().toString().trim().contains("正在加粉")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: 111");
                    WorkManager.getInstance().setAccessibilitySettingsOn();
                }
            }).start();
            Log.e(TAG, "onResume: 正在加粉");
            LogTool.d("fans--正在加粉");
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_FANS), 1000 * 10);
//            //TODO 开一个线程睡眠
        } else if (TextUtils.isNull(tvFansClock.getText().toString().trim())) {
            LogTool.d("停止加好友!");
            try {
                AddByLinkMan.getInstence().jumpRemarkNum = 100;
                AddByLinkMan.jumpRemarkNum = 100;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResponse(int identifier, final String strReuslt) {
        super.onResponse(identifier, strReuslt);
        switch (identifier) {
            case HttpIdentifier.LOG:
                LogTool.d("material_log381--->>" + strReuslt);
                break;
            case HttpIdentifier.LOGIN:
                UserInfo userInfo = JSONObject.parseObject(strReuslt, UserInfo.class);
                if (userInfo.getResult() == null) {
                    showToast("网络异常");
                    tvMainError.setText("没有网,请重新开启!");
                    return;
                }
                if (userInfo.getResult().equals(HttpIdentifier.REQUEST_SUCCESS)) {
                    tvMainError.setText("");
//                    showToast("登录成功");
                    LogTool.m("自动登录成功", -1);
                    SPUtil.putAndApply(this, IS_LOGIN, true);
                    SPUtil.putAndApply(this, COMPANY_ID, companyId);
                    SPUtil.putAndApply(this, CITY, userInfo.getDb().getCity());
                    SPUtil.putAndApply(this, PROVINCE, userInfo.getDb().getProvince());
                    SPUtil.putAndApply(this, COMPANY_USER_CLUB_ID, userInfo.getDb().getCompanyuserclubId() + "");
                    SPUtil.putAndApply(this, COMPANY_CLUB_ID, userInfo.getDb().getCompanyClubId() + "");
                    SPUtil.putAndApply(this, CLUB_NAME, userInfo.getDb().getClubName() + "");
                    tvCompany.setText("企业码:" + companyId + "  " + userInfo.getDb().getProvince() + "  " + userInfo.getDb().getCity());
                    tvGroupName.setText("设备组:" + userInfo.getDb().getClubName());
                } else {
                    LogTool.m("自动登录失败:" + userInfo.getRetMessage(), -1);
                    showToast(userInfo.getRetMessage());
                    SPUtil.putAndApply(MainActivity.this, IS_LOGIN, false);
                    startActivity(LoginActivity.class, null);
                    finish();
                }
                break;
            case HttpIdentifier.HAND_MATERIAL:
                LogTool.d("手动转发 json" + strReuslt);
                handleMaterialByHand(strReuslt);
                break;
            case HttpIdentifier.HAND_FANS:
                handleFansByHand(strReuslt);
                break;
            case HttpIdentifier.HAND_FANS_ONLY://只导号
                handleFansOnlyByHand(strReuslt);
                break;
            case HttpIdentifier.GET_PHONE_NUM_ONLY:
                handleNumOnlyByHand(strReuslt);
                break;
            case HttpIdentifier.GET_PHONE_NUM:
                handleNumByHand(strReuslt);
                break;
            case ERROR:
                LogTool.m("服务器异常或者网络太慢", -1);
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void handleNumOnlyByHand(final String strReuslt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final PhoneBean bean = JSONObject.parseObject(strReuslt, PhoneBean.class);
                if (null == bean.getResult()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progDialog.dismiss();
                                tvMainError.setText("网络异常");
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
                    LogTool.m("获取号码的数量" + bean.getList().size(), 1);
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
                            tvMainError.setText("");
                            showToast("导入完成!");

                            try {
                                progDialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    }

    private void handleNumByHand(final String strReuslt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AddByLinkMan.getInstence().jumpRemarkNum = 0;
                    AddByLinkMan.jumpRemarkNum = 0;
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
                                tvMainError.setText("网络异常");
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
                    LogTool.m("获取号码的数量" + bean.getList().size(), 1);
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
                            tvMainError.setText("");
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
                                if (LoadResultUtil.onLoadListener != null) {
//                                    LoadResultUtil.onLoadListener.onFansResult("一次性导入成功", LOG_FLAG_SUCCESS_ONCE);
                                    LoadResultUtil.onLoadListener.onFansResult("导号成功", LOG_FLAG_SUCCESS_ONCE);
                                }

                            } else {
                                if (flagHand) {
                                    flagHand = false;
                                } else {
                                    if (LoadResultUtil.onLoadListener != null) {
//                                        LoadResultUtil.onLoadListener.onFansResult("二次导入成功", LOG_FLAG_SUCCESS_TWICE);
                                        LoadResultUtil.onLoadListener.onFansResult("导号成功", LOG_FLAG_SUCCESS_TWICE);
                                    }

                                }
                            }

                            long time = Long.parseLong(bean.getTime());
                            Log.e(TAG, "run time: " + time);
                            LogTool.d("代码运行到倒计时这里了--->>" + time);
                            showLongToast("跳转到微信加粉还有" + time + "分钟开始,请提前停止其他操作!");
                            timeCount = new MainActivity.TimeCount(1000 * 60 * time, 1000);//TODO 提交的时候需要更改
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
    }

    private void handleFansOnlyByHand(String strReuslt) {
        try {
            org.json.JSONObject object = new org.json.JSONObject(strReuslt);
            if (object.getString("result").equals("0000")) {
                tvMainError.setText("");
                String latelyData = object.getString("latelyData");
                org.json.JSONObject object1 = new org.json.JSONObject(latelyData);
//                        String typeRemark = object1.getString("typeRemark");
                String type = object1.getString("type");
                String sendImportPhoneId = object1.getString("sendImportPhoneId");
                String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                    new AlertDialog.Builder(this)
                            .setTitle("您已经导过本次任务了,是否再导入?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //手动转发
                                    SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                    showProgress();
                                    doHttp(RetrofitUtils.createApi(Url.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND_FANS_ONLY);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                    return;
                }
                SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onFansResult("手动导入成功", "-1");
                }

                doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, "手动成功", companyId, LOG_FLAG_SUCCESS_HAND, sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                if (type.equals("1")) {
                    flagHand = true;
                    getNumberOnly();
                }


            } else if (object.getString("result").equals("3001")) {
                showToast("没有号段");
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onFansResult("没有号段", "-1");
                }

            } else {
                LogTool.m("手动导号失败--返回" + object.getString("result"), 1);
                String content = wxId + "  手动导号失败--返回" + object.getString("result");
                doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE_HAND, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleFansByHand(String strReuslt) {
        try {
            org.json.JSONObject object = new org.json.JSONObject(strReuslt);
            if (object.getString("result").equals("0000")) {
                tvMainError.setText("");
                String latelyData = object.getString("latelyData");
                org.json.JSONObject object1 = new org.json.JSONObject(latelyData);
//                        String typeRemark = object1.getString("typeRemark");
                String type = object1.getString("type");
                String sendImportPhoneId = object1.getString("sendImportPhoneId");
                String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                if (sendImportPhoneId.equals(sp_sendImportPhoneId)) {
                    new AlertDialog.Builder(this)
                            .setTitle("您已经导过本次任务了,是否再导入?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //手动转发
                                    SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                                    showProgress();
                                    doHttp(RetrofitUtils.createApi(Url.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND_FANS);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                    return;
                }
                SPUtil.putAndApply(MainActivity.this, SEND_IMPORT_PHONE_ID, sendImportPhoneId);
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onFansResult("手动导入成功", "-1");
                }

                doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, "手动成功", companyId, LOG_FLAG_SUCCESS_HAND, sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                if (type.equals("1")) {
                    flagHand = true;
                    getNumber();
                }


            } else if (object.getString("result").equals("3001")) {
                showToast("没有号段");
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onFansResult("没有号段", "-1");
                }

            } else {
                LogTool.m("手动导号失败--返回" + object.getString("result"), 1);
                String content = wxId + "  手动导号失败--返回" + object.getString("result");
                doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE_HAND, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getNumberOnly() {
        showProgressDialog();
        doHttp(RetrofitUtils.createApi(Url.class).myList(companyId, companyuserclubId, province, city, companyClubId), HttpIdentifier.GET_PHONE_NUM_ONLY);

    }

    private void getNumber() {
        showProgressDialog();
        doHttp(RetrofitUtils.createApi(Url.class).myList(companyId, companyuserclubId, province, city, companyClubId), HttpIdentifier.GET_PHONE_NUM);
    }

    private void handleMaterialByHand(String strReuslt) {
        try {
            org.json.JSONObject object = new org.json.JSONObject(strReuslt);
            if (object.getString("result").equals("0000")) {
                String latelyData = object.getString("latelyData");
                org.json.JSONObject object1 = new org.json.JSONObject(latelyData);
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
                                    doHttp(RetrofitUtils.createApi(Url.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND_MATERIAL);
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                    progDialog.dismiss();
                    return;
                }
                SPUtil.putAndApply(MainActivity.this, SEND_COMPANY_CONTENT_ID, sendCompanyContentId);

                if (onLoadListener != null) {
                    String mType = "";
                    if (type.equals("1")) {
                        mType = "图文";
                    } else if (type.equals("2")) {
                        mType = "文章";
                    } else if (type.equals("3")) {
                        mType = "视频";
                    } else if (type.equals("4")) {
                        mType = "电台";
                    }
                    onLoadListener.onMaterialResult("手动转发类型:" + mType, "-1");
                }

                if (type.equals("1")) {//转发图文类型的
                    sendForPhotoText(content, picUrl);
                } else {
                    if (picUrl.indexOf(",") > 0) {
                        String[] pictures = picUrl.split(",");
                        picUrl = pictures[0];
                    }
//                    JpushReceiver.sContent = content;
                    WriteFileUtil.wrieFileUserIdByBufferedWriter(content, SavePath.SAVE_HTTP_CONTENT);
                    ShareUtils.sendToFriendsByHand(MainActivity.this,
                            url,
                            title,
                            title,
                            picUrl);

                }

            } else if (object.getString("result").equals("3001")) {
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogTool.m("没有需要转发的素材!", 0);
                showToast("没有需要转发的素材!");
            } else {
                LogTool.m("手动转发失败", 0);
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String content = wxId + "  手动转发失败--返回" + object.getString("result");
                doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_MATERIAL), HttpIdentifier.LOG);

            }
        } catch (JSONException e) {
            LogTool.m("手动转发失败" + e.toString(), 0);
            e.printStackTrace();
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

    private void sendForPhotoText(final String content, final String picUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                if (TextUtils.isNull(picUrl)) {
                    LogTool.d("素材图片为空");
                    if (onLoadListener != null) {
                        onLoadListener.onMaterialResult("素材图片是空的!", LOG_FLAG_FAILURE_HAND);
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
                        onLoadListener.onMaterialResult("手动请求成功", LOG_FLAG_SUCCESS_HAND);
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

    @OnClick({R.id.tv_company,R.id.tv_access, R.id.rl_main_log, R.id.sbtn_access, R.id.tv_update, R.id.iv_logout, R.id.btn_hand_fans, R.id.btn_hand_material, R.id.btn_hand_num_only, R.id.btn_hand_add_only})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_company:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: 222");
                        WorkManager.getInstance().setAccessibilitySettingOffAndOn();
                    }
                }).start();
                break;
            case R.id.tv_access:
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
                break;
            case R.id.rl_main_log:
                startActivity(LogActivity.class, null);
                break;
            case R.id.sbtn_access:
                Log.e(TAG, "onViewClicked:222 ");
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!sbtnAccess.isChecked()) {
                            setAccessOn();
                        } else {
                            setAccessOff();
                        }
                    }
                }).start();

                break;
            case R.id.btn_hand_material:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: 111");
                        WorkManager.getInstance().setAccessibilitySettingsOn();
                    }
                }).start();
                //手动转发
                showProgressDialog();
                doHttp(RetrofitUtils.createApi(Url.class).getLatelyArticle(companyId, wxId), HttpIdentifier.HAND_MATERIAL);

                break;
            case R.id.btn_hand_fans:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: 111");
                        WorkManager.getInstance().setAccessibilitySettingsOn();
                    }
                }).start();
                showProgress();
                doHttp(RetrofitUtils.createApi(Url.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND_FANS);
                break;
            case R.id.btn_hand_num_only:
                showProgress();
                doHttp(RetrofitUtils.createApi(Url.class).getLatelyDaohao(companyId, wxId), HttpIdentifier.HAND_FANS_ONLY);
                break;
            case R.id.btn_hand_add_only:
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setAccessOn();
                    }
                }).start();
                break;
            case R.id.iv_logout:
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
            case R.id.tv_update:
                checkUpdate();
                break;
        }
    }

    private void setAccessOn() {


        String[] shell = new String[]{"settings put secure enabled_accessibility_services com.shuangyou.material/com.shuangyou.material.service.MergeService"
                , "settings put secure accessibility_enabled 1"};
        int result = ShellUtils.execCommand(shell, true).result;
        Log.e(TAG, "onViewClicked: resulton-->" + result);
//        if (result == 0) {
        boolean isAccessOn = WorkManager.getInstance().isAccessibilitySettingsOn();
        Log.e(TAG, "setAccessOn: " + isAccessOn);
        if (!isAccessOn) {
            setAccessOn();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();

                    try {
                        AddByLinkMan.getInstence().jumpRemarkNum = 0;
                        AddByLinkMan.jumpRemarkNum = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LogTool.d("fans--开始加粉---判断辅助功能数" + AddByLinkMan.getInstence().jumpRemarkNum);
                    if (LoadResultUtil.onLoadListener != null) {
                        LoadResultUtil.onLoadListener.onFansResult("开始加粉", "-1");
                    }
                    timeWxCount = new TimeWxCount(1000 * 60 * 11, 1000);//TODO
                    timeWxCount.start();

                    JumpToWeChatUtil.jumpToLauncherUi();


                }
            });
        }

//        } else {
//            setAccessOn();
//        }
    }

    private void setAccessOff() {
        String[] shell = new String[]{"settings put secure enabled_accessibility_services com.shuangyou.material/com.shuangyou.material.service.MergeService"
                , "settings put secure accessibility_enabled 0"};
        int result = ShellUtils.execCommand(shell, true).result;
        Log.e(TAG, "onViewClicked: resultoff-->" + result);
        if (result == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    sbtnAccess.setChecked(false);
                }
            });
        } else {
            setAccessOff();
        }
    }

    @Override
    public void onMaterialResult(final String content, final String flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMainError.setText("");
                LogTool.m(content, 0);
                Log.e(TAG, "run:我到了 ");
                try {
                    progDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (content.contains("接收推送成功") || content.contains("手动请求成功")) {//以下是判断辅助功能假开的代码
                    Log.e(TAG, "error337: " + content);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_ACCESS), 1000 * 30);
                }
                tvMaterialContent.setText(content);
                tvMaterialTime.setText(DateUtils.formatDate(System.currentTimeMillis()));
                if (!flag.equals("-1")) {
                    String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                    doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId,
                            flag, sp_sendCompanyContentId, LOG_KIND_MATERIAL), HttpIdentifier.LOG);
                }

            }
        });

    }

    @Override
    public void onFansResult(final String content, final String flag) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (content.equals("getjpush")) {
                    frequency = flag;
                    tvMainError.setText("");
                    tvFansContent.setText("收到推送");
                    tvFansTime.setText(DateUtils.formatDate(System.currentTimeMillis()));
                    LogTool.m("收到推送", 1);
                    getNumber();
                    return;
                }
                tvMainError.setText("");
                tvFansContent.setText(content);
                LogTool.m(content, 1);
                tvFansTime.setText(DateUtils.formatDate(System.currentTimeMillis()));

                if (!flag.equals("-1")) {
                    String sp_sendImportPhoneId = (String) SPUtil.get(MainActivity.this, SEND_IMPORT_PHONE_ID, "");
                    doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, content, companyId, flag, sp_sendImportPhoneId, LOG_KIND_IMPORT), HttpIdentifier.LOG);

                }
            }
        });
    }

    @Override
    public void onReplace() {
//        LogTool.m("账号在其他设备登录", -1);
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    @Override
    public void onNetChanged(boolean isNet) {
        Log.e(TAG, "onNetChanged: " + isNet);
        if (!isNet) {
            LogTool.m("断网了!", -1);
            tvMainError.setText("网络断开,请重新开启!");
        } else {
            LogTool.m("网络又连上了!", -1);
            tvMainError.setText("");
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, wxId));
        }
    }

    @Override
    public void addedNum(int num) {
//        LogTool.m("已经加了" + num + "个好友", 1);
        LogTool.d("fans-已经加了" + num + "个好友");
        tvFansContent.setText("已经加了" + num + "个好友");
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
                        Toast.makeText(MainActivity.this, "网络错误!", Toast.LENGTH_SHORT).show();
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

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvFansClock.setText(millisUntilFinished / 1000 + " 秒后微信自动开启加粉");
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
            LogTool.d("fans--开始加粉---判断辅助功能数" + AddByLinkMan.getInstence().jumpRemarkNum);
            if (LoadResultUtil.onLoadListener != null) {
                LoadResultUtil.onLoadListener.onFansResult("开始加粉", "-1");
            }
            timeWxCount = new TimeWxCount(1000 * 60 * 10, 1000);//TODO
            timeWxCount.start();

            JumpToWeChatUtil.jumpToLauncherUi();


            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_ACCESS_FANS), 1000 * 30);
        }

    }

    class TimeWxCount extends CountDownTimer {

        public TimeWxCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            tvFansClock.setText("正在加粉," + l / 1000 + " 秒后结束");
        }

        @Override
        public void onFinish() {
            LogTool.d("fans--结束加粉--判断辅助功能数" + AddByLinkMan.getInstence().jumpRemarkNum);
            if (tvFansContent.getText().toString().trim().equals("开始加粉")) {

//                if (LoadResultUtil.onLoadListener != null) {
//                    LoadResultUtil.onLoadListener.onFansResult("辅助功能未开启", LOG_FLAG_ACCESS_OFF);
//                }
                LogTool.m("辅助功能未开启",1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WorkManager.getInstance().setAccessibilitySettingsOn();
                    }
                }).start();

            } else {
                if (tvFansContent.getText().toString().trim().contains("已经加了")) {
                    LogTool.m("加粉结束," + tvFansContent.getText().toString().trim(), 1);
                    String sp_sendCompanyContentId = (String) SPUtil.get(MainActivity.this, SEND_COMPANY_CONTENT_ID, "");
                    doHttp(RetrofitUtils.createApi(Url.class).save(LOG_TYPE_SHARE, wxId, tvFansContent.getText().toString().trim(), companyId,
                            LOG_FLAG_OTHER, sp_sendCompanyContentId, LOG_KIND_IMPORT), HttpIdentifier.LOG);
                }
                JumpToWeChatUtil.jumpToFansMainActivity();
                tvFansClock.setText("加粉结束");
            }

        }
    }
}
