package com.yunbao.phonelive.event;

import com.yunbao.phonelive.bean.UserBean;

/**
 * Created by cxf on 2018/7/20.
 */

public class ChatRoomCloseEvent {
    private String mToUid;
    private String mLastMessage;
    private String mLastTime;
    private UserBean mToUserBean;

    public ChatRoomCloseEvent(String toUid, String lastMessage, String lastTime, UserBean toUserBean) {
        mToUid = toUid;
        mLastMessage = lastMessage;
        mLastTime=lastTime;
        mToUserBean=toUserBean;
    }

    public String getToUid() {
        return mToUid;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public String getLastTime(){
        return mLastTime;
    }

    public UserBean getToUserBean() {
        return mToUserBean;
    }
}
