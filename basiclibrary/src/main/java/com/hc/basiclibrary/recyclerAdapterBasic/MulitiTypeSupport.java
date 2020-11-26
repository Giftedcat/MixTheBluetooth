package com.hc.basiclibrary.recyclerAdapterBasic;

/**
 * Created by xngly on 2019/4/13.
 * 多条目布局的支持
 */

public interface MulitiTypeSupport<DATA> {
    public int getLayoutId(DATA item);
}
