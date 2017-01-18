package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.ethanco.halo.turbo.mina.MinaUtil.CODEC;
import static com.ethanco.halo.turbo.mina.MinaUtil.LOGGER;
import static com.ethanco.halo.turbo.mina.MinaUtil.convertToISession;

/**
 * Mina Nio Tcp Server
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaServerSocket extends AbstractSocket {
    private NioSocketAcceptor acceptor;

    public MinaServerSocket(Config config) {
        super(config);

        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast(LOGGER, new LoggingFilter());
        acceptor.getFilterChain().addLast(CODEC,
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        acceptor.setHandler(new MinaServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(config.bufferSize);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
    }

    @Override
    public boolean start() {
        super.start();
        try {
            acceptor.bind(new InetSocketAddress(config.sourcePort));
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

        acceptor.dispose();
        acceptor = null;

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
            MinaServerSocket.this.sessionCreated(convertToISession(session));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            MinaServerSocket.this.sessionOpened(convertToISession(session));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            MinaServerSocket.this.messageReceived(convertToISession(session), message);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            MinaServerSocket.this.messageSent(convertToISession(session), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            MinaServerSocket.this.sessionClosed(convertToISession(session));
        }
    }
}
