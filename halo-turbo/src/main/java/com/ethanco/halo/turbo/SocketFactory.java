package com.ethanco.halo.turbo;

import com.ethanco.halo.turbo.ads.ISocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.impl.ByteTcpClientSocket;
import com.ethanco.halo.turbo.impl.ByteTcpServerSocket;
import com.ethanco.halo.turbo.impl.LogHaloImpl;
import com.ethanco.halo.turbo.type.Mode;

/**
 * Created by EthanCo on 2016/9/19.
 */
public class SocketFactory {

    public static ISocket create(Config config) {
        ISocket haloImpl;
        Mode mode = config.mode;
        if (mode == Mode.TCP_CLIENT) {
            haloImpl = new ByteTcpClientSocket(config);
        } else if (mode == Mode.TCP_SERVICE) {
            haloImpl = new ByteTcpServerSocket(config);
        } else {
            haloImpl = new LogHaloImpl(config); //TODO test
        }
        return haloImpl;
    }
}
