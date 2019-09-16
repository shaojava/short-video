package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class MusicAdapter extends RefreshAdapter<MusicBean> {

    private RefreshView mRefreshView;
    private ActionListener mActionListener;
    private int mCheckedPosition = -1;
    private long mLastClickTime;
    private Animation mAnimation1;
    private Animation mAnimation2;
    private ImageView mStarView;
    private int mCollectResult;

    public MusicAdapter(Context context) {
        super(context);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        mAnimation1 = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation1.setDuration(300);
        mAnimation1.setInterpolator(linearInterpolator);
        mAnimation2 = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation2.setDuration(300);
        mAnimation2.setInterpolator(linearInterpolator);
        mAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mRefreshView != null) {
                    mRefreshView.setScrollEnable(false);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mStarView != null) {
                    if (mCollectResult == 1) {
                        mStarView.setImageResource(R.mipmap.icon_collect_selected);
                    } else {
                        mStarView.setImageResource(R.mipmap.icon_collect_unselected);
                    }
                    mStarView.startAnimation(mAnimation2);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mRefreshView != null) {
                    mRefreshView.setScrollEnable(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_music, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    /**
     * 显示使用按钮
     */
    public void expand(int position) {
        if (position >= 0 && position < mList.size()) {
            MusicBean bean = mList.get(position);
            if (bean != null) {
                bean.setExpand(true);
                notifyItemChanged(position, "payload");
            }
        }
    }

    /**
     * 隐藏使用按钮
     */
    public void collapse() {
        if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
            mList.get(mCheckedPosition).setExpand(false);
            notifyItemChanged(mCheckedPosition, "payload");
            mCheckedPosition = -1;
        }
    }

    /**
     * 收藏数据发生变化
     */
    public void collectChanged(MusicAdapter adapter, int musicId, int isCollect) {
        if (adapter != this) {
            for (int i = 0, size = mList.size(); i < size; i++) {
                MusicBean bean = mList.get(i);
                if (bean != null && bean.getId() == musicId) {
                    bean.setIscollect(isCollect);
                    notifyItemChanged(i, "payload");
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mTitle;
        TextView mAuthor;
        TextView mLength;
        ImageView mBtnCollect;
        View mBtnUse;
        ImageView mBtnPlay;
        MusicBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAuthor = (TextView) itemView.findViewById(R.id.author);
            mLength = (TextView) itemView.findViewById(R.id.length);
            mBtnPlay = (ImageView) itemView.findViewById(R.id.btn_play);
            mBtnCollect = (ImageView) itemView.findViewById(R.id.btn_collect);
            mBtnUse = itemView.findViewById(R.id.btn_use);
            mBtnCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long curTime = System.currentTimeMillis();
                    if (curTime - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = curTime;
                    mStarView = mBtnCollect;
                    HttpUtil.setMusicCollect(String.valueOf(mBean.getId()), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                int isCollect = JSON.parseObject(info[0]).getIntValue("iscollect");
                                mBean.setIscollect(isCollect);
                                mCollectResult = isCollect;
                                if (mStarView != null) {
                                    mStarView.startAnimation(mAnimation1);
                                }
                                if (mActionListener != null) {
                                    mActionListener.onCollect(MusicAdapter.this, mBean.getId(), isCollect);
                                }
                            }
                        }
                    });
                }
            });
            mBtnUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckedPosition >= 0) {
                        mList.get(mCheckedPosition).setExpand(false);
                        notifyItemChanged(mCheckedPosition, "payload");
                        mCheckedPosition = -1;
                        if (mActionListener != null) {
                            mActionListener.onStopMusic();
                        }
                    }
                    if (mActionListener != null) {
                        mActionListener.onUseClick(mBean);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckedPosition == mPosition) {
                        mBean.setExpand(false);
                        notifyItemChanged(mPosition, "payload");
                        mCheckedPosition = -1;
                        if (mActionListener != null) {
                            mActionListener.onStopMusic();
                        }
                    } else {
                        if (mCheckedPosition >= 0) {
                            mList.get(mCheckedPosition).setExpand(false);
                            notifyItemChanged(mCheckedPosition, "payload");
                            if (mActionListener != null) {
                                mActionListener.onStopMusic();
                            }
                        }
                        mCheckedPosition = mPosition;
                        if (mActionListener != null) {
                            mActionListener.onPlayMusic(MusicAdapter.this, mBean, mPosition);
                        }
                    }
                }
            });
        }

        void setData(MusicBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                ImgLoader.display(bean.getImg_url(), mImg);
                mTitle.setText(bean.getTitle());
                mAuthor.setText(bean.getAuthor());
                mLength.setText(bean.getLength());
            }
            if (bean.getIscollect() == 1) {
                mBtnCollect.setImageResource(R.mipmap.icon_collect_selected);
            } else {
                mBtnCollect.setImageResource(R.mipmap.icon_collect_unselected);
            }
            if (bean.isExpand()) {
                mBtnPlay.setImageResource(R.mipmap.icon_music_pause);
                if (mBtnUse.getVisibility() != View.VISIBLE) {
                    mBtnUse.setVisibility(View.VISIBLE);
                }
            } else {
                mBtnPlay.setImageResource(R.mipmap.icon_music_play);
                if (mBtnUse.getVisibility() == View.VISIBLE) {
                    mBtnUse.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface ActionListener {
        void onPlayMusic(MusicAdapter adapter, MusicBean bean, int position);

        void onStopMusic();

        void onUseClick(MusicBean bean);

        void onCollect(MusicAdapter adapter, int musicId, int isCollect);
    }

    public void setRefreshView(RefreshView refreshView) {
        mRefreshView = refreshView;
    }
}
