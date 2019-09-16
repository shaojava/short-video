package com.yunbao.phonelive.upload;

/**
 * Created by cxf on 2018/5/21.
 */

public interface UploadStrategy {
    void upload(VideoUploadBean bean, UploadCallback callback);
}
