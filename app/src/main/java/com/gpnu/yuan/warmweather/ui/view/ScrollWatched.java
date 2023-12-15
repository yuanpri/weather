package com.gpnu.yuan.warmweather.ui.view;

/**
 * 定义滑动监听接口
 * @author hefeng
 */
public interface ScrollWatched {
    void addWatcher(ScrollWatcher watcher);
    void removeWatcher(ScrollWatcher watcher);
    void notifyWatcher(int x);
}
