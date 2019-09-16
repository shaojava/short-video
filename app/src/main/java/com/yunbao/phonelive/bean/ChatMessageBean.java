package com.yunbao.phonelive.bean;

import cn.jpush.im.android.api.model.Message;

/**
 * Created by cxf on 2018/7/12.
 */

public class ChatMessageBean {

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VOICE = 3;
    public static final int TYPE_LOCATION = 4;

    private String from;
    private Message rawMessage;
    private int type;
    private boolean fromSelf;
    private long createTime;
    private String mImageFilePath;
    private boolean mPlayVoice;

    public ChatMessageBean(String from, Message rawMessage, int type, boolean fromSelf) {
        this.from = from;
        this.rawMessage = rawMessage;
        this.type = type;
        this.fromSelf = fromSelf;
        this.createTime = rawMessage.getCreateTime();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Message getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(Message rawMessage) {
        this.rawMessage = rawMessage;
        this.createTime = rawMessage.getCreateTime();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFromSelf() {
        return fromSelf;
    }

    public void setFromSelf(boolean fromSelf) {
        this.fromSelf = fromSelf;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getImageFilePath() {
        return mImageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        mImageFilePath = imageFilePath;
    }

    public boolean isPlayVoice() {
        return mPlayVoice;
    }

    public void setPlayVoice(boolean playVoice) {
        mPlayVoice = playVoice;
    }
}
