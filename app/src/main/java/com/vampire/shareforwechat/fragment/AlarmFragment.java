package com.vampire.shareforwechat.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.model.alarm.RemindEntity;
import com.vampire.shareforwechat.receiver.AlarmReceiver;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.ALARM_SERVICE;
import static com.vampire.shareforwechat.receiver.AlarmReceiver.EXTRA_RECEIVER_CONTENT;

/**
 * Created by 焕焕 on 2017/5/17.
 */

public class AlarmFragment extends AppBaseFragment implements CompoundButton.OnCheckedChangeListener{
    private static final String TAG = "AlarmFragment";
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    @BindView(R.id.btn_first)
    Button btnFirst;
    @BindView(R.id.btn_second)
    Button btnSecond;
    @BindView(R.id.sc_set)
    SwitchCompat scSet;
    private List<RemindEntity> remindList = new ArrayList<>();
    private int firstHour,firstMinute,secondHour,secondMinute;
    private boolean isRemind;
    @Override
    protected void loadData() {

    }

    @Override
    protected void initViews() {
        scSet.setOnCheckedChangeListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_alarm;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isRemind = isChecked;
    }

    @OnClick({R.id.btn_first, R.id.btn_second,R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                RemindEntity entity = new RemindEntity();
                entity.setContent("第一个闹钟");
                entity.setRemind(isRemind);
                entity.setFirstHour(firstHour);
                entity.setFirstMinute(firstMinute);
                entity.setSecondHour(secondHour);
                entity.setSecondMinute(secondMinute);
                entity.save();
                remindList = DataSupport.findAll(RemindEntity.class);

                break;
            case R.id.btn_first:
                new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                btnFirst.setText(hourOfDay + ":" + minute);
                                firstHour = hourOfDay;
                                firstMinute = minute;
                            }
                        },
                        Calendar.HOUR, Calendar.MINUTE, DateFormat.is24HourFormat(getActivity())
                ).show();
                break;
            case R.id.btn_second:
                new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                btnSecond.setText(hourOfDay + ":" + minute);
                                secondHour = hourOfDay;
                                secondHour = minute;
                            }
                        },
                        Calendar.HOUR, Calendar.MINUTE, DateFormat.is24HourFormat(getActivity())
                ).show();
                break;
        }
    }

    private void setAlarm(int hourOfDay, int minute, String content, int requestCode) {
        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra(EXTRA_RECEIVER_CONTENT, content);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.e(TAG, "onTimeSet: " + calendar.getTimeInMillis());
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                INTERVAL, pi);
    }

    private void stopAlarm(int requestCode){
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), requestCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
    }

}
