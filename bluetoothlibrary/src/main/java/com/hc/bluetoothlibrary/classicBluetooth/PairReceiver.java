package com.hc.bluetoothlibrary.classicBluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/*
*
* 操作并监听蓝牙配对的广播
*
* 2.0蓝牙配对的广播，默认密码是1234
* 备注：1.蓝牙在配对失败，有时会在BluetoothDevice.getBondState()状态中更新参数，有时不会。
*       2.蓝牙在配对失败时，不注销广播的情况下连接蓝牙，会有异常，连接不上。
* */

public class PairReceiver extends BroadcastReceiver {

    private static final String mBluetoothPin = "1234";
    private PairCallback mCallback;
    private Context mContent;
    private int mLoopNumber = 0;
    private String mClassName = getClass().getSimpleName();

    public PairReceiver(Context context,PairCallback callback){
        this.mCallback = callback;
        this.mContent = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action != null && action.equals("android.bluetooth.device.action.PAIRING_REQUEST")){
            final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d("AppRun"+mClassName,"接收到广播");
            abortBroadcast();//截断有序广播，不让手机弹出配对窗口
            if (bluetoothDevice == null) {
                Log.e("AppRun"+mClassName,"bluetoothDevice is null !!");
                return;
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    bluetoothDevice.setPin(mBluetoothPin.getBytes());
                }else {
                    ClsUtils.setPin(bluetoothDevice.getClass(),bluetoothDevice,mBluetoothPin);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final Timer timer = new Timer();//定时器，检测配对情况

            final TimerTask taskIsBond = new TimerTask() {//在500ms后检测是否完成了配对
                @Override
                public void run() {
                    Log.w("AppRun"+mClassName,"Bluetooth bond state is none,connect bluetooth");
                    if (mCallback != null){
                        try {
                            mCallback.connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            TimerTask taskCloseRadio = new TimerTask() { //在4*800ms内，检测配对情况
                @Override
                public void run() {
                    mLoopNumber++;
                    Log.d("AppRun"+mClassName,"loop,device bond state is "+bluetoothDevice.getBondState());
                    if (mLoopNumber == 8 || bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE){//超时或配对失败
                        try {
                            this.cancel();
                            mContent.unregisterReceiver(PairReceiver.this);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.w("AppRun"+mClassName,"Close broadcast,delayed 500 ms,connect bluetooth");
                        timer.schedule(taskIsBond,500);
                        return;
                    }
                    if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED && mCallback != null){//配对成功
                        try {
                            Log.d("AppRun"+mClassName,"Close broadcast, bluetooth bond state is bonded (success)");
                            mCallback.connect();
                            mContent.unregisterReceiver(PairReceiver.this);
                            mCallback = null;
                            this.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timer.schedule(taskCloseRadio,300,200);
        }
    }


    public interface PairCallback{
        void connect() throws Exception;
    }

}
