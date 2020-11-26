package com.hc.basiclibrary.viewBasic;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.basiclibrary.viewBasic.tool.IMessageInterface;


public abstract class BasFragment extends Fragment implements IMessageInterface {

    private Class mClass;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int id = setFragmentViewId();
        View view = LayoutInflater.from(getActivity()).inflate(id,container,false);
        ViewUtils.inject(view,this);
        initAll(view,getContext());
        return view;
    }

    public void toast(final String string){
        try {
            if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void readData(int state,Object o,byte[] data) {

    }

    @Override
    public void updateState(int state) {

    }

    public void initAll(View view, Context context){
        initAll();
    }

    public void setClass(Class clazz) {
        this.mClass = clazz;
    }

    public void log(String log){
        if (mClass == null){
            mClass = getClass();
        }
        Log.d("AppRun"+mClass.getSimpleName(),log);
    }

    public void log(String log,String lv){
        if (mClass == null){
            mClass = getClass();
        }
        if (lv.equals("e")){
            Log.e("AppRun"+mClass.getSimpleName(),log);
        }else {
            Log.w("AppRun"+mClass.getSimpleName(),log);
        }
    }

    public abstract int setFragmentViewId();

    public abstract void initAll();

}
