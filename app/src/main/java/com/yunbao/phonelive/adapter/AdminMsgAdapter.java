package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.AdminMsgBean;
import com.yunbao.phonelive.custom.RefreshAdapter;

/**
 * Created by cxf on 2018/7/27.
 */

public class AdminMsgAdapter extends RefreshAdapter<AdminMsgBean> {

    private View.OnClickListener mOnClickListener;

    public AdminMsgAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminMsgBean bean = (AdminMsgBean) v.getTag();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_admin_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mDesc;
        TextView mTime;
        View mLine;

        public Vh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mDesc = (TextView) itemView.findViewById(R.id.desc);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mLine = itemView.findViewById(R.id.line);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(AdminMsgBean bean, int position) {
            itemView.setTag(bean);
            mTitle.setText(bean.getTitle());
            mDesc.setText(bean.getSynopsis());
            mTime.setText(bean.getAddtime());
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
    }
}
