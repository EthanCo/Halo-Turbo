package com.ethanco.halo.turbo.bean;

import com.ethanco.halo.turbo.ads.IKeepAliveListener;

/**
 * 心跳
 *
 * @author EthanCo
 * @since 2017/7/31
 * -     ┌─┐       ┌─┐
 * -  ┌──┘ ┴───────┘ ┴──┐
 * -  │                 │
 * -  │       ───       │
 * -  │  ─┬┘       └┬─  │
 * -  │                 │
 * -  │       ─┴─       │
 * -  │                 │
 * -  └───┐         ┌───┘
 * -      │         │
 * -      │         │
 * -      │         │
 * -      │         └──────────────┐
 * -      │                        │
 * -      │                        ├─┐
 * -      │                        ┌─┘
 * -      │                        │
 * -      └─┐  ┐  ┌───────┬──┐  ┌──┘
 * -        │ ─┤ ─┤       │ ─┤ ─┤
 * -        └──┴──┘       └──┴──┘
 * --------------- 神兽保佑 ---------------
 * --------------- 永无BUG! ---------------
 */

public class KeepAlive {
    private int interval; //心跳间隔时间
    private int timeout; //心跳超时时间
    private int reConnTime = 60 * 1000; //重连时间
    private IKeepAliveListener keepAliveListener;

    public KeepAlive(int interval, int timeout, IKeepAliveListener keepAliveListener) {
        this.interval = interval;
        this.timeout = timeout;
        this.keepAliveListener = keepAliveListener;
    }

    public KeepAlive(int interval, int timeout, int reConnTime, IKeepAliveListener keepAliveListener) {
        this.interval = interval;
        this.timeout = timeout;
        this.reConnTime = reConnTime;
        this.keepAliveListener = keepAliveListener;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getReConnTime() {
        return reConnTime;
    }

    public void setReConnTime(int reConnTime) {
        this.reConnTime = reConnTime;
    }

    public IKeepAliveListener getKeepAliveListener() {
        return keepAliveListener;
    }

    public void setKeepAliveListener(IKeepAliveListener keepAliveListener) {
        this.keepAliveListener = keepAliveListener;
    }
}
