package com.yunbao.phonelive.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.TxLocationBean;
import com.yunbao.phonelive.bean.TxLocationPoiBean;
import com.yunbao.phonelive.event.FollowEvent;
import com.yunbao.phonelive.event.NeedRefreshEvent;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.MD5Util;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class HttpUtil {

    private static final String HTTP_URL = AppConfig.HOST + "/api/public/?";
    private static OkHttpClient sOkHttpClient;
    private static final String SALT = "#2hgfk85cm23mk58vncsark";

    public static void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(500, TimeUnit.MILLISECONDS);
        builder.readTimeout(500, TimeUnit.MILLISECONDS);
        builder.writeTimeout(500, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(false);
        //输出HTTP请求 响应信息
        HttpInterceptor interceptor = new HttpInterceptor(new HttpInterceptor.Logger() {

            @Override
            public void log(String message) {
                L.e("http", message);
            }
        });
        interceptor.setLevel(HttpInterceptor.Level.BASIC);
        builder.addInterceptor(interceptor);
        sOkHttpClient = builder.build();

        OkGo.getInstance().init(AppContext.sInstance)
                .setOkHttpClient(sOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(0);
    }

    public static void cancel(String tag) {
        OkGo.cancelTag(sOkHttpClient, tag);
    }

    public static final String IF_TOKEN = "ifToken";
    public static final String GET_CONFIG = "getConfig";
    public static final String GET_LOGIN_CODE = "getLoginCode";
    public static final String LOGIN = "login";
    public static final String GET_VIDEO_LIST = "getVideoList";
    public static final String GET_RECOMMEND_VIDEOS = "getRecommendVideos";
    public static final String GET_NEARBY_VIDEOS = "getNearbyVideos";
    public static final String LOGIN_BY_THIRD = "loginByThird";
    public static final String GET_ATTENTION_VIDEO = "getAttentionVideo";
    public static final String GET_HOME_VIDEO = "getHomeVideo";
    public static final String GET_LIKE_VIDEOS = "getLikeVideos";
    public static final String GET_VIDEO_INFO = "getVideoInfo";
    public static final String GET_USER_HOME = "getUserHome";
    public static final String GET_USER_SEARCH = "getUserSearch";
    public static final String SET_VIDEO_LIKE = "setVideoLike";
    public static final String SET_VIDEO_SHARE = "setVideoShare";
    public static final String GET_COMMENTS = "getComments";
    public static final String GET_REPLYS = "getReplys";
    public static final String SET_COMMENT = "setComment";
    public static final String SET_COMMENT_LIKE = "setCommentLike";
    public static final String GET_REPORT_LIST = "getReportList";
    public static final String REPORT_VIDEO = "reportVideo";
    public static final String GET_BASE_INFO = "getBaseInfo";
    public static final String UPDATE_FIELDS = "updateFields";
    public static final String UPDATE_AVATAR = "updateAvatar";
    public static final String GET_FOLLOWS_LIST = "getFollowsList";
    public static final String GET_FANS_LIST = "getFansList";
    public static final String GET_MUSIC_LIST = "getMusicList";
    public static final String SEARCH_MUSIC = "searchMusic";
    public static final String CHECK_BLACK = "checkBlack";
    public static final String GET_MULTI_INFO = "getMultiInfo";
    public static final String GET_LOCAITON = "getLocationByTxSdk";
    public static final String GET_MAP_INFO = "getMapInfoByTxSdk";
    public static final String GET_MAP_SEARCH = "searchInfoByTxSdk";
    public static final String GET_FANS_MESSAGES = "getFansMessages";
    public static final String GET_ZAN_MESSAGES = "getZanMessages";
    public static final String GET_COMMENT_MESSAGES = "getCommentMessages";
    public static final String GET_AT_MESSAGES = "getAtMessages";
    public static final String GET_MUSIC_CLASS_LIST = "getMusicClassList";
    public static final String GET_HOT_MUSIC_LIST = "getHotMusicList";
    public static final String SET_MUSIC_COLLECT = "setMusicCollect";
    public static final String GET_MUSIC_COLLECT_LIST = "getMusicCollectList";
    public static final String GET_ADMIN_MSG_LIST = "getAdminMsgList";
    public static final String GET_SYSTEM_MSG_LIST = "getSystemMsgList";
    public static final String GET_VIDEO_SEARCH = "getVideoSearch";
    public static final String SET_BLACK = "setBlack";
    public static final String DELETE_VIDEO = "deleteVideo";
    public static final String GET_BLACK_LIST = "getBlackList";
    public static final String START_WATCH_VIDEO = "startWatchVideo";
    public static final String END_WATCH_VIDEO = "endWatchVideo";
    public static final String GET_LAST_MESSAGE = "getLastMessage";


    /**
     * 验证token是否过期
     */
    public static void ifToken(CheckTokenCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.iftoken")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(IF_TOKEN)
                .execute(callback);
    }

    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Home.getConfig")
                .tag(GET_CONFIG)
                .execute(new HttpCallback() {

                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ConfigBean bean = JSON.parseObject(info[0], ConfigBean.class);
                            AppConfig.getInstance().setConfig(bean);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        }
                    }
                });
    }


    /**
     * 获取登录验证码接口
     */
    public static void getLoginCode(String mobile, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Login.getLoginCode")
                .params("mobile", mobile)
                .tag(GET_LOGIN_CODE)
                .execute(callback);
    }


    /**
     * 手机号 验证码登录
     */
    public static void login(String phoneNum, String code, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Login.userLogin")
                .params("user_login", phoneNum)
                .params("code", code)
                .tag(LOGIN)
                .execute(callback);
    }

    /**
     * 第三方登录
     */
    public static void loginByThird(String openid, String nicename, String type, String avatar, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Login.userLoginByThird")
                .params("openid", openid)
                .params("nicename", nicename)
                .params("type", type)
                .params("avatar", avatar)
                .tag(LOGIN_BY_THIRD)
                .execute(callback);
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(AppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        OkGo.<JsonBean>get(HTTP_URL + "service=User.setAttent")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new FollowEvent(touid, isAttention));
                            EventBus.getDefault().post(new NeedRefreshEvent());
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                        }
                    }
                });
    }


    /**
     * 获取热门视频列表
     */
    public static void getVideoList(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getVideoList")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_VIDEO_LIST)
                .execute(callback);
    }

    /**
     * 获取推荐视频列表
     */
    public static void getRecommendVideos(int p,int isStart, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getRecommendVideos")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .params("isstart", isStart)
                .tag(GET_RECOMMEND_VIDEOS)
                .execute(callback);
    }

    /**
     * 获取附近视频列表
     */
    public static void getNearbyVideos(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getNearby")
                .params("uid", AppConfig.getInstance().getUid())
                .params("lng", AppConfig.getInstance().getLng())
                .params("lat", AppConfig.getInstance().getLat())
                .params("p", p)
                .tag(GET_NEARBY_VIDEOS)
                .execute(callback);
    }

    /**
     * 获取关注视频列表
     */
    public static void getAttentionVideo(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getAttentionVideo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_ATTENTION_VIDEO)
                .execute(callback);
    }

    public static final String GET_OUT_VIDEO_URL = "getOutVideoUrl";
    /**
     * 获取外链视频
     * @param paramString
     */
    public static void getOutVideoUrl(String paramString, HttpCallback callback)
    {
        OkGo.<JsonBean>get(HTTP_URL+"service=Video.getOutVideoUrl")
                .headers("Connection", "close")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("url", paramString)
                .tag(GET_OUT_VIDEO_URL).execute(callback);
    }

    /**
     * 获取某人发布的视频
     *
     * @param touid 对方的id
     */
    public static void getHomeVideo(String touid, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getHomeVideo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .tag(GET_HOME_VIDEO)
                .execute(callback);
    }

    /**
     * 获取某人喜欢的视频
     *
     * @param touid 对方的id
     */
    public static void getLikeVideos(String touid, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getLikeVideos")
                .params("uid", touid)
                .params("p", p)
                .tag(GET_LIKE_VIDEOS)
                .execute(callback);
    }

    /**
     * 获取单个视频信息，主要是该视频关于自己的信息 ，如是否关注，是否点赞等
     *
     * @param videoid 视频的id
     */
    public static void getVideoInfo(String videoid, String tag, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getVideo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("videoid", videoid)
                .tag(tag)
                .execute(callback);
    }

    /**
     * 获取某个用户的个人信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getUserHome")
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .tag(GET_USER_HOME)
                .execute(callback);
    }


    /**
     * 搜索用户
     */
    public static void getUserSearch(String key, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Home.search")
                .params("uid", AppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .tag(GET_USER_SEARCH)
                .execute(callback);
    }

    /**
     * 搜索视频
     */
    public static void getVideoSearch(String key, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Home.videoSearch")
                .params("uid", AppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .tag(GET_VIDEO_SEARCH)
                .execute(callback);
    }


    /**
     * 点赞视频
     */
    public static void setVideoLike(String videoid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.addLike")
                .params("uid", AppConfig.getInstance().getUid())
                .params("videoid", videoid)
                .tag(SET_VIDEO_LIKE)
                .execute(callback);
    }

    /**
     * 分享视频
     */
    public static void setVideoShare(String videoid, HttpCallback callback) {
        String uid = AppConfig.getInstance().getUid();
        String s = MD5Util.getMD5(uid + "-" + videoid + "-" + SALT);
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.addShare")
                .params("uid", uid)
                .params("videoid", videoid)
                .params("random_str", s)
                .tag(SET_VIDEO_SHARE)
                .execute(callback);
    }


    /**
     * 获取评论
     */
    public static void getComments(String videoid, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getComments")
                .params("videoid", videoid)
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_COMMENTS)
                .execute(callback);
    }

    /**
     * 获取评论回复
     */
    public static void getReplys(String commentid, String p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getReplys")
                .params("commentid", commentid)
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_REPLYS)
                .execute(callback);
    }

    /**
     * 发表评论  回复
     *
     * @param touid
     * @param videoid
     * @param content
     * @param commentid
     * @param parentid
     * @param callback
     */
    public static void setComment(String touid, String videoid, String content, String commentid, String parentid, String at_info, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.setComment")
                .params("videoid", videoid)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("content", content)
                .params("commentid", commentid)
                .params("parentid", parentid)
                .params("at_info", at_info)
                .tag(SET_COMMENT)
                .execute(callback);
    }

    /**
     * 评论点赞
     */

    public static void setCommentLike(String commentid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.addCommentLike")
                .params("commentid", commentid)
                .params("uid", AppConfig.getInstance().getUid())
                .tag(SET_COMMENT_LIKE)
                .execute(callback);
    }

    //获取举报内容列表接口
    public static void getReportList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Dialect.getReportContentlist")
                .tag(GET_REPORT_LIST)
                .execute(callback);
    }

    //举报视频接口
    public static void reportVideo(String videoId, String content, int type, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Dialect.report")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("dialectid", videoId)
                .params("content", content)
                .params("type", type)
                .tag(REPORT_VIDEO)
                .execute(callback);
    }

    /**
     * 个人页面获取用户自己的信息
     */
    public static void getBaseInfo(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getBaseInfo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .tag(GET_BASE_INFO)
                .execute(callback);
    }

    /**
     * 更新用户资料
     *
     * @param fields 用户资料 ,以json形式出现
     */
    public static void updateFields(String fields, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.updateFields")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("fields", fields)
                .tag(UPDATE_FIELDS)
                .execute(callback);
    }

    /**
     * 上传头像，用post
     */
    public static void updateAvatar(File file, HttpCallback callback) {
        OkGo.<JsonBean>post(HTTP_URL + "service=User.updateAvatar")
                .isMultipart(true)
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("file", file)
                .tag(UPDATE_AVATAR)
                .execute(callback);
    }


    /**
     * 获取腾讯云单次签名
     */
    public static void getCreateNonreusableSignature(String imgname, String videoname, String folderimg,String foldervideo,HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=video.getCreateNonreusableSignature")
                .params("imgname", imgname)
                .params("videoname", videoname)
                .params("folderimg", folderimg)
                .params("foldervideo", foldervideo)
                .execute(callback);
    }

    //获取七牛云token的接口
    public static void getQiniuToken(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.getQiniuToken")
                .execute(callback);
    }


    //短视频上传信息
    public static void uploadVideo(String title, String thumb, String href, int musicId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.setVideo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("lat", AppConfig.getInstance().getLat())
                .params("lng", AppConfig.getInstance().getLng())
                .params("city", AppConfig.getInstance().getCity())
                .params("title", title)
                .params("thumb", thumb)
                .params("href", href)
                .params("music_id", musicId)
                .execute(callback);
    }

    public static void uploadVideo(String title, String thumb, String href, int musicId, int ispass, String type, String videoid, HttpCallback paramHttpCallback)
    {
        OkGo.<JsonBean>get(HTTP_URL+"service=Video.setVideo")
                .headers("Connection", "close")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("lat", AppConfig.getInstance().getLat())
                .params("lng", AppConfig.getInstance().getLng())
                .params("city", AppConfig.getInstance().getCity())
                .params("title", title)
                .params("thumb", thumb)
                .params("href", href)
                .params("music_id", musicId)
                .params("ispass", ispass)
                .params("type", type)
                .params("videoid", videoid)
                .execute(paramHttpCallback);
    }

    /**
     * 获取对方的关注列表
     *
     * @param touid 对方的uid
     */
    public static void getFollowsList(String touid, int p, String key, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getFollowsList")
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("key", key)
                .params("p", p)
                .tag(GET_FOLLOWS_LIST)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     *
     * @param touid 对方的uid
     */
    public static void getFansList(String touid, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getFansList")
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", touid)
                .params("p", p)
                .tag(GET_FANS_LIST)
                .execute(callback);
    }


    /**
     * 判断自己有没有被对方拉黑，聊天的时候用到
     */
    public static void checkBlack(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.checkBlack")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .tag(CHECK_BLACK)
                .execute(callback);
    }

    /**
     * 获取极光聊天列表用户的信息 uids是多个用户的id,以逗号分隔
     */
    public static void getMultiInfo(String uids, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getMultiInfo")
                .params("uid", AppConfig.getInstance().getUid())
                .params("uids", uids)
                .tag(GET_MULTI_INFO)
                .execute(callback);
    }

    /**
     * 使用腾讯定位sdk获取 位置信息
     *
     * @param lng 经度
     * @param lat 纬度
     * @param poi 是否要查询POI
     */
    public static void getAddressInfoByTxLocaitonSdk(final double lng, final double lat, final int poi, int pageIndex, String tag, final CommonCallback<TxLocationBean> commonCallback) {
        OkGo.<String>get("https://apis.map.qq.com/ws/geocoder/v1/")
                .params("location", lat + "," + lng)
                .params("get_poi", poi)
                .params("poi_options", "address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5")
                .params("key", AppConfig.getInstance().getTxLocationKey())
                .tag(tag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj.getIntValue("status") == 0) {
                            JSONObject result = obj.getJSONObject("result");
                            if (result != null) {
                                TxLocationBean bean = new TxLocationBean();
                                bean.setLng(lng);
                                bean.setLat(lat);
                                bean.setAddress(result.getString("address"));
                                JSONObject addressComponent = result.getJSONObject("address_component");
                                if (addressComponent != null) {
                                    bean.setNation(addressComponent.getString("nation"));
                                    bean.setProvince(addressComponent.getString("province"));
                                    bean.setCity(addressComponent.getString("city"));
                                    bean.setDistrict(addressComponent.getString("district"));
                                    bean.setStreet(addressComponent.getString("street"));
                                }
                                if (poi == 1) {
                                    List<TxLocationPoiBean> poiList = JSON.parseArray(result.getString("pois"), TxLocationPoiBean.class);
                                    bean.setPoiList(poiList);
                                }
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            }
                        }
                    }
                });
    }


    /**
     * 使用腾讯地图API进行搜索
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void searchAddressInfoByTxLocaitonSdk(final double lng, final double lat, String keyword, int pageIndex, final CommonCallback<List<TxLocationPoiBean>> commonCallback) {
        OkGo.<String>get("https://apis.map.qq.com/ws/place/v1/search?")
                .params("keyword", keyword)
                .params("boundary", "nearby(" + lat + "," + lng + ",1000)&orderby=_distance&page_size=20&page_index=" + pageIndex)
                .params("key", AppConfig.getInstance().getTxLocationKey())
                .tag(GET_MAP_SEARCH)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj.getIntValue("status") == 0) {
                            List<TxLocationPoiBean> poiList = JSON.parseArray(obj.getString("data"), TxLocationPoiBean.class);
                            if (commonCallback != null) {
                                commonCallback.callback(poiList);
                            }
                        }
                    }
                });
    }


    /**
     * 获取粉丝消息，即别人关注了我，会收到这个消息
     */
    public static void getFansMessages(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.fansLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_FANS_MESSAGES)
                .execute(callback);
    }

    /**
     * 获取点赞消息，即别人给我的视频或评论点了赞，会收到这个消息
     */
    public static void getZanMessages(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.praiseLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_ZAN_MESSAGES)
                .execute(callback);
    }


    /**
     * 获取评论消息，即别人给我的视频进行评论，会收到这个消息
     */
    public static void getCommentMessages(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.commentLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_COMMENT_MESSAGES)
                .execute(callback);
    }

    /**
     * 获取@消息，即别人@我，会收到这个消息
     */
    public static void getAtMessages(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.atLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_AT_MESSAGES)
                .execute(callback);
    }

    /**
     * 获取热门音乐列表
     */
    public static void getHotMusicList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.hotLists")
                .params("uid", AppConfig.getInstance().getUid())
                .tag(GET_HOT_MUSIC_LIST)
                .execute(callback);
    }

    /**
     * 获取普通音乐列表
     */
    public static void getMusicList(String classify, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.music_list")
                .params("uid", AppConfig.getInstance().getUid())
                .params("classify", classify)
                .params("p", p)
                .tag(GET_MUSIC_LIST)
                .execute(callback);
    }


    /**
     * 搜索音乐
     */
    public static void searchMusic(String key, int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.searchMusic")
                .params("uid", AppConfig.getInstance().getUid())
                .params("key", key)
                .params("p", p)
                .tag(SEARCH_MUSIC)
                .execute(callback);
    }

    /**
     * 获取音乐分类列表
     */
    public static void getMusicClassList(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.classify_list")
                .tag(GET_MUSIC_CLASS_LIST)
                .execute(callback);
    }

    /**
     * 音乐收藏
     */
    public static void setMusicCollect(String muiscId, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.collectMusic")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("musicid", muiscId)
                .tag(SET_MUSIC_COLLECT)
                .execute(callback);
    }

    /**
     * 音乐收藏列表
     */
    public static void getMusicCollectList(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Music.getCollectMusicLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_MUSIC_COLLECT_LIST)
                .execute(callback);
    }

    /**
     * 官方消息列表
     */
    public static void getAdminMsgList(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.officialLists")
                .params("p", p)
                .tag(GET_ADMIN_MSG_LIST)
                .execute(callback);
    }


    /**
     * 系统通知消息列表
     */
    public static void getSystemMsgList(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.systemnotifyLists")
                .params("uid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_SYSTEM_MSG_LIST)
                .execute(callback);
    }

    /**
     * 拉黑对方， 解除拉黑
     */
    public static void setBlack(String touid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.setBlack")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("touid", touid)
                .tag(SET_BLACK)
                .execute(callback);
    }

    /**
     * 删除自己的视频
     */
    public static void deleteVideo(String videoid, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.del")
                .params("uid", AppConfig.getInstance().getUid())
                .params("token", AppConfig.getInstance().getToken())
                .params("videoid", videoid)
                .tag(DELETE_VIDEO)
                .execute(callback);
    }

    /**
     * 获取拉黑列表
     */
    public static void getBlackList(int p, HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=User.getBlackList")
                .params("uid", AppConfig.getInstance().getUid())
                .params("touid", AppConfig.getInstance().getUid())
                .params("p", p)
                .tag(GET_BLACK_LIST)
                .execute(callback);
    }

    /**
     * 开始观看视频的时候请求这个接口
     */
    public static void startWatchVideo(String videoid) {
        String uid = AppConfig.getInstance().getUid();
        String s = MD5Util.getMD5(uid + "-" + videoid + "-" + SALT);
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.addView")
                .params("uid", uid)
                .params("videoid", videoid)
                .params("random_str", s)
                .tag(START_WATCH_VIDEO)
                .execute(NO_CALLBACK);
    }

    /**
     * 完整观看完视频后请求这个接口
     */
    public static void endWatchVideo(String videoid) {
        String uid = AppConfig.getInstance().getUid();
        String s = MD5Util.getMD5(uid + "-" + videoid + "-" + SALT);
        OkGo.<JsonBean>get(HTTP_URL + "service=Video.setConversion")
                .params("uid", uid)
                .params("videoid", videoid)
                .params("random_str", s)
                .tag(END_WATCH_VIDEO)
                .execute(NO_CALLBACK);
    }

    //不做任何操作的HttpCallback
    private static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };

    /**
     * 获取最后一条系统消息
     */
    public static void getLastMessage(HttpCallback callback) {
        OkGo.<JsonBean>get(HTTP_URL + "service=Message.getLastTime")
                .params("uid", AppConfig.getInstance().getUid())
                .tag(GET_LAST_MESSAGE)
                .execute(callback);
    }
}




