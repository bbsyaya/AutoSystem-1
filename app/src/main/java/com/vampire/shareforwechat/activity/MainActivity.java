package com.vampire.shareforwechat.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.kidney_hospital.base.fragment.BaseFragment;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.fragment.GraphicFragment;
import com.vampire.shareforwechat.fragment.alarm.MedicineFragment;
import com.vampire.shareforwechat.fragment.fans.AddFansFragment;
import com.vampire.shareforwechat.fragment.total.MaterialFragment;
import com.vampire.shareforwechat.manager.WorkManager;
import com.vampire.shareforwechat.service.ShareService;

import butterknife.BindView;

public class MainActivity extends AppBaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        MedicineFragment.OnMedicineFIListener,MaterialFragment.OnMaterialFIListener{
    private static final String TAG = "MainActivity";
    private static final String CHILD_FRAGMENT_TAG_IMAGE_TEXT= "child_image_text";
    private static final String CHILD_FRAGMENT_TAG_ALARM = "child_alarm";
    private static final String CHILD_FRAGMENT_TAG_FANS = "child_fans";
    public static final String TPIDS= "tpIds";
    @BindView(R.id.show_content_layout)
    FrameLayout showContentLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private MaterialFragment mImageTextFragment;
    private MedicineFragment mAlarmFragment;
    private AddFansFragment mAddFansFragment;
    private String childFragmentType; // 1.图文 ; 2.文章 ; 3.视频  4.电台  tpIds一样

    @Override
    protected void onResume() {
        super.onResume();
//        WorkManager.getInstance().isAccessibilitySettingsOn();
//        startService(new Intent(this, ShareService.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        mImageTextFragment = new MaterialFragment();//图文
        mAlarmFragment = new MedicineFragment();
        mAddFansFragment = new AddFansFragment();
        mNavigationView.setNavigationItemSelectedListener(this);
        childFragmentType = childFragmentType == null ? CHILD_FRAGMENT_TAG_IMAGE_TEXT : childFragmentType;
        setDefaultChildFragment(childFragmentType);
    }
    private void setDefaultChildFragment(String childFragmentTag) {
        switch (childFragmentTag) {
            case CHILD_FRAGMENT_TAG_ALARM:
                mNavigationView.setCheckedItem(R.id.nav_alarm);
                break;
            case CHILD_FRAGMENT_TAG_IMAGE_TEXT:
            default:
                mNavigationView.setCheckedItem(R.id.nav_graphic);
                break;
            case CHILD_FRAGMENT_TAG_FANS:
                mNavigationView.setCheckedItem(R.id.nav_fans);
                break;
        }
        setChildFragment(childFragmentTag);
    }
    @Override
    protected void loadData() {

    }

    private void setChildFragment(String childFragmentTag) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (childFragmentType.equals(childFragmentTag)) {
            BaseFragment childFragment = (BaseFragment) mFragmentManager.findFragmentByTag(childFragmentTag);
            if (childFragment == null) {
                childFragment = getChildFragmentByTag(childFragmentTag);
            } else {
                if (getChildFragmentByTag(childFragmentTag) != childFragment) {
                    mFragmentTransaction.remove(childFragment);
                    childFragment = getChildFragmentByTag(childFragmentTag);
                }
            }
            if (!childFragment.isAdded()) {
                mFragmentTransaction.add(R.id.show_content_layout, childFragment, childFragmentTag);
            }
            if (childFragment.isHidden()) {
                mFragmentTransaction.show(childFragment);
            }
        } else {
            BaseFragment childFragment = (BaseFragment) mFragmentManager.findFragmentByTag(childFragmentType);
            if (childFragment != null) {
                mFragmentTransaction.hide(childFragment);
            }
            BaseFragment addChildFragment = (BaseFragment) mFragmentManager.findFragmentByTag(childFragmentTag);
            if (addChildFragment == null) {
                addChildFragment = getChildFragmentByTag(childFragmentTag);
            } else {
                if (getChildFragmentByTag(childFragmentTag) != addChildFragment) {
                    mFragmentTransaction.remove(addChildFragment);
                    addChildFragment = getChildFragmentByTag(childFragmentTag);
                }
            }
            if (!addChildFragment.isAdded()) {
                mFragmentTransaction.add(R.id.show_content_layout, addChildFragment, childFragmentTag);
            }
            mFragmentTransaction.show(addChildFragment);
        }
        childFragmentType = childFragmentTag;
        mFragmentTransaction.commit();
    }

    private BaseFragment getChildFragmentByTag(String childFragmentTag) {
        switch (childFragmentTag) {
            case CHILD_FRAGMENT_TAG_IMAGE_TEXT:
            default:
                return mImageTextFragment;
            case CHILD_FRAGMENT_TAG_ALARM:
                return mAlarmFragment;
            case CHILD_FRAGMENT_TAG_FANS:
                return mAddFansFragment;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_graphic:
                if (!CHILD_FRAGMENT_TAG_IMAGE_TEXT.equals(childFragmentType)) {
                    setChildFragment(CHILD_FRAGMENT_TAG_IMAGE_TEXT);
                }
                break;
            case R.id.nav_alarm:
                if (!CHILD_FRAGMENT_TAG_ALARM.equals(childFragmentType)){
                    setChildFragment(CHILD_FRAGMENT_TAG_ALARM);
                }
                break;
            case R.id.nav_fans:
                if (!CHILD_FRAGMENT_TAG_FANS.equals(childFragmentType)){
                    setChildFragment(CHILD_FRAGMENT_TAG_FANS);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public void onMedicineToolbar(Toolbar toolbar) {
        toolbar.setTitle("用药提醒");
        setToolbar(toolbar);
    }

    @Override
    public void onMaterialToolbar(Toolbar toolbar) {
        toolbar.setTitle("素材转发");
        setToolbar(toolbar);
    }
}
