package com.yunbao.phonelive.upload;

import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2018/5/21.
 */

public class VideoUploadManager {

    public static final int UPLOAD_QN = 1;//上传到七牛云
    public static final int UPLOAD_TX = 2;//上传到腾讯云

    private static VideoUploadManager sInstance;

    private VideoUploadManager() {

    }

    public static VideoUploadManager getInstance() {
        if (sInstance == null) {
            synchronized (VideoUploadManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoUploadManager();
                }
            }
        }
        return sInstance;
    }

    public void upload(VideoUploadBean bean, UploadStrategy uploadStrategy, final OnUploadSuccess uploadSuccess) {
        uploadStrategy.upload(bean, new UploadCallback() {
            @Override
            public void onSuccess(VideoUploadBean bean) {
                if (uploadSuccess != null) {
                    uploadSuccess.onSuccess(bean);
                }
            }

            @Override
            public void onFailure(String error) {
                ToastUtil.show(error);
            }

        });
    }

    public interface OnUploadSuccess {
        void onSuccess(VideoUploadBean bean);
    }

}
