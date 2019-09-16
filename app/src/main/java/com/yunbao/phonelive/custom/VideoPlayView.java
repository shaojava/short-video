package com.yunbao.phonelive.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by cxf on 2018/6/5.
 */

public class VideoPlayView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "VideoPlayView";
    private TXCloudVideoView mVideoView;
    private TXVodPlayer mPlayer;
    private View mPlayBtn;
    private String mUrl;
    private PlayEventListener mPlayEventListener;
    private Context mContext;
    private boolean mStarted;
    private boolean mPaused;//是否切后台了
    private boolean mDestoryed;
    private boolean mClickPausePlay;//是否手动暂停了播放
    private boolean mPausePlay;//是否被动暂停了播放
    private boolean mFirstFrame;
    private int mScreenWidth;
    private ObjectAnimator mAnimator;

    private VideoPlayWrap mPlayWrap;

    private boolean canStopPlay=true;//是否能手动停止播放
    private View root;

    public VideoPlayView(Context context) {
        this(context, null);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_play, this, false);
        addView(view);
        mPlayBtn = view.findViewById(R.id.btn_play);
        root=view.findViewById(R.id.root);
        root.setOnClickListener(this);
        mVideoView = (TXCloudVideoView) view.findViewById(R.id.player);
        mVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig config = new TXVodPlayConfig();
        config.setCacheFolderPath(mContext.getCacheDir().getAbsolutePath());
        config.setMaxCacheItems(10);
        mPlayer.setConfig(config);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(mPlayListener);
        mAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayBtn,
                PropertyValuesHolder.ofFloat("scaleX", 3f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 3f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mAnimator.setDuration(120);
        mAnimator.setInterpolator(new AccelerateInterpolator());
    }


    private ITXLivePlayListener mPlayListener = new ITXLivePlayListener() {
        @Override
        public void onPlayEvent(int e, Bundle bundle) {
            switch (e) {
                case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                    if (!mDestoryed) {
                        L.e(TAG, "VideoPlayView------>播放开始");
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onPlay();
                        }
                        if (mPlayWrap != null) {
                            mPlayWrap.startMusicAnim();
                        }
                    } else {
                        doDestroy();
                    }
                    break;
                case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                    ToastUtil.show(mContext.getString(R.string.mp4_error));
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onError();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onLoading();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                    if (!mDestoryed) {
                        mFirstFrame = true;
                        L.e(TAG, "VideoPlayView------>第一帧");
                        if (mPaused) {
                            mPlayer.pause();
                        }
                        if (mPlayWrap != null) {
                            mPlayWrap.hideBg();
                        }
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onFirstFrame();
                        }
                    } else {
                        doDestroy();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_PLAY_END:
                    onReplay();
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onPlayEnd();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                    int width = bundle.getInt("EVT_PARAM1", 0);
                    int height = bundle.getInt("EVT_PARAM2", 0);
                    if (!mDestoryed && width > 0 && height > 0) {
                        videoSizeChanged(width, height);
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onVideoSizeChanged(width, height);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onNetStatus(Bundle bundle) {

        }
    };


    /**
     * 循环播放
     */
    private void onReplay() {
        if (mStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    public boolean isCanStopPlay() {
        return canStopPlay;
    }

    public void setCanStopPlay(boolean canStopPlay) {
        this.canStopPlay = canStopPlay;
        if(canStopPlay){
            root.setClickable(true);
        }else{
            root.setClickable(false);
        }
    }

    /**
     * 加载视频成功后调用
     */
    private void videoSizeChanged(int width, int height) {
        L.e(TAG, "videoSizeChanged---width--->" + width);
        L.e(TAG, "videoSizeChanged---height-->" + height);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (width >= height) {//横屏视频
            float rate = ((float) width) / height;
            params.height = (int) (mScreenWidth / rate);
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mVideoView.setLayoutParams(params);
        mVideoView.requestLayout();
    }


    public void play(String url) {
        mFirstFrame = false;
        mPausePlay = false;
        mClickPausePlay = false;
        if (mPlayBtn != null && mPlayBtn.getVisibility() == VISIBLE) {
            mPlayBtn.setVisibility(INVISIBLE);
        }
        if (!mDestoryed && mPlayer != null) {
            if (TextUtils.isEmpty(url) || url.equals(mUrl)) {
                return;
            }
            mUrl = url;
            if (mStarted) {
                mPlayer.stopPlay(false);
            }
            mPlayer.startPlay(mUrl);
            mStarted = true;
            L.e(TAG, "play------->" + mUrl);
        }
        if (mPlayEventListener != null) {
            mPlayEventListener.onReadyPlay();
        }
    }

    public void destroy() {
        if (!mDestoryed) {
            doDestroy();
        }
    }

    private void doDestroy() {
        mDestoryed = true;
        if (mPlayer != null) {
            mPlayer.stopPlay(true);
        }
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        L.e(TAG, "destroy------->");
    }

    public void setPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
        L.e(TAG, "setPlayWrap------->" + wrap.hashCode());
    }

    /**
     * 取消切后台，返回前台
     */
    public void onResume() {
        if (!mPausePlay && !mClickPausePlay && mPaused && !mDestoryed && mPlayer != null) {
            mPaused = false;
            mPlayer.resume();
            if (mPlayWrap != null) {
                mPlayWrap.startMusicAnim();
            }
        }
    }


    /**
     * 切后台
     */
    public void onPause() {
        if (!mPausePlay && !mClickPausePlay && !mPaused && !mDestoryed && mPlayer != null) {
            mPaused = true;
            mPlayer.pause();
//            if (mPlayWrap != null) {
//                mPlayWrap.pauseMusicAnim();
//            }
        }
    }


    /**
     * 手动暂停播放
     */
    private void clickPausePlay() {
        if (mFirstFrame && !mPausePlay && !mClickPausePlay) {
            mClickPausePlay = true;
            if (mPlayBtn != null && mPlayBtn.getVisibility() != VISIBLE) {
                mPlayBtn.setVisibility(VISIBLE);
                mAnimator.start();
            }
            mPlayer.pause();

        }
    }


    /**
     * 手动恢复播放
     */
    private void clickResumePlay() {
        if (mFirstFrame && !mPausePlay && mClickPausePlay) {
            mClickPausePlay = false;
            if (mPlayBtn != null && mPlayBtn.getVisibility() == VISIBLE) {
                mPlayBtn.setVisibility(INVISIBLE);
            }
            mPlayer.resume();
        }
    }

    /**
     * 被动暂停播放
     */
    public void pausePlay() {
        if (mFirstFrame && !mClickPausePlay && !mPausePlay) {
            mPausePlay = true;
            mPlayer.pause();
//            if (mPlayWrap != null) {
//                mPlayWrap.pauseMusicAnim();
//            }
        }
    }


    /**
     * 被动恢复播放
     */
    public void resumePlay() {
        if (mFirstFrame && !mClickPausePlay && mPausePlay) {
            mPausePlay = false;
            mPlayer.resume();
            if (mPlayWrap != null) {
                mPlayWrap.startMusicAnim();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                if (mClickPausePlay) {
                    clickResumePlay();
                } else {
                    if(canStopPlay){
                        clickPausePlay();
                    }

                }
                break;
        }
    }



    public void setPlayEventListener(PlayEventListener playEventListener) {
        mPlayEventListener = playEventListener;
    }

    public interface PlayEventListener {

        void onReadyPlay();

        void onVideoSizeChanged(int width, int height);

        void onError();

        void onLoading();

        void onPlay();

        void onFirstFrame();

        void onPlayEnd();
    }

}
