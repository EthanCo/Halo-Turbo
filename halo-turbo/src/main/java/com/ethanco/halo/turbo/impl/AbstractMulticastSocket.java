package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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

    public AbstractMulticastSocket(Config config) {
        this.config = config;
        getHandler();
    }

    @Override
    public void connected() throws IOException {
        socket = new MulticastSocket(config.sourcePort);
        address = InetAddress.getByName(config.targetIP);
        socket.joinGroup(address);
        handler.sessionOpened(session);

        config.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    receive();
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
            handler.sessionClosed(session);
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

    protected void sent(Object message, byte[] buf) {
        DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, address, config.targetPort);
        try {
            socket.send(packet);
            handler.messageSent(session, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void receive() {
        DatagramPacket packet;
        byte[] rev = new byte[config.bufferSize];
        packet = new DatagramPacket(rev, rev.length);
        try {
            socket.receive(packet);
            handler.messageReceived(session, packet.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
