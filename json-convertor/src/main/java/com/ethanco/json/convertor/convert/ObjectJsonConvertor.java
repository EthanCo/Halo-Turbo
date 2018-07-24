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
        if (message instanceof String || message instanceof Byte[] || message instanceof byte[]) {
            return message;
        }

        String json = gson.toJson(message);
        //Log.i("Z-Test", "sentConvert:" + json);
        return json;
    }

    @Override
    public Object receiveConvert(Object message) {
        return message;
        /*if (message instanceof byte[] || message instanceof Byte[]) {
            return message;//new String((byte[]) message).trim();
        } else if (message instanceof String) {
            return message;//((String) (message)).trim();
        }
        return message;*/
    }
}
