package com.yunbao.phonelive.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.yunbao.phonelive.bean.ChatMessageBean;

/**
 * Created by cxf on 2018/7/19.
 */

public class VoiceMediaPlayerUtil {

    private MediaPlayer mPlayer;
    private boolean mStarted;
    private boolean mPaused;
    private boolean mDestroy;
    private String mCurPath;
    private MediaPlayCallback mMediaPlayCallback;
    private AudioManager mAudioManager;
    private int mOriginVolume;//原始音量
    private int mMaxVolume;
    private ChatMessageBean mCurChatMessageBean;

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mDestroy) {
                destroy();
            } else {
                if (mAudioManager != null) {
                    mOriginVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                mPlayer.start();
                mStarted = true;
                if (mMediaPlayCallback != null) {
                    mMediaPlayCallback.onPlayStart(mCurChatMessageBean);
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            mStarted = false;
            mCurPath = null;
            if (mMediaPlayCallback != null) {
                mMediaPlayCallback.onPlayEnd(mCurChatMessageBean);
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            mStarted = false;
            mCurPath = null;
            if (mMediaPlayCallback != null) {
                mMediaPlayCallback.onPlayError(mCurChatMessageBean);
            }
            return false;
        }
    };

    public void setMediaPlayCallback(MediaPlayCallback callback) {
        mMediaPlayCallback = callback;
    }

    public VoiceMediaPlayerUtil(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMaxVolume=13;
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void startPlay(ChatMessageBean bean, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!mStarted) {
            mCurChatMessageBean = bean;
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
                if (mCurChatMessageBean != null && mMediaPlayCallback != null) {
                    mMediaPlayCallback.onPlayEnd(mCurChatMessageBean);
                }
                mCurChatMessageBean = bean;
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
        if (mStarted) {
            mPlayer.stop();
            mStarted = false;
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
        mAudioManager = null;
        mMediaPlayCallback = null;
    }

    public interface MediaPlayCallback {
        void onPlayStart(ChatMessageBean bean);

        void onPlayEnd(ChatMessageBean bean);

        void onPlayError(ChatMessageBean bean);
    }
}
