package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.ads.absSocket;
import com.ethanco.halo.turbo.bean.Config;
import com.ethanco.halo.turbo.utils.HexUtil;

import static com.ethanco.halo.turbo.utils.Util.println;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class LogHaloImpl extends absSocket {
    private static final String TAG = "Z-LogPivotImpl";

    public LogHaloImpl(Config config) {
        super(config);
    }

    @Override
    public void start() {
        println(TAG, "start...");
    }

    @Override
    public void stop() {
        println(TAG, "stop... ");
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        println(TAG, "send : " + HexUtil.bytesToHexString(buffer) + " offset:" + offset + " length:" + length);
    }

    /*@Override
    public void init(Config config) {

    }*/
}
