package com.ethanco.halo.turbo.ads;

import com.ethanco.halo.turbo.bean.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class absSocket<T> implements ISocket<T> {
    protected Config config;
    //运行状态
    protected State state = State.STOPED;

    public absSocket(Config config) {
        this.config = config;
        //init(config);
    }

    @Override
    public boolean isRunning() {
        //默认以State判断，可重写
        return this.state == State.STARTED;
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

    protected List<StateListener> mStateListeners = new ArrayList<>();

    protected void onStarted() {
        for (StateListener mSocketListener : mStateListeners) {
            mSocketListener.onStarted();
        }
    }

    protected void onStoped() {
        for (StateListener mSocketListener : mStateListeners) {
            mSocketListener.onStoped();
        }
    }

    @Override
    public void addStateListener(StateListener socketListener) {
        if (!mStateListeners.contains(socketListener)) {
            mStateListeners.add(socketListener);
        }
    }

    protected List<ErrorListener> mErrorListeners = new ArrayList<>();

    protected void onError(Error error, Exception e) {
        for (ErrorListener mErrorListener : mErrorListeners) {
            mErrorListener.onError(error, e);
        }
    }

    @Override
    public void addErrorListener(ErrorListener errorListener) {
        if (!mErrorListeners.contains(errorListener)) {
            mErrorListeners.add(errorListener);
        }
    }

    @Override
    public void addSocketListener(SocketListener<T> socketListener) {
        addReceiveListener(socketListener);
        addStateListener(socketListener);
        addErrorListener(socketListener);
    }

}
