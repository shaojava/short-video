package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/7/23.
 */

public class LoginUserChangedEvent {
    private String mUid;
    public LoginUserChangedEvent(String uid){
        mUid=uid;
    }

    public String getUid(){
        return mUid;
    }
}
