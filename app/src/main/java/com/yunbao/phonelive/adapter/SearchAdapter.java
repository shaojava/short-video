package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LoginActivity;
import com.yunbao.phonelive.bean.SearchBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.event.NeedRefreshEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2018/6/11.
 */

public class SearchAdapter extends RefreshAdapter<SearchBean> {

    private String mFollowing;
    private String mFollow;
    private String mFansString;
    private OnItemClickListener<SearchBean> mOnItemClickListener;

    public SearchAdapter(Context context) {
        super(context);
        mFollowing = WordUtil.getString(R.string.following);
        mFollow = WordUtil.getString(R.string.follow);
        mFansString = WordUtil.getString(R.string.fans);
    }


    @Override
    public void setOnItemClickListener(OnItemClickListener<SearchBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_search_user, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    private void updateItem(int positon, int attention) {
        SearchBean bean = mList.get(positon);
        if (bean != null) {
            bean.setIsattention(attention);
            notifyItemChanged(positon, "payload");
        }
    }

    public void updateItem(String id, int attention) {
        if (!TextUtils.isEmpty(id)) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                SearchBean bean = mList.get(i);
                if (bean != null) {
                    if (id.equals(bean.getId())) {
                        bean.setIsattention(attention);
                        notifyItemChanged(i, "payload");
                        break;
                    }
                }
            }
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mId;
        TextView mSign;
        TextView mFans;
        RadioButton mBtnFollow;
        SearchBean mBean;
        View mLine;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mId = (TextView) itemView.findViewById(R.id.id_val);
            mSign = (TextView) itemView.findViewById(R.id.sign);
            mFans = (TextView) itemView.findViewById(R.id.fans);
            mLine = itemView.findViewById(R.id.line);
            mBtnFollow = (RadioButton) itemView.findViewById(R.id.btn_follow);
            mBtnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppConfig.getInstance().isLogin())
                    {
                        HttpUtil.setAttention(SearchAdapter.Vh.this.mBean.getId(), new CommonCallback<Integer>()
                        {
                            @Override
                            public void callback(Integer paramAnonymous2Integer)
                            {
                                SearchAdapter.this.updateItem(SearchAdapter.Vh.this.mPosition, paramAnonymous2Integer.intValue());
                                EventBus.getDefault().post(new NeedRefreshEvent());
                            }
                        });
                        return;
                    }
                    SearchAdapter.Vh.this.mBtnFollow.setChecked(false);
                    LoginActivity.forwardLogin(SearchAdapter.this.mContext);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onItemClick(mBean,mPosition);
                    }
                }
            });
        }

        void setData(SearchBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (bean != null) {
                if (payload == null) {
                    ImgLoader.display(bean.getAvatar(), mAvatar);
                    mName.setText(bean.getUser_nicename());
                    mId.setText("ID: " + bean.getId());
                    mSign.setText(bean.getSignature());
                    mFans.setText(mFansString + "：" + bean.getFans());
                    if (!AppConfig.getInstance().isLogin() || !AppConfig.getInstance().getUid().equals(bean.getId())) {
                        if (mBtnFollow.getVisibility() != View.VISIBLE) {
                            mBtnFollow.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (mBtnFollow.getVisibility() == View.VISIBLE) {
                            mBtnFollow.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                if (bean.getIsattention() == 1) {
                    mBtnFollow.setChecked(true);
                    mBtnFollow.setText(mFollowing);
                } else {
                    mBtnFollow.setChecked(false);
                    mBtnFollow.setText(mFollow);
                }
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

}
