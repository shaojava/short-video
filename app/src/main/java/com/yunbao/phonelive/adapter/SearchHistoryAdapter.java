package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cxf on 2018/7/31.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.Vh> {

    private LinkedList<String> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener1;
    private View.OnClickListener mOnClickListener2;
    private ActionListener mActionListener;

    public SearchHistoryAdapter(Context context) {
        mList = new LinkedList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mActionListener != null) {
                        mActionListener.onItemClick(mList.get(position));
                    }
                }
            }
        };
        mOnClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    mList.remove(position);
                    notifyItemRemoved(position);
                    int size = mList.size();
                    notifyItemRangeChanged(position, size, "payload");
                    if (mActionListener != null) {
                        mActionListener.onListSizeChanged(size);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < size; i++) {
                            sb.append(mList.get(i));
                            if (i < size - 1) {
                                sb.append("/");
                            }
                        }
                        mActionListener.onContentChanged(sb.toString());
                    }
                }
            }
        };
    }

    public boolean clear() {
        if (mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void insertList(List<String> list) {
        int position = mList.size();
        mList.addAll(list);
        notifyItemRangeInserted(position, list.size());
    }

    public void insertItem(String key) {
        if (mList.contains(key)) {
            return;
        }
        if (mList.size() >= 6) {
            mList.removeLast();
        }
        mList.addFirst(key);
        notifyDataSetChanged();
        int size = mList.size();
        if (mActionListener != null) {
            mActionListener.onListSizeChanged(size);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(mList.get(i));
                if (i < size - 1) {
                    sb.append("/");
                }
            }
            mActionListener.onContentChanged(sb.toString());
        }
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mText;
        View mBtnClose;
        View mLine;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);
            mBtnClose = itemView.findViewById(R.id.btn_close);
            mLine = itemView.findViewById(R.id.line);
            itemView.setOnClickListener(mOnClickListener1);
            mBtnClose.setOnClickListener(mOnClickListener2);
        }

        void setData(String s, int position, Object payload) {
            itemView.setTag(position);
            mBtnClose.setTag(position);
            if (payload == null) {
                mText.setText("#" + s);
            }
            if (position == mList.size() - 1) {
                if (mLine.getVisibility() == View.VISIBLE) {
                    mLine.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mLine.getVisibility() != View.VISIBLE) {
                    mLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public interface ActionListener {
        void onItemClick(String s);

        void onListSizeChanged(int size);

        void onContentChanged(String searchHistory);

    }
}
