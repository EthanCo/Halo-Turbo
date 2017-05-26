package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.impl.socket.MulticastSocket;
import com.ethanco.halo.turbo.type.Mode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by EthanCo on 2016/9/19.
 */
class SocketFactory {

    public static ISocket create(Config config) {
        ISocket haloImpl;
        Mode mode = config.mode;

        if (mode == Mode.MULTICAST) {
            haloImpl = new MulticastSocket(config);
        } else {
            haloImpl = createByReflect(mode, config);
        }
        return haloImpl;
    }

    private static ISocket createByReflect(Mode mode, Config config) {
        String className;
        if (mode == Mode.MINA_NIO_TCP_CLIENT) {
            className = "com.ethanco.halo.turbo.mina.MinaTcpClientSocket";
        } else if (mode == Mode.MINA_NIO_TCP_SERVER) {
            className = "com.ethanco.halo.turbo.mina.MinaTcpServerSocket";
        } else if (mode == Mode.MINA_NIO_UDP_CLIENT) {
            className = "com.ethanco.halo.turbo.mina.MinaUdpClientSocket";
        } else if (mode == Mode.MINA_NIO_UDP_SERVER) {
            className = "com.ethanco.halo.turbo.mina.MinaUdpServerSocket";
        } else {
            return null;
        }

        ISocket haloImpl = null;
        try {
            Class cls = Class.forName(className);
            Constructor constructor = cls.getConstructor(Config.class);
            haloImpl = (ISocket) constructor.newInstance(config);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return haloImpl;
    }
}
