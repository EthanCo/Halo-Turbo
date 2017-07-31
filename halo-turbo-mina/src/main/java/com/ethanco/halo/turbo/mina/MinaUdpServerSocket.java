package com.ethanco.halo.turbo.mina;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.ethanco.halo.turbo.mina.MinaUtil.CODEC;
import static com.ethanco.halo.turbo.mina.MinaUtil.HEARTBEAT;
import static com.ethanco.halo.turbo.mina.MinaUtil.LOGGER;
import static com.ethanco.halo.turbo.mina.MinaUtil.convertToISession;

/**
 * Mina Nio Tcp Server
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaUdpServerSocket extends AbstractSocket {

    private NioDatagramAcceptor acceptor;
    private InetSocketAddress address;
    private WifiManager.MulticastLock lock;

    public MinaUdpServerSocket(Config config) {
        super(config);
    }

    private void init(Config config) {
        address = new InetSocketAddress(config.sourcePort);

        acceptor = new NioDatagramAcceptor();
        acceptor.setHandler(new MinaServerHandler());
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        if (acceptor.getFilterChain().get(LOGGER) == null) {
            chain.addLast(LOGGER, new LoggingFilter());
        }
        if (acceptor.getFilterChain().get(CODEC) == null) {
            ProtocolCodecFactory codecFactory = config.codec == null ?
                    MinaUtil.getTextLineCodecFactory() : (ProtocolCodecFactory) config.codec;
            chain.addLast(CODEC, new ProtocolCodecFilter(codecFactory));
        }
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        dcfg.setReadBufferSize(config.bufferSize);
        dcfg.setIdleTime(IdleStatus.WRITER_IDLE, 10);
        dcfg.setBroadcast(true);

        KeepAliveFilter keepAliveFilter = MinaUtil.initServerKeepAlive(config, this);
        if (keepAliveFilter != null) {
            acceptor.getFilterChain().addLast(HEARTBEAT, keepAliveFilter);
        }
    }


    @Override
    public boolean start() {
        super.start();
        if (isRunning()) {
            return false;
        }

        Context context = config.context;
        if (context == null) {
            throw new IllegalArgumentException("context is null，please set context first.");
        }

        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("halo mina udp server");

        lock.acquire();

        init(config);
        try {
            acceptor.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        onStartSuccess();
        lock.release();
        return true;
    }

    @Override
    public void stop() {
        super.stop();
        if (acceptor == null) {
            return;
        }
        if (acceptor.isDisposed() || acceptor.isDisposing()) {
            return;
        }

        acceptor.unbind(address);
        acceptor.dispose();
        acceptor = null;
        address = null;

        onStopped();
    }

    @Override
    public boolean isRunning() {
        if (acceptor == null) {
            return false;
        }

        return acceptor.isActive();
    }

    private class MinaServerHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            MinaUdpServerSocket.this.sessionCreated(convertToISession(session, MinaUdpServerSocket.this));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            MinaUdpServerSocket.this.sessionOpened(convertToISession(session, MinaUdpServerSocket.this));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaUdpServerSocket.this.messageReceived(convertToISession(session, MinaUdpServerSocket.this), receive(message));
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            //MinaServerSocket.this.messageSent(convertToISession(session, MinaServerSocket.this), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            MinaUdpServerSocket.this.sessionClosed(convertToISession(session, MinaUdpServerSocket.this));
        }
    }
}
