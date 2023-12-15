package com.gpnu.yuan.library.base;

import androidx.viewbinding.ViewBinding;

import com.gpnu.yuan.library.base.BaseVBActivity;

public abstract class NetworkActivity<VB extends ViewBinding> extends BaseVBActivity<VB> {

    @Override
    public void initData() {
        onCreate();
        onObserveData();
    }

    protected abstract void onCreate();

    protected abstract void onObserveData();
}
