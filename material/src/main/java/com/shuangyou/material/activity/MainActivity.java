package com.shuangyou.material.activity;

import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.util.AppUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.thread.ThreadPool;
import com.shuangyou.material.R;
import com.kidney_hospital.base.constant.HttpIdentifier;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.model.ContentTimeBean;
import com.shuangyou.material.model.TimeBean;
import com.shuangyou.material.network.GroupControlUrl;
import com.shuangyou.material.receiver.JpushReceiver;
import com.shuangyou.material.service.ControlService;
import com.kidney_hospital.base.util.server.RetrofitUtils;
import com.shuangyou.material.util.WorkManager;

import org.litepal.crud.DataSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.kidney_hospital.base.util.wechat.PerformClickUtils.sleep;

/**
 * Created by Vampire on 2017/5/28.
 */

public class MainActivity extends AppBaseActivity implements KeyValue {
    private static final int INTERVAL = 1000 * 60 * 60;//10分钟请求一次时间
    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void initViews() {
        initJpush();

        tvVersion.setText("版本名: " + AppUtils.getVersionCode(this));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_control;
    }

    @Override
    protected void loadData() {

        ThreadPool.thredP.execute(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        LogTool.d("请求时间27");
                        Log.e(TAG, "run: 请求时间64");
                        getTimeFromUrl();
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void getTimeFromUrl() {
        String companyId = (String) SPUtil.get(this, COMPANY_ID, "1");
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).getContentTime(companyId), HttpIdentifier.GROUP_GET_CONTENT_TIME);
    }


    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
        LogTool.d("getTimeJson-----" + strReuslt);
        switch (identifier) {
            case ERROR:
                tvResult.setText("获取失败,点击重新连接");
                break;
            case HttpIdentifier.GROUP_GET_CONTENT_TIME:
                ContentTimeBean bean = JSONObject.parseObject(strReuslt, ContentTimeBean.class);
                if (HttpIdentifier.REQUEST_SUCCESS.equals(bean.getResult())) {
                    if (bean.getList().size() == 0) {
                        tvResult.setText("没数据");
                        return;
                    }
                    ContentTimeBean.ListBean listBean = bean.getList().get(0);
                    TimeBean timeBean = new TimeBean();
                    timeBean.setGroupId(listBean.getId());//存储 id
                    String time = listBean.getTime();
                    String[] timeSplit = time.split(":");
                    int hour = Integer.parseInt(timeSplit[0]);
                    timeBean.setHour(hour);//存储小时
                    int minute = Integer.parseInt(timeSplit[1]);
                    int tenNum = minute / 10;//十位数
                    int bitNum = (int) (Math.random() * 10);//个位数 随机0-10
                    minute = tenNum * 10 + bitNum;//获取新的分钟数
                    Log.e(TAG, "onResponse: minute----" + minute);
                    timeBean.setMinute(minute);//存储分钟
                    int second = (int) (Math.random() * 60);//随机获取秒钟
                    timeBean.setSecond(second);//存储秒钟
                    List<TimeBean> beans = DataSupport.where("groupId=?", listBean.getId()).find(TimeBean.class);
                    Log.e(TAG, "onResponse beans: " + beans.size());
                    if (beans.size() == 0) {
                        timeBean.save();
                        tvResult.setText("转发的时间是:\n" + hour + ":" + minute + ":" + second);
                        startService(new Intent(this, ControlService.class));
                    } else {
                        Log.e(TAG, "onResponse: 到这里了136");
                        tvResult.setText("转发的时间是:\n" + beans.get(0).getHour() + ":" + beans.get(0).getMinute() + ":" + beans.get(0).getSecond());
                    }


                } else {

                    tvResult.setText("获取失败,点击重新连接");
                }
                break;
        }
    }

    @OnClick(R.id.tv_result)
    public void onViewClicked() {
        if (tvResult.getText().toString().trim().equals("获取失败,点击重新连接")) {
            getTimeFromUrl();
        }
    }

    private void initJpush() {
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
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
