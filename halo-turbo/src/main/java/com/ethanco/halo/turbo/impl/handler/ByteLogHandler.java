package com.ethanco.halo.turbo.impl.handler;

import com.ethanco.halo.turbo.ads.BaseLogHandler;
import com.ethanco.halo.turbo.utils.HexUtil;

/**
 * 默认日志处理
 *
 * @author EthanCo
 * @since 2017/1/18
 */

public class ByteLogHandler extends BaseLogHandler {

    public ByteLogHandler() {
    }

    public ByteLogHandler(String tag) {
        super(tag);
    }

    @Override
    protected String convertToString(Object message) {
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
        return receive.trim();
    }
}
