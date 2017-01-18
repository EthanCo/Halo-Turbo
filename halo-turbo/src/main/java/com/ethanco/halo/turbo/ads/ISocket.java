package com.ethanco.halo.turbo.ads;

import java.util.List;

/**
 * Created by EthanCo on 2016/9/14.
 */
public interface ISocket {
    boolean start();

    void stop();

    List<IHandler> getHandlers();

    void addHandler(IHandler handler);

    boolean removeHandler(IHandler handler);

    boolean isRunning();
}
