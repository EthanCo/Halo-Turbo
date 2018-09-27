package com.ethanco.halo.turbo.mina;

import android.os.SystemClock;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.bean.KeepAlive;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

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

public class MinaTcpClientSocket extends AbstractSocket {
    private InetSocketAddress address;
    private NioSocketConnector connector;
    private IoSession mSession;
    private volatile boolean isAutoReConn = true;
    private boolean isClosed = false;

    public MinaTcpClientSocket(Config config) {
        super(config);
    }

    private void init(Config config) {
        address = new InetSocketAddress(config.targetIP, config.targetPort);
        connector = new NioSocketConnector();
        connector.setDefaultRemoteAddress(address);
        if (connector.getFilterChain().get(LOGGER) == null) {
            connector.getFilterChain().addLast(LOGGER, new LoggingFilter());
        }
        if (connector.getFilterChain().get(CODEC) == null) {
            ProtocolCodecFactory codecFactory = config.codec == null ?
                    MinaUtil.getTextLineCodecFactory() : (ProtocolCodecFactory) config.codec;
            connector.getFilterChain().addLast(CODEC, new ProtocolCodecFilter(codecFactory));
        }
        connector.setHandler(new MinaClientHandler());
        connector.getSessionConfig().setReadBufferSize(config.bufferSize);
        connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
        KeepAliveFilter keepAliveFilter = MinaUtil.initClientKeepAlive(config, this);
        if (keepAliveFilter != null) {
            connector.getFilterChain().addLast(HEARTBEAT, keepAliveFilter);
        }
    }

    @Override
    public boolean start() {
        super.start();
        isAutoReConn = true;
        if (isRunning()) {
            return false;
        }

        init(config);
        ConnectFuture future = connector.connect();
        future.awaitUninterruptibly();
        try {
            mSession = future.getSession();
        } catch (Exception e) {
            onStartFailed(e);
            return false;
        }

        onStartSuccess();
        return true;
        //return mSession == null ? false : true;
    }

    @Override
    public void stop() {
        super.stop();
        isAutoReConn = false;
        if (connector == null) {
            return;
        }
        if (connector.isDisposed() || connector.isDisposing()) {
            return;
        }

        connector.dispose();
        connector = null;
        mSession = null;
        address = null;
        onStopped();
    }

    @Override
    public boolean isRunning() {
        if (connector == null) {
            return false;
        }
        return connector.isActive() && !isClosed;
    }

    private class MinaClientHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            MinaTcpClientSocket.this.sessionCreated(convertToISession(session, MinaTcpClientSocket.this));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            isClosed = false;
            MinaTcpClientSocket.this.sessionOpened(convertToISession(session, MinaTcpClientSocket.this));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaTcpClientSocket.this.messageReceived(convertToISession(session, MinaTcpClientSocket.this), receive(message));
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
            isClosed = true;
            MinaTcpClientSocket.this.sessionClosed(convertToISession(session, MinaTcpClientSocket.this));

            //断线重连 详见:https://my.oschina.net/yjwxh/blog/174633
            synchronized (this) {

                if (!isAutoReConn) return;
                if (isRunning()) return;
                ExecutorService threadPool = config.threadPool;
                final KeepAlive keepAlive = config.keepAlive;
                if (keepAlive != null) return; //如果keepAlive不为NULL，则交由keepAlive进行重连处理

                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean reConn = isAutoReConn && !isRunning();
                        System.out.println("开始重连 reConn:" + reConn);
                        do {
                            try {
                                onReConnecting();
                                MinaTcpClientSocket.this.stop();

                                init(config);
                                MinaTcpClientSocket.this.start();
                                onReConnected();
                                int reConnTime = keepAlive == null ? 10 * 1000 : keepAlive.getReConnTime();
                                System.out.println("开始重连-->Sleep:" + reConnTime);
                                SystemClock.sleep(reConnTime);
                                reConn = isAutoReConn && !isRunning();
                                System.out.println("开始重连--->reConn:" + reConn);
                            } catch (Exception e) {
                                onReConnectFailed(e);
                            }
                        } while (reConn);
                    }
                });
            }
        }
    }
}
