package com.yunbao.phonelive.upload;

import java.io.File;

/**
 * Created by cxf on 2018/5/21.
 */

public class VideoUploadBean {
    private String videoPath;
    private String imgPath;
    private File videoFile;
    private File imgFile;
    private String videoName;
    private String imgName;

    public VideoUploadBean(String videoPath, String imgPath) {
        this.videoPath = videoPath;
        this.imgPath = imgPath;
        this.videoFile = new File(videoPath);
        this.imgFile = new File(imgPath);
        this.videoName = this.videoFile.getName();
        this.imgName = this.imgFile.getName();
    }

    public String getVideoPath() {
        return videoPath;
    }


    public String getImgPath() {
        return imgPath;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public File getImgFile() {
        return imgFile;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
