package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.VerticalViewPager;
import com.yunbao.phonelive.custom.VideoPlayView;
import com.yunbao.phonelive.custom.VideoPlayWrap;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by cxf on 2017/12/20.
 */

public class VideoPlayAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private LinkedList<VideoPlayWrap> mViewList;
    private LinkedList<VideoPlayWrap> mAllList;
    private VideoPlayView mPlayView;
    private List<VideoBean> mVideoList;
    private VideoPlayWrap mCurWrap;
    private OnPlayVideoListener mOnPlayVideoListener;
    private VideoPlayWrap.ActionListener mActionListener;
    private VerticalViewPager mViewPager;

    public VideoPlayAdapter(Context context, List<VideoBean> videoList) {
        mContext = context;
        mVideoList = videoList;
        mViewList = new LinkedList<>();
        mAllList = new LinkedList<>();
        mInflater = LayoutInflater.from(context);
        mPlayView = (VideoPlayView) mInflater.inflate(R.layout.view_video_layout_play, null, false);
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        VideoPlayWrap wrap = null;
        if (mViewList.size() > 0) {
            wrap = mViewList.getFirst();
            mViewList.removeFirst();
        } else {
            wrap = (VideoPlayWrap) mInflater.inflate(R.layout.view_video_layout_wrap, container, false);
            wrap.setActionListener(mActionListener);
            mAllList.add(wrap);
        }
        wrap.loadData(mVideoList.get(position));
        container.addView(wrap);
        return wrap;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object != null) {
            VideoPlayWrap videoWrap = (VideoPlayWrap) object;
            videoWrap.clearData();
            container.removeView(videoWrap);
            mViewList.addLast(videoWrap);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mCurWrap != object) {
            if (mCurWrap != null) {
                mCurWrap.removePlayView();
            }
            mCurWrap = (VideoPlayWrap) object;
            mCurWrap.addPlayView(mPlayView);
            mCurWrap.play();
            if (mOnPlayVideoListener != null) {
                mOnPlayVideoListener.onPlayVideo(mCurWrap.getVideoBean());
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public VideoPlayView getVideoPlayView() {
        return mPlayView;
    }


    public void insertList(List<VideoBean> list) {
        if (mVideoList != null) {
            mVideoList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void removeItem(VideoBean bean) {
        int size = mVideoList.size();
        if (size > 1) {
            int videoPosition = -1;
            for (int i = 0; i < size; i++) {
                if (mVideoList.get(i).getId().equals(bean.getId())) {
                    videoPosition = i;
                    break;
                }
            }
            if (videoPosition >= 0 && videoPosition < mVideoList.size()) {
                mVideoList.remove(videoPosition);
                notifyDataSetChanged();
                mCurWrap = (VideoPlayWrap) mViewPager.getChildAt(mViewPager.getCurrentItem());
                if(mCurWrap!=null){
                    mCurWrap.showBg();
                    mCurWrap.play();
                    if (mOnPlayVideoListener != null) {
                        mOnPlayVideoListener.onPlayVideo(mCurWrap.getVideoBean());
                    }
                }
            }
        } else {
            if (mVideoList.get(0).getId().equals(bean.getId())) {
                if (mContext instanceof VideoPlayActivity) {
                    ((VideoPlayActivity) mContext).onBackPressed();
                }
            }
        }
    }

    /**
     * 刷新当前播放页面的关注点赞等信息
     */
    public void refreshCurrentVideoInfo() {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                if (wrap != null) {
                    wrap.getVideoInfo();
                }
            }
        }
    }

    /**
     * 刷新视频的关注信息
     */
    public void refreshVideoAttention(String uid, int isAttetion) {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                if (wrap != null) {
                    VideoBean bean = wrap.getVideoBean();
                    if (bean != null && uid.equals(bean.getUid())) {
                        wrap.setIsAttent(isAttetion);
                    }
                }
            }
        }
    }

    public void release() {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                wrap.release();
            }
        }
        mActionListener = null;
        mOnPlayVideoListener = null;
    }

    /**
     * 回调出去可以让外面知道播放的是哪个视频
     */
    public interface OnPlayVideoListener {
        void onPlayVideo(VideoBean bean);
    }


    public void setOnPlayVideoListener(OnPlayVideoListener listener) {
        mOnPlayVideoListener = listener;
    }

    public void setActionListener(VideoPlayWrap.ActionListener listener) {
        mActionListener = listener;
    }

    public VideoPlayWrap getCurWrap() {
        return mCurWrap;
    }

    public VideoBean getCurVideoBean() {
        if (mCurWrap != null) {
            return mCurWrap.getVideoBean();
        }
        return null;
    }

    public void setViewPager(VerticalViewPager viewPager) {
        mViewPager = viewPager;
    }


    public void relase(){

    }
}
