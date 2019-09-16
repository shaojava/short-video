package com.yunbao.phonelive.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/7.
 */

public abstract class RefreshAdapter<T> extends RecyclerView.Adapter {

    protected Context mContext;
    protected List<T> mList;
    protected LayoutInflater mInflater;
    protected int mLoadMoreHeight;
    protected RecyclerView mRecyclerView;
    protected OnItemClickListener<T> mOnItemClickListener;
    private long mLastClickTime;

    public RefreshAdapter(Context context) {
        mList = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mLoadMoreHeight = DpUtil.dp2px(50);
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setList(List<T> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
        }
    }

    public void refreshData(List<T> list) {
        if (mRecyclerView != null && list != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void insertList(List<T> list) {
        if (mRecyclerView != null && mList != null && list != null && list.size() > 0) {
            int p = mList.size();
            mList.addAll(list);
            notifyItemRangeInserted(p, list.size());
            mRecyclerView.scrollBy(0, mLoadMoreHeight);
        }
    }

    public void clearData() {
        if (mRecyclerView != null && mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    protected boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = curTime;
        return true;
    }

    public List<T> getList() {
        return mList;
    }
}
