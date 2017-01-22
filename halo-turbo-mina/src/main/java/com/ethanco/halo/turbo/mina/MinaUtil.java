package com.ethanco.halo.turbo.mina;

import com.ethanco.halo.turbo.ads.ISession;

import org.apache.mina.core.session.IoSession;

/**
 * Mina 工具类
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public class MinaUtil {
    public static final String LOGGER = "logger";
    public static final String CODEC = "codec";

    public static ISession convertToISession(final IoSession ioSession) {
        return new ISession() {
            @Override
            public void write(Object message) {
                ioSession.write(message);
            }

            @Override
            public void close() {
                ioSession.closeOnFlush();
                //ioSession.closeNow();
            }
        };
    }
}
