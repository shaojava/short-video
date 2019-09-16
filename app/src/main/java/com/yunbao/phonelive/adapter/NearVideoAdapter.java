package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/6/7.
 */

public class NearVideoAdapter extends RefreshAdapter<VideoBean> {

    public NearVideoAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_near, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((NearVideoAdapter.Vh) vh).setData(mList.get(position), position, payload);

    }

    public void removeItem(VideoBean videoBean) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            VideoBean bean = mList.get(i);
            if (bean.getId().equals(videoBean.getId())) {
                mList.remove(i);
                notifyItemRemoved(i);
                int size2 = mList.size();
                notifyItemRangeChanged(i, size2, "payload");
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        ImageView mAvatar;
        TextView mName;
        TextView mDistance;
        VideoBean mBean;
        int mPosition;


        public Vh(View itemView) {
            super(itemView);
            mThumb = (ImageView) itemView.findViewById(R.id.thumb);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mDistance = (TextView) itemView.findViewById(R.id.distance);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(VideoBean bean, int position, Object payload) {
            mPosition = position;
            if (payload == null) {
                mBean = bean;
                ImgLoader.display(bean.getThumb(), mThumb);
                UserBean u = bean.getUserinfo();
                ImgLoader.display(u.getAvatar(), mAvatar);
                mName.setText(u.getUser_nicename());
                mDistance.setText(bean.getDistance());
            }
        }
    }
}
