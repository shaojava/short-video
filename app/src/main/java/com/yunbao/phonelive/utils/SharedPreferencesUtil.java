package com.yunbao.phonelive.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yunbao.phonelive.AppContext;

/**
 * Created by cxf on 2017/8/3.
 * 保存登录后的uid和token
 */

public class SharedPreferencesUtil {

    private SharedPreferences mSharedPreferences;

    private static SharedPreferencesUtil sInstance;
    private final String UID = "uid";
    private final String TOKEN = "token";
    private final String JIM_LOGIN = "jimLogin";
    private final String SEARCH_HISTORY = "searchHistory";
    private final String USER_BEAN = "userBean";

    private SharedPreferencesUtil() {
        mSharedPreferences = AppContext.sInstance.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferencesUtil();
                }
            }
        }
        return sInstance;
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().commit();
    }

    /**
     * 在登录成功之后返回uid和token
     *
     * @param uid
     * @param token
     */
    public void saveUidAndToken(String uid, String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(UID, uid);
        editor.putString(TOKEN, token);
        editor.commit();
    }

    /**
     * 返回保存在本地的uid和token
     *
     * @return 以字符串数组形式返回uid和token
     */
    public String[] readUidAndToken() {
        String uid = mSharedPreferences.getString(UID, "");
        if ("".equals(uid)) {
            return null;
        }
        String token = mSharedPreferences.getString(TOKEN, "");
        if ("".equals(token)) {
            return null;
        }
        return new String[]{uid, token};
    }

    /**
     * 读取私信登录状态
     */
    public boolean readEMLoginStatus() {
        return mSharedPreferences.getBoolean(JIM_LOGIN, false);
    }

    //保存私信登录状态
    public void saveEMLoginStatus(boolean login) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(JIM_LOGIN, login);
        editor.commit();
    }


    //保存搜索记录
    public void saveSearchHistory(String searchHistory) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SEARCH_HISTORY, searchHistory);
        editor.commit();
    }

    //读取搜索记录
    public String readSearchHistory() {
        return mSharedPreferences.getString(SEARCH_HISTORY, "");
    }

    //保存用户信息
    public void saveUserBeanJson(String userBeanJsonString) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(USER_BEAN, userBeanJsonString);
        editor.commit();
    }

    //读取用户信息
    public String readUserBeanJson() {
        return mSharedPreferences.getString(USER_BEAN, "");
    }
}
