package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.bean.Config;

/**
 * Created by EthanCo on 2016/9/17.
 */
public class ByteTcpServerSocket extends TcpServerSocket<byte[]> {
    public ByteTcpServerSocket(Config config) {
        super(config);
    }

    @Override
    byte[] convert(byte[] data) {
        return data;
    }
}
