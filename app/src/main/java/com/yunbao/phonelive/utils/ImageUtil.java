package com.yunbao.phonelive.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.bean.ImageChooseBean;
import com.yunbao.phonelive.interfaces.CommonCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/20.
 */

public class ImageUtil {

    private ContentResolver mContentResolver;
    private Handler mHandler;
    private CommonCallback<List<ImageChooseBean>> mCallback;
    private boolean mStop;

    public ImageUtil() {
        mContentResolver = AppContext.sInstance.getContentResolver();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<ImageChooseBean> imageList = (List<ImageChooseBean>) msg.obj;
                if (mCallback != null) {
                    mCallback.callback(imageList);
                }
            }
        };
    }

    public void getLocalImageList(CommonCallback<List<ImageChooseBean>> callback) {
        if (callback == null) {
            return;
        }
        mCallback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mHandler != null) {
                    List<ImageChooseBean> imageList = getAllImage();
                    Message msg = Message.obtain();
                    msg.obj = imageList;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private List<ImageChooseBean> getAllImage() {
        List<ImageChooseBean> imageList = new ArrayList<>();
        Cursor cursor = null;
        try {
            //只查询jpeg和png的图片
            cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            if (cursor != null) {
                while (!mStop && cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (!imagePath.contains("/DCIM/")) {
                        continue;
                    }
                    File file = new File(imagePath);
                    if (!file.exists()) {
                        continue;
                    }
                    boolean canRead = file.canRead();
                    long length = file.length();
                    if (!canRead || length == 0) {
                        continue;
                    }
                    imageList.add(new ImageChooseBean(file));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageList;
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
