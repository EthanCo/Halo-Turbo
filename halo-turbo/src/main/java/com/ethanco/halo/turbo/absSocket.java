package com.ethanco.halo.turbo;

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

    //public abstract void init(Config config);

    @Override
    public void send(byte[] buffer) {
        send(buffer, 0, buffer.length);
    }

    @Override
    public void send(String str) {
        send(str.getBytes());
    }

    protected List<ReceiveListener<T>> mReceiveListeners = new ArrayList<>();

    /*interface ReceiveListener<T> {
        void onReceive(T buffer);
    }*/

    public void addReceiveListener(ReceiveListener<T> receiveListener) {
        if (!mReceiveListeners.contains(receiveListener)) {
            mReceiveListeners.add(receiveListener);
        }
    }

    protected List<SocketListener<T>> mSocketListeners = new ArrayList<>();

    /*interface SocketListener<T> extends ReceiveListener<T> {
        void onStart();

        void onStop();
    }*/

    @Override
    public void addSocketListener(SocketListener socketListener) {
        if (!mSocketListeners.contains(socketListener)) {
            mSocketListeners.add(socketListener);
        }
    }
}
