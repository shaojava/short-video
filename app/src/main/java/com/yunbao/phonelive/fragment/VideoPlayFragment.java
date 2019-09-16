package com.yunbao.phonelive.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.adapter.VideoPlayAdapter;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.LoadingBar;
import com.yunbao.phonelive.custom.VerticalViewPager;
import com.yunbao.phonelive.custom.VideoPlayView;
import com.yunbao.phonelive.custom.VideoPlayWrap;
import com.yunbao.phonelive.event.NeedRefreshEvent;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.VideoChangeListener;
import com.yunbao.phonelive.utils.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;


/**
 * Created by cxf on 2018/6/5.
 * 短视频播放的fragment 可以上下滑动
 */

public class VideoPlayFragment extends AbsFragment implements ViewPager.OnPageChangeListener {

    private VerticalViewPager mViewPager;
    private VideoPlayView mPlayView;
    private LoadingBar mLoading;
    private VideoPlayAdapter mAdapter;
    private int mOuterViewPagerPosition;//外层ViewPager的position
    private boolean mHidden;//是否hidden
    private int mPage = 1;//分页加载的页数
    private DataHelper mDataHelper;
    private VideoPlayWrap.ActionListener mActionListener;
    private boolean mPaused;
    private boolean mNeedRefresh;
    //private VideoBean mNeedDeleteVideoBean;
    private OnInitDataCallback mOnInitDataCallback;
    //    private static final int DIRECTION_UP = 1;//向上
//    private static final int DIRECTION_DOWN = 2;//向下
//    private int mSrcollDircetion;//滑动方向
//    private float mLastPositionOffset = -1;
    private int mLastPosition;
    private boolean mStartWatch;
    private boolean mEndWatch;
    private View mInputTip;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_play;
    }

    @Override
    protected void main() {
        if (mContext instanceof VideoPlayActivity) {
            View btnBack = mRootView.findViewById(R.id.btn_back);
            View commentGroup = mRootView.findViewById(R.id.comment_group);
            btnBack.setVisibility(View.VISIBLE);
            commentGroup.setVisibility(View.VISIBLE);
            mInputTip = commentGroup.findViewById(R.id.input_tip);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_back:
                            ((VideoPlayActivity) mContext).onBackPressed();
                            break;
                        case R.id.comment_group:
                            ((VideoPlayActivity) mContext).openCommentWindow();
                            break;
                    }
                }
            };
            btnBack.setOnClickListener(listener);
            commentGroup.setOnClickListener(listener);
        }
        mLoading = (LoadingBar) mRootView.findViewById(R.id.loading);
        mLoading.setLoading(true);
        mViewPager = (VerticalViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setOnPageChangeListener(this);
        if (mDataHelper != null) {
            List<VideoBean> list = mDataHelper.getInitVideoList();
            if (list != null && list.size() > 0) {
                initAdapter(list);
                int initPosition = mDataHelper.getInitPosition();
                if (initPosition >= 0 && initPosition < list.size()) {
                    mLastPosition = initPosition;
                    mViewPager.setCurrentItem(initPosition);
                }
            } else {
                mDataHelper.initData(mInitCallback);
            }
            int initPage = mDataHelper.getInitPage();
            if (initPage > 0) {
                mPage = initPage;
            }
        }
        EventBus.getDefault().register(this);
    }

    private void initAdapter(List<VideoBean> list) {
        mAdapter = new VideoPlayAdapter(mContext, list);
        mAdapter.setOnPlayVideoListener(new VideoPlayAdapter.OnPlayVideoListener() {
            @Override
            public void onPlayVideo(VideoBean bean) {
                if (mContext instanceof VideoChangeListener) {
                    ((VideoChangeListener) mContext).changeVideo(bean);
                }
                mStartWatch = false;
                mEndWatch = false;
            }
        });
        mAdapter.setActionListener(mActionListener);
        mPlayView = mAdapter.getVideoPlayView();
        mPlayView.setPlayEventListener(mPlayEventListener);
        mViewPager.setAdapter(mAdapter);
        mAdapter.setViewPager(mViewPager);
    }

    public void setDataHelper(DataHelper helper) {
        mDataHelper = helper;
    }

    private HttpCallback mInitCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                if (list.size() > 0) {
                    if (mAdapter == null) {
                        initAdapter(list);
                    }
                }
                if (mOnInitDataCallback != null) {
                    mOnInitDataCallback.onInitSuccess(list.size());
                }
            }
        }
    };

    private HttpCallback mLoadMoreCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                if (list.size() > 0) {
                    if (mAdapter != null) {
                        mAdapter.insertList(list);
                    }
                } else {
                    mPage--;
                }
            } else {
                mPage--;
            }
        }
    };

    public void showInputTip() {
        if (mInputTip != null && mInputTip.getVisibility() != View.VISIBLE) {
            mInputTip.setVisibility(View.VISIBLE);
        }
    }

    public void hideInputTip() {
        if (mInputTip != null && mInputTip.getVisibility() == View.VISIBLE) {
            mInputTip.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        if (positionOffset == 0) {
//            mLastPositionOffset = -1;
//        } else {
//            if (mLastPositionOffset == -1) {
//                mLastPositionOffset = positionOffset;
//            } else {
//                if (positionOffset > mLastPositionOffset) {
//                    L.e("positionOffset----->向上");
//                    mSrcollDircetion = DIRECTION_UP;
//                } else if (positionOffset < mLastPositionOffset) {
//                    L.e("positionOffset----->向下");
//                    mSrcollDircetion = DIRECTION_DOWN;
//                }
//            }
//        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mLastPosition != position) {
            if (mLastPosition < position) {
                if (mAdapter != null) {
                    int count = mAdapter.getCount();
                    if (count > 2 && position == count - 2) {
                        L.e("VideoPlayFragment-------->分页加载数据");
                        mPage++;
                        if (mDataHelper != null) {
                            mDataHelper.loadMoreData(mPage, mLoadMoreCallback);
                        }
                    }
                }
            }
            mLastPosition = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private VideoPlayView.PlayEventListener mPlayEventListener = new VideoPlayView.PlayEventListener() {
        @Override
        public void onReadyPlay() {
            if (mLoading != null) {
                mLoading.setLoading(true);
            }
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {

        }

        @Override
        public void onError() {

        }

        @Override
        public void onLoading() {
            if (mLoading != null) {
                mLoading.setLoading(true);
            }
        }

        @Override
        public void onPlay() {
            if (mLoading != null) {
                mLoading.setLoading(false);
            }
        }

        @Override
        public void onFirstFrame() {
            if (mOuterViewPagerPosition != 0 || mHidden) {
                if (mPlayView != null) {
                    mPlayView.pausePlay();
                }
            }
            if (!mStartWatch) {
                mStartWatch = true;
                VideoBean videoBean = mAdapter.getCurWrap().getVideoBean();
                if (videoBean != null) {
                    HttpUtil.startWatchVideo(videoBean.getId());
                }
            }
        }

        @Override
        public void onPlayEnd() {
            if (!mEndWatch) {
                mEndWatch = true;
                VideoBean videoBean = mAdapter.getCurWrap().getVideoBean();
                if (videoBean != null) {
                    HttpUtil.endWatchVideo(videoBean.getId());
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedRefreshEvent(NeedRefreshEvent e) {
        mNeedRefresh = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        VideoBean bean = e.getVideoBean();
        if (bean != null) {
            if (mPaused) {
                mNeedDeleteVideoBean = bean;
            } else {
                if (mAdapter != null) {
                    mAdapter.removeItem(bean);
                }
            }

//            if (!mPaused) {
//                if (mContext instanceof VideoPlayActivity) {
//                    ((VideoPlayActivity) mContext).onBackPressed();
//                }
//                else {
//                    if (mAdapter != null) {
//                        mAdapter.removeItem(bean);
//                    }
//                }
//            }
        }
    }


    public void removeItem(VideoBean bean) {
        if (mAdapter != null) {
            mAdapter.removeItem(bean);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayView != null) {
            mPlayView.onPause();
        }
    }

    private VideoBean mNeedDeleteVideoBean;

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayView != null) {
            mPlayView.onResume();
        }
        if (mPaused) {
            mPaused = false;
            if (mNeedDeleteVideoBean == null) {
                if (mNeedRefresh && mAdapter != null) {
                    mAdapter.refreshCurrentVideoInfo();
                    mNeedRefresh = false;
                }
            }
            else {
                if (mAdapter != null) {
                    mAdapter.removeItem(mNeedDeleteVideoBean);
                }
                mNeedDeleteVideoBean = null;
            }
//            if (mNeedRefresh && mAdapter != null) {
//                mAdapter.refreshCurrentVideoInfo();
//                mNeedRefresh = false;
//            }
        }
    }

    /**
     * back键返回的时候销毁playView
     */
    public void backDestroyPlayView() {
        if (mPlayView != null) {
            mPlayView.destroy();
            mPlayView = null;
        }
    }


    public void setLoading(boolean loading) {
        if (mLoading != null) {
            mLoading.setLoading(loading);
        }
    }

    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpUtil.START_WATCH_VIDEO);
        HttpUtil.cancel(HttpUtil.END_WATCH_VIDEO);
        if (mLoading != null) {
            mLoading.endLoading();
        }
        if (mPlayView != null) {
            mPlayView.destroy();
            mPlayView = null;
        }
        if (mAdapter != null) {
            mAdapter.release();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void hiddenChanged(boolean hidden) {
        if (mHidden == hidden) {
            return;
        }
        mHidden = hidden;
        if (mOuterViewPagerPosition == 0 && mPlayView != null) {
            if (hidden) {
                mPlayView.pausePlay();
            } else {
                mPlayView.resumePlay();
            }
        }
    }

    /**
     * 外层ViewPager滑动事件
     */
    public void onOuterPageSelected(int position) {
        mOuterViewPagerPosition = position;
        if (mPlayView != null) {
            if (position == 0) {
                mPlayView.resumePlay();
            } else {
                mPlayView.pausePlay();
            }
        }
    }

    public void refreshVideoAttention(String uid, int isAttetion) {
        if (mAdapter != null) {
            mAdapter.refreshVideoAttention(uid, isAttetion);
        }
    }

    public VideoBean getCurVideoBean() {
        if (mAdapter != null) {
            return mAdapter.getCurVideoBean();
        }
        return null;
    }

    public VideoPlayWrap getCurWrap() {
        if (mAdapter != null) {
            return mAdapter.getCurWrap();
        }
        return null;
    }

    public interface DataHelper {
        //初始化数据
        void initData(HttpCallback callback);

        //加载更多
        void loadMoreData(int p, HttpCallback callback);

        //初始化的position
        int getInitPosition();

        //初始化的视频列表
        List<VideoBean> getInitVideoList();

        //获取初始的页数
        int getInitPage();
    }

    public void setActionListener(VideoPlayWrap.ActionListener listener) {
        mActionListener = listener;
    }

    public interface OnInitDataCallback {
        void onInitSuccess(int dataSize);
    }

    public void setOnInitDataCallback(OnInitDataCallback onInitDataCallback) {
        mOnInitDataCallback = onInitDataCallback;
    }

}
