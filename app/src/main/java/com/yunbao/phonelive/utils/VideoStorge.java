package com.yunbao.phonelive.utils;

import com.yunbao.phonelive.bean.VideoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/6/9.
 */

public class VideoStorge {

    private static VideoStorge sInstance;
    private Map<String, List<VideoBean>> mMap;

    private VideoStorge() {
        mMap = new HashMap<>();
    }

    public static VideoStorge getInstance() {
        if (sInstance == null) {
            synchronized (VideoStorge.class) {
                if (sInstance == null) {
                    sInstance = new VideoStorge();
                }
            }
        }
        return sInstance;
    }

    public void put(String key, List<VideoBean> list) {
        if (mMap != null) {
            mMap.put(key, list);
        }
    }


    public List<VideoBean> get(String key) {
        if (mMap != null) {
            return mMap.get(key);
        }
        return null;
    }

    public void remove(String key) {
        if (mMap != null) {
            mMap.remove(key);
        }
    }


    public void clear() {
        if (mMap != null) {
            mMap.clear();
        }
    }

}
