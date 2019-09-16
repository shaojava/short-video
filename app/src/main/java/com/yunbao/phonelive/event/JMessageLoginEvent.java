package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/7/20.
 */

public class JMessageLoginEvent {

    private boolean mLogin;

    public JMessageLoginEvent(boolean login) {
        mLogin = login;
    }


    public boolean isLogin() {
        return mLogin;
    }
}
