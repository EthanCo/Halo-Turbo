package com.ethanco.halo.turbo.impl.convert;

import com.ethanco.halo.turbo.ads.IConvertor;

/**
 * String转化为Byte[]
 *
 * @author EthanCo
 * @since 2017/1/20
 */

public class StringByteConvertor implements IConvertor {
    @Override
    public boolean isSentHandler(Object message) {
        return message instanceof String;
    }

    @Override
    public boolean isReceiveHandler(Object message) {
        return message instanceof byte[] || message instanceof Byte[];
    }

    @Override
    public Object sentConvert(Object message) {
        return ((String) message).getBytes();
    }

    @Override
    public Object receiveConvert(Object message) {
        return new String((byte[]) message).trim();
    }
}
