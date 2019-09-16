package com.yunbao.phonelive.upload;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.WordUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by cxf on 2018/5/21.
 * 七牛上传
 */

public class VideoQnUpload implements UploadStrategy {

    private static VideoQnUpload sInstance;
    private UploadManager mUploadManager;
    private final String TAG = "UploadUtil";
    private String mToken;

    private VideoQnUpload() {
        mUploadManager = new UploadManager();
    }

    public static VideoQnUpload getInstance() {
        if (sInstance == null) {
            synchronized (VideoQnUpload.class) {
                if (sInstance == null) {
                    sInstance = new VideoQnUpload();
                }
            }
        }
        return sInstance;
    }


    @Override
    public void upload(final VideoUploadBean bean, final UploadCallback callback) {
        if (bean == null) {
            if (callback != null) {
                callback.onFailure(WordUtil.getString(R.string.upload_failed));
            }
            return;
        }
        final File videoFile = new File(bean.getVideoPath());
        if (!videoFile.exists()) {
            if (callback != null) {
                callback.onFailure(WordUtil.getString(R.string.upload_file_not_exists));
            }
            return;
        }
        final File videoImg = new File(bean.getImgPath());
        if (!videoFile.exists()) {
            if (callback != null) {
                callback.onFailure(WordUtil.getString(R.string.upload_file_not_exists));
            }
            return;
        }
        HttpUtil.getQiniuToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        mToken = JSON.parseObject(info[0]).getString("token");
                        L.e(TAG, "-------上传的token------>" + mToken);
                        //先上传视频文件
                        mUploadManager.put(videoFile, videoFile.getName(), mToken, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                mUploadManager.put(videoImg, videoImg.getName(), mToken, new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        if (callback != null) {
                                            String qiNiuHost = AppConfig.getInstance().getConfig().getQiniu_domain();
                                            bean.setImgName(qiNiuHost + bean.getImgName());
                                            bean.setVideoName(qiNiuHost + bean.getVideoName());
                                            callback.onSuccess(bean);
                                        }
                                    }
                                }, null);
                            }
                        }, null);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(WordUtil.getString(R.string.upload_failed));
                    }
                }
            }
        });
    }

}
