package com.vampire.shareforwechat.supervisor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidney_hospital.base.util.DateUtils;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.adapter.BaseRecyclerViewAdapter;
import com.vampire.shareforwechat.util.GlideUtil;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by 焕焕 on 2017/5/22.
 */

public class SupevisorAdapter extends BaseRecyclerViewAdapter<SupevisorEntity> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public SupevisorAdapter(List<SupevisorEntity> list,Context context) {
        super(list);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NormalViewHolder normalViewHolder = new NormalViewHolder(getView(parent, R.layout.item_supevisor_list));
        return normalViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        updateNormalViews((NormalViewHolder) holder, position);
    }

    private void updateNormalViews(NormalViewHolder holder, int position) {
        holder.pos = position;
        holder.onItemClickListener = onItemClickListener;
        holder.onItemLongClickListener = onItemLongClickListener;
        SupevisorEntity data = mList.get(position);
        GlideUtil.loadCircleAvatar(context,data.getPhotoPath(),holder.ivAvatar);
        holder.tvRemark.setText(data.getRemark());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, data.getYear());
        calendar.set(Calendar.MONTH, data.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, data.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, data.getHour());
        calendar.set(Calendar.MINUTE, data.getMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            holder.tvPassed.setVisibility(View.VISIBLE);
        }else{
            holder.tvPassed.setVisibility(View.GONE);
        }
        holder.tvTime.setText(DateUtils.timeStampToStr(calendar.getTimeInMillis()));

    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {
        private int pos;
        private OnItemClickListener onItemClickListener;
        private OnItemLongClickListener onItemLongClickListener;
        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_remark)
        TextView tvRemark;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_passed)
        TextView tvPassed;
        @BindView(R.id.ll_supevisor)
        LinearLayout llSupevisor;
        @OnClick({R.id.ll_supevisor})
        public void onClick(View view) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(pos, view);
        }

        @OnLongClick(R.id.ll_supevisor)
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(pos, view);
            }
            return true;
        }
        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position, View v);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(int position, View v);
    }
}
