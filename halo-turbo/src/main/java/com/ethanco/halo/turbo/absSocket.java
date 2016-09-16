package com.ethanco.halo.turbo;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class absSocket implements ISocket {
    protected Config config;

    public absSocket(Config config) {
        this.config = config;
        init(config);
    }

    public abstract void init(Config config);

    @Override
    public void send(byte[] buffer) {
        send(buffer, 0, buffer.length);
    }

    @Override
    public void send(String str) {
        send(str.getBytes());
    }
}
