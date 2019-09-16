package com.yunbao.phonelive.upload;

/**
 * Created by cxf on 2018/5/21.
 */

public interface UploadCallback {
    void onSuccess(VideoUploadBean bean);

    void onFailure(String error);

}
