package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/7/20.
 */

public class ChatRoomOpenEvent {
    private String mToUid;

    public ChatRoomOpenEvent(String touid) {
        mToUid = touid;
    }

    public String getToUid() {
        return mToUid;
    }
}
