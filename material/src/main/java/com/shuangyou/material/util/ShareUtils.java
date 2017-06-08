package com.shuangyou.material.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.shuangyou.material.R;
import com.shuangyou.material.interfaces.KeyValue;
import com.shuangyou.material.receiver.JpushReceiver;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.shuangyou.material.util.DownPIcUtils.buildTransaction;
import static com.shuangyou.material.util.LoadResultUtil.onLoadListener;


/**
 * Created by 焕焕 on 2017/5/15.
 */

public class ShareUtils implements KeyValue{
    private static final String TAG = "ShareUtils";


    /**
     * 分享到微信（不是朋友圈）
     */
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
     */
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
            if (onLoadListener != null) {
                onLoadListener.onFailuer("图片加载错误");
            }
            return false;
        }

        return true;
    }

    /**
     * 分享到微信
     */
    public static void sendToWeiXin(final Context context, String httpUrl, String title, String content, String imageUrl) {
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
    public static  void sendToFriends(final Context context, String httpUrl, String title, String content, String imageUrl) {

        final IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = content;
        msg.description = title;
        Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {//后期可以加上延时的,那边网太慢
            @Override
            public void onResourceReady(Bitmap bmp, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 30, 30, true);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);

                if (onLoadListener != null) {
                    if (JpushReceiver.sFrequency.equals("2")) {
                        onLoadListener.onSuccess("第二次推送才成功",LOG_FLAG_SUCCESS_TWICE);
                    }else{
                        onLoadListener.onSuccess("一次性成功",LOG_FLAG_SUCCESS_ONCE);
                    }
                }

                return;
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                LogTool.d("图片加载错误,链接类型的转发失败---error");
                Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
                if (onLoadListener != null) {
                    if (JpushReceiver.sFrequency.equals("2")) {
                        onLoadListener.onSuccess("第二次推送才成功,但是图片加载错误,以logo为图片发送",LOG_FLAG_SUCCESS_TWICE_BAD);
                    }else{
                        onLoadListener.onSuccess("一次性成功,但是图片加载错误,以logo为图片发送",LOG_FLAG_SUCCESS_ONCE_BAD);
                    }
                }
                Toast.makeText(context, "图片加载错误！~", Toast.LENGTH_LONG).show();

            }
        });

    }

    //
//
//    /**
//     *  手动分享到朋友圈
//     */
    public static  void sendToFriendsByHand(final Context context, String httpUrl, String title, String content, String imageUrl) {

        final IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = httpUrl;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = content;
        msg.description = title;
        Glide.with(context).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {//后期可以加上延时的,那边网太慢
            @Override
            public void onResourceReady(Bitmap bmp, GlideAnimation<? super Bitmap> glideAnimation) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 30, 30, true);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);

                if (onLoadListener != null) {
                        onLoadListener.onSuccess("手动转发成功", LOG_FLAG_SUCCESS_HAND);
                }

                return;
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                LogTool.d("图片加载错误,链接类型的转发失败---error");
                Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo);
                msg.thumbData = DownPIcUtils.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
                if (onLoadListener != null) {
                        onLoadListener.onSuccess("手动转发成功,但是图片加载错误,以logo形式转发", LOG_FLAG_FAILURE_HAND);
                }
                Toast.makeText(context, "图片加载错误！~", Toast.LENGTH_LONG).show();

            }
        });

    }


    public static void shareToWxCircle(final Context context, String httpUrl, String title, String content, String imageUrl){
        Bitmap bitmap = null;
        try {
            LogTool.d("图片:" + imageUrl);
            bitmap = BitmapUtil.getBitmap(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();

            if (onLoadListener != null) {
                onLoadListener.onFailuer("图片加载错误--"+e.toString());
            }
            LogTool.d(e.toString());
        }
        byte[] bytes1 = BitmapUtil.Bitmap2Bytes(bitmap);
        shareToWX(context,bytes1, content, title, httpUrl);
    }

    public static void shareToWX(Context context,byte[] bytes, String content, String title, String url) {
        LogTool.d("149");
        final IWXAPI api = WXAPIFactory.createWXAPI(context, HttpApi.WX_APP_ID, true);
        api.registerApp(HttpApi.WX_APP_ID);
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(wxWebpageObject);
        msg.description = content;
        msg.title = title;
        msg.thumbData = bytes;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
//        if (onLoadListener != null) {
//            onLoadListener.onSuccess();
//        }
        LogTool.d("SEND_REQ");
    }
}
