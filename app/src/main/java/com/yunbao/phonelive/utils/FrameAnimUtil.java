package com.yunbao.phonelive.utils;

import com.yunbao.phonelive.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2017/9/24.
 * 获取帧动画的每一帧的图片资源
 */

public class FrameAnimUtil {

    private static final List<Integer> VIDEO_ZAN_ANIM;//视频点赞动画
    private static final List<Integer> VIDEO_ZAN_CANCEL_ANIM;//视频取消点赞动画
    private static final List<Integer> VIDEO_RECORD_BTN_ANIM;//视频录制按钮动画
    private static final List<Integer> CHAT_VOICE_ANIM_LEFT;//聊天语音播放动画左
    private static final List<Integer> CHAT_VOICE_ANIM_RIGHT;//聊天语音播放动画右
    private static final List<Integer> CHAT_VOICE_ANIM_INPUT;//聊天语音输入动画

    static {
        VIDEO_ZAN_ANIM = Arrays.asList(
                R.mipmap.icon_video_zan_01,
                R.mipmap.icon_video_zan_02,
                R.mipmap.icon_video_zan_03,
                R.mipmap.icon_video_zan_04,
                R.mipmap.icon_video_zan_05,
                R.mipmap.icon_video_zan_06,
                R.mipmap.icon_video_zan_07,
                R.mipmap.icon_video_zan_08,
                R.mipmap.icon_video_zan_09,
                R.mipmap.icon_video_zan_10,
                R.mipmap.icon_video_zan_11,
                R.mipmap.icon_video_zan_12
        );
        VIDEO_ZAN_CANCEL_ANIM = Arrays.asList(
                R.mipmap.icon_video_zan_cancel_01,
                R.mipmap.icon_video_zan_cancel_02,
                R.mipmap.icon_video_zan_cancel_03,
                R.mipmap.icon_video_zan_cancel_04,
                R.mipmap.icon_video_zan_cancel_05,
                R.mipmap.icon_video_zan_cancel_06,
                R.mipmap.icon_video_zan_01
        );


        VIDEO_RECORD_BTN_ANIM = Arrays.asList(
                R.mipmap.icon_record_01,
                R.mipmap.icon_record_02,
                R.mipmap.icon_record_03,
                R.mipmap.icon_record_04,
                R.mipmap.icon_record_05,
                R.mipmap.icon_record_06,
                R.mipmap.icon_record_07,
                R.mipmap.icon_record_08,
                R.mipmap.icon_record_09,
                R.mipmap.icon_record_10,
                R.mipmap.icon_record_11,
                R.mipmap.icon_record_12,
                R.mipmap.icon_record_13,
                R.mipmap.icon_record_14
        );

        CHAT_VOICE_ANIM_LEFT = Arrays.asList(
                R.mipmap.icon_voice_left_1,
                R.mipmap.icon_voice_left_2,
                R.mipmap.icon_voice_left_3
        );

        CHAT_VOICE_ANIM_RIGHT = Arrays.asList(
                R.mipmap.icon_voice_right_1,
                R.mipmap.icon_voice_right_2,
                R.mipmap.icon_voice_right_3
        );
        CHAT_VOICE_ANIM_INPUT = Arrays.asList(
                R.mipmap.icon_voice_0,
                R.mipmap.icon_voice_1,
                R.mipmap.icon_voice_2,
                R.mipmap.icon_voice_3,
                R.mipmap.icon_voice_4,
                R.mipmap.icon_voice_5,
                R.mipmap.icon_voice_6
        );
    }

    public static List<Integer> getVideoZanAnim() {
        return VIDEO_ZAN_ANIM;
    }

    public static List<Integer> getVideoCancelZanAnim() {
        return VIDEO_ZAN_CANCEL_ANIM;
    }

    public static List<Integer> getVideoRecordBtnAnim() {
        return VIDEO_RECORD_BTN_ANIM;
    }

    public static List<Integer> getChatVoiceAnimLeft() {
        return CHAT_VOICE_ANIM_LEFT;
    }

    public static List<Integer> getChatVoiceAnimRight() {
        return CHAT_VOICE_ANIM_RIGHT;
    }

    public static List<Integer> getChatVoiceAnimInput() {
        return CHAT_VOICE_ANIM_INPUT;
    }
}
