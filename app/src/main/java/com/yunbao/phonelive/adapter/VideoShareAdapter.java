package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ShareBean;
import com.yunbao.phonelive.custom.ImageTextView;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/6/12.
 */

public class VideoShareAdapter extends RecyclerView.Adapter<VideoShareAdapter.Vh> {

    private static final int MAX_VISIBLE_COUNT = 4;
    private Context mContext;
    private List<ShareBean> mList;
    private LayoutInflater mInflater;
    private int mItemWidth;
    private boolean mShowText;
    private boolean mCheckable;
    public OnItemClickListener<ShareBean> mOnItemClickListener;
    private int mCheckedPosition = -1;

    public VideoShareAdapter(Context context, List<ShareBean> list) {
        this(context, list, true, false);
    }

    public VideoShareAdapter(Context context, List<ShareBean> list, boolean showText, boolean checkable) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mShowText = showText;
        mCheckable = checkable;
    }

    public void setOnItemClickListener(OnItemClickListener<ShareBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (mShowText) {
            itemView = mInflater.inflate(R.layout.item_list_video_share, parent, false);
        } else {
            itemView = mInflater.inflate(R.layout.item_list_video_share_2, parent, false);
        }
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.width = mItemWidth;
        itemView.setLayoutParams(params);
        return new Vh(itemView);
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

        ImageTextView mImageTextView;
        ShareBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mImageTextView = (ImageTextView) itemView;
            mImageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckable) {
                        if (mCheckedPosition == mPosition) {
                            mBean.setChecked(false);
                            notifyItemChanged(mCheckedPosition, "payload");
                            mCheckedPosition = -1;
                        } else {
                            if (mCheckedPosition >= 0) {
                                mList.get(mCheckedPosition).setChecked(false);
                                notifyItemChanged(mCheckedPosition, "payload");
                            }
                            mBean.setChecked(true);
                            notifyItemChanged(mPosition, "payload");
                            mCheckedPosition = mPosition;
                        }
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(ShareBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                mImageTextView.setCheckedImageSrc(bean.getCheckedIcon());
                mImageTextView.setImageSrc(bean.getUnCheckedIcon());
                if (mShowText) {
                    mImageTextView.setText(bean.getText());
                }
                if (mCheckable) {
                    mImageTextView.setChecked(bean.isChecked());
                } else {
                    mImageTextView.setChecked(true);
                }
            } else {
                if (mCheckable) {
                    mImageTextView.setChecked(bean.isChecked());
                }
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mItemWidth = DpUtil.dp2px(80);
        } else {
            int size = mList.size();
            size = size > MAX_VISIBLE_COUNT ? MAX_VISIBLE_COUNT : size;
            mItemWidth = recyclerView.getWidth() / size;
        }
    }
}
