package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.ads.AbstractSession;
import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author EthanCo
 * @since 2017/1/17
 */

public class MulticastSocket extends AbstractSocket {

    protected InetAddress address = null;
    protected java.net.MulticastSocket socket = null;
    protected ExecutorService threadPool;

    public MulticastSocket(Config config) {
        super(config);

        assignThreadPool(config);
        initSession();
    }

    private void initSession() {
        session = new DefaultSession();
        sessionCreated(session);
    }

    private void assignThreadPool(Config config) {
        if (config.threadPool == null) {
            this.threadPool = Executors.newCachedThreadPool();
        } else {
            this.threadPool = config.threadPool;
        }
    }

    @Override
    public boolean start() {
        super.start();
        if (isRunning()) {
            return false;
        }

        try {
            socket = new java.net.MulticastSocket(config.sourcePort);
            address = InetAddress.getByName(config.targetIP);
            socket.joinGroup(address);
            sessionOpened(session);
        } catch (IOException e) {
            onStartFailed(e);
            return false;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    try {
                        receive();
                    } catch (IOException e) {
                        onReceiveException(e);
                    }
                }
            }
        });

        onStartSuccess();
        return true;
    }

    @Override
    public void stop() {
        super.stop();
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
            sessionClosed(session);
            onStopped();
        }
    }

    @Override
    public boolean isRunning() {
        return socket != null;
    }

    protected void sent(final byte[] buf) throws IOException {
        final DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, address, config.targetPort);
        socket.send(packet);
        messageSent(session, packet.getData());
    }

    protected void receive() throws IOException {
        final DatagramPacket packet;
        byte[] rev = new byte[config.bufferSize];
        packet = new DatagramPacket(rev, rev.length);
        socket.receive(packet);
        messageReceived(session, packet.getData());
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
                        sent(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void close() {
            stop();
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
