package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AtFriendsActivity;
import com.yunbao.phonelive.activity.ReplyActivity;
import com.yunbao.phonelive.adapter.CommentAdapter;
import com.yunbao.phonelive.bean.CommentBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.AtEditText;
import com.yunbao.phonelive.custom.RefreshAdapter;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.custom.VideoPlayWrap;
import com.yunbao.phonelive.event.ReplyCommentEvent;
import com.yunbao.phonelive.event.ReplyCommentLikeEvent;
import com.yunbao.phonelive.event.VisibleHeightEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.GlobalLayoutChangedListener;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cxf on 2018/6/12.
 */

public class VideoCommentFragment extends DialogFragment implements View.OnClickListener, CommentAdapter.ActionListener {

    private Context mContext;
    private View mRootView;
    private int mOriginalHeight;//原始高度
    private int mCurHeight;
    private AtEditText mAtEditText;
    private TextView mNums;//评论数量
    private RefreshView mRefreshView;
    private String mVideoId;
    private String mVideoUid;
    private String mCommentString;
    private CommentAdapter mAdapter;
    private String mCurToUid;
    private String mCurCommentId;
    private String mCurParentId;
    private String mReplyString;
    private InputMethodManager imm;
    private boolean mPaused;
    private VideoPlayWrap mPlayWrap;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Bundle bundle = getArguments();
        mVideoId = bundle.getString(Constants.VIDEO_ID);
        mVideoUid = bundle.getString(Constants.UID);
        boolean fullScreen = bundle.getBoolean(Constants.FULL_SCREEN);
        if (fullScreen) {
            mOriginalHeight = ScreenDimenUtil.getInstance().getContentHeight();
        } else {
            mOriginalHeight = (int) (ScreenDimenUtil.getInstance().getScreenHeight() * 0.65f);
        }
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_video_comment, null);
        mCurHeight = mOriginalHeight;
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = mCurHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCommentString = WordUtil.getString(R.string.comment);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_at).setOnClickListener(this);
        //mRootView.findViewById(R.id.btn_face).setOnClickListener(this);
        mAtEditText = (AtEditText) mRootView.findViewById(R.id.edit);
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
        mNums = (TextView) mRootView.findViewById(R.id.nums);
        mRefreshView = (RefreshView) mRootView.findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_comment);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<CommentBean>() {
            @Override
            public RefreshAdapter<CommentBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new CommentAdapter(mContext);
                    mAdapter.setActionListener(VideoCommentFragment.this);
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
        mRefreshView.initData();
        mCurToUid = mVideoUid;
        mCurCommentId = "0";
        mCurParentId = "0";
        mReplyString = WordUtil.getString(R.string.reply);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void VisibleHeightEvent(VisibleHeightEvent e) {
        if (!mPaused) {
            int visibleHeight = e.getVisibleHeight();
            if (visibleHeight >= mOriginalHeight) {
                mCurHeight = mOriginalHeight;
            } else {
                mCurHeight = visibleHeight;
            }
            Dialog dialog = getDialog();
            if (dialog != null) {
                Window window = getDialog().getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.height = mCurHeight;
                window.setAttributes(params);
            }
        }
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
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
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
//            case R.id.btn_face:
//                break;
        }
    }

    /**
     * 召唤好友
     */
    private void forwardAtFriendsActivity() {
        startActivityForResult(new Intent(mContext, AtFriendsActivity.class), Constants.AT_FRIENDS_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.AT_FRIENDS_CODE && resultCode == RESULT_OK) {
            String uid = intent.getStringExtra(Constants.UID);
            String username = intent.getStringExtra(Constants.USER_NICE_NAME);
            if (mAtEditText != null) {
                mAtEditText.addAtSpan(uid, username);
            }
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpUtil.cancel(HttpUtil.SET_COMMENT);
        HttpUtil.cancel(HttpUtil.GET_COMMENTS);
        HttpUtil.cancel(HttpUtil.SET_COMMENT_LIKE);
        ((GlobalLayoutChangedListener) mContext).removeLayoutListener();
        EventBus.getDefault().unregister(this);
        mPlayWrap = null;
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
        startActivity(intent);
    }

    public void setVideoPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
    }


}
