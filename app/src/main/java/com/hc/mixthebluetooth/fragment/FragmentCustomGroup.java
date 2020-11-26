package com.hc.mixthebluetooth.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.ViewById;
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

public class FragmentCustomGroup extends BasFragment {

    private Storage mStorage;

    private FragmentParameter mFragmentParameter;

    private static final String mSeparator = "//**$$/separator/$$**//";

    private Handler mHandler;

    @ViewById(R.id.custom_fragment_Linear)
    private LinearLayout mButtonLinear;

    @ViewById(R.id.custom_fragment_direction_hex)
    private CustomButtonView mSendHex;

    private DefaultNavigationBar mTitle;


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_custom_button_group;
    }

    @Override
    public void initAll() {

    }

    @Override
    public void initAll(View view, Context context) {
        super.initAll(view, context);
        mStorage = new Storage(context);
        mFragmentParameter = FragmentParameter.getInstance();
        setListener();
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
        if (state == CommunicationActivity.FRAGMENT_STATE_SEND_SEND_TITLE){
            mTitle = (DefaultNavigationBar) o;
        }
    }

    private void setListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.custom_fragment_direction_hex){
                    mSendHex.toggle();
                    return;
                }
                String data = mStorage.getDataString(String.valueOf(v.getId()));
                if (data != null)
                    send(data.substring(data.indexOf(mSeparator)+mSeparator.length()));
                else
                    Toast.makeText(getContext(), "此按钮还没有初始化", Toast.LENGTH_SHORT).show();
            }
        };
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setButtonWindow(v);
                return false;
            }
        };
        setItemClickListener(mButtonLinear,listener);
        setItemClickLongListener(mButtonLinear,longClickListener);
        mSendHex.setOnClickListener(listener);
    }

    /**
     * 设置子View的ClickListener
     */
    private void setItemClickListener(View view, View.OnClickListener listener) {
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
                ((Button)view).setText("长按设置");
            }else {
                ((Button)view).setText(data.substring(0,data.indexOf(mSeparator)));
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

    //设置窗口
    private void setButtonWindow(final View view) {
        CommonDialog.Builder collectBuilder = new CommonDialog.Builder(getContext());
        collectBuilder.setView(R.layout.hint_set_button_vessel).fullWidth().loadAnimation().create().show();
        SetButton setButton = collectBuilder.getView(R.id.hint_set_button_vessel_view);
        String data = mStorage.getDataString(String.valueOf(view.getId()));
        String name = data != null?data.substring(0,data.indexOf(mSeparator)):"";
        String content = data != null?data.substring(data.indexOf(mSeparator)+mSeparator.length()):"";
        setButton.setEditText(name,content).setBuilder(collectBuilder).setCallback(new SetButton.OnCollectCallback() {
            @Override
            public void callback(String name, String content) {
                mStorage.saveData(String.valueOf(view.getId()),name+mSeparator+content);
                setListener();
            }

            @Override
            public void callLongClick(String name, String content, boolean isLongClick, String time) {

            }
        });
    }

    private void send(String data){
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
        message.obj = new FragmentMessageItem(false, bytes, null,
                    true, HoldBluetooth.getInstance().getConnectedArray().get(0), false);
        message.what = CommunicationActivity.DATA_TO_MODULE;
        mHandler.sendMessage(message);

    }

}
