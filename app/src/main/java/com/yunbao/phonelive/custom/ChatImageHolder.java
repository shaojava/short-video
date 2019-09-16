package com.yunbao.phonelive.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.ChatImageAdapter;
import com.yunbao.phonelive.glide.ImgLoader;

import java.util.List;

/**
 * Created by cxf on 2018/8/1.
 */

public class ChatImageHolder {

    private Context mContext;
    private ViewGroup mParent;
    private int mScreenWidth;
    private int mScreenHeight;
    private View mContentView;
    private ViewPager mViewPager;
    private ImageView mCover;
    private ValueAnimator mShowAnimator;
    private ValueAnimator mHideAnimator;
    private int mStartX;
    private int mStartY;
    private int mDeltaX;
    private int mDeltaY;
    private int mStartWidth;
    private int mStartHeight;
    private int mDeltaWidth;
    private int mDeltaHeight;
    private ChatImageAdapter mChatImageAdapter;
    private List<String> mFilePathList;
    private int mCurPosition;
    private boolean mShowing;

    public ChatImageHolder(Context context, ViewGroup parent, int screenWidth, int screenHeight) {
        mContext = context;
        mParent = parent;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        View v = LayoutInflater.from(context).inflate(R.layout.view_chat_img, parent, false);
        mContentView = v;
        mViewPager = (ViewPager) v.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(1);
        mCover = (ImageView) v.findViewById(R.id.cover);
        mShowAnimator = ValueAnimator.ofFloat(0, 1);
        mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                int wdith = mStartWidth + (int) (mDeltaWidth * v);
                int height = mStartHeight + (int) (mDeltaHeight * v);
                ViewGroup.LayoutParams params = mCover.getLayoutParams();
                params.width = wdith;
                params.height = height;
                mCover.setLayoutParams(params);
                int x = mStartX + (int) (mDeltaX * v);
                int y = mStartY + (int) (mDeltaY * v);
                mCover.setX(x);
                mCover.setY(y);
            }
        });
        mShowAnimator.setDuration(400);
        mShowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mViewPager.getVisibility() != View.VISIBLE) {
                    mViewPager.setVisibility(View.VISIBLE);
                }
                if (mChatImageAdapter == null) {
                    mChatImageAdapter = new ChatImageAdapter(mContext, mFilePathList, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hide();
                        }
                    });
                    mViewPager.setAdapter(mChatImageAdapter);
                } else {
                    mChatImageAdapter.refreshList(mFilePathList);
                }
                mViewPager.setCurrentItem(mCurPosition);
                mCover.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCover.getVisibility() == View.VISIBLE) {
                            mCover.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 200);
            }
        });

        mHideAnimator = ValueAnimator.ofFloat(1, 0);
        mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                int wdith = mStartWidth + (int) (mDeltaWidth * v);
                int height = mStartHeight + (int) (mDeltaHeight * v);
                ViewGroup.LayoutParams params = mCover.getLayoutParams();
                params.width = wdith;
                params.height = height;
                mCover.setLayoutParams(params);
                int x = mStartX + (int) (mDeltaX * v);
                int y = mStartY + (int) (mDeltaY * v);
                mCover.setX(x);
                mCover.setY(y);
            }
        });
        mHideAnimator.setDuration(400);
        mHideAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mContentView.getParent() != null) {
                    mParent.removeView(mContentView);
                }
            }
        });
    }

    public void show(String filePath, int x, int y, int wdith, int height, List<String> filePathList, int curPosition) {
        mShowing=true;
        if (mViewPager.getVisibility() == View.VISIBLE) {
            mViewPager.setVisibility(View.INVISIBLE);
        }
        if (mCover.getVisibility() != View.VISIBLE) {
            mCover.setVisibility(View.VISIBLE);
        }
        if (mContentView.getParent() == null) {
            mParent.addView(mContentView);
        }
        mFilePathList = filePathList;
        mCurPosition = curPosition;
        mStartX = x;
        mStartY = y;
        mDeltaX = -x;
        mDeltaY = -y;
        ViewGroup.LayoutParams params = mCover.getLayoutParams();
        params.width = wdith;
        params.height = height;
        mCover.setLayoutParams(params);
        mCover.setX(x);
        mCover.setY(y);
        mStartWidth = wdith;
        mStartHeight = height;
        mDeltaWidth = mScreenWidth - wdith;
        mDeltaHeight = mScreenHeight - height;
        ImgLoader.display(filePath, mCover);
        mShowAnimator.start();
    }

    public boolean hide() {
        if(mShowing){
            mShowing=false;
            mShowAnimator.cancel();
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
            if (mViewPager.getVisibility() == View.VISIBLE) {
                mViewPager.setVisibility(View.INVISIBLE);
            }
            mHideAnimator.start();
            return false;
        }
        return true;
    }


}
