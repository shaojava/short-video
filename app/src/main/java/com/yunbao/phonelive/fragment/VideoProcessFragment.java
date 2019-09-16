package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.custom.record.NumberProgressBar;

/**
 * Created by cxf on 2018/6/26.
 */

public class VideoProcessFragment extends DialogFragment {

    private Context mContext;
    private View mRootView;
    private NumberProgressBar mProgressBar;
    private TextView mTextView;
    private ActionListener mActionListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_video_process, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_video_process, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = (NumberProgressBar) mRootView.findViewById(R.id.progressbar);
        mTextView = (TextView) mRootView.findViewById(R.id.title);
        Bundle bundle = getArguments();
        String title = bundle.getString(Constants.VIDEO_PROCESS_DES);
        if (!TextUtils.isEmpty(title)) {
            mTextView.setText(title);
        }
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onCancelClick();
                }
            }
        });
    }

    public void setProgress(float progress) {
        if (mProgressBar != null) {
            int p = (int) (progress * 100);
            if (p > 1 && p <= 100) {
                mProgressBar.setProgress(p);
            }
        }
    }

    public interface ActionListener {
        void onCancelClick();
    }

    public void setActionListener(ActionListener actionListener){
        mActionListener=actionListener;
    }
}
