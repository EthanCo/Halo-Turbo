package com.ethanco.halo.turbo;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by EthanCo on 2016/9/16.
 */
public abstract class TcpServerSocket<T> extends BaseTcpSocket<T> {
    private ServerSocket socket;
    private OutputStream outputStream;

    public TcpServerSocket(final Config config) {
        super(config);
        /*threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new ServerSocket(config.port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    /*@Override
    public void init(Config config) {
        try {
            socket = new ServerSocket(config.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void start() {
        Log.i("Z-", "start : ");
        for (SocketListener<T> mSocketListener : mSocketListeners) {
            Log.i("Z-", "start 0.6: ");
            mSocketListener.onStart();
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i("Z-", "start 1: ");
                if (socket == null) {
                    try {
                        socket = new ServerSocket(config.port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Socket sendSocket;
                while (true) { //TODO flag
                    try {
                        sendSocket = socket.accept();
                        Log.i("Z-", "start accept: ");
                        // 接收客户端信息
                        InputStream in = sendSocket.getInputStream();
                        readStream(in);
                        synchronized (this) {
                            outputStream = sendSocket.getOutputStream();
                        }
                    } catch (Exception e) {
                       /* beginReceive(MAX_BUFFER_SIZE);
                        Log.e(TAG, "run-> error: " + e.getMessage());
                        //receiveIsBegin = false;
                        e.printStackTrace();*/
                    }
                }
            }
        });
    }

    @Override
    public void stop() {
        if (socket == null) {
            return;
        }

        for (SocketListener<T> mSocketListener : mSocketListeners) {
            mSocketListener.onStart();
        }

        try {
            if (outputStream != null) {
                outputStream.close();
            }
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(byte[] buffer, int offset, int length) {
        synchronized (this) {
            if (null != outputStream) {
                try {
                    DataOutputStream out = new DataOutputStream(outputStream);
                    out.write(buffer, offset, length);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //CloseUtils.closeQuietly(out);
                }
            }
        }
    }
}
