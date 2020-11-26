package com.hc.mixthebluetooth.customView.dialog;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.bluetoothlibrary.DeviceModule;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.HIDActivity;
import com.hc.mixthebluetooth.customView.CheckBoxSample;
import com.hc.mixthebluetooth.storage.Storage;

public class InvalidHint extends LinearLayout {

    @ViewById(R.id.hint_invalid_hint_check)
    private CheckBoxSample mSample;

    private CommonDialog.Builder mBuilder;

    private Storage mStorage;

    public InvalidHint(Context context) {
        this(context,null);
    }

    public InvalidHint(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public InvalidHint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hint_invalid_menu,this);
        ViewUtils.inject(this);
    }


    @OnClick({R.id.hint_invalid_affirm,R.id.hint_invalid_cancel,R.id.hint_invalid_hint_check,R.id.hint_invalid_hint,R.id.hint_invalid_hid})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.hint_invalid_affirm:
                affirm();
                break;
            case R.id.hint_invalid_cancel:
                cancel();
                break;
            case R.id.hint_invalid_hid:
                mBuilder.getContext().startActivity(new Intent(mBuilder.getContext(), HIDActivity.class));
                affirm();
                break;
            case R.id.hint_invalid_hint:
            case R.id.hint_invalid_hint_check:
                mSample.toggle();
                break;
        }
    }

    public InvalidHint setBuilder(CommonDialog.Builder mBuilder) {
        this.mBuilder = mBuilder;
        return this;
    }



    private void cancel() {
        if (mBuilder != null)
            mBuilder.dismiss();
    }

    private void affirm() {
        if (mBuilder != null){
            saveData();
            mBuilder.dismiss();
        }
    }

    private void saveData(){
        if (mSample.isChecked()) {
            if (mStorage == null)
                mStorage = new Storage(mBuilder.getContext());
            mStorage.saveInvalidAT();
        }
    }



}
