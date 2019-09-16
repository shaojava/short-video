package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.TxLocationPoiBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/7/18.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.Vh> {

    private List<TxLocationPoiBean> mList;
    private LayoutInflater mInflater;
    private int mCheckedPosition;
    protected int mLoadMoreHeight;
    private RecyclerView mRecyclerView;
    private OnItemClickListener<TxLocationPoiBean> mOnItemClickListener;

    public LocationAdapter(Context context, List<TxLocationPoiBean> list) {
        mList = list;
        if (mList.size() > 0) {
            mCheckedPosition = 0;
            mList.get(0).setChecked(true);
        }
        mInflater = LayoutInflater.from(context);
        mLoadMoreHeight = DpUtil.dp2px(50);
    }

    public TxLocationPoiBean getCheckedLocationPoiBean() {
        if (mList == null || mList.size() == 0) {
            return null;
        }
        return mList.get(mCheckedPosition);
    }

    public void setList(List<TxLocationPoiBean> list) {
        mList = list;
        if (mList.size() > 0) {
            mCheckedPosition = 0;
            mList.get(0).setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void insertList(List<TxLocationPoiBean> list) {
        if (mRecyclerView != null && mList != null && list != null && list.size() > 0) {
            int p = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(p, list.size());
            mRecyclerView.scrollBy(0, mLoadMoreHeight);
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_location, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

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

    class Vh extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mAddress;
        ImageView mRadioButton;
        TxLocationPoiBean mBean;
        View mLine;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mRadioButton = (ImageView) itemView.findViewById(R.id.radioButton);
            mLine = itemView.findViewById(R.id.line);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckedPosition != mPosition) {
                        mList.get(mCheckedPosition).setChecked(false);
                        mBean.setChecked(true);
                        notifyItemChanged(mCheckedPosition, "payload");
                        notifyItemChanged(mPosition, "payload");
                        mCheckedPosition = mPosition;
                        if(mOnItemClickListener!=null){
                            mOnItemClickListener.onItemClick(mBean,mPosition);
                        }
                    }
                }
            });
        }

        void setData(TxLocationPoiBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                mTitle.setText(bean.getTitle());
                mAddress.setText(bean.getAddress());
                if (position == mList.size() - 1) {
                    if (mLine.getVisibility() == View.VISIBLE) {
                        mLine.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mLine.getVisibility() != View.VISIBLE) {
                        mLine.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (bean.isChecked()) {
                mRadioButton.setImageResource(R.mipmap.icon_checked);
            } else {
                mRadioButton.setImageDrawable(null);
            }

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    public void setOnItemClickListener(OnItemClickListener<TxLocationPoiBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
