package com.ethanco.halo.turbo.impl.socket;

import com.ethanco.halo.turbo.ads.AbstractSession;
import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Udp Socket
 *
 * @author EthanCo
 * @since 2018/8/17
 */

public class UdpClientSocket extends AbstractSocket {
    protected ExecutorService threadPool;
    UdpSocket socket;

    public UdpClientSocket(Config config) {
        super(config);
        checkConfig(config);
        assignThreadPool(config);
        initSession();
    }

    private void checkConfig(Config config) {
        if (config.codec != null) {
            throw new IllegalArgumentException("udp client not support codec");
        }
    }

    private void assignThreadPool(Config config) {
        if (config.threadPool == null) {
            this.threadPool = Executors.newCachedThreadPool();
        } else {
            this.threadPool = config.threadPool;
        }
    }

    private void initSession() {
        session = new DefaultSession();
        sessionCreated(session);
    }

    @Override
    public boolean isRunning() {
        return socket!=null;
    }

    @Override
    public boolean start() {
        //return super.start();
        socket = new UdpSocket(threadPool);
        socket.bind(config.sourcePort);
        socket.connect(config.targetIP, config.targetPort);
        sessionOpened(session);
        return true;
    }

    @Override
    public void stop() {
        super.stop();

        if (socket!=null) {
            socket.disconnect();
            socket = null;
            sessionClosed(session);
        }
    }

    protected void sent(final Object object) throws IOException {
        Object convertData = convert(object);
        if (convertData instanceof Byte[] || convertData instanceof byte[]) {
            sent((byte[]) convertData);
        } else {
            sent(convertData.toString().getBytes());
        }
        messageSent(session, object);
    }

    protected void sent(final byte[] buf) throws IOException {
       /* final DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, address, config.targetPort);*/
        socket.send(buf, 0, buf.length);
    }

    private class DefaultSession extends AbstractSession {
        @Override
        public void write(final Object message) {

            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sent(message);
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
    }
}
