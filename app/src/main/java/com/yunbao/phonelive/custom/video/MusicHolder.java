package com.yunbao.phonelive.custom.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicBean;
import com.yunbao.phonelive.custom.RangeSlider;
import com.yunbao.phonelive.custom.TextSeekBar;
import com.yunbao.phonelive.utils.WordUtil;

import java.io.IOException;

/**
 * Created by cxf on 2018/6/27.
 */

public class MusicHolder implements View.OnClickListener {
    private ViewGroup mParent;
    private View mHideView;
    private View mContentView;
    private MusicChangeListener mMusicChangeListener;
    private TextView mMusicName;
    private TextView mCutTip;
    private TextSeekBar mOriginalSeekBar;
    private TextSeekBar mBgmSeekBar;
    private RangeSlider mRangeSlider;
    private View mCutGroup;
    private String mCutMusicTip;
    private long mMusicDuration;
    private MediaPlayer mMediaPlayer;


    public MusicHolder(Context context, ViewGroup parent, View hideView, MusicBean bean) {
        mParent = parent;
        mHideView = hideView;
        View v = LayoutInflater.from(context).inflate(R.layout.view_edit_music, parent, false);
        mContentView = v;
        v.findViewById(R.id.btn_hide).setOnClickListener(this);
        mCutGroup = v.findViewById(R.id.group_cut);
        mMusicName = (TextView) v.findViewById(R.id.music_name);
        mCutTip = (TextView) v.findViewById(R.id.cut_tip);
        mOriginalSeekBar = (TextSeekBar) v.findViewById(R.id.seek_original);
        mOriginalSeekBar.setOnSeekChangeListener(mSeekChangeListener);
        mBgmSeekBar = (TextSeekBar) v.findViewById(R.id.seek_bgm);
        mBgmSeekBar.setOnSeekChangeListener(mSeekChangeListener);
        mRangeSlider = (RangeSlider) v.findViewById(R.id.bgm_range_slider);
        mRangeSlider.setRangeChangeListener(new RangeSlider.OnRangeChangeListener() {
            @Override
            public void onKeyDown(int type) {

            }

            @Override
            public void onKeyUp(int type, int leftPinIndex, int rightPinIndex) {
                long startTime = mMusicDuration * leftPinIndex / 100;
                long endTime = mMusicDuration * rightPinIndex / 100;
                showCutTime(startTime, endTime);
                if (mMusicChangeListener != null) {
                    mMusicChangeListener.onBgmCutTimeChanged(startTime, endTime);
                }
            }
        });
        v.findViewById(R.id.btn_cancel_music).setOnClickListener(this);
        mCutMusicTip = WordUtil.getString(R.string.cut_music_tip);
        mMediaPlayer = new MediaPlayer();
        if (bean != null) {
            mOriginalSeekBar.setEnabled(false);
            mOriginalSeekBar.setProgress(0);
            mMusicName.setText(bean.getTitle() + "_" + bean.getAuthor());
            try {
                mMediaPlayer.setDataSource(bean.getLocalPath());
                mMediaPlayer.prepare();
                mMusicDuration = mMediaPlayer.getDuration();
                showCutTime(0, mMusicDuration);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mBgmSeekBar.setEnabled(false);
            mBgmSeekBar.setProgress(0);
            mCutGroup.setVisibility(View.GONE);
        }
    }

    private TextSeekBar.OnSeekChangeListener mSeekChangeListener = new TextSeekBar.OnSeekChangeListener() {
        @Override
        public void onProgressChanged(View v, int progress) {
            if (mMusicChangeListener != null) {
                switch (v.getId()) {
                    case R.id.seek_original:
                        mMusicChangeListener.onOriginalChanged(progress / 100f);
                        break;
                    case R.id.seek_bgm:
                        mMusicChangeListener.onBgmChanged(progress / 100f);
                        break;
                }
            }
        }
    };

    public void show() {
        if (mParent != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            if (mHideView != null && mHideView.getVisibility() == View.VISIBLE) {
                mHideView.setVisibility(View.INVISIBLE);
            }
            mParent.addView(mContentView);
        }
    }

    private void hide() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
        if (mHideView != null && mHideView.getVisibility() != View.VISIBLE) {
            mHideView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hide:
                hide();
                break;
            case R.id.btn_cancel_music:
                cancelBgmMusic();
                break;
        }
    }

    private void cancelBgmMusic() {
        if (mBgmSeekBar != null) {
            mBgmSeekBar.setEnabled(false);
            mBgmSeekBar.setProgress(0);
        }
        hide();
        if (mCutGroup.getVisibility() == View.VISIBLE) {
            mCutGroup.setVisibility(View.GONE);
        }
        if (mMusicChangeListener != null) {
            mMusicChangeListener.onBgmCancelClick();
        }
    }

    public void setBgmMusic(MusicBean bean) {
        mMusicName.setText(bean.getTitle() + "_" + bean.getAuthor());
        mBgmSeekBar.setEnabled(true);
        mBgmSeekBar.setProgress(80);
        if (mCutGroup.getVisibility() != View.VISIBLE) {
            mCutGroup.setVisibility(View.VISIBLE);
        }
        if (mCutGroup.getVisibility() != View.VISIBLE) {
            mCutGroup.setVisibility(View.VISIBLE);
        }
        mRangeSlider.resetRangePos();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(bean.getLocalPath());
            mMediaPlayer.prepare();
            mMusicDuration = mMediaPlayer.getDuration();
            showCutTime(0, mMusicDuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCutTime(long startTime, long endTime) {
        String cutTime = String.format("%.2f", (endTime - startTime) / 1000f) + "s";
        mCutTip.setText(mCutMusicTip + cutTime);
    }


    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }


    public interface MusicChangeListener {
        void onOriginalChanged(float value);

        void onBgmChanged(float value);

        void onBgmCancelClick();

        void onBgmCutTimeChanged(long startTime, long endTime);
    }

    public void setMusicChangeListener(MusicChangeListener musicChangeListener) {
        mMusicChangeListener = musicChangeListener;
    }

}
