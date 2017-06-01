package com.vampire.shareforwechat.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.util.SystemUiVisibilityUtil;
import com.vampire.shareforwechat.view.photoview.OnPhotoTapListener;
import com.vampire.shareforwechat.view.photoview.PhotoView;

import butterknife.BindView;

/**
 * Created by 焕焕 on 2017/5/15.
 */

public class PhotoDetailActivity extends AppBaseActivity {
    private static final String PHOTO_URL = "photo_url";
    @BindView(R.id.img_iv)
    ImageView mImgIv;

    @BindView(R.id.photo_iv)
    PhotoView mPhotoIv;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private String mPhotoUrl;
    private boolean isHidden = false;
    private boolean mIsStatusBarHidden = false;
    public static Intent getPhotoDetailIntent(Context context, String photoUrl){
        Intent intent = new Intent(context,PhotoDetailActivity.class);
        intent.putExtra(PHOTO_URL,photoUrl);
        return intent;
    }
    @Override
    protected void loadData() {
        Glide.with(this).load(mPhotoUrl)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(R.mipmap.ic_loading)
                .error(R.mipmap.ic_load_fail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.d("ThreadName","----------------"+Thread.currentThread().getName());
                        mImgIv.setImageBitmap(resource);
                    }
                });
        mPhotoIv.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                hideToolBarAndTextView();
                hideOrShowStatusBar();
            }
        });
        initLazyLoadView();
    }

    private void initLazyLoadView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {

                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    loadPhotoView();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }else{
            loadPhotoView();
        }

    }

    private void loadPhotoView() {
                Glide.with(this).load(mPhotoUrl)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .placeholder(R.mipmap.ic_loading)
                        .error(R.mipmap.ic_load_fail)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mPhotoIv);
    }

    @Override
    protected void initViews() {
        mPhotoUrl = getIntent().getStringExtra(PHOTO_URL);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_detail;
    }
    private void hideOrShowStatusBar() {
        if (mIsStatusBarHidden) {
            SystemUiVisibilityUtil.enter(PhotoDetailActivity.this);
        } else {
            SystemUiVisibilityUtil.exit(PhotoDetailActivity.this);
        }
        mIsStatusBarHidden = !mIsStatusBarHidden;
    }
    public void hideToolBarAndTextView(){
        isHidden = !isHidden;
        if(isHidden){
            startAnimation(true,1.0f,0.0f);
        }else{
            startAnimation(false,0.1f,1.0f);
        }
    }
    private void startAnimation(final boolean endState, float startValue, float endValue) {
        ValueAnimator animator = ValueAnimator.ofFloat(startValue,endValue).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float y1;
                if(endState){
                    y1 = (0 - animation.getAnimatedFraction())*mToolbar.getHeight();
                }else{
                    y1 = (animation.getAnimatedFraction() - 1)*mToolbar.getHeight();
                }
                mToolbar.setTranslationY(y1);
            }
        });
        animator.start();
    }

}
