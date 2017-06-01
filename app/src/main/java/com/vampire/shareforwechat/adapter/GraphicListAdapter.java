package com.vampire.shareforwechat.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kidney_hospital.base.contract.OnSubitemClickListener;
import com.vampire.shareforwechat.BaseApp;
import com.vampire.shareforwechat.R;
import com.vampire.shareforwechat.activity.PhotoDetailActivity;
import com.vampire.shareforwechat.model.material.GraphicEntity;
import com.vampire.shareforwechat.util.LocalTime;
import com.vampire.shareforwechat.view.MyGridView;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class GraphicListAdapter extends BaseRecyclerViewAdapter<GraphicEntity.ArticleListBean> {
    public static final int TYPE_MATERIAL = 2;
    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 3;// 默认展示最大行数3行
    private static final int SHOW_CONTENT_NONE_STATE = 0;// 扩充
    private static final int SHRINK_UP_STATE = 1;// 收起状态
    private static final int SPREAD_STATE = 2;// 展开状态
    private static int mState = SHRINK_UP_STATE;//默认收起状态
    public static final String TRANSITION_ANIMATION_NEWS_PHOTOS = "transition_animation_news_photos";

    private String[] photos;
    private Context context;
    private PhotoAdapter photoAdapter;

    public GraphicListAdapter(List<GraphicEntity.ArticleListBean> list, Context context) {
        super(list);
        this.context = context;
    }

    private OnSubitemClickListener onSubitemClickListener;

    public void setOnSubitemClickListener(OnSubitemClickListener onSubitemClickListener) {
        this.onSubitemClickListener = onSubitemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mIsShowFooter && isFooterPosition(position)) {
            return TYPE_FOOTER;
        } else if (mList.get(position).getArticle().getType() == 1) {//图文
            return TYPE_ITEM;
        } else {
            return TYPE_MATERIAL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return new FooterViewHolder(getView(parent, R.layout.item_footer));
            case TYPE_ITEM:
            default:
                NormalViewHolder normalViewHolder = new NormalViewHolder(getView(parent, R.layout.item_graphic_list));
                return normalViewHolder;
            case TYPE_MATERIAL:
                MaterialViewHolder materialViewHolder = new MaterialViewHolder(getView(parent, R.layout.item_material_list));
                return materialViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_ITEM:
                updateNormalViews((NormalViewHolder) holder, position);
                break;
            case TYPE_MATERIAL:
                updateMaterialViews((MaterialViewHolder) holder, position);
                break;
        }
    }

    private void updateMaterialViews(MaterialViewHolder holder, int position) {
        holder.pos = position;
        holder.onSubitemClickListener = onSubitemClickListener;
        GraphicEntity.ArticleListBean data = mList.get(position);
        holder.tvContent.setText(data.getArticle().getTitle());
        holder.tvCount.setText("转发:" + data.getArticle().getCount());
        try {
            holder.tvTime.setText(LocalTime.LocalTimes(data.getArticle().getCtime() + ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvMaterial.setText(data.getArticle().getContent());
        Glide.with(context).load(data.getArticle().getPicUrl()).into(holder.ivMaterial);

    }

    private void updateNormalViews(final NormalViewHolder holder, int position) {
        holder.pos = position;
        holder.onSubitemClickListener = onSubitemClickListener;
        GraphicEntity.ArticleListBean data = mList.get(position);
        holder.tvContent.setText(data.getArticle().getTitle());
        holder.tvCount.setText("转发:" + data.getArticle().getCount());
        if (data.getArticle().getAnswer().equals("")) {
            holder.tvAnswer.setVisibility(View.GONE);
        } else {
            holder.tvAnswer.setVisibility(View.VISIBLE);
        }
        try {
            holder.tvTime.setText(LocalTime.LocalTimes(data.getArticle().getCtime() + ""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!data.getPictures().equals("") && data.getPictures() != null) {
            photos = data.getPictures().split(",");
            photoAdapter = new PhotoAdapter(photos);
            holder.gvPhotos.setAdapter(photoAdapter);
        }
        jumpToDetailPhoto(holder, data);
    }

    private void jumpToDetailPhoto(NormalViewHolder holder, GraphicEntity.ArticleListBean data) {
        final String[] photos = data.getPictures().split(",");
        holder.gvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = PhotoDetailActivity.getPhotoDetailIntent(context, photos[position]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ImageView animationIv = (ImageView) view.findViewById(R.id.iv_grid);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            (Activity) context,
                            animationIv,
                            TRANSITION_ANIMATION_NEWS_PHOTOS);
                    context.startActivity(intent, options.toBundle());
                } else {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                            view,
                            view.getWidth() / 2,
                            view.getHeight() / 2,
                            0,
                            0);
                    ActivityCompat.startActivity((Activity) context, intent, optionsCompat.toBundle());
                }
            }
        });
    }




    static class NormalViewHolder extends RecyclerView.ViewHolder {
        private int pos;
        private OnSubitemClickListener onSubitemClickListener;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.gv_photos)
        MyGridView gvPhotos;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.tv_edit)
        TextView tvEdit;
        @BindView(R.id.tv_answer)
        TextView tvAnswer;
        @BindView(R.id.iv_share)
        ImageView ivShare;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @OnClick({R.id.tv_delete, R.id.tv_edit, R.id.tv_answer, R.id.iv_share})
        public void onClick(View view) {
            if (onSubitemClickListener != null) {
                onSubitemClickListener.onSubitemClick(pos, view);
            }
        }

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PhotoAdapter extends BaseAdapter {
        public String[] getPhotos() {
            return photos;
        }

        private String[] photos;

        public PhotoAdapter(String[] photos) {
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageHolder imageHolder;
            if (convertView == null) {
                convertView = View.inflate(BaseApp.getApplicationCotext(), R.layout.item_graphic_photos, null);
                imageHolder = new ImageHolder();
                imageHolder.ivGrid = (ImageView) convertView.findViewById(R.id.iv_grid);
                convertView.setTag(imageHolder);
            } else {
                imageHolder = (ImageHolder) convertView.getTag();
            }
            Glide.with(context).load(photos[position]).into(imageHolder.ivGrid);
            return convertView;
        }
    }

    class ImageHolder {
        ImageView ivGrid;
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        private int pos;
        private OnSubitemClickListener onSubitemClickListener;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_material)
        ImageView ivMaterial;
        @BindView(R.id.tv_material)
        TextView tvMaterial;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_delete)
        TextView tvDelete;
        @BindView(R.id.tv_edit)
        TextView tvEdit;
        @BindView(R.id.iv_share)
        ImageView ivShare;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @OnClick({R.id.rl_meterial,R.id.tv_delete, R.id.tv_edit, R.id.iv_share})
        public void onClick(View view) {
            if (onSubitemClickListener != null) {
                onSubitemClickListener.onSubitemClick(pos, view);
            }
        }

        public MaterialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
