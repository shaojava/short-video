package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.UserCenterActivity;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.jpush.JMessageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/7/13.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Vh> {

    private static final int HEAD = 1;
    private static final int MIDDLE = 2;
    private static final int FOOT = 3;
    private Context mContext;
    private List<ChatUserBean> mList;
    private List<ChatUserBean> mAdminChatBeanList;
    private LayoutInflater mInflater;
    private OnItemClickListener<ChatUserBean> mOnItemClickListener;
    private long mLastClickTime;

    public MessageAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mAdminChatBeanList = new ArrayList<>();
        ChatUserBean cub1 = new ChatUserBean();
        cub1.setId(Constants.YB_ID_1);
        cub1.setUser_nicename(Constants.YB_NAME_1);
        cub1.setFromType(ChatUserBean.TYPE_SYSTEM);
        JMessageUtil util = JMessageUtil.getInstance();
        ChatUserBean cubInfo1 = util.getLastMessageInfo(cub1);
        if (cubInfo1 != null) {
            cub1 = cubInfo1;
        }
        ChatUserBean cub2 = new ChatUserBean();
        cub2.setId(Constants.YB_ID_2);
        cub2.setUser_nicename(Constants.YB_NAME_2);
        cub2.setFromType(ChatUserBean.TYPE_SYSTEM);
        ChatUserBean cubInfo2 = util.getLastMessageInfo(cub2);
        if (cubInfo2 != null) {
            cub2 = cubInfo2;
        }
        mAdminChatBeanList.add(cub1);
        mAdminChatBeanList.add(cub2);
        mList.addAll(mAdminChatBeanList);
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener<ChatUserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<ChatUserBean> getAdminChatBeanList() {
        return mAdminChatBeanList;
    }

    public void updateAdminChatInfo() {
        JMessageUtil util = JMessageUtil.getInstance();
        for (ChatUserBean c : mAdminChatBeanList) {
            ChatUserBean info = util.getLastMessageInfo(c);
            if (info != null) {
                updateItem(c.getId());
            }
        }
    }

    public void insertList(List<ChatUserBean> list) {
        int p = mList.size();
        mList.addAll(list);
        notifyItemRangeInserted(p, list.size());
    }

    public void insertItem(ChatUserBean bean) {
        int p = mList.size();
        mList.add(bean);
        notifyItemInserted(p);
    }

    public void updateItem(String touid) {
        if (!TextUtils.isEmpty(touid)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (touid.equals(mList.get(i).getId())) {
                    notifyItemChanged(i, "payload");
                    break;
                }
            }
        }
    }

    public void updateItem(int position) {
        notifyItemChanged(position, "payload");
    }

    public void clearData() {
        mList.clear();
        mList.addAll(mAdminChatBeanList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        } else if (position == mList.size() - 1) {
            return FOOT;
        } else {
            return MIDDLE;
        }
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = 0;
        if (viewType == HEAD) {
            layoutId = R.layout.item_list_msg_head;
        } else if (viewType == FOOT) {
            layoutId = R.layout.item_list_msg_foot;
        } else {
            layoutId = R.layout.item_list_msg;
        }
        return new Vh(mInflater.inflate(layoutId, parent, false));
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


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mLastMsg;
        TextView mTime;
        View mSys;
        TextView mRedPoint;
        ChatUserBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mLastMsg = (TextView) itemView.findViewById(R.id.last_msg);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mSys = itemView.findViewById(R.id.sys);
            mRedPoint = (TextView) itemView.findViewById(R.id.red_point);
            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBean.getFromType() == ChatUserBean.TYPE_NORMAL) {
                        UserCenterActivity.forwardOtherUserCenter(mContext, mBean.getId());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long curTime = System.currentTimeMillis();
                    if (curTime - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = curTime;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(ChatUserBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                if (bean.getFromType() == ChatUserBean.TYPE_SYSTEM) {
                    if (Constants.YB_ID_1.equals(bean.getId())) {
                        mAvatar.setImageResource(R.mipmap.ic_launcher);
                    } else if (Constants.YB_ID_2.equals(bean.getId())) {
                        mAvatar.setImageResource(R.mipmap.icon_msg_sys_2);
                    }
                    if (mSys.getVisibility() != View.VISIBLE) {
                        mSys.setVisibility(View.VISIBLE);
                    }
                } else {
                    ImgLoader.display(bean.getAvatar(), mAvatar);
                    if (mSys.getVisibility() == View.VISIBLE) {
                        mSys.setVisibility(View.INVISIBLE);
                    }
                }
                mName.setText(bean.getUser_nicename());
            }
            mTime.setText(bean.getLastTime());
            mLastMsg.setText(bean.getLastMessage());
            if (bean.getUnReadCount() > 0) {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
                mRedPoint.setText(String.valueOf(bean.getUnReadCount()));
            } else {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            }
        }

    }
}
