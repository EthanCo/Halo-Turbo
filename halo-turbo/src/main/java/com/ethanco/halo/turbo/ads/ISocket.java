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

    interface StateListener {
        void onStarted();

        void onStoped();
    }

    void addStateListener(StateListener socketListener);

    interface ErrorListener {
        void onError(Error error, Exception exception);
    }

    void addErrorListener(ErrorListener errorListener);

    interface SocketListener<T> extends ReceiveListener<T>, StateListener, ErrorListener {

    }

    void addSocketListener(SocketListener<T> socketListener);
}
