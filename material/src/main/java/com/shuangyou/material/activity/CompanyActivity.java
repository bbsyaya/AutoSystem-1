package com.shuangyou.material.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;

import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.network.GroupControlUrl;

import org.json.JSONObject;

import java.util.Random;

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
    private String imei = "";
    private String sn = "";
    private String id;
//    private String single = "";



    private String userId;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {

        boolean isLogin = (boolean) SPUtil.get(this, IS_LOGIN, false);
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = telephonyManager.getLine1Number();
        Log.e(TAG, "initViews phoneNum: "+phoneNum );
        imei = telephonyManager.getDeviceId();
        sn = android.os.Build.SERIAL;
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
        if (!isLogin) {
            if (TextUtils.isNull(sn)){
                sn = System.currentTimeMillis()+"";
            }
            userId = sn+ new Random().nextInt(1000)+1;
            SPUtil.putAndApply(this, USER_ID, userId);
        }else{
            userId = (String) SPUtil.get(this,USER_ID,"");
        }
        Log.e(TAG, "initViews single: " + userId);

//        if (isLogin) {
//            startActivity(GetTimeActivity.class, null);
//            finish();
//        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_company;
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        LogTool.d("注册的 json" + strReuslt);
        switch (identifier) {
            case HttpIdentifier.REGISTER:

                try {
                    JSONObject jsonObject = new JSONObject(strReuslt);
                    if (jsonObject.getString("result").equals("0000")) {
                        showLongToast("注册成功");
                        String content = userId + "  注册成功";
                        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("2", userId, content, id, "2"), HttpIdentifier.LOG);
                        SPUtil.putAndApply(this, IS_LOGIN, true);
                        SPUtil.putAndApply(this, COMPANY_ID, id);
                        startActivity(GetTimeActivity.class, null);
                        finish();
                    } else {
                        if (jsonObject.getString("result").equals("1000")) {
                            showToast("企业不存在!");
                            return;
                        }
                        showLongToast("注册失败");
                        String content = userId + "  注册失败--返回-" + jsonObject.getString("result");
                        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("2", userId, content, id, "1"), HttpIdentifier.LOG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case HttpIdentifier.LOG:
                Log.e(TAG, "log: " + strReuslt);
                break;
            case ERROR:
                String content2 = userId + "  注册失败--没有网";// 6.3新添加的--
                showToast("服务器异常,请检查网络!");
                doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save("2", userId, content2, id, "1"), HttpIdentifier.LOG);
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
        if (TextUtils.isNull(userId)){
            showToast("没获取到标识符");
            return;
        }
        showProgress();
        Log.e(TAG, "onViewClicked: " + resultId);
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).register(userId, id, resultId), HttpIdentifier.REGISTER);


    }
}
