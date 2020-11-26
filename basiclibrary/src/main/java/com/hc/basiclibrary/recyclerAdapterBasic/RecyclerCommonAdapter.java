package com.hc.basiclibrary.recyclerAdapterBasic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by xngly on 2019/4/12.
 */

public abstract class RecyclerCommonAdapter<DATA> extends RecyclerView.Adapter<ViewHolder>{
    //条目不一样那么只能通过参数传递
    private  int mLayoutId;
    //参数通用那么就只能泛型
    protected List<DATA> mData;
    //实例化View的LayoutInflater
    private LayoutInflater mInflater;

    protected  Context mContext;

    private  MulitiTypeSupport mTypeSupport;

    public  RecyclerCommonAdapter(Context context,List<DATA> data,int layoutId){
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    public RecyclerCommonAdapter(Context context,List<DATA> data,MulitiTypeSupport typeSupport){
        this(context,data,-1);
        this.mTypeSupport = typeSupport;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mTypeSupport != null){
            //需要启用多条目布局
            mLayoutId = viewType;
        }

        //创建View context
        View itemView = mInflater.inflate(mLayoutId,parent,false);
        //实例化View的方式有多种
        //View.inflate(mContext,mLayoutId,null);
        //LayoutInflater.from(mContext).inflate(mLayoutId,parent);
        //LayoutInflater.from(mContext).inflate(mLayoutId,parent,false)
        return new ViewHolder(itemView);
    }

    //这个方法会在onCreateViewHolder方法调用前启动 --归属多条目布局
    @Override
    public int getItemViewType(int position) {
        if(mTypeSupport != null){
            return mTypeSupport.getLayoutId(mData.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //ViewHolder优化
        convert(holder,mData.get(position),position,mItemClickListener);

        //条目点击事件
        if(mItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(position,view);
                }
            });
        }

        if(mItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mItemLongClickListener.onItemLongClick(position);
                }
            });
        }
    }

    //把必要的参数传出去
    //holder   -> ViewHolder
    //item     -> 当前位置的条目
    //position -> 当前位置
    protected abstract void convert(ViewHolder holder, DATA item, int position,ItemClickListener itemClickListener);


    @Override
    public int getItemCount() {
        return mData.size();
    }

    //只能利用接口回调点击
    private ItemClickListener mItemClickListener;//点击
    private ItemLongClickListener mItemLongClickListener;//长按

    public  void setOnItemClickListener(ItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }

    public  void  setOnItemLongClickListener(ItemLongClickListener longClickListener){
        this.mItemLongClickListener = longClickListener;
    }
}
