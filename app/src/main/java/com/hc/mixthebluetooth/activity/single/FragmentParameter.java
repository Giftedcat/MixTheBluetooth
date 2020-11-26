package com.hc.mixthebluetooth.activity.single;

import android.content.Context;

import com.hc.mixthebluetooth.storage.Storage;

public class FragmentParameter {
    private static final FragmentParameter ourInstance = new FragmentParameter();

    public static FragmentParameter getInstance() {
        return ourInstance;
    }

    private Storage mStorage;

    private String mCodeFormat;

    private FragmentParameter() {
    }


    public void setCodeFormat(String code, Context context){
        if (mStorage == null)
            mStorage = new Storage(context);
        mCodeFormat = code;
        mStorage.saveCodedFormat(code);
    }

    public String getCodeFormat(Context context){
        if (mStorage == null && context != null)
            mStorage = new Storage(context);

        if (mCodeFormat == null && mStorage != null)
            mCodeFormat = mStorage.getCodedFormat();

        if (mCodeFormat == null)
            return "GBK";
        return mCodeFormat;
    }

}
