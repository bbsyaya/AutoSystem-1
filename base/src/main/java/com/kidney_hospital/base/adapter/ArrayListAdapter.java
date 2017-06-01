package com.kidney_hospital.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @time : 2015年12月9日 下午3:04:01
 * @desicription : 适配器基类
 */
public abstract class ArrayListAdapter<T> extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int layoutResId;

    public ArrayListAdapter(Context context, int layoutResId) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.layoutResId = layoutResId;
    }

    private List<T> data;

    public void removeData(int pos){
        if(data!=null && data.size()!=0){
            data.remove(pos);
            notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public T getItem(int pos) {
        if (data != null && pos >= 0) {
            return data.get(pos);
        }
        return null;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object viewHolder;
        if (convertView == null) {
            convertView = getInflater().inflate(layoutResId, parent, false);
            viewHolder = createViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = convertView.getTag();
        }
        bindViewHolder(position, viewHolder);
        return convertView;
    }

    public abstract void bindViewHolder(int position, Object viewHolder);

    public abstract Object createViewHolder(View convertView);


}
