package com.vampire.shareforwechat.supervisor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.AppBaseActivity;
import com.vampire.shareforwechat.view.DividerItemDecoration;
import com.vampire.shareforwechat.view.EmptySupportRecyclerView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自律提醒
 * Created by 焕焕 on 2017/5/20.
 */

public class SupevisorActivity extends AppBaseActivity implements SupevisorAdapter.OnItemClickListener,SupevisorAdapter.OnItemLongClickListener{
    public static final String EXTRA_ID = "id";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.rv_supevior)
    EmptySupportRecyclerView rvSupevior;
    @BindView(R.id.fab_supevisor)
    FloatingActionButton fabSupevisor;
    private SupevisorAdapter mAdapter;
    private List<SupevisorEntity> supevisorList = new ArrayList<>();
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        initToolbar();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        supevisorList = DataSupport.findAll(SupevisorEntity.class);
        mAdapter.setList(supevisorList);
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        rvSupevior.setEmptyView(findViewById(R.id.ll_empty));
        rvSupevior.setHasFixedSize(true);
        rvSupevior.setItemAnimator(new DefaultItemAnimator());
        rvSupevior.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDivider(R.drawable.shp_common_divider);
        rvSupevior.addItemDecoration(dividerItemDecoration);
        mAdapter = new SupevisorAdapter(null,this);
        rvSupevior.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_supevisor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supevisor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                startActivity(SupevisorAboutActivity.class,null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @OnClick(R.id.fab_supevisor)
    public void onClick() {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, -1);//将id传过去
        startActivity(SupevisorAddActivity.class,bundle);
    }

    @Override
    public void onItemClick(int position, View v) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ID, mAdapter.getList().get(position).getId());//将id传过去
        startActivity(SupevisorAddActivity.class,bundle);
    }

    @Override
    public void onItemLongClick(final int position, View v) {
        new AlertDialog.Builder(this)
                .setTitle("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedId = mAdapter.getList().get(position).getId();
                        DataSupport.deleteAll(SupevisorEntity.class, "id=?", selectedId+"");
                        mAdapter.delete(position);
                        startService(new Intent(SupevisorActivity.this, SupevisorService.class));
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
