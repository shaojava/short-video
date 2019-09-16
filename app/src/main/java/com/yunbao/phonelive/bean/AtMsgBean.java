package com.yunbao.phonelive.bean;

/**
 * Created by cxf on 2018/7/25.
 */

public class AtMsgBean {

    private String id;
    private String uid;//发起@的人的id
    private String videoid;//视频的id
    private String touid;//被@的人的id
    private String addtime;
    private String avatar;//发起@的人的头像
    private String user_nicename;//发起@的人的昵称
    private String video_title;//视频的标题
    private String video_thumb;//视频的封面
    private String videouid;//视频作者的uid

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }


    public String getVideouid() {
        return videouid;
    }

    public void setVideouid(String videouid) {
        this.videouid = videouid;
    }
}
