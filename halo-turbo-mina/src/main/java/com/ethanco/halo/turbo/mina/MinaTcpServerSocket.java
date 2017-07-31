package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

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

public class MinaTcpServerSocket extends AbstractSocket {

    private NioSocketAcceptor acceptor;
    private InetSocketAddress address;

    public MinaTcpServerSocket(Config config) {
        super(config);
    }

    private void init(Config config) {
        address = new InetSocketAddress(config.sourcePort);
        acceptor = new NioSocketAcceptor();
        if (acceptor.getFilterChain().get(LOGGER) == null) {
            acceptor.getFilterChain().addLast(LOGGER, new LoggingFilter());
        }
        if (acceptor.getFilterChain().get(CODEC) == null) {
            ProtocolCodecFactory codecFactory = config.codec == null ?
                    MinaUtil.getTextLineCodecFactory() : (ProtocolCodecFactory) config.codec;
            acceptor.getFilterChain().addLast(CODEC, new ProtocolCodecFilter(codecFactory));
        }
        acceptor.setHandler(new MinaServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(config.bufferSize);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
        acceptor.setReuseAddress(true); //避免重启时提示地址被占用
        //设置主服务监听端口的监听队列的最大值为50，如果当前已经有50个连接，新的连接将被服务器拒绝
        acceptor.setBacklog(50);

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

        init(config);
        try {
            acceptor.bind(address);
        } catch (IOException e) {
            onStartFailed(e);
            return false;
        }

        onStartSuccess();
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
            MinaTcpServerSocket.this.sessionCreated(convertToISession(session, MinaTcpServerSocket.this));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            MinaTcpServerSocket.this.sessionOpened(convertToISession(session, MinaTcpServerSocket.this));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaTcpServerSocket.this.messageReceived(convertToISession(session, MinaTcpServerSocket.this), receive(message));
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            //MinaServerSocket.this.messageSent(convertToISession(session, MinaServerSocket.this), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            MinaTcpServerSocket.this.sessionClosed(convertToISession(session, MinaTcpServerSocket.this));
        }
    }
}
