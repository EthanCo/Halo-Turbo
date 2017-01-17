package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;

/**
 * 空的Handler
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class EmptyHandler implements IHandler {
    @Override
    public void sessionCreated(ISession var1) {

    }

    @Override
    public void sessionOpened(ISession var1) {

    }

    @Override
    public void sessionClosed(ISession var1) {

    }

    @Override
    public void messageReceived(ISession var1, Object var2) {

    }

    @Override
    public void messageSent(ISession var1, Object var2) {

    }
}
