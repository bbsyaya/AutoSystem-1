package com.rabbit.fans.activity;

import android.content.Intent;
import android.provider.Settings;
import android.widget.EditText;

import com.kidney_hospital.base.util.SPUtil;
import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.util.WorkManager;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Vampire on 2017/5/27.
 */

public class CompanyActivity extends AppBaseActivity implements KeyValue {
    public static final String IS_LOGIN = "is_login";
    @BindView(R.id.et_id)
    EditText etId;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        boolean isLogin = (boolean) SPUtil.get(this,IS_LOGIN,false);
        if (isLogin){
            startActivity(MainActivity.class,null);
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_company;
    }


    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        SPUtil.putAndApply(this,IS_LOGIN,true);
        String id = etId.getText().toString().trim();
        SPUtil.putAndApply(this,COMPANY_ID,id);
        startActivity(MainActivity.class,null);
        finish();
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
}
