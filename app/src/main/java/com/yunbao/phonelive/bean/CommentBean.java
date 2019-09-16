package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by cxf on 2017/7/14.
 */

public class CommentBean implements Parcelable {


    private String id;
    private String uid;
    private String touid;
    private String videoid;
    private String commentid;
    private String parentid;
    private String content;
    private String addtime;
    private String at_info;
    private UserBean userinfo;
    private String likes;
    private int replys;
    private String datetime;
    private UserBean touserinfo;
    private ToCommentInfo tocommentinfo;
    private int islike;


    public CommentBean() {

    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public ToCommentInfo getTocommentinfo() {
        return tocommentinfo;
    }

    public void setTocommentinfo(ToCommentInfo tocommentinfo) {
        this.tocommentinfo = tocommentinfo;
    }

    public int getReplys() {
        return replys;
    }

    public void setReplys(int replys) {
        this.replys = replys;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

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

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public UserBean getTouserinfo() {
        return touserinfo;
    }

    public void setTouserinfo(UserBean touserinfo) {
        this.touserinfo = touserinfo;
    }


    public String getAt_info() {
        return at_info;
    }

    public void setAt_info(String at_info) {
        this.at_info = at_info;
    }

    public static class ToCommentInfo implements Parcelable {
        public ToCommentInfo() {

        }

        private String content;
        private String at_info;

        protected ToCommentInfo(Parcel in) {
            content = in.readString();
            at_info = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(content);
            dest.writeString(at_info);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ToCommentInfo> CREATOR = new Creator<ToCommentInfo>() {
            @Override
            public ToCommentInfo createFromParcel(Parcel in) {
                return new ToCommentInfo(in);
            }

            @Override
            public ToCommentInfo[] newArray(int size) {
                return new ToCommentInfo[size];
            }
        };

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAt_info() {
            return at_info;
        }

        public void setAt_info(String at_info) {
            this.at_info = at_info;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.touid);
        dest.writeString(this.videoid);
        dest.writeString(this.commentid);
        dest.writeString(this.parentid);
        dest.writeString(this.content);
        dest.writeString(this.addtime);
        dest.writeParcelable(this.userinfo, flags);
        dest.writeString(this.likes);
        dest.writeInt(this.replys);
        dest.writeString(this.datetime);
        dest.writeParcelable(this.touserinfo, flags);
        dest.writeParcelable(this.tocommentinfo, flags);
        dest.writeInt(this.islike);
        dest.writeString(this.at_info);
    }


    protected CommentBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.touid = in.readString();
        this.videoid = in.readString();
        this.commentid = in.readString();
        this.parentid = in.readString();
        this.content = in.readString();
        this.addtime = in.readString();
        this.userinfo = in.readParcelable(UserBean.class.getClassLoader());
        this.likes = in.readString();
        this.replys = in.readInt();
        this.datetime = in.readString();
        this.touserinfo = in.readParcelable(UserBean.class.getClassLoader());
        this.tocommentinfo = in.readParcelable(ToCommentInfo.class.getClassLoader());
        this.islike = in.readInt();
        this.at_info = in.readString();
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel source) {
            return new CommentBean(source);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };
}
