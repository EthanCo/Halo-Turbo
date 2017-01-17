package com.ethanco.halo.turbo.ads;

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public interface IHandler {
    //会话(session)创建之后，回调该方法
    void sessionCreated(ISession session);

    //会话(session)打开之后，回调该方法
    void sessionOpened(ISession session);

    //会话(session)关闭后，回调该方法
    void sessionClosed(ISession session);

    //接收到消息时回调这个方法
    void messageReceived(ISession session, Object message);

    //发送数据是回调这个方法
    void messageSent(ISession session, Object message);
}
