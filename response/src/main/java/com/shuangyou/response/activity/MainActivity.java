package com.shuangyou.response.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shuangyou.response.R;
import com.shuangyou.response.utils.ResponseCallBackUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends  AppBaseActivity implements ResponseCallBackUtil.OnCallBackListener{

    @BindView(R.id.et_demo)
    EditText etDemo;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    public static String sReplyContent;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        ResponseCallBackUtil.setOnCallBackListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:

                break;
        }
    }

    @Override
    public void onCallBack(String content) {
        sReplyContent = "jujing";
    }
}
