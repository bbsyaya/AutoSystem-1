package com.shuangyou.material.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;

import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.network.GroupControlUrl;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
/**
 * Created by Vampire on 2017/5/27.
 */

public class CompanyActivity extends AppBaseActivity implements KeyValue {
    public static final String IS_LOGIN = "is_login";
    private static final String TAG = "CompanyActivity";
    @BindView(R.id.et_id)
    EditText etId;
    private String imei;
    private String id;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {


        boolean isLogin = (boolean) SPUtil.get(this, IS_LOGIN, false);
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        Log.e(TAG, "initViews imei: "+imei );
        if (imei==null){
            imei = android.os.Build.SERIAL;
        }
        if (isLogin) {
            startActivity(GetTimeActivity.class, null);
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_company;
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case HttpIdentifier.REGISTER:

                SPUtil.putAndApply(this, IS_LOGIN, true);
                SPUtil.putAndApply(this, COMPANY_ID, id);
                startActivity(GetTimeActivity.class, null);
                finish();
                break;
        }
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {


        id = etId.getText().toString().trim();
        if (TextUtils.isNull(id)) {
            showToast("请输入企业id!");
            return;
        }
        String resultId = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(resultId)) {
            showToast("没获取到推送 id");
            return;
        }
        showProgress();
        Log.e(TAG, "onViewClicked: " + resultId);
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).register(imei, id, resultId), HttpIdentifier.REGISTER);


    }
}
