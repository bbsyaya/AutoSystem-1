package com.vampire.shareforwechat.supervisor;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.kidney_hospital.base.util.DateUtils;
import com.kidney_hospital.base.util.FileUtils;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.AppBaseActivity;
import com.vampire.shareforwechat.activity.PhotoDetailActivity;
import com.vampire.shareforwechat.util.GlideUtil;
import com.vampire.shareforwechat.view.FormBotomDialogBuilder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kidney_hospital.base.util.TextUtils.addZero;
import static com.vampire.shareforwechat.adapter.GraphicListAdapter.TRANSITION_ANIMATION_NEWS_PHOTOS;

/**
 * Created by 焕焕 on 2017/5/20.
 */

public class SupevisorAddActivity extends AppBaseActivity implements View.OnClickListener {
    private static final String TAG = "SupevisorAddActivity";
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1000;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    @BindView(R.id.iv_supevior_add)
    ImageView ivSupeviorAdd;
    @BindView(R.id.iv_supevior_remove)
    ImageView ivSupeviorRemove;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.toDoCustomTextInput)
    CustomTextInputLayout toDoCustomTextInput;
    @BindView(R.id.et_date)
    EditText etDate;
    @BindView(R.id.et_time)
    EditText etTime;
    private FormBotomDialogBuilder builder;
    private boolean isScan;//是否可放大预览
    private int currentYear, currentMonth, currentDay, currentHour, currentMinute;
    private String photoPath;
    private int id;
    private List<SupevisorEntity> supevisorList = new ArrayList<>();

    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        supevisorList = DataSupport.findAll(SupevisorEntity.class);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt(SupevisorActivity.EXTRA_ID);
        Log.e(TAG, "initViews: id" + id);
        if (id == -1) {
            etDate.setText(DateUtils.getTodayDate());
            etTime.setText(DateUtils.getTodayTime());
            Calendar calendar = Calendar.getInstance();
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH);
            currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMinute = calendar.get(Calendar.MINUTE);
        } else {

            for (SupevisorEntity entity : supevisorList) {
                if (id == entity.getId()) {
                    setDefaultData(entity);
                }
            }
        }


    }

    private void setDefaultData(SupevisorEntity entity) {
        etRemark.setText(entity.getRemark());
        currentYear = entity.getYear();
        currentMonth = entity.getMonth();
        currentDay = entity.getDay();
        currentHour = entity.getHour();
        currentMinute = entity.getMinute();
        etDate.setText(currentYear + "-" + addZero(currentMonth + 1) + "-" + addZero(currentDay));
        etTime.setText(addZero(currentHour) + ":" + addZero(currentMinute));
        photoPath = entity.getPhotoPath();
        GlideUtil.loadImage(this, entity.getPhotoPath(), ivSupeviorAdd);
        ivSupeviorRemove.setVisibility(View.VISIBLE);
        isScan = true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_supevisor_add;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAPTURE_CAMEIA:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                    ivSupeviorAdd.setImageBitmap(bitmap);
                    photoPath = FileUtils.saveFile(this, System.currentTimeMillis() + ".png", bitmap);
                    ivSupeviorRemove.setVisibility(View.VISIBLE);
                    isScan = true;
                    Log.e(TAG, "onActivityResult: " + photoPath);
                    break;
                case REQUEST_CODE_PICK_IMAGE:
                    // 从相册返回的数据
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        photoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        Log.e(TAG, "onActivityResult: path" + photoPath);
                        Bitmap bmp = BitmapFactory.decodeFile(photoPath);
                        ivSupeviorAdd.setImageBitmap(bmp);
                        ivSupeviorRemove.setVisibility(View.VISIBLE);
                        isScan = true;
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.btn_submit, R.id.et_date, R.id.et_time, R.id.iv_supevior_add, R.id.iv_supevior_remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (photoPath == null || photoPath.equals("")) {
                    showToast("请选择图片！");
                    return;
                }
                if (etRemark.getText().toString().trim().equals("")) {
                    showToast("请选择备注！");
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, currentYear);
                calendar.set(Calendar.MONTH, currentMonth);
                calendar.set(Calendar.DAY_OF_MONTH, currentDay);
                calendar.set(Calendar.HOUR_OF_DAY, currentHour);
                calendar.set(Calendar.MINUTE, currentMinute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    showToast("您设置的提醒时间已过！");
                    return;
                }
                SupevisorEntity entity = new SupevisorEntity();
                entity.setRemark(etRemark.getText().toString().trim());
                entity.setPhotoPath(photoPath);
                entity.setYear(currentYear);
                if (currentMonth == 0) {
                    entity.setToDefault("month");
                } else {
                    entity.setMonth(currentMonth);
                }
                entity.setDay(currentDay);
                if (currentHour == 0) {
                    entity.setToDefault("hour");
                } else {
                    entity.setHour(currentHour);
                }
                if (currentMinute == 0) {
                    entity.setToDefault("minute");
                } else {
                    entity.setMinute(currentMinute);
                }
                if (id == -1) {
                    entity.save();
                } else {
                    entity.updateAll("id=?", id + "");
                }
                startService(new Intent(this, SupevisorService.class));
                finish();
                break;
            case R.id.et_date:

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentYear = year;
                        currentMonth = month;
                        currentDay = dayOfMonth;
                        etDate.setText(currentYear + "-" + addZero(currentMonth + 1) + "-" + addZero(currentDay));
                    }
                }, currentYear, currentMonth, currentDay).show();
                break;
            case R.id.et_time:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                currentHour = hourOfDay;
                                currentMinute = minute;
                                etTime.setText(addZero(currentHour) + ":" + addZero(currentMinute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)).show();
                break;
            case R.id.iv_supevior_add:
                if (!isScan) {
                    builder = new FormBotomDialogBuilder(this);
                    View v = getLayoutInflater().inflate(R.layout.dialog_supevisor_bottom, null);
                    builder.setFB_AddCustomView(v);
                    v.findViewById(R.id.tv_camera).setOnClickListener(this);
                    v.findViewById(R.id.tv_album).setOnClickListener(this);
                    v.findViewById(R.id.tv_cancel).setOnClickListener(this);
                    builder.show();
                } else {
                    //点击放大的逻辑
                    Intent intent = PhotoDetailActivity.getPhotoDetailIntent(this, photoPath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ImageView animationIv = (ImageView) view.findViewById(R.id.iv_supevior_add);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                this,
                                animationIv,
                                TRANSITION_ANIMATION_NEWS_PHOTOS);
                        this.startActivity(intent, options.toBundle());
                    } else {
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                                view,
                                view.getWidth() / 2,
                                view.getHeight() / 2,
                                0,
                                0);
                        ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                    }
                }
                break;
            case R.id.iv_supevior_remove:
                ivSupeviorRemove.setVisibility(View.GONE);
                ivSupeviorAdd.setImageResource(R.mipmap.ic_supevisor_add_add);
                isScan = false;
                break;
            case R.id.tv_camera:
                builder.dismiss();
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, REQUEST_CODE_CAPTURE_CAMEIA);
                break;
            case R.id.tv_album:
                builder.dismiss();
                Intent intentAlbum = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intentAlbum, REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.tv_cancel:
                builder.dismiss();
                break;
        }
    }

}
