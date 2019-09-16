package com.yunbao.phonelive.bean;

/**
 * Created by cxf on 2018/6/20.
 * 选择视频的实体类
 */

public class VideoChooseBean {

    private String videoPath;
    private String videoName;
    private String coverPath;
    private long duration;
    private String durationString;

    public VideoChooseBean() {
    }

    public VideoChooseBean(String videoPath, String videoName, String coverPath, long duration) {
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.coverPath = coverPath;
        this.duration = duration;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.durationString = castDurationString(duration);
    }

    public String getDurationString() {
        return durationString;
    }

    /**
     * 把一个long类型的总毫秒数转成时长
     */
    private static String castDurationString(long duration) {
        int hours = (int) (duration / (1000 * 60 * 60));
        int minutes = (int) ((duration % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((duration % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

}
