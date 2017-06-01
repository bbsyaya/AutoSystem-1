package com.vampire.shareforwechat.activity.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.thread.ThreadPool;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.AppBaseActivity;
import com.vampire.shareforwechat.constant.HttpIdentifier;
import com.vampire.shareforwechat.interfaces.KeyValue;
import com.vampire.shareforwechat.manager.WorkManager;
import com.vampire.shareforwechat.model.group.ContentBean;
import com.vampire.shareforwechat.model.group.ContentTimeBean;
import com.vampire.shareforwechat.network.GroupControlUrl;
import com.vampire.shareforwechat.service.GroupControlService;
import com.vampire.shareforwechat.util.DownPIcUtils;
import com.vampire.shareforwechat.util.RetrofitUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 群控
 * Created by Vampire on 2017/5/27.
 */

public class GroupControlActivity extends AppBaseActivity implements KeyValue {
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h

    private static final String TAG = "GroupControlActivity";
    @BindView(R.id.tv_result)
    TextView tvResult;

    @Override
    protected void loadData() {
        ThreadPool.thredP.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getContentTime(), HttpIdentifier.GROUP_GET_CONTENT_TIME);
                        Thread.sleep(INTERVAL);//一天之后再请求一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).findContentById(11), HttpIdentifier.FIND_CONTENT_BY_ID);
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        switch (identifier) {
            case ERROR:
                tvResult.setText("获取失败,点击重新连接");
                break;
            case HttpIdentifier.GROUP_GET_CONTENT_TIME:
                ContentTimeBean bean = JSONObject.parseObject(strReuslt, ContentTimeBean.class);
                if (HttpIdentifier.REQUEST_SUCCESS.equals(bean.getResult())) {

                    int id = bean.getList().get(0).getId();
                    SPUtil.putAndApply(this, GROUP_ID, id);
                    String date = bean.getList().get(0).getDate();
                    String[] dateSplit = date.split("-");
                    int year = Integer.parseInt(dateSplit[0]);
                    int month = Integer.parseInt(dateSplit[1]);
                    int day = Integer.parseInt(dateSplit[2]);
                    SPUtil.putAndApply(this, GROUP_YEAR, year);
                    SPUtil.putAndApply(this, GROUP_MONTH, month);
                    SPUtil.putAndApply(this, GROUP_DAY, day);
                    String time = bean.getList().get(0).getTime();
                    String[] timeSplit = time.split(":");
                    int hour = Integer.parseInt(timeSplit[0]);
                    int minute = (int) (Math.random() * 60);
                    int second = (int) (Math.random() * 60);
                    SPUtil.putAndApply(this, GROUP_HOUR, hour);
                    SPUtil.putAndApply(this, GROUP_MINUTE, minute);
                    SPUtil.putAndApply(this, GROUP_SECOND, second);
                    tvResult.setText("获取成功,转发的时间是:\n"+date+" "+hour+":"+minute+":"+second);


                    startService(new Intent(this, GroupControlService.class));//后期加到欢迎页上

                } else{
                    tvResult.setText("获取失败,点击重新连接");
                }
                break;

        }
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
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_control;
    }




    @OnClick(R.id.tv_result)
    public void onViewClicked() {
        if (tvResult.getText().toString().trim().equals("获取失败,点击重新连接")){
            doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getContentTime(), HttpIdentifier.GROUP_GET_CONTENT_TIME);

        }
    }
}
