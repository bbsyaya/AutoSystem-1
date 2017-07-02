package com.shuangyou.material.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shuangyou.material.activity.LoginActivity;


//系统启动接受器
public class BootReceiver extends BroadcastReceiver {
	final String TAG = "KEYEJ";

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (arg1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			// ipc = new Ipc(arg0);
			//
			// ipc.startService();
			// Log.d(TAG, "service is start");

			Intent intent2 = new Intent(context, LoginActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
		}

	}

}
