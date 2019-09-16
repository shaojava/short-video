package com.yunbao.phonelive.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AdminMsgActivity;
import com.yunbao.phonelive.activity.AtMsgActivity;
import com.yunbao.phonelive.activity.ChatActivity;
import com.yunbao.phonelive.activity.CommentMsgActivity;
import com.yunbao.phonelive.activity.ContactsActivity;
import com.yunbao.phonelive.activity.FansMsgActivity;
import com.yunbao.phonelive.activity.MainActivity;
import com.yunbao.phonelive.activity.SystemMsgActivity;
import com.yunbao.phonelive.activity.ZanMsgActivity;
import com.yunbao.phonelive.adapter.MessageAdapter;
import com.yunbao.phonelive.bean.ChatMessageBean;
import com.yunbao.phonelive.bean.ChatUserBean;
import com.yunbao.phonelive.event.OffLineMsgEvent;
import com.yunbao.phonelive.event.ChatRoomCloseEvent;
import com.yunbao.phonelive.event.ChatRoomOpenEvent;
import com.yunbao.phonelive.event.JMessageLoginEvent;
import com.yunbao.phonelive.event.LoginUserChangedEvent;
import com.yunbao.phonelive.event.RoamMsgEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.jpush.JMessageUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.model.Message;

/**
 * Created by cxf on 2018/6/5.
 */

public class MessageFragment extends AbsFragment implements OnItemClickListener<ChatUserBean>, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private Map<String, ChatUserBean> mMap;
    private String mNotUpdateUid;
    private ChatMessageBean mNewMessageBean;
    private List<ChatUserBean> mAdminList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void main() {
        mRootView.findViewById(R.id.btn_add_user).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_fans).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_zan).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_at).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_comment).setOnClickListener(this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MessageAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mMap = new HashMap<>();
        mAdminList = mAdapter.getAdminChatBeanList();
        for (ChatUserBean bean : mAdminList) {
            mMap.put(bean.getId(), bean);
        }
        EventBus.getDefault().register(this);
        getConversationInfo();
        getLastMessage();
    }

    @Override
    public void onItemClick(ChatUserBean bean, int position) {
        if (bean != null) {
            if (bean.getFromType() == ChatUserBean.TYPE_SYSTEM) {
                if (bean.getUnReadCount() > 0) {
                    bean.setUnReadCount(0);
                    mAdapter.updateItem(bean.getId());
                    JMessageUtil.getInstance().markAllMessagesAsRead(bean.getId());
                    ((MainActivity) mContext).refreshUnReadCount();
                }
                if (Constants.YB_ID_1.equals(bean.getId())) {
                    startActivity(new Intent(mContext, AdminMsgActivity.class));
                } else {
                    startActivity(new Intent(mContext, SystemMsgActivity.class));
                }
            } else {
                ChatActivity.forwardChatRoom(mContext, bean);
            }
        }
    }


    private void getConversationInfo() {
        if (AppConfig.getInstance().isLogin() && AppConfig.getInstance().isLoginIM()) {
            String uids = JMessageUtil.getInstance().getConversationUids();
            if (!TextUtils.isEmpty(uids)) {
                HttpUtil.getMultiInfo(uids, mGetMultiInfoCallback);
            }
        }
    }

    private HttpCallback mGetMultiInfoCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                List<ChatUserBean> list = JSON.parseArray(Arrays.toString(info), ChatUserBean.class);
                List<ChatUserBean> msgList = new ArrayList<>();
                JMessageUtil util = JMessageUtil.getInstance();
                for (ChatUserBean bean : list) {
                    bean = util.getLastMessageInfo(bean);
                    if (bean != null) {
                        msgList.add(bean);
                        mMap.put(bean.getId(), bean);
                    }
                }
                mAdapter.insertList(msgList);
            }
        }
    };

    private HttpCallback mGetMultiInfoCallback2 = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                if (c != null && mNewMessageBean != null && mAdapter != null) {
                    Message message = mNewMessageBean.getRawMessage();
                    JMessageUtil util = JMessageUtil.getInstance();
                    c.setLastTime(util.getMessageTimeString(message));
                    c.setLastMessage(util.getMessageString(message));
                    c.setUnReadCount(1);
                    if (!mMap.containsKey(c.getId())) {
                        mAdapter.insertItem(c);
                    } else {
                        mAdapter.updateItem(c.getId());
                    }
                    mMap.put(c.getId(), c);
                }
            }
        }
    };

    private void getLastMessage() {
        if (AppConfig.getInstance().isLogin()) {
            HttpUtil.getLastMessage(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj.containsKey("officeInfo")) {
                            JSONObject officeInfo = obj.getJSONObject("officeInfo");
                            ChatUserBean bean = mAdminList.get(0);
                            bean.setLastTime(JMessageUtil.getInstance().getMessageTimeString(officeInfo.getLongValue("addtime") * 1000));
                            bean.setLastMessage(officeInfo.getString("title"));
                            if (mAdapter != null) {
                                mAdapter.updateItem(0);
                            }
                        } else {
                            ChatUserBean bean = mAdminList.get(0);
                            bean.setLastTime("");
                            bean.setLastMessage("欢迎入驻" + WordUtil.getString(R.string.app_name));
                            if (mAdapter != null) {
                                mAdapter.updateItem(0);
                            }
                        }
                        if (obj.containsKey("sysInfo")) {
                            JSONObject sysInfo = obj.getJSONObject("sysInfo");
                            ChatUserBean bean = mAdminList.get(1);
                            bean.setLastTime(JMessageUtil.getInstance().getMessageTimeString(sysInfo.getLongValue("addtime") * 1000));
                            bean.setLastMessage(sysInfo.getString("title"));
                            if (mAdapter != null) {
                                mAdapter.updateItem(1);
                            }
                        } else {
                            ChatUserBean bean = mAdminList.get(1);
                            bean.setLastTime("");
                            bean.setLastMessage("暂无通知");
                            if (mAdapter != null) {
                                mAdapter.updateItem(1);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_LAST_MESSAGE);
        HttpUtil.cancel(HttpUtil.GET_MULTI_INFO);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 接收消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageBean(ChatMessageBean bean) {
        if (bean != null && !bean.isFromSelf() && mAdapter != null) {
            if (mNotUpdateUid == null || !mMap.containsKey(mNotUpdateUid) || !mNotUpdateUid.equals(bean.getFrom())) {
                mNewMessageBean = bean;
                ChatUserBean chatUserBean = mMap.get(bean.getFrom());
                if (chatUserBean != null) {
                    if (mAdapter != null) {
                        chatUserBean.setUnReadCount(chatUserBean.getUnReadCount() + 1);
                        Message message = bean.getRawMessage();
                        JMessageUtil util = JMessageUtil.getInstance();
                        chatUserBean.setLastTime(util.getMessageTimeString(message));
                        chatUserBean.setLastMessage(util.getMessageString(message));
                        mAdapter.updateItem(bean.getFrom());
                    }
                } else {
                    HttpUtil.getMultiInfo(bean.getFrom(), mGetMultiInfoCallback2);
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJMessageLoginEvent(JMessageLoginEvent e) {
        if (e.isLogin()) {
            if (mAdapter != null) {
                mAdapter.updateAdminChatInfo();
            }
            getConversationInfo();
        } else {
            if (mMap != null) {
                mMap.clear();
                for (ChatUserBean bean : mAdminList) {
                    mMap.put(bean.getId(), bean);
                }
            }
            if (mAdapter != null) {
                mAdapter.clearData();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomOpenEvent(ChatRoomOpenEvent e) {
        mNotUpdateUid = e.getToUid();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRoomCloseEvent(ChatRoomCloseEvent e) {
        mNotUpdateUid = null;
        if (mAdapter != null) {
            String touid = e.getToUid();
            ChatUserBean bean = mMap.get(touid);
            if (bean != null) {
                bean.setUnReadCount(0);
                bean.setLastMessage(e.getLastMessage());
                bean.setLastTime(e.getLastTime());
                mAdapter.updateItem(touid);
            } else {
                ChatUserBean c = new ChatUserBean();
                c.wrapUserBean(e.getToUserBean());
                c.setUnReadCount(0);
                c.setLastMessage(e.getLastMessage());
                c.setLastTime(e.getLastTime());
                if (!mMap.containsKey(c.getId())) {
                    mAdapter.insertItem(c);
                } else {
                    mAdapter.updateItem(c.getId());
                }
                mMap.put(c.getId(), c);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoamMsgEvent(RoamMsgEvent e) {
        if (mMap == null || mAdapter == null) {
            return;
        }
        final ChatUserBean bean = e.getChatUserBean();
        String from = bean.getId();
        if (mMap.containsKey(from)) {
            ChatUserBean c = mMap.get(from);
            c.setUnReadCount(bean.getUnReadCount());
            c.setLastMessage(bean.getLastMessage());
            c.setLastTime(bean.getLastTime());
            c.setMsgType(bean.getMsgType());
            mAdapter.updateItem(from);
        } else {
            HttpUtil.getMultiInfo(from, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                        c.setUnReadCount(bean.getUnReadCount());
                        c.setLastMessage(bean.getLastMessage());
                        c.setLastTime(bean.getLastTime());
                        c.setMsgType(bean.getMsgType());
                        if (!mMap.containsKey(c.getId())) {
                            mAdapter.insertItem(c);
                        } else {
                            mAdapter.updateItem(c.getId());
                        }
                        mMap.put(c.getId(), c);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOffLineMsgEvent(OffLineMsgEvent e) {
        if (mMap == null || mAdapter == null) {
            return;
        }
        final ChatUserBean bean = e.getChatUserBean();
        String from = bean.getId();
        if (mMap.containsKey(from)) {
            ChatUserBean c = mMap.get(from);
            c.setUnReadCount(c.getUnReadCount() + bean.getUnReadCount());
            c.setLastMessage(bean.getLastMessage());
            c.setLastTime(bean.getLastTime());
            c.setMsgType(bean.getMsgType());
            mAdapter.updateItem(from);
        } else {
            HttpUtil.getMultiInfo(from, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        ChatUserBean c = JSON.parseObject(info[0], ChatUserBean.class);
                        c.setUnReadCount(bean.getUnReadCount());
                        c.setLastMessage(bean.getLastMessage());
                        c.setLastTime(bean.getLastTime());
                        c.setMsgType(bean.getMsgType());
                        if (!mMap.containsKey(c.getId())) {
                            mAdapter.insertItem(c);
                        } else {
                            mAdapter.updateItem(c.getId());
                        }
                        mMap.put(c.getId(), c);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginUserChangedEvent(LoginUserChangedEvent e) {
        getLastMessage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_user:
                forwardContactsActivity();
                //JMessageUtil.getInstance().removeAllConversation();
                break;
            case R.id.btn_fans:
                forwardFansMsgActivity();
                break;
            case R.id.btn_zan:
                forwardZanMsgActivity();
                break;
            case R.id.btn_at:
                forwardAtMsgActivity();
                break;
            case R.id.btn_comment:
                forwardCommentMsgActivity();
                break;
        }
    }

    /**
     * 粉丝
     */
    private void forwardFansMsgActivity() {
        startActivity(new Intent(this.mContext, FansMsgActivity.class));
    }

    /**
     * 赞
     */
    private void forwardZanMsgActivity() {
        startActivity(new Intent(mContext, ZanMsgActivity.class));
    }

    /**
     * "@我的"
     */
    private void forwardAtMsgActivity() {
        startActivity(new Intent(mContext, AtMsgActivity.class));
    }

    /**
     * 评论
     */
    private void forwardCommentMsgActivity() {
        startActivity(new Intent(mContext, CommentMsgActivity.class));
    }

    /**
     * 搜索联系人
     */
    private void forwardContactsActivity() {
        startActivity(new Intent(mContext, ContactsActivity.class));
    }

}
