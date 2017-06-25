package com.rabbit.fans.activity;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.model.UserInfo;
import com.rabbit.fans.network.PhoneUrl;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Vampire on 2017/6/6.
 */

public class LoginActivity extends AppBaseActivity implements KeyValue {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.et_company_id)
    EditText etCompanyId;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_psw)
    EditText etPsw;
    String wxId;
    String companyId;

    @Override
    protected void loadData() {
        SharedPreferences sp = getSharedPreferences("markypq", MODE_WORLD_READABLE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("lat", "37.943782");  //114.470225,37.943782
        e.putString("lon", "114.470225");
        e.commit();
    }

    @Override
    protected void initViews() {
        String local_wxId = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_ID);
        String local_wxPsw = WriteFileUtil.readFileByBufferReader(SavePath.SAVE_WX_PSW);
        etAccount.setText(local_wxId);
        etPsw.setText(local_wxPsw);
        boolean isLogin = (boolean) SPUtil.get(this, IS_LOGIN, false);
        if (isLogin) {
            startActivity(MainActivity.class, null);
            finish();
        }

//        LoadResultUtil.setOnLoadListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case HttpIdentifier.LOGIN:
                UserInfo userInfo = JSONObject.parseObject(strReuslt, UserInfo.class);
                if (userInfo.getResult() == null) {
                    showToast("网络异常");
                    return;
                }
                if (userInfo.getResult().equals(HttpIdentifier.REQUEST_SUCCESS)) {
                    showToast("登录成功");
                    String content = wxId + "  登录成功";
                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
                    SPUtil.putAndApply(this, IS_LOGIN, true);
                    SPUtil.putAndApply(this, COMPANY_ID, companyId);
                    SPUtil.putAndApply(this, CITY, userInfo.getDb().getCity());
                    SPUtil.putAndApply(this, PROVINCE, userInfo.getDb().getProvince());
                    SPUtil.putAndApply(this, COMPANY_USER_CLUB_ID, userInfo.getDb().getCompanyuserclubId() + "");
                    SPUtil.putAndApply(this, COMPANY_CLUB_ID, userInfo.getDb().getCompanyClubId() + "");
                    startActivity(MainActivity.class, null);
                    finish();
                } else {
                    showToast(userInfo.getRetMessage());
                    String content = wxId + "  登录失败--返回-" + userInfo.getResult();
                    doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
                }

//                try {
//                    JSONObject jsonObject = new JSONObject(strReuslt);
//                    String result = jsonObject.getString("result");
//                    if (result.equals("0000")) {
//                        showToast("登录成功");
//                        String content = wxId + "  登录成功";
//                        doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
//                        SPUtil.putAndApply(this, IS_LOGIN, true);
//                        SPUtil.putAndApply(this, COMPANY_ID, companyId);
//                        startActivity(MainActivity.class, null);
//                        finish();
//                    } else {
//                        showToast(jsonObject.getString("retMessage"));
//                        if (result.equals("9999") || result.equals("10000")) {
//                            String content = wxId + "  登录失败--返回-" + result;
//                            doHttp(RetrofitUtils.createApi(PhoneUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_FAILURE, "null", LOG_KIND_IMPORT), HttpIdentifier.LOG);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            case ERROR:
                showToast("连接超时,请检查网络!");
                break;
        }
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
//        TinkerPatch.with().fetchPatchUpdate(true);

        companyId = etCompanyId.getText().toString().trim();
        wxId = etAccount.getText().toString().trim();
        String wxPsw = etPsw.getText().toString().trim();
        String registrationID = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(companyId)) {
            showToast("请输入企业ID");
            return;
        }
        if (TextUtils.isNull(wxId)) {
            showToast("请输入微信号!");
            return;
        }
        if (TextUtils.isNull(wxPsw)) {
            showToast("请输入密码!");
            return;
        }
        if (TextUtils.isNull(registrationID)) {
            showToast("推送注册Id获取失败!");
            return;
        }
        File file_wxId = new File(SavePath.SAVE_WX_ID);
        if (file_wxId.exists()) {
            file_wxId.delete();
        }
        File file_wxPsw = new File(SavePath.SAVE_WX_PSW);
        if (file_wxPsw.exists()) {
            file_wxPsw.delete();
        }
        WriteFileUtil.wrieFileUserIdByBufferedWriter(wxId, SavePath.SAVE_WX_ID);
        WriteFileUtil.wrieFileUserIdByBufferedWriter(wxPsw, SavePath.SAVE_WX_PSW);
        showProgress();
        doHttp(RetrofitUtils.createApi(PhoneUrl.class).login(wxId, companyId, wxPsw, registrationID, LOG_KIND_IMPORT), HttpIdentifier.LOGIN);
    }


//    @Override
//    public void onUpdate(String str) {
//        Intent intent = new Intent(this,LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
//    }
}
