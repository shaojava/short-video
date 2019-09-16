package com.yunbao.phonelive.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yunbao.phonelive.R;


/**
 * Created by cxf on 2017/8/9.
 * 下拉刷新 上拉加载更多 控件
 */
public class RefreshLayout2 extends FrameLayout {

    private final String TAG = "RefreshLayout";
    private Context mContext;
    //子控件，可以是ListView，RecyclerView，WebView等
    private View mScorllView;
    //整个下拉刷新控件的高度
    private float mHeight;
    //下拉刷新的头部区域
    private RelativeLayout mHeadView;
    //下拉刷新的头部区域高度
    private int mHeadHeight;
    //下拉刷新的箭头图片
    private ImageView mHeadImg;
    //下拉刷新头部的菊花loading
    private View mHeadLoadingView;
    //下拉刷新时候停的高度
    private int mRefreshHeight;

    //底部上拉加载更多区域
    private RelativeLayout mFootView;
    //上拉加载更多区域高度
    private int mFootHeight;
    //上拉加载更多的箭头
    private ImageView mFootImg;
    //上拉加载更多时候的菊花loading
    private View mFootLoadingView;
    //上拉加载更多时候停的高度
    private int mLoadMoreHeight;

    //上拉加载更多是否可用
    private boolean mLoadMoreEnable;

    //下拉刷新是否可用
    private boolean mRefreshEnable;
    //是否正在刷新或正在加载更多
    private boolean isRefreshing;
    //黑色和透明色两种风格，默认是黑色
    private int mStyle = 0;

    private float mScale;//dp转px时候用的比例
    private float mLastX;
    private float mLastY;
    private OnRefreshListener mOnRefreshListener;


    public RefreshLayout2(Context context) {
        this(context, null);
    }

    public RefreshLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScale = mContext.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
        mLoadMoreEnable = ta.getBoolean(R.styleable.RefreshLayout_loadMoreEnable, false);
        mRefreshEnable = ta.getBoolean(R.styleable.RefreshLayout_refreshEnable, true);
        mStyle = ta.getInt(R.styleable.RefreshLayout_loadStyle, 0);
        ta.recycle();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new RuntimeException("RefreshLayout最多只能有一个子view");
        }
        addHeadAndFoot();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
    }


    public void setRefreshEnable(boolean enable) {
        mRefreshEnable = enable;
    }


    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
    }


    /**
     * 添加头和脚
     */
    private void addHeadAndFoot() {
        mRefreshHeight = dp2px(50);
        int arrowRes = mStyle == 0 ? R.mipmap.icon_down_arrow : R.mipmap.icon_down_arrow_2;
//        if (mRefreshEnable) {
//
//        }
        //添加headView
        mHeadView = new RelativeLayout(mContext);
        mHeadHeight = dp2px(120);
        LayoutParams headViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeadHeight);
        mHeadView.setLayoutParams(headViewParams);
        if (mStyle == 0) {
            mHeadView.setBackgroundColor(0xff000000);
        }
        addView(mHeadView);
        //添加headImg
        mHeadImg = new ImageView(mContext);
        RelativeLayout.LayoutParams headImgParams = new RelativeLayout.LayoutParams(dp2px(30), dp2px(30));
        headImgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        headImgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        headImgParams.setMargins(0, 0, 0, dp2px(10));
        mHeadImg.setLayoutParams(headImgParams);
        mHeadImg.setImageResource(arrowRes);
        mHeadImg.setScaleType(ImageView.ScaleType.FIT_XY);
        mHeadView.addView(mHeadImg);
        mHeadLoadingView = LayoutInflater.from(mContext).inflate(R.layout.view_loading, mHeadView, false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHeadLoadingView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(0, 0, 0, dp2px(13));
        mHeadView.addView(mHeadLoadingView);
        mHeadLoadingView.setVisibility(GONE);


//        if (mLoadMoreEnable) {
//
//        }

        mLoadMoreHeight = mRefreshHeight;
        //添加footView
        mFootView = new RelativeLayout(mContext);
        mFootHeight = dp2px(120);
        LayoutParams footViewparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFootHeight);
        mFootView.setLayoutParams(footViewparams);
        if (mStyle == 0) {
            mFootView.setBackgroundColor(0xff000000);
        }
        addView(mFootView);
        //添加footImg
        mFootImg = new ImageView(mContext);
        RelativeLayout.LayoutParams footImgParams = new RelativeLayout.LayoutParams(dp2px(30), dp2px(30));
        footImgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        footImgParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        footImgParams.setMargins(0, dp2px(10), 0, 0);
        mFootImg.setLayoutParams(footImgParams);
        mFootImg.setImageResource(arrowRes);
        mFootImg.setScaleType(ImageView.ScaleType.FIT_XY);
        mFootImg.setRotation(180);
        mFootView.addView(mFootImg);

        mFootLoadingView = LayoutInflater.from(mContext).inflate(R.layout.view_loading, mFootView, false);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) mFootLoadingView.getLayoutParams();
        params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.setMargins(0, dp2px(13), 0, 0);
        mFootView.addView(mFootLoadingView);
        mFootLoadingView.setVisibility(GONE);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mHeadView != null) {
            mHeadView.layout(l, -mHeadView.getMeasuredHeight(), r, 0);
        }
        if (mFootView != null) {
            mFootView.layout(l, (int) mHeight, r, (int) mHeight + mFootView.getMeasuredHeight());
        }
    }

    public void setScorllView(View view) {
        mScorllView = view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean intercepted = false;
        if (isRefreshing) {
            return intercepted;
        }
        if (mScorllView == null) {
            return intercepted;
        }
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    if (deltaY > 0 && !mScorllView.canScrollVertically(-1) && mRefreshEnable   //mScorllView不能朝下滚动时候，可以下拉
                            || deltaY < 0 && !mScorllView.canScrollVertically(1) && mLoadMoreEnable) {//mScorllView不能朝上滚动的时候，可以上拉
                        intercepted = true;
                    }
                }
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (isRefreshing) {
            return false;
        }
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                if (Math.abs(deltaY) > Math.abs(deltaX)) {
                    scroll(deltaY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getScrollY() < 0 && mRefreshEnable) {
                    if (-getScrollY() >= mRefreshHeight) {
                        scrollToRefreshHeight();
                    } else {
                        scrollToTop();
                    }
                } else if (getScrollY() > 0 && mLoadMoreEnable) {
                    if (getScrollY() >= mLoadMoreHeight) {
                        scrollToLoadMoreHeight();
                    } else {
                        scrollToBottom();
                    }
                }
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    /**
     * 执行滚动
     *
     * @param deltaY
     */
    private void scroll(float deltaY) {
        float sy = Math.abs(getScrollY());
        if ((getScrollY() < 0 && deltaY > 0 && (!mRefreshEnable||sy + deltaY > mHeadHeight))
                || (getScrollY() > 0 && deltaY < 0 && (!mLoadMoreEnable||sy - deltaY > mFootHeight))) {
            deltaY = 0;
        }
        //stick表示粘度
        float stick = (1 - sy / mHeight) * 0.5f;
        scrollBy(0, -(int) (deltaY * stick));
        scrollAnim(sy);
    }

    /**
     * 滚动时候的一些动画
     */
    private void scrollAnim(float absScrollY) {
        if (getScrollY() < 0) {//表示下拉
            float scale = absScrollY / mRefreshHeight;
            if (scale > 1f && scale <= 2f) {
                mHeadImg.setRotation(Math.min(180, (scale - 1) * 360));
            }
        } else if (getScrollY() > 0) {//表示上拉
            float scale = absScrollY / mLoadMoreHeight;
            if (scale > 1f && scale <= 2f) {
                mFootImg.setRotation(Math.min(360, 180 + (scale - 1) * 360));
            }
        }
    }

    /**
     * 结束刷新
     */
    public void completeRefresh() {
        if (mRefreshEnable) {
            scrollToTop();
        }
    }

    /**
     * 结束加载更多
     */
    public void completeLoadMore() {
        if (mLoadMoreEnable) {
            scrollToBottom2();
        }
    }

    /**
     * 滚动到顶部刷新的位置
     */
    private void scrollToRefreshHeight() {
        if (mHeadImg.getRotation() != 180) {
            mHeadImg.setRotation(180);
        }
        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), -mRefreshHeight);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scrollTo(0, (int) value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                executeRefresh();
            }
        });
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(200);
        a.start();
    }

    /**
     * 执行刷新
     */
    private void executeRefresh() {
        mHeadImg.setVisibility(GONE);
        mHeadLoadingView.setVisibility(VISIBLE);
        isRefreshing = true;
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * 滚动到底部加载更多的位置
     */
    private void scrollToLoadMoreHeight() {
        if (mFootImg.getRotation() != 360) {
            mFootImg.setRotation(360);
        }
        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), mLoadMoreHeight);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scrollTo(0, (int) value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                executeLoadMore();
            }
        });
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(150);
        a.start();
    }

    /**
     * 执行加载更多
     */
    private void executeLoadMore() {
        mFootImg.setVisibility(GONE);
        mFootLoadingView.setVisibility(VISIBLE);
        isRefreshing = true;
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onLoadMore();
        }
    }

    /**
     * 滚动到顶部起始位置
     */
    private void scrollToTop() {
        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), 0);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scrollTo(0, (int) value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHeadLoadingView.setVisibility(GONE);
                mHeadImg.setVisibility(VISIBLE);
                mHeadImg.setRotation(0);
                isRefreshing = false;
            }
        });
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(150);
        a.start();
    }

    /**
     * 滚动到底部起始位置，有动画
     */
    private void scrollToBottom() {
        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), 0);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                scrollTo(0, (int) value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFootLoadingView.setVisibility(GONE);
                mFootImg.setVisibility(VISIBLE);
                mFootImg.setRotation(180);
                isRefreshing = false;
            }
        });
        a.setInterpolator(new AccelerateInterpolator());
        a.setDuration(150);
        a.start();
    }

    /**
     * 滚动到底部起始位置，无动画
     */
    private void scrollToBottom2() {
        mFootLoadingView.setVisibility(GONE);
        mFootImg.setVisibility(VISIBLE);
        mFootImg.setRotation(180);
        isRefreshing = false;
        setScrollY(0);
    }

    /**
     * 代码模拟手动下拉刷新
     */
    public void beginRefresh() {
        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), -1.8f * mRefreshHeight);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                scrollTo(0, (int) value);
                scrollAnim(Math.abs(value));
            }
        });
        a.setDuration(500);
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scrollToRefreshHeight();
            }
        });
        a.start();
    }


    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }


    public interface OnRefreshListener {
        void onRefresh();//刷新

        void onLoadMore();//加载更多
    }

    //dp转px
    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }

}
