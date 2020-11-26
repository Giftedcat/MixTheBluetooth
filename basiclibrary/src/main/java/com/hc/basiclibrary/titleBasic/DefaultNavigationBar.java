package com.hc.basiclibrary.titleBasic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hc.basiclibrary.R;


/**
 * Created by xngly on 2019/5/7.
 */

public class DefaultNavigationBar extends
        AbsNavigationBar<DefaultNavigationBar.Builder.DefaultNavigationParams>
        {


    public DefaultNavigationBar(Builder.DefaultNavigationParams params) {
        super(params);
    }

    /*public DefaultNavigationBar(Context mainActivity, View viewById) {
        super();
    }*/

    @Override
    public int bindLayoutId() {
        return R.layout.title_bary;
    }

    @Override
    public void applyView() {
        //绑定效果
        setText(R.id.title,getParams().mTitle);
        setText(R.id.right_text,getParams().mRightText);
        setText(R.id.left_text,getParams().mLeftText);

        setOnClickListener(R.id.right_icon,getParams().mRightClickListener);

        setOnClickListener(R.id.right_text,getParams().mRightClickListener);
        //左边 要写一个默认的 finish()
        setOnClickListener(R.id.back,getParams().mLeftClickListener);

        setOnClickListener(R.id.left_text,getParams().mLeftClickListener);

        setVisibility(R.id.back,getParams().leftIconVisible);
        setVisibility(R.id.right_text,getParams().rightText);
        setVisibility(R.id.left_text,getParams().leftTextVisible);
        setVisibility(R.id.right_icon,getParams().rightIcon);
        setVisibility(R.id.title_loading,getParams().rightLoading);

        getParams().mLoadingView = (LoadingCircleView) getChildView(R.id.title_loading);
        getParams().mRightImageView = (ImageView) getChildView(R.id.right_icon);
    }

    public void updateText(String name){
        getParams().mTitle = name;
        applyView();
    }

    public void updateRight(String data){
        getParams().mRightText = data;
        applyView();
    }

    public void updateLoadingState(boolean state){
        if (state){
            getParams().mLoadingView.setVisibility(View.VISIBLE);
            getParams().mLoadingView.start();
        }else {
            getParams().mLoadingView.setVisibility(View.GONE);
            getParams().mLoadingView.stop();
        }
    }

    public void updateRightImage(boolean state){
        if (state){
            getParams().mRightImageView.setImageResource(R.drawable.pop_click_icon);
        }else {
            getParams().mRightImageView.setImageResource(R.drawable.pop_icon);
        }
    }




    public static class Builder extends AbsNavigationBar.Builder {

        DefaultNavigationParams p;


        public Builder(Context context, ViewGroup parent) {

            super(context, parent);
            p = new DefaultNavigationParams(context,parent);



        }



        @Override
        public DefaultNavigationBar builer() {
            return new DefaultNavigationBar(p);
        }

        //设置所有效果
        public Builder setTitle(String title){
            p.mTitle = title;
            return this;
        }

        //设置右边的文本
        public Builder setRightText(String rightText){
            p.mRightText = rightText;
            p.rightText = View.VISIBLE;
            return this;
        }

        //设置左边的文本
        public Builder setLeftText(String leftTextText){
            p.mLeftText = leftTextText;
            return this;
        }

        //设置左边的文本不可见
        public Builder hideLeftText(){
            p.leftTextVisible = View.INVISIBLE;
            return this;
        }

        //设置右边的点击事件
        public Builder
            setRightClickListener(View.OnClickListener rightListener){
            p.mRightClickListener = rightListener;
            return this;
        }

        //设置左边的默认点击事件
        public Builder
        setLeftClickListener(View.OnClickListener rightListener){
            p.mLeftClickListener = rightListener;
            return this;
        }

        //设置右边的图片
        public Builder setRightIcon(){
            p.rightIcon = View.VISIBLE;
            return this;
        }
        //设置左边图片不可见
        public Builder hideLeftIcon(){
            p.leftIconVisible = View.INVISIBLE;
            return this;
        }

        //设置右边文本不可见
        public Builder hideRightText(){
            p.rightText = View.INVISIBLE;
            return this;
        }

        public Builder setFoldMenu(final int layout, final FoldMenu.OnItemClickListener listener){
            p.mRightClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FoldMenu().showPopupWindow(layout,v,listener);
                }
            };

            return this;
        }

        public static class DefaultNavigationParams extends
                AbsNavigationBar.Builder.AbsNavigationParams{

            //所有效果的放置
            public String mTitle;
            public String mRightText;
            public String mLeftText;
            public ImageView mRightImageView;
            public LoadingCircleView mLoadingView;
            public int leftTextVisible = View.VISIBLE;
            public int leftIconVisible = View.VISIBLE;
            public int rightText =View.GONE;
            public int rightIcon = View.GONE;
            public int rightLoading = View.GONE;
            public View.OnClickListener mRightClickListener;
            public View.OnClickListener mLeftClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //关闭Activity
                    ((Activity) mContext).finish();
                }
            };

            public DefaultNavigationParams(Context context, ViewGroup parent) {
                super(context, parent);
            }
        }
    }
}
