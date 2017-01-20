package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

import static com.ethanco.halo.turbo.mina.MinaUtil.CODEC;
import static com.ethanco.halo.turbo.mina.MinaUtil.LOGGER;
import static com.ethanco.halo.turbo.mina.MinaUtil.convertToISession;

/**
 * Mina Nio Tcp Socket
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaClientSocket extends AbstractSocket {
    private InetSocketAddress address;
    private NioSocketConnector connector;
    private IoSession mSession;

    public MinaClientSocket(Config config) {
        super(config);
    }

    private void init(Config config) {
        address = new InetSocketAddress(config.targetIP, config.targetPort);
        connector = new NioSocketConnector();
        connector.setDefaultRemoteAddress(address);
        connector.getFilterChain().addLast(LOGGER, new LoggingFilter());
        connector.getFilterChain().addLast(CODEC, new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        connector.setHandler(new MinaClientHandler());
        connector.getSessionConfig().setReadBufferSize(config.bufferSize);
        connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
    }

    @Override
    public boolean start() {
        super.start();
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
        return connector.isActive();
    }

    private class MinaClientHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            MinaClientSocket.this.sessionCreated(convertToISession(session));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            MinaClientSocket.this.sessionOpened(convertToISession(session));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaClientSocket.this.messageReceived(convertToISession(session), message);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            MinaClientSocket.this.messageSent(convertToISession(session), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            MinaClientSocket.this.sessionClosed(convertToISession(session));
        }
    }
}
