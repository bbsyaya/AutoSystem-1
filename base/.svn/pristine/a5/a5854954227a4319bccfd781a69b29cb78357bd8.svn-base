package com.kidney_hospital.base.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.callback.AuthorizationProvider;
import com.kidney_hospital.base.callback.OnResponseCallback;
import com.kidney_hospital.base.config.DefaultConfig;
import com.kidney_hospital.base.model.AuthorizationHelper;
import com.kidney_hospital.base.model.UserInfo;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity基础类
 */
public abstract class BaseActivity extends AppCompatActivity implements OnResponseCallback, AuthorizationProvider {
    public static final int ERROR = 0;
    private AuthorizationHelper mHelper;
    private Dialog mDialog;


    public AuthorizationHelper getHelper() {
        if (mHelper == null) {
            mHelper = new AuthorizationHelper(this);
        }
        return mHelper;
    }

    @Override
    public UserInfo.DbBean getAuthData() {
        return getHelper().getAuthorization();
    }

    @Override
    public boolean isLogin() {
        return getHelper().isLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    /**
     * 创建进度条对话框
     */
    protected Dialog onCreateProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在努力加载......");
        //设置进度条是否可以按退回键取消  
        dialog.setCancelable(true);
        //设置点击进度对话框外的区域对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 显示进度条对话框
     */
    public void showProgress() {
        if (mDialog == null) {
            mDialog = onCreateProgressDialog();
            if (mDialog == null) {
                return;
            }
        }
        mDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void hideProgress() {
        if (isProgressing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 进度条是否在显示
     */
    public boolean isProgressing() {
        return (mDialog != null && mDialog.isShowing());
    }

    @Override
    public void onBackPressed() {
        if (isProgressing()) {
            hideProgress();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 显示一个Toast
     *
     * @param toast toast内容
     */
    protected void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个长时间的Toast
     *
     * @param toast toast内容
     */
    protected void showLongToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }



    @Override
    public void onResponse(int identifier, String strReuslt) {

    }


}
