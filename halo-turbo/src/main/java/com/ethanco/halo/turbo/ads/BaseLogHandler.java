package com.ethanco.halo.turbo.ads;

import android.util.Log;

import com.ethanco.halo.turbo.utils.Util;

/**
 * 默认日志处理
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public abstract class BaseLogHandler extends AbstractLog {
    protected String tag = Util.HALO;

    public BaseLogHandler() {
    }

    public BaseLogHandler(String tag) {
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

    protected abstract String convertToString(Object message);

    protected void printLog(String sessionCreated) {
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
    public void onReConnecting() {
        printLog(getPrefix() + " onReConnecting");
    }

    @Override
    public void onReConnected() {
        printLog(getPrefix() + " onReConnected");
    }

    @Override
    public void onReceiveException(Exception e) {
        printLog(getPrefix() + " onReceiveException:" + e.getMessage());
    }

    public void onKeepAliveTimeOut() {
        printLog(getPrefix() + ">>> keepAlive TimeOut...");
    }
}
