package com.yunbao.phonelive;

import android.util.SparseArray;

public class Constants {
    public static final String VIDEO_POSITION = "videoPosition";
    public static final String VIDEO_PAGE = "videoPage";
    public static final String VIDEO_KEY = "videoKey";
    public static final String VIDEO_FOLLOW = "videoFollow";
    public static final String VIDEO_HOT = "videoHot";
    public static final String VIDEO_SEARCH = "videoSearch";
    public static final String VIDEO_NEAR = "videoNear";
    public static final String VIDEO_BEAN = "videoBean";
    public static final String UID = "uid";
    public static final String NOT_LOGIN_UID = "-1";
    public static final String USER_BEAN = "userBean";
    public static final String IS_MAIN_USER_CENTER = "isMainUserCenter";
    public static final String TYPE_QQ = "qq";
    public static final String TYPE_QZONE = "qzone";
    public static final String TYPE_WX = "wx";
    public static final String TYPE_WX_PYQ = "wchat";
    public static final String TYPE_FACEBOOK = "facebook";
    public static final String TYPE_TWITTER = "twitter";
    public static final String COMMENT_BEAN = "commentBean";
    public static final String VIDEO_ID = "videoId";
    public static final String URL = "url";
    public static final String IS_ATTENTION = "isAttention";
    public static final String GENDER_MALE = "男";
    public static final String GENDER_FAMALE = "女";
    public static final String UPDATE_FIELDS = "updateFields";
    public static final String USER_NICE_NAME = "user_nicename";
    public static final String BIRTHDAY = "birthday";
    public static final String SEX = "sex";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String AREA = "area";
    public static final String SINGATURE = "signature";
    public static final String TO_UID = "toUid";
    public static final String MUSIC_BEAN = "musicBean";
    public static final String VIDEO_PATH = "videoPath";
    public static final String VIDEO_COVER_PATH = "videoCoverPath";
    public static final String VIDEO_DURATION = "videoDuration";
    public static final String VIDEO_PROCESS_DES = "videoProcess";
    public static final String VIDEO_MUSIC_NAME_PREFIX = "musicName_";
    public static final String VIDEO_MUSIC_ID = "musicId";
    public static final String SELECT_IMAGE_PATH = "selectedImagePath";
    public static final String FROM = "from";
    public static final String YB_ID_1 = "dsp_admin_1";
    public static final String YB_ID_2 = "dsp_admin_2";
    public static final String YB_NAME_1 = "云豹官方";
    public static final String YB_NAME_2 = "系统通知";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ADDRESS = "address";
    public static final String SCALE = "scale";
    public static final String SINGLE_VIDEO = "singleVideo";
    public static final String FULL_SCREEN = "fullScreen";
    public static final String SHOW_PRI_MSG = "showPrivateMsg";
    public static final String SAVE_TYPE = "saveType";
    public static final int REQUEST_FILE_PERMISSION = 100;
    public static final int REQUEST_VIDEO_PERMISSION = 101;
    public static final int REQUEST_CAMERA_PERMISSION = 102;
    public static final int REQUEST_LOCATION_PERMISSION = 103;
    public static final int REQUEST_AUDIO_PERMISSION = 104;
    public static final int REQUEST_CALL_PERMISSION = 105;
    public static final int REQUEST_ASR_PERMISSION = 106;
    public static final int VIDEO_CHOOSE_CODE = 200;
    public static final int VIDEO_FROM_RECORD = 201;
    public static final int VIDEO_FROM_CHOOSE = 202;
    public static final int VIDEO_FROM_EDIT = 203;
    public static final int AT_FRIENDS_CODE = 1001;
    public static final int SAVE_TYPE_ALL = 601;//保存并发布
    public static final int SAVE_TYPE_SAVE = 602;//仅保存
    public static final int SAVE_TYPE_PUB = 603;//仅发布

    public static final SparseArray<String> GENDER_MAP;


    static {
        GENDER_MAP = new SparseArray<>();
        GENDER_MAP.put(1, GENDER_MALE);
        GENDER_MAP.put(2, GENDER_FAMALE);
    }


}
