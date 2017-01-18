package com.ethanco.halo.turbo.impl;

import android.util.Log;

import com.ethanco.halo.turbo.Halo;
import com.ethanco.halo.turbo.ads.AbstractLog;
import com.ethanco.halo.turbo.ads.ISession;
import com.ethanco.halo.turbo.utils.HexUtil;

/**
 * 默认日志处理
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public class LogHandler extends AbstractLog {
    private String tag = Halo.HALO;

    public LogHandler() {
    }

    public LogHandler(String tag) {
        this.tag = tag;
    }

    @Override
    public void sessionCreated(ISession session) {
        printLog("sessionCreated");
    }

    @Override
    public void sessionOpened(ISession session) {
        printLog("sessionOpened");
    }

    @Override
    public void sessionClosed(ISession session) {
        printLog("sessionClosed");
    }

    @Override
    public void messageReceived(ISession session, Object message) {
        String receive = convertToString(message);
        printLog("messageReceived:" + receive);
    }

    @Override
    public void messageSent(ISession session, Object message) {
        String sendData = convertToString(message);
        printLog("messageSent:" + sendData);
    }

    private String convertToString(Object message) {
        if (message == null) {
            return "message is null";
        }

        String receive;
        if (message instanceof byte[]) {
            receive = HexUtil.bytesToHexString((byte[]) message);
        } else if (message instanceof String) {
            receive = String.valueOf(message);
        } else {
            receive = message.toString();
        }
        return receive;
    }

    private void printLog(String sessionCreated) {
        Log.i(tag, sessionCreated);
    }

    @Override
    public void onStarting() {
        printLog(getPrefix() + " onStarting");
    }

    @Override
    public void onStartSuccess() {
        printLog(getPrefix() + " onStartSuccess");
    }

    @Override
    public void onStartFailed(Exception e) {
        printLog(getPrefix() + " onStartFailed:" + e.getMessage());
    }

    @Override
    public void onStopping() {
        printLog(getPrefix() + " onStopping");
    }

    @Override
    public void onStopped() {
        printLog(getPrefix() + " onStopped");
    }

    @Override
    public void onReceiveException(Exception e) {
        printLog(getPrefix() + " onReceiveException:" + e.getMessage());
    }
}
