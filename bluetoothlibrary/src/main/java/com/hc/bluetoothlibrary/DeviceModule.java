package com.hc.bluetoothlibrary;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.hc.bluetoothlibrary.bleBluetooth.ParseLeAdvData;
import com.hc.bluetoothlibrary.tootl.DataMemory;
import com.hc.bluetoothlibrary.tootl.ToolClass;

public class DeviceModule {

    private String mName;
    private BluetoothDevice mDevice;
    private boolean isBLE = false;
    private int mRssi;
    private boolean mBeenConnected;
    private ScanResult result;
    private DataMemory mDataMemory;
    private boolean isCollect = false;//是否被收藏

    private String mServiceUUID,mReadWriteUUID;

    public DeviceModule(BluetoothDevice device, int rssi,String name,Context context,ScanResult result){
        this(name,device,false,context,rssi);
        this.result = result;
        if (ToolClass.pattern(device.getName()) && context != null){
            if (!ToolClass.pattern(name)){
                mDataMemory = new DataMemory(context);
                mDataMemory.saveData(device.getAddress(),name);
                Log.d("AppRun"+getClass().getSimpleName(),"修正保存乱码文字..");
            }
        }
    }

    public DeviceModule(String name, BluetoothDevice device){
        this(name,device,false,null,10);
    }

    public DeviceModule(String name, BluetoothDevice device,boolean beenConnected,Context context,int rssi){

        this.mName = name;
        this.mDevice = device;
        this.mBeenConnected = beenConnected;
        this.mRssi = rssi;

        if (device == null)
            return;

        switch (device.getType()){
            case BluetoothDevice.DEVICE_TYPE_CLASSIC :
            case BluetoothDevice.DEVICE_TYPE_DUAL:
               isBLE = false;
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                isBLE = true;
                break;
        }

        if (isBLE && context != null){
            if (ToolClass.pattern(name) || ToolClass.pattern(device.getName())){
                String tempName = new DataMemory(context).getData(device.getAddress());
                if (tempName != null){
                    mName = tempName;
                }
            }
        }

    }

    public String getName(){
        if (mName != null) {
            return mName;
        }else if (mDevice.getName() != null) {
            mName = mDevice.getName();
        }else {
            mName = "N/A";
        }
        return mName;
    }

    public String getOriginalName(Context context){
        mName = getDevice().getName();
        if (isBLE && context != null){
            if (ToolClass.pattern(getDevice().getName())){
                String tempName = new DataMemory(context).getData(getMac());
                if (tempName != null){
                    mName = tempName;
                }
            }
        }
        if (mName == null)
            mName = "N/A";
        return mName;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public String getMac(){
        if (mDevice != null){
            return mDevice.getAddress();
        }
        return "出错了";
    }

    //修正模块名称的乱码..
    public void setMessyCode(Context context){
        if (context != null) {
            String tempName = new DataMemory(context).getData(getMac());
            if (tempName != null) {
                Log.d("AppRun"+getClass().getSimpleName(),"修正成功..");
                mName = tempName;
            }
        }
    }

    public void setRssi(int rssi){
        this.mRssi = rssi;
    }

    public void setUUID(String service,String readWrite){
        if (service != null)
            this.mServiceUUID = service;
        if (readWrite != null)
            this.mReadWriteUUID = readWrite;
    }

    public void setCollectModule(Context context,String name){
        if (mDataMemory != null) {
            mDataMemory.saveCollectData(getMac(), name);
        } else {
            mDataMemory = new DataMemory(context);
            mDataMemory.saveCollectData(getMac(), name);
        }

        if (name == null){
            getOriginalName(context);
            isCollect = false;
        }
    }

    public void isCollectName(Context context){
        String s;
        if (mDataMemory != null) {
            s = mDataMemory.getCollectData(getMac());
        } else {
            mDataMemory = new DataMemory(context);
            s = mDataMemory.getCollectData(getMac());
        }
        if (s != null){
            isCollect = true;
            mName = s;
        }
    }

    public int getRssi() {
        return mRssi;
    }

    public boolean isBLE() {
        return isBLE;
    }

    public boolean isBeenConnected() {
        return mBeenConnected;
    }

    public String bluetoothType(){
        if (isBLE)
            return "Ble蓝牙";
        if (mBeenConnected)
            return "已配对";
        else
            return "未配对";
    }

    public boolean isCollect() {
        return isCollect;
    }

    public boolean isHcModule(boolean isCheck, String dataFilter){
        String data = null;
        try {
            if (result != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                data = ParseLeAdvData.getShort16(result.getScanRecord().getBytes());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (data != null){
            if (!isCheck)
                return data.equals("0xFFE0") || data.equals("0xFFF0");
            else {
                if (dataFilter != null)
                    return data.equals("0x" + dataFilter.toUpperCase());
                else
                    return true;
            }
        }
        return false;
    }


    public String getReadWriteUUID() {
        if (mReadWriteUUID != null)
            return mReadWriteUUID;
        else
            return "没有读写特征";
    }

    public String getServiceUUID() {
        if(mServiceUUID != null)
            return mServiceUUID;
        else
            return "00001101-0000-1000-8000-00805F9B34FB";
    }
}
