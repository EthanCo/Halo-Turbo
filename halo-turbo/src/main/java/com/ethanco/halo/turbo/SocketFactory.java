package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.impl.MulticastSocket;
import com.ethanco.halo.turbo.type.Mode;

/**
 * Created by EthanCo on 2016/9/19.
 */
public class SocketFactory {

    public static ISocket create(Config config) {
        ISocket haloImpl = null;
        Mode mode = config.mode;
//        if (mode == Mode.TCP_CLIENT) {
//            haloImpl = new TcpClientSocket(config);
//        } else if (mode == Mode.TCP_SERVICE) {
//            haloImpl = new TcpServerSocket(config);
//        } else {
//            haloImpl = new LogHaloImpl(config); //TODO test
//        }

        if (mode == Mode.MULTICAST) {
            haloImpl = new MulticastSocket(config);
        }
        return haloImpl;
    }
}
