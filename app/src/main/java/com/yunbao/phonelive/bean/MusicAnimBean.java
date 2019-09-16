package com.yunbao.phonelive.bean;

import android.animation.ValueAnimator;
import android.graphics.PathMeasure;
import android.widget.ImageView;

/**
 * Created by cxf on 2018/7/2.
 */

public class MusicAnimBean {
    private static final int STATUS_START = 0;
    private static final int STATUS_MIDDLE = 1;
    private static final int STATUS_END = 2;
    private ImageView mImageView;
    private ValueAnimator mAnimator;
    private PathMeasure mPathMeasure;
    private float mLength;
    private float[] mPos;
    private int mOffest;
    private int mStatus = STATUS_START;
    private boolean mAnimStarted;
    private int mRotateDirection;

    public MusicAnimBean(int offest) {
        mPos = new float[2];
        mOffest = offest;
    }

    public void setPathMeasure(PathMeasure pathMeasure) {
        mPathMeasure = pathMeasure;
    }


    public void setImageView(ImageView imageView) {
        mImageView = imageView;
        mImageView.setAlpha(0f);
    }


    public void setAnimator(ValueAnimator animator, float length, int rotateDirection) {
        mLength = length;
        mAnimator = animator;
        mRotateDirection = rotateDirection;
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mPathMeasure != null) {
                    mPathMeasure.getPosTan(v, mPos, null);
                    mImageView.setX(mPos[0] - mOffest);
                    mImageView.setY(mPos[1] - mOffest);
                    if (mStatus == STATUS_START) {
                        float rate = (int) (v / mLength / 0.3f * 100f);
                        if (rate >= 100) {
                            rate = 100;
                            mStatus = STATUS_MIDDLE;
                        }
                        float r = rate / 100f;
                        mImageView.setAlpha(r);
                        mImageView.setScaleX(0.2f + r);
                        mImageView.setScaleY(0.2f + r);
                        mImageView.setRotation(mRotateDirection*30 * r);

                    }
                    if (mStatus == STATUS_MIDDLE) {
                        float rate = (int) ((1 - v / mLength) / 0.3f * 100f);
                        if (rate <= 100) {
                            mStatus = STATUS_END;
                        }
                    }
                    if (mStatus == STATUS_END) {
                        float rate = (int) ((1 - v / mLength) / 0.3f * 100f);
                        if (rate <= 0.1f) {
                            rate = 0;
                            mStatus = STATUS_START;
                        }
                        float r = rate / 100f;
                        mImageView.setAlpha(r);
//                        mImageView.setScaleX(0.3f + r);
//                        mImageView.setScaleY(0.3f + r);
                        mImageView.setRotation(mRotateDirection*30 * r);
                    }
                }
            }
        });
    }

    public void startAnim() {
        if (mAnimator != null) {
            if (mAnimStarted) {
                if (mAnimator.isPaused()) {
                    mAnimator.resume();
                }
            } else {
                mStatus = STATUS_START;
                mAnimator.start();
                mAnimStarted = true;
            }
        }
    }

    public void pauseAnim() {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.pause();
        }
    }

    public void cancelAnim() {
        if (mAnimator != null) {
            mImageView.setAlpha(0f);
            mAnimator.cancel();
            mAnimStarted = false;
        }
    }

}
