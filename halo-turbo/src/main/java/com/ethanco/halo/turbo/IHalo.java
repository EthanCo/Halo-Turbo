package com.ethanco.halo.turbo;

/**
 * Created by EthanCo on 2016/9/14.
 */
public interface IHalo {
    void start();

    void reStart();

    void stop();

    void send(final byte[] buffer, final int offset, final int length);

    void send(final byte[] buffer);

    void send(final String str);

    //void send(final Object obj);
}
