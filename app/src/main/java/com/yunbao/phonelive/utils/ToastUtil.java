package com.yunbao.phonelive.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;

    static {
        sToast = new Toast(AppContext.sInstance);
        sToast.setDuration(Toast.LENGTH_SHORT);
        sToast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_toast, null);
        sToast.setView(view);
    }

    public static void show(String s) {
        sToast.setText(s);
        sToast.show();
    }

    public static void showBanquan(){
        ToastUtil.show("破解版仅供参考，请联系官方购买正版,官网www.yunbaokj.com");
    }
    public static void showBanquan2(){
        ToastUtil.show("破解版仅供参考，请联系官方购买正版,官网www.yunbaokj.com");
    }
}
