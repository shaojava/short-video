package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicClassBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by cxf on 2018/7/26.
 */

public class MusicClassAdapter extends RecyclerView.Adapter<MusicClassAdapter.Vh> {

    private List<MusicClassBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<MusicClassBean> mOnItemClickListener;

    public MusicClassAdapter(Context context, List<MusicClassBean> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener<MusicClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_view_music_class, parent, false));
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

        ImageView mImg;
        TextView mText;
        MusicClassBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mText = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(MusicClassBean bean, int position) {
            mBean = bean;
            mPosition = position;
            ImgLoader.display(bean.getImg_url(), mImg);
            mText.setText(bean.getTitle());
        }
    }
}
