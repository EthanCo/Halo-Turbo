package com.ethanco.halo.turbo;

import static com.ethanco.halo.turbo.utils.Util.println;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class LogHaloImpl implements IHalo {
    private static final String TAG = "Z-LogPivotImpl";

    @Override
    public void start() {
        println(TAG, "start...");
    }

    @Override
    public void reStart() {
        println(TAG, "reStart");
    }

    @Override
    public void stop() {
        println(TAG, "stop... ");
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        println(TAG, "send : " + HexUtil.bytesToHexString(buffer) + " offset:" + offset + " length:" + length);
    }

    @Override
    public void send(byte[] buffer) {
        send(buffer, 0, buffer.length);
    }

    @Override
    public void send(String str) {
        send(str.getBytes());
    }
}
