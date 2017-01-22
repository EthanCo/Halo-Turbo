package com.ethanco.halo.turbo.impl.convert;

import com.ethanco.halo.turbo.ads.IConvertor;

import java.util.List;

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/20
 */
public class ConvertManager {
    List<IConvertor> convertChain;

    public ConvertManager(List<IConvertor> convertChain) {
        this.convertChain = convertChain;
    }

    public Object convert(Object message) {
        if (convertChain == null) {
            return message;
        }

        for (IConvertor convertor : convertChain) {
            if (convertor.isSentHandler(message)) {
                return convertor.sentConvert(message);
            }
        }

        return message;
    }

    public Object receive(Object message) {
        if (convertChain == null) {
            return message;
        }

        for (IConvertor convertor : convertChain) {
            return convertor.receiveConvert(message);
        }

        return message;
    }
}
