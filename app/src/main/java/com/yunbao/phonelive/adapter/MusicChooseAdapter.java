package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.MusicChooseBean;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import java.util.List;

public class MusicChooseAdapter extends RecyclerView.Adapter<MusicChooseAdapter.Vh>
{
    private LayoutInflater mInflater;
    private List<MusicChooseBean> mList;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<MusicChooseBean> mOnItemClickListener;

    public MusicChooseAdapter(Context paramContext, List<MusicChooseBean> paramList)
    {
        this.mList = paramList;
        this.mInflater = LayoutInflater.from(paramContext);
        this.mOnClickListener = new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                Object object = paramAnonymousView.getTag();
                if (object != null)
                {
                    MusicChooseBean musicChooseBean = (MusicChooseBean)object;
                    if (MusicChooseAdapter.this.mOnItemClickListener != null)
                        MusicChooseAdapter.this.mOnItemClickListener.onItemClick(musicChooseBean, 0);
                }
            }
        };
    }

    public int getItemCount()
    {
        return this.mList.size();
    }

    public void onBindViewHolder(Vh paramVh, int paramInt)
    {
        paramVh.setData(this.mList.get(paramInt), paramInt);
    }

    public Vh onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
        return new Vh(this.mInflater.inflate(R.layout.item_list_choose_music, paramViewGroup, false));
    }

    public void setOnItemClickListener(OnItemClickListener<MusicChooseBean> paramOnItemClickListener)
    {
        this.mOnItemClickListener = paramOnItemClickListener;
    }

    class Vh extends RecyclerView.ViewHolder
    {
        TextView mArtist;
        MusicChooseBean mBean;
        View mLine;
        int mPosition;
        TextView mTitle;

        public Vh(View arg2)
        {
            super(arg2);
            this.mTitle = ((TextView)itemView.findViewById(R.id.title));
            this.mArtist = ((TextView)itemView.findViewById(R.id.artist));
            this.mLine = itemView.findViewById(R.id.line);
            itemView.setOnClickListener(MusicChooseAdapter.this.mOnClickListener);
        }

        void setData(MusicChooseBean paramMusicChooseBean, int paramInt)
        {
            this.mBean = paramMusicChooseBean;
            this.itemView.setTag(paramMusicChooseBean);
            this.mTitle.setText(paramMusicChooseBean.getTitle());
            this.mArtist.setText(paramMusicChooseBean.getArtist());
            if (paramInt == MusicChooseAdapter.this.mList.size() - 1)
            {
                if (this.mLine.getVisibility() == View.VISIBLE)
                    this.mLine.setVisibility(View.INVISIBLE);
            }
            else if (this.mLine.getVisibility() != View.VISIBLE)
                this.mLine.setVisibility(View.VISIBLE);
        }
    }
}
