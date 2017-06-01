package com.vampire.shareforwechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.util.ClickUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class GraphicChannelAdapter extends BaseRecyclerViewAdapter<String> {

    private static final String TAG = "GraphicChannelAdapter";

    public void setmOnItemClickListener(MyRecyclerListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    protected MyRecyclerListener mOnItemClickListener;
    public GraphicChannelAdapter(List<String> list) {
        super(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(getView(parent, R.layout.item_graphic_channel));
        handleOnClick(holder);
        return holder;
    }
    private void handleOnClick(final MyViewHolder holder) {
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick: " );
                    if(!ClickUtil.isFastDoubleClick()){
                        mOnItemClickListener.OnItemClickListener(v,holder.getLayoutPosition());
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.tvChannel.setText(mList.get(position));
        viewHolder.tvChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ClickUtil.isFastDoubleClick()){
                    mOnItemClickListener.OnItemClickListener(v,viewHolder.getLayoutPosition());
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_channel)
        TextView tvChannel;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
