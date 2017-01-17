package com.ethanco.halo.turbo.impl;

import android.os.Handler;

import com.ethanco.halo.turbo.ads.AbstractSession;
import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.utils.UIHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

/**
 * @author EthanCo
 * @since 2017/1/17
 */

public class MulticastSocket extends AbstractSocket {

    protected final Config config;
    protected InetAddress address = null;
    protected java.net.MulticastSocket socket = null;
    protected ISession session = null;
    protected IHandler handler = null;
    protected ExecutorService threadPool;
    protected Handler uiHandler;

    public MulticastSocket(Config config) {
        super(config);
        if (config.handler != null) {
            this.handler = config.handler;
        }
        getHandler();
        this.session = new DefaultSession();
        this.config = config;
        this.threadPool = config.threadPool;
        this.uiHandler = UIHandler.getHandler();

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.sessionCreated(session);
            }
        });
    }

    @Override
    public void connected() throws IOException {
        socket = new java.net.MulticastSocket(config.sourcePort);
        address = InetAddress.getByName(config.targetIP);
        socket.joinGroup(address);
        handler.sessionOpened(session);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    try {
                        receive();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void dispose() {
        if (socket == null) {
            return;
        }
        if (socket.isClosed()) {
            socket = null;
            return;
        }

        try {
            socket.leaveGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            socket = null;
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    handler.sessionClosed(session);
                }
            });
        }
    }

    @Override
    public boolean isRunning() {
        return socket != null;
    }

    protected void sent(final Object message, final byte[] buf) throws IOException {
        final DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, address, config.targetPort);
        socket.send(packet);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.messageSent(session, packet.getData());
            }
        });
    }

    protected void receive() throws IOException {
        final DatagramPacket packet;
        byte[] rev = new byte[config.bufferSize];
        packet = new DatagramPacket(rev, rev.length);
        socket.receive(packet);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.messageReceived(session, packet.getData());
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
