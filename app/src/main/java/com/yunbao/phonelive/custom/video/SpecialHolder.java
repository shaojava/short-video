package com.yunbao.phonelive.custom.video;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VideoEditWrap;

import com.tencent.ugc.TXVideoEditConstants;
import com.yunbao.video.custom.ColorfulProgress;
import com.yunbao.video.custom.RangeSliderViewContainer;
import com.yunbao.video.custom.SliderViewContainer;
import com.yunbao.video.custom.VideoProgressController;
import com.yunbao.video.custom.VideoProgressView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by cxf on 2018/6/23.
 * 编辑 特效
 */

public class SpecialHolder implements View.OnClickListener {

    private Context mContext;
    private ViewGroup mParent;
    private View mHideView;
    private View mContentView;
    private SparseArray<View> mSparseArray;
    private int mCurKey;
    private long mVideoDuration;
    private long mCurTime;
    private TextView mCurTimeTextView;//当前帧数的时间
    private boolean mShowing;
    private View mBtnCancelOtherSpecial;//取消其他特效的按钮
    private boolean mTouching;//其他特效的按钮是否被按下了
    private boolean mOtherSpecialStartMark;//其他特效是否开始标记了
    private SliderViewContainer mRepeatSlider;//重复
    private SliderViewContainer mSpeedSlider;//慢动作
    private EffectListener mEffectListener;

    private View v_null;

    private static final int TIME_NONE = 0;
    private static final int TIME_DAO_FANG = 1;
    private static final int TIME_FAN_FU = 2;
    private static final int TIME_MDZ = 3;

    private int mCurTimeEffect = TIME_NONE;//当前的时间特效


    private View v;
    //进度条相关
    private VideoProgressView mVideoProgressView;
    private VideoProgressController mVideoProgressController;
    private ColorfulProgress mColorfulProgress;

    public SpecialHolder(Context context, ViewGroup parent, View hideView, long videoDuraiton) {
        mContext = context;
        mParent = parent;
        mHideView = hideView;
        mVideoDuration = videoDuraiton;
        v = LayoutInflater.from(context).inflate(R.layout.view_edit_special, parent, false);
        mContentView = v;
        v.findViewById(R.id.btn_cut).setOnClickListener(this);
        v.findViewById(R.id.btn_time_special).setOnClickListener(this);
        v.findViewById(R.id.btn_other_special).setOnClickListener(this);
        v.findViewById(R.id.btn_hide).setOnClickListener(this);


        mCurTimeTextView = (TextView) v.findViewById(R.id.curTime);
        mSparseArray = new SparseArray<>();
        mSparseArray.put(R.id.btn_cut, v.findViewById(R.id.group_cut));
        mSparseArray.put(R.id.btn_time_special, v.findViewById(R.id.group_time_special));
        mSparseArray.put(R.id.btn_other_special, v.findViewById(R.id.group_other_special));
        mCurKey = R.id.btn_cut;
        //裁剪相关
        mVideoProgressView = (VideoProgressView) v.findViewById(R.id.progress_view);
        mVideoProgressView.addBitmapList(VideoEditWrap.getInstance().getList());
        mVideoProgressController = new VideoProgressController(context, videoDuraiton);
        mVideoProgressController.setVideoProgressView(mVideoProgressView);
        mVideoProgressController.setVideoProgressSeekListener(new VideoProgressController.VideoProgressSeekListener() {
            @Override
            public void onVideoProgressSeek(long currentTimeMs) {
                showCurTime(currentTimeMs);
                if (mEffectListener != null) {
                    mEffectListener.onSeekChanged(currentTimeMs);
                }
            }

            @Override
            public void onVideoProgressSeekFinish(long currentTimeMs) {
                if (mEffectListener != null) {
                    mEffectListener.onSeekChanged(currentTimeMs);
                }
            }
        });


        RangeSliderViewContainer sliderViewContainer = new RangeSliderViewContainer(context);
        sliderViewContainer.init(mVideoProgressController, 0, videoDuraiton, videoDuraiton);
        sliderViewContainer.setDurationChangeListener(new RangeSliderViewContainer.OnDurationChangeListener() {
            @Override
            public void onDurationChange(long startTimeMs, long endTimeMs) {
                if (mEffectListener != null) {
                    mEffectListener.onCutTimeChanged(startTimeMs, endTimeMs);
                }
            }
        });
        v_null=v.findViewById(R.id.btn_time_none);
        v_null.setOnClickListener(this);

        mVideoProgressController.addRangeSliderView(sliderViewContainer);
        //时间特效相关
        v.findViewById(R.id.btn_time_daofang).setOnClickListener(this);
        v.findViewById(R.id.btn_time_fanfu).setOnClickListener(this);
        v.findViewById(R.id.btn_time_ndz).setOnClickListener(this);
        //其他特效相关
        mColorfulProgress = new ColorfulProgress(context);
        mColorfulProgress.setWidthHeight(mVideoProgressController.getThumbnailPicListDisplayWidth(), DpUtil.dp2px(50));
        mVideoProgressController.addColorfulProgress(mColorfulProgress);
        mColorfulProgress.setVisibility(View.INVISIBLE);
        v.findViewById(R.id.btn_other_1).setOnTouchListener(mOnTouchListener);
        v.findViewById(R.id.btn_other_2).setOnTouchListener(mOnTouchListener);
        v.findViewById(R.id.btn_other_3).setOnTouchListener(mOnTouchListener);
        v.findViewById(R.id.btn_other_4).setOnTouchListener(mOnTouchListener);
        mBtnCancelOtherSpecial = v.findViewById(R.id.btn_cancel_other_special);
        mBtnCancelOtherSpecial.setOnClickListener(this);
    }



    public static final int DAOFANG_CLlick_TYPE=1;
    public void cancleClickAble(int type){
        if(DAOFANG_CLlick_TYPE==type){
          final   RadioButton rb_time_daofang= (RadioButton) v.findViewById(R.id.btn_time_daofang);
                  rb_time_daofang.setEnabled(false);
            v.findViewById(R.id.btn_time_daofang).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.show("该视频不能进行倒放");

                }
            });

        }
    }



    private void showCurTime(long currentTimeMs) {
        mCurTime = currentTimeMs;
        if (mCurTimeTextView != null) {
            mCurTimeTextView.setText(String.format("%.2f", currentTimeMs / 1000f) + "s");
        }
    }

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
            mShowing = true;
        }
    }


    private void hide() {
        if (mEffectListener == null || mEffectListener.onHideClicked()) {
            mShowing = false;
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            if (mHideView != null && mHideView.getVisibility() != View.VISIBLE) {
                mHideView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cut:
            case R.id.btn_time_special:
            case R.id.btn_other_special:
                toggle(id);
                break;
            case R.id.btn_hide:
                hide();
                break;
            case R.id.btn_cancel_other_special:
                cancelLastOtherSpecial();
                break;
            case R.id.btn_time_none://无时间特效
                cancelTimeEffect();
                mEffectListener.replay();
                mEffectListener.disMissLoadingDialog();
                break;
            case R.id.btn_time_daofang://倒放
                timeDaoFang();
                break;
            case R.id.btn_time_fanfu://反复
                timeFanFu();
                break;
            case R.id.btn_time_ndz://慢动作
                timeMDZ();
                break;
        }
    }

    public void performNullClick(){
        v_null.performClick();
    }
    /**
     * 取消所有的时间特效
     */
    public boolean cancelTimeEffect() {

        if (mEffectListener != null && mCurTimeEffect != TIME_NONE) {
            System.gc();
        switch (mCurTimeEffect) {
            case TIME_DAO_FANG:
                mEffectListener.onTimeDaoFangChanged(false);
                break;
            case TIME_FAN_FU:
                if (mRepeatSlider != null && mRepeatSlider.getVisibility() == View.VISIBLE) {
                    mRepeatSlider.setVisibility(View.GONE);
                }
                mEffectListener.onTimeFanFuChanged(false, 0);
                break;
            case TIME_MDZ:
                if (mSpeedSlider != null && mSpeedSlider.getVisibility() == View.VISIBLE) {
                    mSpeedSlider.setVisibility(View.GONE);
                }
                mEffectListener.onTimeMdzChanged(false, 0);
                break;
        }
        mCurTimeEffect = TIME_NONE;
        return true;
    }

        return false;
}

    /**
     * 倒放
     */

    private void timeDaoFang() {
        if (mEffectListener != null && mCurTimeEffect != TIME_DAO_FANG) {
                if(cancelTimeEffect()){
                    mEffectListener.showLoadingDialog();
                    createTimer(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }
                        @Override
                        public void onNext(Long aLong) {
                            observerDaoFang();
                        }
                        @Override
                        public void onError(Throwable e) {
                        }
                        @Override
                        public void onComplete() {
                        }
                    },1);
                }else{
                    mEffectListener.onTimeDaoFangChanged(true);
                    mCurTimeEffect = TIME_DAO_FANG;
                }

        }
    }

    private void observerDaoFang() {
        mEffectListener.onTimeDaoFangChanged(true);
        mCurTimeEffect = TIME_DAO_FANG;
    }

    private void createTimer(Observer<Long>observer,int delayTime) {
        io.reactivex.Observable.timer(delayTime, TimeUnit.SECONDS).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(observer);

    }

    /**
     * 倒放
     */
    public void reverse() {
        if (mVideoProgressView != null) {
            mVideoProgressView.reverse();
        }
    }

    /**
     * 反复
     */
    private void timeFanFu() {
        if (mEffectListener != null && mCurTimeEffect != TIME_FAN_FU) {
            cancelTimeEffect();
            observerFanfu();
        }
    }

    private void observerFanfu() {
        mCurTimeEffect = TIME_FAN_FU;
        long currentTimeMs = mVideoProgressController.getCurrentTimeMs();
        mEffectListener.onTimeFanFuChanged(true, currentTimeMs);

        if (mRepeatSlider == null) {
            L.e("thread=="+Thread.currentThread().getName());
            mRepeatSlider = new SliderViewContainer(mContext);
            mRepeatSlider.setOnStartTimeChangedListener(new SliderViewContainer.OnStartTimeChangedListener() {
                @Override
                public void onStartTimeMsChanged(long timeMs) {
                    mEffectListener.onTimeFanFuChanged(true, timeMs);
                    mVideoProgressController.setCurrentTimeMs(timeMs);
                }
            });
            mVideoProgressController.addSliderView(mRepeatSlider);
        } else {
            if (mRepeatSlider.getVisibility() != View.VISIBLE) {
                mRepeatSlider.setVisibility(View.VISIBLE);
            }
        }
        mRepeatSlider.setStartTimeMs(currentTimeMs);
    }

    /**
     * 慢动作
     */
    private void timeMDZ() {
        if (mEffectListener != null && mCurTimeEffect != TIME_MDZ) {
            cancelTimeEffect();
            observerTimeMDZ();
        }
    }

    private void observerTimeMDZ() {
        mCurTimeEffect = TIME_MDZ;
        long currentTimeMs = mVideoProgressController.getCurrentTimeMs();
        mEffectListener.onTimeMdzChanged(true, currentTimeMs);

        if (mSpeedSlider == null) {
            mSpeedSlider = new SliderViewContainer(mContext);
            mSpeedSlider.setOnStartTimeChangedListener(new SliderViewContainer.OnStartTimeChangedListener() {
                @Override
                public void onStartTimeMsChanged(long timeMs) {
                    mEffectListener.onTimeMdzChanged(true, timeMs);
                    mVideoProgressController.setCurrentTimeMs(timeMs);
                }
            });
            mVideoProgressController.addSliderView(mSpeedSlider);
        } else {
            if (mSpeedSlider.getVisibility() != View.VISIBLE) {
                mSpeedSlider.setVisibility(View.VISIBLE);
            }
        }
        mSpeedSlider.setStartTimeMs(currentTimeMs);
    }


    private void toggle(int key) {
        if (mCurKey == key) {
            return;
        }
        mCurKey = key;
        for (int i = 0, size = mSparseArray.size(); i < size; i++) {
            View v = mSparseArray.valueAt(i);
            if (mSparseArray.keyAt(i) == key) {
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            } else {
                if (v.getVisibility() == View.VISIBLE) {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (mCurKey == R.id.btn_other_special) {
            if (mColorfulProgress.getVisibility() != View.VISIBLE) {
                mColorfulProgress.setVisibility(View.VISIBLE);
            }
        } else {
            if (mColorfulProgress.getVisibility() == View.VISIBLE) {
                mColorfulProgress.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void onVideoPreview(long timeMs) {
        if (mShowing) {
            int currentTimeMs = (int) (timeMs / 1000);//转为ms值
            Log.e("===","timeMS=="+currentTimeMs);
            if (mVideoProgressController != null) {
                mVideoProgressController.setCurrentTimeMs(currentTimeMs);
            }
            showCurTime(currentTimeMs);
        }
    }

    public boolean isShowCut() {
        return !mShowing || mCurKey == R.id.btn_cut;
    }

    /**
     * 其他特效按钮的触摸事件
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            int action = e.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if (mTouching) {//防止多个个其他特效同时按下
                    return false;
                }
                mTouching = true;
                otherSpecialDown(v);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mTouching = false;
                otherSpecialUp(v);
            }
            return true;
        }
    };

    /**
     * 其他特效的按钮被按下
     */
    private void otherSpecialDown(View v) {
        if (mCurTime >= mVideoDuration) {
            mOtherSpecialStartMark = false;
            return;
        }
        mOtherSpecialStartMark = true;
        //v.setBackground(mOtherSpecialDown);
        int color = 0;
        int effect = 0;
        switch (v.getId()) {
            case R.id.btn_other_3:
                color = 0xAAEC5F9B;
                effect = TXVideoEditConstants.TXEffectType_SOUL_OUT;
                break;
            case R.id.btn_other_4:
                color = 0xAAEC8435;
                effect = TXVideoEditConstants.TXEffectType_SPLIT_SCREEN;
                break;
            case R.id.btn_other_1:
                color = 0xAA1FBCB6;
                effect = TXVideoEditConstants.TXEffectType_ROCK_LIGHT;
                break;
            case R.id.btn_other_2:
                color = 0xAA449FF3;
                effect = TXVideoEditConstants.TXEffectType_DARK_DRAEM;
                break;
        }
        if (mColorfulProgress != null) {
            mColorfulProgress.startMark(color);
        }
        if (mEffectListener != null) {
            mEffectListener.onOtherSpecialStart(effect, mCurTime);
        }
    }

    /**
     * 其他特效的按钮被抬起
     */
    private void otherSpecialUp(View v) {
        if (!mOtherSpecialStartMark) {
            return;
        }
        mOtherSpecialStartMark = false;
        // v.setBackground(mOtherSpecialUp);
        int effect = 0;
        switch (v.getId()) {
            case R.id.btn_other_3:
                effect = TXVideoEditConstants.TXEffectType_SOUL_OUT;
                break;
            case R.id.btn_other_4:
                effect = TXVideoEditConstants.TXEffectType_SPLIT_SCREEN;
                break;
            case R.id.btn_other_1:
                effect = TXVideoEditConstants.TXEffectType_ROCK_LIGHT;
                break;
            case R.id.btn_other_2:
                effect = TXVideoEditConstants.TXEffectType_DARK_DRAEM;
                break;
        }
        if (mColorfulProgress != null) {
            mColorfulProgress.endMark();
        }
        if (mEffectListener != null) {
            mEffectListener.onOtherSpecialEnd(effect, mCurTime);
        }
        showBtnCancelOtherSpecial();
    }




    /**
     * 删除最后一次的其他特效
     */
    private void cancelLastOtherSpecial() {
        if (mColorfulProgress != null) {
            ColorfulProgress.MarkInfo markInfo = mColorfulProgress.deleteLastMark();
            if (markInfo != null) {
                if (mEffectListener != null) {
                    mEffectListener.onOtherSpecialCancel(markInfo.startTimeMs);
                }
            }
            showBtnCancelOtherSpecial();
        }
    }

    /**
     * 显示或隐藏撤销其他特效的按钮
     */
    private void showBtnCancelOtherSpecial() {
        if (mBtnCancelOtherSpecial != null && mColorfulProgress != null) {
            if (mColorfulProgress.getMarkListSize() > 0) {
                if (mBtnCancelOtherSpecial.getVisibility() != View.VISIBLE) {
                    mBtnCancelOtherSpecial.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnCancelOtherSpecial.getVisibility() == View.VISIBLE) {
                    mBtnCancelOtherSpecial.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    public interface EffectListener {
        void onSeekChanged(long currentTimeMs);

        void onCutTimeChanged(long startTimeMs, long endTimeMs);

        void onOtherSpecialStart(int effect, long currentTimeMs);

        void onOtherSpecialEnd(int effect, long currentTimeMs);

        void onOtherSpecialCancel(long currentTimeMs);

        void onTimeDaoFangChanged(boolean add);

        void onTimeFanFuChanged(boolean add, long startTime);

        void onTimeMdzChanged(boolean add, long startTime);
        void replay();

        boolean onHideClicked();
        void showLoadingDialog();
        void disMissLoadingDialog();

    }

    public void setEffectListener(EffectListener effectListener) {
        mEffectListener = effectListener;
    }
}
