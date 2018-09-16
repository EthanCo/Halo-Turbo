package com.ethanco.halo.turbo.impl.socket;

import java.net.InetAddress;

/**
 * Created by hult on 2016/5/4.
 * Socket接口
 */
public interface IHopeSocket {

    /**
     * 绑定本地地址端口
     *
     * @param port 端口
     */
    void bind(int port);

    /**
     * 绑定本地地址端口
     *
     * @param address 地址
     * @param port    端口
     */
    void bind(InetAddress address, int port);

    /**
     * 绑定本地地址端口
     *
     * @param address 地址
     * @param port    端口
     */
    void bind(String address, int port);

    /**
     * 连接
     *
     * @param address 地址
     * @param port    端口
     */
    void connect(InetAddress address, int port);

    /**
     * 连接
     *
     * @param address 地址
     * @param port    端口
     */
    void connect(String address, int port);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * receive data
     *
     * @param maxBufferSize 最大接收缓冲区大小
     * @return begin receive success ?
     */
    Boolean beginReceive(int maxBufferSize);

    /**
     * end receive
     */
    void endReceive();

    /**
     * send data
     *
     * @param buffer raw byte buffer
     * @param offset buffer offset
     * @param length send length
     */
    void send(byte[] buffer, int offset, int length);

    /**
     * send data
     *
     * @param buffer
     * @param offset
     * @param length
     * @param IP
     * @param port
     */
    void send(byte[] buffer, int offset, int length, String IP, int port);

    /**
     * 设置广播地址
     *
     * @param address IP地址
     * @param port    端口
     */
    void setBroadCastAddress(String address, int port);

    /**
     * UDP广播
     *
     * @param buffer raw byte buffer
     * @param offset buffer offset
     * @param length send length
     */
    void broadCast(byte[] buffer, int offset, int length);

    /**
     * 获取本地IP
     *
     * @return ip
     */
    byte[] getLocalIp();

    void setOnSocketErrorListener(OnSocketErrorListener errorListener);

    interface OnSocketErrorListener {
        void onSocketError(Exception e);
    }
}