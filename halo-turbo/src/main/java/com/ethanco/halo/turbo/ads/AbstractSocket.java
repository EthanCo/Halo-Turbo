package com.ethanco.halo.turbo.ads;

import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.impl.EmptyHandler;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class AbstractSocket implements ISocket {
    protected Config config;
    private IHandler handler;

    public AbstractSocket(Config config) {
        this.config = config;
    }

    @Override
    public IHandler getHandler() {
        if (handler == null) {
            handler = new EmptyHandler();
        }
        return handler;
    }

    @Override
    public void setHandler(IHandler handler) {
        this.handler = handler;
    }
}
