package com.vampire.shareforwechat.fragment.total;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.adapter.PostFragmentPagerAdapter;
import com.vampire.shareforwechat.fragment.AppBaseFragment;
import com.vampire.shareforwechat.util.TabLayoutUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Vampire on 2017/5/26.
 */

public class MaterialFragment extends AppBaseFragment {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private List<String> channelNames = new ArrayList<>();
    private OnMaterialFIListener onMaterialFIListener;
    private PhotoTextFragment photoTextFragment;
    private ArticleFragment articleFragment;
    private VideoFragment videoFragment;
    private RadioFragment radioFragment;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private String mCurrentViewPagerName;

    @Override
    protected void loadData() {
    }

    @Override
    protected void initViews() {
        onMaterialFIListener.onMaterialToolbar(mToolbar);
        channelNames.add("图文");
        channelNames.add("文章");
        channelNames.add("视频");
        channelNames.add("电台");
        photoTextFragment = new PhotoTextFragment();
        articleFragment  = new ArticleFragment();
        videoFragment = new VideoFragment();
        radioFragment = new RadioFragment();
        mFragmentList.add(photoTextFragment);
        mFragmentList.add(articleFragment);
        mFragmentList.add(videoFragment);
        mFragmentList.add(radioFragment);
        setViewPager(channelNames, mFragmentList);

    }

    private void setViewPager(List<String> channelNames, ArrayList<Fragment> fragmentList) {
        PostFragmentPagerAdapter adapter = new PostFragmentPagerAdapter(
                getChildFragmentManager(), channelNames, fragmentList);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        TabLayoutUtil.dynamicSetTabLayoutMode(mTabLayout);
        setPageChangeListener();
        int currentViewPagerPosition = getCurrentViewPagerPosition();
        mViewPager.setCurrentItem(currentViewPagerPosition, false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMaterialFIListener) {
            onMaterialFIListener = (OnMaterialFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onMaterialFIListener = null;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_material;
    }
    private int getCurrentViewPagerPosition() {
        int position = 0;
        if (mCurrentViewPagerName != null) {
            for (int i = 0; i < channelNames.size(); i++) {
                if (mCurrentViewPagerName.equals(channelNames.get(i))) {
                    position = i;
                }
            }
        }
        return position;
    }

    private void setPageChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerName = channelNames.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public interface OnMaterialFIListener {
        void onMaterialToolbar(Toolbar toolbar);
    }
}
