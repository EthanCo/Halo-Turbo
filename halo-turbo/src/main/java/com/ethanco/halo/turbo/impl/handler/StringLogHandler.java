package com.ethanco.halo.turbo.impl.handler;

import com.ethanco.halo.turbo.ads.BaseLogHandler;
import com.ethanco.halo.turbo.ads.ISession;

/**
 * 默认日志处理
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public class StringLogHandler extends BaseLogHandler {

    public StringLogHandler() {
    }

    public StringLogHandler(String tag) {
        super(tag);
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

    @Override
    protected String convertToString(Object message) {
        if (message == null) {
            return "message is null";
        }

        String receive;
        if (message instanceof byte[]) {
            receive = new String((byte[]) message);
        } else if (message instanceof String) {
            receive = (String) message;
        } else {
            receive = message.toString();
        }
        return receive.trim();
    }
}
