package com.yunbao.phonelive.jpush;

import android.text.TextUtils;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ChatMessageBean;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.event.OffLineMsgEvent;
import com.yunbao.phonelive.event.JMessageLoginEvent;
import com.yunbao.phonelive.event.RoamMsgEvent;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.SharedPreferencesUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by cxf on 2017/8/10.
 * 极光IM注册、登陆等功能
 */

public class JMessageUtil {

    private static final String TAG = "极光IM";
    private static final String PWD_SUFFIX = "PUSH";//注册极光IM的时候，密码是用户id+"PUSH"这个常量构成的
    //前缀，当uid不够长的时候无法注册
    public static final String PREFIX = "";
    private Map<String, Long> mMap;
    //针对消息发送动作的控制选项
    private MessageSendingOptions mOptions;
    private SimpleDateFormat mSimpleDateFormat;
    private String mImageString;
    private String mVoiceString;
    private String mLocationString;

    private static JMessageUtil sInstance;

    private JMessageUtil() {
        mMap = new HashMap<>();
        mOptions = new MessageSendingOptions();
        mOptions.setShowNotification(false);//设置针对本次消息发送，是否需要在消息接收方的通知栏上展示通知
        mSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        mImageString = WordUtil.getString(R.string.msg_type_image);
        mVoiceString = WordUtil.getString(R.string.msg_type_voide);
        mLocationString = WordUtil.getString(R.string.msg_type_location);
    }

    public static JMessageUtil getInstance() {
        if (sInstance == null) {
            synchronized (JMessageUtil.class) {
                if (sInstance == null) {
                    sInstance = new JMessageUtil();
                }
            }
        }
        return sInstance;
    }


    public void init() {
        //开启消息漫游
        JMessageClient.init(AppContext.sInstance, true);
    }

    /**
     * 登出极光IM
     */
    public void logoutEMClient() {
        JMessageClient.unRegisterEventReceiver(JMessageUtil.this);
        JMessageClient.logout();
        SharedPreferencesUtil.getInstance().saveEMLoginStatus(false);
        EventBus.getDefault().post(new JMessageLoginEvent(false));
        L.e(TAG, "极光IM登出");
    }

    /**
     * 登录极光IM
     */
    public void loginJMessage(final String uid) {
        if (SharedPreferencesUtil.getInstance().readEMLoginStatus()) {
            L.e(TAG, "极光IM已经登录了");
            JMessageClient.registerEventReceiver(JMessageUtil.this);
            AppConfig.getInstance().setLoginIM(true);
            EventBus.getDefault().post(new JMessageLoginEvent(true));
            return;
        }
        JMessageClient.login(uid, uid + PWD_SUFFIX, new BasicCallback() {

            @Override
            public void gotResult(int code, String msg) {
                L.e(TAG, "登录极光回调---gotResult--->code: " + code + " msg: " + msg);
                if (code == 801003) {//用户不存在
                    L.e(TAG, "未注册，用户不存在");
                    registerAndLoginJMessage(uid);
                } else if (code == 0) {
                    L.e(TAG, "极光IM登录成功");
                    SharedPreferencesUtil.getInstance().saveEMLoginStatus(true);
                    JMessageClient.registerEventReceiver(JMessageUtil.this);
                    AppConfig.getInstance().setLoginIM(true);
                    EventBus.getDefault().post(new JMessageLoginEvent(true));
                }
            }
        });

    }

    //注册并登录极光IM
    private void registerAndLoginJMessage(final String uid) {
        JMessageClient.register(uid, uid + PWD_SUFFIX, new BasicCallback() {

            @Override
            public void gotResult(int code, String msg) {
                L.e(TAG, "注册极光回调---gotResult--->code: " + code + " msg: " + msg);
                if (code == 0) {
                    L.e(TAG, "极光IM注册成功");
                    loginJMessage(uid);
                }
            }
        });
    }

    /**
     * 接收消息 目前是在子线程接收的
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        //收到消息
        Message msg = event.getMessage();
        String from = getFromUserName(msg);
        int type = getMessageType(msg);
        if (TextUtils.isEmpty(from) || type == 0) {
            return;
        }
        boolean canShow = true;
        Long lastTime = mMap.get(from);
        long curTime = System.currentTimeMillis();
        if (lastTime != null) {
            if (curTime - lastTime < 1000) {
                //同一个人，上条消息距离这条消息间隔不到1秒，则不显示这条消息
                msg.setHaveRead(null);
                canShow = false;
            } else {
                mMap.put(from, curTime);
            }
        } else {
            //说明sMap内没有保存这个人的信息，则是首次收到这人的信息，可以显示
            mMap.put(from, curTime);
        }
        if (canShow) {
            L.e(TAG, "显示消息--->");
            EventBus.getDefault().post(new ChatMessageBean(from, msg, type, false));
        }
    }

    /**
     * 获取消息的类型
     */
    public int getMessageType(Message msg) {
        int type = 0;
        if (msg == null) {
            return type;
        }
        MessageContent content = msg.getContent();
        if (content == null) {
            return type;
        }
        switch (content.getContentType()) {
            case text://文字
                type = ChatMessageBean.TYPE_TEXT;
                break;
            case image://图片
                type = ChatMessageBean.TYPE_IMAGE;
                break;
            case voice://语音
                type = ChatMessageBean.TYPE_VOICE;
                break;
            case location://位置
                type = ChatMessageBean.TYPE_LOCATION;
                break;
        }
        return type;
    }

    /**
     * 获取发消息的人的极光用户名
     */
    private String getFromUserName(Message msg) {
        String result = "";
        if (msg == null) {
            return result;
        }
        UserInfo userInfo = msg.getFromUser();
        if (userInfo == null) {
            return result;
        }
        String userName = userInfo.getUserName();
        if (Constants.YB_ID_1.equals(userName) || Constants.YB_ID_2.equals(userName)) {
            return userName;
        }
        if (TextUtils.isEmpty(userName) || userName.length() < PREFIX.length()) {
            return result;
        }
        return userName.substring(PREFIX.length());
    }

    /**
     * 获取发消息的人的极光用户名
     */
    private String getFromUserName(Conversation conversation) {
        String result = "";
        if (conversation == null) {
            return result;
        }
        Object targetInfo = conversation.getTargetInfo();
        if (targetInfo == null) {
            return result;
        }
        String userName="";
        try{
            UserInfo userInfo= (UserInfo) targetInfo;
            userName=userInfo.getUserName();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (Constants.YB_ID_1.equals(userName) || Constants.YB_ID_2.equals(userName)) {
            return userName;
        }
        if (TextUtils.isEmpty(userName) || userName.length() < PREFIX.length()) {
            return result;
        }
        return userName.substring(PREFIX.length());
    }

    /**
     * 返回消息的字符串描述
     *
     * @return
     */
    public String getMessageString(Message message) {
        String result = "";
        MessageContent content = message.getContent();
        if (content == null) {
            return result;
        }
        switch (content.getContentType()) {
            case text://文字
                result = ((TextContent) content).getText();
                break;
            case image://图片
                result = mImageString;
                break;
            case voice://语音
                result = mVoiceString;
                break;
            case location://位置
                result = mLocationString;
                break;
        }
        return result;
    }

    public String getMessageTimeString(Message message) {
        return mSimpleDateFormat.format(new Date(message.getCreateTime()));
    }

    public String getMessageTimeString(long time) {
        return mSimpleDateFormat.format(new Date(time));
    }

    /**
     * 接收离线消息
     */
    public void onEvent(OfflineMessageEvent event) {
        if (AppConfig.getInstance().isLogin() && AppConfig.getInstance().isLoginIM()) {
            String from = getFromUserName(event.getConversation());
            L.e(TAG, "接收到离线消息-------->来自：" + from);
            if (!TextUtils.isEmpty(from) && !from.equals(AppConfig.getInstance().getUid())) {
                List<Message> list = event.getOfflineMessageList();
                if (list != null && list.size() > 0) {
                    ChatUserBean bean = new ChatUserBean();
                    bean.setId(from);
                    Message message = list.get(list.size() - 1);
                    bean.setLastTime(getMessageTimeString(message));
                    bean.setUnReadCount(list.size());
                    bean.setMsgType(getMessageType(message));
                    bean.setLastMessage(getMessageString(message));
                    EventBus.getDefault().post(new OffLineMsgEvent(bean));
                }
            }
        }
    }


    /**
     * 接收漫游消息
     */
    public void onEvent(ConversationRefreshEvent event) {
        if (AppConfig.getInstance().isLogin() && AppConfig.getInstance().isLoginIM()) {
            Conversation conversation = event.getConversation();
            String from = getFromUserName(conversation);
            L.e(TAG, "接收到漫游消息-------->来自：" + from);
            if (!TextUtils.isEmpty(from) && !from.equals(AppConfig.getInstance().getUid())) {
                Message message = conversation.getLatestMessage();
                ChatUserBean bean = new ChatUserBean();
                bean.setId(from);
                bean.setLastTime(getMessageTimeString(message));
                bean.setUnReadCount(conversation.getUnReadMsgCnt());
                bean.setMsgType(getMessageType(message));
                bean.setLastMessage(getMessageString(message));
                EventBus.getDefault().post(new RoamMsgEvent(bean));
            }
        }
    }

    /**
     * 创建文本消息
     *
     * @param toUid
     * @param content
     * @return
     */
    public ChatMessageBean createTextMessage(String toUid, String content) {
        Message message = JMessageClient.createSingleTextMessage(PREFIX + toUid, content);
        if (message == null) {
            return null;
        }
        return new ChatMessageBean(AppConfig.getInstance().getUid(), message, ChatMessageBean.TYPE_TEXT, true);
    }

    /**
     * 创建图片消息
     *
     * @param toUid
     * @param path  图片路径
     * @return
     */
    public ChatMessageBean createImageMessage(String toUid, String path) {
        String appKey = AppConfig.getInstance().getJPushAppKey();
        try {
            Message message = JMessageClient.createSingleImageMessage(PREFIX + toUid, appKey, new File(path));
            return new ChatMessageBean(AppConfig.getInstance().getUid(), message, ChatMessageBean.TYPE_IMAGE, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建位置消息
     *
     * @param toUid
     * @param lat     纬度
     * @param lng     经度
     * @param scale   缩放比例
     * @param address 位置详细地址
     * @return
     */
    public ChatMessageBean createLocationMessage(String toUid, double lat, double lng, int scale, String address) {
        String appKey = AppConfig.getInstance().getJPushAppKey();
        Message message = JMessageClient.createSingleLocationMessage(PREFIX + toUid, appKey, lat, lng, scale, address);
        return new ChatMessageBean(AppConfig.getInstance().getUid(), message, ChatMessageBean.TYPE_LOCATION, true);
    }

    /**
     * 创建语音消息
     *
     * @param toUid
     * @param voiceFile 语音文件
     * @param duration  语音时长
     * @return
     */
    public ChatMessageBean createVoiceMessage(String toUid, File voiceFile, long duration) {
        String appKey = AppConfig.getInstance().getJPushAppKey();
        try {
            Message message = JMessageClient.createSingleVoiceMessage(PREFIX + toUid, appKey, voiceFile, (int) (duration / 1000));
            return new ChatMessageBean(AppConfig.getInstance().getUid(), message, ChatMessageBean.TYPE_VOICE, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(ChatMessageBean bean) {
        JMessageClient.sendMessage(bean.getRawMessage(), mOptions);
    }

    /**
     * 获取会话列表用户的uid，多个uid以逗号分隔
     */
    public String getConversationUids() {
        List<Conversation> conversationList = JMessageClient.getConversationList();
        String uids = "";
        if (conversationList != null) {
            for (Conversation conversation : conversationList) {
                List<Message> messages = conversation.getAllMessage();
                if (messages == null || messages.size() == 0) {
                    Object targetInfo = conversation.getTargetInfo();
                    if (targetInfo != null) {
                        String userName = ((UserInfo) targetInfo).getUserName();
                        JMessageClient.deleteSingleConversation(userName);
                    }
                    continue;
                }
                String from = getFromUserName(conversation);
                if (!TextUtils.isEmpty(from) && !Constants.YB_ID_1.equals(from) && !Constants.YB_ID_2.equals(from)) {
                    uids += from.substring(PREFIX.length());
                    uids += ",";
                }
            }
        }
        if (uids.endsWith(",")) {
            uids = uids.substring(0, uids.length() - 1);
        }
        return uids;
    }


    /**
     * 获取会话的最后一条消息的信息
     */
    public ChatUserBean getLastMessageInfo(ChatUserBean bean) {
        String from = bean.getId();
        if (!Constants.YB_ID_1.equals(from) && !Constants.YB_ID_2.equals(from)) {
            from = PREFIX + bean.getId();
        }
        Conversation conversation = JMessageClient.getSingleConversation(from);
        if (conversation != null) {
            Message message = conversation.getLatestMessage();
            bean.setLastTime(getMessageTimeString(message));
            bean.setUnReadCount(conversation.getUnReadMsgCnt());
            bean.setMsgType(getMessageType(message));
            bean.setLastMessage(getMessageString(message));
            return bean;
        }
        return null;
    }


    /**
     * 获取消息列表
     */
    public List<ChatMessageBean> getChatMessageList(String toUid) {
        List<ChatMessageBean> result = new ArrayList<>();
        Conversation conversation = JMessageClient.getSingleConversation(PREFIX + toUid);
        if (conversation == null) {
            return result;
        }
        List<Message> msgList = conversation.getAllMessage();
        if (msgList == null) {
            return result;
        }
        int size = msgList.size();
        if (size < 20) {
            Message latestMessage = conversation.getLatestMessage();
            if (latestMessage == null) {
                return result;
            }
            List<Message> list = conversation.getMessagesFromNewest(latestMessage.getId(), 20 - size);
            if (list == null) {
                return result;
            }
            list.addAll(msgList);
            msgList = list;
        }
        String uid = AppConfig.getInstance().getUid();
        for (Message msg : msgList) {
            String from = getFromUserName(msg);
            if (Constants.YB_ID_1.equals(from) || Constants.YB_ID_2.equals(from)) {
                continue;
            }
            int type = getMessageType(msg);
            if (!TextUtils.isEmpty(from) && type != 0) {
                ChatMessageBean bean = new ChatMessageBean(from, msg, type, from.equals(uid));
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * 获取全部未读消息的总数
     */
    public String getAllUnReadMsgCount() {
        int unReadCount = JMessageClient.getAllUnReadMsgCount();
        if(unReadCount<0){
            unReadCount=0;
        }
        L.e(TAG, "未读消息总数----->" + unReadCount);
        if (unReadCount > 99) {
            return "99+";
        }
        return String.valueOf(unReadCount);
    }

    /**
     * 设置某个会话的消息为已读
     *
     * @param toUid 对方uid
     */
    public void markAllMessagesAsRead(String toUid) {
        if (!TextUtils.isEmpty(toUid)) {
            if (!Constants.YB_ID_1.equals(toUid) && !Constants.YB_ID_2.equals(toUid)) {
                toUid = PREFIX + toUid;
            }
            Conversation conversation = JMessageClient.getSingleConversation(toUid);
            if (conversation != null) {
                conversation.resetUnreadCount();
            }
        }
    }

    public void removeMessage(String toUid, Message message) {
        if (!TextUtils.isEmpty(toUid) && message != null) {
            if (!Constants.YB_ID_1.equals(toUid) && !Constants.YB_ID_2.equals(toUid)) {
                toUid = PREFIX + toUid;
            }
            Conversation conversation = JMessageClient.getSingleConversation(toUid);
            if (conversation != null) {
                conversation.deleteMessage(message.getId());
            }
        }
    }

    /**
     * 删除所有会话记录
     */
    public void removeAllConversation() {
        List<Conversation> list = JMessageClient.getConversationList();
        for (Conversation conversation : list) {
            Object targetInfo = conversation.getTargetInfo();
            JMessageClient.deleteSingleConversation(((UserInfo) targetInfo).getUserName());
        }
    }


}
