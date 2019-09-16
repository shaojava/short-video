package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2017/8/14.
 */

public class ChatUserBean extends UserBean implements Parcelable {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SYSTEM = 1;
    private String lastMessage;
    private int unReadCount;
    private String lastTime;
    private int fromType;
    private int msgType;
    private int isattent;

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.lastMessage);
        dest.writeInt(this.unReadCount);
        dest.writeString(this.lastTime);
        dest.writeInt(this.fromType);
        dest.writeInt(this.isattent);
    }

    public ChatUserBean() {

    }

    protected ChatUserBean(Parcel in) {
        super(in);
        this.lastMessage = in.readString();
        this.unReadCount = in.readInt();
        this.lastTime = in.readString();
        this.fromType = in.readInt();
        this.isattent = in.readInt();
    }

    public static final Creator<ChatUserBean> CREATOR = new Creator<ChatUserBean>() {
        @Override
        public ChatUserBean[] newArray(int size) {
            return new ChatUserBean[size];
        }

        @Override
        public ChatUserBean createFromParcel(Parcel in) {
            return new ChatUserBean(in);
        }
    };

    public ChatUserBean wrapUserBean(UserBean u) {
        if (u != null) {
            setUser_nicename(u.getUser_nicename());
            setAvatar(u.getAvatar());
            setAvatar_thumb(u.getAvatar_thumb());
            setAge(u.getAge());
            setSex(u.getSex());
            setSignature(u.getSignature());
            setCoin(u.getCoin());
            setProvince(u.getProvince());
            setCity(u.getCity());
            setArea(u.getArea());
            setBirthday(u.getBirthday());
            setFollows(u.getFollows());
            setFans(u.getFans());
            setPraise(u.getPraise());
            setWorkVideos(u.getWorkVideos());
            setLikeVideos(u.getLikeVideos());
        }
        return this;
    }


}
