package com.vampire.shareforwechat.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.kidney_hospital.base.contract.OnSubitemClickListener;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.adapter.GraphicListAdapter;
import com.vampire.shareforwechat.constant.HttpIdentifier;
import com.vampire.shareforwechat.model.material.GraphicEntity;
import com.vampire.shareforwechat.network.MaterialUrl;
import com.vampire.shareforwechat.util.RetrofitUtils;
import com.vampire.shareforwechat.util.ShareUtils;
import com.vampire.shareforwechat.view.FormBotomDialogBuilder;

import butterknife.BindView;

import static com.vampire.shareforwechat.fragment.GraphicFragment.LABEL_ALL_ID;

/**
 * 文章 视频 电台
 * Created by 焕焕 on 2017/5/16.
 */

public class MaterialListFragment extends AppBaseFragment implements OnSubitemClickListener, View.OnClickListener {
    private static final String TAG = "MaterialListFragment";
    private static final String LABEL_ID = "LABEL_ID";
    private static final String TPIDS = "TPIDS";
    private static final int PAGE_SIZE = 10;
    @BindView(R.id.rv_graphic)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;
    @BindView(R.id.tv_empty)
    TextView mEmptyView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private String labelId;
    private String tpIds;
    private int currentPage = 1;
    private boolean isLoading = false;
    private GraphicListAdapter mAdapter;
    private int currentPosition;
    private String newTitle;
    private GraphicEntity.ArticleListBean articleListBean;

    @Override
    protected void loadData() {
        currentPage = 1;
        if (labelId.equals(LABEL_ALL_ID)) {
            doHttp(RetrofitUtils.createApi(MaterialUrl.class).listByTypeAll(uIds,
                    cyIds, tpIds, PAGE_SIZE, currentPage), HttpIdentifier.FIND_BY_LABEL);
        } else {
            doHttp(RetrofitUtils.createApi(MaterialUrl.class).findByLabel(uIds,
                    cyIds, tpIds, PAGE_SIZE, currentPage, labelId), HttpIdentifier.FIND_BY_LABEL);
        }
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
                    if (labelId.equals(LABEL_ALL_ID)) {
                        doHttp(RetrofitUtils.createApi(MaterialUrl.class).listByTypeAll(uIds,
                                cyIds, tpIds, PAGE_SIZE, currentPage), HttpIdentifier.FIND_BY_LABEL_MORE);
                    } else {
                        doHttp(RetrofitUtils.createApi(MaterialUrl.class).findByLabel(uIds,
                                cyIds, tpIds, PAGE_SIZE, currentPage, labelId), HttpIdentifier.FIND_BY_LABEL_MORE);
                    }

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

    @Override
    public void onResponse(int identifier, String strReuslt) {
        super.onResponse(identifier, strReuslt);
        Log.e(TAG, "onResponse: " + strReuslt);
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
            case HttpIdentifier.GRAPHIC_EDIT:
                if (HttpIdentifier.REQUEST_SUCCESS.equals(entity.getResult())) {
                    GraphicEntity.ArticleListBean articleListBean = mAdapter.getList().get(currentPosition);
                    articleListBean.getArticle().setTitle(newTitle);
                    articleListBean.getArticle().setContent(newTitle);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case HttpIdentifier.GRAPHIC_DEL:
                if (HttpIdentifier.REQUEST_SUCCESS.equals(entity.getResult())) {
                    mAdapter.delete(currentPosition);
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_graphic_list;//跟图文的一样，没必要再建
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            labelId = getArguments().getString(LABEL_ID);
            tpIds = getArguments().getString(TPIDS);
        }
        mAdapter = new GraphicListAdapter(null, getActivity());
        mAdapter.setOnSubitemClickListener(this);
    }

    public static MaterialListFragment newInstance(String labelId, String tpIds) {
        MaterialListFragment fragment = new MaterialListFragment();
        Bundle args = new Bundle();
        args.putString(LABEL_ID, labelId);
        args.putString(TPIDS, tpIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSubitemClick(int position, View view) {
        currentPosition = position;
        articleListBean = mAdapter.getList().get(position);
        switch (view.getId()) {
            case R.id.rl_meterial:
                String url = articleListBean.getArticle().getUrl();
                Uri uri = Uri.parse(url);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
            case R.id.tv_delete:
                new AlertDialog.Builder(getActivity())
                        .setTitle("确定要删除这条素材吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgress();
                                doHttp(RetrofitUtils.createApi(MaterialUrl.class).del(uIds, cyIds,
                                        articleListBean.getArticle().getId()), HttpIdentifier.GRAPHIC_DEL);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.tv_edit:
                final EditText editText = new EditText(getActivity());

                editText.setText(articleListBean.getArticle().getTitle());
                editText.setHint("您要修改的内容");
                new AlertDialog.Builder(getActivity())
                        .setTitle("编辑")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newTitle = editText.getText().toString().trim();
                                if (!newTitle.equals(articleListBean.getArticle().getTitle())) {
                                    showProgress();
                                    doHttp(RetrofitUtils.createApi(MaterialUrl.class).edit(uIds,
                                            articleListBean.getArticle().getId(), newTitle, newTitle), HttpIdentifier.GRAPHIC_EDIT);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_share_weixin:

                break;
            case R.id.ll_share_pengyouquan:
                ShareUtils.sendToFriends(getActivity(),
                        articleListBean.getArticle().getUrl(),
                        articleListBean.getArticle().getTitle(),
                        articleListBean.getArticle().getContent(),
                        articleListBean.getArticle().getPicUrl());
                break;
        }
    }
}
