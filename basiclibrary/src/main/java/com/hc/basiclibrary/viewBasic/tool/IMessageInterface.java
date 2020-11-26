package com.hc.basiclibrary.viewBasic.tool;

import android.os.Handler;

public interface IMessageInterface {
    void setHandler(Handler handler);
    void readData(int state,Object o,byte[] data);
    void updateState(int state);
}
