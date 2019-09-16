package com.yunbao.phonelive.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;


/**
 * Created by cxf on 2017/8/9.
 * 下拉刷新 上拉加载更多 控件
 */
public class RefreshLayout extends FrameLayout {

    private static final int DOWN_1 = 0;//下拉可以刷新
    private static final int DOWN_2 = 1;//松开立即刷新
    private static final int DOWN_3 = 2;//正在刷新数据中

    private static final int UP_1 = 0;//正在刷新数据中
    private static final int UP_2 = 1;//正在刷新数据中
    private static final int UP_3 = 2;//正在刷新数据中

    private final String TAG = "RefreshLayout";
    private Context mContext;
    //子控件，可以是ListView，RecyclerView，WebView等
    private View mScorllView;
    //整个下拉刷新控件的高度
    private float mHeight;
    //下拉刷新的头部区域
    private View mHeadView;
    //下拉刷新的头部区域高度
    private int mHeadHeight;
    //下拉刷新的箭头图片
    private ImageView mHeadImg;
    //下拉刷新头部的菊花loading
    private View mHeadLoadingView;
    private TextView mHeadTextView;
    private int mDownStatus;
    private int mUpStatus;
    //下拉刷新时候停的高度
    private int mRefreshHeight;

    //底部上拉加载更多区域
    private View mFootView;
    //上拉加载更多区域高度
    private int mFootHeight;
    //上拉加载更多的箭头
    private ImageView mFootImg;
    //上拉加载更多时候的菊花loading
    private View mFootLoadingView;
    private TextView mFootTextView;
    //上拉加载更多时候停的高度
    private int mLoadMoreHeight;
    //上拉加载更多是否可用
    private boolean mLoadMoreEnable;
    //下拉刷新是否可用
    private boolean mRefreshEnable;
    //是否正在刷新或正在加载更多
    private boolean isRefreshing;

    private float mScale;//dp转px时候用的比例
    private float mLastX;
    private float mLastY;
    private OnRefreshListener mOnRefreshListener;
    private LayoutInflater mInflater;
    private String mDown1;//下拉可以刷新
    private String mDown2;//松开立即刷新
    private String mDown3;//正在刷新数据中
    private String mUp1;//上拉可以加载更多
    private String mUp2;//松开立即加载更多
    private String mUp3;//正在加载更多的数据

    private boolean mScrollEnable = true;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        Resources resources = mContext.getResources();
        mScale = resources.getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
        mLoadMoreEnable = ta.getBoolean(R.styleable.RefreshLayout_loadMoreEnable, false);
        mRefreshEnable = ta.getBoolean(R.styleable.RefreshLayout_refreshEnable, true);
        ta.recycle();
        mInflater = LayoutInflater.from(mContext);
        mDown1 = resources.getString(R.string.refresh_down_1);
        mDown2 = resources.getString(R.string.refresh_down_2);
        mDown3 = resources.getString(R.string.refresh_down_3);
        mUp1 = resources.getString(R.string.refresh_up_1);
        mUp2 = resources.getString(R.string.refresh_up_2);
        mUp3 = resources.getString(R.string.refresh_up_3);
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
        if (enable) {
            if (mHeadView.getVisibility() != VISIBLE) {
                mHeadView.setVisibility(VISIBLE);
            }
        } else {
            if (mHeadView.getVisibility() == VISIBLE) {
                mHeadView.setVisibility(INVISIBLE);
            }
        }
    }


    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
        if (enable) {
            if (mFootView.getVisibility() != VISIBLE) {
                mFootView.setVisibility(VISIBLE);
            }
        } else {
            if (mFootView.getVisibility() == VISIBLE) {
                mFootView.setVisibility(INVISIBLE);
            }
        }
    }


    /**
     * 添加头和脚
     */
    private void addHeadAndFoot() {
        mRefreshHeight = dp2px(50);
        mHeadHeight = dp2px(120);
        mFootHeight = dp2px(120);
        mLoadMoreHeight = mRefreshHeight;

        //添加headView
        mHeadView = mInflater.inflate(R.layout.view_refresh_head, this, false);
        mHeadImg = (ImageView) mHeadView.findViewById(R.id.img);
        mHeadLoadingView = mHeadView.findViewById(R.id.loading);
        mHeadTextView = (TextView) mHeadView.findViewById(R.id.text);
        addView(mHeadView);

        //添加footView
        mFootView = mInflater.inflate(R.layout.view_refresh_foot, this, false);
        mFootImg = (ImageView) mFootView.findViewById(R.id.img);
        mFootLoadingView = mFootView.findViewById(R.id.loading);
        mFootTextView = (TextView) mFootView.findViewById(R.id.text);
        addView(mFootView);
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
        if (!mRefreshEnable) {
            mHeadView.setVisibility(INVISIBLE);
        }
        if (!mLoadMoreEnable) {
            mFootView.setVisibility(INVISIBLE);
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
        float scrollY = getScrollY();
        float sy = Math.abs(scrollY);
        if ((scrollY < 0 && deltaY > 0 && (!mRefreshEnable || sy + deltaY > mHeadHeight))
                || (scrollY > 0 && deltaY < 0 && (!mLoadMoreEnable || sy - deltaY > mFootHeight))) {
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
                float rotation = Math.min(180, (scale - 1) * 360);
                mHeadImg.setRotation(rotation);
                if (rotation == 180) {
                    if (mDownStatus != DOWN_2) {
                        mDownStatus = DOWN_2;
                        mHeadTextView.setText(mDown2);
                    }
                } else {
                    if (mDownStatus != DOWN_1) {
                        mDownStatus = DOWN_1;
                        mHeadTextView.setText(mDown1);
                    }
                }
            }
        } else if (getScrollY() > 0) {//表示上拉
            float scale = absScrollY / mLoadMoreHeight;
            if (scale > 1f && scale <= 2f) {
                float rotation = Math.min(360, 180 + (scale - 1) * 360);
                mFootImg.setRotation(rotation);
                if (rotation == 360) {
                    if (mUpStatus != UP_2) {
                        mUpStatus = UP_2;
                        mFootTextView.setText(mUp2);
                    }
                } else {
                    if (mUpStatus != UP_1) {
                        mUpStatus = UP_1;
                        mFootTextView.setText(mUp1);
                    }
                }
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
        if (mDownStatus != DOWN_3) {
            mDownStatus = DOWN_3;
            mHeadTextView.setText(mDown3);
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
        if (mHeadImg.getVisibility() == VISIBLE) {
            mHeadImg.setVisibility(INVISIBLE);
        }
        if (mHeadLoadingView.getVisibility() != VISIBLE) {
            mHeadLoadingView.setVisibility(VISIBLE);
        }
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
        if (mUpStatus != UP_3) {
            mUpStatus = UP_3;
            mFootTextView.setText(mUp3);
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
        if (mFootImg.getVisibility() == VISIBLE) {
            mFootImg.setVisibility(INVISIBLE);
        }
        if (mFootLoadingView.getVisibility() != VISIBLE) {
            mFootLoadingView.setVisibility(VISIBLE);
        }
        isRefreshing = true;
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onLoadMore();
        }
    }

    /**
     * 滚动到顶部起始位置
     */
    private void scrollToTop() {
        if (mDownStatus != DOWN_1) {
            mDownStatus = DOWN_1;
            mHeadTextView.setText(mDown1);
        }
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
                if (mHeadLoadingView.getVisibility() == VISIBLE) {
                    mHeadLoadingView.setVisibility(INVISIBLE);
                }
                if (mHeadImg.getVisibility() != VISIBLE) {
                    mHeadImg.setVisibility(VISIBLE);
                }
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
        if (mUpStatus != UP_1) {
            mUpStatus = UP_1;
            mFootTextView.setText(mUp1);
        }
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
                if (mFootLoadingView.getVisibility() == VISIBLE) {
                    mFootLoadingView.setVisibility(INVISIBLE);
                }
                if (mFootImg.getVisibility() != VISIBLE) {
                    mFootImg.setVisibility(VISIBLE);
                }
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
        if (mUpStatus != UP_1) {
            mUpStatus = UP_1;
            mFootTextView.setText(mUp1);
        }
        if (mFootLoadingView.getVisibility() == VISIBLE) {
            mFootLoadingView.setVisibility(INVISIBLE);
        }
        if (mFootImg.getVisibility() != VISIBLE) {
            mFootImg.setVisibility(VISIBLE);
        }
        mFootImg.setRotation(180);
        isRefreshing = false;
        setScrollY(0);
    }

//    /**
//     * 代码模拟手动下拉刷新
//     */
//    public void beginRefresh() {
//        ObjectAnimator a = ObjectAnimator.ofFloat(this, "scrollY", getScrollY(), -1.8f * mRefreshHeight);
//        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                scrollTo(0, (int) value);
//                scrollAnim(Math.abs(value));
//            }
//        });
//        a.setDuration(500);
//        a.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                scrollToRefreshHeight();
//            }
//        });
//        a.start();
//    }


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


    public void setScrollEnable(boolean scrollEnable) {
        mScrollEnable = scrollEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mScrollEnable && super.dispatchTouchEvent(ev);
    }
}
