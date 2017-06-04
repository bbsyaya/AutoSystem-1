package com.shuangyou.material.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kidney_hospital.base.constant.HttpApi;
import com.kidney_hospital.base.util.exceptioncatch.LogTool;
import com.kidney_hospital.base.util.wechat.LoadResultUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by 焕焕 on 2017/5/19.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    IWXAPI api;

    private void handleIntent(Intent intent) {
        api.handleIntent(intent, this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, HttpApi.WX_APP_ID);
        handleIntent(getIntent());
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e(TAG, "onReq: " + baseReq);
    }

    @Override
    public void onResp(BaseResp resp) {
        // TODO Auto-generated method stub

        Bundle bundle = new Bundle();
        Log.e("errCode 42", resp.errCode + "");
        LogTool.d("errCode 49" + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 分享收藏的回调方法
                if (resp instanceof SendMessageToWX.Resp) {

//				Intent intent = new Intent(this, MainActivity.class);
//				startActivity(intent);
                    LogTool.d("链接的代码到这里就说明转发成功了");
                    Toast.makeText(this, " 转发成功!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    // 微信第三方登录的回调方法
                    // 获取code,方便以后获取临时票据access_token

                    resp.toBundle(bundle);
                    SendAuth.Resp sp = new SendAuth.Resp(bundle);
                    String code = sp.code;
                    Log.e("code", code);

//				 getResult(code);

                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (LoadResultUtil.onLoadListener != null) {
                    LoadResultUtil.onLoadListener.onFailuer("错误码:" + resp.errCode);
                }

                finish();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }
}
