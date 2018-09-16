package com.ethanco.halo.turbo.mina;

import android.support.annotation.NonNull;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.ads.IKeepAliveListener;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.bean.KeepAlive;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Mina 工具类
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public class MinaUtil {
    private static final int MAX_LINE_LENGTH = 1024 * 20;
    public static final String LOGGER = "logger";
    public static final String CODEC = "codec";
    public static final String HEARTBEAT = "heartbeat";

    //public static Map<IoSession, ISession> sessionMap = new WeakHashMap<>();
    public static Map<IoSession, ISession> sessionMap = new HashMap<>();

    public static ISession convertToISession(final IoSession ioSession, final AbstractSocket _socket) {
        ISession session = sessionMap.get(ioSession);

        if (session == null) {
            session = new ISession() {
                private AbstractSocket socket = _socket;
                //WeakReference<AbstractSocket> socketRef = new WeakReference<>(socket);

                @Override
                public void write(final Object message) {
                    final ISession finalSession = this;
                    //final AbstractSocket socket = socketRef.get();
                    if (socket == null) {
                        return;
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            Object result = socket.convert(message);
                            ioSession.write(result);
                            socket.messageSent(finalSession, message);
                        }
                    }.start();
                }

                @Override
                public void close() {
                    sessionMap.remove(ioSession);
                    //socketRef.clear();
                    socket = null;
                    ioSession.closeOnFlush();
                    //ioSession.closeNow();
                }
//                public void setIoSession(IoSession ioSession) {
//
//                }
            };
            sessionMap.put(ioSession, session);
        }

        return session;
    }

    @NonNull
    static TextLineCodecFactory getTextLineCodecFactory() {
        TextLineCodecFactory codec = new TextLineCodecFactory();
        codec.setDecoderMaxLineLength(MAX_LINE_LENGTH);
        codec.setEncoderMaxLineLength(MAX_LINE_LENGTH);
        return codec;
    }

    static KeepAliveFilter initClientKeepAlive(Config config, final AbstractSocket socket) {
        final KeepAlive keepAlive = config.keepAlive;
        if (keepAlive == null) return null;
        final IKeepAliveListener keepAliveListener = keepAlive.getKeepAliveListener();
        if (keepAliveListener == null) return null;
        final int interval = keepAlive.getInterval();
        final int timeout = keepAlive.getTimeout();
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(new KeepAliveMessageFactory() {
            @Override
            public boolean isRequest(IoSession ioSession, Object o) {
                return false;
            }

            @Override
            public boolean isResponse(IoSession ioSession, Object o) {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                return keepAliveListener.isKeepAliveMessage(iSession, o);
            }

            @Override
            public Object getRequest(IoSession ioSession) {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                return keepAliveListener.getKeepAliveMessage(iSession, null);
            }

            @Override
            public Object getResponse(IoSession ioSession, Object o) {
                return null;
            }
        }, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE, interval, timeout);
        keepAliveFilter.setForwardEvent(true);
        keepAliveFilter.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandler() {
            @Override
            public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                keepAliveListener.onKeepAliveRequestTimedOut(keepAlive, iSession);
            }
        });
        return keepAliveFilter;
    }

    static KeepAliveFilter initServerKeepAlive(Config config, final AbstractSocket socket) {
        final KeepAlive keepAlive = config.keepAlive;
        if (keepAlive == null) return null;
        final IKeepAliveListener keepAliveListener = keepAlive.getKeepAliveListener();
        if (keepAliveListener == null) return null;
        final int interval = keepAlive.getInterval();
        final int timeout = keepAlive.getTimeout();
        KeepAliveFilter keepAliveFilter = new KeepAliveFilter(new KeepAliveMessageFactory() {
            @Override
            public boolean isRequest(IoSession ioSession, Object o) {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                return keepAliveListener.isKeepAliveMessage(iSession, o);
            }

            @Override
            public boolean isResponse(IoSession ioSession, Object o) {
                return false;
            }

            @Override
            public Object getRequest(IoSession ioSession) {
                return null;
            }

            @Override
            public Object getResponse(IoSession ioSession, Object o) {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                return keepAliveListener.getKeepAliveMessage(iSession, null);
            }
        }, IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE, interval, timeout);
        keepAliveFilter.setForwardEvent(true);
        keepAliveFilter.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandler() {
            @Override
            public void keepAliveRequestTimedOut(KeepAliveFilter keepAliveFilter, IoSession ioSession) throws Exception {
                ISession iSession = MinaUtil.convertToISession(ioSession, socket);
                keepAliveListener.onKeepAliveRequestTimedOut(keepAlive, iSession);
            }
        });
        return keepAliveFilter;
    }

    static InetAddress getLocalHostLANAddress() throws Exception {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
