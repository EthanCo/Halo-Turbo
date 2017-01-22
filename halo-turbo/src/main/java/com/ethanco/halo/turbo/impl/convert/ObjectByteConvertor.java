package com.ethanco.halo.turbo.impl.convert;

import com.ethanco.halo.turbo.ads.IConvertor;

/**
 * Object转换为Byte[]
 *
 * @author EthanCo
 * @since 2017/1/20
 */

public class ObjectByteConvertor implements IConvertor {
    @Override
    public boolean isSentHandler(Object message) {
        return message instanceof Object;
    }

    @Override
    public boolean isReceiveHandler(Object message) {
        return message instanceof byte[] || message instanceof Byte[];
    }

    @Override
    public Object sentConvert(Object message) {
        return message.toString().getBytes();
    }

    @Override
    public Object receiveConvert(Object message) {
        if (message instanceof String) {
            ((String) message).trim();
        }
        return message;
    }
}
