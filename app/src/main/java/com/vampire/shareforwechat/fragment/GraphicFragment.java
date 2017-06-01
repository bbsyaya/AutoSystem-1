package com.vampire.shareforwechat.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.GraphicChannelActivity;
import com.vampire.shareforwechat.adapter.PostFragmentPagerAdapter;
import com.vampire.shareforwechat.constant.HttpIdentifier;
import com.vampire.shareforwechat.model.material.LabelEntity;
import com.vampire.shareforwechat.network.MaterialUrl;
import com.vampire.shareforwechat.util.RetrofitUtils;
import com.vampire.shareforwechat.util.TabLayoutUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.vampire.shareforwechat.activity.GraphicChannelActivity.CHANNEL_RESULT;
import static com.vampire.shareforwechat.activity.MainActivity.TPIDS;

/**
 * 图文
 * Created by 焕焕 on 2017/5/14.
 */

public class GraphicFragment extends AppBaseFragment {
    public static final String LABEL_ALL_ID = "label_all_id";
    public static final int CHANNEL = 1;
    public static final String CHANNEL_LIST_NAME = "channel_list_name";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.add_channel_iv)
    ImageView addChannelIv;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private ArrayList<Fragment> mGraphicFragmentList = new ArrayList<>();
    private ArrayList<Fragment> mMaterialFragmentList = new ArrayList<>();
    private List<String> channelNames = new ArrayList<>();
    private String mCurrentViewPagerName;
    private OnGraphicFIListener mGraphicListener;
    private List<LabelEntity.LabelListBean> labelList;
    private String tpIds;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_graphic;
    }

    @Override
    protected void initViews() {
        mGraphicListener.onGraphicToolbar(mToolbar, tpIds);
    }


    @Override
    protected void loadData() {
        showProgress();
        doHttp(RetrofitUtils.createApi(MaterialUrl.class).labelList(cyIds, uIds), HttpIdentifier.LABEL_LIST);
    }

    public static GraphicFragment newInstance(String tpIds) {
        GraphicFragment fragment = new GraphicFragment();
        Bundle args = new Bundle();
        args.putString(TPIDS, tpIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tpIds = getArguments().getString(TPIDS);
        }
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        switch (identifier) {
            case ERROR:
                new AlertDialog.Builder(getActivity())
                        .setTitle("服务器超时，您是否要重新加载？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadData();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case HttpIdentifier.LABEL_LIST:
                LabelEntity entity = JSONObject.parseObject(strReuslt, LabelEntity.class);
                if (HttpIdentifier.REQUEST_SUCCESS.equals(entity.getResult())) {
                    labelList = entity.getLabelList();
                    if (tpIds.equals("1")) {
                        handleGraphic();
                    } else {
                        handleMaterial();
                    }
                }
                break;
        }
    }

    private void handleMaterial() {
        mMaterialFragmentList.clear();
        channelNames.clear();
        MaterialListFragment materialListAllFragment = MaterialListFragment.newInstance(LABEL_ALL_ID, tpIds);
        mMaterialFragmentList.add(materialListAllFragment);
        channelNames.add("全部");
        for (LabelEntity.LabelListBean bean : labelList) {
            MaterialListFragment materialListFragment = MaterialListFragment.newInstance(bean.getLabelId(), tpIds);
            mMaterialFragmentList.add(materialListFragment);
            channelNames.add(bean.getName());
        }
        setViewPager(channelNames, mMaterialFragmentList);
    }

    private void handleGraphic() {
        mGraphicFragmentList.clear();
        channelNames.clear();
        GraphicListFragment graphicListAllFragment = GraphicListFragment.newInstance(LABEL_ALL_ID);//全部的
        mGraphicFragmentList.add(graphicListAllFragment);
        channelNames.add("全部");
        for (LabelEntity.LabelListBean bean : labelList) {
            GraphicListFragment graphicListFragment = createListFragment(bean);
            mGraphicFragmentList.add(graphicListFragment);
            channelNames.add(bean.getName());
        }
        setViewPager(channelNames, mGraphicFragmentList);
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

    private GraphicListFragment createListFragment(LabelEntity.LabelListBean bean) {
        GraphicListFragment fragment = GraphicListFragment.newInstance(bean.getLabelId());
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGraphicFIListener) {
            mGraphicListener = (OnGraphicFIListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mGraphicListener = null;
    }


    @OnClick(R.id.add_channel_iv)
    public void onClick() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CHANNEL_LIST_NAME, (ArrayList<String>) channelNames);
        startActivityForResult(GraphicChannelActivity.class, bundle, CHANNEL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHANNEL) {
                mCurrentViewPagerName = data.getStringExtra(CHANNEL_RESULT);
                int currentViewPagerPosition = getCurrentViewPagerPosition();
                mViewPager.setCurrentItem(currentViewPagerPosition, false);
            }
        }
    }

    public interface OnGraphicFIListener {
        void onGraphicToolbar(Toolbar toolbar, String tpIds);
    }

}
