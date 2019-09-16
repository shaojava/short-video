package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.FilterBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/22.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Vh> {

    private List<FilterBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<FilterBean> mOnItemClickListener;
    private int mCheckedPosition;

    public FilterAdapter(Context context) {
        mList = new ArrayList<>();
        mList.add(new FilterBean(FilterBean.FILTER_ORIGINAL, R.mipmap.icon_filter_orginal, 0, true));
        mList.add(new FilterBean(FilterBean.FILTER_LANG_MAN, R.mipmap.icon_filter_langman, R.mipmap.filter_langman));
        mList.add(new FilterBean(FilterBean.FILTER_QING_XIN, R.mipmap.icon_filter_qingxin, R.mipmap.filter_qingxin));
        mList.add(new FilterBean(FilterBean.FILTER_WEI_MEI, R.mipmap.icon_filter_weimei, R.mipmap.filter_weimei));
        mList.add(new FilterBean(FilterBean.FILTER_FEN_NEN, R.mipmap.icon_filter_fennen, R.mipmap.filter_fennen));
        mList.add(new FilterBean(FilterBean.FILTER_HUAI_JIU, R.mipmap.icon_filter_huaijiu, R.mipmap.filter_huaijiu));
        mList.add(new FilterBean(FilterBean.FILTER_QING_LIANG, R.mipmap.icon_filter_qingliang, R.mipmap.filter_qingliang));
        mList.add(new FilterBean(FilterBean.FILTER_LANG_DIAO, R.mipmap.icon_filter_landiao, R.mipmap.filter_landiao));
        mList.add(new FilterBean(FilterBean.FILTER_RI_XI, R.mipmap.icon_filter_rixi, R.mipmap.filter_rixi));
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener<FilterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        for (FilterBean bean : mList) {
            if (bean != null) {
                Bitmap bitmap = bean.getBitmap();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        mList.clear();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mCheckImg;
        FilterBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mCheckImg = (ImageView) itemView.findViewById(R.id.check_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckedPosition != mPosition) {
                        mList.get(mCheckedPosition).setChecked(false);
                        mList.get(mPosition).setChecked(true);
                        notifyItemChanged(mCheckedPosition, "payload");
                        notifyItemChanged(mPosition, "payload");
                        mCheckedPosition = mPosition;
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(mBean, mPosition);
                        }
                    }
                }
            });
        }

        void setData(FilterBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                mImg.setImageResource(bean.getImgSrc());
            }
            if (bean.isChecked()) {
                if (mCheckImg.getVisibility() != View.VISIBLE) {
                    mCheckImg.setVisibility(View.VISIBLE);
                }
            } else {
                if (mCheckImg.getVisibility() == View.VISIBLE) {
                    mCheckImg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
