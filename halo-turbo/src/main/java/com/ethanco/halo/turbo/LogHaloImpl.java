package com.ethanco.halo.turbo;

import android.util.Log;

/**
 * Created by EthanCo on 2016/9/14.
 */
public class LogHaloImpl implements IHalo {
    private static final String TAG = "Z-LogPivotImpl";

    @Override
    public void start() {
        Log.i(TAG, "start...");
    }

    @Override
    public void reStart() {
        Log.i(TAG, "reStart");
    }

    @Override
    public void stop() {
        Log.i(TAG, "stop... ");
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        Log.i(TAG, "send : " + HexUtil.bytesToHexString(buffer) + " offset:" + offset + " length:" + length);
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
