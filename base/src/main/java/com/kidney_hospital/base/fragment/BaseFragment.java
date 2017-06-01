package com.kidney_hospital.base.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.kidney_hospital.base.callback.AuthorizationProvider;
import com.kidney_hospital.base.callback.OnResponseCallback;
import com.kidney_hospital.base.model.AuthorizationHelper;
import com.kidney_hospital.base.model.UserInfo;

/**
 * Fragment 基础类
 * Created by Yuanyx on 2016/8/16.
 */
public class BaseFragment extends Fragment implements OnResponseCallback, AuthorizationProvider {
    private Dialog mDialog;
    private AuthorizationHelper mHelper;

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgress();
    }

    protected AuthorizationHelper getHelper() {
        if (mHelper == null) {
            mHelper = new AuthorizationHelper(getActivity());
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mDialog = onCreateProgressDialog(context);
    }

    /**
     * 创建进度条对话框
     */
    protected Dialog onCreateProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("请稍等......");
        //设置进度条是否可以按退回键取消  
        dialog.setCancelable(false);
        //设置点击进度对话框外的区域对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    /**
     * 显示进度条对话框
     */
    public void showProgress() {
        if (mDialog == null) {
            return;
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

    /**
     * 显示一个Toast
     *
     * @param toast toast内容
     */
    protected void showToast(String toast) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 显示一个长时间的Toast
     *
     * @param toast toast内容
     */
    protected void showLongToast(String toast) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onResponse(int identifier, String strReuslt) {

    }
}
