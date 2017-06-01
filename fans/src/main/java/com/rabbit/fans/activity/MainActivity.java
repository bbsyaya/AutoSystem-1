package com.rabbit.fans.activity;


import android.content.Intent;
import android.provider.Settings;

import com.rabbit.fans.R;
import com.rabbit.fans.interfaces.KeyValue;
import com.rabbit.fans.service.GetNumAlarmService;
import com.rabbit.fans.util.WorkManager;


public class MainActivity extends AppBaseActivity implements KeyValue {


    private static final String TAG = "MainActivity";

    @Override
    protected void loadData() {
        startService(new Intent(this, GetNumAlarmService.class));

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                String[] shell = new String[]{"am start -n com.tencent.mm/com.tencent.mm.ui.LauncherUI"};
//                int i = ShellUtils.execCommand(shell, true).result;
//
//            }
//        }, 1000*5);//先是10秒之后打开微信

    }


    @Override
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
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
