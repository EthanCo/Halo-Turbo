package com.ethanco.halo.turbo.impl;

import android.os.Handler;

import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.utils.UIHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;

/**
 * @author EthanCo
 * @since 2017/1/17
 */

public abstract class AbstractMulticastSocket implements ISocket {

    protected final Config config;
    protected InetAddress address = null;
    protected MulticastSocket socket = null;
    protected ISession session = null;
    protected IHandler handler = null;
    protected ExecutorService threadPool;
    protected Handler uiHandler;

    public AbstractMulticastSocket(Config config) {
        this.config = config;
        this.threadPool = config.threadPool;
        this.uiHandler = UIHandler.getHandler();
        getHandler();
    }

    @Override
    public void connected() throws IOException {
        socket = new MulticastSocket(config.sourcePort);
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
}
