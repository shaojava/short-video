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
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.CommentBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.custom.TextRender;
import com.yunbao.phonelive.event.ReplyCommentLikeEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2018/6/12.
 */

public class CommentAdapter extends RefreshAdapter<CommentBean> {

    private ActionListener mActionListener;
    private String mAllRelpyString;
    private RefreshView mRefreshView;
    private Animation mAnimation1;
    private Animation mAnimation2;
    private long mLastClickTime;
    private ImageView mHeartView;
    private int mIsLikeStatus;
    private static final int PAYLOAD_1 = 1;
    private static final int PAYLOAD_2 = 2;

    public CommentAdapter(Context context) {
        super(context);
        mAllRelpyString = WordUtil.getString(R.string.all_replys);
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
                if (mHeartView != null) {
                    if (mIsLikeStatus == 1) {
                        mHeartView.setImageResource(R.mipmap.icon_comment_zan_1);
                    } else {
                        mHeartView.setImageResource(R.mipmap.icon_comment_zan_0);
                    }
                    mHeartView.startAnimation(mAnimation2);
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    public void updateItemComments(String commentId, int replys) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            CommentBean bean = mList.get(i);
            if (bean.getId().equals(commentId)) {
                bean.setReplys(replys);
                notifyItemChanged(i, PAYLOAD_1);
                break;
            }
        }
    }

    public void updateItemLike(String commentId, int isLike, String likes) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            CommentBean bean = mList.get(i);
            if (bean.getId().equals(commentId)) {
                bean.setIslike(isLike);
                bean.setLikes(likes);
                notifyItemChanged(i, PAYLOAD_1);
                break;
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        ImageView mImgZan;
        TextView mZanNum;
        TextView mName;
        TextView mContent;
        TextView mTime;
        TextView mReplyNums;
        CommentBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mImgZan = (ImageView) itemView.findViewById(R.id.img_zan);
            mZanNum = (TextView) itemView.findViewById(R.id.zan_num);
            mName = (TextView) itemView.findViewById(R.id.name);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mReplyNums = (TextView) itemView.findViewById(R.id.reply_nums);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionListener != null) {
                        mActionListener.onItemClickListener(mBean, mPosition);
                    }
                }
            });

            mReplyNums.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionListener.onMoreClick(mBean);
                }
            });
            itemView.findViewById(R.id.btn_zan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long timeStamp = System.currentTimeMillis();
                    if (timeStamp - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = timeStamp;
                    if (!AppConfig.getInstance().isLogin()) {
                        ToastUtil.show(WordUtil.getString(R.string.please_login));
                        return;
                    }
                    if (mBean != null) {
                        UserBean u = mBean.getUserinfo();
                        if (u == null || AppConfig.getInstance().getUid().equals(u.getId())) {
                            ToastUtil.show(WordUtil.getString(R.string.cannot_like_self));
                            return;
                        }
                        HttpUtil.setCommentLike(mBean.getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    int islike = obj.getIntValue("islike");
                                    String likes = obj.getString("likes");
                                    mBean.setIslike(islike);
                                    mBean.setLikes(likes);
                                    EventBus.getDefault().post(new ReplyCommentLikeEvent(mBean.getId(), islike, likes));
                                    setData(mBean, mPosition, PAYLOAD_2);
                                    if (mAnimation1 != null) {
                                        mIsLikeStatus = islike;
                                        mHeartView = mImgZan;
                                        mHeartView.startAnimation(mAnimation1);
                                    }
                                }
                            }
                        });
                    }
                }
            });

        }

        void setData(CommentBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            UserBean u = bean.getUserinfo();
            if (payload == null) {
                if (u != null) {
                    ImgLoader.display(u.getAvatar(), mAvatar);
                    mName.setText(u.getUser_nicename());
                }
                mContent.setText(TextRender.renderComment(bean));
                mTime.setText(bean.getDatetime());
                if (bean.getIslike() == 1) {
                    mImgZan.setImageResource(R.mipmap.icon_comment_zan_1);
                } else {
                    mImgZan.setImageResource(R.mipmap.icon_comment_zan_0);
                }
            }else{
                if (((Integer)payload) == PAYLOAD_1) {
                    if (bean.getIslike() == 1) {
                        mImgZan.setImageResource(R.mipmap.icon_comment_zan_1);
                    } else {
                        mImgZan.setImageResource(R.mipmap.icon_comment_zan_0);
                    }
                }
            }
            if (bean.getIslike() == 1) {
                mZanNum.setTextColor(0xffea377f);
            } else {
                mZanNum.setTextColor(0xff969696);
            }
            mZanNum.setText(bean.getLikes());
            if (bean.getReplys() > 0) {
                if (mReplyNums.getVisibility() != View.VISIBLE) {
                    mReplyNums.setVisibility(View.VISIBLE);
                }
                mReplyNums.setText(mAllRelpyString + "(" + bean.getReplys() + ")");
            } else {
                if (mReplyNums.getVisibility() == View.VISIBLE) {
                    mReplyNums.setVisibility(View.GONE);
                }
            }
        }
    }


    public interface ActionListener {
        void onItemClickListener(CommentBean bean, int position);

        void onMoreClick(CommentBean bean);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setRefreshView(RefreshView refreshView) {
        mRefreshView = refreshView;
    }
}
