package com.ethanco.halo.turbo.mina;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static com.ethanco.halo.turbo.mina.MinaUtil.CODEC;
import static com.ethanco.halo.turbo.mina.MinaUtil.HEARTBEAT;
import static com.ethanco.halo.turbo.mina.MinaUtil.LOGGER;
import static com.ethanco.halo.turbo.mina.MinaUtil.convertToISession;

/**
 * Mina Nio Tcp Socket
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaUdpClientSocket extends AbstractSocket {
    private InetSocketAddress address;
    private NioDatagramConnector connector;
    private WifiManager.MulticastLock lock;

    public MinaUdpClientSocket(Config config) {
        super(config);
    }

    private void init(Config config) {
        connector = new NioDatagramConnector();
        connector.setHandler(new MinaClientHandler());
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        if (connector.getFilterChain().get(LOGGER) == null) {
            chain.addLast(LOGGER, new LoggingFilter());
        }
        if (connector.getFilterChain().get(CODEC) == null) {
            ProtocolCodecFactory codecFactory = config.codec == null ?
                    MinaUtil.getTextLineCodecFactory() : (ProtocolCodecFactory) config.codec;
            chain.addLast(CODEC, new ProtocolCodecFilter(codecFactory));
        }
        connector.getSessionConfig().setReadBufferSize(config.bufferSize);

        connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
        connector.getSessionConfig().setBroadcast(true);
        KeepAliveFilter keepAliveFilter = MinaUtil.initClientKeepAlive(config, this);
        if (keepAliveFilter != null) {
            connector.getFilterChain().addLast(HEARTBEAT, keepAliveFilter);
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
        lock = manager.createMulticastLock("halo mina udp client");

        lock.acquire();

        init(config);
        InetSocketAddress remoteAddress = new InetSocketAddress(config.targetIP, config.targetPort);
        InetSocketAddress localAddress = null;
        try {
            InetAddress localIP = MinaUtil.getLocalHostLANAddress();
            localAddress = new InetSocketAddress(localIP, config.sourcePort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectFuture future;
        if (localAddress != null) {
            future = connector.connect(remoteAddress, localAddress);
        }else{
            future = connector.connect(remoteAddress);
        }
        future.awaitUninterruptibly();
        future.addListener(new IoFutureListener() {
            public void operationComplete(IoFuture future) {
                ConnectFuture connFuture = (ConnectFuture) future;
                if (connFuture.isConnected()) {
                    synchronized (MinaUdpClientSocket.this) {
                        session = MinaUtil.convertToISession(future.getSession(), MinaUdpClientSocket.this);
                        onStartSuccess();
                    }
                } else {
                    onStartFailed(new Exception("Not connected...exiting"));
                }
            }
        });

        lock.release();
        return true;
        //return mSession == null ? false : true;
    }

    @Override
    public void stop() {
        super.stop();
        if (connector == null) {
            return;
        }
        if (connector.isDisposed() || connector.isDisposing()) {
            return;
        }

        connector.dispose();
        connector = null;
        address = null;
        onStopped();
    }

    @Override
    public boolean isRunning() {
        if (connector == null) {
            return false;
        }
        return connector.isActive();
    }

    private class MinaClientHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            //MinaUdpClientSocket.this.sessionCreated(convertToISession(session, MinaUdpClientSocket.this));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            MinaUdpClientSocket.this.sessionOpened(convertToISession(session, MinaUdpClientSocket.this));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaUdpClientSocket.this.messageReceived(convertToISession(session, MinaUdpClientSocket.this), receive(message));
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            //Log.i("Z-Test", "messageSent:" + message);
            //MinaClientSocket.this.messageSent(convertToISession(session, MinaClientSocket.this), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            MinaUdpClientSocket.this.sessionClosed(convertToISession(session, MinaUdpClientSocket.this));
        }
    }
}
