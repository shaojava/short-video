package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.VideoChooseBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class VideoChooseAdapter extends RecyclerView.Adapter<VideoChooseAdapter.Vh> {

    private List<VideoChooseBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<VideoChooseBean> mOnItemClickListener;

    public VideoChooseAdapter(Context context, List<VideoChooseBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener<VideoChooseBean> listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_video_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        TextView mDuration;
        VideoChooseBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(VideoChooseBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.displayVideoThumb(bean.getVideoPath(), mCover);
            mDuration.setText(bean.getDurationString());
        }
    }

}
