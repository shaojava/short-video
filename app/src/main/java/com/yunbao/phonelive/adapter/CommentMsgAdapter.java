package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.CommentMsgBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/7/21.
 */

public class CommentMsgAdapter extends RefreshAdapter<CommentMsgBean> {

    private String mCommentVideoString;
    private ActionListener mActionListener;

    public CommentMsgAdapter(Context context) {
        super(context);
        mCommentVideoString = WordUtil.getString(R.string.comment_video);
    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_comment_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position), position);
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mContent;
        TextView mTime;
        ImageView mVideoCover;
        View mLine;
        CommentMsgBean mBean;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mVideoCover = (ImageView) itemView.findViewById(R.id.img);
            mContent=(TextView) itemView.findViewById(R.id.content);
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

        void setData(CommentMsgBean bean, int position) {
            mBean = bean;
            ImgLoader.display(bean.getAvatar(), mAvatar);
            ImgLoader.display(bean.getVideo_thumb(), mVideoCover);
            mContent.setText(bean.getContent());
            mName.setText(Html.fromHtml(bean.getUser_nicename() + "<font color='#969696'>" + mCommentVideoString + "</font>"));
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
        void onAvatarClick(CommentMsgBean bean);

        void onItemClick(CommentMsgBean bean);

        void onVideoClick(CommentMsgBean bean);
    }
}
