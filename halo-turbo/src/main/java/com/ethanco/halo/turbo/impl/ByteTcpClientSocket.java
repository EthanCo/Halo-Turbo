package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.bean.Config;

/**
 * Created by EthanCo on 2016/9/16.
 */
public class ByteTcpClientSocket extends TcpClientSocket<byte[]> {
    public ByteTcpClientSocket(Config config) {
        super(config);
    }

    @Override
    byte[] convert(byte[] data) {
        return data;
    }
}
