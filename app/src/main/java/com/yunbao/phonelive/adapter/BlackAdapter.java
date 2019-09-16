package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/7/30.
 */

public class BlackAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;

    public BlackAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    int position = (int) v.getTag();
                    mActionListener.onItemRemoved(mList.get(position).getId(), position);
                }
            }
        };
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size(), "payload");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_black, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mTime;
        View mBtnRemove;
        View mLine;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mLine = itemView.findViewById(R.id.line);
            mBtnRemove = itemView.findViewById(R.id.btn_remove);
            mBtnRemove.setOnClickListener(mOnClickListener);
        }

        void setData(UserBean bean, int position, Object payload) {
            mBtnRemove.setTag(position);
            if (payload == null) {
                ImgLoader.display(bean.getAvatar(), mAvatar);
                mName.setText(bean.getUser_nicename());
                mTime.setText(bean.getAddtime());
            }
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

    public interface ActionListener {
        void onItemRemoved(String toUid, int position);
    }
}
