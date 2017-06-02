package com.shuangyou.material.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



/**
 * Created by Vampire on 2017/5/29.
 */

public class UpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "UpdateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
            Log.e(TAG, "onReceive: 升级了" );
//            Toast.makeText(context, "下载完成,请重新打开软件!!!", Toast.LENGTH_LONG).show();
//            Intent intent2 = new Intent(context, CompanyActivity.class);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);

        }


        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.e(TAG, "onReceive: 删除了" );
            //DatabaseHelper dbhelper = new DatabaseHelper();
            //dbhelper.executeSql("delete from xxx");
        }
    }
}
