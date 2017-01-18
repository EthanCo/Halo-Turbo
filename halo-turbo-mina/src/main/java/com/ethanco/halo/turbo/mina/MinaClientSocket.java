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
    private final InetSocketAddress address;
    private NioSocketConnector mConnection;
    private IoSession mSession;

    public MinaClientSocket(Config config) {
        super(config);

        address = new InetSocketAddress(config.targetIP, config.targetPort);
        mConnection = new NioSocketConnector();
        mConnection.setDefaultRemoteAddress(address);
        mConnection.getFilterChain().addLast(LOGGER, new LoggingFilter());
        mConnection.getFilterChain().addLast(CODEC, new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        mConnection.setHandler(new MinaClientHandler());
        mConnection.getSessionConfig().setReadBufferSize(config.bufferSize);
        mConnection.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
    }

    @Override
    public boolean start() {
        super.start();
        ConnectFuture future = mConnection.connect();
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
        if (mConnection == null) {
            return;
        }
        if (mConnection.isDisposed() || mConnection.isDisposing()) {
            return;
        }

        mConnection.dispose();
        mConnection = null;
        mSession = null;
        //mAddress = null;
        onStopped();
    }

    @Override
    public boolean isRunning() {
        if (mConnection == null) {
            return false;
        }
        return mConnection.isActive();
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
