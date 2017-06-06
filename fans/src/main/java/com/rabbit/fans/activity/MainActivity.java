package com.rabbit.fans.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.wechat.LoadResultUtil;
import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.util.WorkManager;
import com.tinkerpatch.sdk.TinkerPatch;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MainActivity extends AppBaseActivity implements KeyValue,LoadResultUtil.OnLoadListener {
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

    @Override
    protected void loadData() {
//        startService(new Intent(this, GetNumAlarmService.class));

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                String[] shell = new String[]{"am start -n com.tencent.mm/com.tencent.mm.ui.LauncherUI"};
//                int i = ShellUtils.execCommand(shell, true).result;
//
//            }
//        }, 1000*5);//先是10秒之后打开微信

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
        Log.e(TAG, "initViews 73:"+patchVersion );
        wxId = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_ID);
        initJpush();
//        JpushReceiver.setOnReceiveTimeListener(this);
        LoadResultUtil.setOnLoadListener(this);
        String registrationId = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(registrationId)) {
            tvRegistrationId.setText("推送注册Id获取失败");
        } else {
            tvRegistrationId.setText("推送注册Id:" + registrationId);
        }
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this)+"_"+patchVersion);

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
//                    doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_SHARE, wxId, content, companyId, LOG_FLAG_FAILURE,null), HttpIdentifier.LOG);
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

        } else {
            tvIsOpen.setText("辅助功能已开启!");
        }
    }

    @Override
    public void onSuccess(String error) {

    }

    @Override
    public void onFailuer(String error) {

    }

    @Override
    public void onUpdate(String str) {

    }
    @OnClick({R.id.tv_is_open, R.id.tv_version, R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_is_open:
//                TinkerPatch.with().fetchPatchUpdate(true);
                //打开辅助功能
                Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(service);
                break;
            case R.id.tv_version:
                showToast("补丁后台下载,不要连续点击,请稍等片刻...");
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

}
