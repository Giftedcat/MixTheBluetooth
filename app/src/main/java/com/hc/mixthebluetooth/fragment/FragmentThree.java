package com.hc.mixthebluetooth.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewByIds;
import com.hc.basiclibrary.viewBasic.BasFragment;
import com.hc.bluetoothlibrary.DeviceModule;
import com.hc.bluetoothlibrary.tootl.ModuleParameters;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.CommunicationActivity;
import com.hc.mixthebluetooth.activity.single.FragmentParameter;
import com.hc.mixthebluetooth.customView.CheckBoxSample;

public class FragmentThree extends BasFragment {

    @ViewByIds(value = {R.id.general_fragment_name,R.id.general_fragment_mac,R.id.general_fragment_type,
    R.id.general_fragment_state,R.id.general_fragment_service,R.id.general_fragment_send,R.id.general_fragment_read},
            name = {"mNameTv","mMacTv","mTypeTv","mStateTv","mServiceTv","mSendTv","mReadTv"})
    private TextView mNameTv,mMacTv,mTypeTv,mStateTv,mServiceTv,mSendTv,mReadTv;

    @ViewByIds(value = {R.id.general_fragment_time,R.id.general_fragment_ble_buff,R.id.general_fragment_classic_buff},
            name = {"mTimeEt","mBleBuffEt","mClassicEt"})
    private EditText mTimeEt,mBleBuffEt,mClassicEt;

    @ViewByIds(value = {R.id.general_fragment_height,R.id.general_fragment_centre,R.id.general_fragment_low,R.id.general_fragment_gbk,R.id.general_fragment_utf,R.id.general_fragment_unicode,R.id.general_fragment_ascii},
            name = {"mHeightCs","mCentreCs","mLowCs","mCodedFormatGBK","mCodeFormatUTF","mCodedFormatUnicode","mCodedFormatASCII"})
    private CheckBoxSample mHeightCs,mCentreCs,mLowCs,mCodedFormatGBK,mCodeFormatUTF,mCodedFormatUnicode,mCodedFormatASCII;

    @ViewById(R.id.general_fragment_pack_value)
    private TextView mPackLossValue;

    private enum State{hidden,unhidden}
    private State mState = State.hidden;

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_three;
    }

    @Override
    public void initAll() {
        initView();
    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void updateState(int state) {
        if (CommunicationActivity.FRAGMENT_UNHIDDEN == state){//非隐藏状态
            setHiddenChanged(true);
        }else if (CommunicationActivity.FRAGMENT_THREE_HIDE == state){//隐藏状态
            setHiddenChanged(false);
        }
    }

    @Override
    public void readData(int state, Object o, byte[] data) {
        if (state == CommunicationActivity.FRAGMENT_STATE_DATA && o != null){
            DeviceModule deviceModule = (DeviceModule) o;
            mNameTv.setText(deviceModule.getName());
            mMacTv.setText(deviceModule.getMac());
            mTypeTv.setText(deviceModule.isBLE()?"BLE蓝牙":"2.0经典蓝牙");
            mServiceTv.setText(deviceModule.getServiceUUID());
            mSendTv.setText(deviceModule.getReadWriteUUID());
            mReadTv.setText(deviceModule.getReadWriteUUID());
        }
        if (state == CommunicationActivity.FRAGMENT_STATE_CONNECT_STATE && o != null){
            mStateTv.setText(o.toString());
        }
    }

    @OnClick({R.id.general_fragment_height,R.id.general_fragment_centre,R.id.general_fragment_low,
            R.id.general_fragment_height_text,R.id.general_fragment_centre_text,R.id.general_fragment_low_text})
    private void onClick(View view){
        mHeightCs.setChecked(false);
        mCentreCs.setChecked(false);
        mLowCs.setChecked(false);
        switch (view.getId()){
            case R.id.general_fragment_height:
            case R.id.general_fragment_height_text:
                mHeightCs.setChecked(true);
                break;
            case R.id.general_fragment_centre:
            case R.id.general_fragment_centre_text:
                mCentreCs.setChecked(true);
                break;
            case R.id.general_fragment_low:
            case R.id.general_fragment_low_text:
                mLowCs.setChecked(true);
                break;
        }
    }

    @OnClick({R.id.general_fragment_gbk,R.id.general_fragment_gbk_text,R.id.general_fragment_unicode,R.id.general_fragment_unicode_text,
                R.id.general_fragment_utf,R.id.general_fragment_utf_text,R.id.general_fragment_ascii,R.id.general_fragment_ascii_text})
    private void codedFormatOnClick(View view){
        mCodedFormatGBK.setChecked(false);
        mCodeFormatUTF.setChecked(false);
        mCodedFormatUnicode.setChecked(false);
        mCodedFormatASCII.setChecked(false);
        switch (view.getId()){
            case R.id.general_fragment_gbk:
            case R.id.general_fragment_gbk_text:
                mCodedFormatGBK.setChecked(true);
                FragmentParameter.getInstance().setCodeFormat("GBK",getContext());
                break;
            case R.id.general_fragment_utf:
            case R.id.general_fragment_utf_text:
                mCodeFormatUTF.setChecked(true);
                FragmentParameter.getInstance().setCodeFormat("UTF-8",getContext());
                break;
            case R.id.general_fragment_unicode:
            case R.id.general_fragment_unicode_text:
                mCodedFormatUnicode.setChecked(true);
                FragmentParameter.getInstance().setCodeFormat("Unicode",getContext());
                break;
            case R.id.general_fragment_ascii:
            case R.id.general_fragment_ascii_text:
                mCodedFormatASCII.setChecked(true);
                FragmentParameter.getInstance().setCodeFormat("ASCII",getContext());
                break;
        }
    }

    @OnClick({R.id.general_fragment_pack_add,R.id.general_fragment_pack_minus})
    private void onClickValue(View view){
        switch (view.getId()){
            case R.id.general_fragment_pack_add:
                mPackLossValue.setText(String.valueOf(ModuleParameters.addLevel()));
                break;
            case R.id.general_fragment_pack_minus:
                mPackLossValue.setText(String.valueOf(ModuleParameters.minusLevel()));
                break;
        }
    }

    private void initView(){
        mHeightCs.setChecked(false);
        mCentreCs.setChecked(false);
        mLowCs.setChecked(false);
        mTimeEt.setText(String.valueOf(ModuleParameters.getTime()));
        mBleBuffEt.setText(String.valueOf(ModuleParameters.getBleReadBuff()));
        mClassicEt.setText(String.valueOf(ModuleParameters.getClassicReadBuff()));
        mPackLossValue.setText(String.valueOf(ModuleParameters.getLevel()));
        int state = ModuleParameters.system()?ModuleParameters.getState()-2:ModuleParameters.getState();
        switch (state){
            case 0:
                mHeightCs.setChecked(true);
                break;
            case 1:
                mCentreCs.setChecked(true);
                break;
            case 2:
                mLowCs.setChecked(true);
                break;
        }

        switch (FragmentParameter.getInstance().getCodeFormat(getContext())){
            case "GBK":
                mCodedFormatGBK.setChecked(true);
                break;
            case "UTF-8":
                mCodeFormatUTF.setChecked(true);
                break;
            case "Unicode":
                mCodedFormatUnicode.setChecked(true);
                break;
            case "ASCII":
                mCodedFormatASCII.setChecked(true);
                break;
        }

    }

    private int getSate(){
        if (mHeightCs.isChecked())
            return 0;
        else if (mCentreCs.isChecked())
            return 1;
        else
            return 2;
    }


    //设置fragment隐藏与非隐藏下view的改变
    private void setHiddenChanged(boolean unHidden){

        if (unHidden && mState == State.unhidden)//传来非隐藏，与当前状态值相同，则退出
            return;
        if (!unHidden && mState == State.hidden)//同上
            return;

        if (!unHidden){//隐藏
            int classicBuff = Integer.parseInt(mClassicEt.getText().toString());
            int time = Integer.parseInt(mTimeEt.getText().toString());
            ModuleParameters.setParameters(getSate(),Integer.parseInt(mBleBuffEt.getText().toString()),classicBuff,time,getContext());
            ModuleParameters.saveLevel(Integer.parseInt(mPackLossValue.getText().toString()),getContext());
            mState = State.hidden;//隐藏
        }else {
            initView();
            mState = State.unhidden;//非隐藏
        }
    }
}
