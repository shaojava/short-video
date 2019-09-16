package com.yunbao.phonelive.presenter;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yunbao.phonelive.event.VisibleHeightEvent;
import com.yunbao.phonelive.utils.L;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2018/6/12.
 */

public class GlobalLayoutPresenter implements ViewTreeObserver.OnGlobalLayoutListener {

    private Activity mActivity;
    private View mRootView;
    private Rect mRect;
    private int mLastHeight;

    public GlobalLayoutPresenter(Activity activity, View rootView) {
        mActivity = new WeakReference<>(activity).get();
        mRootView = rootView;
        mRect = new Rect();
    }

    /**
     * 添加布局变化的监听器
     */
    public void addLayoutListener() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        L.e("onGlobalLayout-----添加onGlobalLayout--->");
    }

    /**
     * 移除布局变化的监听器
     */
    public void removeLayoutListener() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        L.e("onGlobalLayout-----移除onGlobalLayout--->");
    }

    @Override
    public void onGlobalLayout() {
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
        int visibleHeight = mRect.height();
        if (mLastHeight != visibleHeight) {
            mLastHeight = visibleHeight;
            EventBus.getDefault().post(new VisibleHeightEvent(visibleHeight));
        }
    }

    public void release() {
        mActivity = null;
    }


}
