package com.hc.mixthebluetooth.customView.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.customView.CheckBoxSample;

public class SetButton extends LinearLayout {

    @ViewById(R.id.hint_set_button_name)
    private EditText mSetName;

    @ViewById(R.id.hint_set_button_content)
    private EditText mSetContent;

    @ViewById(R.id.hint_hide_linear)
    private LinearLayout mHideLinear;

    @ViewById(R.id.hint_hide_time_linear)
    private LinearLayout mHideTimeLinear;

    @ViewById(R.id.hint_hide_click)
    private CheckBoxSample mClickSend;

    @ViewById(R.id.hint_hide_long_click)
    private CheckBoxSample mLongClickSend;

    @ViewById(R.id.hint_hide_time)
    private EditText mSendTime;

    private OnCollectCallback mCallback;

    private CommonDialog.Builder mBuilder;

    public SetButton(Context context) {
        this(context,null);
    }

    public SetButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SetButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hint_set_button_menu,this);
        ViewUtils.inject(this);

    }

    public SetButton setBuilder(CommonDialog.Builder mBuilder) {
        this.mBuilder = mBuilder;
        return this;
    }

    public void setCallback(OnCollectCallback mCallback) {
        this.mCallback = mCallback;
    }


    public void showMove(boolean isClick){
        mHideLinear.setVisibility(VISIBLE);
        mClickSend.setChecked(!isClick);
        mLongClickSend.setChecked(isClick);
        setHideTimeLinear(isClick);
    }

    public SetButton setEditText(String name,String content){
        mSetName.setText(name);
        mSetContent.setText(content);
        return this;
    }

    public SetButton setTime(int time){
        mSendTime.setText(String.valueOf(time));
        return this;
    }

    private void setHideTimeLinear(boolean isClick) {
        if (isClick){
            mHideTimeLinear.setVisibility(VISIBLE);
        }else {
            mHideTimeLinear.setVisibility(GONE);
        }
    }

    @OnClick({R.id.hint_set_button_affirm,R.id.hint_set_button_cancel})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.hint_set_button_affirm:
                affirm();
                break;
            case R.id.hint_set_button_cancel:
                cancel();
                break;
        }
    }

    private void cancel() {
        if (mBuilder != null)
            mBuilder.dismiss();
    }

    private void affirm() {
        if (mSetName.getText().toString().trim().equals("")){
            Toast.makeText(getContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSetContent.getText().toString().trim().equals("")){
            Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mLongClickSend.isChecked() && mSendTime.getText().toString().trim().equals("")){
            Toast.makeText(getContext(), "时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mLongClickSend.isChecked() && Integer.parseInt(mSendTime.getText().toString().trim())<10){
            Toast.makeText(getContext(), "发送间隔不要小于10毫秒", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBuilder != null){
            mBuilder.dismiss();
        }
        if (mCallback != null ){
            if (mHideLinear.getVisibility() == View.GONE)
                mCallback.callback(mSetName.getText().toString().trim(),mSetContent.getText().toString().trim());
            else {
                mCallback.callLongClick(mSetName.getText().toString().trim(), mSetContent.getText().toString().trim(),
                        mLongClickSend.isChecked(), mSendTime.getText().toString().trim());
                Toast.makeText(getContext(), "设置完成，关闭\"设置方向按钮\"后，就可以使用此按键了", Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick({R.id.hint_hide_long_click,R.id.hint_hide_click})
    private void onClick(){
        mClickSend.toggle();
        mLongClickSend.toggle();
        setHideTimeLinear(mLongClickSend.isChecked());
    }


    public interface OnCollectCallback{
        void callback(String name,String content);
        void callLongClick(String name,String content,boolean isLongClick,String time);
    }

}
