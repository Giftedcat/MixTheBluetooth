package com.hc.mixthebluetooth.customView.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewByIds;
import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.mixthebluetooth.R;


public class PermissionHint extends LinearLayout {

    @ViewByIds(value = {R.id.hint_permission_text_hint,R.id.hint_permission_affirm,R.id.hint_permission_cancel},name = {"mHintText","mAffirm","mCancel"})
    private TextView mHintText,mAffirm,mCancel;

    private CommonDialog.Builder mBuilder;

    private PermissionHintCallback mCallback;

    private final static String mHint = "你禁止了授权，请在手机设置里面授权，否则App将无法使用";

    public PermissionHint(Context context) {
        this(context,null);
    }

    public PermissionHint(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PermissionHint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hint_permission_menu,this);
        ViewUtils.inject(this);
    }


    @OnClick({R.id.hint_permission_affirm,R.id.hint_permission_cancel})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.hint_permission_affirm:
                affirm();
                break;
            case R.id.hint_permission_cancel:
                cancel();
                break;
        }
    }

    public PermissionHint setBuilder(CommonDialog.Builder mBuilder) {
        this.mBuilder = mBuilder;
        return this;
    }

    public void setCallback(PermissionHintCallback callback){
        this.mCallback = callback;
    }

    public PermissionHint setPermission(boolean permission){
        if (!permission) {
            mHintText.setText(mHint);
            mAffirm.setVisibility(GONE);
            mCancel.setText("退出");
            mCancel.setBackgroundResource(R.drawable.cancel_back_off2);
        }
        return this;
    }



    private void cancel() {
        if (mBuilder != null)
            mBuilder.dismiss();
        if (mCallback != null)
            mCallback.callback(false);
    }

    private void affirm() {
        if (mBuilder != null)
            mBuilder.dismiss();
        if (mCallback != null)
            mCallback.callback(true);
    }

    public interface PermissionHintCallback{
        void callback(boolean permission);
    }

}
