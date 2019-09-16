package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/6/14.
 */

public class FollowEvent {

    private String touid;
    private int isAttention;

    public FollowEvent(String touid, int isAttention) {
        this.touid = touid;
        this.isAttention = isAttention;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public String getTouid() {
        return touid;
    }

}
