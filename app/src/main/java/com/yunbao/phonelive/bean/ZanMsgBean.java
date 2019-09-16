package com.yunbao.phonelive.bean;

/**
 * Created by cxf on 2018/7/21.
 */

public class ZanMsgBean {

    public static final int TYPE_COMMENT=0;
    public static final int TYPE_VIDEO=1;

    private String id;
    private String uid;
    private String obj_id;
    private int type;
    private String addtime;
    private String video_thumb;
    private String videoid;
    private String user_nicename;
    private String avatar;
    private String videouid;

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

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVideouid() {
        return videouid;
    }

    public void setVideouid(String videouid) {
        this.videouid = videouid;
    }
}
