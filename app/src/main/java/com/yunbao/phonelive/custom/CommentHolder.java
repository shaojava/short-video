package com.yunbao.phonelive.custom;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ReplyActivity;
import com.yunbao.phonelive.adapter.CommentAdapter;
import com.yunbao.phonelive.bean.CommentBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.ReplyCommentEvent;
import com.yunbao.phonelive.event.ReplyCommentLikeEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.GlobalLayoutChangedListener;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/7/25.
 */

public class CommentHolder implements View.OnClickListener, CommentAdapter.ActionListener {

    private Context mContext;
    private ViewGroup mParent;
    private View mContentView;
    private String mVideoId;
    private String mVideoUid;
    private String mCommentString;
    private int mOriginalHeight;//原始高度
    private int mCurHeight;
    private AtEditText mAtEditText;
    private TextView mNums;//评论数量
    private RefreshView mRefreshView;
    private CommentAdapter mAdapter;
    private String mCurToUid;
    private String mCurCommentId;
    private String mCurParentId;
    private String mReplyString;
    private InputMethodManager imm;
    private boolean mPaused;
    private VideoPlayWrap mPlayWrap;


    public CommentHolder(Context context, ViewGroup parent, String videoId, String videoUid) {
        mContext = context;
        mParent = parent;
        mVideoId = videoId;
        mVideoUid = videoUid;
        mCommentString = WordUtil.getString(R.string.comment);
        View v = LayoutInflater.from(context).inflate(R.layout.view_comment, parent, false);
        mContentView = v;
        v.findViewById(R.id.btn_close).setOnClickListener(this);
        v.findViewById(R.id.btn_at).setOnClickListener(this);
        v.findViewById(R.id.btn_face).setOnClickListener(this);
        mAtEditText = (AtEditText) v.findViewById(R.id.edit);
        mAtEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send();
                    return true;
                }
                return false;
            }
        });
        mAtEditText.setActionListener(new AtEditText.ActionListener() {
            @Override
            public void onAtClick() {
                forwardAtFriendsActivity();
            }

            @Override
            public void onContainsUid() {
                ToastUtil.show(WordUtil.getString(R.string.you_have_at_him));
            }

            @Override
            public void onContainsName() {
                ToastUtil.show(WordUtil.getString(R.string.you_have_at_him_2));
            }
        });
        mNums = (TextView) v.findViewById(R.id.nums);
        mRefreshView = (RefreshView) v.findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_comment);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<CommentBean>() {
            @Override
            public RefreshAdapter<CommentBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new CommentAdapter(mContext);
                    mAdapter.setActionListener(CommentHolder.this);
                    mAdapter.setRefreshView(mRefreshView);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getComments(mVideoId, p, callback);
            }

            @Override
            public List<CommentBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (mNums != null) {
                    mNums.setText(obj.getString("comments") + mCommentString);
                }
                List<CommentBean> list = JSON.parseArray(obj.getString("commentlist"), CommentBean.class);
                return list;
            }

            @Override
            public void onRefresh(List<CommentBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCurToUid = mVideoUid;
        mCurCommentId = "0";
        mCurParentId = "0";
        mReplyString = WordUtil.getString(R.string.reply);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    private void send() {
        if (!AppConfig.getInstance().isLogin()) {
            ToastUtil.show(WordUtil.getString(R.string.please_login));
            return;
        }
        String content = mAtEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            HttpUtil.setComment(mCurToUid, mVideoId, content, mCurCommentId, mCurParentId, mAtEditText.getAtUserInfo(), mSetCommentCallback);
        } else {
            ToastUtil.show(WordUtil.getString(R.string.please_input_content));
        }
    }

    private HttpCallback mSetCommentCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (mPlayWrap != null) {
                    mPlayWrap.setCommentNum(obj.getString("comments"));
                }
                ToastUtil.show(msg);
                imm.hideSoftInputFromWindow(mAtEditText.getWindowToken(), 0); //强制隐藏键盘
                dismiss();
            }
        }
    };

    /**
     * 召唤好友
     */
    private void forwardAtFriendsActivity() {
        ///startActivityForResult(new Intent(mContext, AtFriendsActivity.class), Constants.AT_FRIENDS_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_at:
                forwardAtFriendsActivity();
                break;
            case R.id.btn_face:

                break;
        }
    }

    public void show() {
        if (mParent != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            mParent.addView(mContentView);
            mRefreshView.initData();
            EventBus.getDefault().register(this);
        }
    }

    public void dismiss() {
        HttpUtil.cancel(HttpUtil.SET_COMMENT);
        HttpUtil.cancel(HttpUtil.GET_COMMENTS);
        HttpUtil.cancel(HttpUtil.SET_COMMENT_LIKE);
        ((GlobalLayoutChangedListener) mContext).removeLayoutListener();
        EventBus.getDefault().unregister(this);
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
        if (mAdapter != null) {
            mAdapter.clearData();
        }
        mPlayWrap = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReplyCommentEvent(ReplyCommentEvent e) {
        if (mNums != null) {
            mNums.setText(e.getComments() + mCommentString);
        }
        if (mAdapter != null) {
            mAdapter.updateItemComments(e.getCommentId(), e.getReplys());
        }
        if (mPlayWrap != null) {
            mPlayWrap.setCommentNum(e.getComments());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReplyCommentLikeEvent(ReplyCommentLikeEvent e) {
        if (mAdapter != null) {
            mAdapter.updateItemLike(e.getCommentId(), e.getIsLike(), e.getLikes());
        }
    }

    @Override
    public void onItemClickListener(CommentBean bean, int position) {
        if (bean != null) {
            UserBean u = bean.getUserinfo();
            if (u != null) {
                mCurToUid = u.getId();
                mCurCommentId = bean.getCommentid();
                mCurParentId = bean.getId();
                mAtEditText.setHint(mReplyString + u.getUser_nicename());
                mAtEditText.requestFocus();
                imm.showSoftInput(mAtEditText, InputMethodManager.SHOW_FORCED);
            }
        }
    }

    @Override
    public void onMoreClick(CommentBean bean) {
        Intent intent = new Intent(mContext, ReplyActivity.class);
        intent.putExtra(Constants.COMMENT_BEAN, bean);
        intent.putExtra(Constants.VIDEO_ID, mVideoId);
        mContext.startActivity(intent);
    }

    public void setVideoPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
    }

}
