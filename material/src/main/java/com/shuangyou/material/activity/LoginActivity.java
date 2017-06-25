package com.shuangyou.material.activity;

import android.util.Log;
import android.widget.EditText;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.TextUtils;
import com.kidney_hospital.base.util.exceptioncatch.WriteFileUtil;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.network.GroupControlUrl;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * 登录
 * Created by Vampire on 2017/6/5.
 */

public class LoginActivity extends AppBaseActivity implements KeyValue{
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
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: "+strReuslt );
        switch (identifier){
            case HttpIdentifier.LOGIN:
                try {
                    JSONObject jsonObject = new JSONObject(strReuslt);
                    String result = jsonObject.getString("result");
                    if (result.equals("0000")){
                        showToast("登录成功");
                        String content = wxId+ "  登录成功";
                        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_SUCCESS_ONCE,"null",LOG_KIND_MATERIAL), HttpIdentifier.LOG);
                        SPUtil.putAndApply(this, IS_LOGIN, true);
                        SPUtil.putAndApply(this, COMPANY_ID, companyId);
                        startActivity(MainActivity.class, null);
                        finish();
                    }else{
                        showToast(jsonObject.getString("retMessage"));
                        if (result.equals("9999")||result.equals("10000")){
                            String content = wxId + "  登录失败--返回-" + result;
                            doHttp(RetrofitUtils.createApi(GroupControlUrl.class).save(LOG_TYPE_LOGIN, wxId, content, companyId, LOG_FLAG_FAILURE,"null",LOG_KIND_MATERIAL), HttpIdentifier.LOG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ERROR:
                showToast("连接超时,请检查网络!");
                break;
        }
    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {

        companyId = etCompanyId.getText().toString().trim();
        wxId = etAccount.getText().toString().trim();
        String wxPsw = etPsw.getText().toString().trim();
        String registrationID = JPushInterface.getRegistrationID(this);
        if (TextUtils.isNull(companyId)){
            showToast("请输入企业ID");
            return;
        }
        if (TextUtils.isNull(wxId)){
            showToast("请输入微信号!");
            return;
        }
        if (TextUtils.isNull(wxPsw)){
            showToast("请输入密码!");
            return;
        }
        if (TextUtils.isNull(registrationID)){
            showToast("推送注册Id获取失败!");
            return;
        }
        File file_wxId = new File(SavePath.SAVE_WX_ID);
        if (file_wxId.exists()) {
            file_wxId.delete();
        }
        File file_wxPsw = new File(SavePath.SAVE_WX_PSW);
        if (file_wxPsw.exists()){
            file_wxPsw.delete();
        }
        WriteFileUtil.wrieFileUserIdByBufferedWriter(wxId, SavePath.SAVE_WX_ID);
        WriteFileUtil.wrieFileUserIdByBufferedWriter(wxPsw,SavePath.SAVE_WX_PSW);
        showProgress();
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).login(wxId, companyId, wxPsw,registrationID,LOG_KIND_MATERIAL), HttpIdentifier. LOGIN);

    }
}
