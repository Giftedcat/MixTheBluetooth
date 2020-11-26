package com.hc.mixthebluetooth.recyclerData;

import android.content.Context;

import com.hc.basiclibrary.recyclerAdapterBasic.ItemClickListener;
import com.hc.basiclibrary.recyclerAdapterBasic.RecyclerCommonAdapter;
import com.hc.basiclibrary.recyclerAdapterBasic.ViewHolder;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.recyclerData.itemHolder.FragmentLogItem;

import java.util.List;

public class FragmentLogAdapter extends RecyclerCommonAdapter<FragmentLogItem> {
    public FragmentLogAdapter(Context context, List<FragmentLogItem> fragmentLogs, int layoutId) {
        super(context, fragmentLogs, layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, FragmentLogItem item, int position, ItemClickListener itemClickListener) {
        holder.setText(R.id.item_log_name,item.getName()+":").setTextAndColor(R.id.item_log,item.getData(),item.getLv());
    }
}
