package com.ethanco.halo.turbo.impl;

import com.ethanco.halo.turbo.bean.Config;

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
    private Socket sendSocket;

    public TcpServerSocket(final Config config) {
        super(config);
    }

    @Override
    public void start() {
        onStart();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                init();

                while (runningFlag) { //TODO flag
                    try {
                        sendSocket = socket.accept();
                        InputStream in = sendSocket.getInputStream();
                        readStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //TODO 重试机制
                    }
                }
            }
        });
    }

    private void onStart() {
        for (SocketListener<T> mSocketListener : mSocketListeners) {
            mSocketListener.onStart();
        }
    }

    private void init() {
        if (socket == null) {
            try {
                socket = new ServerSocket(config.port);
                runningFlag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (socket == null) {
            return;
        }

        onStop();

        try {
            runningFlag = false;
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onStop() {
        for (SocketListener<T> mSocketListener : mSocketListeners) {
            mSocketListener.onStop();
        }
    }

    @Override
    public void send(final byte[] buffer, final int offset, final int length) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                    OutputStream outputStream = sendSocket.getOutputStream();
                    if (null != outputStream) {
                        DataOutputStream out = new DataOutputStream(outputStream);
                        out.write(buffer, offset, length);
                        out.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO 重试机制
                }
            }
        });
    }
}
