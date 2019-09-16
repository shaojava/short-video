package com.yunbao.phonelive;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.jpush.JMessageUtil;
import com.yunbao.phonelive.jpush.JPushUtil;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.tillusory.sdk.TiSDK;


/**
 * "破解版仅供参考，请联系官方购买正版,官网www.yunbaokj.com")
 */

public class AppContext extends MultiDexApplication {

    public static AppContext sInstance;
    private List<Activity>mActivitieArray;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivitieArray=new ArrayList<>();
        sInstance = this;
        initException();
        //初始化http
        HttpUtil.init();
        //初始化ShareSdk
        ShareSDK.initSDK(this);
        //初始化极光推送
        JPushUtil.getInstance().init();
        //初始化极光IM
        JMessageUtil.getInstance().init();
        //初始化萌颜
        TiSDK.init(AppConfig.BEAUTY_KEY, this);
        //获取uid和token
        String[] uidAndToken = SharedPreferencesUtil.getInstance().readUidAndToken();
        if (uidAndToken != null) {
            AppConfig.getInstance().login(uidAndToken[0], uidAndToken[1]);
        }

       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);*/

    }

    private void initException() {

    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    public void addActiviy(Activity activity){
        mActivitieArray.add(activity);
    }

    public void removeActivity(Activity activity){
        mActivitieArray.remove(activity);
    }

    public void clearActivitySet(){
        for(Activity activity:mActivitieArray){
            activity.finish();
        }
    }
}
