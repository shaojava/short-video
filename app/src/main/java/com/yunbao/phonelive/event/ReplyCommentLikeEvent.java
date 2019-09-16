package com.yunbao.phonelive.event;

/**
 * Created by cxf on 2018/6/13.
 */

public class ReplyCommentLikeEvent {
    private String mCommentId;
    private int mIsLike;
    private String mLikes;

    public ReplyCommentLikeEvent(String commentId, int isLike, String likes) {
        mCommentId = commentId;
        mIsLike = isLike;
        mLikes = likes;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public String getLikes() {
        return mLikes;
    }


    public int getIsLike() {
        return mIsLike;
    }

}
