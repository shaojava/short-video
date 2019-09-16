package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AtFriendsActivity;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.AtEditText;
import com.yunbao.phonelive.custom.VideoPlayWrap;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by cxf on 2018/6/15.
 */

public class VideoInputFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private AtEditText mAtEditText;
    private VideoBean mVideoBean;
    private InputMethodManager imm;
    private Handler mHandler;
    private VideoPlayWrap mPlayWrap;
    private OnStateListener mOnStateListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_video_comment_input, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(46);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mVideoBean = bundle.getParcelable(Constants.VIDEO_BEAN);
        mAtEditText = (AtEditText) mRootView.findViewById(R.id.edit);
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
        mAtEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    return true;
                }
                return false;
            }
        });
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                if(mAtEditText!=null){
                    mAtEditText.requestFocus();
                }
                if(mOnStateListener!=null){
                    mOnStateListener.onShow();
                }
            }
        };
        mRootView.findViewById(R.id.btn_at).setOnClickListener(this);
    }

    private void sendComment() {
        String content = mAtEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            HttpUtil.setComment(mVideoBean.getUid(), mVideoBean.getId(), content, "0", "0", mAtEditText.getAtUserInfo(), mSetCommentCallback);
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
    public void onResume() {
        super.onResume();
        //不加延时，软键盘不会自动弹出
        if(mHandler!=null){
            mHandler.sendEmptyMessageDelayed(0, 200);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(mAtEditText.getWindowToken(), 0);
    }


    @Override
    public void onDestroy() {
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
        HttpUtil.cancel(HttpUtil.GET_COMMENTS);
        mPlayWrap = null;
        if(mOnStateListener !=null){
            mOnStateListener.onHide();
        }
        mOnStateListener =null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_at:
                forwardAtFriendsActivity();
                break;
        }
    }

    public void setVideoPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
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
                mAtEditText.requestFocus();
            }
        }
    }

    public interface OnStateListener {
        void onShow();
        void onHide();
    }

    public void setOnStateListener(OnStateListener onStateListener){
        mOnStateListener = onStateListener;
    }
}
