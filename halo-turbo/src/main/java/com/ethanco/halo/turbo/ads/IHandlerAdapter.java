package com.ethanco.halo.turbo.ads;

/**
 * Handler 默认实现
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public abstract class IHandlerAdapter implements IHandler {

    @Override
    public void sessionCreated(ISession session) {

    }

    @Override
    public void sessionOpened(ISession session) {

    }

    @Override
    public void sessionClosed(ISession session) {

    }

    @Override
    public void messageReceived(ISession session, Object message) {

    }

    @Override
    public void messageSent(ISession session, Object message) {

    }
}
