package com.ethanco.json.convertor.convert;

/**
 * 如果是Object对象，则先转换为json字符串后，再转换为byte[]
 *
 * @author EthanCo
 * @since 2017/1/20
 */

public class ObjectJsonByteConvertor extends ObjectJsonConvertor {
    @Override
    public Object sentConvert(Object message) {
        Object result = super.sentConvert(message);
        if (result instanceof String) {
            return ((String) result).getBytes();
        }
        return result;
    }
}
