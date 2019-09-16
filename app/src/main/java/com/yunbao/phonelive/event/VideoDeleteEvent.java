package com.yunbao.phonelive.event;

import com.yunbao.phonelive.bean.VideoBean;

/**
 * Created by cxf on 2018/7/30.
 */

public class VideoDeleteEvent {
    private VideoBean mVideoBean;

    public VideoDeleteEvent(VideoBean videoBean) {
        mVideoBean = videoBean;
    }

    public VideoBean getVideoBean() {
        return mVideoBean;
    }
}
