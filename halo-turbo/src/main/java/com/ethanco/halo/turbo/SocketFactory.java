package com.ethanco.halo.turbo;

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
