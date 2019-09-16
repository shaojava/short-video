package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/6/12.
 */

public class VisibleHeightEvent {

    private int mVisibleHeight;

    public VisibleHeightEvent(int visibleHeight) {
        mVisibleHeight = visibleHeight;
    }

    public int getVisibleHeight() {
        return mVisibleHeight;
    }
}
