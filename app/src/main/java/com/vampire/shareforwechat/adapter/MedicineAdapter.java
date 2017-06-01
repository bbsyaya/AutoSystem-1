package com.vampire.shareforwechat.adapter;

import android.content.Context;

import com.kidney_hospital.base.adapter.common.CommonAdapter;
import com.kidney_hospital.base.adapter.common.ViewHolder;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.model.alarm.RemindEntity;

import java.util.List;

import static com.kidney_hospital.base.util.TextUtils.addZero;

/**
 * Created by 焕焕 on 2017/5/18.
 */

public class MedicineAdapter extends CommonAdapter<RemindEntity> {
    public MedicineAdapter(Context context, List<RemindEntity> mList, int itemLayoutId) {
        super(context, mList, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder holder, RemindEntity item, int positon) {
        holder.setTextViewText(R.id.tv_name,item.getContent());
        holder.setTextViewText(R.id.tv_dose,"每日"+item.getCountAll()+"次 每次"+item.getDose()+"片 "+item.getTreatment()+"天一疗程");
        holder.setTextViewText(R.id.tv_first,addZero(item.getFirstHour())+":"+addZero(item.getFirstMinute()));
        holder.setTextViewText(R.id.tv_second,addZero(item.getSecondHour())+":"+addZero(item.getSecondMinute()));
        holder.setTextViewText(R.id.tv_third,addZero(item.getThirdHour())+":"+addZero(item.getThirdMinute()));
        holder.setTextViewText(R.id.tv_forth,addZero(item.getForthHour())+":"+addZero(item.getForthMinute()));
        holder.setTextViewText(R.id.tv_fifth,addZero(item.getFifthHour())+":"+addZero(item.getFifthMinute()));
        if (item.getSecondHour()==0){
            holder.setTextViewText(R.id.tv_second,"");
        }
        if (item.getThirdHour()==0){
            holder.setTextViewText(R.id.tv_third,"");
        }
        if (item.getForthHour()==0){
            holder.setTextViewText(R.id.tv_forth,"");
        }
        if (item.getFifthHour()==0){
            holder.setTextViewText(R.id.tv_fifth,"");
        }
    }
}
