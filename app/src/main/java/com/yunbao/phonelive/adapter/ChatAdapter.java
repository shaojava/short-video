package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.UserCenterActivity;
import com.yunbao.phonelive.bean.ChatMessageBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.AnimImageView;
import com.yunbao.phonelive.custom.TextRender;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.FrameAnimUtil;
import com.yunbao.phonelive.utils.MessageDateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;

/**
 * Created by cxf on 2018/7/11.
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TEXT_LEFT = 1;
    private static final int TYPE_TEXT_RIGHT = 2;
    private static final int TYPE_IMAGE_LEFT = 3;
    private static final int TYPE_IMAGE_RIGHT = 4;
    private static final int TYPE_VOICE_LEFT = 5;
    private static final int TYPE_VOICE_RIGHT = 6;
    private static final int TYPE_LOCATION_LEFT = 7;
    private static final int TYPE_LOCATION_RIGHT = 8;

    private Context mContext;
    private UserBean mUserBean;
    private UserBean mToUserBean;
    private String mUserAvatar;
    private String mToUserAvatar;
    private LayoutInflater mInflater;
    private List<ChatMessageBean> mList;
    private RecyclerView mRecyclerView;
    private long mLastMessageTime;
    private String mTxLocationKey;
    private ActionListener mActionListener;
    private int[] mLocation = new int[2];
    private List<String> mImagePathList;
    private int mCurMessagePosition;
    private long mLastClickTime;


    public ChatAdapter(Context context, List<ChatMessageBean> list, UserBean toUserBean) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mUserBean = AppConfig.getInstance().getUserBean();
        mToUserBean = toUserBean;
        mUserAvatar = mUserBean.getAvatar();
        mToUserAvatar = mToUserBean.getAvatar();
        mTxLocationKey = AppConfig.getInstance().getTxLocationKey();
    }

    public boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickTime < 500) {
            return false;
        }
        mLastClickTime = curTime;
        return true;
    }

    public int getMyMessageCount() {
        int count = 0;
        if (mList != null) {
            for (ChatMessageBean bean : mList) {
                if (bean.isFromSelf()) {
                    count++;
                }
            }
        }
        return count;
    }

    private void getImagePathList(ChatMessageBean chatMessageBean) {
        if (mImagePathList == null) {
            mImagePathList = new ArrayList<>();
        } else {
            mImagePathList.clear();
        }
        List<ChatMessageBean> list = new ArrayList<>();
        for (ChatMessageBean bean : mList) {
            if (bean.getType() == ChatMessageBean.TYPE_IMAGE) {
                list.add(bean);
            }
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            ChatMessageBean bean = list.get(i);
            String filePath = bean.getImageFilePath();
            if (TextUtils.isEmpty(filePath)) {
                filePath = ((ImageContent) bean.getRawMessage().getContent()).getLocalThumbnailPath();
            }
            mImagePathList.add(filePath);
            if (bean == chatMessageBean) {
                mCurMessagePosition = i;
            }
        }
    }

    public void updateVoiceItem(ChatMessageBean bean, boolean isPlayVoice) {
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (bean == mList.get(i)) {
                bean.setPlayVoice(isPlayVoice);
                notifyItemChanged(i, "payload");
                break;
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageBean msg = mList.get(position);
        switch (msg.getType()) {
            case ChatMessageBean.TYPE_TEXT:
                if (msg.isFromSelf()) {
                    return TYPE_TEXT_RIGHT;
                } else {
                    return TYPE_TEXT_LEFT;
                }
            case ChatMessageBean.TYPE_IMAGE:
                if (msg.isFromSelf()) {
                    return TYPE_IMAGE_RIGHT;
                } else {
                    return TYPE_IMAGE_LEFT;
                }
            case ChatMessageBean.TYPE_VOICE:
                if (msg.isFromSelf()) {
                    return TYPE_VOICE_RIGHT;
                } else {
                    return TYPE_VOICE_LEFT;
                }
            case ChatMessageBean.TYPE_LOCATION:
                if (msg.isFromSelf()) {
                    return TYPE_LOCATION_RIGHT;
                } else {
                    return TYPE_LOCATION_LEFT;
                }
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT_LEFT:
                return new TextVh(mInflater.inflate(R.layout.item_chat_text_left, parent, false));
            case TYPE_TEXT_RIGHT:
                return new TextVh(mInflater.inflate(R.layout.item_chat_text_right, parent, false));
            case TYPE_IMAGE_LEFT:
                return new ImageVh(mInflater.inflate(R.layout.item_chat_image_left, parent, false));
            case TYPE_IMAGE_RIGHT:
                return new ImageVh(mInflater.inflate(R.layout.item_chat_image_right, parent, false));
            case TYPE_VOICE_LEFT:
                return new VoiceVh(mInflater.inflate(R.layout.item_chat_voice_left, parent, false));
            case TYPE_VOICE_RIGHT:
                return new VoiceVh(mInflater.inflate(R.layout.item_chat_voice_right, parent, false));
            case TYPE_LOCATION_LEFT:
                return new LocationVh(mInflater.inflate(R.layout.item_chat_location_left, parent, false));
            case TYPE_LOCATION_RIGHT:
                return new LocationVh(mInflater.inflate(R.layout.item_chat_location_right, parent, false));
        }
        return null;
    }

    public ChatMessageBean getLastMessage() {
        if (mList != null && mList.size() > 0) {
            return mList.get(mList.size() - 1);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    public void insertItem(ChatMessageBean bean) {
        if (mList != null) {
            int position = mList.size();
            mList.add(bean);
            notifyItemInserted(position);
            mRecyclerView.scrollToPosition(position);
        }
    }

    public void scrollToBottom() {
        if (mList != null) {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mList.size() - 1, 0);
        }
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mTime;
        ChatMessageBean mChatMessageBean;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChatMessageBean != null) {
                        String touid = mChatMessageBean.getFrom();
                        if (!TextUtils.isEmpty(touid)) {
                            UserCenterActivity.forwardOtherUserCenter(mContext, touid, false);
                        }
                    }
                }
            });
        }

        void setData(ChatMessageBean bean, int position, Object payload) {
            mChatMessageBean = bean;
            if (payload == null) {
                if (bean.isFromSelf()) {
                    ImgLoader.display(mUserAvatar, mAvatar);
                } else {
                    ImgLoader.display(mToUserAvatar, mAvatar);
                }
                if (position == 0) {
                    mLastMessageTime = bean.getCreateTime();
                    if (mTime.getVisibility() != View.VISIBLE) {
                        mTime.setVisibility(View.VISIBLE);
                    }
                    mTime.setText(MessageDateUtil.getTimestampString(mLastMessageTime));
                } else {
                    if (MessageDateUtil.isCloseEnough(bean.getCreateTime(), mLastMessageTime)) {
                        if (mTime.getVisibility() == View.VISIBLE) {
                            mTime.setVisibility(View.GONE);
                        }
                    } else {
                        mLastMessageTime = bean.getCreateTime();
                        if (mTime.getVisibility() != View.VISIBLE) {
                            mTime.setVisibility(View.VISIBLE);
                        }
                        mTime.setText(MessageDateUtil.getTimestampString(mLastMessageTime));
                    }
                }
            }
        }
    }

    class TextVh extends Vh {

        TextView mText;

        public TextVh(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(null);
        }

        @Override
        public void setData(ChatMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            String text = ((TextContent) bean.getRawMessage().getContent()).getText();
            mText.setText(TextRender.renderChatMessage(text));
        }
    }

    class ImageVh extends Vh {
        ImageView mImg;
        ChatMessageBean mBean;
        Message mMessage;
        ImageContent mImageContent;
        DownloadCompletionCallback mDownloadCompletionCallback;

        public ImageVh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mDownloadCompletionCallback = new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, File file) {
                    String path = file.getAbsolutePath();
                    mBean.setImageFilePath(path);
                    ImgLoader.display(path, mImg);
                }
            };
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick()) {
                        return;
                    }
                    String filePath = mBean.getImageFilePath();
                    if (TextUtils.isEmpty(filePath)) {
                        return;
                    }
                    mImg.getLocationOnScreen(mLocation);
                    if (mActionListener != null) {
                        getImagePathList(mBean);
                        mActionListener.onImageClick(filePath, mLocation[0], mLocation[1], mImg.getWidth(), mImg.getHeight(), mImagePathList, mCurMessagePosition);
                    }
                }
            });
        }

        @Override
        public void setData(ChatMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mBean = bean;
            if (bean.getImageFilePath() != null) {
                ImgLoader.display(bean.getImageFilePath(), mImg);
            } else {
                mMessage = bean.getRawMessage();
                mImageContent = (ImageContent) mMessage.getContent();
                mImageContent.downloadOriginImage(mMessage, mDownloadCompletionCallback);
            }
        }
    }

    class VoiceVh extends Vh {

        TextView mDuration;
        View mRedPoint;
        Message mMessage;
        VoiceContent mVoiceContent;
        AnimImageView mAnimImageView;
        ChatMessageBean mBean;
        int mPosition;
        DownloadCompletionCallback mDownloadCompletionCallback;

        public VoiceVh(View itemView) {
            super(itemView);
            mAnimImageView = (AnimImageView) itemView.findViewById(R.id.voice_anim_view);
            mRedPoint = itemView.findViewById(R.id.red_point);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            mDownloadCompletionCallback = new DownloadCompletionCallback() {
                @Override
                public void onComplete(int i, String s, File file) {
                    if (mActionListener != null) {
                        mMessage.setHaveRead(null);
                        mActionListener.onVoiceDownload(mBean, file.getAbsolutePath());
                    }
                }
            };
            itemView.findViewById(R.id.bubbleLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canClick()) {
                        return;
                    }
                    if (mBean.isPlayVoice()) {
                        mBean.setPlayVoice(false);
                        notifyItemChanged(mPosition, "payload");
                        if (mActionListener != null) {
                            mActionListener.onStopVoice();
                        }
                    } else {
                        if (mMessage != null) {
                            mVoiceContent.downloadVoiceFile(mMessage, mDownloadCompletionCallback);
                        }
                    }
                }
            });
        }

        @Override
        public void setData(ChatMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mBean = bean;
            mPosition = position;
            mMessage = bean.getRawMessage();
            mVoiceContent = (VoiceContent) mMessage.getContent();
            mDuration.setText((mVoiceContent.getDuration()) + "s");
            if (bean.isFromSelf()) {
                mAnimImageView.setImgList(FrameAnimUtil.getChatVoiceAnimRight());
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                mAnimImageView.setImgList(FrameAnimUtil.getChatVoiceAnimLeft());
                if (mMessage.haveRead()) {
                    if (mRedPoint.getVisibility() == View.VISIBLE) {
                        mRedPoint.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (mRedPoint.getVisibility() != View.VISIBLE) {
                        mRedPoint.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (bean.isPlayVoice()) {
                mAnimImageView.startAnim();
            } else {
                mAnimImageView.stopAnim();
            }
        }
    }

    class LocationVh extends Vh {

        TextView mTitle;
        TextView mAddress;
        ImageView mMap;

        public LocationVh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mMap = (ImageView) itemView.findViewById(R.id.map);
            itemView.setOnClickListener(null);
        }

        @Override
        public void setData(ChatMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            LocationContent locationContent = (LocationContent) (bean.getRawMessage().getContent());
            try {
                JSONObject obj = JSON.parseObject(locationContent.getAddress());
                mTitle.setText(obj.getString("name"));
                mAddress.setText(obj.getString("info"));
            } catch (Exception e) {
                mTitle.setText("");
                mAddress.setText("");
            }
            int zoom = locationContent.getScale().intValue();
            if (zoom > 18 || zoom < 4) {
                zoom = 18;
            }
            double lat = locationContent.getLatitude().doubleValue();
            double lng = locationContent.getLongitude().doubleValue();
            //腾讯地图生成静态图接口
            //https://apis.map.qq.com/ws/staticmap/v2/?center=36.183815,117.074043&zoom=16&markers=color:red|size:large|36.183815,117.074043&key=KNJBZ-IYTLV-2QQPG-UB5UP-PBIUZ-VABRP&size=200*120&scale=2
            String staticMapUrl = "https://apis.map.qq.com/ws/staticmap/v2/?center=" + lat + "," + lng + "&size=200*120&scale=2&zoom=" + zoom + "&key=" + mTxLocationKey;
            ImgLoader.display(staticMapUrl, mMap);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    public interface ActionListener {
        void onImageClick(String filePath, int x, int y, int wdith, int height, List<String> filePathList, int position);

        void onVoiceDownload(ChatMessageBean bean, String filePath);

        void onStopVoice();
    }
}
