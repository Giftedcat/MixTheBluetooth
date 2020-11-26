package com.hc.mixthebluetooth.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewByIds;
import com.hc.basiclibrary.titleBasic.DefaultNavigationBar;
import com.hc.basiclibrary.viewBasic.BasActivity;
import com.hc.basiclibrary.viewBasic.manage.ViewPagerManage;
import com.hc.basiclibrary.viewBasic.tool.IMessageInterface;
import com.hc.bluetoothlibrary.DeviceModule;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.single.HoldBluetooth;
import com.hc.mixthebluetooth.customView.UnderlineTextView;
import com.hc.mixthebluetooth.fragment.FragmentCustom;
import com.hc.mixthebluetooth.fragment.FragmentLog;
import com.hc.mixthebluetooth.fragment.FragmentMessage;
import com.hc.mixthebluetooth.fragment.FragmentThree;
import com.hc.mixthebluetooth.recyclerData.itemHolder.FragmentLogItem;
import com.hc.mixthebluetooth.recyclerData.itemHolder.FragmentMessageItem;

import java.util.List;

public class CommunicationActivity extends BasActivity {

    public static final int FRAGMENT_THREE_HIDE = 0x00;
    public static final int FRAGMENT_CUSTOM_HIDE = 0x01;
    public static final int FRAGMENT_UNHIDDEN = 0x02;

    public static final int DATA_TO_MODULE = 0x03;

    public static final int FRAGMENT_STATE_1 = 0x04;
    public static final int FRAGMENT_STATE_2 = 0x05;

    public static final int FRAGMENT_STATE_DATA = 0x06;
    public static final int FRAGMENT_STATE_NUMBER = 0x07;
    public static final int FRAGMENT_STATE_CONNECT_STATE = 0x08;
    public static final int FRAGMENT_STATE_SEND_SEND_TITLE = 0x09;
    public static final int FRAGMENT_STATE_LOG_MESSAGE = 0x011;
    public static final int FRAGMENT_STATE_SERVICE_VELOCITY = 0x13;//读取实时速度

    private final String CONNECTED = "已连接",CONNECTING = "连接中",DISCONNECT = "断线了";

    @ViewByIds(value = {R.id.one,R.id.log,R.id.two,R.id.three},name = {"mMessTV","mLogTV","mCustomTV","mOtherTV"})
    private UnderlineTextView mMessTV,mLogTV,mCustomTV,mOtherTV;//四个滑动标题

    private UnderlineTextView mUnderlineTV;//滑动标题暂存

    @ViewById(R.id.communication_fragment)
    private ViewPager mViewPager;

    private DefaultNavigationBar mTitle;

    private List<DeviceModule> modules;
    private HoldBluetooth mHoldBluetooth;

    private IMessageInterface mMessage,mCustom,mThree,mLog;

    private DeviceModule mErrorDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        setContext(this);
    }

    @Override
    public void initAll() {
        mHoldBluetooth = HoldBluetooth.getInstance();
        initTitle();
        initDataListener();
        initFragment();
        mUnderlineTV = mMessTV.setState(true);
    }


    private Handler mFragmentHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            FragmentMessageItem item = (FragmentMessageItem) msg.obj;
            mHoldBluetooth.sendData(item.getModule(),item.getByteData().clone());
            return false;
        }
    });



    @OnClick({R.id.one,R.id.two,R.id.three,R.id.log})
    private void onClick(View view){
        //把这个按钮，触发点击事件，并存下到mUnderlineTV中，等下次触发另外按钮时，再复位所保存的按钮
        UnderlineTextView underlineTextView = (UnderlineTextView) view;
        if (mUnderlineTV != null)
            mUnderlineTV.setState(false);
        underlineTextView.setState(true);
        mUnderlineTV = underlineTextView;
        if (mCustom != null)
            mCustom.updateState(FRAGMENT_CUSTOM_HIDE);
        if (mThree != null)
            mThree.updateState(FRAGMENT_THREE_HIDE);
        switch (view.getId()){
            case R.id.one:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.two:
                mViewPager.setCurrentItem(1);
                if (mCustom != null)
                    mCustom.updateState(FRAGMENT_UNHIDDEN);//设置该页面非隐藏
                break;
            case R.id.three:
                mViewPager.setCurrentItem(2);
                if (mThree != null)
                    mThree.updateState(FRAGMENT_UNHIDDEN);//设置该页面非隐藏
                break;
            case R.id.log:
                mViewPager.setCurrentItem(3);
        }
    }


    private void initFragment() {
        ViewPagerManage manage = new ViewPagerManage(mViewPager);

        //获取Fragment的接口，方便操作数据
        mMessage = (IMessageInterface) manage.addFragment(new FragmentMessage());
        mCustom = (IMessageInterface) manage.addFragment(new FragmentCustom());
        mThree = (IMessageInterface) manage.addFragment(new FragmentThree());

        //传入Handler方便Fragment数据回传
        mMessage.setHandler(mFragmentHandler);
        mCustom.setHandler(mFragmentHandler);

        //传过去头部，主要是为了获取连接状况
        mMessage.readData(FRAGMENT_STATE_SEND_SEND_TITLE,mTitle,null);
        mCustom.readData(FRAGMENT_STATE_SEND_SEND_TITLE,mTitle,null);
        if (mHoldBluetooth.isDevelopmentMode()) {
            mLog = (IMessageInterface) manage.addFragment(new FragmentLog());
            mLogTV.setVisibility(View.VISIBLE);
        }

        manage.setDuration(400);//控制ViewPager速度，400ms
        manage.setPositionListener(new ViewPagerManage.PositionListener() {
            @Override
            public void onPageSelected(int position) {
                if (mUnderlineTV != null)
                    mUnderlineTV.setState(false);

                if (mCustom != null){
                    if (position == 1){
                        mCustom.updateState(FRAGMENT_UNHIDDEN);
                    }else {
                        mCustom.updateState(FRAGMENT_CUSTOM_HIDE);
                    }
                }

                if (mThree != null){
                    if (position == 2){//通知是否被选中，处于显示的状态
                        mThree.updateState(FRAGMENT_UNHIDDEN);
                    }else {
                        mThree.updateState(FRAGMENT_THREE_HIDE);
                    }
                }

                switch (position){
                    case 0:
                        mUnderlineTV = mMessTV.setState(true);
                        break;
                    case 1:
                        mUnderlineTV = mCustomTV.setState(true);
                        break;
                    case 2:
                        mUnderlineTV = mOtherTV.setState(true);
                        break;
                    case 3:
                        if (mLog != null)
                            mUnderlineTV = mLogTV.setState(true);
                        break;
                }
            }
        });
        mViewPager.setAdapter(manage.getAdapter());
        mViewPager.setOffscreenPageLimit(4);
    }

    //初始化蓝牙数据的监听
    private void initDataListener() {
        HoldBluetooth.OnReadDataListener dataListener = new HoldBluetooth.OnReadDataListener() {
            @Override
            public void readData(String mac, byte[] data) {
                if (modules != null && modules.size()>0) {
                    mMessage.readData(FRAGMENT_STATE_DATA, modules.get(0), data);
                    mCustom.readData(FRAGMENT_STATE_DATA, modules.get(0), data);
                }
            }

            @Override
            public void reading(boolean isStart) {
                if (isStart)
                    mMessage.updateState(CommunicationActivity.FRAGMENT_STATE_1);
                else
                    mMessage.updateState(CommunicationActivity.FRAGMENT_STATE_2);
            }

            @Override
            public void connectSucceed() {
                modules = mHoldBluetooth.getConnectedArray();
                mMessage.readData(FRAGMENT_STATE_DATA, modules.get(0), null);
                mThree.readData(FRAGMENT_STATE_DATA,modules.get(0),null);
                setState(CONNECTED);//设置连接状态
                log("连接成功: "+modules.get(0).getName());
            }

            @Override
            public void errorDisconnect(final DeviceModule deviceModule) {//蓝牙异常断开
                if (mErrorDisconnect == null) {//判断是否已经重复连接
                    mErrorDisconnect = deviceModule;
                    if (mHoldBluetooth != null && deviceModule != null) {
                        mFragmentHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHoldBluetooth.connect(deviceModule);
                                setState(CONNECTING);//设置正在连接状态
                            }
                        },2000);
                        return;
                    }
                }
                setState(DISCONNECT);//设置断开状态
                if (deviceModule != null)
                    toast("连接" + deviceModule.getName() + "失败，点击右上角的已断线可尝试重连", Toast.LENGTH_LONG);
                else
                    toast("连接模块失败，请返回上一个页面重连", Toast.LENGTH_LONG);
            }

            @Override
            public void readNumber(int number) {
                mMessage.readData(FRAGMENT_STATE_NUMBER, number, null);
            }

            @Override
            public void readLog(String className, String data, String lv) {
                //拿到日志
                if (mLog != null)
                    mLog.readData(FRAGMENT_STATE_LOG_MESSAGE,new FragmentLogItem(className,data,lv),null);
            }

            @Override
            public void readVelocity(int velocity) {
                if (mMessage != null)
                    mMessage.readData(FRAGMENT_STATE_SERVICE_VELOCITY,velocity,null);
            }
        };
        mHoldBluetooth.setOnReadListener(dataListener);
    }

    private void initTitle() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = ((TextView) v).getText().toString();
                if (str.equals(CONNECTED)){
                    if (modules != null && mHoldBluetooth != null) {
                        mHoldBluetooth.tempDisconnect(modules.get(0));
                        setState(DISCONNECT);//设置断线状态
                    }
                }else if (str.equals(DISCONNECT)){
                    if ((modules != null || mErrorDisconnect != null) && mHoldBluetooth != null){
                        mHoldBluetooth.connect(modules!= null&&modules.get(0)!=null?modules.get(0):mErrorDisconnect);
                        log("开启连接动画..");
                        setState(CONNECTING);//设置正在连接状态
                    }else {
                        toast("连接失败...",Toast.LENGTH_SHORT);
                        setState(DISCONNECT);//设置断线状态
                    }
                }
            }
        };
        mTitle = new DefaultNavigationBar
                .Builder(this,(ViewGroup)findViewById(R.id.communication_name))
                .setTitle("HC蓝牙助手")
                .setRightText(CONNECTING)
                .setRightClickListener(listener)
                .builer();
        mTitle.updateLoadingState(true);
    }

    private void setState(String state){
        switch (state){
            case CONNECTED://连接成功
                mTitle.updateRight(CONNECTED);
                mThree.readData(FRAGMENT_STATE_CONNECT_STATE,CONNECTED,null);
                mErrorDisconnect = null;
                break;

            case CONNECTING://连接中
                mTitle.updateRight(CONNECTING);
                mTitle.updateLoadingState(true);
                mThree.readData(FRAGMENT_STATE_CONNECT_STATE,CONNECTING,null);
                break;

            case DISCONNECT://连接断开
                mTitle.updateRight(DISCONNECT);
                mThree.readData(FRAGMENT_STATE_CONNECT_STATE,DISCONNECT,null);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (modules != null)
            mHoldBluetooth.disconnect(modules.get(0));
    }
}
