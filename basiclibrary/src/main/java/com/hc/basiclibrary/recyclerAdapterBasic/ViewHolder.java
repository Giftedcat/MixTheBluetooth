package com.hc.basiclibrary.recyclerAdapterBasic;


import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by xngly on 2019/4/12.
 */

public class ViewHolder extends RecyclerView.ViewHolder{
    //用于缓存已找到的界面 Map
    private SparseArray<View> mViews;
    public ViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray <>();
    }
    //因为直接返回View会导致那边需要强转，而写成泛类形式则不用
    //itemView 中包含ViewId
    //<T>   View
    public <T extends View> T getView(int viewId){
        //多次findViewById 对已有找到的View做一个缓存
        View view =mViews.get(viewId);
        if(view == null){
            view = itemView.findViewById(viewId);
            mViews.put(viewId,view);//对找到的View存于SparseArray中
        }
        return (T) view;
    }
    //对通用功能进行封装，如，设置文本，设置图片，设置条目点击事件

    public ViewHolder setText(int viewId,CharSequence text){
        TextView tv = getView(viewId);
        tv.setText(text);
        //链式调用，即返回的是对象本身，在那边即可再点一次
        //如，holder.setText(id,"").setText(id,"");
        return this;
        //附：链式调用在 Builder(建造者模式)中比较常见
    }

    public ViewHolder setTextAndColor(int viewId,CharSequence text,int color){
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setTextColor(color);
        //链式调用，即返回的是对象本身，在那边即可再点一次
        //如，holder.setText(id,"").setText(id,"");
        return this;
        //附：链式调用在 Builder(建造者模式)中比较常见
    }

    //本地设置图片资源
    public ViewHolder setImageResource(int viewId,int resource){
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resource);
        return this;
    }

    public ViewHolder setLinearLayout(int viewId,int hint){
        LinearLayout linearLayout = getView(viewId);
        linearLayout.setVisibility(hint);
        return this;
    }

    public ViewHolder setOnclickListener(int viewId, final int position, final ItemClickListener itemClickListener){
        View view = getView(viewId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(position,view);
            }
        });
        return this;
    }

    public ViewHolder setViewState(int viewId,boolean show){
        View view = getView(viewId);
        if (show){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public View getItemView(){
        return itemView;
    }

}
