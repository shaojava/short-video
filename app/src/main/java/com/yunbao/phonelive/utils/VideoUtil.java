package com.yunbao.phonelive.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.bean.VideoChooseBean;
import com.yunbao.phonelive.interfaces.CommonCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class VideoUtil {

    private ContentResolver mContentResolver;
    private Handler mHandler;
    private CommonCallback<List<VideoChooseBean>> mCallback;
    private boolean mStop;

    public VideoUtil() {
        mContentResolver = AppContext.sInstance.getContentResolver();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<VideoChooseBean> videoList = (List<VideoChooseBean>) msg.obj;
                if (mCallback != null) {
                    mCallback.callback(videoList);
                }
            }
        };
    }

    public void getLocalVideoList(CommonCallback<List<VideoChooseBean>> callback) {
        if (callback == null) {
            return;
        }
        mCallback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    List<VideoChooseBean> videoList = getAllVideo();
                    Message msg = Message.obtain();
                    msg.obj = videoList;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private List<VideoChooseBean> getAllVideo() {
        List<VideoChooseBean> videoList = new ArrayList<>();
        String[] mediaColumns = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION
        };
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaColumns, null, null, null);
            if (cursor != null) {
                while (!mStop && cursor.moveToNext()) {
                    String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    File file = new File(videoPath);
                    boolean canRead = file.canRead();
                    long length = file.length();
                    if (!canRead || length == 0) {
                        continue;
                    }
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    if (duration >= 16000 || duration <= 0) {
                        continue;
                    }
                    String videoName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    if (TextUtils.isEmpty(videoName) || !videoName.endsWith(".mp4")) {
                        continue;
                    }
                    VideoChooseBean bean = new VideoChooseBean();
                    bean.setVideoPath(videoPath);
                    bean.setDuration(duration);
                    bean.setVideoName(videoName);
                    videoList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return videoList;
    }

    public void release() {
        mStop = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mCallback = null;
    }

}
