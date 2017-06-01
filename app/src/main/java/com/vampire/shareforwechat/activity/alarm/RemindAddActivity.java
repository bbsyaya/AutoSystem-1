package com.vampire.shareforwechat.activity.alarm;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.AppBaseActivity;
import com.vampire.shareforwechat.fragment.alarm.MedicineFragment;
import com.vampire.shareforwechat.model.alarm.RemindEntity;
import com.vampire.shareforwechat.service.RemindService;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.kidney_hospital.base.util.TextUtils.addZero;

/**
 * 新增提醒
 * Created by 焕焕 on 2017/5/18.
 */

public class RemindAddActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "RemindAddActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.sc_remind)
    SwitchCompat scRemind;
    @BindView(R.id.et_treatment)
    EditText etTreatment;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_ci)
    TextView tvCi;
    @BindView(R.id.tv_first)
    TextView tvFirst;
    @BindView(R.id.rl_first)
    RelativeLayout rlFirst;
    @BindView(R.id.tv_second)
    TextView tvSecond;
    @BindView(R.id.rl_second)
    RelativeLayout rlSecond;
    @BindView(R.id.tv_third)
    TextView tvThird;
    @BindView(R.id.rl_third)
    RelativeLayout rlThird;
    @BindView(R.id.tv_forth)
    TextView tvForth;
    @BindView(R.id.rl_forth)
    RelativeLayout rlForth;
    @BindView(R.id.tv_fifth)
    TextView tvFifth;
    @BindView(R.id.rl_fifth)
    RelativeLayout rlFifth;
    @BindView(R.id.et_dose)
    EditText etDose;
    @BindView(R.id.tv_pian)
    TextView tvPian;
    @BindView(R.id.btn_save)
    Button btnSave;
    private int whichSelect;
    private int firstHour;
    private int firstMinute;
    private int secondHour;
    private int secondMinute;
    private int thirdHour;
    private int thirdMinute;
    private int forthHour;
    private int forthMinute;
    private int fifthHour;
    private int fifthMinute;
    private boolean isRemind;
    private List<RemindEntity> remindList = new ArrayList<>();
    private int id;
    private Calendar mCalendar;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        mCalendar = Calendar.getInstance();
        scRemind.setOnCheckedChangeListener(this);
        remindList = DataSupport.findAll(RemindEntity.class);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt(MedicineFragment.EXTRA_ID);
        if (id != -1) {//-1是添加的时候，现在是修改的时候
            for (RemindEntity entity : remindList) {
                if (id == entity.getId()) {
                    setDefaultData(entity);
                }
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_remind_add;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isRemind = isChecked;
    }

    private void setDefaultData(RemindEntity entity) {
        etName.setText(entity.getContent());
        if (entity.isRemind()) {
            scRemind.setChecked(true);
        } else {
            scRemind.setChecked(false);
        }
        etTreatment.setText(entity.getTreatment() + "");
        tvCount.setText(entity.getCountAll() + "");
        etDose.setText(entity.getDose() + "");
        tvFirst.setText(addZero(entity.getFirstHour()) + ":" + addZero(entity.getFirstMinute()));
        tvSecond.setText(addZero(entity.getSecondHour()) + ":" + addZero(entity.getSecondMinute()));
        tvThird.setText(addZero(entity.getThirdHour()) + ":" + addZero(entity.getThirdMinute()));
        tvForth.setText(addZero(entity.getForthHour()) + ":" + addZero(entity.getForthMinute()));
        tvFifth.setText(addZero(entity.getFifthHour()) + ":" + addZero(entity.getFifthMinute()));
        switch (entity.getCountAll()) {
            case 1:
                rlFirst.setVisibility(View.VISIBLE);
                break;
            case 2:
                rlFirst.setVisibility(View.VISIBLE);
                rlSecond.setVisibility(View.VISIBLE);
                break;
            case 3:
                rlFirst.setVisibility(View.VISIBLE);
                rlSecond.setVisibility(View.VISIBLE);
                rlThird.setVisibility(View.VISIBLE);
                break;
            case 4:
                rlFirst.setVisibility(View.VISIBLE);
                rlSecond.setVisibility(View.VISIBLE);
                rlThird.setVisibility(View.VISIBLE);
                rlForth.setVisibility(View.VISIBLE);
                break;
            case 5:
                rlFirst.setVisibility(View.VISIBLE);
                rlSecond.setVisibility(View.VISIBLE);
                rlThird.setVisibility(View.VISIBLE);
                rlForth.setVisibility(View.VISIBLE);
                rlFifth.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void defaultView() {
        rlFirst.setVisibility(View.GONE);
        rlSecond.setVisibility(View.GONE);
        rlThird.setVisibility(View.GONE);
        rlForth.setVisibility(View.GONE);
        rlFifth.setVisibility(View.GONE);
    }

    @OnClick({R.id.tv_count, R.id.tv_first, R.id.tv_second, R.id.tv_third, R.id.tv_forth, R.id.tv_fifth, R.id.btn_save})
    public void onClick(View view) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        int currentHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = mCalendar.get(Calendar.MINUTE);
        switch (view.getId()) {
            case R.id.tv_count:
                SetSelectCountDialog();
                break;
            case R.id.tv_first:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                firstHour = hourOfDay;
                                firstMinute = minute;
                                tvFirst.setText(addZero(hourOfDay) + ":" + addZero(minute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)).show();
                break;
            case R.id.tv_second:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                secondHour = hourOfDay;
                                secondMinute = minute;
                                tvSecond.setText(addZero(hourOfDay) + ":" + addZero(minute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)
                ).show();
                break;
            case R.id.tv_third:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                thirdHour = hourOfDay;
                                thirdMinute = minute;
                                tvThird.setText(addZero(hourOfDay) + ":" + addZero(minute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)
                ).show();
                break;
            case R.id.tv_forth:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                forthHour = hourOfDay;
                                forthMinute = minute;
                                tvForth.setText(addZero(hourOfDay) + ":" + addZero(minute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)
                ).show();
                break;
            case R.id.tv_fifth:
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                fifthHour = hourOfDay;
                                fifthMinute = minute;
                                tvFifth.setText(addZero(hourOfDay) + ":" + addZero(minute));
                            }
                        },
                        currentHour, currentMinute, DateFormat.is24HourFormat(this)
                ).show();
                break;
            case R.id.btn_save:
                if (etName.getText().toString().trim().equals("")) {
                    showToast("请输入药品名称！");
                    return;
                }
                saveOrUpdateData();
                break;
        }
    }

    private void saveOrUpdateData() {
        RemindEntity entity = new RemindEntity();
        entity.setContent(etName.getText().toString().trim());
        entity.setRemind(isRemind);
        entity.setTreatment(Integer.parseInt(etTreatment.getText().toString().trim()));
        entity.setCountAll(Integer.parseInt(tvCount.getText().toString().trim()));
        switch (Integer.parseInt(tvCount.getText().toString().trim())) {
            case 1:
                if (firstHour == 0) {
                    firstHour = 8;
                }
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                break;
            case 2:
                if (firstHour == 0) {
                    firstHour = 8;
                }
                if (secondHour == 0) {
                    secondHour = 11;
                }
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                entity.setSecondHour(secondHour);
                entity.setSecondMinute(secondMinute);
                break;
            case 3:
                if (firstHour == 0) {
                    firstHour = 8;
                }
                if (secondHour == 0) {
                    secondHour = 11;
                }
                if (thirdHour == 0) {
                    thirdHour = 14;
                }
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                entity.setSecondHour(secondHour);
                entity.setSecondMinute(secondMinute);
                entity.setThirdHour(thirdHour);
                entity.setThirdMinute(thirdMinute);
                break;
            case 4:
                if (firstHour == 0) {
                    firstHour = 8;
                }
                if (secondHour == 0) {
                    secondHour = 11;
                }
                if (thirdHour == 0) {
                    thirdHour = 14;
                }
                if (forthHour == 0) {
                    forthHour = 18;
                }
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                entity.setSecondHour(secondHour);
                entity.setSecondMinute(secondMinute);
                entity.setThirdHour(thirdHour);
                entity.setThirdMinute(thirdMinute);
                entity.setForthHour(forthHour);
                entity.setForthMinute(forthMinute);
                break;
            case 5:
                if (firstHour == 0) {
                    firstHour = 8;
                }
                if (secondHour == 0) {
                    secondHour = 11;
                }
                if (thirdHour == 0) {
                    thirdHour = 14;
                }
                if (forthHour == 0) {
                    forthHour = 18;
                }
                if (fifthHour == 0) {
                    fifthHour = 21;
                }
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                entity.setSecondHour(secondHour);
                entity.setSecondMinute(secondMinute);
                entity.setThirdHour(thirdHour);
                entity.setThirdMinute(thirdMinute);
                entity.setForthHour(forthHour);
                entity.setForthMinute(forthMinute);
                entity.setFifthHour(fifthHour);
                entity.setFifthMinute(fifthMinute);
                break;
        }

        entity.setDose(Integer.parseInt(etDose.getText().toString().trim()));
        if (id == -1) {
            entity.save();
        } else {
            entity.updateAll("id=?", id + "");
        }
        startService(new Intent(this, RemindService.class));
        finish();
    }


    private void SetSelectCountDialog() {
        new AlertDialog.Builder(this).setTitle("选择次数").setSingleChoiceItems(
                new String[]{"一次", "二次", "三次", "四次", "五次"}, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        whichSelect = which;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvCount.setText(whichSelect + 1 + "");
                defaultView();
                switch (whichSelect) {
                    case 0:
                        rlFirst.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        rlFirst.setVisibility(View.VISIBLE);
                        rlSecond.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        rlFirst.setVisibility(View.VISIBLE);
                        rlSecond.setVisibility(View.VISIBLE);
                        rlThird.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        rlFirst.setVisibility(View.VISIBLE);
                        rlSecond.setVisibility(View.VISIBLE);
                        rlThird.setVisibility(View.VISIBLE);
                        rlForth.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        rlFirst.setVisibility(View.VISIBLE);
                        rlSecond.setVisibility(View.VISIBLE);
                        rlThird.setVisibility(View.VISIBLE);
                        rlForth.setVisibility(View.VISIBLE);
                        rlFifth.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }).setNegativeButton("取消", null).show();
    }


}
