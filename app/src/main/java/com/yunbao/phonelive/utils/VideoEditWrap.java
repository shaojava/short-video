package com.yunbao.phonelive.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.ugc.TXVideoEditer;
import com.yunbao.phonelive.AppConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cxf on 2018/6/25.
 */

public class VideoEditWrap {

    private Random mRandom;
    private List<Bitmap> mList;
    private TXVideoEditer mTXVideoEditer;

    private static VideoEditWrap sInstance;

    private VideoEditWrap() {
        mRandom = new Random();
        mList = new ArrayList<>();
    }

    public static VideoEditWrap getInstance() {
        if (sInstance == null) {
            synchronized (VideoEditWrap.class) {
                if (sInstance == null) {
                    sInstance = new VideoEditWrap();
                }
            }
        }
        return sInstance;
    }

    public void addVideoBitmap(Bitmap bitmap) {
        if (mList != null) {
            mList.add(bitmap);
        }
    }

    public List<Bitmap> getList() {
        return mList;
    }


    public void release() {
        if (mTXVideoEditer != null) {
            mTXVideoEditer.setVideoProcessListener(null);
            mTXVideoEditer.setThumbnailListener(null);
            mTXVideoEditer.setTXVideoReverseListener(null);
            mTXVideoEditer.setTXVideoPreviewListener(null);
            mTXVideoEditer.setVideoGenerateListener(null);
            mTXVideoEditer.release();
            mTXVideoEditer = null;
        }
        clearBitmapList();
    }

    public void clearBitmapList() {
        if (mList != null) {
            for (Bitmap bitmap : mList) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            mList.clear();
        }
    }

    /**
     * 设置视频输出路径
     */
    public String generateVideoOutputPath() {
        String outputDir = AppConfig.VIDEO_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String videoName = DateFormatUtil.getCurTimeString() + mRandom.nextInt(9999);
        return outputDir + "android_" + videoName + ".mp4";
    }

    public TXVideoEditer createVideoEditer(Context context, String videoPath) {
        mTXVideoEditer = new TXVideoEditer(context);
        mTXVideoEditer.setVideoPath(videoPath);
        return mTXVideoEditer;
    }

    public TXVideoEditer getTXVideoEditer() {
        return mTXVideoEditer;
    }

}
