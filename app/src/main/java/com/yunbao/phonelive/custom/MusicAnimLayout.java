package com.yunbao.phonelive.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicAnimBean;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2018/7/2.
 */

public class MusicAnimLayout extends RelativeLayout {

    private Context mContext;
    private float mScale;
    private int mImgWidth;
    private List<MusicAnimBean> mMusicAnimBeanList;
    private boolean mAnimStarted;
    private boolean mAnimPaused;
    private static final int INTERVAL = 700;
    private static final int IMG_COUNT = 4;
    private RoundedImageView mRoundedImageView;
    private ObjectAnimator mAnimator;
    private Handler mHandler;
    private static final int WHAT = 100;

    public MusicAnimLayout(Context context) {
        this(context, null);
    }

    public MusicAnimLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicAnimLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        mMusicAnimBeanList = new ArrayList<>();
        mImgWidth = dp2px(12);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT) {
                    MusicAnimBean bean = mMusicAnimBeanList.get(msg.arg1);
                    bean.startAnim();
                }
            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < IMG_COUNT; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(mImgWidth, mImgWidth));
            if (i % 2 == 0) {
                imageView.setImageResource(R.mipmap.icon_music_1);
            } else {
                imageView.setImageResource(R.mipmap.icon_music_2);
            }
            MusicAnimBean bean = new MusicAnimBean(mImgWidth / 2);
            bean.setImageView(imageView);
            mMusicAnimBeanList.add(bean);
            addView(imageView);
        }
        RoundedImageView roundedImageView = new RoundedImageView(mContext);
        int dp40 = dp2px(40);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp40, dp40);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int dp15 = dp2px(15);
        params.setMargins(0, 0, dp15, dp15);
        roundedImageView.setLayoutParams(params);
        roundedImageView.setBackgroundResource(R.mipmap.bg_music_anim);
        roundedImageView.setOval(true);
        roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int p = dp2px(8);
        roundedImageView.setPadding(p, p, p, p);
        addView(roundedImageView);
        mRoundedImageView = roundedImageView;
        mAnimator = ObjectAnimator.ofFloat(roundedImageView, "rotation", 0, 359f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(5000);
        mAnimator.setRepeatCount(-1);

        Path path1 = new Path();
        path1.lineTo(0, 0);
        path1.moveTo(dp2px(65), dp2px(64));
        path1.rCubicTo(-dp2px(60), 0, -dp2px(30), -dp2px(50), -dp2px(58), -dp2px(58));
        Path path2 = new Path();
        path2.lineTo(0, 0);
        path2.moveTo(dp2px(65), dp2px(64));
        path2.rCubicTo(-dp2px(60), 0, -dp2px(40), -dp2px(50), -dp2px(30), -dp2px(58));

        PathMeasure pathMeasure1 = new PathMeasure(path1, false);
        PathMeasure pathMeasure2 = new PathMeasure(path2, false);
        float length1 = pathMeasure1.getLength();
        float length2 = pathMeasure2.getLength();
        for (int i = 0, size = mMusicAnimBeanList.size(); i < size; i++) {
            MusicAnimBean bean = mMusicAnimBeanList.get(i);
            if (i % 2 == 0) {
                bean.setPathMeasure(pathMeasure1);
                ValueAnimator animator = ValueAnimator.ofFloat(0, length1);
                bean.setAnimator(animator, length1,1);
                animator.setDuration(INTERVAL * IMG_COUNT);
                animator.setRepeatCount(-1);
            } else {
                bean.setPathMeasure(pathMeasure2);
                ValueAnimator animator = ValueAnimator.ofFloat(0, length2);
                bean.setAnimator(animator, length2,-1);
                animator.setDuration(INTERVAL * IMG_COUNT);
                animator.setRepeatCount(-1);
            }
        }
    }


    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }


    public void startAnim() {
        if (!mAnimStarted) {
            mAnimStarted = true;
            mAnimPaused = false;
            for (int i = 0, size = mMusicAnimBeanList.size(); i < size; i++) {
                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = WHAT;
                    msg.arg1 = i;
                    mHandler.sendMessageDelayed(msg, i * INTERVAL);
                }
            }
            mAnimator.start();
        } else {
            if (mAnimPaused) {
                for (int i = 0, size = mMusicAnimBeanList.size(); i < size; i++) {
                    MusicAnimBean bean = mMusicAnimBeanList.get(i);
                    bean.startAnim();
                }
                mAnimator.resume();
                mAnimPaused = false;
            }
        }

    }

    public void pauseAnim() {
        if (mAnimStarted) {
            for (MusicAnimBean bean : mMusicAnimBeanList) {
                if (bean != null) {
                    bean.pauseAnim();
                }
            }
            mAnimator.pause();
            mAnimPaused = true;
        }
    }

    public void setImageUrl(String url) {
        if (mRoundedImageView != null) {
            ImgLoader.display(url, mRoundedImageView);
        }
    }

    public void cancelAnim() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        for (MusicAnimBean bean : mMusicAnimBeanList) {
            if (bean != null) {
                bean.cancelAnim();
            }
        }
        mAnimator.cancel();
        mRoundedImageView.setRotation(0);
        mAnimStarted = false;
        mAnimPaused = false;
    }

}
