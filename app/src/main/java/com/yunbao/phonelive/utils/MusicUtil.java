package com.yunbao.phonelive.utils;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicBean;

import java.io.File;

/**
 * Created by cxf on 2018/6/27.
 */

public class MusicUtil {

    private static MusicUtil sInstance;

    private MusicUtil() {

    }

    public static MusicUtil getInstance() {
        if (sInstance == null) {
            synchronized (MusicUtil.class) {
                if (sInstance == null) {
                    sInstance = new MusicUtil();
                }
            }
        }
        return sInstance;
    }

    public String getMusicPath(int musicId) {
        String path = AppConfig.VIDEO_MUSIC_PATH + Constants.VIDEO_MUSIC_NAME_PREFIX + musicId;
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        return null;
    }


    public void downloadMusic(MusicBean bean, final MusicDownLoadCallback callback) {
        DownloadUtil downloadUtil = new DownloadUtil();
        String url = bean.getFile_url();
        downloadUtil.download(url,
                AppConfig.VIDEO_MUSIC_PATH,
                Constants.VIDEO_MUSIC_NAME_PREFIX + bean.getId(),
                url, new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        ToastUtil.show(WordUtil.getString(R.string.download_success));
                        if (callback != null) {
                            callback.onDownloadSuccess(file.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onProgress(int progress) {
                        if(callback!=null){
                            callback.onProgress(progress);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show(WordUtil.getString(R.string.download_fail));
                    }
                });
    }

    public interface MusicDownLoadCallback {
        void onDownloadSuccess(String filePath);

        void onProgress(int progress);
    }
}
