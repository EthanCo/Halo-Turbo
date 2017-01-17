package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.ads.AbstractSession;
import com.ethanco.halo.turbo.bean.Config;

import java.io.IOException;

/**
 * @author EthanCo
 * @since 2017/1/17
 */

public class MulticastSocket extends AbstractMulticastSocket {

    public MulticastSocket(Config config) {
        super(config);
        if (config.handler != null) {
            this.handler = config.handler;
        }
        this.session = new DefaultSession();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.sessionCreated(session);
            }
        });
    }

    private class DefaultSession extends AbstractSession {
        @Override
        public void write(final Object message) {

            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buf = convertToBuffer(message);
                    if (buf == null) return;

                    try {
                        sent(message, buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private byte[] convertToBuffer(Object message) {
            byte[] buf = null;
            if (message instanceof byte[]) {
                buf = (byte[]) (message);
            } else if (message instanceof String) {
                String s = String.valueOf(message);
                buf = s.getBytes();
            } else {
                throw new IllegalArgumentException("message type is not supported");
            }

            if (buf == null) {
                return null;
            }
            return buf;
        }
    }
}
