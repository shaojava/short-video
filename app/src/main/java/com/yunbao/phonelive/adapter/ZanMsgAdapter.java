package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ZanMsgBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/7/21.
 */

public class ZanMsgAdapter extends RefreshAdapter<ZanMsgBean> {

    private String mZanVideoString;
    private String mZanCommentString;
    private ActionListener mActionListener;

    public ZanMsgAdapter(Context context) {
        super(context);
        mZanVideoString = WordUtil.getString(R.string.zan_video);
        mZanCommentString = WordUtil.getString(R.string.zan_comment);
    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_zan_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position), position);
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mTime;
        ImageView mVideoCover;
        View mLine;
        ZanMsgBean mBean;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mVideoCover = (ImageView) itemView.findViewById(R.id.img);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mLine = itemView.findViewById(R.id.line);
            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick()) {
                        return;
                    }
                    if (mActionListener != null) {
                        mActionListener.onAvatarClick(mBean);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick()) {
                        return;
                    }
                    if (mActionListener != null) {
                        mActionListener.onItemClick(mBean);
                    }
                }
            });
            mVideoCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick()) {
                        return;
                    }
                    if (mActionListener != null) {
                        mActionListener.onVideoClick(mBean);
                    }
                }
            });
        }

        void setData(ZanMsgBean bean, int position) {
            mBean = bean;
            ImgLoader.display(bean.getAvatar(), mAvatar);
            ImgLoader.display(bean.getVideo_thumb(), mVideoCover);
            if (bean.getType() == ZanMsgBean.TYPE_VIDEO) {
                mName.setText(Html.fromHtml(bean.getUser_nicename() + "<font color='#969696'>" + mZanVideoString + "</font>"));
            } else {
                mName.setText(Html.fromHtml(bean.getUser_nicename() + "<font color='#969696'>" + mZanCommentString + "</font>"));
            }
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

    public interface ActionListener {
        void onAvatarClick(ZanMsgBean bean);

        void onItemClick(ZanMsgBean bean);

        void onVideoClick(ZanMsgBean bean);
    }
}
