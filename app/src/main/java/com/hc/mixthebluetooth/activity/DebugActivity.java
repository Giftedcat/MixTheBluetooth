package com.hc.mixthebluetooth.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.titleBasic.DefaultNavigationBar;
import com.hc.basiclibrary.viewBasic.BasActivity;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.single.HoldBluetooth;
import com.hc.mixthebluetooth.customView.CustomButtonView;
import com.hc.mixthebluetooth.storage.Storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DebugActivity extends BasActivity implements View.OnClickListener{

    private Storage mStorage;

    @ViewById(R.id.debug_development_mode)
    private CustomButtonView customButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        initViev();
        initTitle();
        initData();
    }

    @Override
    public void initAll() {

    }

    private void initData() {

    }

    private void initViev() {
        super.setContext(this);
        setButton(R.id.debug_read);
        if (mStorage == null)
            mStorage = new Storage(this);
        if(mStorage.getData(HoldBluetooth.DEVELOPMENT_MODE_KEY)){
            customButtonView.staysOn();
        }else {
            customButtonView.closed();
        }
    }

    @Override
    public void onClick(View v) {
        TextView textView = findViewById(R.id.bug_log);

        textView.setText(load("errNewLog"));
    }

    @OnClick(R.id.debug_development_mode)
    private void switchDevelopment(View view){
        mStorage.saveData(HoldBluetooth.DEVELOPMENT_MODE_KEY,!customButtonView.isChick());
        customButtonView.toggle();
    }

    private void initTitle(){
        new DefaultNavigationBar
                .Builder(this, (ViewGroup) findViewById(R.id.debug_activity))
                .setTitle("Bug日志")
                .setRightText("")
                .builer();
    }

    public String load(String file){
        FileInputStream in;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = openFileInput(file);
            reader = new BufferedReader(new InputStreamReader(in));
            String line ;
            while ((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader != null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        if(!content.toString().isEmpty())
            return  content.toString();
        else
            return "没有记录到异常";
    }

    public void setButton(int view){
        View textView = findViewById(view);
        textView.setOnClickListener(this);
    }

}
