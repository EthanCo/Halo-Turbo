package com.ethanco.halo.turbo.mina;

import android.support.annotation.NonNull;

import com.ethanco.halo.turbo.ads.AbstractSocket;
import com.ethanco.halo.turbo.ads.ISession;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import java.lang.ref.WeakReference;
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

    //public static Map<IoSession, ISession> sessionMap = new WeakHashMap<>();
    public static Map<IoSession, ISession> sessionMap = new HashMap<>();

    public static ISession convertToISession(final IoSession ioSession, final AbstractSocket socket) {
        ISession session = sessionMap.get(ioSession);

        if (session == null) {
            session = new ISession() {
                WeakReference<AbstractSocket> socketRef = new WeakReference<>(socket);

                @Override
                public void write(final Object message) {
                    final ISession finalSession = this;
                    final AbstractSocket socket = socketRef.get();
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
                    socketRef.clear();
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
}
