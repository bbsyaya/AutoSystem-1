package com.shuangyou.material.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kidney_hospital.base.config.SavePath;
import com.kidney_hospital.base.util.TextUtils;
import com.shuangyou.material.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;

/**
 * Created by Vampire on 2017/7/12.
 */
public class LogActivity extends AppBaseActivity {
    @BindView(R.id.tv_log)
    TextView tvLog;
    @BindView(R.id.sl_log)
    ScrollView slLog;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        initToolbar();
        showInfo();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showInfo() {
        tvLog.setText("");
        File file = new File(SavePath.SAVE_MERGE);
        String str = null;
        try {
            InputStream is = new FileInputStream(file);
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            while ((str = reader.readLine()) != null) {
                tvLog.append(str);
                tvLog.append("\n");
            }
            if (TextUtils.isNull(tvLog.getText().toString().trim())){
                tvLog.setText("日志为空");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tvLog.setText("日志为空");
        } catch (IOException e) {
            e.printStackTrace();
            tvLog.setText("日志为空");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                File file = new File(SavePath.SAVE_MERGE);
                if (file.exists()){
                    file.delete();
                }
                tvLog.setText("日志为空");
                showToast("日志清理成功");
                return true;
            case R.id.refresh:
                showInfo();
                return true;
            case R.id.top:
                slLog.fullScroll(ScrollView.FOCUS_UP);
                return true;
            case R.id.bottom:
                slLog.fullScroll(ScrollView.FOCUS_DOWN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
