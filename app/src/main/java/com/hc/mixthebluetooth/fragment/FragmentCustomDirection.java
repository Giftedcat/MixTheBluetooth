package com.hc.mixthebluetooth.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewByIds;
import com.hc.basiclibrary.titleBasic.DefaultNavigationBar;
import com.hc.basiclibrary.viewBasic.BasFragment;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.CommunicationActivity;
import com.hc.mixthebluetooth.activity.single.FragmentParameter;
import com.hc.mixthebluetooth.activity.single.HoldBluetooth;
import com.hc.mixthebluetooth.activity.tool.Analysis;
import com.hc.mixthebluetooth.customView.CustomButtonView;
import com.hc.mixthebluetooth.customView.dialog.SetButton;
import com.hc.mixthebluetooth.recyclerData.itemHolder.FragmentMessageItem;
import com.hc.mixthebluetooth.storage.Storage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FragmentCustomDirection extends BasFragment {

    private static final String mSeparator = "//**$$/separator/$$**//";

    private  static final String TIME_ID = "0x44557788";

    @ViewById(R.id.custom_fragment_direction_linear)
    private LinearLayout mLinear;

    @ViewByIds(value = {R.id.custom_fragment_direction_top,R.id.custom_fragment_direction_left,R.id.custom_fragment_direction_middle,
            R.id.custom_fragment_direction_right,R.id.custom_fragment_direction_bottom},name = {"top","left","middle","right","bottom"})
    private TextView top,left,middle,right,bottom;

    @ViewByIds(value = {R.id.custom_fragment_direction_hex,R.id.custom_fragment_direction_set},name = {"mSendHex","mSetButton"})
    private CustomButtonView mSendHex,mSetButton;

    private DefaultNavigationBar mTitle;

    private Storage mStorage;

    private FragmentParameter mFragmentParameter;

    private Handler mHandler;

    private int mSendTime = 500;

    private boolean mIsContinueSend = false;//是否为按住持续发送

    private ExecutorService mService;

    private boolean isSend = false;

    private int mButtonMinWidth = -1;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_custom_button_direction;
    }

    @Override
    public void initAll() {

    }

    @Override
    public void initAll(View view, Context context) {
        super.initAll(view, context);
        mStorage = new Storage(context);//时间重写
        mFragmentParameter = FragmentParameter.getInstance();
        String data = mStorage.getDataString(TIME_ID);
        if (data!=null) {
            mSendTime = Integer.parseInt(data.substring(0, data.indexOf(mSeparator)));
            mIsContinueSend = data.substring(data.indexOf(mSeparator) + mSeparator.length()).equals("true");
        }
        mButtonMinWidth = mStorage.getWidth();
        setListener();
        setDirection();
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void updateState(int state) {

    }


    @Override
    public void readData(int state, Object o, byte[] data) {
        if (CommunicationActivity.FRAGMENT_STATE_SEND_SEND_TITLE == state){
            mTitle = (DefaultNavigationBar) o;
        }
    }

    private void setListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.custom_fragment_direction_set:
                        if (!mSetButton.isChick())
                            toast("单击方向按钮即可编辑按钮内容");
                        mSetButton.toggle();
                        return;
                    case R.id.custom_fragment_direction_hex:
                        mSendHex.toggle();
                        return;
                    case R.id.custom_fragment_direction_top:
                    case R.id.custom_fragment_direction_left:
                    case R.id.custom_fragment_direction_bottom:
                    case R.id.custom_fragment_direction_right:
                    case R.id.custom_fragment_direction_middle:
                        if (mSetButton.isChick()) {
                            setButtonWindow(v, true);
                            return;
                        }
                        if (mIsContinueSend)
                            return;
                }
                String data = mStorage.getDataString(String.valueOf(v.getId()));
                if (data != null) {
                    send(data.substring(data.indexOf(mSeparator) + mSeparator.length()));
                }else {
                    Toast.makeText(getContext(), "此按钮还没有初始化", Toast.LENGTH_SHORT).show();
                }
            }
        };
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setButtonWindow(v,false);
                return false;
            }
        };
        setItemClickListener(mLinear,listener);
        setItemClickLongListener(mLinear,longClickListener);
        TextView[] buttons = {left,bottom,right,top,middle};
        for (TextView button : buttons) {
            button.setOnClickListener(listener);
            String data = mStorage.getDataString(String.valueOf(button.getId()));
            if (data != null){
                button.setText(data.substring(0,data.indexOf(mSeparator)));
            }
        }
        mSetButton.setOnClickListener(listener);
        mSendHex.setOnClickListener(listener);
    }

    /**
     * 设置子View的ClickListener
     */
    private void setItemClickListener(final View view, View.OnClickListener listener) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i=0;i<childCount;i++){
                //不断的递归给里面所有的View设置OnClickListener
                View childView = viewGroup.getChildAt(i);
                setItemClickListener(childView,listener);
            }
        }else{
            String data = mStorage.getDataString(String.valueOf(view.getId()));
            if (data == null){
                ((TextView)view).setText("长按设置");
                if (mButtonMinWidth <= 0) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            mButtonMinWidth = view.getWidth();
                            mStorage.saveWidth(mButtonMinWidth);
                        }
                    });
                }
            }else {
                ((TextView)view).setText(data.substring(0,data.indexOf(mSeparator)));
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        log("设置按钮，按钮值为: "+((TextView)view).getText().toString()+" 宽度为："+view.getWidth());
                        if (view.getWidth()<mButtonMinWidth){
                            ViewGroup.LayoutParams params=view.getLayoutParams();
                            params.width= mButtonMinWidth;
                            view.setLayoutParams(params);
                            log("设置完成，设置的宽度为："+view.getWidth(),"e");
                        }
                    }
                });
            }
            view.setOnClickListener(listener);
        }
    }


    /**
     * 设置子View的ClickListener
     */
    private void setItemClickLongListener(View view, View.OnLongClickListener listener) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i=0;i<childCount;i++){
                //不断的递归给里面所有的View设置OnClickListener
                View childView = viewGroup.getChildAt(i);
                setItemClickLongListener(childView,listener);
            }
        }else{
            view.setOnLongClickListener(listener);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setDirection() {
        View.OnTouchListener touch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSend = true;
                        startSend(v);
                        break;
                    case MotionEvent.ACTION_UP:
                        isSend = false;
                        break;
                }
                return false;
            }
        };

        top.setOnTouchListener(touch);
        left.setOnTouchListener(touch);
        middle.setOnTouchListener(touch);
        right.setOnTouchListener(touch);
        bottom.setOnTouchListener(touch);

    }

    private void startSend(final View view){
        if (!mIsContinueSend)
            return;
        if (mService == null)
            mService = Executors.newScheduledThreadPool(2);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String data = mStorage.getDataString(String.valueOf(view.getId()));
                if (data == null){
                    isSend = false;
                    if (!mSetButton.isChick())
                    toast("这个按钮还没有初始化,请打开\"设置方向按钮\",然后设置此键");
                    return;
                }
                while (isSend){
                    try {
                        send(data.substring(data.indexOf(mSeparator) + mSeparator.length()));
                        Thread.sleep(mSendTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mService.execute(runnable);
    }


    //设置窗口
    private void setButtonWindow(final View view,boolean isDirection) {
        CommonDialog.Builder collectBuilder = new CommonDialog.Builder(getContext());
        collectBuilder.setView(R.layout.hint_set_button_vessel).fullWidth().loadAnimation().create().show();
        SetButton setButton = collectBuilder.getView(R.id.hint_set_button_vessel_view);
        String data = mStorage.getDataString(String.valueOf(view.getId()));
        String name = data != null?data.substring(0,data.indexOf(mSeparator)):"";
        String content = data != null?data.substring(data.indexOf(mSeparator)+mSeparator.length()):"";
        if (isDirection)
            setButton.showMove(mIsContinueSend);
        setButton.setEditText(name,content).setTime(mSendTime).setBuilder(collectBuilder).setCallback(new SetButton.OnCollectCallback() {
            @Override
            public void callback(String name, String content) {
                mStorage.saveData(String.valueOf(view.getId()),name+mSeparator+content);
                setListener();
            }

            @Override
            public void callLongClick(String name, String content, boolean isLongClick, String time) {
                mStorage.saveData(String.valueOf(view.getId()),name+mSeparator+content);
                mSendTime = Integer.parseInt(time);
                mIsContinueSend = isLongClick;
                mStorage.saveData(TIME_ID,time+mSeparator+isLongClick);
                log("mIsContinueSend: "+mIsContinueSend);
                setListener();
            }
        });
    }

    private void send(String data) {
        if (mTitle != null && !mTitle.getParams().mRightText.equals("已连接")){//代表当前没有连接上
            toast("当前状态不能发送数据，请连接完再尝试发送数据");
            return;
        }
        Message message = mHandler.obtainMessage();
        boolean isHex = mSendHex.getState() == CustomButtonView.State.Open;

        byte[] bytes = null;
        try {
            bytes = Analysis.getBytes(data,mFragmentParameter.getCodeFormat(getContext()), isHex);
        }catch (Exception e){
            log(e.toString());
            if (isHex) {
                data = Analysis.changeHexString(true, data).replaceAll(" ", "");
                bytes = Analysis.getBytes(data,mFragmentParameter.getCodeFormat(getContext()), isHex);
            }
        }
        message.obj = new FragmentMessageItem(isHex,bytes,null,
                true, HoldBluetooth.getInstance().getConnectedArray().get(0),false);
        message.what = CommunicationActivity.DATA_TO_MODULE;
        mHandler.sendMessage(message);
    }




}
