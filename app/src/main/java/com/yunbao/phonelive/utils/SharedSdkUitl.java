package com.yunbao.phonelive.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ShareBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by cxf on 2017/8/29.
 * sharedSDK登录 分享
 */

public class SharedSdkUitl {

    private static final int CODE_SUCCESS = 200;//成功
    private static final int CODE_ERROR = 300;//失败
    private static final int CODE_CANCEL = 400;//取消

    private PlatformActionListener mPlatformActionListener;
    private Handler mHandler;
    private ShareListener mListener;

    public SharedSdkUitl() {
        mPlatformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Message msg = Message.obtain();
                msg.what = CODE_SUCCESS;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Message msg = Message.obtain();
                msg.what = CODE_ERROR;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Message msg = Message.obtain();
                msg.what = CODE_CANCEL;
                msg.obj = platform;
                mHandler.sendMessage(msg);
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Platform platform = (Platform) msg.obj;
                switch (msg.what) {
                    case CODE_SUCCESS:
                        if (mListener != null) {
                            mListener.onSuccess(platform);
                            mListener.onShareFinish();
                        }
                        break;
                    case CODE_ERROR:
                        if (mListener != null) {
                            mListener.onError(platform);
                            mListener.onShareFinish();
                        }
                        break;
                    case CODE_CANCEL:
                        if (mListener != null) {
                            mListener.onCancel(platform);
                            mListener.onShareFinish();
                        }
                        break;
                }
            }
        };
    }


    /**
     * 登录
     *
     * @param platType 平台类型
     */
    public void login(String platType, ShareListener listener) {
        mListener = listener;
        String platName = null;
        switch (platType) {
            case Constants.TYPE_QQ:
                platName = QQ.NAME;
                break;
            case Constants.TYPE_WX:
                platName = Wechat.NAME;
                break;
        }
        if (platName == null) {
            return;
        }
        Platform platform = null;
        try {
            platform = ShareSDK.getPlatform(platName);
            platform.setPlatformActionListener(mPlatformActionListener);
            platform.SSOSetting(false);
            platform.removeAccount(true);
            platform.showUser(null);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(platform);
            }
        }

    }

    /**
     * 分享
     *
     * @param platType 平台类型
     * @param des      分享的描述
     * @param title    分享的标题
     * @param url      分享的链接
     */
    public void share(String platType, String title, String des, String imgUrl, String url, ShareListener listener) {
        if (TextUtils.isEmpty(platType)) {
            return;
        }
        mListener = listener;
        String platName = null;
        switch (platType) {
            case Constants.TYPE_QQ:
                platName = QQ.NAME;
                break;
            case Constants.TYPE_QZONE:
                platName = QZone.NAME;
                break;
            case Constants.TYPE_WX:
                platName = Wechat.NAME;
                break;
            case Constants.TYPE_WX_PYQ:
                platName = WechatMoments.NAME;
                break;
            case Constants.TYPE_FACEBOOK:
                platName = Facebook.NAME;
                break;
            case Constants.TYPE_TWITTER:
                platName = Twitter.NAME;
                break;
        }
        if (platName == null) {
            return;
        }
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(true);
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platName);
        oks.setTitle(title);
        oks.setText(des);
        oks.setImageUrl(imgUrl);
        oks.setUrl(url);
        oks.setSiteUrl(url);
        oks.setTitleUrl(url);
        oks.setSite(WordUtil.getString(R.string.app_name));
        oks.setCallback(mPlatformActionListener);
        oks.show(AppContext.sInstance);
    }


    public List<ShareBean> getShareList(String[] shareTypes) {
        List<ShareBean> list = null;
        if (shareTypes.length > 0) {
            list = new ArrayList<>();
            for (String type : shareTypes) {
                ShareBean bean = null;
                switch (type) {
                    case Constants.TYPE_QQ:
                        bean = new ShareBean(Constants.TYPE_QQ, R.mipmap.icon_share_qq, R.mipmap.icon_share_qq_2, WordUtil.getString(R.string.share_qq));
                        break;
                    case Constants.TYPE_QZONE:
                        bean = new ShareBean(Constants.TYPE_QZONE, R.mipmap.icon_share_qzone, R.mipmap.icon_share_qzone_2, WordUtil.getString(R.string.share_qzone));
                        break;
                    case Constants.TYPE_WX:
                        bean = new ShareBean(Constants.TYPE_WX, R.mipmap.icon_share_wx, R.mipmap.icon_share_wx_2, WordUtil.getString(R.string.share_wx));
                        break;
                    case Constants.TYPE_WX_PYQ:
                        bean = new ShareBean(Constants.TYPE_WX_PYQ, R.mipmap.icon_share_wx_pyq, R.mipmap.icon_share_wx_pyq_2, WordUtil.getString(R.string.share_wx_pyq));
                        break;
                    case Constants.TYPE_FACEBOOK:
                        bean = new ShareBean(Constants.TYPE_FACEBOOK, R.mipmap.icon_share_facebook, R.mipmap.icon_share_facebook_2, WordUtil.getString(R.string.share_wx_facebook));
                        break;
                    case Constants.TYPE_TWITTER:
                        bean = new ShareBean(Constants.TYPE_TWITTER, R.mipmap.icon_share_twitter, R.mipmap.icon_share_twitter_2, WordUtil.getString(R.string.share_wx_twitter));
                        break;
                }
                if (bean != null) {
                    list.add(bean);
                }
            }
        }
        return list;
    }

    public interface ShareListener {
        void onSuccess(Platform platform);

        void onError(Platform platform);

        void onCancel(Platform platform);

        void onShareFinish();
    }


    public void cancelListener() {
        mListener = null;
    }

}
