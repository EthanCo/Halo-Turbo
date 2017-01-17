package com.ethanco.halo.turbo.ads;

/**
 * TODO
 *
 * @author EthanCo
 * @since 2017/1/17
 */

public interface IHandler {
    //会话(session)创建之后，回调该方法
    void sessionCreated(ISession var1);

    //会话(session)打开之后，回调该方法
    void sessionOpened(ISession var1);

    //会话(session)关闭后，回调该方法
    void sessionClosed(ISession var1);

    //接收到消息时回调这个方法
    void messageReceived(ISession var1, Object var2);

    //发送数据是回调这个方法
    void messageSent(ISession var1, Object var2);
}
