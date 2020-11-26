package com.hc.mixthebluetooth.customView.dialog;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hc.basiclibrary.dialog.CommonDialog;
import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.ioc.ViewByIds;
import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.HIDActivity;
import com.hc.mixthebluetooth.customView.CheckBoxSample;
import com.hc.mixthebluetooth.storage.Storage;

public class HintHID extends LinearLayout {

    private CommonDialog.Builder mBuilder;

    @ViewById(R.id.hint_hid_no_show)
    private CheckBoxSample mCheck;

    private static boolean isShow = true;

    public HintHID(Context context) {
        this(context,null);
    }

    public HintHID(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HintHID(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.hint_hid_menu,this);
        ViewUtils.inject(this);
    }

    public void setBuilder(CommonDialog.Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

    public boolean isShow() {
        if (isShow){
            isShow = false;
            return true;
        }
        return isShow;
    }

    @OnClick({R.id.hint_hid_download,R.id.hint_hid_right,R.id.hint_hid_no_show,R.id.hint_hid_no_show_text})
    private void onClock(View view){
        switch (view.getId()){
            case R.id.hint_hid_download:
                mBuilder.dismiss();
                view.getContext().startActivity(new Intent(view.getContext(), HIDActivity.class));
                setNoShowPopWindow();
                break;

            case R.id.hint_hid_right:
                mBuilder.dismiss();
                setNoShowPopWindow();
                break;

            case R.id.hint_hid_no_show:
            case R.id.hint_hid_no_show_text:
                mCheck.setChecked(!mCheck.isChecked());
                break;
        }
    }

    private void setNoShowPopWindow(){
        if (mCheck.isChecked()){
            Storage storage = new Storage(getContext());
            storage.saveFirstTime();
        }
    }


}
