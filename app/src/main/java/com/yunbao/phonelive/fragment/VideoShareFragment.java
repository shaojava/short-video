package com.yunbao.phonelive.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cn.sharesdk.framework.Platform;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ReportActivity;
import com.yunbao.phonelive.adapter.VideoShareAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.ShareBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.custom.ImageTextView;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DownloadUtil;
import com.yunbao.phonelive.utils.SharedSdkUitl;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class VideoShareFragment extends DialogFragment
        implements View.OnClickListener, OnItemClickListener<ShareBean>
{
    private ActionListener mActionListener;
    private ImageTextView mBtnReport;
    private ConfigBean mConfigBean;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private View mRootView;
    private SharedSdkUitl mSharedSdkUitl;
    private VideoBean mVideoBean;

    private void black()
    {
    }

    private void close()
    {
        dismiss();
    }

    private void copy()
    {
        if (this.mVideoBean != null)
        {
            ((ClipboardManager)this.mContext.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("text", this.mVideoBean.getHref()));
            ToastUtil.show(getString(R.string.clip_success));
            dismiss();
        }
    }

    private void download()
    {
        String str = this.mVideoBean.getHref();
        if (str == null)
            return;
        str = str.substring(str.lastIndexOf("/"));
        final Dialog localDialog = DialogUitl.loadingDialog(this.mContext, WordUtil.getString(R.string.downloading));
        localDialog.show();
        new DownloadUtil().download("tag", AppConfig.VIDEO_PATH, str, this.mVideoBean.getHref(), new DownloadUtil.Callback()
        {
            public void onError(Throwable paramAnonymousThrowable)
            {
                ToastUtil.show(WordUtil.getString(R.string.download_fail));
                localDialog.dismiss();
            }

            public void onProgress(int paramAnonymousInt)
            {
            }

            public void onSuccess(File paramAnonymousFile)
            {
                ToastUtil.show(WordUtil.getString(R.string.download_success));
                localDialog.dismiss();
                DownloadUtil.saveVideoInfo(VideoShareFragment.this.mContext, paramAnonymousFile.getAbsolutePath());
            }
        });
    }

    private void report()
    {
        if (this.mVideoBean != null)
        {
            String str = this.mVideoBean.getId();
            String uid = this.mVideoBean.getUid();
            if (!TextUtils.isEmpty(str))
            {
                if (TextUtils.isEmpty(uid))
                    return;
                if (!uid.equals(AppConfig.getInstance().getUid()))
                {
                    Intent intent = new Intent(this.mContext, ReportActivity.class);
                    intent.putExtra("videoId", str);
                    startActivity(intent);
                    return;
                }
                Dialog dialog = DialogUitl.loadingDialog(this.mContext, WordUtil.getString(R.string.processing));
                dialog.show();
                HttpUtil.deleteVideo(str, new HttpCallback()
                {
                    public void onSuccess(int paramAnonymousInt, String paramAnonymousString, String[] paramAnonymousArrayOfString)
                    {
                        if (dialog != null)
                            dialog.dismiss();
                        VideoShareFragment.this.dismiss();
                        EventBus.getDefault().post(new VideoDeleteEvent(VideoShareFragment.this.mVideoBean));
                    }
                });
                return;
            }
            return;
        }
    }

    private void save()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if ((ContextCompat.checkSelfPermission(this.mContext, "android.permission.READ_EXTERNAL_STORAGE") == 0) && (ContextCompat.checkSelfPermission(this.mContext, "android.permission.WRITE_EXTERNAL_STORAGE") == 0))
            {
                download();
                return;
            }
            requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" }, 100);
            return;
        }
        download();
    }

    public void onActivityCreated(Bundle paramBundle)
    {
        super.onActivityCreated(paramBundle);
        this.mVideoBean = ((VideoBean)getArguments().getParcelable("videoBean"));
        this.mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        this.mBtnReport = ((ImageTextView)this.mRootView.findViewById(R.id.btn_report));
        this.mBtnReport.setOnClickListener(this);
        this.mRootView.findViewById(R.id.btn_copy).setOnClickListener(this);
        this.mRootView.findViewById(R.id.btn_black).setOnClickListener(this);
        this.mRootView.findViewById(R.id.btn_save).setOnClickListener(this);
        this.mRecyclerView = ((RecyclerView)this.mRootView.findViewById(R.id.recyclerView));
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, 0, false));
        this.mSharedSdkUitl = new SharedSdkUitl();
        HttpUtil.getConfig(new CommonCallback<ConfigBean>()
        {
            @Override
            public void callback(ConfigBean paramAnonymousConfigBean)
            {
                //VideoShareFragment.access$002(VideoShareFragment.this, paramAnonymousConfigBean);
                mConfigBean = paramAnonymousConfigBean;
                List<ShareBean> shareBeans = VideoShareFragment.this.mSharedSdkUitl.getShareList(paramAnonymousConfigBean.getShare_type());
                if (shareBeans != null && shareBeans.size() > 0)
                {
                    VideoShareAdapter videoShareAdapter = new VideoShareAdapter(VideoShareFragment.this.mContext, shareBeans);
                    videoShareAdapter.setOnItemClickListener(VideoShareFragment.this);
                    VideoShareFragment.this.mRecyclerView.setAdapter(videoShareAdapter);
                }
            }
        });
        if (this.mVideoBean != null)
        {
            String uid = this.mVideoBean.getUid();
            if ((!TextUtils.isEmpty(uid)) && (uid.equals(AppConfig.getInstance().getUid())))
            {
                this.mBtnReport.setImageResource(R.mipmap.icon_share_delete);
                this.mBtnReport.setText(WordUtil.getString(R.string.delete));
            }
        }
    }

    public void onClick(View paramView)
    {
        switch (paramView.getId())
        {
            case R.id.btn_save:
                save();
                return;
            case R.id.btn_report:
                report();
                return;
            case R.id.btn_copy:
                copy();
                return;
            case R.id.btn_close:
                close();
                return;
            case R.id.btn_black:
            default:
                black();
                return;
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle paramBundle)
    {
        this.mContext = getActivity();
        this.mRootView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_video_share, null);
        Dialog dialog = new Dialog(this.mContext, R.style.dialog2);
        dialog.setContentView(this.mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window localWindow = dialog.getWindow();
        localWindow.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.width = -1;
        localLayoutParams.height = -2;
        localLayoutParams.gravity = 80;
        localWindow.setAttributes(localLayoutParams);
        return dialog;
    }

    public void onItemClick(ShareBean paramShareBean, int paramInt)
    {
        UserBean userBean = this.mVideoBean.getUserinfo();
        if ((this.mConfigBean != null) && (userBean != null))
        {
            SharedSdkUitl localSharedSdkUitl = this.mSharedSdkUitl;
            String type = paramShareBean.getType();
            String str = this.mConfigBean.getVideo_share_title();
            StringBuilder localObject2 = new StringBuilder();
            localObject2.append(userBean.getUser_nicename());
            localObject2.append(this.mConfigBean.getVideo_share_des());
            String thumb = this.mVideoBean.getThumb();
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("http://shipin.xinshizaixian.com/index.php?g=appapi&m=video&a=index&videoid=");
            localStringBuilder.append(this.mVideoBean.getId());
            localSharedSdkUitl.share(type, str, localObject2.toString(), thumb, localStringBuilder.toString(), new SharedSdkUitl.ShareListener()
            {
                public void onCancel(Platform paramAnonymousPlatform)
                {
                }

                public void onError(Platform paramAnonymousPlatform)
                {
                }

                public void onShareFinish()
                {
                }

                public void onSuccess(Platform paramAnonymousPlatform)
                {
                    if (VideoShareFragment.this.mActionListener != null)
                        VideoShareFragment.this.mActionListener.onShareSuccess();
                }
            });
        }
    }

    public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt)
    {
        if (paramInt != 100)
            return;
        if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
        {
            download();
            return;
        }
        ToastUtil.show(getString(R.string.storage_permission_refused));
    }

    public void setActionListener(ActionListener paramActionListener)
    {
        this.mActionListener = paramActionListener;
    }

    public static abstract interface ActionListener
    {
        public abstract void onShareSuccess();
    }
}
