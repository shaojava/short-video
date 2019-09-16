package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/6/13.
 */

public class ReplyCommentEvent {
    private String mCommentId;
    private int mReplys;
    private String mComments;

    public ReplyCommentEvent(String commentId, String comments, int replys) {
        mCommentId = commentId;
        mComments = comments;
        mReplys = replys;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public int getReplys() {
        return mReplys;
    }

    public String getComments() {
        return mComments;
    }

}
