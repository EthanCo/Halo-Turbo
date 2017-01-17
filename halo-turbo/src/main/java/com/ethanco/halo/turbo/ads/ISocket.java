package com.ethanco.halo.turbo.ads;

import java.io.IOException;

/**
 * Created by EthanCo on 2016/9/14.
 */
public interface ISocket {
    void connected() throws IOException;

    void dispose();

    IHandler getHandler();

    void setHandler(IHandler handler);

    boolean isRunning();
}
