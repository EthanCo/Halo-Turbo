package com.ethanco.halo.turbo.impl.socket;

import android.os.SystemClock;
import android.util.Log;

import com.ethanco.halo.turbo.utils.HexUtil;
import com.ethanco.halo.turbo.utils.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

/**
 * Created by hult on 2016/5/5.
 * UDP
 */
class UdpSocket implements IHopeSocket {

    public static final String TAG = "Z-UdpSocket";
    protected DatagramSocket socket = null;
    protected DatagramPacket sendPacket = null;
    protected DatagramPacket broadcastPacket = null;
    protected DatagramPacket receivePacket = null;
    //private final int TIMEOUT = 3000;
    protected Boolean receiveIsBegin = false;

    //用作记录
    protected String connectIp = "";
    protected int connectPort = -1;
    protected int bindPort = -1;
    private ExecutorService threadPool;

    public UdpSocket(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    private void bind(InetSocketAddress sa) {
        try {
            if (socket != null && socket.getLocalPort() != sa.getPort()) {
                socket.disconnect();
                socket.close();
                socket = new DatagramSocket(sa);
            }
            if (socket == null) {
                socket = new DatagramSocket(sa);
            }

            //socket.setSoTimeout(TIMEOUT); // 设置阻塞时间

        } catch (SocketException e) {
            Log.e("HOPE_TAG", e.getMessage());
        }
    }

    @Override
    public void bind(int port) {
        Log.i(TAG, "bind:" + port);
        bind(port, true);
    }

    /**
     * @param port
     * @param errorRecursion 出现错误是否重新调用
     */
    private void bind(int port, boolean errorRecursion) {
        try {
            if (socket != null && socket.getLocalPort() != port) {
                socket.disconnect();
                socket.close();
                socket = new DatagramSocket(port);
            }
            if (socket == null)
                socket = new DatagramSocket(port);

            bindPort = port;
            //socket.setSoTimeout(TIMEOUT); // 设置阻塞时间

        } catch (SocketException e) {
            Log.e("Z-1-HOPE_TAG", e.getMessage());
            if (errorRecursion) {
                if ("bind failed: EADDRINUSE (Address already in use)".equals(e.getMessage())) {
                    SystemClock.sleep(3000);
                    bind(port, false);
                }
            }
        }
        if (socket == null) {
            Log.e("Z-1-HOPE_TAG", "socket==null" + (socket == null));
        }
    }

    @Override
    public void bind(InetAddress address, int port) {
        InetSocketAddress sa = new InetSocketAddress(address, port);
        bind(sa);
    }

    @Override
    public void bind(String address, int port) {
        InetSocketAddress sa = new InetSocketAddress(address, port);
        bind(sa);
    }

    private void connect(InetSocketAddress sa) {
        try {
            sendPacket = new DatagramPacket(new byte[0], 0, sa);
            if (sendPacket.getAddress() != null) {
                connectIp = sendPacket.getAddress().getHostAddress();
                connectPort = sendPacket.getPort();
            }
        } catch (SocketException e) {
            Log.e("HOPE_TAG", e.getMessage());
        }
    }

    @Override
    public void connect(InetAddress address, int port) {
        InetSocketAddress sa = new InetSocketAddress(address, port);
        connect(sa);
    }

    @Override
    public void connect(String address, int port) {
        Log.i(TAG, "connect:" + address + " port:" + port);
        InetSocketAddress sa = new InetSocketAddress(address, port);
        connect(sa);
    }

    @Override
    public void disconnect() {
        if (socket != null) {
            receiveIsBegin = false;
            socket.close();
            socket.disconnect();
        }
    }

    @Override
    public Boolean beginReceive(final int maxBufferSize) throws NullPointerException {
        Log.i(TAG, "beginReceive");
        preBeginReceive();
        if (!receiveIsBegin) {
            if (socket == null) {
                SystemClock.sleep(300);
                if (socket == null) {
                    Log.e(TAG, "未绑定本地端口");
                    return false;
                }
            }
            receiveIsBegin = true;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] buffer = new byte[maxBufferSize];
                        receivePacket = new DatagramPacket(buffer, buffer.length);
                        Log.i(TAG, "beginReceive : " + receiveIsBegin);
                        while (receiveIsBegin) {
                            receivePacket.setLength(buffer.length);
                            Log.i(TAG, "beginReceive 1: ");
                            socket.receive(receivePacket);
                            Log.i(TAG, "beginReceive 2: ");
                            byte[] data = Arrays.copyOf(buffer, receivePacket.getLength());
                            Log.i(TAG, "beginReceive bindPort:" + bindPort + " data: " + new String(data));
                            Log.i(TAG, "beginReceive byte: " + HexUtil.bytesToHexString(data));
                            onReceive(data, receivePacket.getPort());
                        }
                    } catch (IOException e) {
                        receiveIsBegin = false;
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }
        return false;
    }

    //准备开始接收
    protected void preBeginReceive() {

    }

    //当收到数据
    protected void onReceive(byte[] data, int port) {

    }


    @Override
    public void endReceive() {
        receiveIsBegin = false;
    }

    @Override
    public void send(final byte[] buffer, final int offset, final int length) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPacket.setData(buffer, offset, length);
                    socket.send(sendPacket);
                } catch (IOException e) {
                    Log.e("Z-HOPE_TAG", e.getMessage());
                }
            }
        });
    }

    @Override
    public void send(byte[] buffer, int offset, int length, String IP, int port) {
        throw new IllegalStateException("暂不支持");
    }

    @Override
    public void setBroadCastAddress(String address, int port) {
        try {
            SocketAddress sa = new InetSocketAddress(address, port);
            broadcastPacket = new DatagramPacket(new byte[0], 0, sa);
        } catch (SocketException e) {
            Log.e("HOPE_TAG", e.getMessage());
        }
    }

    @Override
    public void broadCast(final byte[] buffer, final int offset, final int length) {

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    broadcastPacket.setData(buffer, offset, length);
                    socket.send(broadcastPacket);
                } catch (IOException e) {
                    Log.e("HOPE_TAG", e.getMessage());
                }
            }
        });
    }

    @Override
    public byte[] getLocalIp() {
        return Util.getLocalIP();
    }

    @Override
    public void setOnSocketErrorListener(OnSocketErrorListener errorListener) {
        throw new IllegalStateException("not support setOnErrorListener");
    }
}
