package com.hc.mixthebluetooth.recyclerData;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.hc.basiclibrary.recyclerAdapterBasic.ItemClickListener;
import com.hc.basiclibrary.recyclerAdapterBasic.RecyclerCommonAdapter;
import com.hc.basiclibrary.recyclerAdapterBasic.ViewHolder;
import com.hc.bluetoothlibrary.DeviceModule;
import com.hc.mixthebluetooth.R;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerCommonAdapter<DeviceModule> {

    public MainRecyclerAdapter(Context context, List<DeviceModule> strings, int layoutId){
        super(context,strings,layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, DeviceModule item, int position, ItemClickListener itemClickListener) {
        holder.setImageResource(R.id.item_main_icon,item.isBLE()?R.drawable.item_src_ble:R.drawable.item_src_2)
                .setText(R.id.item_main_name,item.getName())
                .setText(R.id.item_main_mac,item.getMac())
                .setTextAndColor(R.id.item_main_pair,item.bluetoothType(),item.isBeenConnected()? Color.parseColor("#79D0A5"): Color.parseColor("#737373"))
                .setText(R.id.item_main_rssi,item.getRssi()!=10?item.getRssi()+" dBm":"")
                .setOnclickListener(R.id.item_main_icon,position,itemClickListener);
        if (item.isCollect()){
            holder.setText(R.id.item_main_name,"");
            holder.setText(R.id.item_main_name_collect,item.getName());
        }else {
            holder.setText(R.id.item_main_name_collect,"");
        }
    }
}
