package com.yunbao.phonelive.jpush;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.utils.L;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by cxf on 2017/8/3.
 */

public class JPushUtil {

    public static final String TAG = "极光推送";
    private boolean hasAlians;//是否设置了别名
    private static JPushUtil sInstance;

    private JPushUtil() {

    }

    public static JPushUtil getInstance() {
        if (sInstance == null) {
            synchronized (JPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new JPushUtil();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        JPushInterface.init(AppContext.sInstance);
    }

    public void setAlias(String uid) {
        if (JPushInterface.isPushStopped(AppContext.sInstance)) {
            JPushInterface.resumePush(AppContext.sInstance);
        }
        if (hasAlians) {
            L.e(TAG, "已经设置了别名---->");
        } else {
            JPushInterface.setAlias(AppContext.sInstance, uid + "PUSH", new TagAliasCallback() {

                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    L.e(TAG, "设置别名---->" + s);
                    hasAlians = true;
                }
            });
        }
    }

    public void stopPush() {
        JPushInterface.stopPush(AppContext.sInstance);
        hasAlians = false;
    }
}
