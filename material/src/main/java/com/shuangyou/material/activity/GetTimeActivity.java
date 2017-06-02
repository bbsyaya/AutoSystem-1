package com.shuangyou.material.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.interfaces.OnLoadListener;
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.network.GroupControlUrl;
import com.shuangyou.material.receiver.JpushReceiver;
import com.shuangyou.material.service.CheckUpdateService;
import com.shuangyou.material.util.ShareUtils;
import com.shuangyou.material.util.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Vampire on 2017/5/31.
 */

public class GetTimeActivity extends AppBaseActivity implements OnReceiveTimeListener, OnLoadListener, KeyValue {
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
    private String imei = "";
    private String sn = "";
//    private String single= "";

    private String companyId;
    private String userId;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        userId = (String) SPUtil.get(this, USER_ID, "");
        initJpush();
        JpushReceiver.setOnReceiveTimeListener(this);
        ShareUtils.setOnLoadListener(this);
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this));

        companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        tvCompany.setText("企业码:" + companyId);
    }

    private void initJpush() {
//        TelephonyManager telephonyManager = (TelephonyManager) this
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        imei = telephonyManager.getDeviceId();
//        sn = Build.SERIAL;
//        if (TextUtils.isNull(imei)) {
//            single = sn;
//        } else {
//            if (imei.length() > 6) {
//                imei = imei.substring(imei.length() - 5, imei.length());
//                single = imei + sn;
//            }else{
//                single =sn;
//            }
//        }
        Log.e(TAG, "initJpush single: " + userId);
        tvDevice.setText("标识码:" + userId);
        Set<String> set = new HashSet<>();
        set.add(userId);//手机的机器码
        JPushInterface.setAliasAndTags(this, userId, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.e(TAG, "code==" + i);
                LogTool.d("极光返回码------>" + i);
                if (i != 0) {
                    String content = "极光集成失败,错误码为:" + i;
                    tvError.setText(content);
                    doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("4", userId, content, companyId, "1"), HttpIdentifier.LOG);
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
            //打开辅助功能
            Intent service = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(service);
        }
    }

    @Override
    public void onReceiveTime(String time) {
        tvResult.setText(time);
    }

    @Override
    public void onSuccess() {
        //转发成功的回调

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String content = userId + "  转发成功";
                tvError.setText("转发成功");
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("4", userId, content, companyId, "2"), HttpIdentifier.LOG);

            }
        });

    }

    @Override
    public void onFailuer(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //转发失败的回调
                String content = userId + "  转发失败--" + error;
                tvError.setText("  转发失败--" + error);
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("4", userId, content, companyId, "1"), HttpIdentifier.LOG);

            }
        });

    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: "+strReuslt );
        switch (identifier) {
            case HttpIdentifier.UNINSTALL:
                try {
                    JSONObject object = new JSONObject(strReuslt);
                    if (object.getString("result").equals("0000")){
                        showToast("成功!你可以卸载软件了!");
                    }else{
                        showToast("服务器异常");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @OnClick({R.id.tv_version, R.id.tv_uninstall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_version:
                showLongToast("请看通知栏,不要多次点击!如果有新版本的话,会有显示");
                //点击更新
                startService(new Intent(this, CheckUpdateService.class));
                break;
            case R.id.tv_uninstall:
                new AlertDialog.Builder(this)
                        .setTitle("确定要卸载吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgress();
                                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).delCompanyuser(userId, companyId), HttpIdentifier.UNINSTALL);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }
}
