package com.ethanco.halo.turbo.ads;

/**
 * 转换器
 *
 * @author EthanCo
 * @since 2017/1/20
 */

public interface IConvertor {
    //发送时 是否处理
    boolean isSentHandler(Object message);

    //接收时 是否处理
    boolean isReceiveHandler(Object message);

    //发送时 转换
    Object sentConvert(Object message);

    //接收时 转换
    Object receiveConvert(Object message);
}
