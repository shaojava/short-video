package com.yunbao.phonelive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;

/**
 * Created by cxf on 2017/9/4.
 */

public class DownloadUtil {

    public void download(String tag, String fileDir, String fileName, String url, final Callback callback) {
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        OkGo.<File>get(url).tag(tag).execute(new FileCallback(fileDir, fileName) {
            @Override
            public void onSuccess(Response<File> response) {
                //下载成功结束后的回调
                if (callback != null) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void downloadProgress(Progress progress) {
                if (callback != null) {
                    int val = (int) (progress.currentSize * 100 / progress.totalSize);
                    L.e("下载进度--->" + val);
                    callback.onProgress(val);
                }
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                Throwable e = response.getException();
                L.e("下载失败--->" + e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }


    public interface Callback {
        void onSuccess(File file);

        void onProgress(int progress);

        void onError(Throwable e);
    }


    /**
     * 使用ContentProvider保存视频信息
     */
    public static void saveVideoInfo(Context context, String path, long duration) {
        try {
            File videoFile = new File(path);
            String fileName = videoFile.getName();
            long currentTimeMillis = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATA, path);
            values.put(MediaStore.MediaColumns.SIZE, videoFile.length());
            values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, currentTimeMillis);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.VideoColumns.DURATION, duration);
            context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用ContentProvider保存视频信息
     */
    public static void saveVideoInfo(Context context, String path) {
        try {
            File videoFile = new File(path);
            String fileName = videoFile.getName();
            long currentTimeMillis = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeMillis);
            values.put(MediaStore.MediaColumns.DATA, path);
            values.put(MediaStore.MediaColumns.SIZE, videoFile.length());
            values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, currentTimeMillis);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path); //在获取前，设置文件路径（应该只能是本地路径）
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release(); //释放
            if (!TextUtils.isEmpty(durationStr)) {
                long duration = Integer.valueOf(durationStr);
                values.put(MediaStore.Video.VideoColumns.DURATION, duration);
            }
            context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
