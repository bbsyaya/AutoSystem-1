package com.shuangyou.material.activity;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.interfaces.OnReceiveTimeListener;
import com.shuangyou.material.receiver.JpushReceiver;
import com.shuangyou.material.util.WorkManager;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Vampire on 2017/5/31.
 */

public class GetTimeActivity extends AppBaseActivity implements OnReceiveTimeListener,KeyValue{
    private static final String TAG ="GetTimeActivity";
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        initJpush();
        JpushReceiver.setOnReceiveTimeListener(this);
        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this));

        String companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        tvCompany.setText("企业码是:"+companyId);
    }
    private void initJpush() {
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (imei==null){
            imei = android.os.Build.SERIAL;
        }
        tvDevice.setText("机器码是:"+imei);
        Set<String> set = new HashSet<>();
        set.add(imei);//手机的机器码
        JPushInterface.setAliasAndTags(this, imei, set, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.e(TAG, "code==" + i);
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
}
