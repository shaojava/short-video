package com.yunbao.phonelive.custom.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.FilterAdapter;
import com.yunbao.phonelive.adapter.TieZhiAdapter;
import com.yunbao.phonelive.bean.FilterBean;
import com.yunbao.phonelive.bean.TieZhiBean;
import com.yunbao.phonelive.custom.ItemDecoration;
import com.yunbao.phonelive.custom.TextSeekBar;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.BitmapUtil;

import cn.tillusory.sdk.bean.TiDistortionEnum;

/**
 * Created by cxf on 2018/6/22.
 */

public class BeautyHolder implements View.OnClickListener {

    private ViewGroup mParent;
    private View mHideView;
    private View mContentView;
    private SparseArray<View> mSparseArray;
    private int mCurKey;
    private TieZhiAdapter mTieZhiAdapter;
    private FilterAdapter mFilterAdapter;
    private EffectListener mEffectListener;

    public BeautyHolder(Context context, ViewGroup parent, View hideView) {
        mParent = parent;
        mHideView = hideView;
        View v = LayoutInflater.from(context).inflate(R.layout.view_record_beauty, parent, false);
        mContentView = v;
        v.findViewById(R.id.btn_beauty).setOnClickListener(this);
        v.findViewById(R.id.btn_beauty_shape).setOnClickListener(this);
        v.findViewById(R.id.btn_meng).setOnClickListener(this);
        v.findViewById(R.id.btn_filter).setOnClickListener(this);
        v.findViewById(R.id.btn_haha).setOnClickListener(this);
        v.findViewById(R.id.btn_hide).setOnClickListener(this);
        v.findViewById(R.id.btn_haha_0).setOnClickListener(this);
        v.findViewById(R.id.btn_haha_1).setOnClickListener(this);
        v.findViewById(R.id.btn_haha_2).setOnClickListener(this);
        v.findViewById(R.id.btn_haha_3).setOnClickListener(this);
        v.findViewById(R.id.btn_haha_4).setOnClickListener(this);
        mSparseArray = new SparseArray<>();
        mSparseArray.put(R.id.btn_beauty, v.findViewById(R.id.group_beauty));
        mSparseArray.put(R.id.btn_beauty_shape, v.findViewById(R.id.group_beauty_shape));
        mSparseArray.put(R.id.btn_meng, v.findViewById(R.id.group_meng));
        mSparseArray.put(R.id.btn_filter, v.findViewById(R.id.group_filter));
        mSparseArray.put(R.id.btn_haha, v.findViewById(R.id.group_haha));
        mCurKey = R.id.btn_beauty;
        ((TextSeekBar) v.findViewById(R.id.seek_meibai)).setOnSeekChangeListener(mOnSeekChangeListener);
        ((TextSeekBar) v.findViewById(R.id.seek_mopi)).setOnSeekChangeListener(mOnSeekChangeListener);
        ((TextSeekBar) v.findViewById(R.id.seek_baohe)).setOnSeekChangeListener(mOnSeekChangeListener);
        ((TextSeekBar) v.findViewById(R.id.seek_fengnen)).setOnSeekChangeListener(mOnSeekChangeListener);
        ((TextSeekBar) v.findViewById(R.id.seek_big_eye)).setOnSeekChangeListener(mOnSeekChangeListener);
        ((TextSeekBar) v.findViewById(R.id.seek_face)).setOnSeekChangeListener(mOnSeekChangeListener);
        //贴纸
        RecyclerView tieZhiRecyclerView = (RecyclerView) v.findViewById(R.id.tiezhi_recyclerView);
        tieZhiRecyclerView.setHasFixedSize(true);
        tieZhiRecyclerView.setLayoutManager(new GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration1 = new ItemDecoration(context, 0x00000000, 8, 8);
        decoration1.setOnlySetItemOffsetsButNoDraw(true);
        tieZhiRecyclerView.addItemDecoration(decoration1);
        mTieZhiAdapter = new TieZhiAdapter(context);
        mTieZhiAdapter.setOnItemClickListener(new OnItemClickListener<TieZhiBean>() {
            @Override
            public void onItemClick(TieZhiBean bean, int position) {
                if (mEffectListener != null) {
                    mEffectListener.onTieZhiChanged(bean.getName());
                }
            }
        });
        tieZhiRecyclerView.setAdapter(mTieZhiAdapter);
        //滤镜
        RecyclerView filterRecyclerView = (RecyclerView) v.findViewById(R.id.filter_recyclerView);
        filterRecyclerView.setHasFixedSize(true);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ItemDecoration decoration2 = new ItemDecoration(context, 0x00000000, 4, 4);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        filterRecyclerView.addItemDecoration(decoration2);
        mFilterAdapter = new FilterAdapter(context);
        mFilterAdapter.setOnItemClickListener(new OnItemClickListener<FilterBean>() {
            @Override
            public void onItemClick(FilterBean bean, int position) {
                if (mEffectListener != null) {
                    if (bean.getId() == FilterBean.FILTER_ORIGINAL) {
                        mEffectListener.onFilterChanged(null);
                    } else {
                        Bitmap bitmap = bean.getBitmap();
                        if (bitmap == null) {
                            bitmap = BitmapUtil.getInstance().decodeBitmap(bean.getFilterBitmapSrc());
                            bean.setBitmap(bitmap);
                        }
                        mEffectListener.onFilterChanged(bitmap);
                    }
                }
            }
        });
        filterRecyclerView.setAdapter(mFilterAdapter);
    }

    public void setEffectListener(EffectListener effectListener) {
        mEffectListener = effectListener;
    }

    public void show() {
        if (mParent != null && mContentView != null) {
            ViewParent parent = mContentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mContentView);
            }
            if (mHideView != null && mHideView.getVisibility() == View.VISIBLE) {
                mHideView.setVisibility(View.INVISIBLE);
            }
            mParent.addView(mContentView);
        }
    }

    private void hide() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
        if (mHideView != null && mHideView.getVisibility() != View.VISIBLE) {
            mHideView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_beauty:
            case R.id.btn_beauty_shape:
            case R.id.btn_meng:
            case R.id.btn_filter:
            case R.id.btn_haha:
                toggle(id);
                break;
            case R.id.btn_hide:
                hide();
                break;
            case R.id.btn_haha_0:
                if (mEffectListener != null) {
                    mEffectListener.onHaHaChanged(TiDistortionEnum.NO_DISTORTION);
                }
                break;
            case R.id.btn_haha_1:
                if (mEffectListener != null) {
                    mEffectListener.onHaHaChanged(TiDistortionEnum.ET_DISTORTION);
                }
                break;
            case R.id.btn_haha_2:
                if (mEffectListener != null) {
                    mEffectListener.onHaHaChanged(TiDistortionEnum.PEAR_FACE_DISTORTION);
                }
                break;
            case R.id.btn_haha_3:
                if (mEffectListener != null) {
                    mEffectListener.onHaHaChanged(TiDistortionEnum.SLIM_FACE_DISTORTION);
                }
                break;
            case R.id.btn_haha_4:
                if (mEffectListener != null) {
                    mEffectListener.onHaHaChanged(TiDistortionEnum.SQUARE_FACE_DISTORTION);
                }
                break;
        }
    }

    private void toggle(int key) {
        if (mCurKey == key) {
            return;
        }
        mCurKey = key;
        for (int i = 0, size = mSparseArray.size(); i < size; i++) {
            View v = mSparseArray.valueAt(i);
            if (mSparseArray.keyAt(i) == key) {
                if (v.getVisibility() != View.VISIBLE) {
                    v.setVisibility(View.VISIBLE);
                }
            } else {
                if (v.getVisibility() == View.VISIBLE) {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void release() {
        if (mFilterAdapter != null) {
            mFilterAdapter.clear();
        }
        if (mTieZhiAdapter != null) {
            mTieZhiAdapter.clear();
        }
    }

    private TextSeekBar.OnSeekChangeListener mOnSeekChangeListener = new TextSeekBar.OnSeekChangeListener() {
        @Override
        public void onProgressChanged(View view, int progress) {
            if (mEffectListener != null) {
                switch (view.getId()) {
                    case R.id.seek_meibai:
                        mEffectListener.onMeiBaiChanged(progress);
                        break;
                    case R.id.seek_mopi:
                        mEffectListener.onMoPiChanged(progress);
                        break;
                    case R.id.seek_baohe:
                        mEffectListener.onBaoHeChanged(progress);
                        break;
                    case R.id.seek_fengnen:
                        mEffectListener.onFengNenChanged(progress);
                        break;
                    case R.id.seek_big_eye:
                        mEffectListener.onBigEyeChanged(progress);
                        break;
                    case R.id.seek_face:
                        mEffectListener.onFaceChanged(progress);
                        break;
                }
            }
        }
    };

    public interface EffectListener {
        void onFilterChanged(Bitmap bitmap);

        void onMeiBaiChanged(int progress);

        void onMoPiChanged(int progress);

        void onBaoHeChanged(int progress);

        void onFengNenChanged(int progress);

        void onBigEyeChanged(int progress);

        void onFaceChanged(int progress);

        void onTieZhiChanged(String tieZhiName);

        void onHaHaChanged(TiDistortionEnum tiDistortionEnum);
    }

}
