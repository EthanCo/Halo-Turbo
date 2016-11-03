package com.ethanco.halo.turbo.ads;

/**
 * Created by EthanCo on 2016/9/14.
 */
public interface ISocket<T> {
    void start();

    void stop();

    void send(final byte[] buffer, final int offset, final int length);

    void send(final byte[] buffer);

    void send(final String str);

    boolean isRunning();

    interface ReceiveListener<T> {
        void onReceive(T buffer);
    }

    void addReceiveListener(ReceiveListener<T> receiveListener);

    interface SocketListener<T> extends ReceiveListener<T> {
        void onStart();

        void onStop();
    }

    void addSocketListener(SocketListener socketListener);
}
