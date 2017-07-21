package com.kidney_hospital.base.util;

import android.content.ComponentName;
import android.content.Intent;

import com.kidney_hospital.base.BaseApp;

/**
 * Created by Vampire on 2017/5/8.
 */

public class JumpToWeChatUtil {

    public static void jumpToLauncherUi() {


        Intent intnet = new Intent();
        intnet.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
        intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getContext().startActivity(intnet);
    }

    public static void jumpToChattingUi(String userName) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.chatting.ChattingUI");
        intent.setComponent(comp);
        intent.putExtra("Chat_User", userName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getContext().startActivity(intent);
    }


    public static void jumpToFansMainActivity() {
        Intent intnet = new Intent();
        intnet.setComponent(new ComponentName("com.shuangyou.material", "com.shuangyou.material.activity.MainActivity"));
        intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getContext().startActivity(intnet);
    }

    public static void jumpToMaterialMainActivity() {
        Intent intnet = new Intent();
        intnet.setComponent(new ComponentName("com.shuangyou.material", "com.shuangyou.material.activity.MainActivity"));
        intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getContext().startActivity(intnet);
    }
}
