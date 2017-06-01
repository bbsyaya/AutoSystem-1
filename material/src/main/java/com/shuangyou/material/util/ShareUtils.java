package com.shuangyou.material.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.constant.HttpApi;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.shuangyou.material.util.DownPIcUtils.buildTransaction;


/**
 * Created by 焕焕 on 2017/5/15.
 */

public class ShareUtils {
    private static final String TAG = "ShareUtils";
    /**
     * 分享到微信（不是朋友圈）
     * */
    public static boolean shareMultipleToMomentsWeixin(Activity activity, String text, List<File> files) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra("Kdescription", text);

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : files) {
            imageUris.add(Uri.fromFile(f));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        try {
            activity.startActivity(intent);

        } catch (android.content.ActivityNotFoundException e) {
            return false;
        }
        return true;
    }
    /**
     * 分享到朋友圈
     * */
    public static boolean shareMultipleToMoments(Context activity, String text, List<File> files) {

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putExtra("Kdescription", text);

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for (File f : files) {
            imageUris.add(Uri.fromFile(f));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        LogTool.d("代码已经运行到这里 79");
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } catch (android.content.ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * 分享到微信
     */
    public static void sendToWeiXin(final Context context, String httpUrl,String title, String content,String imageUrl) {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = content;
        msg.description = title;
        Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bmp, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap thumbBmp =  Bitmap.createScaledBitmap(bmp, 30, 30, true);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                api.sendReq(req);
            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Toast.makeText(context, "图片加载错误！~", Toast.LENGTH_SHORT).show();
            }
        });

    }
//
//
//    /**
//     * 分享到朋友圈
//     */
    public static void sendToFriends(final Context context, String httpUrl,String title, String content,String imageUrl) {

        final IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = content;
        msg.description = title;
        Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bmp, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 30, 30, true);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                LogTool.d("图片加载错误,链接类型的转发失败---");
                Toast.makeText(context, "图片加载错误！~", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
