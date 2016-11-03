package com.ethanco.halo.turbo.ads;

import com.ethanco.halo.turbo.bean.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class absSocket<T> implements ISocket<T> {
    protected Config config;
    //是否正在运行 true运行状态 false停止状态
    protected boolean runningFlag = false;

    public absSocket(Config config) {
        this.config = config;
    }

    @Override
    public boolean isRunning() {
        return runningFlag;
    }

    @Override
    public void send(byte[] buffer) {
        send(buffer, 0, buffer.length);
    }

    @Override
    public void send(String str) {
        send(str.getBytes());
    }

    protected List<ReceiveListener<T>> mReceiveListeners = new ArrayList<>();

    public void addReceiveListener(ReceiveListener<T> receiveListener) {
        if (!mReceiveListeners.contains(receiveListener)) {
            mReceiveListeners.add(receiveListener);
        }
    }

    protected List<SocketListener<T>> mSocketListeners = new ArrayList<>();

    @Override
    public void addSocketListener(SocketListener socketListener) {
        if (!mSocketListeners.contains(socketListener)) {
            mSocketListeners.add(socketListener);
        }
    }
}