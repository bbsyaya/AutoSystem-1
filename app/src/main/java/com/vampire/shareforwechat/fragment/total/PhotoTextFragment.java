package com.vampire.shareforwechat.fragment.total;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.contract.OnSubitemClickListener;
import com.kidney_hospital.base.util.FileUtils;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.adapter.GraphicListAdapter;
import com.vampire.shareforwechat.constant.HttpIdentifier;
import com.vampire.shareforwechat.fragment.AppBaseFragment;
import com.vampire.shareforwechat.model.material.GraphicEntity;
import com.vampire.shareforwechat.network.MaterialUrl;
import com.vampire.shareforwechat.util.DownPIcUtils;
import com.vampire.shareforwechat.util.RetrofitUtils;
import com.vampire.shareforwechat.util.ShareUtils;
import com.vampire.shareforwechat.view.FormBotomDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



/**
 * Created by Vampire on 2017/5/26.
 */

public class PhotoTextFragment extends AppBaseFragment implements OnSubitemClickListener ,View.OnClickListener{
    private static final int PAGE_SIZE = 10;
    private static final String TPIDS = "1";
    @BindView(R.id.rv_graphic)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.tv_empty)
    TextView mEmptyView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private int currentPage = 1;
    private boolean isLoading = false;
    private GraphicListAdapter mAdapter;
    private GraphicEntity.ArticleListBean articleListBean;
    public List<File> filePictures = new ArrayList<>();

    @Override
    protected void loadData() {
        currentPage = 1;
        doHttp(RetrofitUtils.createApi(MaterialUrl.class).listByTypeAll(uIds,
                cyIds, TPIDS, PAGE_SIZE, currentPage), HttpIdentifier.FIND_BY_LABEL);

    }

    @Override
    protected void initViews() {
        showProgressBar();
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        GraphicEntity entity = JSONObject.parseObject(strReuslt, GraphicEntity.class);
        switch (identifier) {
            case HttpIdentifier.FIND_BY_LABEL:
                srl.setRefreshing(false);
                hideProgressBar();
                if (HttpIdentifier.REQUEST_SUCCESS.equals(entity.getResult())) {
                    if (entity.getArticleList().size() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                        mAdapter.setList(entity.getArticleList());
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case HttpIdentifier.FIND_BY_LABEL_MORE:
                isLoading = false;
                mAdapter.hideFooter();
                mAdapter.addMore(entity.getArticleList());
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new GraphicListAdapter(null, getActivity());
        mAdapter.setOnSubitemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_graphic_list;
    }

    private void initSwipeRefreshLayout() {
        srl.setColorSchemeResources(R.color.colorPrimary);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO refresh data
                loadData();
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (!isLoading && visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition >= totalItemCount - 1) {
                    //TODO load more % show footer
                    isLoading = true;
                    if (mAdapter != null) {
                        mAdapter.showFooter();
                    }
                    currentPage++;
                    doHttp(RetrofitUtils.createApi(MaterialUrl.class).listByTypeAll(uIds,
                            cyIds, TPIDS, PAGE_SIZE, currentPage), HttpIdentifier.FIND_BY_LABEL_MORE);

                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSubitemClick(int position, View view) {
        articleListBean = mAdapter.getList().get(position);
        switch (view.getId()) {
            case R.id.iv_share:
                shareDialog();
                break;
        }
    }
    private void shareDialog() {
        final FormBotomDialogBuilder builder = new FormBotomDialogBuilder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_share, null);
        builder.setFB_AddCustomView(v);
        v.findViewById(R.id.ll_share_weixin).setOnClickListener(this);
        v.findViewById(R.id.ll_share_pengyouquan).setOnClickListener(this);
        builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share_weixin:
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.delFolder(SavePath.savePath);
                        String[] pictures = articleListBean.getPictures().split(",");
                        for (String picture : pictures) {
                            byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                                    b.length);
                            FileUtils.saveFile(getActivity(), System.currentTimeMillis() + ".png", bmp);
                        }
                        File folder = new File(SavePath.savePath + "labPic/");
                        addToList(folder);
                        ShareUtils.shareMultipleToMomentsWeixin(getActivity(), articleListBean.getArticle().getTitle(), filePictures);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }
                        });
                    }
                }).start();

                break;
            case R.id.ll_share_pengyouquan:
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.delFolder(SavePath.savePath);
                        String[] pictures = articleListBean.getPictures().split(",");
                        for (String picture : pictures) {
                            byte[] b = DownPIcUtils.getHtmlByteArray(picture);
                            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0,
                                    b.length);
                            FileUtils.saveFile(getActivity(), System.currentTimeMillis() + ".png", bmp);
                        }
                        File folder = new File(SavePath.savePath + "labPic/");
                        addToList(folder);
                        ShareUtils.shareMultipleToMoments(getActivity(), articleListBean.getArticle().getTitle(), filePictures);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }
                        });
                    }
                }).start();
                break;
        }
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
}
