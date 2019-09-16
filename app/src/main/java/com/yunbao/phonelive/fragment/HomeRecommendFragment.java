package com.yunbao.phonelive.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LoginActivity;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.UserCenterActivity;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.VideoPlayWrap;
import com.yunbao.phonelive.event.FollowEvent;
import com.yunbao.phonelive.event.NeedRefreshLikeEvent;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.GlobalLayoutChangedListener;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/6/9.
 * 首页推荐
 */

public class HomeRecommendFragment extends AbsFragment {

    private VideoShareFragment mShareFragment;
    private VideoPlayFragment mVideoPlayFragment;
    private VideoCommentFragment mCommentFragment;
    private long mLastClickTime;
    private View mLoadingGroup;
    private boolean mPaused;
    private VideoBean mNeedDeleteVideoBean;
    private Handler mHandler;
    private View mNoData;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    protected void main() {
        mLoadingGroup = mRootView.findViewById(R.id.loading_group);
        mNoData = mRootView.findViewById(R.id.no_data);
        mVideoPlayFragment = new VideoPlayFragment();
        mVideoPlayFragment.setDataHelper(new VideoPlayFragment.DataHelper() {
            @Override
            public void initData(HttpCallback callback) {
                if(AppConfig.isFirstLauch){
                    HttpUtil.getRecommendVideos(1,1, callback);
                    AppConfig.isFirstLauch=false;
                }else{
                    HttpUtil.getRecommendVideos(1,0, callback);
                }

            }

            @Override
            public void loadMoreData(int p, HttpCallback callback) {
                HttpUtil.getRecommendVideos(p,0, callback);
            }

            @Override
            public int getInitPosition() {
                return 0;
            }

            @Override
            public List<VideoBean> getInitVideoList() {
                return null;
            }

            @Override
            public int getInitPage() {
                return 0;
            }

        });
        mVideoPlayFragment.setActionListener(mActionListener);
        mVideoPlayFragment.setOnInitDataCallback(mOnInitDataCallback);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction tx = fragmentManager.beginTransaction();
            tx.replace(R.id.replaced, mVideoPlayFragment);
            tx.commit();
        }
        EventBus.getDefault().register(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mNeedDeleteVideoBean != null && mVideoPlayFragment != null) {
                    mVideoPlayFragment.removeItem(mNeedDeleteVideoBean);
                    mNeedDeleteVideoBean = null;
                }
            }
        };
    }

    public void hiddenChanged(boolean hidden) {
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.hiddenChanged(hidden);
        }
    }

    private VideoPlayFragment.OnInitDataCallback mOnInitDataCallback = new VideoPlayFragment.OnInitDataCallback() {
        @Override
        public void onInitSuccess(int dataSize) {
            if (mLoadingGroup != null && mLoadingGroup.getVisibility() == View.VISIBLE) {
                mLoadingGroup.setVisibility(View.INVISIBLE);
            }
            if (dataSize == 0) {
                if (mNoData != null) {
                    mNoData.setVisibility(View.VISIBLE);
                }
                if (mVideoPlayFragment != null) {
                    mVideoPlayFragment.setLoading(false);
                }
            }
        }
    };

    private VideoPlayWrap.ActionListener mActionListener = new VideoPlayWrap.ActionListener() {
        @Override
        public void onZanClick(final VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            if (AppConfig.getInstance().isLogin()) {
                if (AppConfig.getInstance().getUid().equals(bean.getUid())) {
                    ToastUtil.show(WordUtil.getString(R.string.cannot_zan_self));
                    return;
                }
                String videoId = bean.getId();
                if (!TextUtils.isEmpty(videoId)) {
                    HttpUtil.setVideoLike(videoId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                int islike = obj.getIntValue("islike");
                                wrap.setLikes(islike, obj.getString("likes"));
                                EventBus.getDefault().post(new NeedRefreshLikeEvent());
                            }
                        }
                    });
                }
            } else {
                LoginActivity.forwardLogin(mContext);
            }
        }

        @Override
        public void onCommentClick(VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            ((GlobalLayoutChangedListener) mContext).addLayoutListener();
            mCommentFragment = new VideoCommentFragment();
            mCommentFragment.setVideoPlayWrap(wrap);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.VIDEO_ID, bean.getId());
            bundle.putString(Constants.UID, bean.getUid());
            bundle.putBoolean(Constants.FULL_SCREEN, false);
            mCommentFragment.setArguments(bundle);
            if (!mCommentFragment.isAdded()) {
                mCommentFragment.show(((MainActivity) mContext).getSupportFragmentManager(), "VideoCommentFragment");
            }
        }

        @Override
        public void onFollowClick(final VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            if (AppConfig.getInstance().isLogin()) {
                if (AppConfig.getInstance().getUid().equals(bean.getUid())) {
                    ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
                    return;
                }
                final String touid = bean.getUid();
                if (!TextUtils.isEmpty(touid)) {
                    HttpUtil.setAttention(touid, null);
                }
            } else {
                LoginActivity.forwardLogin(mContext);
            }
        }

        @Override
        public void onAvatarClick(VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            UserCenterActivity.forwardOtherUserCenter(mContext, bean.getUid());
            //((MainActivity) mContext).showUserInfo();
        }

        @Override
        public void onShareClick(final VideoPlayWrap wrap, final VideoBean bean) {
            if (!HomeRecommendFragment.this.canClick())
                return;
            HomeRecommendFragment.this.mShareFragment = new VideoShareFragment();
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("videoBean", bean);
            HomeRecommendFragment.this.mShareFragment.setArguments(localBundle);
            HomeRecommendFragment.this.mShareFragment.setActionListener(new VideoShareFragment.ActionListener()
            {
                public void onShareSuccess()
                {
                    HttpUtil.setVideoShare(bean.getId(), new HttpCallback()
                    {
                        public void onSuccess(int paramAnonymous3Int, String paramAnonymous3String, String[] paramAnonymous3ArrayOfString)
                        {
                            if ((paramAnonymous3Int == 0) && (paramAnonymous3ArrayOfString.length > 0))
                            {
                                JSONObject jsonObject = JSON.parseObject(paramAnonymous3ArrayOfString[0]);
                                bean.setShares(jsonObject.getString("shares"));
                            }
                        }
                    });
                }
            });
            if (!HomeRecommendFragment.this.mShareFragment.isAdded())
                HomeRecommendFragment.this.mShareFragment.show(((MainActivity)HomeRecommendFragment.this.mContext).getSupportFragmentManager(), "VideoShareFragment");
        }
    };

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_RECOMMEND_VIDEOS);
        HttpUtil.cancel(HttpUtil.SET_VIDEO_SHARE);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.setDataHelper(null);
            mVideoPlayFragment.setActionListener(null);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.refreshVideoAttention(e.getTouid(), e.getIsAttention());
        }
    }

    private boolean canClick() {
        long timeStamp = System.currentTimeMillis();
        if (timeStamp - mLastClickTime < 1000) {
            return false;
        } else {
            mLastClickTime = timeStamp;
            return true;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        if (mPaused) {
            mNeedDeleteVideoBean = e.getVideoBean();
        } else {
            if (mVideoPlayFragment != null) {
                mVideoPlayFragment.removeItem(e.getVideoBean());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPaused) {
            mPaused = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 300);
            }
        }
    }
}
