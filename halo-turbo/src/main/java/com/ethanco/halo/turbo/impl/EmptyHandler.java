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
