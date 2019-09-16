package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/9/13.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.Vh> {

    private List<VideoBean> mList;
    private LayoutInflater mInflater;

    public TestAdapter(Context context, List<VideoBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_test, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        VideoBean mVideoBean;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
        }

        void setData(VideoBean videoBean){
            mVideoBean=videoBean;
            ImgLoader.display(videoBean.getThumb(),mImg);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        new PagerSnapHelper().attachToRecyclerView(recyclerView);
    }
}
