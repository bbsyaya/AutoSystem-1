package com.vampire.shareforwechat.activity.group;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.FileUtils;
import com.kidney_hospital.base.util.SPUtil;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.AppBaseActivity;
import com.vampire.shareforwechat.constant.HttpIdentifier;
import com.vampire.shareforwechat.interfaces.KeyValue;
import com.vampire.shareforwechat.model.group.ContentBean;
import com.vampire.shareforwechat.network.GroupControlUrl;
import com.vampire.shareforwechat.util.DownPIcUtils;
import com.vampire.shareforwechat.util.RetrofitUtils;
import com.vampire.shareforwechat.util.ShareUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.type;

/**
 * Created by Vampire on 2017/5/27.
 */

public class GroupSendingActivity extends AppBaseActivity implements KeyValue {
    public List<File> filePictures = new ArrayList<>();
    @BindView(R.id.tv_result)
    TextView tvResult;
    private int id;//转发素材的 id

    @Override
    protected void loadData() {
        id = (int) SPUtil.get(this, GROUP_ID, -1);
        doHttp(RetrofitUtils.createApi(GroupControlUrl.class).findContentById(id), HttpIdentifier.FIND_CONTENT_BY_ID);
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        switch (identifier) {
            case ERROR:
                tvResult.setText("获取素材失败,点击重新获取");
                break;
            case HttpIdentifier.FIND_CONTENT_BY_ID:
                final ContentBean contentBean = JSONObject.parseObject(strReuslt, ContentBean.class);
                if (HttpIdentifier.REQUEST_SUCCESS.equals(contentBean.getResult())) {
                    tvResult.setText("获取素材成功");
                    int type = contentBean.getList().get(0).getType();
                    if (type == 1) {//转发图文的
                        sendForPhotoText(contentBean);
                    } else  {//转发链接的
                        sendForUrl(contentBean);//转发链接类型的
                    }
                } else {
                    tvResult.setText("获取素材失败,点击重新获取");
                }
                break;
        }
    }

    private void sendForUrl(ContentBean contentBean) {
        String url = contentBean.getList().get(0).getUrl();
        String picUrl = contentBean.getList().get(0).getPicUrl();
        String content = contentBean.getList().get(0).getContent();
        ShareUtils.sendToFriends(GroupSendingActivity.this,
                url,
                content,
                content,
                picUrl);
    }

    private void sendForPhotoText(final ContentBean contentBean) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.delFolder(SavePath.savePath);
                String[] pictures = contentBean.getList().get(0).getPicUrl().split(",");
                for (String picture : pictures) {
                    byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                    Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                            b.length);
                    FileUtils.saveFile(GroupSendingActivity.this, System.currentTimeMillis() + ".png", bmp);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                    }
                });
                String content = contentBean.getList().get(0).getContent();
                int type = contentBean.getList().get(0).getType();
                SPUtil.putAndApply(GroupSendingActivity.this, GROUP_CONTENT, content);
                SPUtil.putAndApply(GroupSendingActivity.this, GROUP_TYPE, type);
                File folder = new File(SavePath.SAVE_PIC_PATH);
                addToList(folder);
                ShareUtils.shareMultipleToMoments(GroupSendingActivity.this, content, filePictures);


            }
        }).start();
    }

    private void addToList(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    filePictures.add(f);
                }
            }
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_sending;
    }


    @OnClick(R.id.tv_result)
    public void onViewClicked() {
        if (tvResult.getText().toString().trim().equals("获取素材失败,点击重新获取")) {
            loadData();
        }
    }
}
