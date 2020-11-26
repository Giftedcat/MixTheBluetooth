package com.hc.mixthebluetooth.fragment;

import android.os.Handler;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hc.basiclibrary.ioc.OnClick;
import com.hc.basiclibrary.ioc.ViewById;
import com.hc.basiclibrary.viewBasic.BasFragment;
import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.activity.CommunicationActivity;
import com.hc.mixthebluetooth.activity.single.HoldBluetooth;
import com.hc.mixthebluetooth.recyclerData.FragmentLogAdapter;
import com.hc.mixthebluetooth.recyclerData.FragmentMessAdapter;
import com.hc.mixthebluetooth.recyclerData.itemHolder.FragmentLogItem;

import java.util.ArrayList;
import java.util.List;
//差更新，差接收，传输几乎完成
public class FragmentLog extends BasFragment {

    @ViewById(R.id.recycler_log_fragment)
    private RecyclerView mRecyclerView;
    private FragmentLogAdapter mAdapter;
    private List<FragmentLogItem> mDataList = new ArrayList<>();

    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_log;
    }

    @Override
    public void initAll() {
        initRecycler();
    }

    @Override
    public void setHandler(Handler handler) {

    }

    @Override
    public void readData(int state, final Object o, byte[] data) {
        if (getActivity() == null || state != CommunicationActivity.FRAGMENT_STATE_LOG_MESSAGE || o == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataList.add((FragmentLogItem) o);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mDataList.size());
            }
        });
    }

    @OnClick(R.id.clear_log_fragment)
    private void clear(){
        mDataList.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void initRecycler(){
        mAdapter = new FragmentLogAdapter(getContext(),mDataList,R.layout.item_log_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

}
