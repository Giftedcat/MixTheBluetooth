package com.hc.basiclibrary.recyclerAdapterBasic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xngly on 2019/3/24.
 */

//ListView样式的分隔线
public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration{
    //还有一种，用的是 系统属性，android.R.attrs.listDriver
    private Drawable mDrawable;
    public LinearLayoutItemDecoration(Context context,int drawableResId){
        //获取Drawable
        mDrawable = ContextCompat.getDrawable(context,drawableResId);

    }


    //这个是预留位置
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //parent.getChildCount()是不断变化的
        //outRect.bottom =10;代表在底部的位置流出10px来绘制分割线

        int position = parent.getChildAdapterPosition(view);
        //给除了第一行，其余每一行的顶部添加一个留空
        if(position != 0){
            outRect.top = mDrawable.getIntrinsicHeight();
        }
    }


    //绘制的方法
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        //利用canvas绘图

        //在每一个item的头部绘制，以下所有
        int chidCount = parent.getChildCount();//获得列表的排列数

        //指定绘制的区域,分隔线的底部是Item的头部
        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 1; i<chidCount;i++){
            rect.bottom = parent.getChildAt(i).getTop();
            rect.top = rect.bottom -mDrawable.getIntrinsicHeight();

            mDrawable.setBounds(rect);
            mDrawable.draw(canvas);
        }

    }

}
