package com.ethanco.halo.turbo.impl.convert;

import com.ethanco.halo.turbo.ads.IConvertor;

import java.util.List;

/**
 * IConvertor 管理类
 *
 * @author EthanCo
 * @since 2017/1/20
 */
public class ConvertManager {
    List<IConvertor> convertChain;

    public ConvertManager(List<IConvertor> convertChain) {
        this.convertChain = convertChain;
    }

    public void add(IConvertor convertor) {
        if (convertChain == null) {
            return;
        }
        if (!convertChain.contains(convertor)) {
            convertChain.add(convertor);
        }
    }

    public void remove(IConvertor convertor) {
        if (convertChain == null) {
            return;
        }

        convertChain.remove(convertor);
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
