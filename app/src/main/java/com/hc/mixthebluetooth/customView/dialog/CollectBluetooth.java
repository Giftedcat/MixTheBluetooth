package com.hc.mixthebluetooth.customView.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.bluetoothlibrary.DeviceModule;
import com.hc.mixthebluetooth.R;

public class CollectBluetooth extends LinearLayout {

    @ViewById(R.id.hint_collect_edit)
    private EditText mSetName;

    @ViewById(R.id.hint_collect_state1)
    private LinearLayout mState1;

    @ViewById(R.id.hint_collect_state2)
    private LinearLayout mState2;

    private DeviceModule mDeviceModule;

    private OnCollectCallback mCallback;

    private CommonDialog.Builder mBuilder;

    public CollectBluetooth(Context context) {
        this(context,null);
    }

    public CollectBluetooth(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CollectBluetooth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hint_collect_menu,this);
        ViewUtils.inject(this);

    }

    public CollectBluetooth setBuilder(CommonDialog.Builder mBuilder) {
        this.mBuilder = mBuilder;
        return this;
    }

    public CollectBluetooth setDevice(DeviceModule device){
        this.mDeviceModule = device;
        if (device.isCollect()){
            mState2.setVisibility(VISIBLE);
        }else {
            mState1.setVisibility(VISIBLE);
        }
        return this;
    }

    public void setCallback(OnCollectCallback mCallback) {
        this.mCallback = mCallback;
    }

    @OnClick({R.id.hint_collect_affirm,R.id.hint_collect_cancel,R.id.hint_collect_affirm_state2,R.id.hint_collect_cancel_state2})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.hint_collect_affirm:
                affirm(view);
                break;
            case R.id.hint_collect_affirm_state2:
                affirmState2(view);
                break;
            case R.id.hint_collect_cancel:
            case R.id.hint_collect_cancel_state2:
                cancel();
                break;
        }
    }

    private void affirmState2(View view) {
        if (mDeviceModule != null){
            mDeviceModule.setCollectModule(view.getContext(),null);
        }
        if (mBuilder != null){
            mBuilder.dismiss();
        }
        if (mCallback != null){
            mCallback.callback();
        }
    }

    private void cancel() {
        if (mBuilder != null)
            mBuilder.dismiss();
    }

    private void affirm(View view) {
        if (mDeviceModule != null){
            mDeviceModule.setCollectModule(view.getContext(),mSetName.getText().toString().trim().equals("")?mDeviceModule.getOriginalName(view.getContext()):mSetName.getText().toString().trim());
        }
        if (mBuilder != null){
            mBuilder.dismiss();
        }
        if (mCallback != null){
            mCallback.callback();
        }
    }


    public interface OnCollectCallback{
        void callback();
    }

}
