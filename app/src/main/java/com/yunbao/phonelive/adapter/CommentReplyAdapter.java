package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.custom.TextRender;
import com.yunbao.phonelive.event.ReplyCommentLikeEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by cxf on 2017/9/7.
 */

public class CommentReplyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<CommentBean> mList;
    private CommentBean mHostBean;
    private LayoutInflater mInflater;
    private HeadVh mHeadVh;
    private OnItemClickListener<CommentBean> mOnItemClickListener;
    private final int HEAD = 0;
    private final int NORMAL = 1;
    private RecyclerView mRecyclerView;

    private Animation mAnimation1;
    private Animation mAnimation2;
    private long mLastClickTime;
    private ImageView mHeartView;
    private int mIsLikeStatus;
    private RefreshLayout mRefreshLayout;
    private String mReplyString;

    public CommentReplyAdapter(Context context, CommentBean hostBean, List<CommentBean> list) {
        mContext = context;
        mHostBean = hostBean;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
        mReplyString = WordUtil.getString(R.string.reply);
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
                if (mRefreshLayout != null) {
                    mRefreshLayout.setScrollEnable(false);
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
                if (mRefreshLayout != null) {
                    mRefreshLayout.setScrollEnable(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener<CommentBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void refreshList(List<CommentBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setReplyCount(String count) {
        mHeadVh.setReplyCount(count);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void insertList(List<CommentBean> list) {
        int p = mList.size() + 1;
        mList.addAll(list);
        notifyItemRangeInserted(p, list.size());
        notifyItemRangeChanged(p, list.size());
        mRecyclerView.scrollToPosition(p);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            if (mHeadVh == null) {
                mHeadVh = new HeadVh(mInflater.inflate(R.layout.view_reply_head, parent, false));
                mHeadVh.setReplyCount(mHostBean.getReplys() + "");
                mHeadVh.setIsRecyclable(false);
            }
            return mHeadVh;
        }
        return new Vh(mInflater.inflate(R.layout.view_comment_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (holder instanceof Vh) {
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            ((Vh) holder).setData(mList.get(position - 1), position, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder {
        ImageView head;
        TextView name;
        TextView time;
        TextView likes;
        TextView content;
        TextView mReplyCount;
        ImageView heart;

        public HeadVh(View itemView) {
            super(itemView);
            head = (ImageView) itemView.findViewById(R.id.head_img);
            name = (TextView) itemView.findViewById(R.id.name);
            time = (TextView) itemView.findViewById(R.id.time);
            likes = (TextView) itemView.findViewById(R.id.like_num);
            content = (TextView) itemView.findViewById(R.id.content);
            mReplyCount = (TextView) itemView.findViewById(R.id.reply_count);
            heart = (ImageView) itemView.findViewById(R.id.heart);
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
                    UserBean u = mHostBean.getUserinfo();
                    if (u == null || AppConfig.getInstance().getUid().equals(u.getId())) {
                        ToastUtil.show(WordUtil.getString(R.string.cannot_like_self));
                        return;
                    }
                    HttpUtil.setCommentLike(mHostBean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                int islike = obj.getIntValue("islike");
                                String likes = obj.getString("likes");
                                mHostBean.setIslike(islike);
                                mHostBean.setLikes(likes);
                                EventBus.getDefault().post(new ReplyCommentLikeEvent(mHostBean.getId(), islike, likes));
                                setData("payload");
                                if (mAnimation1 != null) {
                                    mIsLikeStatus = islike;
                                    mHeartView = heart;
                                    mHeartView.startAnimation(mAnimation1);
                                }
                            }
                        }
                    });
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mHostBean, 0);
                    }
                }
            });
            setData(null);
        }

        void setData(Object payload) {
            if (payload == null) {
                UserBean u = mHostBean.getUserinfo();
                ImgLoader.display(u.getAvatar(), head);
                name.setText(u.getUser_nicename());
                time.setText(mHostBean.getDatetime());
                content.setText(TextRender.renderComment(mHostBean));
            }
            likes.setText(mHostBean.getLikes());
            if (mHostBean.getIslike() == 1) {
                heart.setImageResource(R.mipmap.icon_comment_zan_1);
                likes.setTextColor(0xffea377f);
            } else {
                heart.setImageResource(R.mipmap.icon_comment_zan_0);
                likes.setTextColor(0xff969696);
            }
        }

        void setReplyCount(String replyCount) {
            mReplyCount.setText("全部回复(" + replyCount + ")");
        }
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView head;
        TextView name;
        TextView time;
        TextView content;
        TextView question;
        TextView likes;
        ImageView heart;
        CommentBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            head = (ImageView) itemView.findViewById(R.id.head_img);
            name = (TextView) itemView.findViewById(R.id.name);
            time = (TextView) itemView.findViewById(R.id.time);
            content = (TextView) itemView.findViewById(R.id.content);
            question = (TextView) itemView.findViewById(R.id.question);

            likes = (TextView) itemView.findViewById(R.id.like_num);
            heart = (ImageView) itemView.findViewById(R.id.heart);
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
                                mBean.setIslike(islike);
                                mBean.setLikes(obj.getString("likes"));
                                notifyItemChanged(mPosition, "payload");
                                if (mAnimation1 != null) {
                                    mIsLikeStatus = islike;
                                    mHeartView = heart;
                                    mHeartView.startAnimation(mAnimation1);
                                }
                            }
                        }
                    });
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mBean, mPosition);
                    }
                }
            });
        }

        void setData(CommentBean bean, final int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                UserBean u = bean.getUserinfo();
                if (u != null) {
                    ImgLoader.display(u.getAvatar(), head);
                    name.setText(u.getUser_nicename());
                }
                time.setText(bean.getDatetime());

                if (!bean.getCommentid().equals(bean.getParentid())) {
                    UserBean tou = bean.getTouserinfo();
                    if (tou != null) {
                        if (question.getVisibility() != View.VISIBLE) {
                            question.setVisibility(View.VISIBLE);
                        }
                        String toName = tou.getUser_nicename() + "：";
                        content.setText(TextRender.renderComment2(mReplyString + " ", toName, mBean));
                        CommentBean.ToCommentInfo toCommentInfo = bean.getTocommentinfo();
                        if (toCommentInfo != null) {
                            question.setText(TextRender.renderComment3(toName, toCommentInfo.getContent(), toCommentInfo.getAt_info()));
                        }

                    } else {
                        content.setText(mBean.getContent());
                        if (question.getVisibility() == View.VISIBLE) {
                            question.setVisibility(View.GONE);
                        }
                    }
                } else {
                    content.setText(TextRender.renderComment(bean));
                    if (question.getVisibility() == View.VISIBLE) {
                        question.setVisibility(View.GONE);
                    }
                }
                if (bean.getIslike() == 1) {
                    heart.setImageResource(R.mipmap.icon_comment_zan_1);
                } else {
                    heart.setImageResource(R.mipmap.icon_comment_zan_0);
                }
            }
            likes.setText(bean.getLikes());
            if (bean.getIslike() == 1) {
                likes.setTextColor(0xffea377f);
            } else {
                likes.setTextColor(0xff969696);
            }

        }
    }

    public void setRefreshLayout(RefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
    }
}
