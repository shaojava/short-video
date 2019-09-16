package com.yunbao.phonelive.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * Created by cxf on 2018/7/19.
 */

public class MusicMediaPlayerUtil {

    private MediaPlayer mPlayer;
    private boolean mStarted;
    private boolean mPaused;
    private boolean mDestroy;
    private String mCurPath;
    private MediaPlayCallback mMediaPlayCallback;

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mDestroy) {
                destroy();
            } else {
                mPlayer.start();
                mStarted = true;
                if (mMediaPlayCallback != null) {
                    mMediaPlayCallback.onPlayStart();
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mStarted = false;
            mCurPath = null;
            if (mMediaPlayCallback != null) {
                mMediaPlayCallback.onPlayEnd();
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mStarted = false;
            mCurPath = null;
            if (mMediaPlayCallback != null) {
                mMediaPlayCallback.onPlayError();
            }
            return false;
        }
    };

    public void setMediaPlayCallback(MediaPlayCallback callback) {
        mMediaPlayCallback = callback;
    }

    public MusicMediaPlayerUtil() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void startPlay(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!mStarted) {
            mCurPath = path;
            try {
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.setLooping(false);
                mPlayer.setVolume(1f, 1f);
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!path.equals(mCurPath)) {
                mCurPath = path;
                mStarted = false;
                try {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.setDataSource(path);
                    mPlayer.setLooping(false);
                    mPlayer.setVolume(1f, 1f);
                    mPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pausePlay() {
        if (mStarted && !mDestroy) {
            mPlayer.pause();
            mPaused = true;
        }
    }

    public void resumePlay() {
        if (mStarted && !mDestroy && mPaused) {
            mPaused = false;
            mPlayer.start();
        }
    }

    public void stopPlay(){
        if (mStarted && !mDestroy) {
            mPlayer.stop();
            mStarted=false;
            mCurPath = null;
        }
    }

    public void destroy() {
        if (mStarted) {
            mPlayer.stop();
            mPlayer.release();
            mStarted = false;
            mCurPath = null;
        }
        mDestroy = true;
        mMediaPlayCallback = null;
    }

    public interface MediaPlayCallback {
        void onPlayStart();

        void onPlayEnd();

        void onPlayError();
    }
}
