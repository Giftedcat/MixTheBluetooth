package com.hc.basiclibrary.ioc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ViewUtils {

    public static void inject(Activity activity){
        inject(new Finder(activity),activity);
    }

    public static void inject(View view){
        inject(new Finder(view),view);
    }

    public static void inject(View view,Object object){
        inject(new Finder(view),object);
    }

    private static void inject(Finder finder,Object object){
        injectFiled(finder, object);//绑定熟悉
        injectMultiAttribute(finder,object);
        injectEvent(finder, object);//绑定点击事件
        injectLongEvent(finder, object);//长按事件
    }

    private static void injectLongEvent(Finder finder, Object object) {
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            OnLongClick onLongClick = method.getAnnotation(OnLongClick.class);
            if (onLongClick != null){
                int[] viewIds = onLongClick.value();
                for (int viewId : viewIds) {
                    View view = finder.findViewById(viewId);
                    if (view != null){
                        view.setOnLongClickListener(new DeclaredOnLongClickListener(object,method));
                    }
                }
            }
        }
    }

    private static class DeclaredOnLongClickListener implements View.OnLongClickListener {
        private Object mObject;
        private Method mMethod;
        public DeclaredOnLongClickListener(Object object, Method method) {
            this.mMethod = method;
            this.mObject = object;
        }

        @Override
        public boolean onLongClick(View v) {
            try {
                mMethod.setAccessible(true);
                mMethod.invoke(mObject, null);
            } catch (Exception e) {
                try {
                    mMethod.invoke(mObject,v);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            return true;
        }
    }



    private static void injectEvent(Finder finder, Object object) {
        Class<?> clazz = object.getClass();
        Method [] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);

            if (onClick != null) {
                int[] viewIds = onClick.value();

                for (int viewId : viewIds) {
                    View view = finder.findViewById(viewId);
                    boolean isCheckNet = method.getAnnotation(CheckNet.class) != null;
                    boolean isCheckWifi = method.getAnnotation(CheckWifi.class) != null;
                    if (view != null)
                    view.setOnClickListener(new DeclaredOnClickListener(object, method, isCheckNet,isCheckWifi));
                }
            }
        }

    }

    private static class DeclaredOnClickListener implements View.OnClickListener {

        private Object mObject;
        private Method mMethod;
        private boolean mIsCheckNet;
        private boolean mIsCheckWifi;

        public DeclaredOnClickListener(Object object, Method method, boolean isCheckNet, boolean isCHeckWifi) {
            this.mMethod = method;
            this.mObject = object;
            this.mIsCheckNet = isCheckNet;
            this.mIsCheckWifi = isCHeckWifi;
        }

        @Override
        public void onClick(View v) {

            // 需不需要检测网络
            if (mIsCheckNet) {
                // 需要
                if (!networkAvailable(v.getContext())) {
                    Toast.makeText(v.getContext(), "没有网络可配不了网哦", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            if (mIsCheckWifi){
                if(!isWifiEnable(v.getContext())){
                    Toast.makeText(v.getContext(), "现在没有打开WiFi，无法扫描局域网内的模块", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                mMethod.setAccessible(true);
                mMethod.invoke(mObject,null);
            } catch (Exception e) {
                try {
                    mMethod.invoke(mObject,v);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (!e.toString().equals("java.lang.IllegalArgumentException: Wrong number of arguments; expected 1, got 0"))
                e.printStackTrace();
            }
        }
    }

    private static void injectFiled(Finder finder, Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ViewById viewById = field.getAnnotation(ViewById.class);
            if (viewById != null){
                int viewId = viewById.value();
                View view = finder.findViewById(viewId);
                if (view != null){
                    field.setAccessible(true);
                    try {
                        field.set(object,view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectMultiAttribute(Finder finder, Object object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ViewByIds viewByIds = field.getAnnotation(ViewByIds.class);
            if (viewByIds != null){
                int [] value = viewByIds.value();
                String[] names = viewByIds.name();
                int number = 0;
                View view = null;
                for (String name : names) {
                    if (name.equals(field.getName())){
                        view = finder.findViewById(value[number]);
                    }
                    ++number;
                }
                if (view != null){
                    field.setAccessible(true);
                    try {
                        field.set(object,view);
                        if (field.getName().equals("mSetContent"))
                            Log.w("AppRun","mSetContent field is "+field.toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * 判断当前网络是否可用
     */
    private static boolean networkAvailable(Context context) {
        // 得到连接管理器对象
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断是否打开WiFi
    private static  boolean isWifiEnable(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);//获取状态
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();//判断wifi已连接的条件
        if(wifi == NetworkInfo.State.CONNECTED||wifi== NetworkInfo.State.CONNECTING)
            return true;
        else
            return false;
    }

}
