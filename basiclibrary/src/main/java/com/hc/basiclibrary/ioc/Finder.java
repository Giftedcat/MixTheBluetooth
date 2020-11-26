package com.hc.basiclibrary.ioc;

import android.app.Activity;
import android.view.View;

public class Finder {

    private Activity mActivity;
    private View mView;

    public Finder(Activity activity) {
        this.mActivity = activity;
    }

    public Finder(View view) {
        this.mView = view;
    }

    public View findViewById(int viewId){
        return mActivity != null?mActivity.findViewById(viewId):mView.findViewById(viewId);
    }

}
