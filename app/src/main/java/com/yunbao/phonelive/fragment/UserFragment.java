package com.yunbao.phonelive.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ChatActivity;
import com.yunbao.phonelive.activity.EditProfileActivity;
import com.yunbao.phonelive.activity.FansActivity;
import com.yunbao.phonelive.activity.FollowActivity;
import com.yunbao.phonelive.activity.LoginActivity;
import com.yunbao.phonelive.activity.UserCenterActivity;
import com.yunbao.phonelive.activity.SettingActivity;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.activity.WebUploadImgActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.ViewPagerIndicator;
import com.yunbao.phonelive.event.FollowEvent;
import com.yunbao.phonelive.event.LoginUserChangedEvent;
import com.yunbao.phonelive.event.NeedRefreshEvent;
import com.yunbao.phonelive.event.VideoDeleteEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.AppBarStateListener;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/6/5.
 */

public class UserFragment extends AbsFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private TextView mTitle;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mId;
    private TextView mSign;
    private TextView mAge;
    private TextView mCity;
    private TextView mGender;
    private TextView mZan;
    private TextView mFans;
    private TextView mFollow;
    private View mBtnBack;
    private View mBtnMore;
    private View mBtnPrivateMsg;
    private LinearLayout mOtherGroup;
    private AppBarLayout mAppBarLayout;
    private List<UserItemFragment> mFragmentList;
    private OnBackClickListener mOnBackClickListener;
    private boolean mIsMainUserCenter;//是否是MainFragment 里面的个人中心
    private UserBean mUserBean;
    private View mBtnAddFollow;
    private View mBtnFollowing;
    private boolean mNeedRefresh;
    private boolean mPaused;
    private ObjectAnimator mInAnim;
    private ObjectAnimator mOutAnim;
    private String mWorkString;
    private String mLikeString;
    private boolean mAppBarExpand = true;//AppBarLayout是否展开
    private String mToUid;
    private long mLastTime;
    private boolean mUserChanged = true;
    private boolean mCalculateBg;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    protected void main() {
        mTitle = (TextView) mRootView.findViewById(R.id.title);
        mAvatar = (ImageView) mRootView.findViewById(R.id.avatar);
        mName = (TextView) mRootView.findViewById(R.id.name);
        mId = (TextView) mRootView.findViewById(R.id.id_val);
        mSign = (TextView) mRootView.findViewById(R.id.sign);
        mGender = (TextView) mRootView.findViewById(R.id.gender);
        mAge = (TextView) mRootView.findViewById(R.id.age);
        mCity = (TextView) mRootView.findViewById(R.id.city);
        mZan = (TextView) mRootView.findViewById(R.id.zan);
        mFans = (TextView) mRootView.findViewById(R.id.fans);
        mFollow = (TextView) mRootView.findViewById(R.id.follow);
        mBtnBack = mRootView.findViewById(R.id.btn_back);
        mBtnMore = mRootView.findViewById(R.id.btn_more);
        mWorkString = WordUtil.getString(R.string.zuoping);
        mLikeString = WordUtil.getString(R.string.like);
        mFans.setOnClickListener(this);
        mFollow.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
        Bundle bundle = getArguments();
        mIsMainUserCenter = bundle.getBoolean(Constants.IS_MAIN_USER_CENTER, false);
        if (mIsMainUserCenter) {
            mBtnBack.setVisibility(View.INVISIBLE);
            bundle.putString(Constants.UID, AppConfig.getInstance().getUid());
            mRootView.setPadding(0, 0, 0, DpUtil.dp2px(46));
            mRootView.findViewById(R.id.group_self).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.btn_edit_profile).setOnClickListener(this);
            mRootView.findViewById(R.id.btn_setting).setOnClickListener(this);
        } else {
            calculateBgLayout();
            mOtherGroup = (LinearLayout) mRootView.findViewById(R.id.group_other);
            mBtnPrivateMsg = mRootView.findViewById(R.id.btn_private_msg);
            mBtnPrivateMsg.setOnClickListener(this);
            mBtnAddFollow = mRootView.findViewById(R.id.btn_add_follow);
            mBtnFollowing = mRootView.findViewById(R.id.btn_following);
            mBtnAddFollow.setOnClickListener(this);
            mBtnFollowing.setOnClickListener(this);
            mBtnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnBackClickListener != null) {
                        mOnBackClickListener.onBackClick();
                    }
                }
            });
            if (mContext instanceof VideoPlayActivity) {
                mUserBean = bundle.getParcelable(Constants.USER_BEAN);
                int isAttention = bundle.getInt(Constants.IS_ATTENTION);
                if (mUserBean != null) {
                    mToUid = mUserBean.getId();
                    bundle.putString(Constants.UID, mToUid);
                    showUserInfo();
                    if ("0".equals(mToUid)) {//对方是系统管理员
                        if (mOtherGroup.getVisibility() == View.VISIBLE) {
                            mOtherGroup.setVisibility(View.INVISIBLE);
                        }
                        if (mBtnMore.getVisibility() == View.VISIBLE) {
                            mBtnMore.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (!AppConfig.getInstance().isLogin() || !mUserBean.getId().equals(AppConfig.getInstance().getUid())) {
                            showAttention(isAttention);
                            if (mOtherGroup.getVisibility() != View.VISIBLE) {
                                mOtherGroup.setVisibility(View.VISIBLE);
                            }
                            if (mBtnMore.getVisibility() != View.VISIBLE) {
                                mBtnMore.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (mOtherGroup.getVisibility() == View.VISIBLE) {
                                mOtherGroup.setVisibility(View.INVISIBLE);
                            }
                            if (mBtnMore.getVisibility() == View.VISIBLE) {
                                mBtnMore.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                }
            } else if (mContext instanceof UserCenterActivity) {
                String uid = bundle.getString(Constants.UID);
                mToUid = uid;
                if ("0".equals(uid)) {
                    if (mOtherGroup.getVisibility() == View.VISIBLE) {
                        mOtherGroup.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!AppConfig.getInstance().isLogin() || !AppConfig.getInstance().getUid().equals(uid)) {
                        if (mOtherGroup.getVisibility() != View.VISIBLE) {
                            mOtherGroup.setVisibility(View.VISIBLE);
                        }
                        boolean showPrivate = bundle.getBoolean(Constants.SHOW_PRI_MSG);
                        if (!showPrivate && mBtnPrivateMsg.getVisibility() == View.VISIBLE) {
                            mBtnPrivateMsg.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (mOtherGroup.getVisibility() == View.VISIBLE) {
                            mOtherGroup.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                showMore(uid);
                HttpUtil.getUserHome(uid, mGetUserHomeCallback);
            } else {
                bundle.putString(Constants.UID, Constants.NOT_LOGIN_UID);
            }
            if (mOtherGroup.getVisibility() == View.VISIBLE) {
                initAnim();
            }
        }
        mIndicator = (ViewPagerIndicator) mRootView.findViewById(R.id.indicator);
        mIndicator.setVisibleChildCount(2);
        mIndicator.setTitles(new String[]{mWorkString, mLikeString});
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new UserWorkFragment());
        mFragmentList.add(new UserLikeFragment());
        for (UserItemFragment fragment : mFragmentList) {
            fragment.setArguments(bundle);
        }
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        mAppBarLayout = (AppBarLayout) mRootView.findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float totalScrollRange = appBarLayout.getTotalScrollRange();
                float rate = -1 * verticalOffset / totalScrollRange;
                mTitle.setAlpha(rate);
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int state) {
                switch (state) {
                    case AppBarStateListener.EXPANDED:
                        //L.e("mAppBarLayout--------->展开");
                        mAppBarExpand = true;
                        break;
                    case AppBarStateListener.COLLAPSED:
                        //L.e("mAppBarLayout--------->收起");
                        mAppBarExpand = false;
                        break;
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    private void calculateBgLayout() {
        if (!mCalculateBg) {
            mCalculateBg = true;
            final View view = mRootView.findViewById(R.id.top_group);
            view.post(new Runnable() {
                @Override
                public void run() {
                    View bg = view.findViewById(R.id.top_group_bg);
                    ViewGroup.LayoutParams params = bg.getLayoutParams();
                    params.height = view.getHeight();
                    bg.requestLayout();
                }
            });
        }
    }

    /**
     * AppBarLayout 展开
     */
    private void expand() {
        if (!mAppBarExpand && mAppBarLayout != null) {
            mAppBarLayout.setExpanded(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mFragmentList != null) {
            mFragmentList.get(position).loadData();
        }
        if (mIsMainUserCenter && mUserBean != null) {
            if (position == 0) {
                ((MainFragment) getParentFragment()).showRecordTip(mUserBean.getWorkVideos() == 0);
            } else {
                ((MainFragment) getParentFragment()).showRecordTip(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsMainUserCenter && !hidden && AppConfig.getInstance().isLogin()) {
            calculateBgLayout();
            HttpUtil.getUserHome(AppConfig.getInstance().getUid(), mGetUserHomeCallback);
        }
    }

    private HttpCallback mGetUserHomeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                mUserBean = JSON.toJavaObject(obj, UserBean.class);
                showUserInfo();
                if (!mIsMainUserCenter) {
                    showAttention(obj.getIntValue("isattention"));
                }
            }
        }
    };

    private void showUserInfo() {
        if (mUserBean != null) {
            if (mTitle != null) {
                mTitle.setText(mUserBean.getUser_nicename());
            }
            if (mName != null) {
                mName.setText(mUserBean.getUser_nicename());
            }
            if (mId != null) {
                mId.setText("ID: " + mUserBean.getId());
            }
            if (mSign != null) {
                mSign.setText(mUserBean.getSignature());
            }
            if (mZan != null) {
                mZan.setText(mUserBean.getPraise() + " " + WordUtil.getString(R.string.get_zan));
            }
            if (mFans != null) {
                mFans.setText(mUserBean.getFans() + " " + WordUtil.getString(R.string.fans));
            }
            if (mFollow != null) {
                mFollow.setText(mUserBean.getFollows() + " " + WordUtil.getString(R.string.follow));
            }
            if (mAvatar != null) {
                ImgLoader.display(mUserBean.getAvatar(), mAvatar);
            }
            if (mGender != null) {
                mGender.setText(Constants.GENDER_MAP.get(mUserBean.getSex()));
            }
            if (mAge != null) {
                mAge.setText(mUserBean.getAge());
            }
            if (mCity != null) {
                mCity.setText(mUserBean.getCity());
            }
            if (mIndicator != null) {
                mIndicator.setTitleText(0, mWorkString + " " + mUserBean.getWorkVideos());
                mIndicator.setTitleText(1, mLikeString + " " + mUserBean.getLikeVideos());
            }
            if (mIsMainUserCenter && AppConfig.getInstance().isLogin() && mViewPager != null && mViewPager.getCurrentItem() == 0) {
                ((MainFragment) getParentFragment()).showRecordTip(mUserBean.getWorkVideos() == 0);
            }
        }
    }


    public void setUserInfo(UserBean userBean, int isAttention) {
        if (mFragmentList == null) {
            return;
        }
        if (userBean == null) {
            return;
        }
        String uid = userBean.getId();
        if (uid == null || mUserBean != null && uid.equals(mUserBean.getId())) {
            return;
        }
        mUserChanged = true;
        for (UserItemFragment fragment : mFragmentList) {
            if (fragment != null) {
                fragment.setFirst(true);
            }
        }
        mUserBean = userBean;
        mToUid = userBean.getId();
        if ("0".equals(mToUid)) {
            if (mOtherGroup.getVisibility() == View.VISIBLE) {
                mOtherGroup.setVisibility(View.INVISIBLE);
            }
        } else {
            if (!AppConfig.getInstance().isLogin() || !AppConfig.getInstance().getUid().equals(uid)) {
                if (mOtherGroup.getVisibility() != View.VISIBLE) {
                    mOtherGroup.setVisibility(View.VISIBLE);
                }
            } else {
                if (mOtherGroup.getVisibility() == View.VISIBLE) {
                    mOtherGroup.setVisibility(View.INVISIBLE);
                }
            }
        }
        showUserInfo();
        showAttention(isAttention);
        showMore(userBean.getId());
        if (mFragmentList != null) {
            for (UserItemFragment userItemFragment : mFragmentList) {
                if (userItemFragment != null) {
                    userItemFragment.setUid(userBean.getId());
                    userItemFragment.clearData();
                }
            }

        }
        expand();
        setFirstCurrentItem();

    }

    private void setFirstCurrentItem() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(0, false);
        }
    }

    public void setOnBackClickListener(OnBackClickListener listener) {
        mOnBackClickListener = listener;
    }

    private void showAttention(int isAttention) {
        if (mBtnFollowing != null && mBtnAddFollow != null) {
            if (isAttention == 0) {
                if (mBtnFollowing.getVisibility() == View.VISIBLE) {
                    mBtnFollowing.setVisibility(View.GONE);
                }
                if (mBtnAddFollow.getVisibility() != View.VISIBLE) {
                    mBtnAddFollow.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnAddFollow.getVisibility() == View.VISIBLE) {
                    mBtnAddFollow.setVisibility(View.GONE);
                }
                if (mBtnFollowing.getVisibility() != View.VISIBLE) {
                    mBtnFollowing.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showMore(String uid) {
        if ("0".equals(uid)) {
            if (mBtnMore.getVisibility() == View.VISIBLE) {
                mBtnMore.setVisibility(View.INVISIBLE);
            }
            return;
        }
        if (!TextUtils.isEmpty(uid)) {
            if (!AppConfig.getInstance().isLogin() || !uid.equals(AppConfig.getInstance().getUid())) {
                if (mBtnMore.getVisibility() != View.VISIBLE) {
                    mBtnMore.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnMore.getVisibility() == View.VISIBLE) {
                    mBtnMore.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View paramView) {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastTime < 1000) {
            return;
        }
        mLastTime = curTime;
        switch (paramView.getId())
        {
            case R.id.follow:
                forwardFollow();
                return;
            case R.id.fans:
                forwardFans();
                return;
            case R.id.btn_setting:
                forwardSetting();
                return;
            case R.id.btn_private_msg:
                sendPrivateMsg();
                return;
            case R.id.btn_more:
                openMoreWindow();
                return;
            case R.id.btn_edit_profile:
                forwardEditProfile();
                return;
        }
        changeFollow();
    }

    /**
     * 打开更多的弹窗
     */
    private void openMoreWindow() {
        if (AppConfig.getInstance().isLogin()) {
            HttpUtil.checkBlack(mToUid, mCheckBlackCallback);
        } else {
            LoginActivity.forwardLogin(mContext);
        }
    }

    private HttpCallback mCheckBlackCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String black = obj.getIntValue("u2t") == 0 ? WordUtil.getString(R.string.black) : WordUtil.getString(R.string.cancel_black);
                DialogUitl.showUserMoreDialog(mContext,
                        new String[]{WordUtil.getString(R.string.report), black},
                        new int[]{0xff1271FB, 0xffff0000}, new DialogUitl.StringArrayDialogCallback() {
                            @Override
                            public void onItemClick(String text, int position) {
                                if (position == 0) {
                                    reportUser();
                                } else {
                                    setBlack();
                                }
                            }
                        }
                );
            }
        }
    };

    /**
     * 举报用户
     */
    private void reportUser() {
        String url = AppConfig.HOST + "/index.php?g=Appapi&m=Userreport&a=index&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken() + "&touid=" + mToUid;
        Intent intent = new Intent(mContext, WebUploadImgActivity.class);
        intent.putExtra(Constants.URL, url);
        startActivity(intent);
    }

    /**
     * 拉黑对方或取消拉黑
     */
    private void setBlack() {
        HttpUtil.setBlack(mToUid, mSetBlackCallback);
    }

    private HttpCallback mSetBlackCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                int res = JSON.parseObject(info[0]).getIntValue("isblack");
                if (res == 1) {//已拉黑
                    ToastUtil.show(WordUtil.getString(R.string.set_black_success));
                    //拉黑的时候把关注取消
                    EventBus.getDefault().post(new FollowEvent(mToUid, 0));
                } else if (res == 0) {//解除拉黑
                    ToastUtil.show(getString(R.string.cancel_black_success));
                }
            }
        }
    };

    /**
     * 前往粉丝列表
     */
    private void forwardFans() {
        if (mUserBean != null) {
            Intent intent = new Intent(mContext, FansActivity.class);
            intent.putExtra(Constants.TO_UID, mUserBean.getId());
            startActivity(intent);
        }
    }

    /**
     * 前往关注列表
     */
    private void forwardFollow() {
        if (mUserBean != null) {
            Intent intent = new Intent(mContext, FollowActivity.class);
            intent.putExtra(Constants.TO_UID, mUserBean.getId());
            startActivity(intent);
        }
    }

    /**
     * 前往设置
     */
    private void forwardSetting() {
        startActivity(new Intent(mContext, SettingActivity.class));
    }


    /**
     * 前往编辑资料
     */
    private void forwardEditProfile() {
       startActivity(new Intent(mContext, EditProfileActivity.class));
    }

    /**
     * 添加或取消关注
     */
    private void changeFollow() {
        if (AppConfig.getInstance().isLogin()) {
            if (mUserBean != null) {
                HttpUtil.setAttention(mUserBean.getId(), null);
            }
        } else {
            LoginActivity.forwardLogin(mContext);
        }
    }


    /**
     * 发私信
     */
    private void sendPrivateMsg() {
        if (!AppConfig.getInstance().isLogin()) {
            LoginActivity.forwardLogin(mContext);
        } else {
            if (mUserBean != null) {
                ChatActivity.forwardChatRoom(mContext, mUserBean);
            }
        }
    }


    public interface OnBackClickListener {
        void onBackClick();
    }

    public void loadData() {
        if (!mIsMainUserCenter) {
            if (mUserBean != null) {
                HttpUtil.getUserHome(mUserBean.getId(), mGetUserHomeCallback);
            }
            if (mFragmentList != null && mFragmentList.size() > 0) {
                if (mUserChanged) {
                    mUserChanged = false;
                    mFragmentList.get(0).loadData();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPaused) {
            mPaused = false;
            if (!mIsMainUserCenter || AppConfig.getInstance().isLogin()) {
                if (mNeedRefresh) {
                    mNeedRefresh = false;
                    if (mUserBean != null) {
                        HttpUtil.getUserHome(mUserBean.getId(), mGetUserHomeCallback);
                    }
                }
            }
        }
    }

    public void onWorkCountChanged(int workCount) {
        if (mIsMainUserCenter) {
            if (mUserBean != null && workCount > mUserBean.getWorkVideos()) {
                HttpUtil.getUserHome(AppConfig.getInstance().getUid(), mGetUserHomeCallback);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpUtil.GET_USER_HOME);
        HttpUtil.cancel(HttpUtil.CHECK_BLACK);
        HttpUtil.cancel(HttpUtil.SET_BLACK);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mUserBean != null && e.getTouid().equals(mUserBean.getId())) {
            int isAttention = e.getIsAttention();
            showAttention(isAttention);
            if (mFans != null) {
                if (isAttention == 0) {
                    int fans = mUserBean.getFans() - 1;
                    mUserBean.setFans(fans);
                    mFans.setText(fans + " " + WordUtil.getString(R.string.fans));
                } else {
                    int fans = mUserBean.getFans() + 1;
                    mUserBean.setFans(fans);
                    mFans.setText(fans + " " + WordUtil.getString(R.string.fans));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedRefreshEvent(NeedRefreshEvent e) {
        mNeedRefresh = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginUserChangedEvent(LoginUserChangedEvent e) {
        if (mIsMainUserCenter) {
            if (mFragmentList != null) {
                for (UserItemFragment itemFragment : mFragmentList) {
                    if (itemFragment != null) {
                        itemFragment.onLoginUserChanged(e.getUid());
                    }
                }
            }
            setFirstCurrentItem();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        mNeedRefresh = true;
    }

    private void initAnim() {
        mInAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f),
                PropertyValuesHolder.ofFloat("translationX", 100f, 0f));
        mOutAnim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("alpha", 1f, 0f),
                PropertyValuesHolder.ofFloat("translationX", 0f, 100f));
        LayoutTransition mTransition = new LayoutTransition();
        mTransition.setDuration(LayoutTransition.CHANGE_APPEARING, 100);
        mTransition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 200);
        mTransition.setDuration(LayoutTransition.APPEARING, 200);
        mTransition.setDuration(LayoutTransition.DISAPPEARING, 100);
        //-----------------------设置动画--------------------
        mTransition.setAnimator(LayoutTransition.APPEARING, mInAnim);
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, mOutAnim);
        //---------------------------------------------------
        mTransition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        mTransition.setStartDelay(LayoutTransition.APPEARING, 0);
        mTransition.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        mTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 50);
        //----viewgroup绑定----
        mOtherGroup.setLayoutTransition(mTransition);
    }


}
