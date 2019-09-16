package com.yunbao.phonelive.interfaces;

import android.app.Dialog;

/**
 * Created by cxf on 2017/8/11.
 */

public abstract class CommonCallback<T> {
    public abstract void callback(T obj);
    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }
}
