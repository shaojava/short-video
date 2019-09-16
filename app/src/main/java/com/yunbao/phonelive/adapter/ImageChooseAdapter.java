package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ImageChooseBean;
import com.yunbao.phonelive.glide.ImgLoader;

import java.io.File;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class ImageChooseAdapter extends RecyclerView.Adapter<ImageChooseAdapter.Vh> {

    private static final int POSITION_NONE = -1;
    private static final String PAYLOAD = "payload";
    private List<ImageChooseBean> mList;
    private LayoutInflater mInflater;
    private int mSelectedPosition;

    public ImageChooseAdapter(Context context, List<ImageChooseBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mSelectedPosition = POSITION_NONE;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_image_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    public File getSelectedFile() {
        if (mSelectedPosition != POSITION_NONE) {
            return mList.get(mSelectedPosition).getImageFile();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mImg;
        ImageChooseBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPosition != mSelectedPosition) {
                        if (mSelectedPosition == POSITION_NONE) {
                            mBean.setChecked(true);
                            notifyItemChanged(mPosition, PAYLOAD);
                        } else {
                            mList.get(mSelectedPosition).setChecked(false);
                            mBean.setChecked(true);
                            notifyItemChanged(mSelectedPosition, PAYLOAD);
                            notifyItemChanged(mPosition, PAYLOAD);
                        }
                        mSelectedPosition = mPosition;
                    }
                }
            });
        }

        void setData(ImageChooseBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                ImgLoader.display(bean.getImageFile(), mCover);
            }
            if (bean.isChecked()) {
                mImg.setImageResource(R.mipmap.icon_checked);
            } else {
                mImg.setImageResource(R.mipmap.icon_checked_none);
            }
        }
    }

}
