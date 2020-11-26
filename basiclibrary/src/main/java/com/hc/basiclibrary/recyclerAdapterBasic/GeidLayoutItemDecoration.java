package com.hc.basiclibrary.recyclerAdapterBasic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hc.basiclibrary.R;


/**
 * Created by xngly on 2019/3/24.
 */

//ListView样式的分隔线
public class GeidLayoutItemDecoration extends RecyclerView.ItemDecoration{
    //还有一种，用的是 系统属性，android.R.attrs.listDriver
    private Drawable mDrawable;
    private Paint mPaint;
    @SuppressLint("ResourceAsColor")
    public GeidLayoutItemDecoration(Context context, int drawableResId){

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(R.color.line);
        //获取Drawable
        mDrawable = ContextCompat.getDrawable(context,drawableResId);

    }


    //这个是预留位置
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //留出分隔线，下边与右边
        outRect.bottom = mDrawable.getIntrinsicHeight();
        outRect.right = mDrawable.getIntrinsicWidth();
        outRect.left =5;
        outRect.top = 40;

        //不留最下边和最右边

//        int bottom = mDrawable.getIntrinsicHeight();
//        int right =mDrawable.getIntrinsicWidth();
//
//        if(isLastCloumn(view,parent)){//最后一列  当前的位置%列数 == 0
//            right =0;
//        }
//        if(isLastRow(view,parent)){//最后一行
//            bottom = 0;
//        }

//        outRect.bottom = bottom;
//        outRect.right = right;
    }


    //绘制的方法
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

//        int childCount = parent.getChildCount();
//
//        Rect  rect = new Rect();
//
//        for (int i=0;i<childCount;i++){
//            rect.left = parent.getPaddingLeft();
//            rect.right = parent.getWidth()-parent.getTop();
//            rect.bottom = parent.getChildAt(i).getTop();
//            rect.top = rect.bottom - 5;
//            canvas.drawRect(rect,mPaint);
//        }
//
//        for (int i=0;i<childCount;i++){
//            rect.left = parent.getPaddingLeft();
//            rect.right = parent.getWidth()-parent.getTop();
//            rect.bottom = parent.getChildAt(i).getBottom();
//            rect.top = rect.bottom + 5;
//            canvas.drawRect(rect,mPaint);
//        }

//        for (int i=0;i<childCount;i++){
//            rect.left = parent.getPaddingLeft()-10;
//            rect.right = parent.getPaddingLeft();
//            rect.bottom = parent.getChildAt(i).getBottom();
//            rect.top = parent.getChildAt(i).getHeight();
//            canvas.drawRect(rect,mPaint);
//        }

    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {

        int chilCount = parent.getChildCount();

        for(int i = 0;i<chilCount;i++){
            View chilView = parent.getChildAt(i);
            //这个是预防在布局文件中有Margin的这个属性而出现的bug。
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)chilView.getLayoutParams();

            int top = chilView.getTop() - params.topMargin;
            int bottom = chilView.getBottom()+params.bottomMargin;
            int left = chilView.getRight()+params.rightMargin;
            int right =left+mDrawable.getIntrinsicWidth();

            mDrawable.setBounds(left,top,right,bottom);
            mDrawable.draw(canvas);

        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int chilCount = parent.getChildCount();//获取总列数

        for(int i = 0;i<chilCount;i++){
            //得到当前列
            View chilView = parent.getChildAt(i);
            //这个是预防在布局文件中有Margin的这个属性而出现的bug。
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)chilView.getLayoutParams();

            //params.leftMargin，没有margin值可以不加
            int left = chilView.getLeft()-params.leftMargin;
            //mDrawable.getIntrinsicHeight()是让绘制的水平分隔线穿过垂直的分隔线（或垂直留空）
            //params.rightMargin  一样
            int right = chilView.getRight()+mDrawable.getIntrinsicHeight()+params.rightMargin;
            //params.bottomMargin 一样
            int top = chilView.getBottom()+params.bottomMargin;
            int bottom = top+mDrawable.getIntrinsicHeight();

            mDrawable.setBounds(left,top,right,bottom);
            mDrawable.draw(canvas);

        }
    }

    //判断是否为最右
    public boolean isLastCloumn(View view , RecyclerView parent) {
        //获取当前位置
        int currentPosition =((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();

        //获取列数
        int spanCount = getSpanCount(parent);

        return (currentPosition+1)%spanCount == 0;
    }

    private int  getSpanCount(RecyclerView parent) {
        //当前列数 GridLayout
        RecyclerView.LayoutManager layoutManager =parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            //返回列数
            return spanCount;
        }
        return 1;
    }
    //判断是否为最底列
    public boolean isLastRow(View view , RecyclerView parent) {

        ///列数
        int spanCount = getSpanCount(parent);
        //行数 总的条目除以列数  下面是三目运算符  ? :
        int rowNumber = parent.getAdapter().getItemCount()%spanCount ==0 ?
                parent.getAdapter().getItemCount()/spanCount:
                (parent.getAdapter().getItemCount()/spanCount+1);

        //当前的位置
        int currentPosition =((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();

        //当前的位置+1 >（行数-1）*列数
        return (currentPosition+1) >(rowNumber-1)*spanCount;
    }
}
