package com.ethanco.json.convertor.convert;

import com.ethanco.halo.turbo.ads.IConvertor;
import com.google.gson.Gson;

/**
 * @author EthanCo
 * @since 2017/1/20
 */

public class ObjectJsonConvertor implements IConvertor {
    private Gson gson = new Gson();

    @Override
    public boolean isSentHandler(Object message) {
        return message instanceof Object;
    }

    @Override
    public boolean isReceiveHandler(Object message) {
        return message instanceof Object;
    }

    @Override
    public Object sentConvert(Object message) {
        return gson.toJson(message);
    }

    @Override
    public Object receiveConvert(Object message) {
        if (message instanceof byte[] || message instanceof Byte[]) {
            return new String((byte[]) message);
        }
        return message;
    }
}
