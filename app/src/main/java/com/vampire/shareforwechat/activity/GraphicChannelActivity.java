package com.vampire.shareforwechat.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.adapter.BaseRecyclerViewAdapter;
import com.vampire.shareforwechat.adapter.GraphicChannelAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.vampire.shareforwechat.fragment.GraphicFragment.CHANNEL_LIST_NAME;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class GraphicChannelActivity extends AppBaseActivity implements SearchView.OnQueryTextListener ,BaseRecyclerViewAdapter.MyRecyclerListener{
    private static final String TAG = "GraphicChannelActivity";
    public static final String CHANNEL_RESULT = "channel_result";
    @BindView(R.id.rv_channel)
    RecyclerView rvChannel;
    private List<String> channelNames = new ArrayList<>();
    private List<String> searchedChannelNames = new ArrayList<>();
    private GraphicChannelAdapter graphicChannelAdapter;

    @Override
    protected void loadData() {
        Bundle bundle = getIntent().getExtras();
        channelNames = bundle.getStringArrayList(CHANNEL_LIST_NAME);
        graphicChannelAdapter = new GraphicChannelAdapter(channelNames);
        rvChannel.setAdapter(graphicChannelAdapter);
        graphicChannelAdapter.setmOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel, menu);//指定Toolbar上的视图文件
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.ab_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //SearchView 自动展开和弹出输入法
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void initViews() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvChannel.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        rvChannel.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_graphic_channel;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e(TAG, "onQueryTextSubmit: " + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchedChannelNames.clear();
        Log.e(TAG, "onQueryTextChange: 81" + newText);
        for (String name : channelNames) {
            if (name.contains(newText)) {
                Log.e(TAG, "onQueryTextChange: " + newText);
                searchedChannelNames.add(name);
            }
        }
        graphicChannelAdapter.setList(searchedChannelNames);
        graphicChannelAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public void OnItemClickListener(View view, int position) {
        String channelName = graphicChannelAdapter.getList().get(position);
        Intent intent = new Intent();
        intent.putExtra(CHANNEL_RESULT, channelName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
