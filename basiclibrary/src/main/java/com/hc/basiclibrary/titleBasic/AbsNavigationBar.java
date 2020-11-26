package com.hc.basiclibrary.titleBasic;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xngly on 2019/5/7.
 * 头部的基类
 */

public abstract class AbsNavigationBar<P extends AbsNavigationBar.Builder.AbsNavigationParams> implements INavigationbar{

    private P mParams;

    private View mNavigationView;

    public AbsNavigationBar(P params){
        this.mParams = params;
        createAndBindView();
    }

    public P getParams() {
        return mParams;
    }

    protected void setText(int viewId, String text) {
        TextView tv =mNavigationView.findViewById(viewId);
        if(!TextUtils.isEmpty(text)){
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
        }
    }

    protected View getChildView(int id){
        return findViewById(id);
    }

    protected void setVisibility(int viewId, int visibility) {
        findViewById(viewId).setVisibility(visibility);
    }

    protected void setOnClickListener(int viewId,View.OnClickListener listener){
        findViewById(viewId).setOnClickListener(listener);
    }

    public <T extends View> T findViewById(int viewId){
        return (T)mNavigationView.findViewById(viewId);
    }

    //绑定和创建View
    private void createAndBindView() {
        //创建View
        mNavigationView = LayoutInflater.from(mParams.mContext).
                inflate(bindLayoutId(),mParams.mParent,false);

        //添加
        if (mParams == null)
            Log.w("AppRun","mParams == null");
        mParams.mParent.addView(mNavigationView,0);

        applyView();
    }




    public abstract static class Builder{

        AbsNavigationParams p;

        public Builder(Context context, ViewGroup parent){
            p = new AbsNavigationParams(context,parent);
        }

        public abstract AbsNavigationBar builer();

        public static class AbsNavigationParams{

            public Context mContext;
            public ViewGroup mParent;

            public AbsNavigationParams(Context context,ViewGroup parent){
                this.mContext = context;
                this.mParent = parent;
            }

        }
    }
}
