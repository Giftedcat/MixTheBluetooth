package com.hc.mixthebluetooth.recyclerData.itemHolder;

import android.graphics.Color;

import com.hc.mixthebluetooth.R;

public class FragmentLogItem {
    private String name,data,lv;

    public FragmentLogItem(String name,String data,String lv){
        this.name = name;
        this.data = data;
        this.lv = lv;
    }

    public int getLv() {
        if (lv.equals("e"))
            return Color.parseColor("#FE4F6E");
        if (lv.equals("w"))
            return Color.parseColor("#4EB9D2");
        return Color.parseColor("#737373");
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }
}
