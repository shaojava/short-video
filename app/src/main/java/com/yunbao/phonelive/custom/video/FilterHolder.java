package com.yunbao.phonelive.custom.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.FilterAdapter;
import com.yunbao.phonelive.bean.FilterBean;
import com.yunbao.phonelive.custom.ItemDecoration;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.BitmapUtil;

/**
 * Created by cxf on 2018/6/23.
 * 视频编辑时候的滤镜
 */

public class FilterHolder implements View.OnClickListener {

    private ViewGroup mParent;
    private View mHideView;
    private View mContentView;
    private FilterAdapter mFilterAdapter;
    private FilterEffectListener mFilterEffectListener;

    public FilterHolder(Context context, ViewGroup parent, View hideView) {
        mParent = parent;
        mHideView = hideView;
        View v = LayoutInflater.from(context).inflate(R.layout.view_edit_filter, parent, false);
        mContentView = v;
        v.findViewById(R.id.btn_hide).setOnClickListener(this);
        //滤镜
        RecyclerView filterRecyclerView = (RecyclerView) v.findViewById(R.id.filter_recyclerView);
        filterRecyclerView.setHasFixedSize(true);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ItemDecoration decoration2 = new ItemDecoration(context, 0x00000000, 4, 4);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        filterRecyclerView.addItemDecoration(decoration2);
        mFilterAdapter = new FilterAdapter(context);
        mFilterAdapter.setOnItemClickListener(new OnItemClickListener<FilterBean>() {
            @Override
            public void onItemClick(FilterBean bean, int position) {
                if (mFilterEffectListener != null) {
                    if (bean.getId() == FilterBean.FILTER_ORIGINAL) {
                        mFilterEffectListener.onFilterChanged(null);
                    } else {
                        Bitmap bitmap = bean.getBitmap();
                        if (bitmap == null) {
                            bitmap = BitmapUtil.getInstance().decodeBitmap(bean.getFilterBitmapSrc());
                            bean.setBitmap(bitmap);
                        }
                        mFilterEffectListener.onFilterChanged(bitmap);
                    }
                }
            }
        });
        filterRecyclerView.setAdapter(mFilterAdapter);
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
        }
    }

    public void release() {
        if (mFilterAdapter != null) {
            mFilterAdapter.clear();
        }
    }

    public interface FilterEffectListener {
        void onFilterChanged(Bitmap bitmap);
    }

    public void setFilterEffectListener(FilterEffectListener filterEffectListener) {
        mFilterEffectListener = filterEffectListener;
    }
}
