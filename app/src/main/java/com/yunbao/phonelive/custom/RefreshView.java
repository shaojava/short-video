package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/6/7.
 */

public class RefreshView extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private boolean mEnableRefresh;
    private boolean mEnableLoadMore;
    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private DataHelper mDataHelper;
    private boolean mShowNoData;//是否显示没有数据
    private RelativeLayout mNoData;//没有数据
    private View mLoadFailure;//加载失败
    private View mLoading;
    private boolean mShowLoading;
    private int mPage;
    private boolean mScrollEnable = true;


    public RefreshView(Context context) {
        this(context, null);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshView);
        mShowNoData = ta.getBoolean(R.styleable.RefreshView_showNoData, true);
        mShowLoading = ta.getBoolean(R.styleable.RefreshView_showLoading, true);
        mEnableRefresh = ta.getBoolean(R.styleable.RefreshView_enableRefresh, true);
        mEnableLoadMore = ta.getBoolean(R.styleable.RefreshView_enableLoadMore, true);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_refresh_group, this, false);
        addView(view);
        mNoData = (RelativeLayout) view.findViewById(R.id.no_data);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.setScorllView(mRecyclerView);
        mLoadFailure = view.findViewById(R.id.load_failure);
        mLoading = view.findViewById(R.id.loading);
        if (!mShowLoading) {
            mLoading.setVisibility(INVISIBLE);
        }
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        mRefreshLayout.setRefreshEnable(mEnableRefresh);
        mRefreshLayout.setLoadMoreEnable(mEnableLoadMore);
        View btnReload = view.findViewById(R.id.btn_reload);
        if (btnReload != null) {
            btnReload.setOnClickListener(this);
        }
    }

    private HttpCallback mRefreshCallback = new HttpCallback() {

        private int mDataCount;

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (mDataHelper == null) {
                return;
            }
            if (mShowLoading && mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.INVISIBLE);
            }
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.VISIBLE) {
                mLoadFailure.setVisibility(View.INVISIBLE);
            }
            RefreshAdapter adapter = mDataHelper.getAdapter();
            if (adapter == null) {
                return;
            }
            if (adapter.getRecyclerView() == null) {
                mRecyclerView.setAdapter(adapter);
            }
            if (code != 0) {
                return;
            }
            if (info != null) {
                List list = mDataHelper.processData(info);
                mDataCount = list.size();
                if (list.size() > 0) {
                    if (mShowNoData && mNoData != null && mNoData.getVisibility() == View.VISIBLE) {
                        mNoData.setVisibility(View.INVISIBLE);
                    }
                    mDataHelper.onNoData(false);
                    adapter.refreshData(list);
                    mDataHelper.onRefresh(adapter.getList());
                } else {
                    adapter.clearData();
                    if (mShowNoData && mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                    mDataHelper.onNoData(true);
                }
            } else {
                if (adapter != null) {
                    adapter.clearData();
                }
                if (mShowNoData && mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                    mNoData.setVisibility(View.VISIBLE);
                }
                mDataHelper.onNoData(true);
            }
        }


        @Override
        public void onError() {
            RefreshAdapter adapter = mDataHelper.getAdapter();
            if (adapter != null) {
                adapter.clearData();
            }
            if (mShowNoData && mNoData != null && mNoData.getVisibility() == View.VISIBLE) {
                mNoData.setVisibility(View.INVISIBLE);
            }
            if (mShowLoading && mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.INVISIBLE);
            }
            if (mLoadFailure != null && mLoadFailure.getVisibility() != View.VISIBLE) {
                mLoadFailure.setVisibility(View.VISIBLE);
            }
            mDataHelper.onNoData(true);
        }

        @Override
        public void onFinish() {
            if (mRefreshLayout != null) {
                mRefreshLayout.completeRefresh();
            }
            if (mDataHelper != null) {
                mDataHelper.onLoadDataCompleted(mDataCount);
            }
        }
    };

    private HttpCallback mLoadMoreCallback = new HttpCallback() {

        private int mDataCount;

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (mDataHelper == null) {
                mPage--;
                return;
            }
            if (code != 0) {
                mPage--;
                return;
            }
            if (mLoadFailure != null && mLoadFailure.getVisibility() == View.VISIBLE) {
                mLoadFailure.setVisibility(View.GONE);
            }
            if (info != null) {
                List list = mDataHelper.processData(info);
                mDataCount = list.size();
                RefreshAdapter adapter = mDataHelper.getAdapter();
                if (list.size() > 0) {
                    if (adapter != null) {
                        adapter.insertList(list);
                    }
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                    mPage--;
                }
            } else {
                ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                mPage--;
            }
        }

        @Override
        public void onFinish() {
            if (mRefreshLayout != null) {
                mRefreshLayout.completeLoadMore();
            }
            if (mDataHelper != null) {
                mDataHelper.onLoadDataCompleted(mDataCount);
            }
        }
    };

    public <T> void setDataHelper(DataHelper<T> dataHelper) {
        mDataHelper = dataHelper;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setItemDecoration(ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    public void refreshLocalData(List list) {
        if (mDataHelper != null) {
            RefreshAdapter adapter = mDataHelper.getAdapter();
            if (adapter != null) {
                if (list != null && list.size() > 0) {
                    if (mShowNoData && mNoData != null && mNoData.getVisibility() == View.VISIBLE) {
                        mNoData.setVisibility(View.INVISIBLE);
                    }
                    if (adapter.getRecyclerView() == null) {
                        adapter.setList(list);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        adapter.refreshData(list);
                    }
                } else {
                    adapter.clearData();
                    if (mShowNoData && mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public void showLoading() {
        mPage = 1;
        if (mLoading != null && mLoading.getVisibility() != VISIBLE) {
            mLoading.setVisibility(VISIBLE);
        }
        if (mNoData != null && mNoData.getVisibility() == VISIBLE) {
            mNoData.setVisibility(INVISIBLE);
        }
        if (mLoadFailure != null && mLoadFailure.getVisibility() == VISIBLE) {
            mLoadFailure.setVisibility(INVISIBLE);
        }
    }

    public void showNoData() {
        if (mNoData != null && mNoData.getVisibility() != VISIBLE) {
            mNoData.setVisibility(VISIBLE);
        }
    }

    public void initData() {
        refresh();
    }

    private void refresh() {
        if (mDataHelper != null) {
            mPage = 1;
            mDataHelper.loadData(mPage, mRefreshCallback);
        }
    }

    private void loadMore() {
        if (mDataHelper != null) {
            mPage++;
            mDataHelper.loadData(mPage, mLoadMoreCallback);
        }
    }

    public int getPage() {
        return mPage;
    }

    public void setRefreshEnable(boolean enable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setRefreshEnable(enable);
        }
    }

    public void setLoadMoreEnable(boolean enable) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setLoadMoreEnable(enable);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reload) {
            refresh();
        }
    }


    public interface DataHelper<T> {
        RefreshAdapter<T> getAdapter();

        void loadData(int p, HttpCallback callback);

        List<T> processData(String[] info);

        void onRefresh(List<T> list);

        void onNoData(boolean noData);

        void onLoadDataCompleted(int dataCount);
    }

    //无数据的时候的布局
    public void setNoDataLayoutId(int noDataLayoutId) {
        if (mShowNoData && mNoData != null) {
            View v = LayoutInflater.from(mContext).inflate(noDataLayoutId, mNoData, false);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            v.setLayoutParams(params);
            mNoData.addView(v);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mScrollEnable && super.dispatchTouchEvent(ev);
    }

    public void setScrollEnable(boolean scrollEnable) {
        mScrollEnable = scrollEnable;
    }

}
