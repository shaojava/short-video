package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.AtMsgBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/7/21.
 */

public class AtMsgAdapter extends RefreshAdapter<AtMsgBean> {

    private String mAtString;
    private String mZaiString;
    private ActionListener mActionListener;

    public AtMsgAdapter(Context context) {
        super(context);
        mAtString = WordUtil.getString(R.string.at_msg_str);
        mZaiString = WordUtil.getString(R.string.zai);
    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_at_msg, parent, false));
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
        AtMsgBean mBean;

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

        void setData(AtMsgBean bean, int position) {
            mBean = bean;
            ImgLoader.display(bean.getAvatar(), mAvatar);
            ImgLoader.display(bean.getVideo_thumb(), mVideoCover);
            mName.setText(Html.fromHtml(bean.getUser_nicename() + "\t<font color='#969696'>" +mZaiString+bean.getVideo_title()+ mAtString + "</font>"));
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
        void onAvatarClick(AtMsgBean bean);

        void onItemClick(AtMsgBean bean);

        void onVideoClick(AtMsgBean bean);
    }
}
