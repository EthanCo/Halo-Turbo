package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.bean.Config;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaClientSocket extends AbstractSocket {
    public static final String LOGGING = "logging";
    public static final String CODEC = "codec";
    private final InetSocketAddress address;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private IHandler handler;

    public MinaClientSocket(Config config) {
        super(config);

        this.handler = config.handler;
        getHandler();

        address = new InetSocketAddress(config.targetIP, config.targetPort);
        mConnection = new NioSocketConnector();
        mConnection.setDefaultRemoteAddress(address);
        mConnection.getSessionConfig().setReadBufferSize(config.bufferSize);
        mConnection.getFilterChain().addLast(LOGGING, new LoggingFilter());
        mConnection.getFilterChain().addLast(CODEC, new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        mConnection.setHandler(new MinaClientHandler());
    }

    @Override
    public void connected() throws IOException {
        ConnectFuture future = mConnection.connect();
        future.awaitUninterruptibly();//一直等到它连接为止
        mSession = future.getSession();

        //return mSession == null ? false : true;
    }

    @Override
    public void dispose() {
        if (mConnection == null) {
            return;
        }
        if (mConnection.isDisposed() || mConnection.isDisposing()) {
            return;
        }

        mConnection.dispose();
        mConnection = null;
        mSession = null;
//        mAddress = null;
//        mContextRef = null;
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
            handler.sessionCreated(convertToISession(session));
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            handler.sessionOpened(convertToISession(session));
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            handler.messageReceived(convertToISession(session), message);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            handler.messageSent(convertToISession(session), message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            handler.sessionClosed(convertToISession(session));
        }
    }

    private ISession convertToISession(final IoSession ioSession) {
        return new ISession() {
            @Override
            public void write(Object message) {
                ioSession.write(message);
            }
        };
    }
}
