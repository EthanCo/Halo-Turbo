package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.ads.IHandler;
import com.ethanco.halo.turbo.ads.ISession;
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

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public class MinaServerSocket extends AbstractSocket {

    public static final String LOGGER = "Logger";
    public static final String CODEC = "codec";
    private IHandler handler;
    private NioSocketAcceptor acceptor;

    public MinaServerSocket(Config config) {
        super(config);

        this.handler = config.handler;
        getHandler();

        //创建一个IoAcceptor
        acceptor = new NioSocketAcceptor();
        //得到Mima为我们提供的默认的日志过滤器
        acceptor.getFilterChain().addLast(LOGGER, new LoggingFilter());
        //添加Codec过滤器
        acceptor.getFilterChain().addLast(CODEC,
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        //设置事件处理Handler
        acceptor.setHandler(new MinaServerHandler());
        //设置读缓存区大小
        acceptor.getSessionConfig().setReadBufferSize(config.bufferSize);
        //设置空闲时间 10后没有任何读写，回到空闲状态
        //IdleStatus.BOTH_IDLE 读和写 IdleStatus.READER_IDLE 读 IdleStatus.WRITER_IDLE 写
        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 10);
    }

    @Override
    public void connected() throws IOException {
        acceptor.bind(new InetSocketAddress(config.sourcePort));
    }

    @Override
    public void dispose() {
        if (acceptor == null) {
            return;
        }
        if (acceptor.isDisposed() || acceptor.isDisposing()) {
            return;
        }

        acceptor.dispose();
        acceptor = null;
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

    //TODO 多个地方用到了
    private ISession convertToISession(final IoSession ioSession) {
        return new ISession() {
            @Override
            public void write(Object message) {
                ioSession.write(message);
            }
        };
    }
}
