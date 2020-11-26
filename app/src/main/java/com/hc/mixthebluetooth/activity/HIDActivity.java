package com.hc.mixthebluetooth.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hc.basiclibrary.titleBasic.DefaultNavigationBar;
import com.hc.basiclibrary.viewBasic.BasActivity;
import com.hc.mixthebluetooth.R;

public class HIDActivity extends BasActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hid);
        setContext(this);
    }

    @Override
    public void initAll() {
        setTitle();
    }


    //设置头部
    private void setTitle() {
         new DefaultNavigationBar
                .Builder(this,(ViewGroup)findViewById(R.id.hid_name))
                .setTitle("下载步骤")
                .builer();
    }

}
